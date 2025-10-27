# 🎯 AI Mock Interview Prep Tool

A comprehensive desktop application built with Java Swing that provides AI-powered mock interview preparation, resume analysis, and interview analytics. The application supports both local AI models (Ollama) and cloud-based AI services (AWS Bedrock) for flexible deployment options.

## ✨ Features

### 🤖 AI-Powered Interview Generation
- **Dynamic Question Generation**: Generate personalized interview questions based on job descriptions and resume content
- **Multiple AI Services**: Switch between Ollama (local) and AWS Bedrock (cloud) AI services
- **Smart Question Categorization**: Questions are automatically categorized (Technical, Behavioral, Leadership, etc.)

### 📄 Resume Analysis
- **PDF Resume Parsing**: Upload and analyze PDF resumes using Apache Tika
- **AI-Powered Analysis**: Get detailed feedback on resume strengths, weaknesses, and improvement suggestions
- **Job Matching**: Compare resume against job descriptions to identify skill gaps and matches

### 🎥 Interview Simulation
- **Video Recording**: Record interview sessions with webcam integration
- **Audio Recording**: Capture audio responses for later analysis
- **Real-time Question Display**: Clear, readable question presentation
- **Interview Modes**: Support for different interview types and durations

### 📊 Advanced Analytics
- **AI-Generated Analytics**: Comprehensive performance analysis using AI
- **Performance Scoring**: Technical, behavioral, communication, and confidence scores
- **Detailed Feedback**: Strengths, weaknesses, and improvement suggestions
- **Session History**: Track and review past interview sessions

### 🎤 Text-to-Speech
- **Question Narration**: Listen to questions being read aloud
- **Cross-Platform TTS**: Works on Windows, macOS, and Linux
- **Audio Controls**: Play, pause, and stop audio playback

### 🎨 Modern UI
- **Cross-Platform Design**: Consistent experience across Windows, macOS, and Linux
- **Responsive Layout**: Adaptive UI that works on different screen sizes
- **Icon Support**: Platform-optimized icons and emojis
- **Dark/Light Themes**: Modern FlatLaf theming support

## 🚀 Quick Start

### Prerequisites
- **Java 11 or higher**
- **Maven 3.6+**
- **Ollama** (for local AI) or **AWS Account** (for cloud AI)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd IDS517-InterviewApp
   ```

2. **Build the application**
   ```bash
   mvn clean compile
   ```

3. **Run the application**
   ```bash
   mvn exec:java
   ```

## 🔧 Configuration

### AI Service Setup

#### Option 1: Ollama (Local AI)
1. Install Ollama from [ollama.ai](https://ollama.ai)
2. Pull a model:
   ```bash
   ollama pull llama3.2:latest
   ```
3. The application will automatically detect and use Ollama

#### Option 2: AWS Bedrock (Cloud AI)
1. Set up AWS credentials:
   ```bash
   export AWS_ACCESS_KEY_ID="your-access-key"
   export AWS_SECRET_ACCESS_KEY="your-secret-key"
   export AWS_SESSION_TOKEN="your-session-token"  # For temporary credentials
   ```

2. Configure the model in `src/main/resources/application.properties`:
   ```properties
   ai.bedrock.model=amazon.titan-text-express-v1
   ai.bedrock.region=us-east-1
   ai.bedrock.enabled=true
   ```

### Application Properties
Edit `src/main/resources/application.properties` to customize:

```properties
# AI Service Configuration
ai.preferred.service=ollama  # or bedrock
ai.ollama.model=llama3.2:latest
ai.bedrock.model=amazon.titan-text-express-v1
ai.bedrock.region=us-east-1

# Recording Settings
recording.video.enabled=true
recording.audio.enabled=true
recording.output.directory=recordings

