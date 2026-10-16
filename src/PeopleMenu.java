import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class PeopleMenu extends JFrame {
    // Komponen GUI
    public JPanel mainPanel;
    public JLabel titleLabel;
    public JTextField idField;
    public JTextField namaField;
    public JTextField tahunLahirField;
    public JComboBox<String> kategoriComboBox;
    public JRadioButton lakiRadio;       // komponen baru (bukan JTextField)
    public JRadioButton perempuanRadio;  // komponen baru
    public ButtonGroup jenisKelaminGroup;
    public JButton addUpdateButton;
    public JButton deleteButton;
    public JButton cancelButton;
    public JTable peopleTable;

    // Variabel logika
    private ArrayList<Person> listPerson;
    private int selectedIndex = -1;

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
        populateList();
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
                String curJk = peopleTable.getModel().getValueAt(selectedIndex, 5).toString();

                idField.setText(curId);
                namaField.setText(curNama);
                tahunLahirField.setText(curTahun);
                kategoriComboBox.setSelectedItem(curKategori);
                if (curJk.equals("Laki-laki")) {
                    lakiRadio.setSelected(true);
                } else {
                    perempuanRadio.setSelected(true);
                }
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

    public void populateList() {
        listPerson.add(new Person("S001", "Andi Pratama", 2008, "Siswa", "Laki-laki"));
        listPerson.add(new Person("S002", "Siti Nurhaliza", 2009, "Siswa", "Perempuan"));
        listPerson.add(new Person("G001", "Bambang Hartono", 1980, "Guru", "Laki-laki"));
        listPerson.add(new Person("G002", "Rina Marlina", 1985, "Guru", "Perempuan"));
        listPerson.add(new Person("T001", "Dedi Kurniawan", 1990, "Staf TU", "Laki-laki"));
        listPerson.add(new Person("K001", "Hj. Ratna Sari", 1972, "Kepala Sekolah", "Perempuan"));
        listPerson.add(new Person("A001", "Fajar Nugraha", 2001, "Alumni", "Laki-laki"));
    }

    public final DefaultTableModel setTable() {
        Object[] cols = { "No", "ID", "Nama", "Tahun Lahir", "Kategori", "Jenis Kelamin" };
        DefaultTableModel tmp = new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (int i = 0; i < listPerson.size(); i++) {
            Person p = listPerson.get(i);
            Object[] row = {
                    i + 1,
                    p.getId(),
                    p.getNama(),
                    p.getTahunLahir(),
                    p.getKategori(),
                    p.getJenisKelamin()
            };
            tmp.addRow(row);
        }
        return tmp;
    }

    private String getJenisKelamin() {
        if (lakiRadio.isSelected()) return "Laki-laki";
        if (perempuanRadio.isSelected()) return "Perempuan";
        return null;
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
        if (getJenisKelamin() == null) {
            return "Silakan pilih jenis kelamin!";
        }
        return null;
    }

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

        listPerson.add(new Person(id, nama, tahun, kategori, getJenisKelamin()));
        peopleTable.setModel(setTable());
        clearForm();
        JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan");
    }

    public void updateData() {
        String error = validateInput();
        if (error != null) {
            JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Person p = listPerson.get(selectedIndex);
        p.setId(idField.getText().trim());
        p.setNama(namaField.getText().trim());
        p.setTahunLahir(Integer.parseInt(tahunLahirField.getText().trim()));
        p.setKategori(kategoriComboBox.getSelectedItem().toString());
        p.setJenisKelamin(getJenisKelamin());

        peopleTable.setModel(setTable());
        clearForm();
        JOptionPane.showMessageDialog(null, "Data berhasil diubah");
    }

    public void deleteData() {
        listPerson.remove(selectedIndex);
        peopleTable.setModel(setTable());
        clearForm();
        JOptionPane.showMessageDialog(null, "Data berhasil dihapus");
    }

    public void clearForm() {
        idField.setText("");
        namaField.setText("");
        tahunLahirField.setText("");
        kategoriComboBox.setSelectedIndex(0);
        jenisKelaminGroup.clearSelection();
        addUpdateButton.setText("Add");
        deleteButton.setVisible(false);
        peopleTable.clearSelection();
        selectedIndex = -1;
    }
}
