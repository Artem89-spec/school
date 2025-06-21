package ru.hogwarts.school.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Faculty {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String color;

    public Faculty(){
    }

    public Faculty(long id, String name,  String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Факультет: " + "id: " + id + "Название: " + name + "Цвет: " + color;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if(other == null || this.getClass() != other.getClass()) {
            return false;
        }
        Faculty object = (Faculty) other;
        return id == object.id && Objects.equals(name, object.name) && Objects.equals(color, object.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
