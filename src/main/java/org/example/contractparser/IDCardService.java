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
        System.setProperty("jna.library.path", "/opt/homebrew/lib");
        tesseract.setDatapath("/opt/homebrew/share/tessdata");
         tesseract.setLanguage("eng");
         tesseract.setTessVariable("user_defined_dpi", "300");
         tesseract.setOcrEngineMode(1); // 1 = OEM_LSTM_ONLY
         tesseract.setPageSegMode(3);   // 3 = PSM_AUTO
        try {
            return tesseract.doOCR(imageFile);
        } catch (TesseractException e) {
            e.printStackTrace();
            return "";
        }
    }
}
