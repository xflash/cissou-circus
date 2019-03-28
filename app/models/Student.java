package models;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.*;

/**
 * @author rcoqueugniot
 * @since 14.03.19
 */
@Entity
public class Student extends Model {

    public String identifiant;

    public String name;

    public String firstname;

    @ManyToOne()
    public Classroom classroom;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Student student = (Student) o;
        return Objects.equals(identifiant, student.identifiant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), identifiant);
    }

    public static Map<String, Set<Student>> buildSiblings(List<Student> students) {
        Map<String, Set<Student>> fraties = new HashMap<>();

        Student laststudent = null;
        for (Student student : students) {
            if (laststudent != null) {
                if (laststudent.name.equals(student.name)) {
                    Set<Student> fra = fraties.computeIfAbsent(student.name, k -> new TreeSet<>(Comparator.comparing((Student o) -> o.firstname)));
                    fra.add(student);
                    fra.add(laststudent);
                }
            }
            laststudent = student;
        }
        return fraties;
    }

    public static long countByClassroom(Classroom classroom) {
        return count("classroom.id", classroom.id);
    }

    public static List<Student> findByClassroomOrdered(Classroom classroom) {
        return find("select s from Student s where classroom.id = ?1 order by name, firstname", classroom.id)
                .fetch();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("identifiant", identifiant)
                .append("firstname", firstname)
                .append("name", name)
                .toString();
    }

    public boolean isSiblingWith(Student student) {
        return name.equals(student.name);
    }
}
