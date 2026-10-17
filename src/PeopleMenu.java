import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PeopleMenu extends JFrame {
    // Komponen GUI (terikat ke PeopleMenu.form)
    public JPanel mainPanel;
    public JLabel titleLabel;
    public JTextField idField;
    public JTextField namaField;
    public JTextField tahunLahirField;
    public JComboBox<String> kategoriComboBox;
    public JButton addUpdateButton;
    public JButton deleteButton;
    public JButton cancelButton;
    public JTable peopleTable;
    private JTextField emailField;

    // Variabel logika
    private ArrayList<Person> listPerson;
    private int selectedIndex = -1;
    private String selectedId = null; // ID asli baris yang dipilih (untuk WHERE saat update/delete)
    private Database database;

    public static void main(String[] args) {
        PeopleMenu menu = new PeopleMenu();
        menu.setSize(800, 600);
        menu.setLocationRelativeTo(null);
        menu.setContentPane(menu.mainPanel);
        menu.getContentPane().setBackground(Color.WHITE);
        menu.setVisible(true);
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public PeopleMenu() {
        // Inisialisasi Data & Logika
        listPerson = new ArrayList<>();
        database = new Database();
        peopleTable.setModel(setTable());
        deleteButton.setVisible(false);

        // --- EVENT LISTENERS ---
        peopleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = peopleTable.getSelectedRow();
                if (row == -1) {
                    return;
                }
                selectedIndex = row;
                String curId = peopleTable.getModel().getValueAt(selectedIndex, 1).toString();
                String curNama = peopleTable.getModel().getValueAt(selectedIndex, 2).toString();
                String curTahun = peopleTable.getModel().getValueAt(selectedIndex, 3).toString();
                String curKategori = peopleTable.getModel().getValueAt(selectedIndex, 4).toString();
                String curEmail = peopleTable.getModel().getValueAt(selectedIndex, 5).toString();

                selectedId = curId;
                idField.setText(curId);
                namaField.setText(curNama);
                tahunLahirField.setText(curTahun);
                kategoriComboBox.setSelectedItem(curKategori);
                emailField.setText(curEmail);
                addUpdateButton.setText("Update");
                deleteButton.setVisible(true);
            }
        });

        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex == -1) {
                    insertData();
                } else {
                    updateData();
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Confirmation prompt sebelum data dihapus
                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Apakah Anda yakin ingin menghapus data ini?",
                        "Konfirmasi Hapus",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteData();
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
    }

    // READ: ambil seluruh data dari database, simpan ke listPerson, lalu bentuk model tabel
    public final DefaultTableModel setTable() {
        Object[] cols = { "No", "ID", "Nama", "Tahun Lahir", "Kategori", "Email" };
        DefaultTableModel tmp = new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        listPerson.clear();
        try {
            ResultSet resultSet = database.selectQuery("SELECT * FROM person");
            while (resultSet.next()) {
                listPerson.add(new Person(
                        resultSet.getString("id"),
                        resultSet.getString("nama"),
                        resultSet.getInt("thn_lahir"),
                        resultSet.getString("kategori"),
                        resultSet.getString("email")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < listPerson.size(); i++) {
            Person p = listPerson.get(i);
            Object[] row = {
                    i + 1,
                    p.getId(),
                    p.getNama(),
                    p.getTahunLahir(),
                    p.getKategori(),
                    p.getEmail() == null ? "" : p.getEmail()
            };
            tmp.addRow(row);
        }
        return tmp;
    }

    // Menghindari error SQL akibat tanda petik pada input
    private String esc(String s) {
        return s.replace("'", "''");
    }

    // Validasi input; mengembalikan pesan error atau null jika valid
    private String validateInput() {
        if (idField.getText().trim().isEmpty() || namaField.getText().trim().isEmpty()) {
            return "ID dan Nama tidak boleh kosong!";
        }
        try {
            Integer.parseInt(tahunLahirField.getText().trim());
        } catch (NumberFormatException ex) {
            return "Tahun lahir harus berupa angka!";
        }
        if (kategoriComboBox.getSelectedIndex() == 0) {
            return "Silakan pilih kategori!";
        }
        String email = emailField.getText().trim();
        if (email.isEmpty() || !email.contains("@")) {
            return "Email tidak valid (harus berisi @)!";
        }
        return null;
    }

    // CREATE
    public void insertData() {
        String error = validateInput();
        if (error != null) {
            JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String id = idField.getText().trim();
        String nama = namaField.getText().trim();
        int tahun = Integer.parseInt(tahunLahirField.getText().trim());
        String kategori = kategoriComboBox.getSelectedItem().toString();
        String email = emailField.getText().trim();

        String sqlQuery = "INSERT INTO person (id, nama, thn_lahir, kategori, email) VALUES ('"
                + esc(id) + "', '" + esc(nama) + "', " + tahun + ", '" + esc(kategori) + "', '" + esc(email) + "')";

        try {
            database.insertUpdateDeleteQuery(sqlQuery);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null, "Gagal menambah data (ID mungkin sudah ada):\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        peopleTable.setModel(setTable());
        clearForm();
        JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan");
    }

    // UPDATE
    public void updateData() {
        String error = validateInput();
        if (error != null) {
            JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String id = idField.getText().trim();
        String nama = namaField.getText().trim();
        int tahun = Integer.parseInt(tahunLahirField.getText().trim());
        String kategori = kategoriComboBox.getSelectedItem().toString();
        String email = emailField.getText().trim();

        String sqlQuery = "UPDATE person SET id='" + esc(id) + "', nama='" + esc(nama) + "', thn_lahir=" + tahun
                + ", kategori='" + esc(kategori) + "', email='" + esc(email) + "' WHERE id='" + esc(selectedId) + "'";

        try {
            database.insertUpdateDeleteQuery(sqlQuery);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null, "Gagal mengubah data:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        peopleTable.setModel(setTable());
        clearForm();
        JOptionPane.showMessageDialog(null, "Data berhasil diubah");
    }

    // DELETE
    public void deleteData() {
        String sqlQuery = "DELETE FROM person WHERE id='" + esc(selectedId) + "'";

        try {
            database.insertUpdateDeleteQuery(sqlQuery);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus data:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        peopleTable.setModel(setTable());
        clearForm();
        JOptionPane.showMessageDialog(null, "Data berhasil dihapus");
    }

    public void clearForm() {
        idField.setText("");
        namaField.setText("");
        tahunLahirField.setText("");
        kategoriComboBox.setSelectedIndex(0);
        emailField.setText("");
        addUpdateButton.setText("Add");
        deleteButton.setVisible(false);
        peopleTable.clearSelection();
        selectedIndex = -1;
        selectedId = null;
    }
}