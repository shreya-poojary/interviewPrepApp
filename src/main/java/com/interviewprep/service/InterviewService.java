package com.interviewprep.service;

import com.interviewprep.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main service for interview management and AI interactions
 */
@Slf4j
public class InterviewService {
    private final AIServiceManager aiServiceManager;
    private final JavaTTSService ttsService;
    private final StorageService storageService;
    
    private static final Set<String> FILLER_WORDS = Set.of(
        "um", "uh", "like", "you know", "actually", "basically",
        "literally", "so", "well", "right", "okay", "hmm", "kind of"
    );
    
    public InterviewService(AIServiceManager aiServiceManager, JavaTTSService ttsService,
                          StorageService storageService) {
        this.aiServiceManager = aiServiceManager;
        this.ttsService = ttsService;
        this.storageService = storageService;
    }
    
    /**
     * Analyze resume against job description
     */
    public ResumeAnalysis analyzeResume(Resume resume, JobDescription jobDescription) throws IOException {
        String prompt = buildResumeAnalysisPrompt(resume, jobDescription);
        String response = aiServiceManager.generate(prompt);
        return parseResumeAnalysis(response);
    }
    
    /**
     * Generate interview questions
     */
    public List<InterviewQuestion> generateQuestions(Resume resume, JobDescription jobDescription,
                                                     InterviewMode mode, int count) throws IOException {
        String prompt = buildQuestionGenerationPrompt(resume, jobDescription, mode, count);
        String response = aiServiceManager.generate(prompt);
        return parseQuestions(response, mode);
    }
    
    /**
     * Evaluate a user's answer to a question
     */
    public QuestionAnalysis evaluateAnswer(InterviewQuestion question, String answer) throws IOException {
        QuestionAnalysis analysis = new QuestionAnalysis(question, answer);
        
        // Basic metrics
        String[] words = answer.trim().split("\\s+");
        analysis.setWordCount(words.length);
        analysis.setFillerWords(countFillerWords(answer));
        
        // Get AI feedback
        String prompt = buildAnswerEvaluationPrompt(question, answer);
        String feedback = aiServiceManager.generate(prompt);
        
        analysis.setFeedback(feedback);
        analysis.setScore(extractScoreFromFeedback(feedback));
        
        return analysis;
    }
    
    private String buildResumeAnalysisPrompt(Resume resume, JobDescription jobDescription) {
        return String.format("""
            You are an expert career coach and recruiter. Analyze this resume against the job description.
            
            JOB DESCRIPTION:
            %s
            
            RESUME:
            %s
            
            Provide a comprehensive analysis in this format:
            
            MATCH_SCORE: [0-100]
            
            OVERALL_FEEDBACK:
            [Detailed assessment in 2-3 sentences]
            
            STRENGTHS:
            - [Strength 1]
            - [Strength 2]
            - [Strength 3]
            
            WEAKNESSES:
            - [Weakness 1]
            - [Weakness 2]
            - [Weakness 3]
            
            SUGGESTIONS:
            - [Suggestion 1]
            - [Suggestion 2]
            - [Suggestion 3]
            
            MATCHING_SKILLS:
            - [Skill 1]
            - [Skill 2]
            
            MISSING_SKILLS:
            - [Skill 1]
            - [Skill 2]
            """,
            jobDescription.getContent(),
            resume.getContent()
        );
    }
    
    private String buildQuestionGenerationPrompt(Resume resume, JobDescription jobDescription,
                                                 InterviewMode mode, int count) {
        return String.format("""
            You are an experienced interviewer. Generate %d interview questions for %s.
            
            JOB DESCRIPTION:
            %s
            
            CANDIDATE'S RESUME:
            %s
            
            Guidelines:
            - Mix of technical, behavioral, and situational questions
            - Questions should be specific to the candidate's background and job requirements
            - Difficulty level: %s
            - Format: Number each question (1., 2., 3., etc.)
            - Make questions realistic and relevant
            
            Generate the questions now:
            """,
            count,
            mode.getDisplayName(),
            jobDescription.getContent(),
            resume.getContent(),
            mode.getDifficulty()
        );
    }
    
