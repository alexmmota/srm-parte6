SET GLOBAL sql_mode = '';
CREATE DATABASE IF NOT EXISTS db_srm_estoque;
CREATE DATABASE IF NOT EXISTS db_srm_routeservice;
CREATE USER IF NOT EXISTS 'srm'@'%' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON db_srm_estoque.* TO 'srm'@'%';
GRANT ALL PRIVILEGES ON db_srm_routeservice.* TO 'srm'@'%';
FLUSH PRIVILEGES;

