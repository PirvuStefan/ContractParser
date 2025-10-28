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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class HelloApplication extends Application {
    private ImageView imageView = new ImageView();
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        try {
            ConfigToJarDir.main(new String[]{});
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        stage.setTitle("ContractParser");

        Label imageLabel = new Label("ID Image (JPG/PNG):");
        Button imageButton = new Button("Choose Image");
        imageButton.setOnAction(e -> chooseImage(stage));
        imageButton.setStyle(glassButtonStyle());

        HBox imageBox = new HBox(10, imageLabel, imageButton, imageView);
        imageBox.setPadding(new Insets(10));

        TextField regNumberField = new TextField();
        regNumberField.setPromptText("Registration Number");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        TextField placeField = new TextField();
        placeField.setPromptText("Place");
        TextField cityField = new TextField();
        cityField.setPromptText("City");

        VBox fieldsBox = new VBox(15,
                imageBox,
                new Label("Registration Number:"), regNumberField,
                new Label("Phone Number:"), phoneField,
                new Label("Place:"), placeField,
                new Label("City:"), cityField
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
            File arhivaDir = new File("arhiva");
            if (!arhivaDir.exists()) {
                arhivaDir.mkdir();
            }

            String imagePath = imageView.getImage().getUrl().replaceFirst("^file:", "");
            DetectText.main(new String[]{}, new File(imagePath));


            String name = "Andrei_Mihai";


            int salary;
            Path configPath = Paths.get("config.yml");
            if(!Files.exists(configPath)) {
                try {
                    Files.createFile(configPath);
                    Files.write(configPath, List.of("salary: 4800"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            try {
                if (Files.exists(configPath)) {
                    List<String> lines = Files.readAllLines(configPath);
                    salary = 4800; // default
                    for (String l : lines) {
                        String trimmed = l.trim();
                        if (trimmed.startsWith("salary:")) {
                            String val = trimmed.substring("salary:".length()).trim();
                            val = val.replaceAll("^['\"]|['\"]$", "");
                            try {
                                salary = Integer.parseInt(val);
                            } catch (NumberFormatException ignored) {
                            }
                            break;
                        }
                    }
                } else {
                    salary = 4800; // default if config missing
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            String today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            String hireday = java.time.LocalDate.now().plusDays(1).format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            String phone = phoneField.getText();
            phone = phone.replaceAll("(\\d{4})(\\d{3})(\\d{3})", "$1 $2 $3");

            // this should be the palceholder for salary :  ɥ
            System.out.println(phone); // Debugging line
            System.out.println("Today's date: " + today); // Debugging line
//            Map < String, String > placeholders = Map.of(
//                    "registration_number", regNumberField.getText(),
//                    "today", today,
//                    "hireday", hireday,
//                    "phone", phoneField.getText(),
//                    "place", placeField.getText(),
//                    "city", cityField.getText(),
//                    "name", name,
//                    "salary", Integer.toString(salary)
//            );
            Map<String, String> placeholders;
            try {
                placeholders = new DetectText().extractMap(imagePath);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            String salaryStr = Integer.toString(salary);
            placeholders.put("ɔ", regNumberField.getText());
            placeholders.put("ɖ", today);
            placeholders.put("ɐ", hireday);
            placeholders.put("ɕ", phone);
            placeholders.put("ɘ", placeField.getText());
            placeholders.put("ə", cityField.getText());
            placeholders.put("ɥ", salaryStr);
            File contractFile = new File(arhivaDir, name + ".docx");
//            try {
//                Contract.generateContract(
//                        "src/main/resources/contract.docx",
//                        "output.docx",
//                        placeholders
//                );
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//
//            try {
//                Contract.generateContract(
//                        "src/main/resources/fisa.docx",
//                        "output2.docx",
//                        placeholders
//                );
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }

            showReviewPage(placeholders, regNumberField.getText(),phone, placeField.getText(),cityField.getText());


            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
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

    private void showReviewPage(Map<String, String> extractedData, String regNumber,
                                String phone, String place, String city) {
        primaryStage.setTitle("Review Extracted Data");

        // Create text fields for the 7 extracted values
        TextField nameField = new TextField(extractedData.getOrDefault("ɛ", ""));
        nameField.setPromptText("Name");

        TextField seriesField = new TextField(extractedData.getOrDefault("ɜ", ""));
        seriesField.setPromptText("ID Series");

        TextField numberField = new TextField(extractedData.getOrDefault("ɝ", ""));
        numberField.setPromptText("ID Number");

        TextField cnpField = new TextField(extractedData.getOrDefault("ɞ", ""));
        cnpField.setPromptText("CNP");

        TextField issuedByField = new TextField(extractedData.getOrDefault("ɟ", ""));
        issuedByField.setPromptText("Issued By");

        TextField addressField = new TextField(extractedData.getOrDefault("ɠ", ""));
        addressField.setPromptText("Address");

        TextField validityField = new TextField(extractedData.getOrDefault("ɣ", ""));
        validityField.setPromptText("Validity Date");

        VBox fieldsBox = new VBox(15,
                new Label("Name:"), nameField,
                new Label("ID Series:"), seriesField,
                new Label("ID Number:"), numberField,
                new Label("CNP:"), cnpField,
                new Label("Issued By:"), issuedByField,
                new Label("Address:"), addressField,
                new Label("Validity Date:"), validityField
        );
        fieldsBox.setPadding(new Insets(20));
        fieldsBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 10; -fx-background-radius: 10;");
        fieldsBox.setEffect(new DropShadow());

        Button createButton = new Button("Create Contract");
        createButton.setDefaultButton(true);
        createButton.setStyle(glassButtonStyle());

        Button backButton = new Button("Back");
        backButton.setStyle(glassButtonStyle());
        backButton.setOnAction(e -> start(primaryStage));

        createButton.setOnAction(e -> {
            // Update placeholders with edited values
            extractedData.put("ɛ", nameField.getText());
            extractedData.put("ɜ", seriesField.getText());
            extractedData.put("ɝ", numberField.getText());
            extractedData.put("ɞ", cnpField.getText());
            extractedData.put("ɟ", issuedByField.getText());
            extractedData.put("ɠ", addressField.getText());
            extractedData.put("ɣ", validityField.getText());

            // Add additional data
            int salary;
            Path configPath = Paths.get("config.yml");
            try {
                if (!Files.exists(configPath)) {
                    Files.createFile(configPath);
                    Files.write(configPath, List.of("salary: 4800"));
                }
                List<String> lines = Files.readAllLines(configPath);
                salary = 4800;
                for (String l : lines) {
                    String trimmed = l.trim();
                    if (trimmed.startsWith("salary:")) {
                        String val = trimmed.substring("salary:".length()).trim();
                        val = val.replaceAll("^['\"]|['\"]$", "");
                        try {
                            salary = Integer.parseInt(val);
                        } catch (NumberFormatException ignored) {
                        }
                        break;
                    }
                }
            } catch (IOException ex) {
                salary = 4800;
            }

            String today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            String hireday = java.time.LocalDate.now().plusDays(1).format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            String formattedPhone = phone.replaceAll("(\\d{4})(\\d{3})(\\d{3})", "$1 $2 $3");

            extractedData.put("ɔ", regNumber);
            extractedData.put("ɖ", today);
            extractedData.put("ɐ", hireday);
            extractedData.put("ɕ", formattedPhone);
            extractedData.put("ɘ", place);
            extractedData.put("ə", city);
            extractedData.put("ɥ", Integer.toString(salary));

            String name = nameField.getText().replace(" ", "_");
            File arhivaDir = new File("arhiva");

            try {
                Contract.generateContract(
                        "src/main/resources/contract.docx",
                        "output.docx",
                        extractedData
                );
                Contract.generateContract(
                        "src/main/resources/fisa.docx",
                        "output2.docx",
                        extractedData
                );

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Contracts Generated");
                alert.setContentText("Contract and Fisa have been successfully created!");
                alert.showAndWait();

            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to generate contracts");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
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

    public static void main(String[] args) {
        launch();
    }

}
