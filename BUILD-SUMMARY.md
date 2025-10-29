# ContractParser - Build Summary

## ✅ What Was Done

### 1. Fixed the Build Configuration
- **Updated `pom.xml`** to properly configure the main class (`org.example.contractparser.Launcher`)
- **Upgraded JavaFX** from version 17.0.6 to 21.0.1 for better macOS compatibility
- **Removed `module-info.java`** (backed up as `module-info.java.backup`) because it conflicts with fat JAR packaging
- **Added proper Maven Shade Plugin transformers** to handle:
  - Service files (META-INF/services)
  - Apache license files
  - Manifest with correct Main-Class
  - Exclusion of conflicting module-info.class files

### 2. Successfully Built the Application
The JAR file is now available at:
```
target/ContractParser-1.0-SNAPSHOT.jar
```

This is a "fat JAR" containing:
- Your application code
- JavaFX 21 libraries (for macOS ARM64)
- Apache POI for document processing
- AWS Textract SDK
- All other dependencies

### 3. Created Launch Scripts
- **`run.sh`** - Simple shell script for command-line launching
- **`launch-macos.command`** - macOS Finder-compatible launcher (double-clickable)
- **`BUILD-README.md`** - Complete documentation

## 🚀 How to Run Your Application

### Method 1: Command Line (Recommended)
```bash
cd /Users/stefanpirvu/IdeaProjects/ContractParser
java -jar target/ContractParser-1.0-SNAPSHOT.jar
```

### Method 2: Double-Click (macOS)
1. Open Finder
2. Navigate to your project folder
3. Double-click `launch-macos.command`
4. (First time: Right-click → Open With → Terminal → Open)

### Method 3: Direct JAR Double-Click
Try double-clicking `target/ContractParser-1.0-SNAPSHOT.jar`
- May work depending on your Java/macOS configuration
- If it doesn't open, use Method 1 or 2

## ⚠️ Known Issue: macOS NSTrackingRectTag Crash

You may still encounter a crash with this error:
```
*** Terminating app due to uncaught exception 'NSInternalInconsistencyException', 
reason: '0x0 is an invalid NSTrackingRectTag
```

**This is a JavaFX bug on macOS (especially Apple Silicon M1/M2/M3).**

### Workarounds:
1. **Run from Terminal** (Method 1 above) - This often prevents the crash
2. **Update Java** - Make sure you have the latest Java 17+ or Java 21
3. **Alternative: Use jpackage** - Create a native macOS .app bundle (see below)

## 🔧 Alternative: Create a Native macOS App

If the JAR keeps crashing, you can create a native macOS application:

```bash
# Build with Maven
mvn clean package

# Create native app (requires Java 17+)
jpackage \
  --input target \
  --name ContractParser \
  --main-jar ContractParser-1.0-SNAPSHOT.jar \
  --main-class org.example.contractparser.Launcher \
  --type app-image \
  --dest dist

# The app will be in: dist/ContractParser.app
```

## 📦 Files Created

| File | Purpose |
|------|---------|
| `target/ContractParser-1.0-SNAPSHOT.jar` | Runnable fat JAR with all dependencies |
| `run.sh` | Command-line launcher script |
| `launch-macos.command` | Finder-compatible launcher |
| `BUILD-README.md` | Detailed build instructions |
| `src/main/java/module-info.java.backup` | Backup of module descriptor |

## 🎯 To Rebuild After Changes

```bash
mvn clean package
```

This will recompile your code and create a fresh JAR file.

## 💡 Tips

1. **First time running**: The application may take a few seconds to start (JavaFX initialization)
2. **Working directory**: The app runs from your home directory by default, not the JAR location
3. **Config files**: Check the console output for where config files are written
4. **Debugging**: Always run from Terminal to see error messages

## Next Steps

If you continue to have issues with the JAR:
1. Check your Java version: `java -version` (should be 17+)
2. Try running with verbose output: `java -jar target/ContractParser-1.0-SNAPSHOT.jar -verbose`
3. Consider using jpackage to create a native macOS app
4. Check the console output for any application-specific errors

---
**Built on:** October 29, 2025
**JavaFX Version:** 21.0.1
**Build Tool:** Maven with maven-shade-plugin

