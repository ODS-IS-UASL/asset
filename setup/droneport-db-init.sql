--
--Setting
--
--exec Settings
SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;
--tacle Settings
SET default_tablespace = '';
SET default_table_access_method = heap;

--
--SCHEMA
--
--CREATE SCHEMA tiger
CREATE SCHEMA IF NOT EXISTS tiger;
ALTER SCHEMA tiger OWNER TO droneroute;

--CREATE SCHEMA tiger_data
CREATE SCHEMA IF NOT EXISTS tiger_data;
ALTER SCHEMA tiger_data OWNER TO droneroute;

--CREATE SCHEMA topology
CREATE SCHEMA IF NOT EXISTS topology;
ALTER SCHEMA topology OWNER TO droneroute;
COMMENT ON SCHEMA topology IS 'PostGIS Topology schema';


--
--EXTENSION
--
--CREATE EXTENSION fuzzystrmatch
CREATE EXTENSION IF NOT EXISTS fuzzystrmatch WITH SCHEMA public;
COMMENT ON EXTENSION fuzzystrmatch IS 'determine similarities and distance between strings';

--CREATE EXTENSION postgis
CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;
COMMENT ON EXTENSION postgis IS 'PostGIS geometry and geography spatial types and functions';

--CREATE EXTENSION postgis_tiger_geocoder
CREATE EXTENSION IF NOT EXISTS postgis_tiger_geocoder WITH SCHEMA tiger;
COMMENT ON EXTENSION postgis_tiger_geocoder IS 'PostGIS tiger geocoder and reverse geocoder';

--CREATE EXTENSION postgis_topology
CREATE EXTENSION IF NOT EXISTS postgis_topology WITH SCHEMA topology;
COMMENT ON EXTENSION postgis_topology IS 'PostGIS topology spatial types and functions';

--CREATE EXTENSION "uuid-ossp"
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;
COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


--
--TABLE
--
--CREATE TABLE droneport_info
DROP TABLE IF EXISTS public.droneport_info; 
CREATE TABLE public.droneport_info (
  droneport_id varchar ,
  droneport_name varchar ,
  address varchar ,
  manufacturer varchar ,
  serial_number varchar ,
  port_type smallint ,
  vis_droneport_company_id varchar ,
  lat double precision ,
  lon double precision ,
  alt double precision ,
  support_drone_type varchar ,
  image_data bytea ,
  image_format varchar ,
  operator_id varchar ,
  update_user_id varchar ,
  create_time timestamp ,
  update_time timestamp ,
  public_flag boolean ,
  delete_flag boolean ,
  PRIMARY KEY (droneport_id)
);
ALTER TABLE public.droneport_info OWNER TO droneroute;

--CREATE TABLE droneport_status
DROP TABLE IF EXISTS public.droneport_status; 
CREATE TABLE public.droneport_status (
  droneport_id varchar ,
  active_status smallint ,
  inactive_status smallint ,
  inactive_time tsrange ,
  stored_aircraft_id UUID ,
  operator_id varchar ,
  update_user_id varchar ,
  create_time timestamp ,
  update_time timestamp ,
  delete_flag boolean ,
  PRIMARY KEY (droneport_id)
);
ALTER TABLE public.droneport_status OWNER TO droneroute;

--CREATE TABLE droneport_reserve_info
DROP TABLE IF EXISTS public.droneport_reserve_info; 
CREATE TABLE public.droneport_reserve_info (
  droneport_reservation_id UUID DEFAULT public.uuid_generate_v4(),
  group_reservation_id UUID ,
  droneport_id varchar ,
  aircraft_id UUID ,
  route_reservation_id UUID ,
  usage_type smallint ,
  reservation_time tsrange ,
  reservation_active_flag boolean NOT NULL,
  operator_id varchar ,
  reserve_provider_id UUID ,
  update_user_id varchar ,
  create_time timestamp ,
  update_time timestamp ,
  delete_flag boolean ,
  PRIMARY KEY (droneport_reservation_id)
);
ALTER TABLE public.droneport_reserve_info OWNER TO droneroute;

--CREATE TABLE vis_telemetry_info
DROP TABLE IF EXISTS public.vis_telemetry_info; 
CREATE TABLE public.vis_telemetry_info (
  droneport_id varchar ,
  droneport_ip_address varchar(15) ,
  droneport_name varchar ,
  droneport_status varchar ,
  vis_status varchar ,
  droneport_lat double precision ,
  droneport_lon double precision ,
  droneport_alt double precision ,
  wind_direction double precision ,
  wind_speed double precision ,
  maxinst_wind_direction double precision ,
  maxinst_wind_speed double precision ,
  rainfall double precision ,
  temp double precision ,
  humidity double precision ,
  pressure double precision ,
  illuminance double precision ,
  ultraviolet double precision ,
  observation_time timestamp ,
  invasion_flag boolean ,
  invasion_category varchar ,
  threshold_wind_speed double precision ,
  base_id varchar ,
  base_address varchar ,
  base_name varchar ,
  base_status varchar ,
  usage smallint ,
  error_code varchar ,
  error_reason varchar ,
  PRIMARY KEY (droneport_id)
);
ALTER TABLE public.vis_telemetry_info OWNER TO droneroute;

