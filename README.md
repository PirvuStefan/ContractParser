# ContractParser

A JavaFX desktop application that automates the generation of employment contracts and employee information sheets (fisa) by extracting text from Romanian ID cards using AWS Textract OCR technology.

## ✨ Features

- **ID Card OCR Recognition**: Automatically extracts information from Romanian ID cards (JPG/PNG) using AWS Textract
- **Contract Generation**: Creates personalized employment contracts from templates
- **Employee Information Sheets**: Generates employee information sheets (fisa) with extracted data
- **Interactive Review Interface**: Allows manual verification and editing of extracted data before contract generation
- **Form Validation**: Ensures all required fields are filled before document generation
- **Document Archive**: Saves all generated documents in an organized `arhiva` folder
- **Modern UI**: Glass-morphism styled JavaFX interface with intuitive navigation

## 🔧 Prerequisites

- **Java Development Kit (JDK)**: Version 17 or higher
- **Apache Maven**: Version 3.6 or higher
- **AWS Account**: With Textract API access
- **AWS Credentials**: Access Key ID and Secret Access Key with Textract permissions

## 🛠 Technologies Used

### Core Technologies
- **Java 17**: Programming language
- **JavaFX 21.0.1**: Desktop UI framework
- **Apache Maven**: Build automation and dependency management

### Libraries & Dependencies

#### UI Components
- **JavaFX Controls & FXML** (21.0.1): Core UI components
- **ControlsFX** (11.2.1): Enhanced JavaFX controls
- **FormsFX** (11.6.0): Form handling
- **Ikonli** (12.3.1): Icon library for JavaFX
- **BootstrapFX** (0.4.0): Bootstrap-inspired styling

#### Document Processing
- **Apache POI** (5.2.5): Microsoft Word document manipulation
  - `poi-ooxml`: OOXML format support for .docx files
- **Apache PDFBox** (2.0.29): PDF document handling

#### AWS Integration
- **AWS SDK for Java - Textract** (2.21.0): OCR text extraction from ID cards

#### Utilities
- **Jackson Databind** (2.15.2): JSON processing
- **Dotenv Java** (3.0.0): Environment variable management
- **SLF4J Simple** (2.0.13): Logging framework

#### Testing
- **JUnit Jupiter** (5.10.2): Unit testing framework

### Build Plugins
- **Maven Compiler Plugin** (3.13.0): Java compilation
- **Maven Shade Plugin** (3.5.1): Creates uber JAR with all dependencies
- **JavaFX Maven Plugin**: JavaFX application support

## 📁 Project Structure

```
ContractParser/
├── src/
│   └── main/
│       ├── java/
│       │   └── org/example/contractparser/
│       │       ├── HelloApplication.java    # Main JavaFX application
│       │       ├── Launcher.java            # Application entry point
│       │       ├── Contract.java            # Contract generation logic
│       │       ├── DetectText.java          # AWS Textract integration
│       │       ├── ConfigToJarDir.java      # Configuration file handler
│       │       └── domain/model/
│       │           └── IdentificationDocument.java
│       └── resources/
│           ├── contract.docx                # Employment contract template
│           ├── fisa.docx                    # Employee info sheet template
│           ├── META-INF/MANIFEST.MF
│           └── org/example/contractparser/
│               └── hello-view.fxml
├── arhiva/                                   # Generated contracts storage
├── config.yml                                # Salary configuration
├── .env                                      # AWS credentials (not committed)
├── pom.xml                                   # Maven configuration
└── README.md

```

