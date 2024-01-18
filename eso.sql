-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Sty 18, 2024 at 10:25 PM
-- Wersja serwera: 10.4.28-MariaDB
-- Wersja PHP: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `eso`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `accounts`
--

CREATE TABLE `accounts` (
  `id` int(11) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `name` varchar(30) NOT NULL,
  `surname` varchar(30) NOT NULL,
  `role` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `accounts`
--

INSERT INTO `accounts` (`id`, `email`, `password`, `name`, `surname`, `role`) VALUES
(1, 'jan.kowalski@example.com', 'JanKowalski1', 'Jan', 'Kowalski', 0),
(2, 'piotr.nowak@example.com', 'PiotrNowak1', 'Piotr', 'Nowak', 0),
(3, 'krzysztof.wisniewski@example.com', 'KrzysztofWisniewski1', 'Krzysztof', 'Wiśniewski', 0),
(4, 'anna.kowalczyk@example.com', 'AnnaKowalczyk1', 'Anna', 'Kowalczyk', 0),
(5, 'maria.wojcik@example.com', 'MariaWojcik1', 'Maria', 'Wójcik', 0),
(6, 'katarzyna.kaminska@example.com', 'KatarzynaKaminska1', 'Katarzyna', 'Kamińska', 0),
(7, 'jan.nowak@example.com', 'JanNowak1', 'Jan', 'Nowak', 0),
(8, 'piotr.kowalski@example.com', 'PiotrKowalski1', 'Piotr', 'Kowalski', 0),
(9, 'krzysztof.kowalczyk@example.com', 'KrzysztofKowalczyk1', 'Krzysztof', 'Kowalczyk', 0),
(10, 'anna.wisniewska@example.com', 'AnnaWisniewska1', 'Anna', 'Wiśniewska', 0),
(11, 'maria.kaminska@example.com', 'MariaKaminska1', 'Maria', 'Kamińska', 0),
(12, 'katarzyna.wojcik@example.com', 'KatarzynaWojcik1', 'Katarzyna', 'Wójcik', 0),
(13, 'jan.wisniewski@example.com', 'JanWisniewski1', 'Jan', 'Wiśniewski', 0),
(14, 'piotr.kowalczyk@example.com', 'PiotrKowalczyk1', 'Piotr', 'Kowalczyk', 0),
(15, 'krzysztof.wojcik@example.com', 'KrzysztofWojcik1', 'Krzysztof', 'Wójcik', 0),
(16, 'anna.kaminska@example.com', 'AnnaKaminska1', 'Anna', 'Kamińska', 0),
(17, 'maria.nowak@example.com', 'MariaNowak1', 'Maria', 'Nowak', 0),
(18, 'katarzyna.kowalska@example.com', 'KatarzynaKowalska1', 'Katarzyna', 'Kowalska', 0),
(19, 'adam.nowak@example.com', 'adamnowak12345', 'Adam', 'Nowak', 1);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `grades`
--

CREATE TABLE `grades` (
  `id` smallint(5) NOT NULL,
  `student_id` int(11) NOT NULL,
  `teacher_id` int(11) NOT NULL,
  `grade` float NOT NULL,
  `type` varchar(20) NOT NULL,
  `date` datetime NOT NULL DEFAULT current_timestamp(),
  `subject_id` tinyint(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `grades`
--

INSERT INTO `grades` (`id`, `student_id`, `teacher_id`, `grade`, `type`, `date`, `subject_id`) VALUES
(1, 1, 19, 3.5, 'Praca domowa', '2024-01-18 22:14:05', 2),
(2, 3, 19, 3, 'Praca domowa', '2024-01-18 22:14:05', 2),
(3, 4, 19, 4.5, 'Praca domowa', '2024-01-18 22:14:05', 2),
(4, 7, 19, 3, 'Praca domowa', '2024-01-18 22:14:05', 2),
(5, 14, 19, 3, 'Aktywność', '2024-01-18 22:14:42', 1),
(6, 15, 19, 3.5, 'Aktywność', '2024-01-18 22:14:42', 1),
(7, 16, 19, 4, 'Aktywność', '2024-01-18 22:14:42', 1),
(8, 17, 19, 4.5, 'Aktywność', '2024-01-18 22:14:42', 1),
(9, 18, 19, 5, 'Aktywność', '2024-01-18 22:14:42', 1),
(10, 11, 19, 4.5, 'Wejściówka', '2024-01-18 22:15:08', 4),
(11, 12, 19, 3, 'Wejściówka', '2024-01-18 22:15:08', 4),
(12, 13, 19, 3.5, 'Wejściówka', '2024-01-18 22:15:08', 4),
(13, 14, 19, 5, 'Wejściówka', '2024-01-18 22:15:08', 4);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `subjects`
--

CREATE TABLE `subjects` (
  `id` tinyint(3) NOT NULL,
  `name` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `subjects`
--

INSERT INTO `subjects` (`id`, `name`) VALUES
(1, 'Matematyka'),
(2, 'Informatyka'),
(3, 'Historia'),
(4, 'Chemia'),
(5, 'Biologia'),
(6, 'Język angielski'),
(7, 'Fizyka'),
(8, 'Geografia'),
(9, 'Wiedza o społeczeństwie'),
(10, 'Edukacja plastyczna'),
(11, 'Język polski'),
(12, 'Ekonomia'),
(13, 'Muzyka'),
(14, 'Wychowanie fizyczne'),
(15, 'Psychologia');

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `accounts`
--
ALTER TABLE `accounts`
  ADD PRIMARY KEY (`id`);

--
-- Indeksy dla tabeli `grades`
--
ALTER TABLE `grades`
  ADD PRIMARY KEY (`id`),
  ADD KEY `student_id` (`student_id`),
  ADD KEY `teacher_id` (`teacher_id`),
  ADD KEY `subject_id` (`subject_id`);

--
-- Indeksy dla tabeli `subjects`
--
ALTER TABLE `subjects`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `accounts`
--
ALTER TABLE `accounts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `grades`
--
ALTER TABLE `grades`
  MODIFY `id` smallint(5) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `subjects`
--
ALTER TABLE `subjects`
  MODIFY `id` tinyint(3) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `grades`
--
ALTER TABLE `grades`
  ADD CONSTRAINT `student_id` FOREIGN KEY (`student_id`) REFERENCES `accounts` (`id`),
  ADD CONSTRAINT `subject_id` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`),
  ADD CONSTRAINT `teacher_id` FOREIGN KEY (`teacher_id`) REFERENCES `accounts` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
