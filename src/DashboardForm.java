import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardForm extends JFrame {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private JLabel nameLabel, emailLabel, roleNameLabel;
    private JTable gradesTable;
    private JScrollPane gradesScrollPane;
    private final User user;

    public DashboardForm(User user) {
        this.user = user;

        setTitle("Elektroniczny system oceniania");
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(mainPanel);

        nameLabel.setText(user.getName() + " " + user.getSurname());
        emailLabel.setText(user.getEmailAddress());
        roleNameLabel.setText(user.getRole().getName());

        tabbedPane.removeAll();
        switch (user.getRole()) {
            case STUDENT -> {
                tabbedPane.addTab("Moje oceny", gradesScrollPane);
                updateGradesTable();
            }
            case TEACHER -> {

            }

            case ADMIN -> {

            }
        }
    }

    private void updateGradesTable() {
        try {
            Connection connection = Database.getConnection();

            String sql = """
                    SELECT grades.*, accounts.name, accounts.surname, subjects.name AS subject_name FROM grades
                    JOIN accounts ON accounts.id = grades.teacher_id
                    JOIN subjects ON subjects.id = grades.subject_id
                    WHERE grades.student_id = ?
                    ORDER BY grades.subject_id, grades.date DESC
            """;

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, user.getId());

            DefaultTableModel defaultTableModel = new DefaultTableModel(
                    new String[] {
                            "Lp.",
                            "Przedmiot",
                            "Nauczyciel",
                            "Data wstawienia",
                            "Typ oceny",
                            "Ocena"
                    },
                    0 // rowCount
            );

            ResultSet resultSet = preparedStatement.executeQuery();

            int lp = 1;
            while (resultSet.next()) {
                defaultTableModel.addRow(
                        new String[] {
                                String.format("%d.", lp),
                                resultSet.getString("subject_name"),
                                resultSet.getString("name") + " " + resultSet.getString("surname"),
                                resultSet.getString("date"),
                                resultSet.getString("type"),
                                resultSet.getString("grade")
                        }
                );

                lp++;
            }
            gradesTable.setModel(defaultTableModel);

            preparedStatement.close();
            connection.close();
        } catch (SQLException exception) {
            // exception.printStackTrace();
        }
    }
}