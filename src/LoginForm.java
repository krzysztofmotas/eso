import javax.swing.*;
import java.sql.*;

public class LoginForm extends JFrame {
    private JPanel mainPanel;
    private JButton loginButton;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel logoLabel;

    public LoginForm() {
        setTitle("Logowanie");
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(mainPanel);
        setSize(325, 325);

        Utilities.centreWindow(this);
        Utilities.setLabelIconToProjectLogo(logoLabel, 0.15f);
        Utilities.setIconToProjectIcon(this);

        // umożliwienie zatwierdzenia danych poprzez klawisz enter
        getRootPane().setDefaultButton(loginButton);

        loginButton.addActionListener(e -> {
            String emailAddress = emailField.getText();

            if (!Utilities.emailPatternMatches(emailAddress)) {
                JOptionPane.showMessageDialog(
                        this,
                        """
                            Podano nieprawidłowy format adresu e-mail!
                            Przykład: name@example.com
                            
                            (maksymalnie 100 znaków)
                        """,
                        "Błąd logowania",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            String password = String.valueOf(passwordField.getPassword());

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
                exception.printStackTrace();

                JOptionPane.showMessageDialog(
                        this,
                        "Wystąpił błąd i logowanie nie mogło dojść do skutku.",
                        "Błąd logowania",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}