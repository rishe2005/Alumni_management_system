import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Main extends JFrame {
    private static final String DB_URL = "jdbc:sqlite:db/alumni.db";

    private JTextField nameField, branchField, yearField, phoneField;
    private JTextArea addressArea;
    private JTable alumniTable;
    private DefaultTableModel tableModel;

    public Main() {
        setTitle("Alumni Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel panel = new JPanel(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        nameField = new JTextField();
        branchField = new JTextField();
        yearField = new JTextField();
        phoneField = new JTextField();
        addressArea = new JTextArea(3, 20);

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Branch:"));
        formPanel.add(branchField);
        formPanel.add(new JLabel("Year of Graduation:"));
        formPanel.add(yearField);
        formPanel.add(new JLabel("Phone Number:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(new JScrollPane(addressArea));

        // Buttons
        JButton addButton = new JButton("Add Alumni");
        JButton deleteButton = new JButton("Delete Alumni");
        JButton displayButton = new JButton("Display Alumni");

        // Creating a separate panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(displayButton);

        // Table to display alumni
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Branch", "Year", "Phone", "Address"}, 0);
        alumniTable = new JTable(tableModel);

        // Adding components to main panel
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(new JScrollPane(alumniTable), BorderLayout.SOUTH);

        add(panel);

        // Event Listeners
        addButton.addActionListener(e -> addAlumni());
        deleteButton.addActionListener(e -> deleteAlumni());
        displayButton.addActionListener(e -> displayAlumni());
    }

    // Method to add a new alumni record
    private void addAlumni() {
        String name = nameField.getText();
        String branch = branchField.getText();
        int year = Integer.parseInt(yearField.getText());
        String phone = phoneField.getText();
        String address = addressArea.getText();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO alumni (name, branch, year_of_graduation, phone_number, address) VALUES (?, ?, ?, ?, ?)")) {

            pstmt.setString(1, name);
            pstmt.setString(2, branch);
            pstmt.setInt(3, year);
            pstmt.setString(4, phone);
            pstmt.setString(5, address);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Alumni added successfully!");

            // Clear input fields
            nameField.setText("");
            branchField.setText("");
            yearField.setText("");
            phoneField.setText("");
            addressArea.setText("");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding alumni.");
        }
    }

    // Method to display all alumni records in the table
    private void displayAlumni() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM alumni")) {

            tableModel.setRowCount(0); // Clear existing rows

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("branch"));
                row.add(rs.getInt("year_of_graduation"));
                row.add(rs.getString("phone_number"));
                row.add(rs.getString("address"));
                tableModel.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error displaying alumni.");
        }
    }

    // Method to delete an alumni record by ID
    private void deleteAlumni() {
        int selectedRow = alumniTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record to delete.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM alumni WHERE id = ?")) {

            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Alumni deleted successfully.");
                displayAlumni(); // Refresh table
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting alumni.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}
