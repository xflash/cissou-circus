package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.List;

@Entity
public class StudentChoices extends Model {

    @OneToOne(mappedBy = "choices")
    public
    Student student;

    @ManyToOne
    public
    ActivityKind choice1;
    @ManyToOne
    public
    ActivityKind choice2;
    @ManyToOne
    public
    ActivityKind choice3;
    @ManyToOne
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

    public static List<Student> findAllChoice1(ActivityKind activityKind) {
        return findAllChoice(activityKind, "choice1");
    }
    public static List<Student> findAllChoice2(ActivityKind activityKind) {
        return findAllChoice(activityKind, "choice2");
    }
    public static List<Student> findAllChoice3(ActivityKind activityKind) {
        return findAllChoice(activityKind, "choice3");
    }
    public static List<Student> findAllChoice4(ActivityKind activityKind) {
        return findAllChoice(activityKind, "choice4");
    }

    private static List<Student> findAllChoice(ActivityKind activityKind, String s) {
        return StudentChoices.find("select sc.student from StudentChoices sc where sc."+ s +" = :choice")
                .bind("choice", activityKind)
                .fetch();
    }
}
