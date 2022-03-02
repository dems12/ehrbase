-- This script needs to be run as database superuser in order to create the database
-- and its required extensions.
-- These operations can not be run by Flyway as they require super user privileged
-- and/or can not be installed inside a transaction.
--
-- Extentions are installed in a separate schema called 'ext'
--
-- For production servers these operations should be performed by a configuration
-- management system.
--
-- If the username, password or database is changed, they also need to be changed
-- in the root build.gradle file.
--
-- On *NIX run this using:
--
--   sudo -u postgres psql < createdb.sql
--
-- You only have to run this script once.
--
-- THIS WILL NOT CREATE THE ENTIRE DATABASE!
-- It only contains those operations which require superuser privileges.
-- The actual database schema is managed by flyway.
--
 -- create database and roles (you might see an error here, these can be ignored)

CREATE ROLE EHRBASE WITH LOGIN PASSWORD 'ehrbase';


CREATE DATABASE EHRBASE ENCODING 'UTF-8' TEMPLATE TEMPLATE0;

GRANT ALL PRIVILEGES ON DATABASE EHRBASE TO EHRBASE;

-- install the extensions
C:\EHRBASE
CREATE SCHEMA IF NOT EXISTS EHR
AUTHORIZATION EHRBASE;


CREATE SCHEMA IF NOT EXISTS EXT
AUTHORIZATION EHRBASE;


CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA EXT;


CREATE EXTENSION IF NOT EXISTS "temporal_tables" SCHEMA EXT;


CREATE EXTENSION IF NOT EXISTS "jsquery" SCHEMA EXT;


CREATE EXTENSION IF NOT EXISTS "ltree" SCHEMA EXT;

--
-- setup the search_patch so the extensions can be found

ALTER DATABASE EHRBASE
SET SEARCH_PATH TO "$user",
	PUBLIC,
	EXT;

-- ensure INTERVAL is ISO8601 encoded

ALTER DATABASE EHRBASE
SET INTERVALSTYLE = 'iso_8601';

GRANT ALL ON ALL FUNCTIONS IN SCHEMA EXT TO EHRBASE;