import javax.swing.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginForm extends JFrame {
    private static final String EMAIL_PLACEHOLDER = "Podaj adres e-mail",
                                PASSWORD_PLACEHOLDER = "Podaj hasło";

    private boolean emailFieldHasPlaceholder = true,
                    passwordFieldHasPlaceholder = true;

    private JPanel mainPanel;
    private JButton loginButton;
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginForm(boolean automaticLogin) {
        setTitle("Elektroniczny system oceniania");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(mainPanel);
        setSize(300, 300);

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
                if (new String(passwordField.getPassword()).isBlank()) {
                    passwordField.setText(PASSWORD_PLACEHOLDER);
                    passwordField.setEchoChar((char) 0);
                    passwordFieldHasPlaceholder = true;
                }
            }
        });


        // umożliwienie zatwierdzenia danych poprzez klawisz enter
        getRootPane().setDefaultButton(loginButton);

        loginButton.addActionListener(e -> {
            if (emailFieldHasPlaceholder || passwordFieldHasPlaceholder) {
                return;
            }

            String emailAddress = emailField.getText();

            if (!Utilities.emailPatternMatches(emailAddress)) {
                JOptionPane.showMessageDialog(
                        this,
                        """
                            Podano nieprawidłowy format adresu e-mail!
                            Przykład: example@gmail.com
                            
                            (maksymalnie 100 znaków)
                        """,
                        "Błąd logowania",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            String password = new String(passwordField.getPassword());

            if (!Utilities.passwordPatternMatches(password)) {
                JOptionPane.showMessageDialog(
                        this,

                        // https://www.baeldung.com/java-text-blocks
                        """
                            Podano nieprawidłowy format hasła!

                            Prawidłowe hasło posiada:
                            - minimum 8 znaków
                            - maksymalnie 100 znaków
                            - conajmniej jedną literę
                            - conajmniej jedną cyfrę
                        """,
                        "Błąd logowania",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            try {
                Connection connection = Database.getConnection();

                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM accounts WHERE email = ? AND password = ?");
                preparedStatement.setString(1, emailAddress);
                preparedStatement.setString(2, password);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (!resultSet.next()) { // nie znaleziono żadnego użytkownika
                    JOptionPane.showMessageDialog(
                            this,
                            "Podano nieprawidłowy adres e-mail lub hasło.",
                            "Błąd logowania",
                            JOptionPane.ERROR_MESSAGE
                    );

                    return;
                }
                dispose();

                Role role = Role.fromInt(resultSet.getInt("role"));

                if (role == null) {
                    throw new NullPointerException();
                }

                User user = new User(
                        resultSet.getInt("id"),
                        emailAddress,
                        resultSet.getString("name"),
                        resultSet.getString("surname"),
                        role
                );

                DashboardForm dashboardForm = new DashboardForm(user);
                dashboardForm.setVisible(true);

                preparedStatement.close();
                connection.close();
            } catch (SQLException | NullPointerException exception) {
                JOptionPane.showMessageDialog(
                        this,
                        "Wystąpił błąd i logowanie nie mogło dojść do skutku.",
                        "Błąd logowania",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        if (automaticLogin) {
            emailField.setText("adam.nowak@wp.pl");
            passwordField.setText("adamnowak12345");

            emailFieldHasPlaceholder = false;
            passwordFieldHasPlaceholder = false;

            loginButton.doClick();
        }
    }
}