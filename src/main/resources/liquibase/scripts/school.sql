-- liquibase formatted sql

-- changeset azhuravlev:1
create index student_name_index on student (name);

-- changeset azhuravlev:2
create index faculty_name_color_index on faculty (color, name);
