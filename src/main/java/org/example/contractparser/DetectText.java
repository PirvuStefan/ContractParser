package org.example.contractparser;

import io.github.cdimascio.dotenv.Dotenv;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetectText {

    private final TextractClient textractClient;
    private final Dotenv dotenv;

    public DetectText() {
        this.dotenv = Dotenv.load();//
        String awsAccessKeyId = dotenv.get("AWS_ACCESS_KEY_ID");
        String awsSecretAccessKey = dotenv.get("AWS_SECRET_ACCESS_KEY");
        String awsRegion = dotenv.get("AWS_REGION") != null ? dotenv.get("AWS_REGION") : "us-east-1";

        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(awsAccessKeyId, awsSecretAccessKey);

        this.textractClient = TextractClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

    public DetectText(Region region) {
        this.dotenv = Dotenv.load();
        String awsAccessKeyId = dotenv.get("AWS_ACCESS_KEY_ID");
        String awsSecretAccessKey = dotenv.get("AWS_SECRET_ACCESS_KEY");

        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(awsAccessKeyId, awsSecretAccessKey);

        this.textractClient = TextractClient.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }


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


    private List<TextBlock> extractTextWithDetails(String imagePath) throws IOException {
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

    public Map<String, String> extractMap(String imagePath) throws IOException {
        Map<String, String> textMap = new HashMap<>();
        int wordNumber = 1;
        int count = 0;

        List < String> textBlocks = DetectText.this.extractTextLines(imagePath);
        for (int i = 0; i < textBlocks.size(); i++) {
            String word = textBlocks.get(i).trim();

            if(word.contains("<<")){
                textMap.put("name", getName(word));
                System.out.println("Numele este: " + getName(word));

            }
            else if(word.contains("<")){
                word = word.trim();
                textMap.put("seria", word.substring(0,2));
                textMap.put("nr", word.substring(2,7));
                // here we can also put the cnp if needed
                textMap.put("cnp", getCNP(word));


                System.out.println("Seria este: " + word.substring(0,2));
                System.out.println("Numarul este: " + word.substring(2,8));

            }
            else if(word.contains("SPCLEP")){
                word = word.trim();
                textMap.put("issued",word);
                System.out.println("Eliberat de: " + word);

            }
            else if(word.contains("Adresse") || word.contains("Adress") || word.contains("Domiciliu")){
                String adress1 = textBlocks.get(i+1).trim();
                String adress2 = textBlocks.get(i+2).trim();
                System.out.println("Adresa este: " + adress1 + " " + adress2);
                textMap.put("address", adress1 + " " + adress2);
                i = i + 2;
            }
        }


        return textMap;
    }

    private String getName(String word){
        word = word.substring(5);
        word = word.replaceAll("<+", " ").trim();
        word = word.replaceAll("\\s+", " ");
        return word;

    }

    private String getCNP(String word){
        word = word.trim();
        char c = word.charAt(word.length() - 1);
        // AX839941<58009702248M310803310126561
        word = c + word.substring(13,19) + word.substring(29,35);
        System.out.println(" CNP este: " + word);
        return word;

    }

    private boolean checkLetter(char c) {
        return Character.isLetter(c);
    }



    public void close() {
        if (textractClient != null) {
            textractClient.close();
        }
    }


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


    public static void main(String[] args, File imageFile) {
        DetectText detector = new DetectText();

        try {
            String imagePath = imageFile.getAbsolutePath().replaceFirst("^file:", "");

            // Extract text as a single string


            // Extract text as lines
            List<String> lines = detector.extractTextLines(imagePath);
            System.out.println("\nExtracted Lines:");
            lines.forEach(System.out::println);

            // Extract with details
//            List<TextBlock> blocks = detector.extractTextWithDetails(imagePath);
//            System.out.println("\nDetailed Extraction:");
//            blocks.forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("Error extracting text: " + e.getMessage());
        } finally {
            detector.close();
        }
    }
}
