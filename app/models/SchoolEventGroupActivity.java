package models;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.annotations.SortComparator;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 */
@Entity
public class SchoolEventGroupActivity extends Model {

    @ManyToOne(optional = false)
    public SchoolEventGroup schoolEventGroup;

    @ManyToOne(optional = false)
    public SchoolEventActivity schoolEventActivity;

    @OneToMany(mappedBy = "schoolEventGroupActivity")
    @SortComparator(SchoolEventGroupStudentAssignmentComparator.class)
    public Set<SchoolEventGroupStudentAssignment> assignments = new TreeSet<>(new SchoolEventGroupStudentAssignmentComparator());

    public SchoolEventGroupActivity(SchoolEventGroup schoolEventGroup, SchoolEventActivity schoolEventActivity) {
        this.schoolEventGroup = schoolEventGroup;
        this.schoolEventActivity = schoolEventActivity;
    }

    public static class SchoolEventGroupStudentAssignmentComparator implements Comparator<SchoolEventGroupStudentAssignment> {
        @Override
        public int compare(SchoolEventGroupStudentAssignment o1, SchoolEventGroupStudentAssignment o2) {
            return new CompareToBuilder()
                    .append(o1.studentChoices.student.classroom.kind.ordinal(), o2.studentChoices.student.classroom.kind.ordinal())
                    .append(o1.studentChoices.student.classroom.name, o2.studentChoices.student.classroom.name)
                    .append(o1.studentChoices.student.name, o2.studentChoices.student.name)
                    .append(o1.studentChoices.student.firstname, o2.studentChoices.student.firstname)
                    .toComparison();
        }
    }
}
