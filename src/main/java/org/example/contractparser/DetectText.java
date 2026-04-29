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
import java.util.*;


public class DetectText {

    private final TextractClient textractClient;
    private final Dotenv dotenv;

    public DetectText() {
        this.dotenv = Dotenv.load();
        String awsRegion = dotenv.get("AWS_REGION") != null ? dotenv.get("AWS_REGION") : "us-east-1";

        Map<String, String> env = EnvLoader.loadEnvFromJarDirectory(".env", true);

        String awsAccessKeyId = env.get("AWS_ACCESS_KEY_ID");
        String awsSecretAccessKey = env.get("AWS_SECRET_ACCESS_KEY");
        System.out.println("AWS_ACCESS_KEY_ID: " + awsAccessKeyId);
        System.out.println("AWS_SECRET_ACCESS_KEY: " + awsSecretAccessKey);



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

                System.out.println(lineText);

                }
            }
        }

        System.out.println("\n\nExtracted lines: " + lines);

        return lines;
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

}
