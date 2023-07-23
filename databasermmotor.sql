-- MySQL dump 10.13  Distrib 8.0.30, for Win64 (x86_64)
--
-- Host: localhost    Database: databasermmotor
-- ------------------------------------------------------
-- Server version	8.0.30

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `datamotor`
--

DROP TABLE IF EXISTS `datamotor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `datamotor` (
  `idmotor` int NOT NULL AUTO_INCREMENT,
  `merk` varchar(15) COLLATE utf8mb4_general_ci NOT NULL,
  `ketsurat` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `plat` varchar(15) COLLATE utf8mb4_general_ci NOT NULL,
  `idtransaksipembelian` int DEFAULT NULL,
  `idtransaksipenjualan` int DEFAULT NULL,
  PRIMARY KEY (`idmotor`),
  UNIQUE KEY `platunik` (`plat`),
  KEY `idtransaksipembelian` (`idtransaksipembelian`),
  KEY `idtransaksipenjualan` (`idtransaksipenjualan`),
  CONSTRAINT `datamotor_ibfk_1` FOREIGN KEY (`idtransaksipembelian`) REFERENCES `datapembelian` (`idtransaksipembelian`),
  CONSTRAINT `datamotor_ibfk_2` FOREIGN KEY (`idtransaksipenjualan`) REFERENCES `datapenjualan` (`idtransaksipenjualan`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `datamotor`
--

LOCK TABLES `datamotor` WRITE;
/*!40000 ALTER TABLE `datamotor` DISABLE KEYS */;
INSERT INTO `datamotor` VALUES (1,'VESPA LX 125','LENGKAP','D 6953 SAF',1,1),(2,'LEGENDA 2002','LENGKAP','D 2681 CM',2,2),(3,'HONDA TIGER','LENGKAP','D 6634 VCK',3,3),(4,'BEAT 2019','LENGKAP','D 6188 ACI',4,4),(5,'BEAT 2019','LENGKAP','D 3086 UDX',5,5),(6,'BEAT 2018','LENGKAP','D 4593 ABS',6,6),(7,'BLADE REPSOL','LENGKAP','D 6442 VBO',7,7),(8,'VEGA ZR 2013','LENGKAP','D 5226 VBO',8,8),(9,'BEAT KARBU 2009','LENGKAP','D 2548 CV',9,9),(10,'VARIO 2019','LENGKAP','D 3040 ACU',10,10),(11,'CB 150 R 2016','LENGKAP','D 6529 SAW',11,11),(14,'R15 vv3 2017','LENGKAP','D 6098 AJD',12,19),(15,'Mio M3 2016','LENGKAP','D 3664 AJ',13,20);
/*!40000 ALTER TABLE `datamotor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `datapembelian`
--

