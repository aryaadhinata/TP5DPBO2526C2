# PeopleData – Manajemen Data Sekolah (Java Swing & MySQL)

Aplikasi CRUD (Create, Read, Update, Delete) berbasis Java Swing dengan tema **People** pada konteks **sekolah**. Program ini telah diintegrasikan dengan database MySQL menggunakan JDBC untuk menyimpan data secara permanen.

## Janji

```text
Saya Mohammad Arya Dhinata dengan NIM 2504992 mengerjakan Tugas Praktikum 5 dalam mata kuliah Desain
Pemrograman Berbasis Objek untuk keberkahan-Nya maka saya tidak melakukan kecurangan seperti
yang di spesifikasikan. Aamiin
```

---

## i. Desain Program

### Arsitektur dan Komponen
Program ini dirancang menggunakan arsitektur antarmuka GUI berbasis Java Swing dengan koneksi langsung ke Database MySQL (JDBC).
1. **`Person.java`**: Class model yang merepresentasikan entitas warga sekolah.
2. **`Database.java`**: Class *helper* untuk mengelola koneksi database `db_people` dan mengeksekusi *query* SQL (`selectQuery` dan `insertUpdateDeleteQuery`).
3. **`PeopleMenu.java` & `PeopleMenu.form`**: Antarmuka pengguna utama yang berisi form input dan tabel tampilan data.

### Desain Database (`db_people.person`)
Tabel `person` dirancang untuk menampung data dengan struktur berikut:
*   `id` (VARCHAR) - Primary Key
*   `nama` (VARCHAR)
*   `thn_lahir` (INT)
*   `kategori` (ENUM: Siswa, Guru, Staf TU, Kepala Sekolah, Alumni)
*   `jns_kelamin` (VARCHAR)
*   `email` (VARCHAR)

### Desain Antarmuka (GUI)
| Atribut | Tipe Komponen GUI | Keterangan / Validasi |
|---|---|---|
| **ID** | `JTextField` (`idField`) | Wajib diisi, harus unik (Primary Key). |
| **Nama** | `JTextField` (`namaField`) | Wajib diisi. |
| **Tahun Lahir** | `JTextField` (`tahunLahirField`) | Wajib diisi, divalidasi harus berupa **angka**. |
| **Kategori** | `JComboBox` (`kategoriComboBox`) | Pilihan drop-down warga sekolah. |
| **Jenis Kelamin** | `JCheckBox` (`lakiCheckBox`, `perempuanCheckBox`) | Wajib dipilih salah satu (Logika mutual-eksklusif diatur via *ActionListener*). |
| **Email** | `JTextField` (`emailField`) | Wajib diisi, divalidasi harus mengandung karakter `@`. |

Terdapat 3 tombol aksi:
*   **Add / Update**: Tombol dinamis. Berfungsi menambah data saat mode *insert*, dan berubah menjadi *update* saat baris tabel diklik.
*   **Delete**: Disembunyikan secara default. Muncul hanya saat baris tabel dipilih untuk menghapus data.
*   **Cancel**: Membatalkan pilihan, mengosongkan form, dan mengembalikan mode ke awal.

---

## ii. Penjelasan Alur

Berikut adalah alur logika untuk operasi pada program:

### 1. Inisialisasi & Read (Tampil Data)
1. Saat program dijalankan (`PeopleMenu` diinstansiasi), sistem memanggil class `Database` untuk membuka koneksi ke MySQL.
2. Method `setTable()` dieksekusi: Mengirim query `SELECT * FROM person`.
3. Hasil dari *ResultSet* dilooping dan dimasukkan ke dalam `DefaultTableModel`.
4. Model ini dipasang ke `JTable` untuk menampilkan semua data ke layar.

### 2. Create (Tambah Data)
1. Pengguna mengisi semua komponen input pada form lalu menekan tombol **Add**.
2. **Validasi Input**:
   * Program mengecek apakah ada field yang kosong. Jika ya, muncul *Prompt Error* daftar field yang kosong.
   * Mengecek apakah *Tahun Lahir* adalah angka bulat.
   * Mengecek apakah *Email* memiliki tanda `@`.
3. **Validasi Database**: Sistem mengecek apakah `ID` sudah ada di tabel database (Mencegah duplikasi PK).
4. Jika semua valid, query `INSERT INTO person ...` dieksekusi via class `Database`.
5. Muncul pesan sukses, form dikosongkan (method `clearForm()`), dan tabel diperbarui dengan data terbaru.

