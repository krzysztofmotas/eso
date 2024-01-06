import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class DashboardForm extends JFrame {
    private JPanel mainPanel, addGradesPanel;
    private JTabbedPane tabbedPane;
    private JLabel nameLabel, emailLabel, roleNameLabel;
    private JTable gradesTable, studentsTable;
    private JScrollPane gradesScrollPane;
    private JButton confirmAddGradesButton;
    private JComboBox<String> subjectTypeComboBox, gradesTypeComboBox;
    private final User user;

    public DashboardForm(User user) {
        this.user = user;

        setTitle("Elektroniczny system oceniania");
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(mainPanel);
        pack();

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
                tabbedPane.addTab("Wstawianie ocen", addGradesPanel);
                updateAddGradesPanel();
            }

            case ADMIN -> {

            }
        }
    }

    private static final String[] gradeTypesList = {
            "Praca domowa",
            "Kolokwium",
            "Projekt",
            "Aktywność",
            "Egzamin",
            "Inne"
    };

    private static final double[] gradesList = {
            2.0,
            3.0,
            3.5,
            4.0,
            4.5,
            5.0
    };

    private void updateAddGradesPanel() {
        gradesTypeComboBox.removeAllItems();

        for (String type : gradeTypesList) {
            gradesTypeComboBox.addItem(type);
        }

        gradesTypeComboBox.setSelectedIndex(-1);

        try {
            Connection connection = Database.getConnection();
            Statement statement = connection.createStatement();

            ResultSet subjectsResultSet = statement.executeQuery("SELECT * FROM subjects");

            subjectTypeComboBox.removeAllItems();
            while (subjectsResultSet.next()) {
                // pasowałoby dodać pobieranie id, żeby obeszło się bez wydłużania zapytania sql
                // w celu wyszukania id przedmiotu po jego nazwie
                subjectTypeComboBox.addItem(subjectsResultSet.getString("name"));
            }

            subjectTypeComboBox.setSelectedIndex(-1);

            DefaultTableModel defaultTableModel = new DefaultTableModel(
                    new String[] {
                            "Nr indeksu",
                            "Imię i nazwisko",
                            "Nowa ocena"
                    },
                    0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 2; // 2 - nowa ocena
                }
            };

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, name, surname FROM accounts WHERE role = ?");
            preparedStatement.setInt(1, Role.STUDENT.getId());

            ResultSet studentsResultSet = preparedStatement.executeQuery();
            while (studentsResultSet.next()) {
                defaultTableModel.addRow(
                        new String[] {
                                String.valueOf(studentsResultSet.getInt("id")),
                                studentsResultSet.getString("name") + " " + studentsResultSet.getString("surname")
                        }
                );
            }

            JComboBox<Double> gradeComboBox = new JComboBox<>();

            for (double grade : gradesList) {
                gradeComboBox.addItem(grade);
            }

            studentsTable.setModel(defaultTableModel);
            studentsTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(gradeComboBox));

            preparedStatement.close();
            statement.close();
            connection.close();
        } catch (SQLException exception) {
            // JOptionPane...
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