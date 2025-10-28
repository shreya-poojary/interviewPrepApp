package com.interviewprep.util;

import com.interviewprep.model.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Utility class for creating test objects and common test operations
 */
public class TestUtils {

    /**
     * Create a test InterviewSession with default values
     */
    public static InterviewSession createTestSession() {
        return createTestSession(InterviewMode.TECHNICAL);
    }

    /**
     * Create a test InterviewSession with specified mode
     */
    public static InterviewSession createTestSession(InterviewMode mode) {
        InterviewSession session = new InterviewSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setMode(mode);
        session.setStartTime(LocalDateTime.now());
        session.setEndTime(LocalDateTime.now().plusMinutes(30));
        
        // Add test questions
        session.getQuestions().add(createTestQuestion("What is your Java experience?", "Technical"));
        session.getQuestions().add(createTestQuestion("Describe a challenging project", "Behavioral"));
        session.getQuestions().add(createTestQuestion("How do you handle team conflicts?", "Leadership"));
        
        // Add test answers
        session.getUserAnswers().add("I have 5 years of Java experience");
        session.getUserAnswers().add("I worked on a complex microservices project");
        session.getUserAnswers().add("I facilitate open communication and find common ground");
        
        // Add test durations
        session.getAnswerDurations().add(120);
        session.getAnswerDurations().add(180);
        session.getAnswerDurations().add(150);
        
        return session;
    }

    /**
     * Create a test InterviewQuestion
     */
    public static InterviewQuestion createTestQuestion(String question, String category) {
        InterviewQuestion q = new InterviewQuestion();
        q.setQuestion(question);
        q.setCategory(category);
        return q;
    }

    /**
     * Create a test InterviewAnalytics
     */
    public static InterviewAnalytics createTestAnalytics() {
        return createTestAnalytics(UUID.randomUUID().toString());
    }

    /**
     * Create a test InterviewAnalytics with specified session ID
     */
    public static InterviewAnalytics createTestAnalytics(String sessionId) {
        InterviewAnalytics analytics = new InterviewAnalytics();
        analytics.setSessionId(sessionId);
        analytics.setOverallScore(8.5);
        analytics.setTechnicalScore(9.0);
        analytics.setBehavioralScore(8.0);
        analytics.setCommunicationScore(8.5);
        analytics.setConfidenceScore(8.0);
        analytics.setPerformanceLevel("Good");
        analytics.setDetailedFeedback("Good overall performance with strong technical skills");
        analytics.setStrengths(List.of(
            "Strong technical knowledge",
            "Good communication skills",
            "Problem-solving ability"
        ));
        analytics.setWeaknesses(List.of(
            "Could improve time management",
            "Needs more leadership examples"
        ));
        analytics.setImprovementSuggestions(List.of(
            "Practice more coding problems",
            "Prepare specific leadership examples",
            "Work on time management techniques"
        ));
        analytics.setGeneratedAt(LocalDateTime.now());
        
        return analytics;
    }

    /**
     * Create a test ResumeAnalysis
     */
    public static ResumeAnalysis createTestResumeAnalysis() {
        return createTestResumeAnalysis("test-user");
    }

    /**
     * Create a test ResumeAnalysis with specified user ID
     */
    public static ResumeAnalysis createTestResumeAnalysis(String userId) {
        ResumeAnalysis analysis = new ResumeAnalysis();
        analysis.setUserId(userId);
        analysis.setMatchScore(85);
        analysis.setOverallFeedback("Strong candidate with relevant experience");
        analysis.setStrengths(List.of(
            "Strong technical skills",
            "Leadership experience",
            "Project management abilities"
        ));
        analysis.setWeaknesses(List.of(
            "Limited industry experience",
            "Could improve specific skills"
        ));
        analysis.setSuggestions(List.of(
            "Gain more hands-on experience",
            "Take relevant courses",
            "Build a portfolio"
        ));
        analysis.setAnalyzedAt(LocalDateTime.now());
        
        return analysis;
    }

    /**
     * Create a test JobDescription
     */
    public static JobDescription createTestJobDescription() {
        return createTestJobDescription("test-user");
    }

    /**
     * Create a test JobDescription with specified user ID
     */
    public static JobDescription createTestJobDescription(String userId) {
        JobDescription jobDesc = new JobDescription();
        jobDesc.setUserId(userId);
        jobDesc.setTitle("Senior Software Engineer");
        jobDesc.setCompany("Tech Corp");
        jobDesc.setDescription("Looking for a senior software engineer with Java experience");
        jobDesc.setRequirements("5+ years Java, Spring Boot, AWS, Microservices");
        jobDesc.setCreatedAt(LocalDateTime.now());
        
        return jobDesc;
    }

    /**
     * Create a test MCPContext
     */
    public static MCPContext createTestMCPContext() {
        return createTestMCPContext("test-user");
    }

    /**
     * Create a test MCPContext with specified user ID
     */
    public static MCPContext createTestMCPContext(String userId) {
        MCPContext context = new MCPContext();
        context.setUserId(userId);
        context.setPreferredAIService("ollama");
        context.setPreferredAIModel("llama3.2:latest");
        context.setLastUpdated(LocalDateTime.now());
        
        return context;
    }

