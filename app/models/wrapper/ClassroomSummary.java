package models.wrapper;

import models.ClassRoomKind;
import models.Classroom;
import models.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rcoqueugniot
 * @since 16.03.19
 */
public class ClassroomSummary {
    private final Classroom classroom;
    private final long nb;

    public ClassroomSummary(Classroom classroom, long nb) {
        this.classroom = classroom;
        this.nb = nb;
    }

    public static List<ClassroomSummary> wrap(List<Classroom> classrooms) {
        ArrayList<ClassroomSummary> res = new ArrayList<>();
        for (Classroom classroom : classrooms) {
            res.add(new ClassroomSummary(classroom, Student.countByClassroom(classroom)));
        }
        return res;
    }

    public long getId() {
        return classroom.id;
    }

    public String getName() {
        return classroom.name;
    }

    public ClassRoomKind getKind() {
        return classroom.kind;
    }

    public long getNb() {
        return nb;
    }
}
