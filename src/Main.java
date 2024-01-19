import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("TabbedPane.showTabSeparators", true);
            UIManager.put("TabbedPane.tabSeparatorsFullHeight", true);
            UIManager.put("TabbedPane.selectedBackground", new Color(222, 230, 237));

            EventQueue.invokeLater(() -> {
                try {
                    Connection connection = Database.getConnection();
                    connection.close();
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

                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            });
        } catch (UnsupportedLookAndFeelException exception) {
            exception.printStackTrace();
        }
    }
}