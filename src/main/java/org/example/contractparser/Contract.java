package org.example.contractparser;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class Contract {

    public static void generateContract(String templatePath, String outputPath, Map<String, String> data) throws IOException {
        try (FileInputStream fis = new FileInputStream(templatePath);
             XWPFDocument document = new XWPFDocument(fis)) {

            // Replace placeholders in paragraphs
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.getText(0);
                    if (text != null) {
                        for (Map.Entry<String, String> entry : data.entrySet()) {
                            text = text.replace(entry.getKey(), entry.getValue());
                        }
                        run.setText(text, 0);
                    }
                    System.out.println(run.getText(0)); // Debugging line to print run text
                }
            }

            System.out.println("Finished replacing placeholders in paragraphs.\n\n\n\n"); // Debugging line

            // Replace placeholders in tables too (many contracts use tables)
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            for (XWPFRun run : paragraph.getRuns()) {
                                String text = run.getText(0);
                                if (text != null) {
                                    for (Map.Entry<String, String> entry : data.entrySet()) {
                                        text = text.replace(entry.getKey(), entry.getValue());
                                    }
                                    run.setText(text, 0);
                                }
                                System.out.println(run.getText(0)); // Debugging line to print run text
                            }
                        }
                    }
                }
            }

            // Save the new document
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                document.write(fos);
            }
        }
    }
}
