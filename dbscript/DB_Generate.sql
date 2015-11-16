-- MySQL Script generated by MySQL Workbench
-- Wed Nov 11 04:35:27 2015
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema access_control_DB
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema access_control_DB
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `access_control_DB` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `access_control_DB` ;

-- -----------------------------------------------------
-- Table `access_control_DB`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `access_control_DB`.`user` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `access_control_DB`.`employer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `access_control_DB`.`employer` (
  `user_id` INT NOT NULL,
  `detail` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_employer_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `access_control_DB`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `access_control_DB`.`patient`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `access_control_DB`.`patient` (
  `user_id` INT NOT NULL,
  `detail` VARCHAR(45) NOT NULL,
  `employer_user_id` INT NULL,
  PRIMARY KEY (`user_id`),
  INDEX `fk_patient_employer1_idx` (`employer_user_id` ASC),
  CONSTRAINT `fk_patient_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `access_control_DB`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_patient_employer1`
    FOREIGN KEY (`employer_user_id`)
    REFERENCES `access_control_DB`.`employer` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `access_control_DB`.`organisation`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `access_control_DB`.`organisation` (
  `id` INT NOT NULL,
  `title` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `access_control_DB`.`author`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `access_control_DB`.`author` (
  `user_id` INT NOT NULL,
  `organisation_id` INT NOT NULL,
  `detail` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`user_id`, `organisation_id`),
  INDEX `fk_author_organisation1_idx` (`organisation_id` ASC),
  CONSTRAINT `fk_hospital_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `access_control_DB`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_author_organisation1`
    FOREIGN KEY (`organisation_id`)
    REFERENCES `access_control_DB`.`organisation` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `access_control_DB`.`doctor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `access_control_DB`.`doctor` (
  `user_id` INT NOT NULL,
  `detail` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_doctor_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `access_control_DB`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `access_control_DB`.`insurance_co`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `access_control_DB`.`insurance_co` (
  `user_id` INT NOT NULL,
  `detail` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_insurance_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `access_control_DB`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `access_control_DB`.`patient_health_record`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `access_control_DB`.`patient_health_record` (
  `id` INT NOT NULL,
  `detail` LONGBLOB NOT NULL,
  `policy` VARCHAR(100) NOT NULL,
  `owner_patient_user_id` INT NOT NULL,
  `author_user_id` INT NOT NULL,
  `doctor_user_id` INT NOT NULL,
  `insurance_co_user_id` INT NOT NULL,
  PRIMARY KEY (`id`, `owner_patient_user_id`, `author_user_id`, `doctor_user_id`, `insurance_co_user_id`),
  INDEX `fk_patient_health_record_patient1_idx` (`owner_patient_user_id` ASC),
  INDEX `fk_patient_health_record_hospital1_idx` (`author_user_id` ASC),
  INDEX `fk_patient_health_record_doctor1_idx` (`doctor_user_id` ASC),
  INDEX `fk_patient_health_record_insurance_co1_idx` (`insurance_co_user_id` ASC),
  CONSTRAINT `fk_patient_health_record_patient1`
    FOREIGN KEY (`owner_patient_user_id`)
    REFERENCES `access_control_DB`.`patient` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_patient_health_record_hospital1`
    FOREIGN KEY (`author_user_id`)
    REFERENCES `access_control_DB`.`author` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_patient_health_record_doctor1`
    FOREIGN KEY (`doctor_user_id`)
    REFERENCES `access_control_DB`.`doctor` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_patient_health_record_insurance_co1`
    FOREIGN KEY (`insurance_co_user_id`)
    REFERENCES `access_control_DB`.`insurance_co` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
