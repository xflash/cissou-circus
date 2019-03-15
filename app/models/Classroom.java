package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * @author rcoqueugniot
 * @since 14.03.19
 */
@Entity
public class Classroom extends Model {
    public String name;

    @Enumerated(EnumType.STRING)
    public ClassRoomKind kind;

    @OneToMany(mappedBy = "classroom")
    List<Student> students;

    public static Classroom createClassroom(String classe, ClassRoomKind kind) {
        Classroom classroom;
        classroom = new Classroom();
        classroom.name = classe;
        classroom.kind = kind;
        return classroom.save();
    }
}
