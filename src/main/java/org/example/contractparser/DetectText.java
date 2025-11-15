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

                System.out.println(lineText);

                }
            }
        }

        System.out.println("\n\nExtracted lines: " + lines);

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
                textMap.put("ɛ", getName(word));
                System.out.println("Numele este: " + getName(word));


            }
            else if(word.contains("CNP")){
                word = word.replaceAll("CNP", "").trim();
                textMap.put("ɞ", word);
                System.out.println("CNP-ul este: " + word);
                textMap.put("ȕ", getBirthDate( word ));
                System.out.println("Data nasterii este: " + getBirthDate( word ));
            }
            else if(word.contains("<")){
                word = word.trim();
                textMap.put("ɜ", word.substring(0,2));
                textMap.put("ɝ", word.substring(2,8));
                // here we can also put the cnp if needed
                System.out.println("Seria este: " + word.substring(0,2));
                System.out.println("Numarul este: " + word.substring(2,8));

            }
            else if(word.contains("SPCLEP")){
                word = word.trim();
                textMap.put("ɟ",word);
                System.out.println("Eliberat de: " + word);

            }
            else if(word.contains("Loc Nastere") || word.contains("Lieu de naissance") || word.contains("Place of birth")){
                String place = textBlocks.get(i+1).trim();
                System.out.println("lets eee " + place);
                System.out.println("Locul nasterii este: " + getBirthLocation(place, "judet"));
                System.out.println("Locul nasterii este: " + getBirthLocation(place, "country"));
                // now we need to put here for the placeholder of judet and country if needed
                textMap.put("Ȣ", getBirthLocation(place, "judet"));
                textMap.put("Ȥ", getBirthLocation(place, "country"));


            } /// TODO : make a way to extract the birth place because it might have a lot of edge cases
            else if(word.contains("Adresse") || word.contains("Adress") || word.contains("Domiciliu")){
                String adress1 = textBlocks.get(i+1).trim();
                String adress2 = textBlocks.get(i+2).trim();
                System.out.println("Adresa este: " + adress1 + " " + adress2);
                System.out.println("blocul este: " + getAdressDetails(adress2, "bloc"));
                System.out.println("numarul este: " + getAdressDetails(adress2, "numar"));
                System.out.println("scara este: " + getAdressDetails(adress2, "scara"));
                System.out.println("etajul este: " + getAdressDetails(adress2, "etaj"));
                System.out.println("apartamentul este: " + getAdressDetails(adress2, "apartment"));

                textMap.put("Ƚ",getBirthLocation(adress1, "judet"));
                textMap.put("ʦ", getBirthLocation(adress1, "localitate"));
                textMap.put("ɠ", adress1 + " " + adress2);
                textMap.put("ʠ", getAdressDetails(adress2, "bloc"));
                textMap.put("ʡ", getAdressDetails(adress2, "numar"));
                textMap.put("ʢ", getAdressDetails(adress2, "scara"));
                textMap.put("ʣ", getAdressDetails(adress2, "etaj"));
                textMap.put("ʤ", getAdressDetails(adress2, "apartment"));

                System.out.println("strada este: " + getAdressDetails(adress1, "strada"));
                System.out.println("judetul este: " + getBirthLocation(adress1, "judet"));
                System.out.println("orasul este: " + getBirthLocation(adress1, "oras"));

                if(!getAdressDetails(adress1, "strada").equals("nedefinit")){
                    textMap.put("Ɋ",getAdressDetails(adress1, "strada"));
                }
                else textMap.put("Ɋ",getAdressDetails(adress2, "strada")); // if the street is not in the first line try the second line
                i = i + 2;
            }
            else if(word.contains("Valabilitate") || word.contains("Validity") || word.contains("Validite")){
                String date = textBlocks.get(i+2).trim();
                int dashIndex = date.indexOf('-');
                if (dashIndex != -1) {
                    date = date.substring(0, dashIndex).trim();
                }
                System.out.println("Data este: " + date);
                textMap.put("ɣ", date);
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



        return word;

    }

    private String getBirthDate(String cnp){
        String birthDate = "";
        if(cnp.length() == 13){
            String year = cnp.substring(1,3);
            String month = cnp.substring(3,5);
            String day = cnp.substring(5,7);
           if (cnp.charAt(0) == '1' || cnp.charAt(0) == '2') {
                year = "19" + year;
           } else {
                year = "20" + year;
           }
            birthDate = day + "." + month + "." + year;
        }
        return birthDate;
    }

    private String getBirthLocation(String place, String type) {
        String[] search ;

        if (Objects.equals(type, "judet")) search = new String[]{"Jud."};
        else search = new String[]{"Mun.","Ors.", "Sat"};



        String[] parts = place.split(",");
       for( String s : search) {
           int judIndex = place.indexOf(s);
           if (judIndex != -1) {
               int start = judIndex + "Jud.".length();
               int spaceIndex = place.indexOf(' ', start);
               if (spaceIndex != -1) {
                   return place.substring(start, spaceIndex).trim();
               } else {
                   return place.substring(start).trim();
               }
           }
           if (parts.length > 1) {
               return parts[parts.length - 1].trim();
           }
       }

        return "nedefinit";
    }

    private String getAdressDetails(String adress, String type) {

        String[] search = switch (type) {
            case "numar" -> new String[]{"nr."};
            case "scara" -> new String[]{"sc."};
            case "bloc" -> new String[]{"bl."};
            case "etaj" -> new String[]{"et."};
            case "strada" -> new String[]{"Str.","Bd."};
            default -> new String[]{"ap."};
        };

        if(type.equals("strada")){

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
                    name = name.replaceAll("^[,\\.:\\-\\s]+", "").replaceAll("[,\\.:\\-\\s]+$", "");
                    return name;
                }
            }
            return "nedefinit";
        }


        String[] parts = adress.split(",");
        for( String s : search) {
            int judIndex = adress.indexOf(s);
            if (judIndex != -1) {
                int start = judIndex + s.length();
                int spaceIndex = adress.indexOf(' ', start);
                if (spaceIndex != -1) {
                    return adress.substring(start, spaceIndex).trim();
                } else {
                    return adress.substring(start).trim();
                }
            }
            if (parts.length > 1) {
                return parts[parts.length - 1].trim();
            }
        }

        return "nedefinit";
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