--CREATE TABLE aircraft_info
DROP TABLE IF EXISTS public.aircraft_info; 
CREATE TABLE public.aircraft_info (
  aircraft_id UUID DEFAULT public.uuid_generate_v4() NOT NULL,
  aircraft_name varchar,
  manufacturer varchar,
  model_number varchar,
  model_name varchar,
  manufacturing_number varchar,
  aircraft_type smallint,
  max_takeoff_weight double precision,
  body_weight double precision,
  max_flight_speed double precision,
  max_flight_time double precision,
  lat double precision,
  lon double precision,
  certification boolean,
  dips_registration_code varchar,
  owner_type smallint,
  owner_id UUID,
  image_data bytea,
  image_format varchar,
  public_flag boolean,
  operator_id varchar,
  update_user_id varchar,
  create_time timestamp,
  update_time timestamp,
  delete_flag boolean,
  PRIMARY KEY (aircraft_id)
);
ALTER TABLE public.aircraft_info OWNER TO droneroute;

--CREATE TABLE aircraft_reserve_info
DROP TABLE IF EXISTS public.aircraft_reserve_info; 
CREATE TABLE public.aircraft_reserve_info (
  aircraft_reserve_id UUID DEFAULT public.uuid_generate_v4() NOT NULL,
  group_reservation_id UUID,
  aircraft_id UUID,
  reservation_time tsrange,
  reserve_provider_id UUID,
  operator_id varchar,
  update_user_id varchar,
  create_time timestamp,
  update_time timestamp,
  delete_flag boolean,
  PRIMARY KEY (aircraft_reserve_id)
);
ALTER TABLE public.aircraft_reserve_info OWNER TO droneroute;

--CREATE TABLE file_info
DROP TABLE IF EXISTS public.file_info; 
CREATE TABLE public.file_info (
  file_id UUID DEFAULT public.uuid_generate_v4() NOT NULL,
  aircraft_id UUID NOT NULL,
  file_number smallint NOT NULL,
  file_logical_name varchar(100) NOT NULL,
  file_physical_name varchar(200) NOT NULL,
  file_data bytea NOT NULL,
  file_format varchar NOT NULL,
  operator_id varchar,
  update_user_id varchar,
  create_time timestamp,
  update_time timestamp,
  delete_flag boolean,
  PRIMARY KEY (file_id)
);
ALTER TABLE public.file_info OWNER TO droneroute;

--CREATE TABLE payload_info
DROP TABLE IF EXISTS public.payload_info; 
CREATE TABLE public.payload_info (
  payload_id UUID DEFAULT public.uuid_generate_v4() NOT NULL,
  aircraft_id UUID NOT NULL,
  payload_number smallint NOT NULL,
  payload_name varchar(100) NOT NULL,
  payload_detail_text varchar(1000),
  image_data bytea,
  image_format varchar,
  file_physical_name varchar(200),
  file_data bytea,
  file_format varchar,
  operator_id varchar,
  update_user_id varchar,
  create_time timestamp,
  update_time timestamp,
  delete_flag boolean,
  PRIMARY KEY (payload_id)
);
ALTER TABLE public.payload_info OWNER TO droneroute;

--CREATE TABLE price_info
DROP TABLE IF EXISTS public.price_info; 
CREATE TABLE public.price_info (
  price_id UUID DEFAULT public.uuid_generate_v4() NOT NULL,
  resource_id varchar NOT NULL,
  resource_type smallint NOT NULL,
  primary_route_operator_id varchar NOT NULL,
  price_type smallint NOT NULL,
  price_per_unit integer NOT NULL,
  price integer NOT NULL,
  effective_time tsrange NOT NULL,
  priority integer NOT NULL,
  operator_id varchar,
  update_user_id varchar,
  create_time timestamp,
  update_time timestamp,
  delete_flag boolean,
  PRIMARY KEY (price_id)
);
ALTER TABLE public.price_info OWNER TO droneroute;

--CREATE TABLE price_history_info
DROP TABLE IF EXISTS public.price_history_info; 
CREATE TABLE public.price_history_info (
  price_history_id UUID DEFAULT public.uuid_generate_v4() NOT NULL,
  price_id UUID NOT NULL,
  resource_id varchar NOT NULL,
  resource_type smallint NOT NULL,
  primary_route_operator_id varchar NOT NULL,
  price_type smallint NOT NULL,
  price_per_unit integer NOT NULL,
  price integer NOT NULL,
  effective_time tsrange NOT NULL,
  priority integer NOT NULL,
  operator_id varchar,
  update_user_id varchar,
  create_time timestamp,
  update_time timestamp,
  delete_flag boolean,
  PRIMARY KEY (price_history_id)
);
ALTER TABLE public.price_history_info OWNER TO droneroute;


--
--SEQUENCE
--
--CREATE SEQUENCE droneport_id_sequence
DROP SEQUENCE IF EXISTS public.droneport_id_sequence; 
CREATE SEQUENCE public.droneport_id_sequence START 1;
