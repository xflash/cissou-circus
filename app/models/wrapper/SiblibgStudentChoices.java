package models.wrapper;

import models.SchoolEventActivity;
import models.Student;
import models.StudentChoices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author rcoqueugniot
 * @since 17.03.19
 */
public class SiblibgStudentChoices {
    private final StudentChoices studentChoices;
    private final boolean sibling;

    public SiblibgStudentChoices(StudentChoices studentChoices, boolean sibling) {
        this.studentChoices = studentChoices;
        this.sibling = sibling;
    }

    public StudentChoices getStudentChoices() {
        return studentChoices;
    }

    public boolean isSibling() {
        return sibling;
    }

    public static List<SiblibgStudentChoices> wrapAll(Collection<StudentChoices> value, boolean isSibling) {
        return value.stream()
                .map(choices -> wrap(choices, isSibling))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static SiblibgStudentChoices wrap(StudentChoices studentChoice, boolean isSibling) {
        return new SiblibgStudentChoices(studentChoice, isSibling);
    }

    public Student getStudent() {
        return studentChoices.student;
    }
    public SchoolEventActivity getChoice1() {
        return studentChoices.choice1;
    }
    public SchoolEventActivity getChoice2() {
        return studentChoices.choice2;
    }
    public SchoolEventActivity getChoice3() {
        return studentChoices.choice3;
    }
    public SchoolEventActivity getChoice4() {
        return studentChoices.choice4;
    }
}
