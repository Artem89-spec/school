select * from student;
update student set "age" = 23 where id = 1;
update student set "age" = 20 where id = 5;
select * from student;
select * from student where age < 16;

alter table student add constrant age_constraint check (age >= 16);
insert into student (id, age, name, faculty_id) values (9, 16, 'Draco Malfoy', 4);
insert into student (id, age, name, faculty_id) values (10, 15, 'Harry', 1);
/**Error executing INSERT statement. ОШИБКА: новая строка в отношении "student" нарушает ограничение-проверку "age_constraint"
  Detail: Ошибочная строка содержит (10, 15, Harry, 1).*/

alter table student add constraint unique_name unique (name);
insert into student (id, age, name, faculty_id) values (10, 17, 'Draco Malfoy', 1);
/**Error executing INSERT statement. ОШИБКА: повторяющееся значение ключа нарушает ограничение уникальности "unique_name"
  Detail: Ключ "(name)=(Draco Malfoy)" уже существует.*/

alter table faculty add constraint unique_name_color unique (name, color);
insert into faculty (id, color, name) values (5, 'blue', 'Ravenclaw');
/**Error executing INSERT statement. ОШИБКА: повторяющееся значение ключа нарушает ограничение уникальности "unique_name_color"
  Detail: Ключ "(name, color)=(Ravenclaw, blue)" уже существует.*/

alter table student drop constraint age_constraint;
alter table student alter column age set default 20;
alter table student add constraint age_constraint check (age >= 16);
insert into student (id, name, faculty_id) values (10, 'Sam', 3);

