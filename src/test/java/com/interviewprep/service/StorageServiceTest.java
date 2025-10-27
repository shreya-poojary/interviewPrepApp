package com.interviewprep.service;

import com.interviewprep.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StorageServiceTest {

    @TempDir
    Path tempDir;

    private StorageService storageService;

    @BeforeEach
    void setUp() {
        storageService = new StorageService(tempDir.toString());
    }

    @Test
    void testSaveAndLoadSession() throws IOException {
        // Arrange
        InterviewSession session = createTestSession();
        
        // Act
        storageService.saveSession(session);
        InterviewSession loadedSession = storageService.loadSession(session.getSessionId());
        
        // Assert
        assertNotNull(loadedSession);
        assertEquals(session.getSessionId(), loadedSession.getSessionId());
        assertEquals(session.getMode(), loadedSession.getMode());
        assertEquals(session.getQuestions().size(), loadedSession.getQuestions().size());
        assertEquals(session.getUserAnswers().size(), loadedSession.getUserAnswers().size());
    }

    @Test
    void testSaveAndLoadAnalytics() throws IOException {
        // Arrange
        InterviewAnalytics analytics = createTestAnalytics();
        
        // Act
        storageService.saveAnalytics(analytics);
        InterviewAnalytics loadedAnalytics = storageService.loadAnalytics(analytics.getSessionId());
        
        // Assert
        assertNotNull(loadedAnalytics);
        assertEquals(analytics.getSessionId(), loadedAnalytics.getSessionId());
        assertEquals(analytics.getOverallScore(), loadedAnalytics.getOverallScore());
        assertEquals(analytics.getStrengths().size(), loadedAnalytics.getStrengths().size());
    }

    @Test
    void testGetRecentSessions() throws IOException {
        // Arrange
        InterviewSession session1 = createTestSession();
        InterviewSession session2 = createTestSession();
        session2.setSessionId(UUID.randomUUID().toString());
        
        storageService.saveSession(session1);
        storageService.saveSession(session2);
        
        // Act
        List<InterviewSession> sessions = storageService.getRecentSessions(10);
        
        // Assert
        assertNotNull(sessions);
        assertTrue(sessions.size() >= 2);
    }

    @Test
    void testSaveAndLoadResumeAnalysis() throws IOException {
        // Arrange
        ResumeAnalysis analysis = createTestResumeAnalysis();
        
        // Act
        storageService.saveResumeAnalysis(analysis);
        ResumeAnalysis loadedAnalysis = storageService.loadResumeAnalysis(analysis.getUserId());
        
        // Assert
        assertNotNull(loadedAnalysis);
        assertEquals(analysis.getUserId(), loadedAnalysis.getUserId());
        assertEquals(analysis.getMatchScore(), loadedAnalysis.getMatchScore());
    }

    @Test
    void testSaveAndLoadJobDescription() throws IOException {
        // Arrange
        JobDescription jobDesc = createTestJobDescription();
        
        // Act
        storageService.saveJobDescription(jobDesc);
        JobDescription loadedJobDesc = storageService.loadJobDescription(jobDesc.getUserId());
        
        // Assert
        assertNotNull(loadedJobDesc);
        assertEquals(jobDesc.getUserId(), loadedJobDesc.getUserId());
        assertEquals(jobDesc.getTitle(), loadedJobDesc.getTitle());
    }

    @Test
    void testLoadNonExistentSession() {
        // Act
        InterviewSession session = storageService.loadSession("non-existent-id");
        
        // Assert
        assertNull(session);
    }

    @Test
    void testLoadNonExistentAnalytics() {
        // Act
        InterviewAnalytics analytics = storageService.loadAnalytics("non-existent-id");
        
        // Assert
        assertNull(analytics);
    }

    @Test
    void testGetAllSessions() throws IOException {
        // Arrange
        InterviewSession session1 = createTestSession();
        InterviewSession session2 = createTestSession();
        session2.setSessionId(UUID.randomUUID().toString());
        
        storageService.saveSession(session1);
        storageService.saveSession(session2);
        
        // Act
        List<InterviewSession> allSessions = storageService.getAllSessions();
        
        // Assert
        assertNotNull(allSessions);
        assertTrue(allSessions.size() >= 2);
    }

    @Test
    void testDeleteSession() throws IOException {
        // Arrange
        InterviewSession session = createTestSession();
        storageService.saveSession(session);
        
        // Act
        boolean deleted = storageService.deleteSession(session.getSessionId());
        
        // Assert
        assertTrue(deleted);
        assertNull(storageService.loadSession(session.getSessionId()));
    }

    @Test
    void testDeleteNonExistentSession() {
        // Act
        boolean deleted = storageService.deleteSession("non-existent-id");
        
        // Assert
        assertFalse(deleted);
    }

    // Helper methods to create test objects
    private InterviewSession createTestSession() {
        InterviewSession session = new InterviewSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setMode(InterviewMode.TECHNICAL);
        session.setStartTime(LocalDateTime.now());
        session.setEndTime(LocalDateTime.now().plusMinutes(30));
        
        InterviewQuestion question = new InterviewQuestion();
        question.setQuestion("What is your experience with Java?");
        question.setCategory("Technical");
        session.getQuestions().add(question);
        
        session.getUserAnswers().add("I have 5 years of Java experience");
        session.getAnswerDurations().add(120);
        
        return session;
    }

    private InterviewAnalytics createTestAnalytics() {
        InterviewAnalytics analytics = new InterviewAnalytics();
        analytics.setSessionId(UUID.randomUUID().toString());
        analytics.setOverallScore(8.5);
        analytics.setTechnicalScore(9.0);
        analytics.setBehavioralScore(8.0);
        analytics.setCommunicationScore(8.5);
        analytics.setConfidenceScore(8.0);
        analytics.setPerformanceLevel("Good");
        analytics.setDetailedFeedback("Good overall performance");
        analytics.setStrengths(List.of("Strong technical knowledge", "Good communication"));
        analytics.setWeaknesses(List.of("Could improve time management"));
        analytics.setImprovementSuggestions(List.of("Practice more coding problems"));
        analytics.setGeneratedAt(LocalDateTime.now());
        
        return analytics;
    }

    private ResumeAnalysis createTestResumeAnalysis() {
        ResumeAnalysis analysis = new ResumeAnalysis();
        analysis.setUserId("test-user");
        analysis.setMatchScore(85);
        analysis.setOverallFeedback("Strong candidate with relevant experience");
        analysis.setStrengths(List.of("Technical skills", "Leadership experience"));
        analysis.setWeaknesses(List.of("Limited industry experience"));
        analysis.setSuggestions(List.of("Gain more hands-on experience"));
        analysis.setAnalyzedAt(LocalDateTime.now());
        
        return analysis;
    }

    private JobDescription createTestJobDescription() {
        JobDescription jobDesc = new JobDescription();
        jobDesc.setUserId("test-user");
        jobDesc.setTitle("Senior Software Engineer");
        jobDesc.setCompany("Tech Corp");
        jobDesc.setDescription("Looking for a senior software engineer with Java experience");
        jobDesc.setRequirements("5+ years Java, Spring Boot, AWS");
        jobDesc.setCreatedAt(LocalDateTime.now());
        
        return jobDesc;
    }
}