# UI Settings
ui.theme=flatlaf
ui.font.family=Segoe UI
```

## 📖 User Guide

### Getting Started

1. **Launch the Application**
   - Run `mvn exec:java` or use your IDE
   - The application will start with a splash screen

2. **Upload Your Resume**
   - Go to the "Resume Upload" tab
   - Click "Choose File" and select your PDF resume
   - The system will automatically parse and analyze your resume

3. **Enter Job Description**
   - Navigate to the "Job Description" tab
   - Paste the job description you're preparing for
   - This helps generate relevant interview questions

4. **Generate Interview Questions**
   - Go to the "Interview" tab
   - Click "Generate Questions" to create personalized questions
   - Review the generated questions before starting

5. **Start Your Mock Interview**
   - Click "Start Interview" to begin recording
   - Answer questions naturally - the system records both video and audio
   - Use "Next Question" to proceed through the interview

6. **Review Analytics**
   - After completing the interview, view detailed analytics
   - Get AI-powered feedback on your performance
   - Identify areas for improvement

### AI Service Management

The application supports switching between AI services:

1. **Go to AI Service Selector Tab**
2. **Test Available Services**
   - Click "Test All Services" to check availability
   - Green checkmarks indicate working services
3. **Switch Services**
   - Select your preferred AI service from the dropdown
   - Changes take effect immediately

### Recording Features

- **Video Recording**: Automatically starts when you begin an interview
- **Audio Recording**: Captures your voice responses
- **Playback**: Review your recordings in the Analytics tab
- **File Management**: All recordings are saved with timestamps

## 🛠️ Development

### Project Structure
```
src/
├── main/
│   ├── java/com/interviewprep/
│   │   ├── ui/                 # User interface components
│   │   ├── service/            # Business logic services
│   │   ├── model/              # Data models
│   │   ├── util/               # Utility classes
│   │   └── Main.java           # Application entry point
│   └── resources/
│       ├── application.properties
│       └── logback.xml
├── test/                       # Test suite
│   ├── java/com/interviewprep/
│   │   ├── service/            # Unit tests for services
│   │   ├── ui/                 # UI component tests
│   │   ├── integration/        # Integration tests
│   │   └── util/               # Test utilities
│   └── resources/
│       └── logback-test.xml    # Test logging configuration
├── .github/workflows/          # CI/CD pipeline
├── run-tests.sh               # Linux/macOS test runner
├── run-tests.bat              # Windows test runner
├── test-documentation.md      # Comprehensive test documentation
└── pom.xml                    # Maven configuration
```

### Key Components

- **MainFrame**: Main application window and tab management
- **InterviewPanel**: Interview simulation and recording
- **ResumeUploadPanel**: Resume upload and analysis
- **AnalyticsDashboardPanel**: Performance analytics and reporting
- **AIServiceManager**: Manages different AI service integrations
- **StorageService**: Handles data persistence and file management

### Testing Framework

The application includes a comprehensive test suite with:

#### 🧪 **Test Categories**
- **Unit Tests**: Fast, isolated tests for individual components
- **Integration Tests**: End-to-end workflow testing
- **UI Tests**: Component testing with mocked services
- **Test Utilities**: Common helpers and mock objects

#### 🚀 **Test Execution**
```bash
# Quick test run
./run-tests.sh    # Linux/macOS
run-tests.bat     # Windows

# Maven commands
mvn test                    # Unit tests only
mvn verify -Pintegration-tests  # Integration tests
mvn test -Dtest=OllamaServiceTest  # Specific test class
mvn test jacoco:report     # With coverage report
```

#### 📊 **Test Coverage**
- **Target Coverage**: 70%+ overall instruction coverage
- **Service Coverage**: 80%+ for critical business logic
- **Reports**: Generated in `target/site/jacoco/index.html`

#### 🔧 **Test Features**
- **Mock Services**: Isolated testing with Mockito
- **Test Data**: Automated test object creation
- **CI/CD Integration**: GitHub Actions for automated testing
- **Cross-Platform**: Tests run on Windows, macOS, and Linux
- **Documentation**: Comprehensive test documentation in `test-documentation.md`

### Building from Source

1. **Clone and build**
   ```bash
   git clone <repository-url>
   cd IDS517-InterviewApp
   mvn clean package
   ```

2. **Run tests**
   ```bash
   # Run all unit tests
   mvn test
   
   # Run integration tests
   mvn verify -Pintegration-tests
   
   # Run with test scripts
   ./run-tests.sh    # Linux/macOS
   run-tests.bat     # Windows
   ```

3. **Create executable JAR**
   ```bash
   mvn package
   java -jar target/ai-interview-prep-1.0.0.jar
   ```

## 🔧 Troubleshooting

### Common Issues

#### AI Service Not Available
- **Ollama**: Ensure Ollama is running (`ollama serve`)
- **Bedrock**: Check AWS credentials and model availability
- **Network**: Verify internet connection for cloud services

#### Recording Issues
- **Webcam**: Check camera permissions and availability
- **Microphone**: Ensure microphone access is granted
- **Storage**: Verify write permissions to recording directory

#### UI Issues
- **Icons**: Some icons may not display on older systems
- **Fonts**: Application uses system fonts for best compatibility
- **Themes**: Try switching between light and dark themes

### Logs and Debugging

Enable debug logging by editing `src/main/resources/logback.xml`:
```xml
<logger name="com.interviewprep" level="DEBUG"/>
```

Logs are written to:
- Console output
- `logs/application.log` (if configured)

## 🤝 Contributing

### Development Workflow

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Write tests for your changes**
   - Add unit tests for new functionality
   - Update integration tests if needed
   - Ensure all tests pass
4. **Commit your changes**
   ```bash
   git commit -m 'Add amazing feature'
   ```
5. **Run the test suite**
   ```bash
   ./run-tests.sh    # or run-tests.bat on Windows
   ```
6. **Push to the branch**
   ```bash
   git push origin feature/amazing-feature
   ```
7. **Open a Pull Request**

### Testing Requirements

- **All new code must have corresponding tests**
- **Tests must pass before merging**
- **Coverage must not decrease**
- **Integration tests must be added for new features**

### Code Quality

- Follow existing code style and patterns
- Add comprehensive documentation
- Include error handling and logging
- Test both success and failure scenarios

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Apache Tika** for document parsing
- **FlatLaf** for modern UI theming
- **Ollama** for local AI model support
- **AWS Bedrock** for cloud AI services
- **JavaCV** for video recording capabilities
- **JUnit 5** and **Mockito** for comprehensive testing
- **JaCoCo** for code coverage analysis
- **GitHub Actions** for CI/CD automation

## 📞 Support

For support and questions:
- Create an issue in the repository
- Check the troubleshooting section
- Review the logs for error details

---

**Built with ❤️ for better interview preparation**