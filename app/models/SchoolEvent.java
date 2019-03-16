package models;

import play.db.jpa.Model;

import javax.persistence.Entity;

@Entity
public class SchoolEvent extends Model {

    String name;

    public SchoolEvent(String name) {
        this.name = name;
    }
}