    /**
     * Create a test Resume
     */
    public static Resume createTestResume() {
        Resume resume = new Resume();
        resume.setUserId("test-user");
        resume.setFileName("test-resume.pdf");
        resume.setFilePath("/path/to/test-resume.pdf");
        resume.setFileSize(1024L);
        resume.setUploadedAt(LocalDateTime.now());
        
        return resume;
    }

    /**
     * Create a test InterviewMode
     */
    public static InterviewMode createTestInterviewMode() {
        return InterviewMode.TECHNICAL;
    }

    /**
     * Create a list of test questions
     */
    public static List<InterviewQuestion> createTestQuestions() {
        return List.of(
            createTestQuestion("What is your Java experience?", "Technical"),
            createTestQuestion("Describe a challenging project", "Behavioral"),
            createTestQuestion("How do you handle team conflicts?", "Leadership"),
            createTestQuestion("What is your approach to debugging?", "Technical"),
            createTestQuestion("Tell me about a time you failed", "Behavioral")
        );
    }

    /**
     * Create a test AI response for question generation
     */
    public static String createTestQuestionGenerationResponse() {
        return 
            "1. What is your experience with Java programming?\n" +
            "(This question assesses technical skills and experience level)\n\n" +
            "2. Describe a challenging project you worked on recently.\n" +
            "(This question evaluates problem-solving and project management skills)\n\n" +
            "3. How do you handle conflicts within your development team?\n" +
            "(This question tests leadership and communication skills)\n\n" +
            "4. What is your approach to debugging complex issues?\n" +
            "(This question examines technical problem-solving methodology)\n\n" +
            "5. Tell me about a time when you had to learn a new technology quickly.\n" +
            "(This question assesses adaptability and learning ability)";
    }

    /**
     * Create a test AI response for resume analysis
     */
    public static String createTestResumeAnalysisResponse() {
        return 
            "MATCH_SCORE: 85\n\n" +
            "OVERALL_FEEDBACK:\n" +
            "Strong candidate with relevant technical experience and good communication skills.\n\n" +
            "STRENGTHS:\n" +
            "- Strong Java programming skills\n" +
            "- Experience with Spring Boot and microservices\n" +
            "- Good problem-solving abilities\n" +
            "- Leadership experience in student organizations\n\n" +
            "WEAKNESSES:\n" +
            "- Limited industry experience\n" +
            "- Could improve cloud platform knowledge\n" +
            "- Needs more specific project examples\n\n" +
            "SUGGESTIONS:\n" +
            "- Gain more hands-on experience with AWS\n" +
            "- Prepare specific project examples with metrics\n" +
            "- Consider obtaining relevant certifications";
    }

    /**
     * Create a test AI response for interview analytics
     */
    public static String createTestAnalyticsResponse() {
        return 
            "OVERALL_SCORE: 8.5\n\n" +
            "TECHNICAL_SCORE: 9.0\n" +
            "BEHAVIORAL_SCORE: 8.0\n" +
            "COMMUNICATION_SCORE: 8.5\n" +
            "CONFIDENCE_SCORE: 8.0\n\n" +
            "PERFORMANCE_LEVEL: Good\n\n" +
            "STRENGTHS:\n" +
            "- Strong technical knowledge\n" +
            "- Good communication skills\n" +
            "- Problem-solving ability\n" +
            "- Clear explanations\n\n" +
            "WEAKNESSES:\n" +
            "- Could improve time management\n" +
            "- Needs more specific examples\n" +
            "- Could be more confident in responses\n\n" +
            "DETAILED_FEEDBACK:\n" +
            "Overall good performance with strong technical skills. Candidate demonstrated good understanding of concepts but could improve in providing specific examples and managing time better.\n\n" +
            "IMPROVEMENT_SUGGESTIONS:\n" +
            "- Practice more coding problems\n" +
            "- Prepare specific project examples\n" +
            "- Work on time management techniques\n" +
            "- Build confidence through practice";
    }

    /**
     * Assert that two InterviewSessions are equal (ignoring timestamps)
     */
    public static void assertSessionsEqual(InterviewSession expected, InterviewSession actual) {
        assertEquals(expected.getSessionId(), actual.getSessionId());
        assertEquals(expected.getMode(), actual.getMode());
        assertEquals(expected.getQuestions().size(), actual.getQuestions().size());
        assertEquals(expected.getUserAnswers().size(), actual.getUserAnswers().size());
        assertEquals(expected.getAnswerDurations().size(), actual.getAnswerDurations().size());
    }

    /**
     * Assert that two InterviewAnalytics are equal (ignoring timestamps)
     */
    public static void assertAnalyticsEqual(InterviewAnalytics expected, InterviewAnalytics actual) {
        assertEquals(expected.getSessionId(), actual.getSessionId());
        assertEquals(expected.getOverallScore(), actual.getOverallScore(), 0.01);
        assertEquals(expected.getTechnicalScore(), actual.getTechnicalScore(), 0.01);
        assertEquals(expected.getBehavioralScore(), actual.getBehavioralScore(), 0.01);
        assertEquals(expected.getCommunicationScore(), actual.getCommunicationScore(), 0.01);
        assertEquals(expected.getConfidenceScore(), actual.getConfidenceScore(), 0.01);
        assertEquals(expected.getPerformanceLevel(), actual.getPerformanceLevel());
        assertEquals(expected.getStrengths().size(), actual.getStrengths().size());
        assertEquals(expected.getWeaknesses().size(), actual.getWeaknesses().size());
    }
}
