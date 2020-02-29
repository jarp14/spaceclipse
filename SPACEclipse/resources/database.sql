-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	4.0.18-nt


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema `space`
--

CREATE DATABASE IF NOT EXISTS `space_example2`;
USE `space_example2`;

--
-- Definition of table `accesos`
--

DROP TABLE IF EXISTS `accesos`;
CREATE TABLE `accesos` (
  `sesion` varchar(10) default NULL,
  `tipo` varchar(10) default NULL,
  `fecha` varchar(10) default NULL,
  `hora` varchar(12) default NULL,
  `usuario` varchar(10) default NULL,
  `acceso` char(1) default NULL,
  KEY `accesos` (`sesion`,`fecha`,`hora`)
) ENGINE=InnoDB;


--
-- Definition of table `mensajes_chat`
--

DROP TABLE IF EXISTS `mensajes_chat`;
CREATE TABLE `mensajes_chat` (
  `numero` int(10) NOT NULL auto_increment,
  `sesion` varchar(10) default NULL,
  `fecha` varchar(10) default NULL,
  `hora` varchar(12) default NULL,
  `usuario` varchar(10) default NULL,
  `mensaje` varchar(10) default NULL,
  `tipo_mensaje` char(1) default NULL,
  `texto` varchar(255) default NULL,
  PRIMARY KEY  (`numero`),
  KEY `mensajes_chat` (`sesion`,`usuario`,`fecha`,`hora`)
) ENGINE=InnoDB;


--
-- Definition of table `miembros_sesion`
--

DROP TABLE IF EXISTS `miembros_sesion`;
CREATE TABLE `miembros_sesion` (
  `sesion` varchar(10) NOT NULL default '',
  `usuario` varchar(10) NOT NULL default '',
  PRIMARY KEY  (`sesion`,`usuario`)
) ENGINE=InnoDB;


--
-- Definition of table `sesiones`
--

DROP TABLE IF EXISTS `sesiones`;
CREATE TABLE `sesiones` (
  `nombre` varchar(10) NOT NULL default '',
  `tipo` varchar(10) default NULL,
  `creador` varchar(10) default NULL,
  `fichero` varchar(100) default NULL,
  `privada` tinyint(1) default NULL,
  `dia_inic` varchar(8) default NULL,
  `dia_final` varchar(8) default NULL,
  `hora_inic` varchar(8) default NULL,
  `hora_final` varchar(8) default NULL,
  PRIMARY KEY  (`nombre`)
) ENGINE=InnoDB;

--
-- Dumping data for table `sesiones`
--

/*!40000 ALTER TABLE `sesiones` DISABLE KEYS */;
INSERT INTO `sesiones` (`nombre`,`tipo`,`creador`,`fichero`,`privada`,`dia_inic`,`dia_final`,`hora_inic`,`hora_final`) VALUES 
 ('circuits','circuits','user1','Circuits/Circuits.circuitosdigitales_diagram',0,'00000101','99991231','00:00:00','23:59:59');
/*!40000 ALTER TABLE `sesiones` ENABLE KEYS */;


--
-- Definition of table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE `usuarios` (
  `id` varchar(10) NOT NULL default '',
  `nombre` varchar(50) default NULL,
  `clave` varchar(10) default NULL,
  `admin` tinyint(1) default NULL,
  `foto` varchar(64) default NULL,
  `ip` varchar(20) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB;

--
-- Dumping data for table `usuarios`
--

/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` (`id`,`nombre`,`clave`,`admin`,`foto`,`ip`) VALUES 
 ('user1','user1','user1',1,'https://i.ibb.co/wSbWf4b/anonimo1.png',NULL),
 ('user2','user2','user2',1,'https://i.ibb.co/89gQXPm/anonimo2.png',NULL),
 ('user3','user3','user3',1,'https://i.ibb.co/XsvG4Gt/anonimo3.png',NULL),
 ('user4','user4','user4',1,'https://i.ibb.co/Hz8xF21/anonimo4.png',NULL),
 ('user5','user5','user5',1,'https://i.ibb.co/cbVBjhC/anonimo5.png',NULL),
 ('user6','user6','user6',1,'https://i.ibb.co/tpppYdb/anonimo6.png',NULL),
 ('user7','user7','user7',1,'https://i.ibb.co/FgXcrb1/anonimo7.png',NULL),
 ('user8','user8','user8',1,'https://i.ibb.co/dkNpWvs/anonimo8.png',NULL);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;


--
-- Definition of table `usuarios_sesion`
--

DROP TABLE IF EXISTS `usuarios_sesion`;
CREATE TABLE `usuarios_sesion` (
  `sesion` varchar(10) NOT NULL default '',
  `usuario` varchar(10) NOT NULL default '',
  PRIMARY KEY  (`sesion`,`usuario`)
) ENGINE=InnoDB;


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