    private String buildAnswerEvaluationPrompt(InterviewQuestion question, String answer) {
        return String.format("""
            Evaluate this interview answer on a scale of 0-10:
            
            Question (%s): %s
            
            Candidate's Answer: %s
            
            Provide evaluation in this format:
            
            SCORE: [0-10]
            
            FEEDBACK:
            What was good:
            - [Point 1]
            - [Point 2]
            
            What could be improved:
            - [Point 1]
            - [Point 2]
            
            Specific suggestions:
            - [Suggestion 1]
            - [Suggestion 2]
            """,
            question.getCategory(),
            question.getQuestion(),
            answer
        );
    }
    
    private ResumeAnalysis parseResumeAnalysis(String aiResponse) {
        ResumeAnalysis analysis = new ResumeAnalysis();
        
        try {
            analysis.setMatchScore(extractIntValue(aiResponse, "MATCH_SCORE:", 75));
            analysis.setOverallFeedback(extractSection(aiResponse, "OVERALL_FEEDBACK:"));
            analysis.setStrengths(extractList(aiResponse, "STRENGTHS:"));
            analysis.setWeaknesses(extractList(aiResponse, "WEAKNESSES:"));
            analysis.setSuggestions(extractList(aiResponse, "SUGGESTIONS:"));
            analysis.setMatchingSkills(extractList(aiResponse, "MATCHING_SKILLS:"));
            analysis.setMissingSkills(extractList(aiResponse, "MISSING_SKILLS:"));
        } catch (Exception e) {
            log.warn("Error parsing resume analysis, using fallback", e);
            analysis.setOverallFeedback(aiResponse);
            analysis.setMatchScore(75);
        }
        
        return analysis;
    }
    
    private List<InterviewQuestion> parseQuestions(String aiResponse, InterviewMode mode) {
        List<InterviewQuestion> questions = new ArrayList<>();
        String[] lines = aiResponse.split("\\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.matches("^[0-9]+[.)].*")) {
                String questionText = line.replaceFirst("^[0-9]+[.)]\\s*", "");
                
                InterviewQuestion question = new InterviewQuestion();
                question.setQuestion(questionText);
                question.setDifficulty(mode.getDifficulty());
                question.setTimeLimit(mode.getSecondsPerQuestion());
                
                // Categorize question
                String lower = questionText.toLowerCase();
                if (lower.contains("algorithm") || lower.contains("code") || lower.contains("implement")) {
                    question.setCategory("Technical");
                } else if (lower.contains("tell me about") || lower.contains("describe a time")) {
                    question.setCategory("Behavioral");
                } else {
                    question.setCategory("General");
                }
                
                questions.add(question);
            }
        }
        
        return questions;
    }
    
    private int countFillerWords(String text) {
        int count = 0;
        String lowerText = text.toLowerCase();
        
        for (String filler : FILLER_WORDS) {
            Pattern pattern = Pattern.compile("\\b" + filler + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(lowerText);
            while (matcher.find()) {
                count++;
            }
        }
        
        return count;
    }
    
    private double extractScoreFromFeedback(String feedback) {
        Pattern pattern = Pattern.compile("SCORE:\\s*([0-9.]+)");
        Matcher matcher = pattern.matcher(feedback);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return 7.0;
    }
    
    private int extractIntValue(String text, String label, int defaultValue) {
        Pattern pattern = Pattern.compile(label + "\\s*([0-9]+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return defaultValue;
    }
    
    private String extractSection(String text, String label) {
        Pattern pattern = Pattern.compile(label + "\\s*([^\\n]+(?:\\n(?![A-Z_]+:)[^\\n]+)*)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }
    
    private List<String> extractList(String text, String label) {
        List<String> items = new ArrayList<>();
        Pattern sectionPattern = Pattern.compile(label + "\\s*([\\s\\S]*?)(?=\\n[A-Z_]+:|$)");
        Matcher sectionMatcher = sectionPattern.matcher(text);
        
        if (sectionMatcher.find()) {
            String section = sectionMatcher.group(1);
            Pattern itemPattern = Pattern.compile("-\\s*(.+)");
            Matcher itemMatcher = itemPattern.matcher(section);
            
            while (itemMatcher.find()) {
                items.add(itemMatcher.group(1).trim());
            }
        }
        
        return items;
    }
}

