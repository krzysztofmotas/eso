import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static boolean AUTOMATIC_LOGIN = false; // TODO: do usunięcia po skończeniu projektu

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());

            EventQueue.invokeLater(() -> {
                LoginForm loginForm = new LoginForm(AUTOMATIC_LOGIN);

                if (!AUTOMATIC_LOGIN) {
                    loginForm.setVisible(true);
                }
            });
        } catch (UnsupportedLookAndFeelException exception) {
            exception.printStackTrace();
        }
    }
}