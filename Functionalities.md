# ContractParser - Technical Functionalities Documentation

## Table of Contents
1. [Overview](#overview)
2. [Architecture & Design Patterns](#architecture--design-patterns)
3. [Core Components](#core-components)
   - [Launcher](#1-launcher)
   - [HelloApplication](#2-helloapplication)
   - [ContractService](#3-contractservice)
   - [DetectText](#4-detecttext)
   - [Contract](#5-contract)
   - [EnvLoader](#6-envloader)
   - [ConfigToJarDir](#7-configtojardir)
   - [IdentificationDocument](#8-identificationdocument)
   - [HelloController](#9-hellocontroller)
4. [Design Patterns Used](#design-patterns-used)
5. [Data Flow](#data-flow)

---

## Overview

ContractParser is a JavaFX-based desktop application that automates the generation of employment contracts by extracting data from Romanian ID cards (Buletin de identitate) using AWS Textract OCR service. The application processes ID card images, validates extracted information, and generates standardized Word documents (contracts and employee files).

**Key Technologies:**
- JavaFX for GUI
- AWS Textract SDK for OCR
- Apache POI for Word document manipulation
- Environment variable management for AWS credentials

---

## Architecture & Design Patterns

The application follows a **layered architecture** with clear separation of concerns:

```
Presentation Layer (UI) → Service Layer (Business Logic) → Data Access Layer (AWS API, File I/O)
```

### Key Architectural Principles:
1. **Single Responsibility Principle**: Each class has one clear purpose
2. **Separation of Concerns**: UI, business logic, and data access are separated
3. **Dependency Injection**: Services are injected where needed
4. **Immutability**: Domain models use final fields
5. **Resource Management**: Try-with-resources for proper cleanup

---

## Core Components

### 1. Launcher

**File**: `Launcher.java`

**Purpose**: Entry point for the JAR file that launches the JavaFX application.

**Functionality**: 
- Provides a workaround for JavaFX module system requirements
- Ensures proper initialization of the JavaFX runtime

**Code Example**:
```java
public class Launcher {
    public static void main(String[] args) {
        Application.launch(HelloApplication.class, args);
    }
}
```

**Design Pattern**: **Facade Pattern** - Simplifies the complex JavaFX launch process.

**Why It Exists**: When packaging as JAR, JavaFX requires the main class to NOT extend `Application`. This class serves as a non-JavaFX entry point.

---

### 2. HelloApplication

**File**: `HelloApplication.java`

**Purpose**: Main JavaFX application controller that manages the UI workflow and user interactions.

**Key Responsibilities**:
1. Initialize the GUI and manage scenes
2. Handle user input and file selection
3. Coordinate between UI and business logic
4. Manage navigation between forms

**Core Functionalities**:

#### A. Application Initialization
```java
@Override
public void start(Stage stage) {
    this.primaryStage = stage;
    
    // Initialize configuration
    ContractService.initializeConfig();
    
    stage.setTitle("ContractParser");
    // ... UI setup
}
```

#### B. Image Selection
```java
private void chooseImage(Stage stage) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select ID Image");
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png")
    );
    File selectedFile = fileChooser.showOpenDialog(stage);
    if (selectedFile != null) {
        Image image = new Image(selectedFile.toURI().toString(), 80, 80, true, true);
        imageView.setImage(image);
    }
}
```

**Pattern**: **Observer Pattern** - Event handlers observe button clicks and file selections.

#### C. Glass Morphism Button Styling
```java
private String glassButtonStyle() {
    return "-fx-background-color: rgba(255,255,255,0.25);"
            + "-fx-background-radius: 15;"
            + "-fx-border-radius: 15;"
            + "-fx-border-color: rgba(255,255,255,0.5);"
            + "-fx-border-width: 1;"
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0.5, 0, 2);"
            // ... additional styling
}

private String glassButtonPressedStyle() {
    return "-fx-background-color: rgba(0,0,0,0.2);" // Darker when pressed
            // ... similar styling with darker background
}
```

**UI/UX Design**: Implements modern glass morphism design with visual feedback on button press.

#### D. Form Validation and Data Extraction
```java
submitButton.setOnAction(e -> {
    // Validate initial form fields
    ContractService.ValidationResult validation = ContractService.validateInitialForm(
        regNumberField.getText(),
        phoneField.getText(),
        placeField.getText(),
        cityField.getText()
    );

    if (!validation.isValid()) {
        showErrorAlert(validation.getErrorTitle(), validation.getErrorMessage());
        return;
    }

    // Extract data from image if present
    Map<String, String> placeholders = new HashMap<>();
    if (imageView.getImage() != null) {
        try {
            String imagePath = imageView.getImage().getUrl().replaceFirst("^file:", "");
            placeholders = ContractService.extractDataFromImage(imagePath);
        } catch (IOException ex) {
            showErrorAlert("Extraction Error", 
                "Failed to extract data from ID card: " + ex.getMessage());
            return;
        }
    }
    
    showDetailedReviewPage(placeholders, regNumber, phone, place, city);
});
```

**Pattern**: **Template Method Pattern** - Defines the workflow: validate → extract → review → generate.

#### E. Detailed Review Page
```java
private void showDetailedReviewPage(Map<String, String> extractedData, String regNumber,
                                    String phone, String place, String city) {
    // Create text fields pre-populated with extracted data
    TextField nameField = new TextField(extractedData.getOrDefault("ɛ", ""));
    TextField seriesField = new TextField(extractedData.getOrDefault("ɜ", ""));
    TextField cnpField = new TextField(extractedData.getOrDefault("ɞ", ""));
    // ... many more fields
    
    // Create button with final validation and document generation
    createButton.setOnAction(e -> {
        ContractService.ValidationResult validation = 
            ContractService.validateDetailedReviewForm(/* all fields */);
        
        if (!validation.isValid()) {
            showErrorAlert(validation.getErrorTitle(), validation.getErrorMessage());
            return;
        }
        
        // Update extracted data with edited values
        extractedData.put("ɛ", nameField.getText());
        // ... update all fields
        
        // Build complete data map
        Map<String, String> completeData = ContractService.buildCompleteDataMap(
            extractedData, regNumber, phone, place, city
        );
        
        // Generate documents
        ContractService.generateDocuments(nameField.getText(), completeData);
    });
}
```

**Key Feature**: Two-stage form system - initial input → review and edit extracted data → final generation.

#### F. Fisa Registration Number Calculation
```java
private String getFisaRegistration(String registrationNumber) {
    if (registrationNumber == null || registrationNumber.isBlank()) {
        return "";
    }
    String[] parts = registrationNumber.split("/", 2);
    String numPart = parts[0].trim();
    try {
        long value = Long.parseLong(numPart);
        return Long.toString(value + 1); // Increment by 1 for employee file
    } catch (NumberFormatException e) {
        return numPart;
    }
}
```

**Business Logic**: Employee file registration number is contract number + 1.

**Design Patterns**:
- **MVC Pattern**: HelloApplication acts as Controller, managing Views and coordinating with Model (ContractService)
- **State Pattern**: Different UI states (initial form, review page, success)
- **Observer Pattern**: Event-driven button actions

---

### 3. ContractService

**File**: `ContractService.java`

**Purpose**: Service layer that encapsulates all business logic for contract generation.

**Key Responsibilities**:
1. Configuration management
2. Data validation
3. Data transformation
4. Document generation coordination
5. File system operations

**Core Functionalities**:

#### A. Configuration Initialization
```java
public static void initializeConfig() {
    try {
        ConfigToJarDir.main(new String[]{});
    } catch (Exception ex) {
        throw new RuntimeException("Failed to initialize configuration", ex);
    }
}
```

**Pattern**: **Facade Pattern** - Simplifies configuration setup.

#### B. Salary Configuration Management
```java
public static int getSalaryFromConfig() {
    Path configPath = Paths.get(CONFIG_FILE);

    // Create config file if it doesn't exist
    if (!Files.exists(configPath)) {
        try {
            Files.createFile(configPath);
            Files.write(configPath, List.of("salary: " + DEFAULT_SALARY));
            return DEFAULT_SALARY;
        } catch (IOException ex) {
            return DEFAULT_SALARY;
        }
    }

    // Read salary from config file
    try {
        List<String> lines = Files.readAllLines(configPath);
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("salary:")) {
                String value = trimmed.substring("salary:".length()).trim();
                value = value.replaceAll("^['\"]|['\"]$", ""); // Remove quotes
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return DEFAULT_SALARY;
                }
            }
        }
    } catch (IOException ex) {
        return DEFAULT_SALARY;
    }

    return DEFAULT_SALARY;
}
```

**Pattern**: **Factory Pattern** with fallback defaults - Always returns a valid salary value.

#### C. Phone Number Formatting
```java
public static String formatPhoneNumber(String phone) {
    if (phone == null) return "";
    String cleaned = phone.replaceAll("\\s+", "");
    return cleaned.replaceAll("(\\d{4})(\\d{3})(\\d{3})", "$1 $2 $3");
}
```

**Regex Logic**: Groups phone number into format: XXXX XXX XXX

#### D. Data Extraction Wrapper
```java
public static Map<String, String> extractDataFromImage(String imagePath) throws IOException {
    if (imagePath == null || imagePath.isEmpty()) {
        return new HashMap<>();
    }
    return new DetectText().extractMap(imagePath);
}
```

**Pattern**: **Adapter Pattern** - Adapts DetectText API to service layer.

#### E. Complete Data Map Builder
```java
public static Map<String, String> buildCompleteDataMap(
        Map<String, String> extractedData,
        String regNumber,
        String phone,
        String place,
        String city) {

    int salary = getSalaryFromConfig();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    String today = LocalDate.now().format(formatter);
    String hireDay = LocalDate.now().plusDays(1).format(formatter);
    String formattedPhone = formatPhoneNumber(phone);

    // Add additional data to extracted data
    extractedData.put("ɔ", regNumber);      // Registration Number
    extractedData.put("ɖ", today);          // Today's Date
    extractedData.put("ɐ", hireDay);        // Hire Date
    extractedData.put("ɕ", formattedPhone); // Phone
    extractedData.put("ɘ", place);          // Location
    extractedData.put("ə", city);           // City
    extractedData.put("ɥ", String.valueOf(salary)); // Salary

    return extractedData;
}
```

**Key Insight**: Uses special Unicode characters as placeholders in Word templates.

#### F. Document Generation
```java
public static void generateDocuments(String employeeName, Map<String, String> data) throws IOException {
    ensureArhivaDirectory();

    String sanitizedName = employeeName.replace(" ", "_");
    File arhivaDir = new File(ARHIVA_DIR);
    File contractFile = new File(arhivaDir, sanitizedName + ".docx");
    File fisaFile = new File(arhivaDir, sanitizedName + "_fisa.docx");

    // Create output files
    if (!contractFile.exists()) contractFile.createNewFile();
    if (!fisaFile.exists()) fisaFile.createNewFile();

    // Load templates from resources
    try (InputStream contractTemplate = ContractService.class.getResourceAsStream("/contract.docx");
         InputStream fisaTemplate = ContractService.class.getResourceAsStream("/fisa.docx")) {

        if (contractTemplate == null) {
            throw new IOException("Resource `/contract.docx` not found in JAR");
        }
        if (fisaTemplate == null) {
            throw new IOException("Resource `/fisa.docx` not found in JAR");
        }

        // Create temporary template files
        File tmpContract = File.createTempFile("contract_template", ".docx");
        tmpContract.deleteOnExit();
        Files.copy(contractTemplate, tmpContract.toPath(), StandardCopyOption.REPLACE_EXISTING);

        File tmpFisa = File.createTempFile("fisa_template", ".docx");
        tmpFisa.deleteOnExit();
        Files.copy(fisaTemplate, tmpFisa.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // Generate documents
        Contract.generateContract(tmpContract.getAbsolutePath(), contractFile.getAbsolutePath(), data);
        Contract.generateContract(tmpFisa.getAbsolutePath(), fisaFile.getAbsolutePath(), data);
    }
}
```

**Pattern**: **Template Method Pattern** - Loads templates from JAR resources, creates temp files, generates docs.

**Resource Management**: Uses try-with-resources for proper stream cleanup.

#### G. ValidationResult Inner Class
```java
public static class ValidationResult {
    private final boolean valid;
    private final String errorTitle;
    private final String errorMessage;

    private ValidationResult(boolean valid, String errorTitle, String errorMessage) {
        this.valid = valid;
        this.errorTitle = errorTitle;
        this.errorMessage = errorMessage;
    }

    public static ValidationResult success() {
        return new ValidationResult(true, null, null);
    }

    public static ValidationResult error(String title, String message) {
        return new ValidationResult(false, title, message);
    }

    public boolean isValid() { return valid; }
    public String getErrorTitle() { return errorTitle; }
    public String getErrorMessage() { return errorMessage; }
}
```

**Pattern**: **Builder Pattern / Factory Pattern** - Static factory methods for creating validation results.

**Design**: Immutable result object that encapsulates validation state and error messages.

#### H. Form Validation Methods
```java
public static ValidationResult validateInitialForm(String regNumber, String phone, 
                                                   String place, String city) {
    if (!isFieldValid(regNumber)) {
        return ValidationResult.error("Numarul de inregistrare lipsa",
            "Te rog introdu numarul de inregistrare.");
    }
    if (!isFieldValid(phone)) {
        return ValidationResult.error("Numarul de telefon lipsa",
            "Te rog introdu numarul de telefon.");
    }
    // ... more validations
    return ValidationResult.success();
}

public static ValidationResult validateDetailedReviewForm(String name, String series, 
    String number, String cnp, String issuedBy, String address, String validity, 
    String birthCounty, String birthCountry, String birthDate, 
    String addressCounty, String addressCountry, String addressStreet, 
    String addressNumber, String addressBloc, String addressScara, 
    String addressEtaj, String addressApartment) {
    
    if (!isFieldValid(name)) {
        return ValidationResult.error("Numele lipsa", "Te rog introdu numele.");
    }
    // ... validate all 18 fields
    return ValidationResult.success();
}
```

**Pattern**: **Strategy Pattern** - Different validation strategies for different forms.

**Design Patterns**:
- **Service Layer Pattern**: Encapsulates business logic
- **Static Utility Methods**: Stateless operations
- **Result Object Pattern**: ValidationResult encapsulates operation results

---

### 4. DetectText

**File**: `DetectText.java`

**Purpose**: Handles OCR (Optical Character Recognition) of Romanian ID cards using AWS Textract.

**Key Responsibilities**:
1. AWS Textract client initialization
2. Image text extraction
3. Romanian ID card parsing
4. Data structure mapping

**Core Functionalities**:

#### A. AWS Client Initialization
```java
public DetectText() {
    this.dotenv = Dotenv.load();
    String awsRegion = dotenv.get("AWS_REGION") != null ? 
                       dotenv.get("AWS_REGION") : "us-east-1";

    Map<String, String> env = EnvLoader.loadEnvFromJarDirectory(".env", true);

    String awsAccessKeyId = env.get("AWS_ACCESS_KEY_ID");
    String awsSecretAccessKey = env.get("AWS_SECRET_ACCESS_KEY");

    AwsBasicCredentials awsCredentials = 
        AwsBasicCredentials.create(awsAccessKeyId, awsSecretAccessKey);

    this.textractClient = TextractClient.builder()
            .region(Region.of(awsRegion))
            .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
            .build();
}
```

**Pattern**: **Dependency Injection** - Credentials are injected from environment.

**Security**: Credentials loaded from .env file, never hardcoded.

#### B. Text Lines Extraction
```java
public List<String> extractTextLines(String imagePath) throws IOException {
    File imageFile = new File(imagePath);
    List<String> lines = new ArrayList<>();

    try (FileInputStream imageStream = new FileInputStream(imageFile)) {
        SdkBytes sourceBytes = SdkBytes.fromInputStream(imageStream);

        Document document = Document.builder()
                .bytes(sourceBytes)
                .build();

        DetectDocumentTextRequest request = DetectDocumentTextRequest.builder()
                .document(document)
                .build();

        DetectDocumentTextResponse response = textractClient.detectDocumentText(request);

        for (Block block : response.blocks()) {
            if (block.blockType() == BlockType.LINE) {
                String lineText = block.text().trim();
                if (!lineText.isEmpty()) {
                    lines.add(lineText);
                }
            }
        }
    }

    return lines;
}
```

**AWS API**: Uses AWS Textract's `detectDocumentText` to extract text blocks.

**Pattern**: **Resource Management** - Try-with-resources ensures streams are closed.

#### C. Romanian ID Card Parsing
```java
public Map<String, String> extractMap(String imagePath) throws IOException {
    Map<String, String> textMap = new HashMap<>();
    List<String> textBlocks = this.extractTextLines(imagePath);
    
    for (int i = 0; i < textBlocks.size(); i++) {
        String word = textBlocks.get(i).trim();

        // Extract name from MRZ (Machine Readable Zone)
        if (word.contains("<<")) {
            textMap.put("ɛ", getName(word));
        }
        
        // Extract CNP (Personal Numerical Code)
        else if (word.contains("CNP")) {
            word = word.replaceAll("CNP", "").trim();
            textMap.put("ɞ", word);
            textMap.put("ȕ", getBirthDate(word)); // Extract birth date from CNP
        }
        
        // Extract series and number from MRZ
        else if (word.contains("<")) {
            textMap.put("ɜ", word.substring(0, 2));  // Series
            textMap.put("ɝ", word.substring(2, 8));  // Number
        }
        
        // Extract issuing authority
        else if (word.contains("SPCLEP")) {
            textMap.put("ɟ", word);
        }
        
        // Extract birth location
        else if (word.contains("Loc Nastere") || 
                 word.contains("Lieu de naissance") || 
                 word.contains("Place of birth")) {
            String place = textBlocks.get(i + 1).trim();
            textMap.put("Ȣ", getBirthLocation(place, "judet"));  // County
            textMap.put("Ȥ", getBirthLocation(place, "country")); // Country
        }
        
        // Extract address details
        else if (word.contains("Adresse") || 
                 word.contains("Adress") || 
                 word.contains("Domiciliu")) {
            String adress1 = textBlocks.get(i + 1).trim();
            String adress2 = textBlocks.get(i + 2).trim();
            
            textMap.put("Ƚ", getBirthLocation(adress1, "judet"));      // County
            textMap.put("ʦ", getBirthLocation(adress1, "localitate")); // City
            textMap.put("ɠ", adress1 + " " + adress2);                // Full address
            textMap.put("ʠ", getAdressDetails(adress2, "bloc"));      // Building
            textMap.put("ʡ", getAdressDetails(adress2, "numar"));     // Number
            textMap.put("ʢ", getAdressDetails(adress2, "scara"));     // Staircase
            textMap.put("ʣ", getAdressDetails(adress2, "etaj"));      // Floor
            textMap.put("ʤ", getAdressDetails(adress2, "apartment")); // Apartment
            textMap.put("Ɋ", getAdressDetails(adress1, "strada"));    // Street
            
            i = i + 2; // Skip processed lines
        }
        
        // Extract validity date
        else if (word.contains("Valabilitate") || 
                 word.contains("Validity") || 
                 word.contains("Validite")) {
            String date = textBlocks.get(i + 2).trim();
            int dashIndex = date.indexOf('-');
            if (dashIndex != -1) {
                date = date.substring(0, dashIndex).trim();
            }
            textMap.put("ɣ", date);
        }
    }

    return textMap;
}
```

**Key Insight**: Romanian ID cards have multilingual labels (Romanian, French, English).

**Pattern**: **State Machine** - Processes text line by line, maintaining context.

#### D. Name Extraction from MRZ
```java
private String getName(String word) {
    word = word.substring(5);              // Remove "IDROU" prefix
    word = word.replaceAll("<+", " ").trim(); // Replace << with spaces
    word = word.replaceAll("\\s+", " ");   // Normalize whitespace
    return word;
}
```

**MRZ Format**: Machine Readable Zone uses `<<` as surname/given name separator.

#### E. Birth Date Extraction from CNP
```java
private String getBirthDate(String cnp) {
    String birthDate = "";
    if (cnp.length() == 13) {
        String year = cnp.substring(1, 3);
        String month = cnp.substring(3, 5);
        String day = cnp.substring(5, 7);
        
        if (cnp.charAt(0) == '1' || cnp.charAt(0) == '2') {
            year = "19" + year; // Born in 1900s
        } else {
            year = "20" + year; // Born in 2000s
        }
        
        birthDate = day + "." + month + "." + year;
    }
    return birthDate;
}
```

**CNP Structure**: 
- Digit 0: Gender and century (1=male 1900s, 2=female 1900s, 5=male 2000s, 6=female 2000s)
- Digits 1-2: Year
- Digits 3-4: Month
- Digits 5-6: Day

#### F. Location Parsing
```java
private String getBirthLocation(String place, String type) {
    String[] search;

    if (Objects.equals(type, "judet")) {
        search = new String[]{"Jud."}; // County
    } else {
        search = new String[]{"Mun.", "Ors.", "Sat"}; // Municipality, Town, Village
    }

    for (String s : search) {
        int judIndex = place.indexOf(s);
        if (judIndex != -1) {
            int start = judIndex + s.length();
            int spaceIndex = place.indexOf(' ', start);
            if (spaceIndex != -1) {
                return place.substring(start, spaceIndex).trim();
            } else {
                return place.substring(start).trim();
            }
        }
    }

    return "-";
}
```

**Romanian Address Format**: 
- `Jud.` = Județ (County)
- `Mun.` = Municipiu (Municipality)
- `Ors.` = Oraș (Town)
- `Sat` = Village

#### G. Address Details Parsing
```java
private String getAdressDetails(String adress, String type) {
    String[] search = switch (type) {
        case "numar" -> new String[]{"nr."};      // Number
        case "scara" -> new String[]{"sc."};      // Staircase
        case "bloc" -> new String[]{"bl."};       // Building
        case "etaj" -> new String[]{"et."};       // Floor
        case "strada" -> new String[]{"Str.", "Bd."}; // Street, Boulevard
        default -> new String[]{"ap."};           // Apartment
    };

    // Special handling for street names
    if (type.equals("strada")) {
        for (String s : search) {
            String lower = adress.toLowerCase();
            int idx = lower.indexOf(s.toLowerCase());
            if (idx != -1) {
                int start = idx + s.length();
                int nrIdx = lower.indexOf("nr.", start);
                int commaIdx = lower.indexOf(',', start);
                int end;
                if (nrIdx != -1) end = nrIdx;
                else if (commaIdx != -1) end = commaIdx;
                else end = adress.length();
                
                String name = adress.substring(start, end).trim();
                name = name.replaceAll("^[,\\.:\\-\\s]+", "")
                           .replaceAll("[,\\.:\\-\\s]+$", "");
                return name;
            }
        }
        return "-";
    }

    // Standard parsing for other fields
    for (String s : search) {
        int idx = adress.indexOf(s);
        if (idx != -1) {
            int start = idx + s.length();
            int spaceIndex = adress.indexOf(' ', start);
            if (spaceIndex != -1) {
                return adress.substring(start, spaceIndex).trim();
            } else {
                return adress.substring(start).trim();
            }
        }
    }

    return "-";
}
```

**Romanian Address Abbreviations**:
- `Str.` = Strada (Street)
- `Bd.` = Bulevard (Boulevard)
- `nr.` = Număr (Number)
- `bl.` = Bloc (Building)
- `sc.` = Scara (Staircase)
- `et.` = Etaj (Floor)
- `ap.` = Apartament (Apartment)

#### H. TextBlock Inner Class
```java
public static class TextBlock {
    private final String text;
    private final Float confidence;
    private final Geometry geometry;

    public TextBlock(String text, Float confidence, Geometry geometry) {
        this.text = text;
        this.confidence = confidence;
        this.geometry = geometry;
    }

    public String getText() { return text; }
    public Float getConfidence() { return confidence; }
    public Geometry getGeometry() { return geometry; }

    @Override
    public String toString() {
        return String.format("Text: %s (Confidence: %.2f%%)", text, confidence);
    }
}
```

**Pattern**: **Value Object** - Immutable data container for text extraction results.

**Design Patterns**:
- **Facade Pattern**: Simplifies AWS Textract API
- **Strategy Pattern**: Different parsing strategies for different ID card sections
- **Builder Pattern**: AWS SDK uses builders for requests

---

### 5. Contract

**File**: `Contract.java`

**Purpose**: Handles Word document manipulation for contract generation.

**Key Responsibilities**:
1. Load Word document templates
2. Replace placeholders with actual data
3. Save modified documents

**Core Functionality**:

```java
public class Contract {

    public static void generateContract(String templatePath, String outputPath, 
                                       Map<String, String> data) throws IOException {
        try (FileInputStream fis = new FileInputStream(templatePath);
             XWPFDocument document = new XWPFDocument(fis)) {

            // Replace placeholders in paragraphs
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.getText(0);
                    if (text != null) {
                        for (Map.Entry<String, String> entry : data.entrySet()) {
                            text = text.replace(entry.getKey(), entry.getValue());
                        }
                        run.setText(text, 0);
                    }
                }
            }

            // Replace placeholders in tables
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            for (XWPFRun run : paragraph.getRuns()) {
                                String text = run.getText(0);
                                if (text != null) {
                                    for (Map.Entry<String, String> entry : data.entrySet()) {
                                        text = text.replace(entry.getKey(), entry.getValue());
                                    }
                                    run.setText(text, 0);
                                }
                            }
                        }
                    }
                }
            }

            // Save the new document
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                document.write(fos);
            }
        }
    }
}
```

**Apache POI Hierarchy**:
```
XWPFDocument
├── Paragraphs (XWPFParagraph)
│   └── Runs (XWPFRun) - Contains actual text
└── Tables (XWPFTable)
    └── Rows (XWPFTableRow)
        └── Cells (XWPFTableCell)
            └── Paragraphs (XWPFParagraph)
                └── Runs (XWPFRun)
```

**Why Two Loops?**:
1. First loop: Replace placeholders in document body paragraphs
2. Second loop: Replace placeholders in table cells (contracts often use tables)

**Design Patterns**:
- **Template Method Pattern**: Templates define structure, method fills in data
- **Strategy Pattern**: Same method works for both contract and employee file templates
- **Resource Management**: Try-with-resources ensures document streams are closed

---

### 6. EnvLoader

**File**: `EnvLoader.java`

**Purpose**: Loads environment variables from `.env` file located in the same directory as the JAR.

**Key Responsibilities**:
1. Locate JAR directory
2. Parse .env file
3. Set system properties

**Core Functionalities**:

#### A. JAR Directory Detection
```java
private static File getJarDirectory() {
    try {
        File jarFile = new File(
            EnvLoader.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
        );

        // If running from IDE (not jar), jarFile may be a directory (build/classes...)
        File dir = jarFile.isDirectory() ? jarFile : jarFile.getParentFile();
        return dir;

    } catch (URISyntaxException e) {
        e.printStackTrace();
        return null;
    }
}
```

**Key Insight**: Works both in IDE (returns classes directory) and as JAR (returns JAR's parent directory).

#### B. .env File Loading
```java
public static Map<String, String> loadEnvFromJarDirectory(String envFileName, 
                                                          boolean setSystemProps) {
    Map<String, String> envMap = new HashMap<>();

    File jarDir = getJarDirectory();
    if (jarDir == null) {
        System.err.println("Could not determine JAR directory; skipping .env load.");
        return envMap;
    }

    File envFile = new File(jarDir, envFileName);
    if (!envFile.exists()) {
        System.err.println(".env file not found at: " + envFile.getAbsolutePath());
        return envMap;
    }

    try (BufferedReader reader = new BufferedReader(
            new FileReader(envFile, StandardCharsets.UTF_8))) {

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();

            // Skip empty lines and comments
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            int eqIndex = line.indexOf('=');
            if (eqIndex <= 0) {
                // no '=' or key is empty -> skip
                continue;
            }

            String key = line.substring(0, eqIndex).trim();
            String value = line.substring(eqIndex + 1).trim();

            // Optional: remove surrounding quotes
            value = stripQuotes(value);

            envMap.put(key, value);

            if (setSystemProps && !key.isEmpty()) {
                System.setProperty(key, value);
            }
        }

    } catch (IOException e) {
        System.err.println("Error reading .env file: " + e.getMessage());
    }

    return envMap;
}
```

**Parsing Logic**:
1. Skip empty lines and comments (`#`)
2. Split on `=` character
3. Strip quotes from values
4. Optionally set as system properties

#### C. Quote Stripping
```java
private static String stripQuotes(String s) {
    if (s.length() >= 2) {
        char first = s.charAt(0);
        char last = s.charAt(s.length() - 1);
        if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
            return s.substring(1, s.length() - 1);
        }
    }
    return s;
}
```

**Supports**: Both single and double quotes.

**Design Patterns**:
- **Singleton Pattern** (implicit): Static methods ensure single loading
- **Strategy Pattern**: Can choose whether to set system properties

---

### 7. ConfigToJarDir

**File**: `ConfigToJarDir.java`

**Purpose**: Creates/updates configuration file in JAR directory.

**Core Functionality**:

```java
public class ConfigToJarDir {
    public static File getJarDir() throws URISyntaxException {
        URL url = ConfigToJarDir.class.getProtectionDomain()
                                       .getCodeSource()
                                       .getLocation();
        File jarOrDir = new File(url.toURI());
        return jarOrDir.isFile() ? jarOrDir.getParentFile() : jarOrDir;
    }

    public static void main(String[] args) throws Exception {
        File jarDir = getJarDir();
        File config = new File(jarDir, "config.yml");
        try (PrintWriter pw = new PrintWriter(new FileWriter(config))) {
            pw.println("salary: 4800");
        }
        System.out.println("Wrote config to: " + config.getAbsolutePath());
    }
}
```

**Purpose**: Ensures config.yml exists in the same directory as the JAR file.

**Design Pattern**: **Factory Pattern** - Creates configuration if it doesn't exist.

---

### 8. IdentificationDocument

**File**: `IdentificationDocument.java`

**Purpose**: Domain model representing an identification document (currently unused but demonstrates good OOP design).

**Core Functionality**:

```java
public class IdentificationDocument {
    private final String series;
    private final String number;
    private final String issuedBy;
    private final String validityDate;

    public IdentificationDocument(String series, String number, 
                                 String issuedBy, String validityDate) {
        if (series == null || series.trim().isEmpty()) {
            throw new IllegalArgumentException("Series cannot be empty");
        }
        if (number == null || number.trim().isEmpty()) {
            throw new IllegalArgumentException("Number cannot be empty");
        }
        if (issuedBy == null || issuedBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Issued by cannot be empty");
        }
        if (validityDate == null || validityDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Validity date cannot be empty");
        }
        
        this.series = series.trim();
        this.number = number.trim();
        this.issuedBy = issuedBy.trim();
        this.validityDate = validityDate.trim();
    }

    // Getters...

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentificationDocument that = (IdentificationDocument) o;
        return Objects.equals(series, that.series) &&
               Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(series, number);
    }
}
```

**Design Principles**:
1. **Immutability**: All fields are final
2. **Encapsulation**: Private fields with public getters
3. **Validation**: Constructor validates all inputs
4. **Value Object Pattern**: Two IDs are equal if series and number match
5. **Defensive Copying**: Trims strings to prevent whitespace issues

**Design Patterns**:
- **Value Object Pattern**: Represents an immutable value
- **Factory Pattern**: Constructor validates and creates valid objects only

---

### 9. HelloController

**File**: `HelloController.java`

**Purpose**: Legacy FXML controller (currently unused as the app uses programmatic JavaFX).

```java
public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
```

**Status**: Not currently used. The application uses programmatic JavaFX in `HelloApplication` instead of FXML.

---

## Design Patterns Used

### 1. **MVC (Model-View-Controller)**
- **Model**: Domain objects, data maps
- **View**: JavaFX UI components
- **Controller**: HelloApplication, ContractService

### 2. **Service Layer Pattern**
- **ContractService**: Encapsulates all business logic
- Separates UI from business rules

### 3. **Facade Pattern**
- **DetectText**: Simplifies AWS Textract API
- **ContractService**: Simplifies contract generation workflow
- **EnvLoader**: Simplifies environment variable loading

### 4. **Template Method Pattern**
- **Contract.generateContract()**: Template defines structure, data fills it
- **Submit workflow**: validate → extract → review → generate

### 5. **Factory Pattern**
- **ValidationResult.success()** and **ValidationResult.error()**: Factory methods
- **ConfigToJarDir**: Creates config if missing

### 6. **Strategy Pattern**
- Different validation strategies (initial vs detailed)
- Different parsing strategies (name, CNP, address)

### 7. **Builder Pattern**
- AWS SDK uses builders extensively
- JavaFX UI component creation

### 8. **Observer Pattern**
- JavaFX event handlers
- Button click listeners

### 9. **Value Object Pattern**
- **IdentificationDocument**: Immutable value object
- **TextBlock**: Immutable data container

### 10. **Adapter Pattern**
- **ContractService.extractDataFromImage()**: Adapts DetectText to service layer

### 11. **Resource Management Pattern**
- Try-with-resources throughout for streams, documents, clients

---

## Data Flow

### Complete Application Flow:

```
1. User launches application
   ↓
2. Launcher.main() → HelloApplication.start()
   ↓
3. ContractService.initializeConfig() → ConfigToJarDir creates config.yml
   ↓
4. User fills initial form (reg number, phone, location, city)
   ↓
5. User selects ID card image
   ↓
6. User clicks Submit
   ↓
7. ContractService.validateInitialForm() validates inputs
   ↓
8. ContractService.extractDataFromImage() calls DetectText
   ↓
9. DetectText initializes AWS Textract client
   ↓ (EnvLoader loads .env file with AWS credentials)
10. DetectText.extractTextLines() calls AWS API
   ↓
11. DetectText.extractMap() parses Romanian ID card data
   ↓ (Uses getName, getBirthDate, getBirthLocation, getAdressDetails)
12. Extracted data returned to HelloApplication
   ↓
13. showDetailedReviewPage() displays all fields for review/edit
   ↓
14. User reviews and edits extracted data
   ↓
15. User clicks "Creare Contract"
   ↓
16. ContractService.validateDetailedReviewForm() validates all 18 fields
   ↓
17. ContractService.buildCompleteDataMap() combines extracted + user data
   ↓ (Adds: registration number, dates, phone, location, salary)
18. ContractService.generateDocuments() creates files
   ↓
19. Contract.generateContract() loads templates from JAR resources
   ↓
20. Contract.generateContract() replaces placeholders using Apache POI
   ↓ (Processes both paragraphs and tables)
21. Generated .docx files saved to arhiva/ directory
   ↓
22. Success alert shown, app returns to initial form
```

### Placeholder Mapping:

| Unicode | Field | Example |
|---------|-------|---------|
| ɛ | Name | "POPESCU ION" |
| ɜ | Series | "RX" |
| ɝ | Number | "123456" |
| ɞ | CNP | "1234567890123" |
| ɟ | Issued By | "SPCLEP SECTOR 1" |
| ɠ | Full Address | "Str. Mihai Bravu nr.10..." |
| ɣ | Validity Date | "31.12.2025" |
| ɔ | Registration Number | "100/2024" |
| ɖ | Today's Date | "01.12.2025" |
| ɐ | Hire Date | "02.12.2025" |
| ɕ | Formatted Phone | "0722 123 456" |
| ɘ | Location | "Bucharest" |
| ə | City | "Sector 1" |
| ɥ | Salary | "4800" |
| ŵ | Fisa Registration | "101/2024" |
| Ȣ | Birth County | "AB" |
| Ȥ | Birth Country | "ROU" |
| ȕ | Birth Date | "15.03.1990" |
| Ƚ | Address County | "AB" |
| ʦ | Address City | "Campina" |
| Ɋ | Street | "Mihai Bravu" |
| ʡ | Street Number | "10" |
| ʠ | Building | "A1" |
| ʢ | Staircase | "B" |
| ʣ | Floor | "3" |
| ʤ | Apartment | "12" |

---

## Key Technologies & Libraries

1. **JavaFX** - Desktop GUI framework
2. **AWS SDK for Java (Textract)** - OCR service
3. **Apache POI** - Word document manipulation
4. **Dotenv-java** - Environment variable management
5. **Maven** - Dependency management and build tool

---

## Security Considerations

1. **AWS Credentials**: Never hardcoded, always loaded from `.env` file
2. **File Validation**: Image file type validation before processing
3. **Input Sanitization**: File names sanitized (spaces replaced with underscores)
4. **Error Handling**: Try-catch blocks prevent crashes from invalid inputs
5. **Resource Cleanup**: Try-with-resources ensures no resource leaks

---

## Future Improvements

Based on the current design, potential improvements could include:

1. **Use IdentificationDocument model** throughout instead of Map<String, String>
2. **Database integration** for storing generated contracts
3. **Logging framework** (SLF4J + Logback) instead of System.out.println
4. **Unit tests** for parsing logic
5. **Configuration UI** for editing salary and other settings
6. **Async processing** with progress indicators for OCR
7. **Batch processing** for multiple ID cards
8. **Export to PDF** in addition to Word format

---

## Conclusion

ContractParser demonstrates solid OOP principles and design patterns:

- **Separation of Concerns**: UI, business logic, and data access are clearly separated
- **Encapsulation**: Each class has a single, well-defined responsibility
- **Immutability**: Where possible (ValidationResult, IdentificationDocument)
- **Error Handling**: Comprehensive validation and user-friendly error messages
- **Resource Management**: Proper cleanup of streams and connections
- **Extensibility**: Easy to add new fields or document types

The application successfully automates a complex business process (contract generation) by combining modern technologies (JavaFX, AWS, Apache POI) with clean code architecture.

