package org.example.contractparser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class that handles all business logic for contract generation.
 * This includes configuration management, data extraction, validation, and document generation.
 */
public class ContractService {

    private static final String ARHIVA_DIR = "arhiva";
    private static final String CONFIG_FILE = "config.yml";
    private static final int DEFAULT_SALARY = 4050;
    private static final String DATE_FORMAT = "dd.MM.yyyy";


    public static void initializeConfig() {
        try {
            ConfigToJarDir.main(new String[]{});
        } catch (Exception ex) {
            throw new RuntimeException("Failed to initialize configuration", ex);
        }
    }


    public static void ensureArhivaDirectory() {
        File arhivaDir = new File(ARHIVA_DIR);
        if (!arhivaDir.exists()) {
            arhivaDir.mkdir();
        }
    }


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
                    value = value.replaceAll("^['\"]|['\"]$", "");
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


    public static String formatPhoneNumber(String phone) {
        if (phone == null) return "";
        String cleaned = phone.replaceAll("\\s+", "");
        return cleaned.replaceAll("(\\d{4})(\\d{3})(\\d{3})", "$1 $2 $3");
    }


    public static Map<String, String> extractDataFromImage(String imagePath) throws IOException {
        if (imagePath == null || imagePath.isEmpty()) {
            return new HashMap<>();
        }
        return new DetectText().extractMap(imagePath);
    }


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


