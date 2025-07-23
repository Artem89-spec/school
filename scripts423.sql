select student.name, student.age, faculty.name from student inner join faculty on student.faculty_id = faculty.id;

select * from avatar;
select student.name, student.age, avatar.file_path, avatar.file_size, avatar.media_type from student
inner join avatar on student.id = avatar.student_id;