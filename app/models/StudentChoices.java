package models;

import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.*;

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
    @ManyToOne
    SchoolEvent schoolEvent;

    public StudentChoices(Student student, SchoolEvent schoolEvent) {
        this.student = student;
        this.schoolEvent = schoolEvent;
    }

    public static StudentChoices createStudentChoices(Student student, String choix1, String choix2, String choix3, String choix4, SchoolEvent schoolEvent) {
//        Logger.info("Creating student choices for %s", student.identifiant);
        StudentChoices choices = new StudentChoices(student, schoolEvent);
        choices.choice1 = ActivityKind.findOrCreate(choix1, schoolEvent);
        choices.choice2 = ActivityKind.findOrCreate(choix2, schoolEvent);
        choices.choice3 = ActivityKind.findOrCreate(choix3, schoolEvent);
        choices.choice4 = ActivityKind.findOrCreate(choix4, schoolEvent);
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
        return StudentChoices.find("select sc from StudentChoices sc where sc." + s + " = :choice")
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

    public static Map<String, Set<StudentChoices>> buildSiblings(List<StudentChoices> studentChoicesList) {
        HashMap<String, Set<StudentChoices>> siblings = new HashMap<>();

        StudentChoices laststudentChoice = null;
        for (StudentChoices studentChoice : studentChoicesList) {
            if (laststudentChoice != null) {
                if (laststudentChoice.student.name.equals(studentChoice.student.name)) {
                    Set<StudentChoices> fra = siblings.computeIfAbsent(studentChoice.student.name, k -> new TreeSet<>(Comparator.comparing((StudentChoices o) -> o.student.firstname)
                    ));
                    fra.add(studentChoice);
                    fra.add(laststudentChoice);
                }
            }
            laststudentChoice = studentChoice;
        }
        return siblings;
    }


    public static List<StudentChoices> listStudentsChoicesInClassrooms(SchoolEvent schoolEvent, List<Long> classrooms) {
        return find(
                "select sc " +
                        "from StudentChoices as sc " +
                        "inner join sc.schoolEvent as se " +
                        "inner join sc.student as s " +
                        "inner join s.classroom as cr " +
                        "where 1=1 " +
                        "and se.id in :schoolEvent " +
                        "and cr.id in :classrooms " +
                        "and sc.choice1 is not null " +
                        "and sc.choice2 is not null " +
                        "order by s.name")
                .bind("schoolEvent", schoolEvent.id)
                .bind("classrooms", classrooms)
                .fetch();
    }
}
