package models;

import org.apache.commons.lang.StringUtils;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.*;

@Entity
public class StudentChoices extends Model {

    @ManyToOne(optional = false)
    public Student student;
    @ManyToOne(cascade = CascadeType.ALL)
    public
    SchoolEventActivity choice1;
    @ManyToOne(cascade = CascadeType.ALL)
    public
    SchoolEventActivity choice2;
    @ManyToOne(cascade = CascadeType.ALL)
    public
    SchoolEventActivity choice3;
    @ManyToOne(cascade = CascadeType.ALL)
    public
    SchoolEventActivity choice4;
    @ManyToOne
    SchoolEvent schoolEvent;
    private boolean orangeSheet;
    public boolean absentFriday;
    public boolean absentSaturday;
    public boolean absent;

    public StudentChoices(Student student, SchoolEvent schoolEvent) {
        this.student = student;
        this.schoolEvent = schoolEvent;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StudentChoices that = (StudentChoices) o;
        return Objects.equals(student, that.student) &&
                Objects.equals(choice1, that.choice1) &&
                Objects.equals(choice2, that.choice2) &&
                Objects.equals(choice3, that.choice3) &&
                Objects.equals(choice4, that.choice4) &&
                Objects.equals(schoolEvent, that.schoolEvent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), student, choice1, choice2, choice3, choice4, schoolEvent);
    }

    public static StudentChoices createStudentChoices(SchoolEvent schoolEvent, Student student,
                                                      String choix1, String choix2, String choix3, String choix4,
                                                      String orangesSheet,
                                                      String absentFriday,
                                                      String absentSaturday,
                                                      String absent) {
//        Logger.info("Creating student choices for %s", student.identifiant);
        StudentChoices choices = new StudentChoices(student, schoolEvent);
        choices.choice1 = SchoolEventActivity.findOrCreate(choix1, schoolEvent);
        choices.choice2 = SchoolEventActivity.findOrCreate(choix2, schoolEvent);
        choices.choice3 = SchoolEventActivity.findOrCreate(choix3, schoolEvent);
        choices.choice4 = SchoolEventActivity.findOrCreate(choix4, schoolEvent);

        choices.orangeSheet = StringUtils.isNotBlank(orangesSheet);
        choices.absentFriday = StringUtils.isNotBlank(absentFriday);
        choices.absentSaturday = StringUtils.isNotBlank(absentSaturday);
        choices.absent = StringUtils.isNotBlank(absent);
        return choices.save();
    }

    public static List<StudentChoices> findInAllChoices(SchoolEventActivity schoolEventActivity) {
        return StudentChoices.find("select sc from StudentChoices sc where " +
                "1=1 " +
                "or sc.choice1 = :choice " +
                "or sc.choice2 = :choice " +
                "or sc.choice3 = :choice " +
                "or sc.choice4 = :choice ")
                .bind("choice", schoolEventActivity)
                .fetch();
    }

    public static List<StudentChoices> findAllChoice1(SchoolEventActivity schoolEventActivity) {
        return findAllChoice(schoolEventActivity, "choice1");
    }

    public static List<StudentChoices> findAllChoice2(SchoolEventActivity schoolEventActivity) {
        return findAllChoice(schoolEventActivity, "choice2");
    }

    public static List<StudentChoices> findAllChoice3(SchoolEventActivity schoolEventActivity) {
        return findAllChoice(schoolEventActivity, "choice3");
    }

    public static List<StudentChoices> findAllChoice4(SchoolEventActivity schoolEventActivity) {
        return findAllChoice(schoolEventActivity, "choice4");
    }

    private static List<StudentChoices> findAllChoice(SchoolEventActivity schoolEventActivity, String s) {
        return StudentChoices.find("select sc from StudentChoices sc where sc." + s + " = :choice")
                .bind("choice", schoolEventActivity)
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

    public static List<StudentChoices> listAllStudentsChoicesInClassroom(SchoolEvent schoolEvent, Long classroomid) {
        return find(
                "select sc " +
                        "from StudentChoices as sc " +
                        "inner join sc.schoolEvent as se " +
                        "inner join sc.student as s " +
                        "inner join s.classroom as cr " +
                        "where 1=1 " +
                        "and se.id in :schoolEvent " +
                        "and cr.id = :classroomid " +
                        "order by s.name")
                .bind("schoolEvent", schoolEvent.id)
                .bind("classroomid", classroomid)
                .fetch();

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
                        "and (sc.absent is null or sc.absent = false)" +
                        "and sc.choice1 is not null " +
                        "and sc.choice2 is not null " +
                        "and sc.choice3 is not null " +
                        "and sc.choice4 is not null " +
                        "order by s.name")
                .bind("schoolEvent", schoolEvent.id)
                .bind("classrooms", classrooms)
                .fetch();
    }

    public SchoolEventActivity getChoice1() {
        return choice1;
    }

    public SchoolEventActivity getChoice2() {
        return choice2;
    }

    public SchoolEventActivity getChoice3() {
        return choice3;
    }

    public SchoolEventActivity getChoice4() {
        return choice4;
    }

    public boolean isAbsentFriday() {
        return absentFriday;
    }

    public boolean isAbsentSaturday() {
        return absentSaturday;
    }
}
