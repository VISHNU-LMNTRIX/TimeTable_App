package core;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

public class CustomFontUtil {
    public static Font loadFont(String fontFileName, float fontSize) {
        try {
            File fontFile = new File(fontFileName);
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            return customFont.deriveFont(fontSize);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, 12); // Fallback font.
        }
    }
}
