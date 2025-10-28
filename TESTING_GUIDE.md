# AI Interview Prep Tool - Testing Guide

## Overview
This document provides comprehensive testing procedures for the AI Interview Prep Tool. All tests are manual and should be performed to ensure the application functions correctly across all features.

## Prerequisites
- Java 17 or higher installed
- Maven installed
- Ollama running locally (for AI service testing)
- AWS credentials configured (for Bedrock testing)
- Webcam and microphone available (for video/audio testing)

## Test Environment Setup

### 1. Build and Run Application
```bash
# Clean and compile
mvn clean compile

# Run the application
mvn exec:java
```

**Expected Result:** Application starts successfully with GUI window opening.

### 2. Verify Service Status
Check the service status panel in the application:
- ✅ AI service is available (Ollama or Bedrock)
- ✅ Text-to-speech is available
- ✅ Webcam is available
- ✅ Microphone is available

## Feature Testing

### Test 1: Resume Upload and Analysis

#### 1.1 Resume Upload
1. Navigate to the Resume Review tab
2. Click "Upload Resume" button
3. Select a PDF or text file containing resume content
4. Wait for upload to complete

**Expected Result:** 
- File uploads successfully
- Resume content is extracted and displayed
- "Resume loaded: [filename]" message appears

#### 1.2 Resume Analysis
1. Upload a job description file (text format)
2. Click "Analyze Resume" button
3. Wait for analysis to complete

**Expected Result:**
- Analysis completes without errors
- Results show:
  - Match Score (0-100)
  - Overall Feedback
  - Strengths list
  - Weaknesses list
  - Suggestions list
  - Matching Skills
  - Missing Skills

**Test Cases:**
- Test with different resume formats (PDF, TXT)
- Test with various job descriptions
- Test with long resume content (>10,000 characters)
- Test with special characters in resume content

### Test 2: Interview Question Generation

#### 2.1 Basic Question Generation
1. Ensure resume and job description are loaded
2. Navigate to Interview tab
3. Select interview mode (Practice, Mock, Technical)
4. Set number of questions (1-10)
5. Click "Generate Questions" button

**Expected Result:**
- Questions generate successfully
- Questions are numbered and formatted properly
- Questions are relevant to the job description and resume
- Questions are categorized (Technical, Behavioral, General)

#### 2.2 Different Interview Modes
Test each interview mode:

**Practice Mode:**
- Questions should be easier/more basic
- Time limit: 60 seconds per question
- Focus on general skills

**Mock Mode:**
- Questions should be moderate difficulty
- Time limit: 90 seconds per question
- Mix of technical and behavioral

**Technical Mode:**
- Questions should be challenging
- Time limit: 120 seconds per question
- Focus on technical skills and problem-solving

### Test 3: AI Service Switching

#### 3.1 Switch Between AI Services
1. Use the AI service selector in the top panel
2. Switch between Ollama and Bedrock (if available)
3. Test question generation with each service

**Expected Result:**
- Service switches without errors
- Both services generate valid responses
- No "Malformed input request" errors with Bedrock
- Fallback to Ollama if Bedrock fails

#### 3.2 Service Availability Testing
1. Test with Ollama running
2. Test with Ollama stopped (should show as unavailable)
3. Test with valid AWS credentials
4. Test with invalid/expired AWS credentials

**Expected Result:**
- Service status accurately reflects availability
- Graceful handling of service unavailability
- Proper error messages for failed services

### Test 4: Interview Session Management

#### 4.1 Start Interview Session
1. Generate questions
2. Click "Start Interview" button
3. Verify timer starts counting down

**Expected Result:**
- Interview session begins
- Timer displays correctly
- Question is displayed clearly
- Recording indicators show (if enabled)

#### 4.2 Answer Recording
1. Click "Start Recording" button
2. Speak an answer
3. Click "Stop Recording" button
4. Verify answer is captured

**Expected Result:**
- Audio recording starts/stops properly
- Answer text is displayed
- Answer duration is tracked
- No audio quality issues

#### 4.3 Question Navigation
1. Answer current question
2. Click "Next Question" button
3. Navigate through all questions

**Expected Result:**
- Smooth navigation between questions
- Previous answers are preserved
- Timer resets for each question
- Progress indicator updates

### Test 5: Answer Evaluation

#### 5.1 Submit Answer for Evaluation
1. Complete an interview session
2. Click "Evaluate Answers" button
3. Wait for evaluation to complete

**Expected Result:**
- Evaluation completes successfully
- Scores are generated (0-10 scale)
- Detailed feedback is provided
- Suggestions for improvement are given

#### 5.2 Analytics Generation
1. Complete a full interview session
2. Click "Generate Analytics" button
3. Review the analytics report

**Expected Result:**
- Analytics generate without errors
- Report includes:
  - Overall Score
  - Technical Score
  - Behavioral Score
  - Communication Score
  - Confidence Score
  - Performance Level
  - Strengths and Weaknesses
  - Detailed Feedback
  - Improvement Suggestions

### Test 6: Video Recording (if enabled)

#### 6.1 Video Recording Setup
1. Enable video recording in settings
2. Start an interview session
3. Verify webcam is detected

