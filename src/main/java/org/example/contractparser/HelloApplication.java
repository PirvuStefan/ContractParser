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

public class HelloApplication extends Application {
    private ImageView imageView = new ImageView();

    // Java
    @Override
    public void start(Stage stage) {
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

            File imageFile = null;
            if (imageView.getImage() != null && imageView.getImage().getUrl() != null) {
                String url = imageView.getImage().getUrl();
                if (url.startsWith("file:/")) {
                    imageFile = new File(url.replaceFirst("file:/+", "/"));
                }
            }
            if (imageFile != null && imageFile.exists()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Form submitted successfully!", ButtonType.OK);
                extractTextFromImage(imageFile);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an ID image.", ButtonType.OK);
                alert.showAndWait();
            }
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

    public static void main(String[] args) {
        launch();
    }

    private void extractTextFromImage(File imageFile) {
        IDCardService idCardService = new IDCardService();
        String extractedText = idCardService.extractTextFromImage(imageFile);
        System.out.println("Extracted Text: " + extractedText);
    }
}

