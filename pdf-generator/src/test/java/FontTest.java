import com.itextpdf.io.font.FontProgramDescriptor;
import com.itextpdf.io.font.FontProgramDescriptorFactory;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import java.awt.*;
import java.io.IOException;
import java.util.Set;

public class FontTest {
    public static void main(String[] args) throws IOException {


        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] allFonts = e.getAllFonts();
        for (Font font : allFonts) {
            System.out.println();
        }

        PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H");
        System.out.println();

        PdfFont font1 = PdfFontFactory.createFont("HeiseiKakuGo-W5","UniJIS-UCS2-H");
        PdfFont font2 = PdfFontFactory.createFont("HeiseiMin-W3");
        PdfFont font3 = PdfFontFactory.createFont("STSong-Light");

        System.out.println(FontProgramFactory.getRegisteredFonts());
        System.out.println(FontProgramFactory.getRegisteredFontFamilies());
    }
}
