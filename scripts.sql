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






