package org.example.contractparser;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HelloApplication extends Application {
    private ImageView imageView = new ImageView();
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // Initialize configuration
        ContractService.initializeConfig();

        stage.setTitle("ContractParser");

        Label imageLabel = new Label("ID Image (JPG/PNG):");
        Button imageButton = new Button("Choose Image");
        imageButton.setOnAction(e -> chooseImage(stage));
        imageButton.setStyle(glassButtonStyle());

        HBox imageBox = new HBox(10, imageLabel, imageButton, imageView);
        imageBox.setPadding(new Insets(10));

        TextField regNumberField = new TextField();
        regNumberField.setPromptText("Numar de Inregistrare");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Numar de Telefon");
        TextField placeField = new TextField();
        placeField.setPromptText("Locatie");
        TextField cityField = new TextField();
        cityField.setPromptText("Oras");

        VBox fieldsBox = new VBox(15,
                imageBox,
                new Label("Numar de inregistrare:"), regNumberField,
                new Label("Telefon:"), phoneField,
                new Label("Locatie:"), placeField,
                new Label("Oras:"), cityField
        );
        fieldsBox.setPadding(new Insets(20));
        fieldsBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 10; -fx-background-radius: 10;");
        fieldsBox.setEffect(new DropShadow());

        Button submitButton = new Button("Submit");
        submitButton.setDefaultButton(true);
        submitButton.setStyle(glassButtonStyle());

        HBox submitBox = new HBox(submitButton);
        submitBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(20, fieldsBox, submitBox);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #e9ecef;");
        Scene scene = new Scene(root, 400, 500);

        stage.setScene(scene);
        stage.show();

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

            // Ensure arhiva directory exists
            ContractService.ensureArhivaDirectory();

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

            // Show review page
//            showReviewPage(placeholders,
//                regNumberField.getText(),
//                phoneField.getText(),
//                placeField.getText(),
//                cityField.getText()
//            );

            showDetailedReviewPage(placeholders,
                regNumberField.getText(),
                phoneField.getText(),
                placeField.getText(),
                cityField.getText()
            );
        });

    }

    private String glassButtonStyle() {
        return "-fx-background-color: rgba(255,255,255,0.25);"
                + "-fx-background-radius: 15;"
                + "-fx-border-radius: 15;"
                + "-fx-border-color: rgba(255,255,255,0.5);"
                + "-fx-border-width: 1;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0.5, 0, 2);"
                + "-fx-text-fill: #222;"
                + "-fx-font-size: 15px;"
                + "-fx-padding: 8 24 8 24;"
                + "-fx-cursor: hand;"
                + "-fx-font-weight: bold;"
                + "-fx-background-insets: 0;"
                + "-fx-focus-color: transparent;"
                + "-fx-faint-focus-color: transparent;";
    }

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

    private void showDetailedReviewPage(Map<String, String> extractedData, String regNumber,
                                        String phone, String place, String city) {
        primaryStage.setTitle("Review Extracted Data");

        // Original fields
        TextField nameField = new TextField(extractedData.getOrDefault("ɛ", ""));
        nameField.setPromptText("Nume");

        TextField seriesField = new TextField(extractedData.getOrDefault("ɜ", ""));
        seriesField.setPromptText("Seria");

        TextField numberField = new TextField(extractedData.getOrDefault("ɝ", ""));
        numberField.setPromptText("Numarul");

        TextField cnpField = new TextField(extractedData.getOrDefault("ɞ", ""));
        cnpField.setPromptText("CNP");

        TextField issuedByField = new TextField(extractedData.getOrDefault("ɟ", ""));
        issuedByField.setPromptText("Emis de");

        TextField addressField = new TextField(extractedData.getOrDefault("ɠ", ""));
        addressField.setPromptText("Adresa");

        TextField validityField = new TextField(extractedData.getOrDefault("ɣ", ""));
        validityField.setPromptText("Data de valabilitate");

        // New additional fields
       TextField birthCountyField = new TextField(extractedData.getOrDefault("Ȣ", ""));
        birthCountyField.setPromptText("Judet nastere");

        TextField birthCountryField = new TextField(extractedData.getOrDefault("Ȥ", ""));
        birthCountryField.setPromptText("Tara nastere");

        TextField birthDateField = new TextField(extractedData.getOrDefault("ȕ", ""));
        birthDateField.setPromptText("Data nastere");

        TextField addressCountyField = new TextField(extractedData.getOrDefault("Ƚ", ""));
        addressCountyField.setPromptText("Judet adresa");

        TextField addressCountryField = new TextField(extractedData.getOrDefault("ʦ", ""));
        addressCountryField.setPromptText("Tara adresa");

        TextField addressStreetField = new TextField(extractedData.getOrDefault("Ɋ", ""));
        addressStreetField.setPromptText("Strada");

        TextField addressNumberField = new TextField(extractedData.getOrDefault("ʡ", ""));
        addressNumberField.setPromptText("Numar");

        TextField addressBlocField = new TextField(extractedData.getOrDefault("ʠ", ""));
        addressBlocField.setPromptText("Bloc");

        TextField addressScaraField = new TextField(extractedData.getOrDefault("ʢ", ""));
        addressScaraField.setPromptText("Scara");

        TextField adressEtajField = new TextField(extractedData.getOrDefault("ʣ", ""));
        adressEtajField.setPromptText("Etaj");

        TextField addressApartmentField = new TextField(extractedData.getOrDefault("ʤ", ""));
        addressApartmentField.setPromptText("Apartament");

        VBox fieldsBox = new VBox(15,
                new Label("Nume:"), nameField,
                new Label("Seria:"), seriesField,
                new Label("Numarul:"), numberField,
                new Label("CNP:"), cnpField,
                new Label("Emis de:"), issuedByField,
                new Label("Adresa:"), addressField,
                new Label("Data de valabilitate:"), validityField,
                new Separator(),
                new Label("Judet nastere:"), birthCountyField,
                new Label("Tara nastere:"), birthCountryField,
                new Label("Data nastere:"), birthDateField,
                new Separator(),
                new Label("Judet adresa:"), addressCountyField,
                new Label("Tara adresa:"), addressCountryField,
                new Label("Strada:"), addressStreetField,
                new Label("Numar:"), addressNumberField,
                new Label("Bloc:"), addressBlocField,
                new Label("Scara:"), addressScaraField,
                new Label("Etaj:"), adressEtajField,
                new Label("Apartament:"), addressApartmentField
        );
        fieldsBox.setPadding(new Insets(20));
        fieldsBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 10; -fx-background-radius: 10;");
        fieldsBox.setEffect(new DropShadow());

        Button createButton = new Button("Creare Contract");
        createButton.setDefaultButton(true);
        createButton.setStyle(glassButtonStyle());

        Button backButton = new Button("Inapoi");
        backButton.setStyle(glassButtonStyle());
        backButton.setOnAction(e -> start(primaryStage));

        createButton.setOnAction(e -> {
            // Validate review form fields
            ContractService.ValidationResult validation = ContractService.validateReviewForm(
                    nameField.getText(),
                    seriesField.getText(),
                    numberField.getText(),
                    cnpField.getText(),
                    issuedByField.getText(),
                    addressField.getText(),
                    validityField.getText()
            );

            if (!validation.isValid()) {
                showErrorAlert(validation.getErrorTitle(), validation.getErrorMessage());
                return;
            }

            // Update extracted data with all edited values
            extractedData.put("ɛ", nameField.getText());
            extractedData.put("ɜ", seriesField.getText());
            extractedData.put("ɝ", numberField.getText());
            extractedData.put("ɞ", cnpField.getText());
            extractedData.put("ɟ", issuedByField.getText());
            extractedData.put("ɠ", addressField.getText());
            extractedData.put("ɣ", validityField.getText());

            // Add new fields to extracted data
            extractedData.put("Ȣ", birthCountyField.getText());
            extractedData.put("Ȥ", birthCountryField.getText());
            extractedData.put("ȕ", birthDateField.getText());
            extractedData.put("Ƚ", addressCountyField.getText());
            extractedData.put("ʦ", addressCountryField.getText());
            extractedData.put("Ɋ", addressStreetField.getText());
            extractedData.put("ʡ", addressNumberField.getText());
            extractedData.put("ʠ", addressBlocField.getText());
            extractedData.put("ʢ", addressScaraField.getText());
            extractedData.put("ʣ", adressEtajField.getText());
            extractedData.put("ʤ", addressApartmentField.getText());

            // Build complete data map with all placeholders
            Map<String, String> completeData = ContractService.buildCompleteDataMap(
                    extractedData, regNumber, phone, place, city
            );

            // Generate documents
            try {
                ContractService.generateDocuments(nameField.getText(), completeData);

                showSuccessAlert("Contracte generate",
                        "Contractul si Fisa au fost generate cu succes in folderul 'arhiva'.");
                start(primaryStage);

            } catch (IOException ex) {
                showErrorAlert("Failed to generate contracts", ex.getMessage());
            }
        });

        HBox buttonBox = new HBox(15, backButton, createButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(20, fieldsBox, buttonBox);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #e9ecef;");

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #e9ecef;");

        Scene scene = new Scene(scrollPane, 500, 700);
        primaryStage.setScene(scene);
    }



    private void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void showSuccessAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }

}
