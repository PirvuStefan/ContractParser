package org.example.contractparser;
package org.apache.poi.xwpf.usermodel;

import java.io.File;
import java.util.Map;

public class Contract {


    private File generatedContract(Map< String, String> extractedData) {
        // Implement contract generation logic here
        File arhivaDir = new File("arhiva");
        if (!arhivaDir.exists()) {
            arhivaDir.mkdirs();
        }


try {
            File templateFile = new File("contract.docx");
            File outputFile = new File(arhivaDir, "generated_contract.docx");

            org.apache.poi.xwpf.usermodel.XWPFDocument doc =
                new org.apache.poi.xwpf.usermodel.XWPFDocument(
                    new java.io.FileInputStream(templateFile)
                );

            for (org.apache.poi.xwpf.usermodel.XWPFParagraph p : doc.getParagraphs()) {
                for (Map.Entry<String, String> entry : extractedData.entrySet()) {
                    String placeholder = entry.getKey();
                    String value = entry.getValue();
                    String text = p.getText();
                    if (text.contains(placeholder)) {
                        for (org.apache.poi.xwpf.usermodel.XWPFRun run : p.getRuns()) {
                            String runText = run.getText(0);
                            if (runText != null && runText.contains(placeholder)) {
                                run.setText(runText.replace(placeholder, value), 0);
                            }
                        }
                    }
                }
            }

            try (java.io.FileOutputStream out = new java.io.FileOutputStream(outputFile)) {
                doc.write(out);
            }
            doc.close();
            return outputFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }
}
