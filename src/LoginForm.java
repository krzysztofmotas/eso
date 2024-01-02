import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class LoginForm extends JFrame {
    private static final String EMAIL_PLACEHOLDER = "Podaj adres e-mail",
                                PASSWORD_PLACEHOLDER = "Podaj hasło";

    private boolean emailFieldHasPlaceholder = true,
                    passwordFieldHasPlaceholder = true;

    private JPanel mainPanel;
    private JButton loginButton;
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginForm() {
        setTitle("Elektroniczny system oceniania");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(mainPanel);
        pack();

        // usunięcie domyślnych obramowań pól tekstowych i dodanie odstępu tekstu od lewej strony
        Border fieldsBorder = BorderFactory.createEmptyBorder(0, 20, 0, 0);
        emailField.setBorder(fieldsBorder);
        passwordField.setBorder(fieldsBorder);

        emailField.setText(EMAIL_PLACEHOLDER);
        passwordField.setText(PASSWORD_PLACEHOLDER);
        passwordField.setEchoChar((char) 0);

        emailField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (emailFieldHasPlaceholder) {
                    emailField.setText("");
                    emailFieldHasPlaceholder = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (emailField.getText().isBlank()) {
                    emailField.setText(EMAIL_PLACEHOLDER);
                    emailFieldHasPlaceholder = true;
                }
            }
        });

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (passwordFieldHasPlaceholder) {
                    passwordField.setText("");
                    passwordField.setEchoChar('•');
                    passwordFieldHasPlaceholder = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (passwordField.getText().isBlank()) {
                    passwordField.setText(PASSWORD_PLACEHOLDER);
                    passwordField.setEchoChar((char) 0);
                    passwordFieldHasPlaceholder = true;
                }
            }
        });
    }
}
