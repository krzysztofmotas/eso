import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class Main {
    public static boolean AUTOMATIC_LOGIN = true; // TODO: do usunięcia po skończeniu projektu

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());

            EventQueue.invokeLater(() -> {
                try {
                    Database.getConnection();
                } catch (SQLException exception) {
                    exception.printStackTrace();

                    JOptionPane.showMessageDialog(
                         null,
                         "Wystąpił błąd podczas łączenia z bazą danych. Wyłączanie aplikacji...",
                         "Błąd",
                         JOptionPane.ERROR_MESSAGE
                    );

                    return;
                }

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