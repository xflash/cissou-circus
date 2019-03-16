package models.wrapper;

import models.Classroom;
import models.StudentChoices;

import java.util.List;

/**
 * @author rcoqueugniot
 * @since 16.03.19
 */
public class ClassroomStudentChoices {
    private final Classroom classroom;
    private final List<StudentChoices> studentChoices;

    public ClassroomStudentChoices(Classroom classroom, List<StudentChoices> studentChoices) {
        this.classroom = classroom;
        this.studentChoices = studentChoices;
    }
}
