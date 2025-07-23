create table drivers (
id serial primary key,
name varchar(100) not null,
age integer check (age >= 18),
has_license boolean default false);

create table cars (
id serial primary key,
brand varchar(100) not null,
model varchar(100) not null,
coast numeric(12, 2) not null
);

create table drivers_cars (
id serial primary key,
id_driver integer not null,
id_car integer not null,
constraint fk_driver foreign key (id_driver) references drivers(id),
constraint fk_car foreign key (id_car)references cars(id),
constraint unique_combination unique (id_driver, id_car)
);

SELECT table_name
FROM information_schema.tables
WHERE table_schema='public';

insert into drivers (name, age, has_license) values ('Rick', 55, true);
select *  from drivers;

insert into cars (brand, model, coast) values ('Cadillac', 'Devill', 3000000.00);
select * from cars;

insert into drivers_cars (id_driver, id_car) values (1, 1);
select * from drivers_cars;

select
    drivers.name,
    cars.brand,
    cars.model
from drivers_cars
join drivers on drivers_cars.id_driver = drivers.id
join cars on drivers_cars.id_car = cars.id;