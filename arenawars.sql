-- phpMyAdmin SQL Dump
-- version 4.6.5.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Erstellungszeit: 05. Apr 2019 um 11:00
-- Server-Version: 10.1.21-MariaDB
-- PHP-Version: 7.1.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `arenawars`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `account`
--

CREATE TABLE `account` (
  `id` int(11) NOT NULL,
  `rank` int(11) NOT NULL DEFAULT '1',
  `name` varchar(50) NOT NULL,
  `pw` varchar(32) NOT NULL,
  `email` varchar(100) NOT NULL,
  `email_verified` tinyint(1) NOT NULL DEFAULT '0',
  `email_newsletter` tinyint(1) NOT NULL DEFAULT '1',
  `registration` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastaction` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `locked` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `map`
--

CREATE TABLE `map` (
  `id` int(11) NOT NULL,
  `title` varchar(100) NOT NULL COMMENT 'Abweichender Name zum Mapset',
  `w` int(11) NOT NULL,
  `h` int(11) NOT NULL,
  `overlayer` text NOT NULL,
  `groundlayer` text NOT NULL,
  `weapons` varchar(2000) NOT NULL,
  `content` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `map`
--

INSERT INTO `map` (`id`, `title`, `w`, `h`, `overlayer`, `groundlayer`, `weapons`, `content`) VALUES
(1, 'Mikrig', 300, 200, '[]', '[{\"go\":1,\"x\":80,\"y\":100},{\"go\":1,\"x\":140, \"y\":80}]', '{}', ''),
(2, 'Winzig', 400, 300, '[]', '[]', '', ''),
(3, 'Klein', 600, 400, '[]', '[]', '', ''),
(4, 'Mittel', 800, 600, '[]', '[{\"go\":1,\"x\":100,\"y\":120},{\"go\":1,\"x\":250,\"y\":110},{\"go\":1,\"x\":400,\"y\":175},{\"go\":1,\"x\":550,\"y\":125},{\"go\":1,\"x\":750,\"y\":200},{\"go\":1,\"x\":110,\"y\":400},{\"go\":1,\"x\":160,\"y\":400},{\"go\":1,\"x\":500,\"y\":270},{\"go\":1,\"x\":500,\"y\":320},{\"go\":1,\"x\":620,\"y\":500},{\"go\":1,\"x\":620,\"y\":550},{\"go\":1,\"x\":350,\"y\":300}]', '', ''),
(5, 'Groß', 1200, 800, '[]', '[]', '', ''),
(6, 'Riesig', 2000, 1500, '[]', '[{\"go\":1,\"x\":1200,\"y\":150},{\"go\":1,\"x\":1250, \"y\":150},{\"go\":1,\"x\":1300, \"y\":150},{\"go\":1,\"x\":1400, \"y\":150},{\"go\":1,\"x\":1450, \"y\":150},{\"go\":1,\"x\":1500, \"y\":150},{\"go\":1,\"x\":1550, \"y\":150},{\"go\":1,\"x\":1600, \"y\":150},{\"go\":1,\"x\":1650, \"y\":150},{\"go\":1,\"x\":1700, \"y\":150},{\"go\":1,\"x\":1200, \"y\":1300},{\"go\":1,\"x\":1250, \"y\":1300},{\"go\":1,\"x\":1300, \"y\":1300},{\"go\":1,\"x\":1350, \"y\":1300},{\"go\":1,\"x\":1400, \"y\":1300},{\"go\":1,\"x\":1450, \"y\":1300},{\"go\":1,\"x\":1500, \"y\":1300},{\"go\":1,\"x\":1550, \"y\":1300},{\"go\":1,\"x\":1600, \"y\":1300},{\"go\":1,\"x\":350, \"y\":250},{\"go\":1,\"x\":350, \"y\":300},{\"go\":1,\"x\":350, \"y\":350},{\"go\":1,\"x\":350, \"y\":400},{\"go\":1,\"x\":350, \"y\":450},{\"go\":1,\"x\":350, \"y\":500},{\"go\":1,\"x\":350, \"y\":550},{\"go\":1,\"x\":850, \"y\":300},{\"go\":1,\"x\":1100, \"y\":320},{\"go\":1,\"x\":990, \"y\":310},{\"go\":1,\"x\":100, \"y\":750},{\"go\":1,\"x\":1700, \"y\":350},{\"go\":1,\"x\":1750, \"y\":450},{\"go\":1,\"x\":1000, \"y\":850},{\"go\":1,\"x\":150, \"y\":1350}]', '', '');

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `account`
--
ALTER TABLE `account`
  ADD PRIMARY KEY (`id`);

--
-- Indizes für die Tabelle `map`
--
ALTER TABLE `map`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `account`
--
ALTER TABLE `account`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=121;
--
-- AUTO_INCREMENT für Tabelle `map`
--
ALTER TABLE `map`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
