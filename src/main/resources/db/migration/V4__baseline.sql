-- MySQL dump 10.13  Distrib 8.0.35, for Win64 (x86_64)
--
-- Host: localhost    Database: tasker_db
-- ------------------------------------------------------
-- Server version	8.0.35

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','<< Flyway Baseline >>','BASELINE','<< Flyway Baseline >>',NULL,'root','2025-05-23 15:12:19',0,1),(2,'2','baseline','SQL','V2__baseline.sql',1305903030,'root','2025-05-26 08:37:59',766,1),(3,'3','baseline','SQL','V3__baseline.sql',-1772370832,'root','2025-05-26 08:38:25',774,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `priority`
--

DROP TABLE IF EXISTS `priority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `priority` (
  `id` int NOT NULL AUTO_INCREMENT,
  `label` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL,
  `teamId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `priority_team_team_id_fk` (`teamId`),
  CONSTRAINT `priority_team_team_id_fk` FOREIGN KEY (`teamId`) REFERENCES `team` (`team_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `priority`
--

LOCK TABLES `priority` WRITE;
/*!40000 ALTER TABLE `priority` DISABLE KEYS */;
INSERT INTO `priority` VALUES (1,'All','all','test_team'),(2,'Low','low','test_team'),(3,'Medium','medium','test_team'),(4,'High','high','test_team'),(5,'Urgent','urgent','test_team');
/*!40000 ALTER TABLE `priority` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `role_id` varchar(36) NOT NULL,
  `role_name` varchar(50) NOT NULL,
  `description` text,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES ('r1','ADMIN','System Administrator'),('r2','USER','Team Member'),('r3','MANAGER','Project Manager');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_attachments`
--

DROP TABLE IF EXISTS `task_attachments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_attachments` (
  `id` varchar(36) NOT NULL,
  `taskId` varchar(36) DEFAULT NULL,
  `url` varchar(1000) NOT NULL,
  `fileName` varchar(255) NOT NULL,
  `fileType` varchar(100) NOT NULL,
  `uploadedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `task_attachments_tasks_id_fk` (`taskId`),
  CONSTRAINT `task_attachments_tasks_id_fk` FOREIGN KEY (`taskId`) REFERENCES `tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_attachments`
--

LOCK TABLES `task_attachments` WRITE;
/*!40000 ALTER TABLE `task_attachments` DISABLE KEYS */;
/*!40000 ALTER TABLE `task_attachments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_tags`
--

DROP TABLE IF EXISTS `task_tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_tags` (
  `task_id` varchar(36) NOT NULL,
  `tag` varchar(50) NOT NULL,
  PRIMARY KEY (`task_id`,`tag`),
  CONSTRAINT `task_tags_ibfk_1` FOREIGN KEY (`task_id`) REFERENCES `tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_tags`
--

LOCK TABLES `task_tags` WRITE;
/*!40000 ALTER TABLE `task_tags` DISABLE KEYS */;
INSERT INTO `task_tags` VALUES ('task-0001','backend'),('task-0001','setup'),('task-0002','design'),('task-0002','ui'),('task-0003','api'),('task-0003','auth'),('task-0003','backend'),('task-0004','frontend'),('task-0004','integration'),('task-0005','testing'),('task-0005','unit-tests'),('task-0006','database'),('task-0006','setup'),('task-0007','backend'),('task-0007','model'),('task-0008','automation'),('task-0008','ci'),('task-0009','api'),('task-0009','documentation'),('task-0010','code-quality'),('task-0010','refactor'),('task-0011','design'),('task-0011','ui'),('task-0012','api'),('task-0012','backend'),('task-0013','frontend'),('task-0013','integration'),('task-0014','testing'),('task-0014','unit-tests'),('task-0015','notifications'),('task-0015','push');
/*!40000 ALTER TABLE `task_tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tasks`
--

DROP TABLE IF EXISTS `tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tasks` (
  `id` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text,
  `completed` tinyint(1) NOT NULL DEFAULT '0',
  `priority` enum('LOW','MEDIUM','HIGH') NOT NULL,
  `due_date` timestamp NOT NULL,
  `created_at` timestamp NOT NULL,
  `assigned_to` varchar(36) DEFAULT NULL,
  `parent_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `parent_id` (`parent_id`),
  KEY `tasks_assigned__fk` (`assigned_to`),
  CONSTRAINT `tasks_assigned__fk` FOREIGN KEY (`assigned_to`) REFERENCES `users` (`user_id`),
  CONSTRAINT `tasks_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tasks`
--

LOCK TABLES `tasks` WRITE;
/*!40000 ALTER TABLE `tasks` DISABLE KEYS */;
INSERT INTO `tasks` VALUES ('task-0001','Setup project repo','Initialize git repository and CI pipeline',0,'MEDIUM','2025-06-10 04:30:00','2025-05-20 02:30:00','1',NULL),('task-0002','Design login screen','Create wireframes and mockups for login',0,'HIGH','2025-06-15 11:30:00','2025-05-21 04:00:00','2',NULL),('task-0003','Implement login API','Backend API for user authentication',0,'HIGH','2025-06-20 06:30:00','2025-05-22 04:45:00','3',NULL),('task-0004','Login UI integration','Connect login screen with API',0,'MEDIUM','2025-06-25 09:30:00','2025-05-23 08:30:00','2','task-0003'),('task-0005','Write login tests','Unit and integration tests for login',0,'LOW','2025-06-28 05:30:00','2025-05-24 03:30:00','1','task-0003'),('task-0006','Setup database','Create schema and tables',1,'MEDIUM','2025-06-05 03:30:00','2025-05-19 02:30:00','4',NULL),('task-0007','Create user model','Define user entity and validation',0,'HIGH','2025-06-12 08:30:00','2025-05-20 05:30:00','1','task-0006'),('task-0008','Setup CI pipeline','Automate build and test',0,'MEDIUM','2025-06-15 04:30:00','2025-05-21 03:00:00','5',NULL),('task-0009','Write API documentation','Swagger docs for login API',0,'LOW','2025-06-18 10:30:00','2025-05-22 07:30:00','3','task-0003'),('task-0010','Refactor login flow','Improve code quality and flow',0,'MEDIUM','2025-06-22 06:30:00','2025-05-23 09:30:00','2','task-0003'),('task-0011','Design user profile screen','Wireframes for profile UI',0,'HIGH','2025-06-30 03:30:00','2025-05-25 04:30:00','4',NULL),('task-0012','Implement profile update API','Backend API to update user profile',0,'HIGH','2025-07-05 06:30:00','2025-05-26 05:30:00','3',NULL),('task-0013','Profile UI integration','Connect profile screen with API',0,'MEDIUM','2025-07-10 09:30:00','2025-05-27 08:30:00','2','task-0012'),('task-0014','Write profile tests','Unit and integration tests for profile',0,'LOW','2025-07-15 05:30:00','2025-05-28 03:30:00','1','task-0012'),('task-0015','Setup notifications','Implement push notifications',0,'MEDIUM','2025-07-20 03:30:00','2025-05-29 02:30:00','5',NULL);
/*!40000 ALTER TABLE `tasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team`
--

DROP TABLE IF EXISTS `team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `team` (
  `team_id` varchar(36) NOT NULL,
  `team_name` varchar(100) NOT NULL,
  `description` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team`
--

LOCK TABLES `team` WRITE;
/*!40000 ALTER TABLE `team` DISABLE KEYS */;
INSERT INTO `team` VALUES ('T1','UI Core','Responsible for building pixel-perfect, accessible, and responsive user interfaces.','2025-05-15 03:30:00'),('T2','API Forge','Designs and maintains scalable APIs and microservices for internal and external use.','2025-05-15 03:30:00'),('T3','CloudOps','Ensures infrastructure stability, continuous delivery, and cloud-native tooling.','2025-05-15 03:30:00'),('test_team','Test Team','Record for testing a team','2025-05-25 13:38:15');
/*!40000 ALTER TABLE `team` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_credentials`
--

DROP TABLE IF EXISTS `user_credentials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_credentials` (
  `credential_id` varchar(36) NOT NULL,
  `user_id` varchar(36) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `last_login` timestamp NULL DEFAULT NULL,
  `last_password_change` timestamp NULL DEFAULT NULL,
  `failed_attempts` int DEFAULT '0',
  `account_locked` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`credential_id`),
  UNIQUE KEY `user_id` (`user_id`),
  UNIQUE KEY `username` (`username`),
  KEY `idx_credentials_username` (`username`),
  CONSTRAINT `user_credentials_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_credentials`
--

LOCK TABLES `user_credentials` WRITE;
/*!40000 ALTER TABLE `user_credentials` DISABLE KEYS */;
INSERT INTO `user_credentials` VALUES ('c1','1','johndoe','$2a$10$dummyhashedpassword123','2024-01-15 03:00:00','2023-12-01 06:30:00',0,0),('c2','2','alicesmith','$2a$10$dummyhashedpassword456','2024-01-14 10:15:00','2023-11-15 04:00:00',0,0),('c3','3','rjohnson','$2a$10$dummyhashedpassword789','2024-01-13 05:50:00','2023-12-20 08:45:00',0,0),('c4','4','ewilson','$2a$10$dummyhashedpassword101','2024-01-10 03:45:00','2023-10-05 11:15:00',0,0),('c5','5','mbrown','$2a$10$dummyhashedpassword202','2024-01-15 08:10:00','2023-11-30 04:50:00',0,0);
/*!40000 ALTER TABLE `user_credentials` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` varchar(36) NOT NULL,
  `role_id` varchar(36) NOT NULL,
  `granted_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `granted_by` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES ('1','r1','2025-05-23 15:29:15','1'),('1','r2','2025-05-23 15:29:15','1'),('2','r2','2025-05-23 15:29:15','1'),('3','r3','2025-05-23 15:29:15','1'),('4','r2','2025-05-23 15:29:15','1'),('5','r3','2025-05-23 15:29:15','1');
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_sessions`
--

DROP TABLE IF EXISTS `user_sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_sessions` (
  `session_id` varchar(36) NOT NULL,
  `user_id` varchar(36) NOT NULL,
  `token` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `expires_at` timestamp NOT NULL,
  `last_activity` timestamp NULL DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `user_agent` text,
  PRIMARY KEY (`session_id`),
  UNIQUE KEY `token` (`token`),
  KEY `user_id` (`user_id`),
  KEY `idx_sessions_token` (`token`),
  KEY `idx_sessions_expiry` (`expires_at`),
  CONSTRAINT `user_sessions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_sessions`
--

LOCK TABLES `user_sessions` WRITE;
/*!40000 ALTER TABLE `user_sessions` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_team`
--

DROP TABLE IF EXISTS `user_team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_team` (
  `user_id` varchar(36) NOT NULL,
  `team_id` varchar(36) NOT NULL,
  `joined_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `role_in_team` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`user_id`,`team_id`),
  KEY `team_id` (`team_id`),
  CONSTRAINT `user_team_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_team_ibfk_2` FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_team`
--

LOCK TABLES `user_team` WRITE;
/*!40000 ALTER TABLE `user_team` DISABLE KEYS */;
INSERT INTO `user_team` VALUES ('1','T1','2025-05-16 02:30:00','Senior UI Engineer'),('2','T1','2025-05-16 02:40:00','Accessibility Specialist'),('3','T2','2025-05-16 02:50:00','Backend Developer'),('4','T3','2025-05-16 03:00:00','SRE Engineer'),('5','T2','2025-05-16 03:10:00','Database Engineer');
/*!40000 ALTER TABLE `user_team` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` varchar(36) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) DEFAULT 'ACTIVE',
  `profile_picture_url` varchar(255) DEFAULT NULL,
  `address` text,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('1','John','Doe','john.doe@email.com','+1-555-0123','1990-05-15','2025-05-23 15:29:02','2025-05-23 15:29:02','ACTIVE','https://randomuser.me/api/portraits/men/9.jpg','123 Main St, Boston, MA'),('2','Alice','Smith','alice.smith@email.com','+1-555-0124','1988-08-21','2025-05-23 15:29:02','2025-05-23 15:29:02','ACTIVE','https://randomuser.me/api/portraits/men/30.jpg','456 Oak Ave, Seattle, WA'),('3','Robert','Johnson','robert.j@email.com','+1-555-0125','1995-03-30','2025-05-23 15:29:02','2025-05-23 15:29:02','ACTIVE','https://randomuser.me/api/portraits/men/59.jpg','789 Pine Rd, Austin, TX'),('4','Emma','Wilson','emma.w@email.com','+1-555-0126','1992-11-08','2025-05-23 15:29:02','2025-05-23 15:29:02','INACTIVE','https://randomuser.me/api/portraits/women/85.jpg','321 Elm St, Denver, CO'),('5','Michael','Brown','michael.b@email.com','+1-555-0127','1985-07-14','2025-05-23 15:29:02','2025-05-23 15:29:02','ACTIVE','https://randomuser.me/api/portraits/men/29.jpg','654 Maple Dr, Chicago, IL');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-27 21:49:43
