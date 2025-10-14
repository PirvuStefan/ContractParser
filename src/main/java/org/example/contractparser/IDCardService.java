package org.example.contractparser;


import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

public class IDCardService {

    public File preprocessImage(File inputFile) {
        Mat src = Imgcodecs.imread(inputFile.getAbsolutePath());
        // Resize (400%)
        Imgproc.resize(src, src, new Size(src.width() * 4, src.height() * 4));
        // Blur
        Imgproc.GaussianBlur(src, src, new Size(5, 5), 0);
        // Threshold
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(src, src, 140, 255, Imgproc.THRESH_BINARY);

        File outFile = new File("processed.png");
        Imgcodecs.imwrite(outFile.getAbsolutePath(), src);
        return outFile;
    }

    public String extractTextFromImage(File imageFile) {
        imageFile = preprocessImage(imageFile);
        ITesseract tesseract = new Tesseract();
        // Optionally set language and data path:
        // tesseract.setDatapath("/path/to/tessdata");
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
        System.setProperty("java.library.path", "/opt/homebrew/lib");
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