**Expected Result:**
- Webcam initializes properly
- Video preview shows (if available)
- Recording starts without errors

#### 6.2 Video Recording During Interview
1. Start interview with video recording enabled
2. Answer questions while being recorded
3. Stop recording and review

**Expected Result:**
- Video records smoothly
- No frame drops or quality issues
- Video file is saved properly
- Video syncs with audio

### Test 7: Text-to-Speech

#### 7.1 TTS Question Reading
1. Enable TTS in settings
2. Generate questions
3. Click "Read Question" button

**Expected Result:**
- Question is read aloud clearly
- Voice is natural and understandable
- No audio distortion
- TTS can be stopped/started

#### 7.2 TTS Feedback Reading
1. Complete answer evaluation
2. Click "Read Feedback" button

**Expected Result:**
- Feedback is read aloud
- Long feedback is handled properly
- TTS works with special characters

### Test 8: Keyboard Shortcuts

#### 8.1 Test All Keyboard Shortcuts
- `Ctrl+N`: New Interview
- `Ctrl+O`: Open Resume
- `Ctrl+S`: Save Session
- `Space`: Start/Stop Recording
- `Enter`: Submit Answer
- `Tab`: Next Question

**Expected Result:**
- All shortcuts work as expected
- No conflicts with system shortcuts
- Shortcuts work in all tabs

### Test 9: Error Handling

#### 9.1 Network Error Handling
1. Disconnect internet connection
2. Try to generate questions
3. Reconnect and retry

**Expected Result:**
- Graceful error messages
- No application crashes
- Retry functionality works

#### 9.2 File Error Handling
1. Try to upload invalid file formats
2. Try to upload corrupted files
3. Try to upload very large files

**Expected Result:**
- Appropriate error messages
- File validation works
- No application crashes

#### 9.3 Service Error Handling
1. Stop Ollama service during question generation
2. Use invalid AWS credentials
3. Test with no internet connection

**Expected Result:**
- Service unavailability is handled gracefully
- Fallback mechanisms work
- User is informed of issues

### Test 10: Performance Testing

#### 10.1 Large Resume Handling
1. Upload a very large resume (>50,000 characters)
2. Generate questions
3. Monitor memory usage

**Expected Result:**
- Application handles large files
- No memory leaks
- Reasonable response times

#### 10.2 Long Interview Sessions
1. Generate 10 questions
2. Complete full interview
3. Generate analytics

**Expected Result:**
- Application remains responsive
- No performance degradation
- All features work correctly

## Regression Testing

### Critical Path Testing
1. Upload resume → Upload job description → Generate questions → Start interview → Answer questions → Evaluate answers → Generate analytics
2. Test this complete flow with both AI services
3. Test with different file types and sizes

### Cross-Platform Testing
- Test on Windows 10/11
- Test with different Java versions
- Test with different screen resolutions

## Known Issues and Workarounds

### Issue 1: Bedrock Validation Errors
**Problem:** "Malformed input request" errors with Bedrock
**Status:** Fixed in latest version
**Workaround:** Application automatically falls back to Ollama

### Issue 2: Large File Upload
**Problem:** Very large files may cause memory issues
**Status:** Mitigated with input validation
**Workaround:** Use files under 50,000 characters

### Issue 3: Audio Recording Quality
**Problem:** Audio quality may vary based on system
**Status:** Known limitation
**Workaround:** Use external microphone for better quality

## Test Data

### Sample Resume Content
```
John Doe
Software Engineer
Email: john.doe@email.com
Phone: (555) 123-4567

EXPERIENCE:
- Senior Software Engineer at TechCorp (2020-2023)
- Developed web applications using Java, Spring Boot
- Led team of 5 developers
- Implemented CI/CD pipelines

SKILLS:
- Java, Spring Boot, React
- AWS, Docker, Kubernetes
- SQL, MongoDB
- Git, Jenkins
```

### Sample Job Description
```
Software Engineer Position

Requirements:
- 3+ years Java development experience
- Experience with Spring Boot framework
- Knowledge of cloud platforms (AWS preferred)
- Strong problem-solving skills
- Team leadership experience

Responsibilities:
- Develop and maintain web applications
- Collaborate with cross-functional teams
- Mentor junior developers
- Participate in code reviews
```

## Test Completion Checklist

- [ ] Application builds and runs successfully
- [ ] All service status indicators work
- [ ] Resume upload and analysis works
- [ ] Question generation works for all modes
- [ ] AI service switching works
- [ ] Interview session management works
- [ ] Answer evaluation works
- [ ] Analytics generation works
- [ ] Video recording works (if enabled)
- [ ] Text-to-speech works
- [ ] Keyboard shortcuts work
- [ ] Error handling works properly
- [ ] Performance is acceptable
- [ ] No memory leaks detected
- [ ] Cross-platform compatibility verified

## Reporting Issues

When reporting issues, please include:
1. Operating system and version
2. Java version
3. Steps to reproduce
4. Expected vs actual behavior
5. Error messages (if any)
6. Log files (if available)

## Test Environment Information

- **Application Version:** 1.0.0
- **Java Version:** 17+
- **Maven Version:** 3.6+
- **Tested Platforms:** Windows 10/11
- **Last Updated:** October 28, 2025
