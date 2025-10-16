package org.example.contractparser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;

import io.github.cdimascio.dotenv.Dotenv;


public class DeepSeekOCR {

    Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = System.getenv("DEEPSEEK_API_KEY");;
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";

    public static String extractTextFromImage(String imagePath) {
        try {
            // Encode image to base64
            String filePath = imagePath.startsWith("file:/") ? imagePath.replace("file:/", "") : imagePath;
            byte[] imageBytes = Files.readAllBytes(Paths.get(filePath));

            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            String requestBody = String.format(
                    "{" +
                            "\"model\": \"deepseek-chat\"," +
                            "\"messages\": [{" +
                            "\"role\": \"user\"," +
                            "\"content\": [" +
                            "{\"type\": \"text\", \"text\": \"Extrage toate informațiile text din această imagine. Răspunde doar cu textul extras.\"}," +
                            "{\"type\": \"image_url\", \"image_url\": {\"url\": \"data:image/png;base64,%s\"}}" +
                            "]" +
                            "}]," +
                            "\"max_tokens\": 1000" +
                            "}",
                    base64Image
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();

        } catch (Exception e) {
            return "Eroare: " + e.getMessage();
        }
    }

    private static String parseOCRResponse(String jsonResponse) {
        // Parse JSON response to extract text content
        // Implementare simplificată - recomand Jackson pentru parsing detaliat
        if (jsonResponse.contains("\"content\"")) {
            int start = jsonResponse.indexOf("\"content\":\"") + 11;
            int end = jsonResponse.indexOf("\"", start);
            return jsonResponse.substring(start, end).replace("\\n", "\n");
        }
        return "Text negăsit în răspuns";
    }

}