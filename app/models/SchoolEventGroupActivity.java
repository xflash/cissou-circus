package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 */
@Entity
public class SchoolEventGroupActivity extends Model {

    @ManyToOne(optional = false)
    SchoolEventGroup schoolEventGroup;

    @ManyToOne(optional = false)
    SchoolEventActivity schoolEventActivity;

    @OneToMany(mappedBy = "schoolEventGroupActivity")
    public Set<SchoolEventGroupStudentAssignment> assignments = new TreeSet<>(
            Comparator.comparing(o -> o.studentChoices.student.name)
    );

    public SchoolEventGroupActivity(SchoolEventGroup schoolEventGroup, SchoolEventActivity schoolEventActivity) {
        this.schoolEventGroup = schoolEventGroup;
        this.schoolEventActivity = schoolEventActivity;
    }

}
