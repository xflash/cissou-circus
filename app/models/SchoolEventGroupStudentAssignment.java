package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 */
@Entity
public class SchoolEventGroupStudentAssignment extends Model {

    @ManyToOne
    SchoolEventGroup schoolEventGroup;
    @ManyToOne
    SchoolEventActivity schoolEventActivity;
    @ManyToOne
    StudentChoices studentChoices;

    public SchoolEventGroupStudentAssignment(SchoolEventGroup schoolEventGroup, SchoolEventActivity schoolEventActivity, StudentChoices studentChoices) {
        this.schoolEventGroup = schoolEventGroup;
        this.studentChoices = studentChoices;
        this.schoolEventActivity = schoolEventActivity;
    }

    public static List<SchoolEventGroupStudentAssignment> list4GroupSchoolEventGroup(SchoolEventGroup schoolEventGroup) {
        return find("schoolEventGroup", schoolEventGroup).fetch();
    }
}
