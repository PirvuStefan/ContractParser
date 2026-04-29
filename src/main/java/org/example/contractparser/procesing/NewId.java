package org.example.contractparser.procesing;

import org.example.contractparser.DetectText;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewId implements UserMapParser{

    @Override
    public Map<String, String> extractMap(String imagePath) throws IOException {

        Map<String, String> textMap = new HashMap<>();

        DetectText detectText = new DetectText();

        List< String> textBlocks = detectText.extractTextLines(imagePath);
        for (int i = 0; i < textBlocks.size(); i++) {
            String word = textBlocks.get(i).trim();

            // Implement the logic to identify and extract relevant information based on the new ID format
            // This may involve checking for specific keywords or patterns in the text blocks

            // Example:
            if(word.contains("NewIDKeyword")){
                // Extract and put relevant information into textMap
                textMap.put("key", "value");
            }
        }

        return textMap;
    }
}
