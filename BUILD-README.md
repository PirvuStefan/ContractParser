# ContractParser - Build and Run Instructions

## Building the Application

### Using Maven (includes dependencies)

```bash
mvn clean package
```

This will create a fat JAR file at: `target/ContractParser-1.0-SNAPSHOT.jar`

## Running the Application

### Option 1: Command Line

From the project directory, run:

```bash
java -jar target/ContractParser-1.0-SNAPSHOT.jar
```

### Option 2: macOS Double-Click Launcher

Double-click the `launch-macos.command` file in Finder. This will open a Terminal window and launch the application.

**First time setup:**
1. Right-click `launch-macos.command`
2. Select "Open With" → "Terminal"
3. Click "Open" if prompted about security

### Option 3: Direct JAR Double-Click

You can try double-clicking the JAR file directly:
`target/ContractParser-1.0-SNAPSHOT.jar`

However, this may not work on all systems due to JavaFX requirements.

## Building Without Maven

If you want to build without Maven, you can use `javac` and `jar` commands directly, but you'll need to manually manage all dependencies.

### Steps:

1. **Compile** your Java files:
   ```bash
   javac -d bin -cp "lib/*" src/main/java/org/example/contractparser/*.java
   ```

2. **Copy resources**:
   ```bash
   cp -r src/main/resources/* bin/
   ```

3. **Create JAR**:
   ```bash
   jar cvfm ContractParser.jar manifest.txt -C bin .
   ```

4. **Create manifest.txt** with:
   ```
   Main-Class: org.example.contractparser.Launcher

   ```
   (Note: The blank line at the end is required)

## Known Issues

### macOS NSTrackingRectTag Error

If you encounter a crash with `NSTrackingRectTag` error on macOS:

- This is a known issue with JavaFX on macOS (especially Apple Silicon)
- The application uses JavaFX 21 which has improvements for macOS
- Try running from Terminal instead of double-clicking
- Make sure you're using a recent version of Java (17 or later)

### "Unsupported JavaFX configuration" Warning

This warning appears when running from a fat JAR. It's generally harmless and the application should still work.

## Project Structure

- `src/main/java/` - Java source files
- `src/main/resources/` - Resource files (FXML, images, etc.)
- `target/` - Build output directory
- `pom.xml` - Maven configuration
- `run.sh` - Shell script launcher
- `launch-macos.command` - macOS Finder-compatible launcher

## Dependencies

The application includes all dependencies in the JAR file (fat JAR), including:
- JavaFX 21.0.1
- Apache POI (Excel/Word processing)
- AWS Textract SDK
- Jackson (JSON processing)
- And others...

## Java Version

Required: Java 17 or later

Check your Java version:
```bash
java -version
```