## 🚀 Installation

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/ContractParser.git
cd ContractParser
```

### 2. Install Dependencies
```bash
mvn clean install
```

## ⚙️ Configuration

### 1. Create `.env` File
Create a `.env` file in the project root directory with your AWS credentials:

```env
AWS_ACCESS_KEY_ID=your_access_key_id
AWS_SECRET_ACCESS_KEY=your_secret_access_key
AWS_REGION=us-east-1
```

**Important**: Never commit the `.env` file to version control. Add it to `.gitignore`.

### 2. Configure Salary Settings
Edit `config.yml` in the project root:

```yaml
salary: 4800
```

This default salary value will be used in generated contracts.

### 3. Template Files
Ensure the following template files exist in `src/main/resources/`:
- `contract.docx` - Employment contract template
- `fisa.docx` - Employee information sheet template

#### Template Placeholders
Use the following special characters as placeholders in your Word templates:

| Placeholder | Field | Description |
|-------------|-------|-------------|
| `ɛ` | Name | Full name from ID |
| `ɜ` | Series | ID card series |
| `ɝ` | Number | ID card number |
| `ɞ` | CNP | Personal identification number |
| `ɟ` | Issued By | Issuing authority |
| `ɠ` | Address | Residential address |
| `ɣ` | Validity | ID expiration date |
| `ɔ` | Registration Number | Company registration number |
| `ɖ` | Today's Date | Contract creation date |
| `ɐ` | Hire Date | Employment start date (today + 1 day) |
| `ɕ` | Phone | Phone number (formatted) |
| `ɘ` | Location | Work location |
| `ə` | City | City name |
| `ɥ` | Salary | Monthly salary |

## 🏗 Building the Application

### Build JAR File
```bash
mvn clean package
```

This creates an uber JAR with all dependencies at:
```
target/ContractParser-1.0-SNAPSHOT.jar
```

### Build for macOS (Optional)
If you're on macOS, you can create a native app bundle:
```bash
./create-macos-app.sh
```

## ▶️ Running the Application

### From JAR
```bash
java -jar target/ContractParser-1.0-SNAPSHOT.jar
```

### From Maven
```bash
mvn javafx:run
```

### From IDE
Run the `Launcher` class as the main class.

### macOS Command (if app bundle created)
```bash
./launch-macos.command
```

## 📖 Usage

### Step 1: Upload ID Card Image
1. Click "Choose Image" button
2. Select a JPG or PNG image of a Romanian ID card
3. The application will display a thumbnail of the selected image

### Step 2: Enter Additional Information
Fill in the following required fields:
- **Numar de inregistrare** (Registration Number): Company registration number
- **Telefon** (Phone): Contact phone number
- **Locatie** (Location): Work location/address
- **Oras** (City): City name

### Step 3: Review Extracted Data
1. Click "Submit" button
2. The application will use AWS Textract to extract data from the ID card
3. Review the automatically filled fields:
   - Nume (Name)
   - Seria (Series)
   - Numarul (Number)
   - CNP
   - Emis de (Issued By)
   - Adresa (Address)
   - Data de valabilitate (Validity Date)

### Step 4: Generate Contracts
1. Edit any incorrectly extracted fields if necessary
2. Click "Creare Contract" (Create Contract)
3. Two documents will be generated in the `arhiva` folder:
   - `{Name}.docx` - Employment contract
   - `{Name}_fisa.docx` - Employee information sheet

### Step 5: Access Generated Documents
Generated documents are saved in the `arhiva` folder with the employee's name.

## 🔐 AWS Textract Setup

### 1. Create AWS Account
If you don't have one, sign up at [aws.amazon.com](https://aws.amazon.com)

### 2. Create IAM User
1. Go to AWS IAM Console
2. Create a new user with programmatic access
3. Attach the `AmazonTextractFullAccess` policy (or create a custom policy with minimal permissions)

### 3. Get Credentials
1. Save the **Access Key ID** and **Secret Access Key**
2. Add them to your `.env` file

## 🐛 Troubleshooting

### Issue: "Resource `/contract.docx` not found in JAR"
**Solution**: Ensure template files are in `src/main/resources/` before building, and rebuild:
```bash
mvn clean package
```

> **Note:** The application relies on the presence of `contract.docx` and `fisa.docx` in the resources directory. If these files are missing, the application will throw a "Resource not found" error when trying to access them.

### Issue: Submit button doesn't extract text from ID
**Solution**: 
1. Verify `.env` file exists with valid AWS credentials
2. Check AWS Textract service is enabled in your region
3. Verify network connectivity to AWS
4. Check IAM user has Textract permissions

### Issue: Application doesn't start
**Solution**:
1. Verify Java 17+ is installed: `java -version`
2. Rebuild the project: `mvn clean install`
3. Check for errors in terminal output

### Issue: Config file not found when running JAR
**Solution**: The application automatically creates `config.yml` in the same directory as the JAR file on first run. If issues persist, manually create it:
```yaml
salary: 4050
```

## 📝 Development Notes

### Key Classes

- **`HelloApplication`**: Main JavaFX application with UI components and event handlers
- **`Launcher`**: Entry point that launches the JavaFX application (required for JAR packaging)
- **`Contract`**: Handles Word document manipulation and placeholder replacement
- **`DetectText`**: AWS Textract integration for OCR text extraction
- **`ConfigToJarDir`**: Manages configuration file creation relative to JAR location

### Building Modifications

The project uses Maven Shade Plugin to create an uber JAR that includes all dependencies. The main class is set to `Launcher` which then launches the JavaFX application.


## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- AWS Textract for OCR capabilities
- Apache POI for Word document processing
- JavaFX community for UI components
