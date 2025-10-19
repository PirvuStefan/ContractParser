package org.example.contractparser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;

// AIzaSyB78GoqSTrfVM7JDrnL7VzkgnWnhSAmUjE

import io.github.cdimascio.dotenv.Dotenv;


public class DeepSeekOCR {

    Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = Dotenv.load().get("DEEPSEEK_API_KEY");
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";

    public static String extractTextWithGoogleVision(String imagePath) {
        try {
            ByteString imgBytes = ByteString.readFrom(Files.newInputStream(Paths.get(imagePath)));

            List<AnnotateImageRequest> requests = new ArrayList<>();
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request =
                    AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
            requests.add(request);

            try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
                AnnotateImageResponse response = client.batchAnnotateImages(requests)
                        .getResponsesList().get(0);
                if (response.hasError()) {
                    return "Error: " + response.getError().getMessage();
                }
                return response.getFullTextAnnotation().getText();
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }

    }
}