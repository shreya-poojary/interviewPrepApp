package com.interviewprep.service;

import com.interviewprep.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock
    private AIServiceManager mockAIServiceManager;

    @Mock
    private StorageService mockStorageService;

    @Mock
    private DocumentService mockDocumentService;

    private InterviewService interviewService;

    @BeforeEach
    void setUp() {
        interviewService = new InterviewService(mockAIServiceManager, mockStorageService, mockDocumentService);
    }

    @Test
    void testGenerateQuestions_Success() throws IOException {
        // Arrange
        String resumeText = "Experienced Java developer with 5 years of experience";
        String jobDescription = "Looking for a senior Java developer";
        String aiResponse = "1. Tell me about your Java experience\n2. Describe a challenging project\n3. How do you handle debugging?";
        
        when(mockAIServiceManager.generate(anyString())).thenReturn(aiResponse);

        // Act
        List<InterviewQuestion> questions = interviewService.generateQuestions(resumeText, jobDescription);

        // Assert
        assertNotNull(questions);
        assertTrue(questions.size() > 0);
        verify(mockAIServiceManager).generate(anyString());
    }

    @Test
    void testGenerateQuestions_IOException() throws IOException {
        // Arrange
        String resumeText = "Resume text";
        String jobDescription = "Job description";
        
        when(mockAIServiceManager.generate(anyString())).thenThrow(new IOException("AI service error"));

        // Act & Assert
        assertThrows(IOException.class, () -> {
            interviewService.generateQuestions(resumeText, jobDescription);
        });
    }

    @Test
    void testAnalyzeResume_Success() throws IOException {
        // Arrange
        String resumeText = "Experienced Java developer";
        String jobDescription = "Java developer position";
        String aiResponse = "MATCH_SCORE: 85\nSTRENGTHS:\n- Strong Java skills\nWEAKNESSES:\n- Limited cloud experience";
        
        when(mockAIServiceManager.generate(anyString())).thenReturn(aiResponse);

        // Act
        ResumeAnalysis analysis = interviewService.analyzeResume(resumeText, jobDescription);

        // Assert
        assertNotNull(analysis);
        assertEquals(85, analysis.getMatchScore());
        assertTrue(analysis.getStrengths().contains("Strong Java skills"));
        verify(mockAIServiceManager).generate(anyString());
    }

    @Test
    void testAnalyzeResume_IOException() throws IOException {
        // Arrange
        String resumeText = "Resume text";
        String jobDescription = "Job description";
        
        when(mockAIServiceManager.generate(anyString())).thenThrow(new IOException("AI service error"));

        // Act & Assert
        assertThrows(IOException.class, () -> {
            interviewService.analyzeResume(resumeText, jobDescription);
        });
    }

    @Test
    void testGenerateAnalytics_Success() throws IOException {
        // Arrange
        InterviewSession session = createTestSession();
        String aiResponse = "OVERALL_SCORE: 8.5\nTECHNICAL_SCORE: 9.0\nBEHAVIORAL_SCORE: 8.0\n" +
                           "COMMUNICATION_SCORE: 8.5\nCONFIDENCE_SCORE: 8.0\n" +
                           "PERFORMANCE_LEVEL: Good\n" +
                           "STRENGTHS:\n- Strong technical knowledge\n- Good communication\n" +
                           "WEAKNESSES:\n- Could improve time management\n" +
                           "DETAILED_FEEDBACK: Good overall performance\n" +
                           "IMPROVEMENT_SUGGESTIONS:\n- Practice more coding problems";
        
        when(mockAIServiceManager.generate(anyString())).thenReturn(aiResponse);

        // Act
        InterviewAnalytics analytics = interviewService.generateAnalytics(session);

        // Assert
        assertNotNull(analytics);
        assertEquals(session.getSessionId(), analytics.getSessionId());
        assertEquals(8.5, analytics.getOverallScore());
        assertEquals(9.0, analytics.getTechnicalScore());
        assertEquals("Good", analytics.getPerformanceLevel());
        assertTrue(analytics.getStrengths().contains("Strong technical knowledge"));
        assertTrue(analytics.getWeaknesses().contains("Could improve time management"));
        verify(mockAIServiceManager).generate(anyString());
    }

    @Test
    void testGenerateAnalytics_IOException() throws IOException {
        // Arrange
        InterviewSession session = createTestSession();
        
        when(mockAIServiceManager.generate(anyString())).thenThrow(new IOException("AI service error"));

        // Act & Assert
        assertThrows(IOException.class, () -> {
            interviewService.generateAnalytics(session);
        });
    }

    @Test
    void testCreateSession() {
        // Arrange
        InterviewMode mode = InterviewMode.TECHNICAL;
        List<InterviewQuestion> questions = List.of(
            createTestQuestion("What is your Java experience?", "Technical"),
            createTestQuestion("Describe a challenging project", "Behavioral")
        );

        // Act
        InterviewSession session = interviewService.createSession(mode, questions);

        // Assert
        assertNotNull(session);
        assertNotNull(session.getSessionId());
        assertEquals(mode, session.getMode());
        assertEquals(questions.size(), session.getQuestions().size());
        assertNotNull(session.getStartTime());
    }

    @Test
    void testSubmitAnswer() {
        // Arrange
        InterviewSession session = createTestSession();
        String answer = "I have 5 years of Java experience";
        
        // Act
        interviewService.submitAnswer(session, answer);

        // Assert
        assertEquals(1, session.getUserAnswers().size());
        assertEquals(answer, session.getUserAnswers().get(0));
        assertEquals(1, session.getAnswerDurations().size());
        assertTrue(session.getAnswerDurations().get(0) >= 0);
    }

    @Test
    void testCompleteSession() {
        // Arrange
        InterviewSession session = createTestSession();
        session.setStartTime(LocalDateTime.now().minusMinutes(30));

        // Act
        interviewService.completeSession(session);

        // Assert
        assertNotNull(session.getEndTime());
        assertTrue(session.getTotalDurationSeconds() > 0);
    }

    @Test
    void testGetCurrentQuestion() {
        // Arrange
        InterviewSession session = createTestSession();
        session.getQuestions().add(createTestQuestion("Question 1", "Technical"));
        session.getQuestions().add(createTestQuestion("Question 2", "Behavioral"));

        // Act
        InterviewQuestion question1 = interviewService.getCurrentQuestion(session);
        session.setCurrentQuestionIndex(1);
        InterviewQuestion question2 = interviewService.getCurrentQuestion(session);

        // Assert
        assertNotNull(question1);
        assertEquals("Question 1", question1.getQuestion());
        assertNotNull(question2);
        assertEquals("Question 2", question2.getQuestion());
    }

    @Test
    void testGetCurrentQuestion_OutOfBounds() {
        // Arrange
        InterviewSession session = createTestSession();
        session.setCurrentQuestionIndex(10); // Out of bounds

        // Act
        InterviewQuestion question = interviewService.getCurrentQuestion(session);

        // Assert
        assertNull(question);
    }

    @Test
    void testIsSessionComplete() {
        // Arrange
        InterviewSession session = createTestSession();
        session.getQuestions().add(createTestQuestion("Question 1", "Technical"));
        session.getQuestions().add(createTestQuestion("Question 2", "Behavioral"));

        // Act & Assert
        assertFalse(interviewService.isSessionComplete(session));
        
        session.setCurrentQuestionIndex(2);
        assertTrue(interviewService.isSessionComplete(session));
    }

    @Test
    void testGetSessionProgress() {
        // Arrange
        InterviewSession session = createTestSession();
        session.getQuestions().add(createTestQuestion("Question 1", "Technical"));
        session.getQuestions().add(createTestQuestion("Question 2", "Behavioral"));
        session.getQuestions().add(createTestQuestion("Question 3", "Leadership"));

        // Act
        double progress1 = interviewService.getSessionProgress(session);
        session.setCurrentQuestionIndex(1);
        double progress2 = interviewService.getSessionProgress(session);
        session.setCurrentQuestionIndex(3);
        double progress3 = interviewService.getSessionProgress(session);

        // Assert
        assertEquals(0.0, progress1, 0.01);
        assertEquals(33.33, progress2, 0.01);
        assertEquals(100.0, progress3, 0.01);
    }

    // Helper methods
    private InterviewSession createTestSession() {
        InterviewSession session = new InterviewSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setMode(InterviewMode.TECHNICAL);
        session.setStartTime(LocalDateTime.now());
        return session;
    }

    private InterviewQuestion createTestQuestion(String question, String category) {
        InterviewQuestion q = new InterviewQuestion();
        q.setQuestion(question);
        q.setCategory(category);
        return q;
    }
}