### 3. Update (Ubah Data)
1. Pengguna mengklik salah satu baris data pada `JTable`.
2. Data dari baris tersebut diambil dan diisi ke masing-masing komponen form. Tombol **Add** berubah menjadi **Update** dan tombol **Delete** muncul.
3. Pengguna mengubah data lalu menekan tombol **Update**.
4. Sistem melakukan **Validasi Input** yang sama seperti proses *Create*.
5. Jika `ID` diubah, sistem memvalidasi agar `ID` baru tersebut tidak bertabrakan dengan ID milik data lain.
6. Jika valid, query `UPDATE person SET ... WHERE id = 'id_lama'` dieksekusi.
7. Tabel di-refresh dan form dikembalikan ke keadaan semula.

### 4. Delete (Hapus Data)
1. Pengguna memilih baris data pada tabel.
2. Menekan tombol **Delete**.
3. Sistem menampilkan **Dialog Konfirmasi**: *"Apakah Anda yakin ingin menghapus data ini?"* (Yes/No).
4. Jika **Yes**, query `DELETE FROM person WHERE id = ...` dieksekusi.
5. Pesan sukses muncul, tabel di-refresh, dan mode dikembalikan ke awal.

---

## iii. Dokumentasi Program Dijalankan

### 1. Bukti Video (Screencast) & GIF
Tonton video demo lengkap yang menunjukkan seluruh proses:
**[Tonton video demo CRUD](Documentation/TP5Doc.mp4)**

## Documentation

![Documentation 1](Documentation/TP5Doc-ezgif.com-video-to-gif-converter%20%287%29.gif)

![Documentation 2](Documentation/TP5Doc-ezgif.com-video-to-gif-converter%20%286%29.gif)

![Documentation 3](Documentation/TP5Doc-ezgif.com-video-to-gif-converter%20%285%29.gif)

![Documentation 4](Documentation/TP5Doc-ezgif.com-video-to-gif-converter%20%284%29.gif)

![Documentation 5](Documentation/TP5Doc-ezgif.com-video-to-gif-converter%20%283%29.gif)

![Documentation 6](Documentation/TP5Doc-ezgif.com-video-to-gif-converter%20%282%29.gif)

### 2. Operasi CRUD Utama
*   **READ (Tampilan Awal)**: Tabel terisi oleh data bawaan dari database MySQL saat aplikasi pertama kali dijalankan.
*   **CREATE (Tambah Data)**: Mengisi form dengan data baru (contoh: ID = P010, Nama = Arya Dhinata, dsb), lalu menekan Add. Data muncul secara real-time di JTable bagian bawah.
*   **UPDATE (Ubah Data)**: Mengklik baris "P010", mengubah Tahun Lahir dari 2007 menjadi 2008, menekan Update, dan data di tabel langsung berubah.
*   **DELETE (Hapus Data)**: Mengklik baris tertentu, menekan tombol Delete, dan data hilang dari antarmuka maupun database setelah disetujui.

### 3. Dokumentasi Dialog/Prompt Error (Validasi)
Program telah dilengkapi dengan berbagai *Message Dialog* untuk menangani kesalahan *user*:

1. **Prompt Error - Kolom Kosong**
   *Muncul jika salah satu atau lebih kolom belum diisi (termasuk CheckBox).*
   > **[Error Dialog]** "Kolom berikut masih kosong: Nama, Kategori, Jenis Kelamin"

2. **Prompt Error - Format Tahun Lahir**
   *Muncul jika input tahun lahir bukan angka.*
   > **[Error Dialog]** "Tahun lahir harus berupa angka!"

3. **Prompt Error - Format Email**
   *Muncul jika email tidak memiliki karakter `@`.*
   > **[Error Dialog]** "Email tidak valid (harus berisi @)!"

4. **Prompt Error - Duplikasi ID**
   *Muncul saat membuat data baru atau mengupdate ke ID yang sudah dipakai.*
   > **[Error Dialog]** "ID 'P001' sudah digunakan! Gunakan ID yang berbeda."

5. **Prompt Konfirmasi Delete**
   *Muncul untuk mencegah ketidaksengajaan penghapusan data.*
   > **[Warning Dialog]** "Apakah Anda yakin ingin menghapus data ini?" (Yes / No)

6. **Prompt Sukses Operasi**
   *Pemberitahuan keberhasilan untuk DML.*
   > **[Information Dialog]** "Data berhasil ditambahkan" / "Data berhasil diubah" / "Data berhasil dihapus"
