public class Main {
    public static boolean AUTOMATIC_LOGIN = true; // TODO: do usunięcia po skończeniu projektu

    public static void main(String[] args) {
        LoginForm loginForm = new LoginForm(AUTOMATIC_LOGIN);

        if (!AUTOMATIC_LOGIN) {
            loginForm.setVisible(true);
        }
    }
}