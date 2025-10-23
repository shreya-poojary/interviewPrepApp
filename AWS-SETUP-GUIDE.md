# 🔐 AWS Bedrock Credentials Setup Guide

## Quick Setup Options

### **Option 1: Environment Variables (Recommended)**

#### Windows PowerShell:
```powershell
$env:AWS_ACCESS_KEY_ID="your-access-key-id"
$env:AWS_SECRET_ACCESS_KEY="your-secret-access-key"
$env:AWS_DEFAULT_REGION="us-east-1"
```

#### Windows Command Prompt:
```cmd
set AWS_ACCESS_KEY_ID=your-access-key-id
set AWS_SECRET_ACCESS_KEY=your-secret-access-key
set AWS_DEFAULT_REGION=us-east-1
```

#### Linux/Mac:
```bash
export AWS_ACCESS_KEY_ID="your-access-key-id"
export AWS_SECRET_ACCESS_KEY="your-secret-access-key"
export AWS_DEFAULT_REGION="us-east-1"
```

### **Option 2: AWS Credentials File**

Create `~/.aws/credentials` (Windows: `C:\Users\YourUsername\.aws\credentials`):

```ini
[default]
aws_access_key_id = your-access-key-id
aws_secret_access_key = your-secret-access-key
region = us-east-1
```

### **Option 3: Application Properties (For Testing)**

Edit `src/main/resources/application.properties`:

```properties
# Uncomment and fill in your credentials
ai.bedrock.access.key.id=your-access-key-id
ai.bedrock.secret.access.key=your-secret-access-key
```

## 🔑 Getting AWS Credentials

1. **Go to AWS Console** → IAM → Users → Your User
2. **Security credentials** tab
3. **Create access key** → Command Line Interface (CLI)
4. **Download the CSV** with your credentials

## 🌍 Supported Regions

- `us-east-1` (N. Virginia) - **Recommended**
- `us-west-2` (Oregon)
- `eu-west-1` (Ireland)
- `ap-southeast-1` (Singapore)

## 🤖 Supported Models

### Claude Models:
- `anthropic.claude-3-5-sonnet-20241022-v2:0` (Latest)
- `anthropic.claude-3-haiku-20240307-v1:0` (Fast)
- `anthropic.claude-3-opus-20240229-v1:0` (Most capable)

### Amazon Titan Models:
- `amazon.titan-text-express-v1`
- `amazon.titan-text-lite-v1`

## ✅ Testing Your Setup

1. **Run the application**
2. **Go to "🤖 AI Service" tab**
3. **Select "Bedrock" from dropdown**
4. **Click "🧪 Test Connection"**
5. **Should show "✅ Bedrock: Available"**

## 🚨 Troubleshooting

### Common Issues:

1. **"Credentials not found"**
   - Check environment variables are set
   - Verify AWS credentials file exists
   - Ensure credentials are valid

2. **"Access denied"**
   - Check IAM permissions for Bedrock
   - Ensure user has `bedrock:InvokeModel` permission

3. **"Region not supported"**
   - Change region to `us-east-1`
   - Update `ai.bedrock.region` in properties

### Required IAM Permissions:
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "bedrock:InvokeModel",
                "bedrock:InvokeModelWithResponseStream"
            ],
            "Resource": "arn:aws:bedrock:*::foundation-model/*"
        }
    ]
}
```

## 🎯 Next Steps

Once credentials are configured:
1. **Test the connection** in the AI Service tab
2. **Switch to Bedrock** for cloud-based AI
3. **Enjoy faster, more capable AI responses!**

---

**Need Help?** Check the application logs for detailed error messages.
