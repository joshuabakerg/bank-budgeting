package za.co.joshuabakerg.bankimport.core.method;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Joshua Baker on 04/03/2020
 */
@Component
public class GetPDFText {

    public String execute(final String file) {
        try {
            final PDDocument doc = PDDocument.load(getClass().getClassLoader().getResourceAsStream(file));
            final PDFTextStripper stripper = new PDFTextStripper();
            final String text = stripper.getText(doc);
            doc.close();
            return text;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