DROP TABLE IF EXISTS `datapembelian`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `datapembelian` (
  `idtransaksipembelian` int NOT NULL AUTO_INCREMENT,
  `tglpembelian` date NOT NULL,
  `hargapembelian` int NOT NULL,
  `idpengguna` int DEFAULT NULL,
  `idmotor` int DEFAULT NULL,
  PRIMARY KEY (`idtransaksipembelian`),
  KEY `idpengguna` (`idpengguna`),
  KEY `idmotor` (`idmotor`),
  CONSTRAINT `datapembelian_ibfk_1` FOREIGN KEY (`idpengguna`) REFERENCES `datapengguna` (`idpengguna`),
  CONSTRAINT `datapembelian_ibfk_2` FOREIGN KEY (`idmotor`) REFERENCES `datamotor` (`idmotor`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `datapembelian`
--

LOCK TABLES `datapembelian` WRITE;
/*!40000 ALTER TABLE `datapembelian` DISABLE KEYS */;
INSERT INTO `datapembelian` VALUES (1,'2022-12-20',19500000,8,1),(2,'2023-02-05',3900000,9,2),(3,'2023-03-11',9000000,10,3),(4,'2023-03-12',10900000,11,4),(5,'2023-03-29',10000000,12,5),(6,'2023-04-02',9500000,13,6),(7,'2023-06-10',7400000,14,7),(8,'2023-01-03',4200000,15,8),(9,'2023-01-07',6300000,16,9),(10,'2023-02-20',14800000,17,10),(11,'2023-03-04',11000000,18,11),(12,'2023-04-19',19000000,96,14),(13,'2023-05-13',6300000,98,15);
/*!40000 ALTER TABLE `datapembelian` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `datapengguna`
--

DROP TABLE IF EXISTS `datapengguna`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `datapengguna` (
  `idpengguna` int NOT NULL AUTO_INCREMENT,
  `nama` varchar(64) COLLATE utf8mb4_general_ci NOT NULL,
  `nomortelp` varchar(12) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`idpengguna`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `datapengguna`
--

LOCK TABLES `datapengguna` WRITE;
/*!40000 ALTER TABLE `datapengguna` DISABLE KEYS */;
INSERT INTO `datapengguna` VALUES (8,'Muhamad Furkon','081861727899'),(9,'Riki Mulyadi','089521217020'),(10,'Syam Rizal','089525304642'),(11,'Mubaydilah','082320467270'),(12,'Zaldi Fahrezi','082215713400'),(13,'Rika Amelia','083188138199'),(14,'Fitria Nuranissa','081218213831'),(15,'Mita Safitri','082118594157'),(16,'Ayu Wulandari','083140548544'),(17,'Toni Cahyadi','087824079569'),(18,'Muhammad Budi','081225734436'),(79,'Robby Gunara','082214534567'),(80,'Siti Patonah','08987494760'),(81,'Vifa Lina','089516177392'),(82,'Siti Nurhayati','08986182801'),(83,'Windi Maryani','087825935321'),(84,'Dharina Kamelia','089524694945'),(85,'Eky Ferdiansyah','088223073721'),(86,'Tika oktavia','083168133155'),(87,'Vitman Firmansyah','089563129283'),(88,'Pian Julian Laban','083811676389'),(89,'Muhammad Azzam','085722332093'),(90,'Raihan saputra','081252929878'),(91,'Muhamad Imam','085722725775'),(92,'Ghea ramdani','081382747114'),(93,'Nanda Felianda','085759416745'),(94,'Titin Supriatin','089679102345'),(95,'Dede Wahyudi','085371321418'),(96,'Kiky Saputra','087882199158'),(97,'Nabilla Rismayanti','083813259740'),(98,'Sartika Hizkia','083821488828'),(99,'Hunsya Nur Aziza','081398637718');
/*!40000 ALTER TABLE `datapengguna` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `datapenjualan`
--

DROP TABLE IF EXISTS `datapenjualan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `datapenjualan` (
  `idtransaksipenjualan` int NOT NULL AUTO_INCREMENT,
  `tglpenjualan` date NOT NULL,
  `hargapenjualan` int NOT NULL,
  `idpengguna` int DEFAULT NULL,
  `idmotor` int DEFAULT NULL,
  PRIMARY KEY (`idtransaksipenjualan`),
  KEY `idpengguna` (`idpengguna`),
  KEY `idmotor` (`idmotor`),
  CONSTRAINT `datapenjualan_ibfk_1` FOREIGN KEY (`idpengguna`) REFERENCES `datapengguna` (`idpengguna`),
  CONSTRAINT `datapenjualan_ibfk_2` FOREIGN KEY (`idmotor`) REFERENCES `datamotor` (`idmotor`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `datapenjualan`
--

LOCK TABLES `datapenjualan` WRITE;
/*!40000 ALTER TABLE `datapenjualan` DISABLE KEYS */;
INSERT INTO `datapenjualan` VALUES (1,'2023-02-20',22500000,79,1),(2,'2023-05-14',4800000,80,2),(3,'2023-06-12',9850000,81,3),(4,'2023-04-20',11800000,82,4),(5,'2023-04-22',10800000,83,5),(6,'2023-05-15',10500000,84,6),(7,'2023-06-19',8000000,85,7),(8,'2023-02-20',22500000,86,1),(9,'2023-05-14',4800000,87,2),(10,'2023-06-12',9850000,88,3),(11,'2023-04-20',11800000,89,4),(19,'2023-04-20',25500000,97,14),(20,'2023-05-17',6500000,99,15);
/*!40000 ALTER TABLE `datapenjualan` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-07-23 19:40:58
