# 🎯 AI Mock Interview Prep Tool

A **free**, **AI-powered** desktop application for interview preparation with video/audio recording, real-time feedback, and comprehensive analytics. Built with Java Swing and integrated with **100% free** AI tools.

![Java](https://img.shields.io/badge/Java-17+-orange)
![License](https://img.shields.io/badge/License-MIT-green)
![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20Linux%20%7C%20Mac-blue)

## ✨ Features

### 🤖 AI-Powered Intelligence
- **Ollama Integration** - Free local LLM (Llama 3.1) for resume analysis and question generation
- **Smart Resume Analysis** - AI evaluates your resume against job descriptions
- **Dynamic Question Generation** - Personalized interview questions based on your background
- **Real-time Feedback** - Instant AI evaluation of your answers

### 🎥 Video/Audio Recording
- **Full Interview Recording** - Capture video and audio of your practice sessions
- **Local Storage** - All recordings saved securely on your machine
- **Playback & Review** - Review your performance anytime
- **Auto-Transcription** - OpenAI Whisper converts speech to text (free & local)

### 🎤 Voice Features
- **Text-to-Speech** - AI interviewer asks questions aloud using Piper TTS
- **Speech-to-Text** - Your spoken answers are transcribed automatically
- **Natural Voices** - High-quality neural voices (free)

### 📊 Analytics Dashboard
- **Performance Tracking** - Monitor your improvement over time
- **Detailed Metrics** - Scores for technical, behavioral, and communication skills
- **Filler Word Detection** - Track and reduce "um", "uh", "like"
- **Answer Analysis** - Word count, pace, confidence metrics

### 🎯 Multiple Interview Modes
- **Practice Mode** - Unlimited time, hints available
- **Timed Mode** - Strict time limits, real pressure
- **Surprise Mode** - Random questions, test adaptability
- **FAANG Mode** - Tech giant style interviews
- **Startup Mode** - Fast-paced, culture fit focus
- **Behavioral Mode** - STAR method, soft skills

### ⌨️ Productivity Features
- **Keyboard Shortcuts** - Space, Ctrl+N, Ctrl+M, Ctrl+A, Ctrl+Q, F1
- **Progress Bars** - Visual feedback on recording and progress
- **Auto-Save** - Never lose your session data
- **MCP (Model Context Protocol)** - Adaptive difficulty based on performance

## 🚀 Quick Start

### Prerequisites
- **Java 17+** - [Download](https://adoptium.net/)
- **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
- **Python 3.8+** (for Whisper) - [Download](https://www.python.org/downloads/)
- **Ollama** - [Download](https://ollama.ai/download)

### Installation

#### Windows
```batch
# 1. Clone the repository
git clone <your-repo-url>
cd IDS517-InterviewApp

# 2. Run setup script
setup.bat

# 3. Start Ollama (in a separate terminal)
ollama serve

# 4. Pull the AI model
ollama pull llama3.1:8b

# 5. Run the application
run.bat
```

#### Linux/Mac
```bash
# 1. Clone the repository
git clone <your-repo-url>
cd IDS517-InterviewApp

# 2. Make scripts executable
chmod +x setup.sh run.sh

# 3. Run setup script
./setup.sh

# 4. Start Ollama (in a separate terminal)
ollama serve

# 5. Pull the AI model
ollama pull llama3.1:8b

# 6. Run the application
./run.sh
```

## 📦 Detailed Installation Guide

### 1. Install Java 17+
```bash
# Windows (using Chocolatey)
choco install openjdk17

# Mac (using Homebrew)
brew install openjdk@17

# Linux (Ubuntu/Debian)
sudo apt install openjdk-17-jdk

# Verify installation
java -version
```

### 2. Install Maven
```bash
# Windows (using Chocolatey)
choco install maven

# Mac (using Homebrew)
brew install maven

# Linux (Ubuntu/Debian)
sudo apt install maven

# Verify installation
mvn -version
```

### 3. Install Ollama

#### Windows
1. Download from [https://ollama.ai/download](https://ollama.ai/download)
2. Run the installer
3. Open command prompt and run:
   ```batch
   ollama serve
   ```
4. In a new terminal:
   ```batch
   ollama pull llama3.1:8b
   ```

#### Mac
```bash
brew install ollama
ollama serve &
ollama pull llama3.1:8b
```

#### Linux
```bash
curl https://ollama.ai/install.sh | sh
ollama serve &
ollama pull llama3.1:8b
```

### 4. Install Whisper (Speech-to-Text)
```bash
# Install with pip
pip install -U openai-whisper

# Or with pip3 on Linux/Mac
pip3 install -U openai-whisper

# Verify installation
whisper --help
```

### 5. Install Piper TTS (Text-to-Speech)

#### Windows
1. Download from [https://github.com/rhasspy/piper/releases](https://github.com/rhasspy/piper/releases)
2. Extract to `C:\Program Files\Piper`
3. Add to PATH:
   - Open System Properties → Environment Variables
   - Edit PATH and add `C:\Program Files\Piper`
4. Download voice model:
   ```batch
   curl -L -o en_US-lessac-medium.onnx https://huggingface.co/rhasspy/piper-voices/resolve/main/en/en_US/lessac/medium/en_US-lessac-medium.onnx
   ```

#### Linux/Mac
```bash
# Download Piper
wget https://github.com/rhasspy/piper/releases/download/v1.2.0/piper_amd64.tar.gz
tar -xzf piper_amd64.tar.gz
sudo mv piper /usr/local/bin/

# Download voice model
mkdir -p ~/.local/share/piper/voices
cd ~/.local/share/piper/voices
wget https://huggingface.co/rhasspy/piper-voices/resolve/main/en/en_US/lessac/medium/en_US-lessac-medium.onnx
```

### 6. Build the Application
```bash
cd IDS517-InterviewApp
mvn clean package
```

## 📖 Usage Guide

### First-Time Setup
1. **Launch the application**
   ```bash
   # Windows
   run.bat
   
   # Linux/Mac
   ./run.sh
   ```

2. **Upload your resume**
   - Go to "📄 Resume" tab
   - Click "Choose File"
   - Select PDF, DOCX, or TXT format

3. **Add job description**
   - Go to "📋 Job Description" tab
   - Paste or upload the job description

4. **Get AI analysis**
   - Go to "✅ Review" tab
   - Click "🤖 Analyze with AI"
   - Review match score and feedback

5. **Select interview mode**
   - Go to "🎯 Mode" tab
   - Choose your preferred mode (Practice recommended for first time)

6. **Start practicing!**
   - Go to "🎤 Interview" tab
   - Click "▶ Start Interview"
   - Answer questions (type or speak)
   - Click "Submit Answer" when done
   - Review analytics afterward

### Keyboard Shortcuts
| Shortcut | Action |
|----------|--------|
| `Space` | Toggle Recording/Submit Answer |
| `Ctrl+N` | Next Question |
| `Ctrl+M` | Select Interview Mode |
| `Ctrl+A` | View Analytics |
| `Ctrl+Q` | Quick Practice Mode |
| `F1` | Show Help |

## 🎨 Customization

### Configuration File
Edit `src/main/resources/application.properties`:

```properties
# AI Service (ollama or groq)
ai.service=ollama
ai.ollama.url=http://localhost:11434
ai.ollama.model=llama3.1:8b

# Speech Services
stt.enabled=true
stt.whisper.model=base
tts.enabled=true
tts.piper.model=en_US-lessac-medium

# Video Settings
video.enabled=true
video.fps=30
video.width=640
video.height=480

# UI Theme (dark or light)
ui.theme=dark
ui.keyboard.shortcuts=true

# Analytics
analytics.track.filler.words=true
analytics.track.pace=true
```

### Available AI Models
```bash
# Faster, less accurate
ollama pull llama3.1:7b

# Balanced (recommended)
ollama pull llama3.1:8b

# Better quality, slower
ollama pull llama3.1:13b

# For coding interviews
ollama pull codellama:13b
```

### Available Whisper Models
| Model | Size | Speed | Accuracy |
|-------|------|-------|----------|
| tiny | 39 MB | ⚡⚡⚡⚡⚡ | ⭐⭐ |
| base | 74 MB | ⚡⚡⚡⚡ | ⭐⭐⭐ |
| small | 244 MB | ⚡⚡⚡ | ⭐⭐⭐⭐ |
| medium | 769 MB | ⚡⚡ | ⭐⭐⭐⭐⭐ |
| large | 1550 MB | ⚡ | ⭐⭐⭐⭐⭐ |

## 📂 Project Structure

```
IDS517-InterviewApp/
├── src/main/java/com/interviewprep/
│   ├── Main.java                       # Application entry point
│   ├── model/                          # Data models
│   │   ├── InterviewMode.java
│   │   ├── InterviewSession.java
│   │   ├── InterviewAnalytics.java
│   │   └── ...
│   ├── service/                        # Business logic
│   │   ├── OllamaService.java         # AI integration
│   │   ├── WhisperService.java        # Speech-to-text
│   │   ├── PiperTTSService.java       # Text-to-speech
│   │   ├── VideoRecordingService.java
│   │   ├── InterviewService.java
│   │   └── StorageService.java        # File-based storage
│   ├── ui/                             # User interface
│   │   ├── MainFrame.java
│   │   ├── ResumeUploadPanel.java
│   │   ├── InterviewPanel.java
│   │   ├── AnalyticsDashboardPanel.java
│   │   └── ...
│   └── util/                           # Utilities
├── src/main/resources/
│   └── application.properties          # Configuration
├── recordings/                         # Interview recordings
├── data/                               # Session data (JSON)
├── pom.xml                             # Maven configuration
├── setup.bat / setup.sh                # Setup scripts
├── run.bat / run.sh                    # Run scripts
└── README.md                           # This file
```

## 🔧 Troubleshooting

### Ollama Not Running
```bash
# Start Ollama server
ollama serve

# Check if running
curl http://localhost:11434/api/tags
```

### Webcam Not Detected
- Check permissions in system settings
- Ensure no other app is using the webcam
- Try changing camera index in code (default is 0)

### Whisper Not Found
```bash
# Reinstall Whisper
pip install --upgrade --force-reinstall openai-whisper

# Add to PATH if needed
export PATH=$PATH:~/.local/bin
```

### Out of Memory
```bash
# Increase heap size
java -Xmx4g -jar target/ai-interview-prep-1.0.0.jar
```

### No Audio Recording
- Check microphone permissions
- Test microphone in system settings
- Ensure Java has audio permissions

## 💰 Cost Comparison

### This Tool (100% Free)
- Ollama (local): **$0**
- Whisper (local): **$0**
- Piper TTS (local): **$0**
- **Total: $0** ✅

### Cloud Alternatives
- AWS Bedrock: ~$0.14/session
- AWS Transcribe: ~$0.36/session
- AWS Polly: ~$0.002/session
- **Total: ~$0.50/session** ❌

## 🎓 Best Practices

### For Best Results:
1. **Practice regularly** - Use daily for 15-20 minutes
2. **Start with Practice Mode** - Build confidence before timed modes
3. **Review recordings** - Watch yourself to improve body language
4. **Track progress** - Use analytics to identify weak areas
5. **Update resume** - Incorporate AI suggestions
6. **Vary modes** - Try different modes to prepare for anything

### Interview Tips:
- **STAR Method** (Situation, Task, Action, Result) for behavioral questions
- **Think aloud** for technical questions
- **Ask clarifying questions** before answering
- **Maintain eye contact** with the camera
- **Speak clearly** and at a moderate pace
- **Use specific examples** from your experience

## 🚀 Advanced Features

### MCP (Model Context Protocol)
The app learns from your performance and adapts:
- **Difficulty adjustment** - Questions get harder as you improve
- **Personalized focus** - More questions on weak areas
- **Progress tracking** - Historical performance trends

### Analytics Metrics
- Overall performance score (0-10)
- Category scores (Technical, Behavioral, Communication, Confidence)
- Filler word count and rate
- Speaking pace (words per minute)
- Answer length and structure
- Improvement over time

## 📜 License

MIT License - feel free to use, modify, and distribute.

## 🤝 Contributing

Contributions welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## 📞 Support

- **Issues**: Open a GitHub issue
- **Discussions**: Use GitHub Discussions
- **Documentation**: Check the Wiki

## 🎉 Acknowledgments

Built with these amazing **free** tools:
- [Ollama](https://ollama.ai/) - Local LLM inference
- [OpenAI Whisper](https://github.com/openai/whisper) - Speech recognition
- [Piper TTS](https://github.com/rhasspy/piper) - Text-to-speech
- [JavaCV](https://github.com/bytedeco/javacv) - Video processing
- [Apache Tika](https://tika.apache.org/) - Document parsing
- [FlatLaf](https://www.formdev.com/flatlaf/) - Modern UI

## 📈 Roadmap

- [ ] Export analytics to PDF
- [ ] Facial expression analysis
- [ ] Mobile companion app
- [ ] Collaborative practice sessions
- [ ] Industry-specific question banks
- [ ] Integration with LinkedIn
- [ ] Cloud sync (optional)

---

**Made with ❤️ for students preparing for their dream jobs!**

**Star ⭐ this repo if it helps you ace your interview!**

