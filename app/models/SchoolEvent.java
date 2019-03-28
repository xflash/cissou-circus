package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import java.util.Objects;

@Entity
public class SchoolEvent extends Model {

    String name;

    public SchoolEvent(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SchoolEvent that = (SchoolEvent) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
