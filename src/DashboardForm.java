import javax.swing.*;

public class DashboardForm extends JFrame {
    private JPanel mainPanel;

    public DashboardForm(User user) {
        setTitle("Elektroniczny system oceniania");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(mainPanel);
        pack();
    }
}
