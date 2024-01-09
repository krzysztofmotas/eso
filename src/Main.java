public class Main {
    public static boolean AUTOMATIC_LOGIN = false; // TODO: do usunięcia po skończeniu projektu

    public static void main(String[] args) {
        LoginForm loginForm = new LoginForm();

        if (!AUTOMATIC_LOGIN) {
            loginForm.setVisible(true);
        }
    }
}