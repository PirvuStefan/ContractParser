package org.example.contractparser;


import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class IDCardService {
    public String extractTextFromImage(File imageFile) {
        ITesseract tesseract = new Tesseract();
        // Optionally set language and data path:
        // tesseract.setDatapath("/path/to/tessdata");
        // tesseract.setLanguage("eng");
        try {
            return tesseract.doOCR(imageFile);
        } catch (TesseractException e) {
            e.printStackTrace();
            return "";
        }
    }
}
