-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Oct 17, 2026 at 11:18 AM
-- Server version: 8.0.30
-- PHP Version: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_people`
--

-- --------------------------------------------------------

--
-- Table structure for table `person`
--

CREATE TABLE `person` (
  `id` varchar(255) NOT NULL,
  `nama` varchar(255) NOT NULL,
  `thn_lahir` int NOT NULL,
  `kategori` enum('Siswa','Guru','Staf TU','Kepala Sekolah','Alumni') NOT NULL,
  `jns_kelamin` varchar(10) NOT NULL DEFAULT 'Laki-laki',
  `email` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `person`
--

INSERT INTO `person` (`id`, `nama`, `thn_lahir`, `kategori`, `jns_kelamin`, `email`) VALUES
('P001', 'Budi Santoso', 1988, 'Guru', 'Laki-laki', 'budi.santoso@example.com'),
('P002', 'Siti Rahmawati', 1995, 'Guru', 'Perempuan', 'siti.rahmawati@example.com'),
('P003', 'Ahmad Rizky', 2002, 'Alumni', 'Laki-laki', 'ahmad.rizky@example.com'),
('P004', 'Dewi Lestari', 1985, 'Siswa', 'Perempuan', 'dewi.lestari@example.com'),
('P005', 'Eko Prasetyo', 1998, 'Staf TU', 'Laki-laki', 'eko.prasetyo@example.com'),
('P006', 'Fitriani', 2003, 'Alumni', 'Perempuan', 'fitriani@example.com'),
('P007', 'Gita Gutawa', 1992, 'Guru', 'Perempuan', 'gita.gutawa@example.com'),
('P008', 'Hendra Wijaya', 1990, 'Siswa', 'Laki-laki', 'hendra.wijaya@example.com'),
('P009', 'Indah Permata', 1999, 'Staf TU', 'Perempuan', 'indah.permata@example.com');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `person`
--
ALTER TABLE `person`
  ADD PRIMARY KEY (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
