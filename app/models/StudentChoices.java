package models;

import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class StudentChoices extends Model {

    @ManyToOne
    public Student student;

    @ManyToOne(cascade = CascadeType.ALL)
    public
    ActivityKind choice1;
    @ManyToOne(cascade = CascadeType.ALL)
    public
    ActivityKind choice2;
    @ManyToOne(cascade = CascadeType.ALL)
    public
    ActivityKind choice3;
    @ManyToOne(cascade = CascadeType.ALL)
    public
    ActivityKind choice4;

    public static StudentChoices createStudentChoices(Student student, String choix1, String choix2, String choix3, String choix4) {
//        Logger.info("Creating student choices for %s", student.identifiant);
        StudentChoices choices = new StudentChoices();
        choices.student = student;
        choices.choice1 = ActivityKind.findOrCreate(choix1);
        choices.choice2 = ActivityKind.findOrCreate(choix2);
        choices.choice3 = ActivityKind.findOrCreate(choix3);
        choices.choice4 = ActivityKind.findOrCreate(choix4);
        return choices.save();
    }

    public static List<StudentChoices> findAllChoice1(ActivityKind activityKind) {
        return findAllChoice(activityKind, "choice1");
    }
    public static List<StudentChoices> findAllChoice2(ActivityKind activityKind) {
        return findAllChoice(activityKind, "choice2");
    }
    public static List<StudentChoices> findAllChoice3(ActivityKind activityKind) {
        return findAllChoice(activityKind, "choice3");
    }
    public static List<StudentChoices> findAllChoice4(ActivityKind activityKind) {
        return findAllChoice(activityKind, "choice4");
    }

    private static List<StudentChoices> findAllChoice(ActivityKind activityKind, String s) {
        return StudentChoices.find("select sc from StudentChoices sc where sc."+ s +" = :choice")
                .bind("choice", activityKind)
                .fetch();
    }

    public static List<Student> getStudents(List<StudentChoices> studentChoices) {
        ArrayList<Student> students = new ArrayList<>();
        for (StudentChoices studentChoice : studentChoices) {
            students.add(studentChoice.student);
        }
        return students;
    }
}
