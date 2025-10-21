package org.example.contractparser;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DetectText {

    private final TextractClient textractClient;

    public DetectText() {
        this.textractClient = TextractClient.builder()
                .region(Region.US_EAST_1) // Change to your preferred region
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }

    public DetectText(Region region) {
        this.textractClient = TextractClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }

    /**
     * Extracts text from an image file using Amazon Textract
     * @param imagePath Path to the image file
     * @return Extracted text as a single string
     * @throws IOException If file reading fails
     */
    public String extractText(String imagePath) throws IOException {
        File imageFile = new File(imagePath);

        try (FileInputStream imageStream = new FileInputStream(imageFile)) {
            SdkBytes sourceBytes = SdkBytes.fromInputStream(imageStream);

            Document document = Document.builder()
                    .bytes(sourceBytes)
                    .build();

            DetectDocumentTextRequest request = DetectDocumentTextRequest.builder()
                    .document(document)
                    .build();

            DetectDocumentTextResponse response = textractClient.detectDocumentText(request);

            StringBuilder extractedText = new StringBuilder();
            List<Block> blocks = response.blocks();

            for (Block block : blocks) {
                if (block.blockType() == BlockType.LINE) {
                    extractedText.append(block.text()).append("\n");
                }
            }

            return extractedText.toString().trim();
        }
    }

    /**
     * Extracts text and returns it as a list of lines
     * @param imagePath Path to the image file
     * @return List of extracted text lines
     * @throws IOException If file reading fails
     */
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
                    lines.add(block.text());
                }
            }
        }

        return lines;
    }

    /**
     * Extracts text with detailed information including confidence scores
     * @param imagePath Path to the image file
     * @return List of TextBlock objects containing text and metadata
     * @throws IOException If file reading fails
     */
    public List<TextBlock> extractTextWithDetails(String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        List<TextBlock> textBlocks = new ArrayList<>();

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
                    textBlocks.add(new TextBlock(
                            block.text(),
                            block.confidence(),
                            block.geometry()
                    ));
                }
            }
        }

        return textBlocks;
    }

    /**
     * Close the Textract client
     */
    public void close() {
        if (textractClient != null) {
            textractClient.close();
        }
    }

    /**
     * Inner class to hold text block information
     */
    public static class TextBlock {
        private final String text;
        private final Float confidence;
        private final Geometry geometry;

        public TextBlock(String text, Float confidence, Geometry geometry) {
            this.text = text;
            this.confidence = confidence;
            this.geometry = geometry;
        }

        public String getText() {
            return text;
        }

        public Float getConfidence() {
            return confidence;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        @Override
        public String toString() {
            return String.format("Text: %s (Confidence: %.2f%%)", text, confidence);
        }
    }

    /**
     * Example usage
     */
    public static void main(String[] args) {
        DetectText detector = new DetectText();

        try {
            String imagePath = "path/to/your/image.jpg";

            // Extract text as a single string
            String extractedText = detector.extractText(imagePath);
            System.out.println("Extracted Text:\n" + extractedText);

            // Extract text as lines
            List<String> lines = detector.extractTextLines(imagePath);
            System.out.println("\nExtracted Lines:");
            lines.forEach(System.out::println);

            // Extract with details
            List<TextBlock> blocks = detector.extractTextWithDetails(imagePath);
            System.out.println("\nDetailed Extraction:");
            blocks.forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("Error extracting text: " + e.getMessage());
        } finally {
            detector.close();
        }
    }
}
