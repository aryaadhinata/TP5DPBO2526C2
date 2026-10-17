import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PeopleMenu extends JFrame {
    // Komponen GUI (terikat ke PeopleMenu.form)
    public JPanel mainPanel;
    public JLabel titleLabel;
    public JTextField idField;
    public JTextField namaField;
    public JTextField tahunLahirField;
    public JComboBox<String> kategoriComboBox;
    public JCheckBox lakiCheckBox;       // komponen baru (bukan JTextField)
    public JCheckBox perempuanCheckBox;  // komponen baru
    public JButton addUpdateButton;
    public JButton deleteButton;
    public JButton cancelButton;
    public JTable peopleTable;
    private JTextField emailField;

    // Variabel logika
    private int selectedIndex = -1;
    private String selectedId = null; // ID asli baris yang dipilih (untuk WHERE saat update/delete)
    private Database database;

    public static void main(String[] args) {
        PeopleMenu menu = new PeopleMenu();
        menu.setSize(800, 650);
        menu.setLocationRelativeTo(null);
        menu.setContentPane(menu.mainPanel);
        menu.getContentPane().setBackground(Color.WHITE);
        menu.setVisible(true);
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public PeopleMenu() {
        // Inisialisasi koneksi database & tampilkan data
        database = new Database();
        peopleTable.setModel(setTable());
        deleteButton.setVisible(false);

        // --- EVENT LISTENERS ---
        // CheckBox jenis kelamin: hanya boleh satu yang dicentang
        lakiCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lakiCheckBox.isSelected()) {
                    perempuanCheckBox.setSelected(false);
                }
            }
        });

        perempuanCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (perempuanCheckBox.isSelected()) {
                    lakiCheckBox.setSelected(false);
                }
            }
        });

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
                String curJk = peopleTable.getModel().getValueAt(selectedIndex, 5).toString();
                String curEmail = peopleTable.getModel().getValueAt(selectedIndex, 6).toString();

                selectedId = curId;
                idField.setText(curId);
                namaField.setText(curNama);
                tahunLahirField.setText(curTahun);
                kategoriComboBox.setSelectedItem(curKategori);
                lakiCheckBox.setSelected(curJk.equals("Laki-laki"));
                perempuanCheckBox.setSelected(curJk.equals("Perempuan"));
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

    // READ: ambil seluruh data dari database dan bentuk model tabel
    public final DefaultTableModel setTable() {
        Object[] cols = { "No", "ID", "Nama", "Tahun Lahir", "Kategori", "Jenis Kelamin", "Email" };
        DefaultTableModel tmp = new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            ResultSet resultSet = database.selectQuery("SELECT * FROM person");

            int i = 0;
            while (resultSet.next()) {
                Object[] row = new Object[7];
                row[0] = i + 1;
                row[1] = resultSet.getString("id");
                row[2] = resultSet.getString("nama");
                row[3] = resultSet.getString("thn_lahir");
                row[4] = resultSet.getString("kategori");
                row[5] = resultSet.getString("jns_kelamin");
                row[6] = resultSet.getString("email") == null ? "" : resultSet.getString("email");
                tmp.addRow(row);
                i++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return tmp;
    }

    // Menghindari error SQL akibat tanda petik pada input
    private String esc(String s) {
        return s.replace("'", "''");
    }

    private String getJenisKelamin() {
        if (lakiCheckBox.isSelected()) return "Laki-laki";
        if (perempuanCheckBox.isSelected()) return "Perempuan";
        return null;
    }

    // Cek apakah ID sudah ada di database
    private boolean isIdExists(String id) {
        try {
            ResultSet rs = database.selectQuery("SELECT id FROM person WHERE id='" + esc(id) + "'");
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Validasi input; mengembalikan pesan error atau null jika valid
    private String validateInput() {
        // 1. Cek kolom yang masih kosong
        StringBuilder kosong = new StringBuilder();
        if (idField.getText().trim().isEmpty()) kosong.append("ID, ");
        if (namaField.getText().trim().isEmpty()) kosong.append("Nama, ");
        if (tahunLahirField.getText().trim().isEmpty()) kosong.append("Tahun Lahir, ");
        if (kategoriComboBox.getSelectedIndex() == 0) kosong.append("Kategori, ");
        if (getJenisKelamin() == null) kosong.append("Jenis Kelamin, ");
        if (emailField.getText().trim().isEmpty()) kosong.append("Email, ");
        if (kosong.length() > 0) {
            kosong.setLength(kosong.length() - 2); // buang koma terakhir
            return "Kolom berikut masih kosong:\n" + kosong;
        }

        // 2. Cek format
        try {
            Integer.parseInt(tahunLahirField.getText().trim());
        } catch (NumberFormatException ex) {
            return "Tahun lahir harus berupa angka!";
        }
        if (!emailField.getText().trim().contains("@")) {
            return "Email tidak valid (harus berisi @)!";
        }
        return null;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // CREATE
    public void insertData() {
        String error = validateInput();
        if (error != null) {
            showError(error);
            return;
        }
        String id = idField.getText().trim();
        String nama = namaField.getText().trim();
        int tahun = Integer.parseInt(tahunLahirField.getText().trim());
        String kategori = kategoriComboBox.getSelectedItem().toString();
        String jk = getJenisKelamin();
        String email = emailField.getText().trim();

        // Cek ID ganda
        if (isIdExists(id)) {
            showError("ID \"" + id + "\" sudah digunakan! Gunakan ID yang berbeda.");
            return;
        }

        String sqlQuery = "INSERT INTO person (id, nama, thn_lahir, kategori, jns_kelamin, email) VALUES ('"
                + esc(id) + "', '" + esc(nama) + "', " + tahun + ", '" + esc(kategori) + "', '"
                + esc(jk) + "', '" + esc(email) + "')";

        try {
            database.insertUpdateDeleteQuery(sqlQuery);
        } catch (RuntimeException ex) {
            showError("Gagal menambah data:\n" + ex.getMessage());
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
            showError(error);
            return;
        }
        String id = idField.getText().trim();
        String nama = namaField.getText().trim();
        int tahun = Integer.parseInt(tahunLahirField.getText().trim());
        String kategori = kategoriComboBox.getSelectedItem().toString();
        String jk = getJenisKelamin();
        String email = emailField.getText().trim();

        // Jika ID diubah, pastikan ID baru belum dipakai data lain
        if (!id.equals(selectedId) && isIdExists(id)) {
            showError("ID \"" + id + "\" sudah digunakan! Gunakan ID yang berbeda.");
            return;
        }

        String sqlQuery = "UPDATE person SET id='" + esc(id) + "', nama='" + esc(nama) + "', thn_lahir=" + tahun
                + ", kategori='" + esc(kategori) + "', jns_kelamin='" + esc(jk) + "', email='" + esc(email)
                + "' WHERE id='" + esc(selectedId) + "'";

        try {
            database.insertUpdateDeleteQuery(sqlQuery);
        } catch (RuntimeException ex) {
            showError("Gagal mengubah data:\n" + ex.getMessage());
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
            showError("Gagal menghapus data:\n" + ex.getMessage());
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
        lakiCheckBox.setSelected(false);
        perempuanCheckBox.setSelected(false);
        emailField.setText("");
        addUpdateButton.setText("Add");
        deleteButton.setVisible(false);
        peopleTable.clearSelection();
        selectedIndex = -1;
        selectedId = null;
    }
}