    public static void generateDocuments(String employeeName, Map<String, String> data) throws IOException {
        ensureArhivaDirectory();

        String sanitizedName = employeeName.replace(" ", "_");
        File arhivaDir = new File(ARHIVA_DIR);
        File contractFile = new File(arhivaDir, sanitizedName + ".docx");
        File fisaFile = new File(arhivaDir, sanitizedName + "_fisa.docx");

        // Create output files
        if (!contractFile.exists()) contractFile.createNewFile();
        if (!fisaFile.exists()) fisaFile.createNewFile();

        // Load templates from resources and copy to temp files
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



    public static boolean isFieldValid(String value) {
        return value != null && !value.trim().isEmpty();
    }


    public static class ValidationResult {
        private final boolean valid;
        private final String errorTitle;
        private final String errorMessage;

        public ValidationResult(boolean valid, String errorTitle, String errorMessage) {
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

        public boolean isValid() {
            return valid;
        }

        public String getErrorTitle() {
            return errorTitle;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }


    public static ValidationResult validateInitialForm(String regNumber, String phone, String place, String city) {
        if (!isFieldValid(regNumber)) {
            return ValidationResult.error("Numarul de inregistrare lipsa",
                "Te rog introdu numarul de inregistrare.");
        }
        if (!isFieldValid(phone)) {
            return ValidationResult.error("Numarul de telefon lipsa",
                "Te rog introdu numarul de telefon.");
        }
        if (!isFieldValid(place)) {
            return ValidationResult.error("Locatia lipsa",
                "Te rog introdu locatia.");
        }
        if (!isFieldValid(city)) {
            return ValidationResult.error("Orasul lipsa",
                "Te rog introdu orasul.");
        }
        return ValidationResult.success();
    }

    /**
     *
     * @param name name
     * @param series series
     * @param number number
     * @param cnp CNP
     * @param issuedBy issued by
     * @param address address
     * @param validity validity date
     * @return validation result
     */
    public static ValidationResult validateReviewForm(String name, String series, String number,
                                                     String cnp, String issuedBy, String address,
                                                     String validity) {
        if (!isFieldValid(name)) {
            return ValidationResult.error("Numele lipsa", "Te rog introdu numele.");
        }
        if (!isFieldValid(series)) {
            return ValidationResult.error("Seria lipsa", "Te rog introdu seria.");
        }
        if (!isFieldValid(number)) {
            return ValidationResult.error("Numarul lipsa", "Te rog introdu numarul.");
        }
        if (!isFieldValid(cnp)) {
            return ValidationResult.error("CNP-ul lipsa", "Te rog introdu CNP-ul.");
        }
        if (!isFieldValid(issuedBy)) {
            return ValidationResult.error("Cimpul 'Emis de' lipsa",
                "Te rog introdu cimpul 'Emis de'.");
        }
        if (!isFieldValid(address)) {
            return ValidationResult.error("Adresa lipsa", "Te rog introdu adresa.");
        }
        if (!isFieldValid(validity)) {
            return ValidationResult.error("Data de valabilitate lipsa",
                "Te rog introdu data de valabilitate.");
        }
        return ValidationResult.success();
    }

    /**
     * Validates the detailed review form with all additional fields
     *
     * @param name name
     * @param series series
     * @param number number
     * @param cnp CNP
     * @param issuedBy issued by
     * @param address address
     * @param validity validity date
     * @param birthCounty birth county
     * @param birthCountry birth country
     * @param birthDate birth date
     * @param addressCounty address county
     * @param addressCountry address country
     * @param addressStreet address street
     * @param addressNumber address number
     * @param addressBloc address bloc
     * @param addressScara address scara
     * @param addressEtaj address etaj
     * @param addressApartment address apartment
     * @return validation result
     */
    public static ValidationResult validateDetailedReviewForm(String name, String series, String number,
                                                              String cnp, String issuedBy, String address,
                                                              String validity, String birthCounty, String birthCountry,
                                                              String birthDate, String addressCounty, String addressCountry,
                                                              String addressStreet, String addressNumber, String addressBloc,
                                                              String addressScara, String addressEtaj, String addressApartment) {
        if (!isFieldValid(name)) {
            return ValidationResult.error("Numele lipsa", "Te rog introdu numele.");
        }
        if (!isFieldValid(series)) {
            return ValidationResult.error("Seria lipsa", "Te rog introdu seria.");
        }
        if (!isFieldValid(number)) {
            return ValidationResult.error("Numarul lipsa", "Te rog introdu numarul.");
        }
        if (!isFieldValid(cnp)) {
            return ValidationResult.error("CNP-ul lipsa", "Te rog introdu CNP-ul.");
        }
        if (!isFieldValid(issuedBy)) {
            return ValidationResult.error("Cimpul 'Emis de' lipsa",
                "Te rog introdu cimpul 'Emis de'.");
        }
        if (!isFieldValid(address)) {
            return ValidationResult.error("Adresa lipsa", "Te rog introdu adresa.");
        }
        if (!isFieldValid(validity)) {
            return ValidationResult.error("Data de valabilitate lipsa",
                "Te rog introdu data de valabilitate.");
        }
        if (!isFieldValid(birthCounty)) {
            return ValidationResult.error("Judetul de nastere lipsa",
                "Te rog introdu judetul de nastere.");
        }
        if (!isFieldValid(birthCountry)) {
            return ValidationResult.error("Tara de nastere lipsa",
                "Te rog introdu tara de nastere.");
        }
        if (!isFieldValid(birthDate)) {
            return ValidationResult.error("Data nasterii lipsa",
                "Te rog introdu data nasterii.");
        }
        if (!isFieldValid(addressCounty)) {
            return ValidationResult.error("Judetul adresei lipsa",
                "Te rog introdu judetul adresei.");
        }
        if (!isFieldValid(addressCountry)) {
            return ValidationResult.error("Tara adresei lipsa",
                "Te rog introdu tara adresei.");
        }
        if (!isFieldValid(addressStreet)) {
            return ValidationResult.error("Strada lipsa",
                "Te rog introdu strada.");
        }
        if (!isFieldValid(addressNumber)) {
            return ValidationResult.error("Numarul adresei lipsa",
                "Te rog introdu numarul adresei.");
        }
        if (!isFieldValid(addressBloc)) {
            return ValidationResult.error("Blocul lipsa",
                "Te rog introdu blocul.");
        }
        if (!isFieldValid(addressScara)) {
            return ValidationResult.error("Scara lipsa",
                "Te rog introdu scara.");
        }
        if (!isFieldValid(addressEtaj)) {
            return ValidationResult.error("Etajul lipsa",
                "Te rog introdu etajul.");
        }
        if (!isFieldValid(addressApartment)) {
            return ValidationResult.error("Apartamentul lipsa",
                "Te rog introdu apartamentul.");
        }
        return ValidationResult.success();
    }
}

