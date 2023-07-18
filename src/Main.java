import javax.swing.SwingUtilities;

import view.Welcome;

public class Main {
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            new Welcome();
        });
    }
}
