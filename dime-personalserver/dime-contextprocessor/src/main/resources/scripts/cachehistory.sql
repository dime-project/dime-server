-- MySQL dump 10.10
--
-- Host: localhost    Database: oauth
-- ------------------------------------------------------
-- Server version	5.0.24-community-max-nt

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cache_history`
--

DROP TABLE IF EXISTS `cache_history`;
CREATE TABLE `cache_history` (
  `ID` bigint(20) NOT NULL auto_increment,
  `ENTITY` varchar(255) collate utf8_bin NOT NULL default '' COMMENT 'entityType:entityName, ex. username:cristinaf',
  `SCOPE` varchar(255) collate utf8_bin NOT NULL COMMENT 'Ccope name',
  `TIMSTAMP` bigint(20) NOT NULL COMMENT 'Timestamp in timemillis',
  `EXPIRE` bigint(20) NOT NULL COMMENT 'Expire time in timemillis',
  `CTXELOBJ` blob,
  `CTXELSTR` text collate utf8_bin,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;