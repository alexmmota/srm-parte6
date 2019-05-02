-- -----------------------------------------------------
-- Table `db_srm_estoque`.`srm_product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `db_srm_estoque`.`srm_product` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(400) NULL,
  `bar_code` VARCHAR(200) NULL,
  `department` VARCHAR(100) NULL,
  `amount` INT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;
