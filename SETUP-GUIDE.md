# 🚀 Complete Local Setup Guide

## Prerequisites Check

Run these commands to check what you have:
```powershell
java -version        # Should be 17 or higher ✓
mvn -version         # Should show Maven 3.8+ (install if missing)
```

---

## Step-by-Step Installation

### **Step 1: Install Maven** (Skip if already installed)

**Quick Install with Chocolatey:**
```powershell
# Run PowerShell as Administrator
choco install maven -y
```

**Manual Install:**
1. Download: https://maven.apache.org/download.cgi (apache-maven-3.9.9-bin.zip)
2. Extract to: `C:\Program Files\Apache\maven`
3. Add to PATH: `C:\Program Files\Apache\maven\bin`
4. Restart terminal and verify: `mvn -version`

---

### **Step 2: Install Ollama**

1. **Download Ollama for Windows:**
   - Visit: https://ollama.ai/download
   - Download and run `OllamaSetup.exe`
   - Follow the installation wizard

2. **Verify Ollama is installed:**
   ```powershell
   ollama --version
   ```

3. **Start Ollama (in a separate terminal):**
   ```powershell
   ollama serve
   ```
   Leave this terminal open - Ollama needs to run in the background!

4. **Download the AI model (in a new terminal):**
   ```powershell
   ollama pull llama3.1:8b
   ```
   This will download ~4.7GB - may take 5-10 minutes depending on your internet.

---

### **Step 3: Build the Project**

1. **Navigate to project directory:**
   ```powershell
   cd C:\Users\shrey\OneDrive\Desktop\IDS517-InterviewApp
   ```

2. **Clean and build:**
   ```powershell
   mvn clean package
   ```
   This will:
   - Download all dependencies (first time only)
   - Compile the code
   - Run tests
   - Create executable JAR file
   - Takes 2-5 minutes on first run

3. **Check for the JAR file:**
   ```powershell
   dir target\*.jar
   ```
   You should see: `ai-interview-prep-1.0.0.jar`

---

### **Step 4: Run the Application**

**Method 1 - Using the run script:**
```powershell
.\run.bat
```

**Method 2 - Direct Java command:**
```powershell
java -jar target\ai-interview-prep-1.0.0.jar
```

**Method 3 - With more memory (if needed):**
```powershell
java -Xmx4g -jar target\ai-interview-prep-1.0.0.jar
```

---

## 🎯 First Time Usage

When the application starts:

1. **Upload Resume Tab:**
   - Click "Choose File"
   - Select your resume (PDF, DOCX, or TXT)
   - Wait for preview to load

2. **Job Description Tab:**
   - Paste or upload the job description
   - Click "Save"

3. **Review Tab:**
   - Click "🤖 Analyze with AI"
   - Wait 10-30 seconds for AI analysis
   - Review your match score and feedback

4. **Mode Tab:**
   - Select an interview mode (try "Practice Mode" first)
   - Click "Start Interview"

5. **Interview Tab:**
   - Click "▶ Start Interview"
   - Answer questions (type in the text area)
   - Click "Submit Answer" after each question
   - Click "Next Question" to continue
   - Click "■ Stop Interview" when done

6. **Analytics Tab:**
   - View your past interview sessions
   - See performance metrics and scores

---

## 🔧 Troubleshooting

### Ollama Connection Error
```
Error: Cannot connect to Ollama
```
**Solution:** Make sure Ollama is running:
```powershell
# In a separate terminal
ollama serve
```

### Maven Build Error
```
Error: JAVA_HOME not set
```
**Solution:** Set JAVA_HOME:
```powershell
# Find Java installation
where java

# Set JAVA_HOME (adjust path if needed)
setx JAVA_HOME "C:\Program Files\Java\jdk-23"
```

### Out of Memory Error
```
Error: Java heap space
```
**Solution:** Run with more memory:
```powershell
java -Xmx4g -jar target\ai-interview-prep-1.0.0.jar
```

### Webcam Not Working
- Check if another app is using the webcam
- Grant camera permissions in Windows Settings
- Restart the application

### Port Already in Use (Ollama)
```
Error: Port 11434 already in use
```
**Solution:** Kill existing Ollama process:
```powershell
taskkill /F /IM ollama.exe
ollama serve
```

---

## ⌨️ Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Space` | Submit Answer |
| `Ctrl+N` | Next Question |
| `Ctrl+M` | Select Mode |
| `Ctrl+A` | View Analytics |
| `Ctrl+Q` | Quick Practice |
| `F1` | Show Help |

---

## 📁 Project Structure

After setup, you'll have:
```
IDS517-InterviewApp/
├── target/
│   └── ai-interview-prep-1.0.0.jar    ← Executable file
├── data/                               ← Session data (JSON files)
├── recordings/                         ← Interview recordings
├── logs/                               ← Application logs
├── run.bat                             ← Quick start script
└── README.md                           ← Full documentation
```

---

## 🔄 Daily Usage

**Every time you want to use the app:**

1. Start Ollama (if not running):
   ```powershell
   ollama serve
   ```

2. Run the application:
   ```powershell
   cd C:\Users\shrey\OneDrive\Desktop\IDS517-InterviewApp
   .\run.bat
   ```

That's it! 🎉

---

## 💡 Tips

1. **Keep Ollama running** in a separate terminal while using the app
2. **First analysis takes longer** (30-60 seconds) as Ollama loads the model
3. **Subsequent analyses are faster** (10-20 seconds)
4. **Save your work regularly** - sessions auto-save
5. **Review recordings** to improve your interview skills
6. **Track progress** in the Analytics dashboard

---

## 🆘 Need Help?

- **Check logs:** `logs/interview-prep.log`
- **Verify Ollama:** http://localhost:11434 in browser
- **GitHub Issues:** https://github.com/shreya-poojary/interviewPrepApp/issues
- **Documentation:** README.md in project root

---

## 🎓 Best Practices

1. **Practice regularly** - 15-20 minutes daily
2. **Start with Practice Mode** - Build confidence
3. **Review your recordings** - Learn from yourself
4. **Use AI feedback** - Implement suggestions
5. **Try different modes** - Prepare for various scenarios
6. **Track analytics** - Monitor your improvement

---

**Happy Interviewing! 🚀**

