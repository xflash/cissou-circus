package models;

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 * @author rcoqueugniot
 * @since 14.03.19
 */
@Entity
public class Classroom extends Model {

    @Column(unique = true)
    public
    String name;

    @Enumerated(EnumType.STRING)
    public ClassRoomKind kind;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Classroom classroom = (Classroom) o;
        return Objects.equals(name, classroom.name) &&
                kind == classroom.kind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, kind);
    }

    @OneToMany(mappedBy = "classroom")
    List<Student> students;

    public static Classroom createClassroom(String classe, ClassRoomKind kind) {
        Classroom classroom;
        classroom = new Classroom();
        classroom.name = classe;
        classroom.kind = kind;
        return classroom.save();
    }

    public static Classroom findByName(String classe) {
        return find("byName", classe).first();
    }
    public static List<Classroom> findByKind(ClassRoomKind kind) {
        return find("byKind", kind).fetch();
    }

    public static Classroom findOrCreate(ClassRoomKind classRoomKind, String classe) {
        Classroom classroom = findByName(classe);
        if (classroom == null) {
            classroom = createClassroom(classe, classRoomKind);
        }
        return classroom;
    }

    public static List<Classroom> findAllOrdered() {
        return find("select cr from Classroom cr order by cr.kind, cr.name").fetch();
    }
}
