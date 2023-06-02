-- time conversion example:
-- select now(),now()::timestamp,now()::timestamp - interval '1 hours';

-- input streams
create table if not exists processor_temperature
(
    id             bigserial
        primary key,
    processor_id   bigint       not null,
    temperature    integer      not null,
    insertion_date timestamp(6) not null
);

create table if not exists water_temperature
(
    id             bigserial
        primary key,
    rack_id        bigint       not null,
    temperature    integer      not null,
    insertion_date timestamp(6) not null
);

create table if not exists water_flow
(
    id              bigserial
        primary key,
    rack_id         bigint       not null,
    liters_per_hour integer      not null,
    insertion_date  timestamp(6) not null
);


-- average tables
create table if not exists processor_temperature_average
(
    id             bigserial
        primary key,
    processor_id   bigint       not null,
    temperature    integer      not null,
    insertion_date timestamp(6) not null
);

create table if not exists water_temperature_average
(
    id             bigserial
        primary key,
    rack_id        bigint       not null,
    temperature    integer      not null,
    insertion_date timestamp(6) not null
);

create table if not exists water_flow_average
(
    id              bigserial
        primary key,
    rack_id         bigint       not null,
    liters_per_hour integer      not null,
    insertion_date  timestamp(6) not null
);


-- incidents table
create table if not exists incident
(
    id             bigserial
        primary key,
    datacenter_id  bigint       not null,
    rack_id        bigint       not null,
    processor_id   bigint,
    incident_type  varchar(255) not null
        constraint incident_incident_type_check
            check ((incident_type)::text = ANY
                   ((ARRAY ['MAX_TEMP'::character varying, 'NO_FLOW'::character varying, 'BAD_CONTACT'::character varying])::text[])),
    incident_value varchar(255) not null,
    insertion_date timestamp(6) not null
);


-- triggers:
-- on insert processor reading:
-- if the average temp on a processor in the last 5 min was > 100 and no MAX_TEMP incident on that cpu -> incident

create or replace function check_processor_max_temp()
    RETURNS trigger
    language PLPGSQL
AS
$$
BEGIN
    RAISE NOTICE 'Value: %', NEW.temperature;
END;
$$;

CREATE TRIGGER processor_max_temp_check
    AFTER INSERT
    ON processor_temperature
EXECUTE PROCEDURE check_processor_max_temp();


-- on insert flow reading:
-- if the average flow in a waterBlock 5 min was < 5 and no NO_FLOW incident on that rack -> incident

-- on insert cpu temp average:
-- if the temp - last average temp of the waterBlock of the rack > 20 and no BAD_CONTACT incident on that cpu -> incident
