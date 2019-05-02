-- -----------------------------------------------------
-- Table `db_srm_routeservice`.`abtesting`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `db_srm_routeservice`.`abtesting` (
  `service_name` VARCHAR(100) NOT NULL,
  `active` VARCHAR(10) NULL,
  `endpoint` VARCHAR(500) NULL,
  `weight` INT NULL,
  PRIMARY KEY (`service_name`))
ENGINE = InnoDB;
