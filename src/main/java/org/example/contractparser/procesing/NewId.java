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

        String[] name = new String[2];

        DetectText detectText = new DetectText();

        List< String> textBlocks = detectText.extractTextLines(imagePath);
        for (int i = 0; i < textBlocks.size(); i++) {
            String word = textBlocks.get(i).trim();

            // Implement the logic to identify and extract relevant information based on the new ID format
            // This may involve checking for specific keywords or patterns in the text blocks

            if(word.contains("CNP:")){
                textMap.put("ɞ", getStringInLine("CNP:", word, textBlocks.get(i+1)));
                System.out.println("CNP-ul este: " + textMap.get("ɞ"));

            }
            else if(word.contains("Data nasterii:")){
                textMap.put("ȕ", getStringInLine("Data nasterii:", word, textBlocks.get(i+1)));
            }
            else if(word.contains("Locul nasterii:")){
                textMap.put("Ȣ", getStringInLine("Locul nasterii:", word, textBlocks.get(i+1)));
            }
             else if(word.contains("Numar document")){
                textMap.put("ɝ", getStringInLine("Numar document:", word, textBlocks.get(i+1)));
            }
             else if(word.contains("Autoritatea")){
                textMap.put("ɟ", textBlocks.get(i+1));
            }
             if(word.contains("Nume de familie:")){
                 name[0] = getStringInLine("Nume de familie:", word, textBlocks.get(i+1));
             }
             if(word.contains("Prenume:")){
                 name[1] = getStringInLine("Prenume:", word, textBlocks.get(i+1));
             }
             if(word.contains("Domiciliu:")){
                 textMap.put("ɠ", textBlocks.get(i+1));
             }
        }


        textMap.put("ɛ", name[0] + " " + name[1]);
        System.out.println("Numele este: " + textMap.get("ɛ"));
        return textMap;
    }

    private String getStringInLine(String placeholder, String s1, String s2){

        if(s1.substring(placeholder.length()).trim().isEmpty()){
           return s2.trim();
        }
        return s2.trim();


    }
}
