package com.interviewprep.integration;

import com.interviewprep.service.*;
import com.interviewprep.model.*;
import com.interviewprep.util.TestUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests that test the complete flow of the application
 * These tests require actual services to be running (Ollama, etc.)
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationTestSuite {

    private static final String TEST_DATA_DIR = "target/test-data";
    private static final String TEST_USER_ID = "integration-test-user";
    
    private StorageService storageService;
    private AIServiceManager aiServiceManager;
    private InterviewService interviewService;
    private DocumentService documentService;

    @BeforeAll
    static void setUpTestEnvironment() throws IOException {
        // Create test data directory
        Path testDataPath = Paths.get(TEST_DATA_DIR);
        if (!Files.exists(testDataPath)) {
            Files.createDirectories(testDataPath);
        }
    }

    @BeforeEach
    void setUp() {
        // Initialize services with test data directory
        storageService = new StorageService(TEST_DATA_DIR);
        aiServiceManager = new AIServiceManager();
        documentService = new DocumentService();
        interviewService = new InterviewService(aiServiceManager, storageService, documentService);
    }

    @AfterEach
    void tearDown() {
        // Clean up test data
        try {
            storageService.deleteSession("test-session-id");
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test AI Service Availability")
    void testAIServiceAvailability() {
        // Test if at least one AI service is available
        List<String> availableServices = aiServiceManager.getAvailableServices();
        
        assertNotNull(availableServices, "Available services list should not be null");
        assertFalse(availableServices.isEmpty(), "At least one AI service should be available");
        
        System.out.println("Available AI services: " + availableServices);
    }

    @Test
    @Order(2)
    @DisplayName("Test AI Service Switching")
    void testAIServiceSwitching() throws IOException {
        // Test switching between available services
        List<String> availableServices = aiServiceManager.getAvailableServices();
        
        if (availableServices.contains("Ollama")) {
            aiServiceManager.setPreferredService("ollama");
            assertTrue(aiServiceManager.isAvailable(), "Ollama service should be available");
            assertEquals("Ollama", aiServiceManager.getServiceName());
        }
        
        if (availableServices.contains("Bedrock")) {
            aiServiceManager.setPreferredService("bedrock");
            assertTrue(aiServiceManager.isAvailable(), "Bedrock service should be available");
            assertEquals("Bedrock", aiServiceManager.getServiceName());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Test Question Generation Flow")
    void testQuestionGenerationFlow() throws IOException {
        // Skip if no AI service is available
        if (!aiServiceManager.isAvailable()) {
            System.out.println("Skipping question generation test - no AI service available");
            return;
        }

        String resumeText = "Experienced Java developer with 5 years of experience in Spring Boot, microservices, and AWS.";
        String jobDescription = "Looking for a senior Java developer with experience in Spring Boot, microservices, and cloud technologies.";

        // Generate questions
        List<InterviewQuestion> questions = interviewService.generateQuestions(resumeText, jobDescription);

        assertNotNull(questions, "Generated questions should not be null");
        assertFalse(questions.isEmpty(), "Should generate at least one question");
        assertTrue(questions.size() >= 3, "Should generate at least 3 questions");

        // Verify question structure
        for (InterviewQuestion question : questions) {
            assertNotNull(question.getQuestion(), "Question text should not be null");
            assertFalse(question.getQuestion().trim().isEmpty(), "Question text should not be empty");
            assertNotNull(question.getCategory(), "Question category should not be null");
        }

        System.out.println("Generated " + questions.size() + " questions successfully");
    }

    @Test
    @Order(4)
    @DisplayName("Test Resume Analysis Flow")
    void testResumeAnalysisFlow() throws IOException {
        // Skip if no AI service is available
        if (!aiServiceManager.isAvailable()) {
            System.out.println("Skipping resume analysis test - no AI service available");
            return;
        }

        String resumeText = "Senior Java Developer with 5 years of experience in Spring Boot, microservices, and AWS. " +
                           "Led a team of 3 developers and implemented CI/CD pipelines.";
        String jobDescription = "Looking for a senior Java developer with leadership experience and cloud knowledge.";

        // Analyze resume
        ResumeAnalysis analysis = interviewService.analyzeResume(resumeText, jobDescription);

        assertNotNull(analysis, "Resume analysis should not be null");
        assertTrue(analysis.getMatchScore() >= 0 && analysis.getMatchScore() <= 100, 
                  "Match score should be between 0 and 100");
        assertNotNull(analysis.getStrengths(), "Strengths should not be null");
        assertNotNull(analysis.getWeaknesses(), "Weaknesses should not be null");
        assertFalse(analysis.getStrengths().isEmpty(), "Should identify at least one strength");
        assertFalse(analysis.getWeaknesses().isEmpty(), "Should identify at least one weakness");

        System.out.println("Resume analysis completed - Match Score: " + analysis.getMatchScore());
    }

    @Test
    @Order(5)
    @DisplayName("Test Interview Session Flow")
    void testInterviewSessionFlow() throws IOException {
        // Skip if no AI service is available
        if (!aiServiceManager.isAvailable()) {
            System.out.println("Skipping interview session test - no AI service available");
            return;
        }

        // Create test session
        InterviewMode mode = InterviewMode.TECHNICAL;
        List<InterviewQuestion> questions = TestUtils.createTestQuestions();
        InterviewSession session = interviewService.createSession(mode, questions);

        assertNotNull(session, "Session should not be null");
        assertNotNull(session.getSessionId(), "Session ID should not be null");
        assertEquals(mode, session.getMode(), "Session mode should match");
        assertEquals(questions.size(), session.getQuestions().size(), "Session should have all questions");

        // Test answering questions
        for (int i = 0; i < questions.size(); i++) {
            InterviewQuestion currentQuestion = interviewService.getCurrentQuestion(session);
            assertNotNull(currentQuestion, "Current question should not be null");
            assertEquals(questions.get(i).getQuestion(), currentQuestion.getQuestion());

            // Submit answer
            String answer = "Test answer for question " + (i + 1);
            interviewService.submitAnswer(session, answer);

            // Move to next question
            session.setCurrentQuestionIndex(session.getCurrentQuestionIndex() + 1);
        }

        // Complete session
        interviewService.completeSession(session);
        assertNotNull(session.getEndTime(), "Session should have end time");
        assertTrue(session.getTotalDurationSeconds() > 0, "Session should have positive duration");

        // Save session
        storageService.saveSession(session);
        InterviewSession loadedSession = storageService.loadSession(session.getSessionId());
        assertNotNull(loadedSession, "Saved session should be loadable");
        assertEquals(session.getSessionId(), loadedSession.getSessionId());

        System.out.println("Interview session flow completed successfully");
    }

    @Test
    @Order(6)
    @DisplayName("Test Analytics Generation Flow")
    void testAnalyticsGenerationFlow() throws IOException {
        // Skip if no AI service is available
        if (!aiServiceManager.isAvailable()) {
            System.out.println("Skipping analytics generation test - no AI service available");
            return;
        }

        // Create and save a test session
        InterviewSession session = TestUtils.createTestSession();
        storageService.saveSession(session);

        // Generate analytics
        InterviewAnalytics analytics = interviewService.generateAnalytics(session);

        assertNotNull(analytics, "Analytics should not be null");
        assertEquals(session.getSessionId(), analytics.getSessionId(), "Analytics should match session ID");
        assertTrue(analytics.getOverallScore() >= 0 && analytics.getOverallScore() <= 10, 
                  "Overall score should be between 0 and 10");
        assertNotNull(analytics.getStrengths(), "Strengths should not be null");
        assertNotNull(analytics.getWeaknesses(), "Weaknesses should not be null");
        assertFalse(analytics.getStrengths().isEmpty(), "Should identify at least one strength");
        assertFalse(analytics.getWeaknesses().isEmpty(), "Should identify at least one weakness");

        // Save analytics
        storageService.saveAnalytics(analytics);
        InterviewAnalytics loadedAnalytics = storageService.loadAnalytics(session.getSessionId());
        assertNotNull(loadedAnalytics, "Saved analytics should be loadable");
        assertEquals(analytics.getSessionId(), loadedAnalytics.getSessionId());

        System.out.println("Analytics generation completed successfully");
    }

    @Test
    @Order(7)
    @DisplayName("Test Data Persistence Flow")
    void testDataPersistenceFlow() throws IOException {
        // Test saving and loading various data types
        ResumeAnalysis resumeAnalysis = TestUtils.createTestResumeAnalysis();
        JobDescription jobDescription = TestUtils.createTestJobDescription();
        MCPContext mcpContext = TestUtils.createTestMCPContext();

        // Save data
        storageService.saveResumeAnalysis(resumeAnalysis);
        storageService.saveJobDescription(jobDescription);
        storageService.saveMCPContext(mcpContext);

        // Load data
        ResumeAnalysis loadedResumeAnalysis = storageService.loadResumeAnalysis(resumeAnalysis.getUserId());
        JobDescription loadedJobDescription = storageService.loadJobDescription(jobDescription.getUserId());
        MCPContext loadedMCPContext = storageService.loadMCPContext(mcpContext.getUserId());

        // Verify data integrity
        assertNotNull(loadedResumeAnalysis, "Resume analysis should be loadable");
        assertEquals(resumeAnalysis.getMatchScore(), loadedResumeAnalysis.getMatchScore());

        assertNotNull(loadedJobDescription, "Job description should be loadable");
        assertEquals(jobDescription.getTitle(), loadedJobDescription.getTitle());

        assertNotNull(loadedMCPContext, "MCP context should be loadable");
        assertEquals(mcpContext.getPreferredAIService(), loadedMCPContext.getPreferredAIService());

        System.out.println("Data persistence flow completed successfully");
    }

    @Test
    @Order(8)
    @DisplayName("Test Error Handling")
    void testErrorHandling() {
        // Test handling of invalid inputs
        assertThrows(IllegalArgumentException.class, () -> {
            new StorageService(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new StorageService("");
        });

        // Test handling of non-existent data
        assertNull(storageService.loadSession("non-existent-id"));
        assertNull(storageService.loadAnalytics("non-existent-id"));
        assertNull(storageService.loadResumeAnalysis("non-existent-user"));

        System.out.println("Error handling tests completed successfully");
    }
}
