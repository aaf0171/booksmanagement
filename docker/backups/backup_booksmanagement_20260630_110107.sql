/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-11.8.6-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: 127.0.0.1    Database: booksmanagement
-- ------------------------------------------------------
-- Server version	12.3.2-MariaDB-ubu2404

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Table structure for table `authors`
--

DROP TABLE IF EXISTS `authors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `authors` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `firstname` varchar(100) DEFAULT NULL,
  `lastname` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authors`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `authors` WRITE;
/*!40000 ALTER TABLE `authors` DISABLE KEYS */;
INSERT INTO `authors` VALUES
(49,'Antoine','de Saint-Exupéry'),
(50,'George','Orwell'),
(51,'Victor','Hugo'),
(52,'Alexandre','Dumas'),
(53,'Harper','Lee'),
(54,'J.K.','Rowling'),
(55,'J.R.R.','Tolkien'),
(56,'Frank','Herbert'),
(57,'Albert','Camus'),
(58,'Simone','de Beauvoir'),
(59,'Herman','Melville'),
(60,'Fiodor','Dostoïevski'),
(61,'Umberto','Eco'),
(62,'Aldous','Huxley');
/*!40000 ALTER TABLE `authors` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `borrowers`
--

DROP TABLE IF EXISTS `borrowers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `borrowers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `firstname` varchar(100) NOT NULL,
  `lastname` varchar(100) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `borrowers`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `borrowers` WRITE;
/*!40000 ALTER TABLE `borrowers` DISABLE KEYS */;
INSERT INTO `borrowers` VALUES
(21,'Marie','Dupont','marie.dupont@email.com','2026-06-30 08:07:11'),
(22,'James','Smith','james.smith@email.com','2026-06-30 08:07:11'),
(23,'Yuki','Tanaka','yuki.tanaka@email.com','2026-06-30 08:07:11'),
(24,'Amara','Diallo','amara.diallo@email.com','2026-06-30 08:07:11'),
(25,'Sofia','Rossi','sofia.rossi@email.com','2026-06-30 08:07:11'),
(26,'Chen','Wei','chen.wei@email.com','2026-06-30 08:07:11'),
(27,'Aisha','Obi','aisha.obi@email.com','2026-06-30 08:07:11'),
(28,'Lukas','Meyer','lukas.meyer@email.com','2026-06-30 08:07:11'),
(29,'Priya','Sharma','priya.sharma@email.com','2026-06-30 08:07:11'),
(30,'Elena','Petrova','elena.petrova@email.com','2026-06-30 08:07:11'),
(31,'Lucas','Martin','lucas.martin@email.com','2026-06-30 08:07:11'),
(32,'Fatou','Ndiaye','fatou.ndiaye@email.com','2026-06-30 08:07:11'),
(33,'Hiroshi','Suzuki','hiroshi.suzuki@email.com','2026-06-30 08:07:11'),
(34,'Emma','Johnson','emma.johnson@email.com','2026-06-30 08:07:11'),
(35,'Diego','Garcia','diego.garcia@email.com','2026-06-30 08:07:11'),
(36,'Nala','Mwangi','nala.mwangi@email.com','2026-06-30 08:07:11'),
(37,'Olga','Ivanova','olga.ivanova@email.com','2026-06-30 08:07:11'),
(38,'Pierre','Bernard','pierre.bernard@email.com','2026-06-30 08:07:11'),
(39,'Mei','Ling','mei.ling@email.com','2026-06-30 08:07:11'),
(40,'Omar','Hassan','omar.hassan@email.com','2026-06-30 08:07:11');
/*!40000 ALTER TABLE `borrowers` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `document_authors`
--

DROP TABLE IF EXISTS `document_authors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `document_authors` (
  `document_id` bigint(20) NOT NULL,
  `author_id` bigint(20) NOT NULL,
  PRIMARY KEY (`document_id`,`author_id`),
  KEY `author_id` (`author_id`),
  CONSTRAINT `1` FOREIGN KEY (`document_id`) REFERENCES `documents` (`id`) ON DELETE CASCADE,
  CONSTRAINT `2` FOREIGN KEY (`author_id`) REFERENCES `authors` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `document_authors`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `document_authors` WRITE;
/*!40000 ALTER TABLE `document_authors` DISABLE KEYS */;
INSERT INTO `document_authors` VALUES
(1,49),
(2,50),
(19,50),
(3,51),
(4,52),
(5,53),
(6,54),
(7,55),
(17,55),
(8,56),
(9,57),
(10,57),
(15,57),
(11,58),
(12,59),
(13,60),
(16,60),
(14,61),
(18,62),
(20,62);
/*!40000 ALTER TABLE `document_authors` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `documents`
--

DROP TABLE IF EXISTS `documents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `documents` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `subtitle` varchar(255) DEFAULT NULL,
  `document_type` enum('BOOK','DVD','GAME','DEVICE','OTHER') NOT NULL DEFAULT 'BOOK',
  `isbn` varchar(20) DEFAULT NULL,
  `publisher` varchar(255) DEFAULT NULL,
  `publication_year` int(11) DEFAULT NULL,
  `language` varchar(50) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `cover_url` varchar(500) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  FULLTEXT KEY `idx_documents_search` (`title`,`subtitle`,`description`,`publisher`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `documents`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `documents` WRITE;
/*!40000 ALTER TABLE `documents` DISABLE KEYS */;
INSERT INTO `documents` VALUES
(1,'Le Petit Prince',NULL,'BOOK','9782070612758','Gallimard',1943,'français','Un conte poétique et philosophique sur l\'amour, l\'amitié et la nature humaine.',NULL,'2026-06-30 08:20:35'),
(2,'1984',NULL,'BOOK','9780451524935','Signet Classics',1949,'anglais','Un roman dystopique sur la surveillance totale et le contrôle totalitaire.',NULL,'2026-06-30 08:20:35'),
(3,'Les Misérables',NULL,'BOOK','9782070368228','Gallimard',1862,'français','Un roman historique et social qui retrace la vie de Jean Valjean dans la France du XIXe siècle.',NULL,'2026-06-30 08:20:35'),
(4,'Le Comte de Monte-Cristo',NULL,'BOOK','9782253085298','Le Livre de Poche',1844,'français','Une histoire d\'aventures, de trahison et de vengeance.',NULL,'2026-06-30 08:20:35'),
(5,'To Kill a Mockingbird',NULL,'BOOK','9780061120084','Harper Perennial',1960,'anglais','Un roman sur le racisme et l\'injustice à travers le regard d\'une jeune fille dans le sud des États-Unis.',NULL,'2026-06-30 08:20:35'),
(6,'Harry Potter à l\'école des sorciers',NULL,'BOOK','9782070551940','Gallimard',1997,'français','Les aventures d\'un jeune sorcier orphelin découvrir le monde magique.',NULL,'2026-06-30 08:20:35'),
(7,'Le Seigneur des Anneaux',NULL,'BOOK','9782266025223','Le Livre de Poche',1954,'français','Une épopée fantasy qui suit la quête de Frodon pour détruire l\'Anneau unique.',NULL,'2026-06-30 08:20:35'),
(8,'Dune',NULL,'BOOK','9782266025223','Les Mille et Une Nuits',1965,'français','Un roman de science-fiction sur la planète désertique Arrakis et la lutte pour le contrôle de la spice.',NULL,'2026-06-30 08:20:35'),
(9,'L\'Étranger',NULL,'BOOK','9782070360088','Gallimard',1942,'français','Un roman existentialiste sur l\'absurdité de la vie à travers l\'histoire de Meursault.',NULL,'2026-06-30 08:20:35'),
(10,'Le Coma',NULL,'BOOK','9782070408290','Gallimard',1938,'français','Un roman sur l\'absurde et la condition humaine.',NULL,'2026-06-30 08:20:35'),
(11,'Le Deuxième Sexe',NULL,'BOOK','9782070366288','Gallimard',1949,'français','Un essai fondateur du féminisme moderne sur la condition féminine.',NULL,'2026-06-30 08:20:35'),
(12,'Moby Dick',NULL,'BOOK','9780142437247','Penguin Classics',1851,'anglais','L\'histoire de la quête obsessionnelle du capitaine Ahab pourchassant le grand cachalot blanc.',NULL,'2026-06-30 08:20:35'),
(13,'Crime et Châtiment',NULL,'BOOK','9782253085298','Le Livre de Poche',1866,'français','Un roman psychologique sur la culpabilité et la rédemption.',NULL,'2026-06-30 08:20:35'),
(14,'Le Nom de la Rose',NULL,'BOOK','9782070367148','Gallimard',1980,'français','Un roman policier médiéval se déroulant dans une abbénie italienne au XIVe siècle.',NULL,'2026-06-30 08:20:35'),
(15,'La Peste',NULL,'BOOK','9782070360088','Gallimard',1947,'français','Un roman sur une épidémie de peste qui frappe la ville d\'Oran.',NULL,'2026-06-30 08:20:35'),
(16,'Les Frères Karamazov',NULL,'BOOK','9782253085298','Le Livre de Poche',1880,'français','Un roman philosophique sur la foi, la raison et la liberté.',NULL,'2026-06-30 08:20:35'),
(17,'L\'Épée de la Liberté',NULL,'BOOK','9782266025223','Les Mille et Une Nuits',1995,'français','Le premier tome d\'une épopée fantasy se déroulant dans le monde fictif des Royaumes du Nord.',NULL,'2026-06-30 08:20:35'),
(18,'Neuromancien',NULL,'BOOK','9782070552008','Gallimard',1984,'français','Le roman fondateur du cyberpunk, suivi d\'un hacker déchu dans un monde numérique.',NULL,'2026-06-30 08:20:35'),
(19,'La Ferme des Animaux',NULL,'BOOK','9782070408290','Gallimard',1945,'français','Une fable satirique sur la révolution et le totalitarisme à travers les animaux d\'une ferme.',NULL,'2026-06-30 08:20:35'),
(20,'Brave New World',NULL,'BOOK','9780060850524','Harper Perennial',1932,'anglais','Un roman dystopique sur un monde futur où les humains sont clonés et conditionnés.',NULL,'2026-06-30 08:20:35');
/*!40000 ALTER TABLE `documents` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `items` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `document_id` bigint(20) NOT NULL,
  `barcode` varchar(50) NOT NULL,
  `acquisition_date` date DEFAULT NULL,
  `location` varchar(100) DEFAULT NULL,
  `status` enum('CLEAN','LOST','DAMAGED','REPAIR') NOT NULL DEFAULT 'CLEAN',
  PRIMARY KEY (`id`),
  UNIQUE KEY `barcode` (`barcode`),
  KEY `idx_items_document` (`document_id`),
  CONSTRAINT `1` FOREIGN KEY (`document_id`) REFERENCES `documents` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `items` WRITE;
/*!40000 ALTER TABLE `items` DISABLE KEYS */;
INSERT INTO `items` VALUES
(1,1,'CODE128-1000000001','2025-01-15','Rayon A1','CLEAN'),
(2,1,'CODE128-1000000002','2025-02-20','Rayon A1','CLEAN'),
(3,1,'CODE128-1000000003','2025-03-10','Rayon A2','CLEAN'),
(4,2,'CODE128-1000000004','2025-01-20','Rayon A3','CLEAN'),
(5,2,'CODE128-1000000005','2025-02-25','Rayon A3','CLEAN'),
(6,2,'CODE128-1000000006','2025-03-15','Rayon A4','REPAIR'),
(7,3,'CODE128-1000000007','2025-01-25','Rayon B1','CLEAN'),
(8,3,'CODE128-1000000008','2025-02-28','Rayon B1','CLEAN'),
(9,4,'CODE128-1000000009','2025-02-01','Rayon B2','CLEAN'),
(10,4,'CODE128-1000000010','2025-03-05','Rayon B2','CLEAN'),
(11,4,'CODE128-1000000011','2025-03-20','Rayon B3','DAMAGED'),
(12,5,'CODE128-1000000012','2025-02-10','Rayon C1','CLEAN'),
(13,5,'CODE128-1000000013','2025-03-12','Rayon C1','CLEAN'),
(14,6,'CODE128-1000000014','2025-02-15','Rayon C2','CLEAN'),
(15,6,'CODE128-1000000015','2025-03-18','Rayon C2','LOST'),
(16,7,'CODE128-1000000016','2025-02-20','Rayon C3','CLEAN'),
(17,7,'CODE128-1000000017','2025-03-22','Rayon C3','CLEAN'),
(18,7,'CODE128-1000000018','2025-04-01','Rayon D1','CLEAN'),
(19,8,'CODE128-1000000019','2025-03-01','Rayon D2','CLEAN'),
(20,8,'CODE128-1000000020','2025-03-25','Rayon D2','REPAIR'),
(21,9,'CODE128-1000000021','2025-03-05','Rayon D3','CLEAN'),
(22,9,'CODE128-1000000022','2025-03-28','Rayon D3','CLEAN'),
(23,10,'CODE128-1000000023','2025-03-10','Rayon E1','CLEAN'),
(24,11,'CODE128-1000000024','2025-03-15','Rayon E2','CLEAN'),
(25,12,'CODE128-1000000025','2025-03-20','Rayon E3','CLEAN'),
(26,12,'CODE128-1000000026','2025-04-05','Rayon E3','CLEAN'),
(27,13,'CODE128-1000000027','2025-03-25','Rayon F1','CLEAN'),
(28,13,'CODE128-1000000028','2025-04-08','Rayon F1','LOST'),
(29,14,'CODE128-1000000029','2025-03-30','Rayon F2','CLEAN'),
(30,14,'CODE128-1000000030','2025-04-10','Rayon F2','CLEAN'),
(31,14,'CODE128-1000000031','2025-04-15','Rayon F3','DAMAGED'),
(32,15,'CODE128-1000000032','2025-04-01','Rayon F3','CLEAN'),
(33,15,'CODE128-1000000033','2025-04-12','Rayon G1','CLEAN'),
(34,16,'CODE128-1000000034','2025-04-05','Rayon G2','CLEAN'),
(35,16,'CODE128-1000000035','2025-04-18','Rayon G2','CLEAN'),
(36,17,'CODE128-1000000036','2025-04-10','Rayon G3','CLEAN'),
(37,17,'CODE128-1000000037','2025-04-20','Rayon G3','REPAIR'),
(38,18,'CODE128-1000000038','2025-04-15','Rayon H1','CLEAN'),
(39,18,'CODE128-1000000039','2025-04-22','Rayon H1','CLEAN'),
(40,19,'CODE128-1000000040','2025-04-20','Rayon H2','CLEAN'),
(41,19,'CODE128-1000000041','2025-04-25','Rayon H2','LOST'),
(42,20,'CODE128-1000000042','2025-04-25','Rayon H3','CLEAN'),
(43,20,'CODE128-1000000043','2025-05-01','Rayon H3','CLEAN');
/*!40000 ALTER TABLE `items` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `loans`
--

DROP TABLE IF EXISTS `loans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `loans` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `item_id` bigint(20) NOT NULL,
  `borrower_id` bigint(20) NOT NULL,
  `loan_date` date NOT NULL,
  `due_date` date NOT NULL,
  `return_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `borrower_id` (`borrower_id`),
  KEY `idx_loans_active` (`item_id`),
  CONSTRAINT `1` FOREIGN KEY (`item_id`) REFERENCES `items` (`id`),
  CONSTRAINT `2` FOREIGN KEY (`borrower_id`) REFERENCES `borrowers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `loans`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `loans` WRITE;
/*!40000 ALTER TABLE `loans` DISABLE KEYS */;
/*!40000 ALTER TABLE `loans` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Dumping events for database 'booksmanagement'
--

--
-- Dumping routines for database 'booksmanagement'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2026-06-30 11:01:07
