select * from student;
select * from faculty;
insert into student (id, age, name) values (3, 18, 'Bob');
insert into faculty (id, color, name) values (3, 'blue', 'Ravenclaw');
select *  from student where age between 15 and 17;
select name from student;
select * from student where name like '%o%';
select * from student where age < id;
select * from student order by age;
select * from student order by age desc;
ALTER TABLE public.student ADD COLUMN faculty_id BIGINT;
select * from student, faculty where student.faculty_id = faculty."id" and faculty.color = 'red';
select s.* from student as s, faculty as f where s.faculty_id = f."id" and f."id" = 3;
SELECT table_schema, table_name FROM information_schema.tables WHERE table_name='student';

SELECT * FROM student WHERE id = 3;
SELECT * FROM faculty WHERE id = 3;
UPDATE student SET faculty_id = 3 WHERE id = 3;
SELECT * FROM student WHERE id = 3;

insert into faculty (id, color, name) values (1, 'red', 'Gryffindor');
insert into student (id, age, name, faculty_id) values (1, 15, 'John', 1);

insert into faculty (id, color, name) values (2, 'yellow', 'Hufflepuff');
insert into student (id, age, name, faculty_id) values (2, 19, 'Scott', 2);

insert into faculty (id, color, name) values (4, 'green', 'Slytherin');
insert into student (id, age, name, faculty_id) values (4, 22, 'Alex', 4);

insert into student (id, age, name, faculty_id) values (5, 14, 'Helen', 1);
insert into student (id, age, name, faculty_id) values (6, 17, 'Bill', 2);
insert into student (id, age, name, faculty_id) values (7, 19, 'Fill', 3);
insert into student (id, age, name, faculty_id) values (8, 21, 'Scarlett', 4);

select * from avatar;
DELETE FROM avatar WHERE student_id = 1;






