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
        return String.format(
            "You are an expert career coach and recruiter. Analyze this resume against the job description.\n\n" +
            "JOB DESCRIPTION:\n" +
            "%s\n\n" +
            "RESUME:\n" +
            "%s\n\n" +
            "Provide a comprehensive analysis in this format:\n\n" +
            "MATCH_SCORE: [0-100]\n\n" +
            "OVERALL_FEEDBACK:\n" +
            "[Detailed assessment in 2-3 sentences]\n\n" +
            "STRENGTHS:\n" +
            "- [Strength 1]\n" +
            "- [Strength 2]\n" +
            "- [Strength 3]\n\n" +
            "WEAKNESSES:\n" +
            "- [Weakness 1]\n" +
            "- [Weakness 2]\n" +
            "- [Weakness 3]\n\n" +
            "SUGGESTIONS:\n" +
            "- [Suggestion 1]\n" +
            "- [Suggestion 2]\n" +
            "- [Suggestion 3]\n\n" +
            "MATCHING_SKILLS:\n" +
            "- [Skill 1]\n" +
            "- [Skill 2]\n\n" +
            "MISSING_SKILLS:\n" +
            "- [Skill 1]\n" +
            "- [Skill 2]",
            jobDescription.getContent(),
            resume.getContent()
        );
    }
    
    private String buildQuestionGenerationPrompt(Resume resume, JobDescription jobDescription,
                                                 InterviewMode mode, int count) {
        return String.format(
            "You are an experienced interviewer. Generate %d interview questions for %s.\n\n" +
            "JOB DESCRIPTION:\n" +
            "%s\n\n" +
            "CANDIDATE'S RESUME:\n" +
            "%s\n\n" +
            "Guidelines:\n" +
            "- Mix of technical, behavioral, and situational questions\n" +
            "- Questions should be specific to the candidate's background and job requirements\n" +
            "- Difficulty level: %s\n" +
            "- Format: Number each question (1., 2., 3., etc.)\n" +
            "- Make questions realistic and relevant\n\n" +
            "Generate the questions now:",
            count,
            mode.getDisplayName(),
            jobDescription.getContent(),
            resume.getContent(),
            mode.getDifficulty()
        );
    }
    
    private String buildAnswerEvaluationPrompt(InterviewQuestion question, String answer) {
        return String.format(
            "Evaluate this interview answer on a scale of 0-10:\n\n" +
            "Question (%s): %s\n\n" +
            "Candidate's Answer: %s\n\n" +
            "Provide evaluation in this format:\n\n" +
            "SCORE: [0-10]\n\n" +
            "FEEDBACK:\n" +
            "What was good:\n" +
            "- [Point 1]\n" +
            "- [Point 2]\n\n" +
            "What could be improved:\n" +
            "- [Point 1]\n" +
            "- [Point 2]\n\n" +
            "Specific suggestions:\n" +
            "- [Suggestion 1]\n" +
            "- [Suggestion 2]",
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
    
    /**
     * Generate comprehensive analytics for an interview session
     */
    public InterviewAnalytics generateAnalytics(InterviewSession session) throws IOException {
        log.info("Generating analytics for session: {}", session.getSessionId());
        
        // Build comprehensive analytics prompt
        String prompt = buildAnalyticsPrompt(session);
        String response = aiServiceManager.generate(prompt);
        
        // Parse the AI response into analytics
        InterviewAnalytics analytics = parseAnalytics(response, session);
        
        log.info("Analytics generated successfully for session: {}", session.getSessionId());
        return analytics;
    }
    
    private String buildAnalyticsPrompt(InterviewSession session) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert interview coach. Analyze this interview session and provide comprehensive feedback.\n\n");
        
        prompt.append("INTERVIEW SESSION DETAILS:\n");
        prompt.append("Mode: ").append(session.getMode().getDisplayName()).append("\n");
        prompt.append("Duration: ").append(session.getTotalDurationSeconds() / 60).append(" minutes\n");
        prompt.append("Questions: ").append(session.getQuestions().size()).append("\n");
        prompt.append("Answered: ").append(session.getUserAnswers().size()).append("\n\n");
        
        prompt.append("QUESTIONS AND ANSWERS:\n");
        for (int i = 0; i < session.getQuestions().size(); i++) {
            InterviewQuestion question = session.getQuestions().get(i);
            prompt.append("Q").append(i + 1).append(" (").append(question.getCategory()).append("): ");
            prompt.append(question.getQuestion()).append("\n");
            
            if (i < session.getUserAnswers().size()) {
                String answer = session.getUserAnswers().get(i);
                prompt.append("Answer: ").append(answer).append("\n");
                prompt.append("Duration: ").append(session.getAnswerDurations().get(i)).append(" seconds\n");
            } else {
                prompt.append("Answer: [Not answered]\n");
            }
            prompt.append("\n");
        }
        
        prompt.append(
            "Provide comprehensive analytics in this exact format:\n\n" +
            "OVERALL_SCORE: [0-10]\n\n" +
            "TECHNICAL_SCORE: [0-10]\n" +
            "BEHAVIORAL_SCORE: [0-10]\n" +
            "COMMUNICATION_SCORE: [0-10]\n" +
            "CONFIDENCE_SCORE: [0-10]\n\n" +
            "PERFORMANCE_LEVEL: [Excellent/Good/Needs Improvement]\n\n" +
            "STRENGTHS:\n" +
            "- [Strength 1]\n" +
            "- [Strength 2]\n" +
            "- [Strength 3]\n\n" +
            "WEAKNESSES:\n" +
            "- [Weakness 1]\n" +
            "- [Weakness 2]\n" +
            "- [Weakness 3]\n\n" +
            "DETAILED_FEEDBACK:\n" +
            "[Comprehensive analysis of the interview performance]\n\n" +
            "IMPROVEMENT_SUGGESTIONS:\n" +
            "- [Suggestion 1]\n" +
            "- [Suggestion 2]\n" +
            "- [Suggestion 3]");
        
        return prompt.toString();
    }
    
    private InterviewAnalytics parseAnalytics(String aiResponse, InterviewSession session) {
        InterviewAnalytics analytics = new InterviewAnalytics();
        analytics.setSessionId(session.getSessionId());
        analytics.setGeneratedAt(java.time.LocalDateTime.now());
        
        try {
            // Extract scores
            analytics.setOverallScore(extractDoubleValue(aiResponse, "OVERALL_SCORE:", 7.0));
            analytics.setTechnicalScore(extractDoubleValue(aiResponse, "TECHNICAL_SCORE:", 7.0));
            analytics.setBehavioralScore(extractDoubleValue(aiResponse, "BEHAVIORAL_SCORE:", 7.0));
            analytics.setCommunicationScore(extractDoubleValue(aiResponse, "COMMUNICATION_SCORE:", 7.0));
            analytics.setConfidenceScore(extractDoubleValue(aiResponse, "CONFIDENCE_SCORE:", 7.0));
            
            // Extract performance level
            String performanceLevel = extractSection(aiResponse, "PERFORMANCE_LEVEL:");
            analytics.setPerformanceLevel(performanceLevel.isEmpty() ? "Good" : performanceLevel);
            
            // Extract lists
            analytics.setStrengths(extractList(aiResponse, "STRENGTHS:"));
            analytics.setWeaknesses(extractList(aiResponse, "WEAKNESSES:"));
            
            // Extract detailed feedback
            String detailedFeedback = extractSection(aiResponse, "DETAILED_FEEDBACK:");
            analytics.setDetailedFeedback(detailedFeedback.isEmpty() ? aiResponse : detailedFeedback);
            
            // Extract improvement suggestions
            List<String> suggestions = extractList(aiResponse, "IMPROVEMENT_SUGGESTIONS:");
            analytics.setImprovementSuggestions(suggestions);
            
        } catch (Exception e) {
            log.warn("Error parsing analytics, using fallback", e);
            analytics.setOverallScore(7.0);
            analytics.setTechnicalScore(7.0);
            analytics.setBehavioralScore(7.0);
            analytics.setCommunicationScore(7.0);
            analytics.setConfidenceScore(7.0);
            analytics.setPerformanceLevel("Good");
            analytics.setDetailedFeedback(aiResponse);
            analytics.setStrengths(List.of("Good effort", "Completed interview"));
            analytics.setWeaknesses(List.of("Could improve communication", "Practice more"));
        }
        
        return analytics;
    }
    
    private double extractDoubleValue(String text, String label, double defaultValue) {
        Pattern pattern = Pattern.compile(label + "\\s*([0-9.]+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                log.warn("Error parsing double value for {}: {}", label, matcher.group(1));
            }
        }
        return defaultValue;
    }
}

