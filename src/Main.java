import java.awt.Font;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import core.CustomFontUtil;
import view.Welcome;

public class Main {
    public static void main(String[] args){
        //Custom fonts used within the application
        Font interBold = CustomFontUtil.loadFont("src/resources/fonts/Inter-SemiBold.otf", 12);

        UIManager.put("Menu.font", interBold);
        UIManager.put("MenuItem.font", interBold);
        UIManager.put("Button.font", interBold);
        UIManager.put("ComboBox.font", interBold);
        UIManager.put("Label.font", interBold);

        SwingUtilities.invokeLater(() -> {
            new Welcome();
        });
    }
}
