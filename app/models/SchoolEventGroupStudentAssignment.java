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
    SchoolEventGroupActivity schoolEventGroupActivity;

    @ManyToOne
    StudentChoices studentChoices;

    public SchoolEventGroupStudentAssignment(SchoolEventGroupActivity schoolEventGroupActivity, StudentChoices studentChoices) {
        this.schoolEventGroupActivity = schoolEventGroupActivity;
        this.studentChoices = studentChoices;
    }

//    public static List<SchoolEventGroupStudentAssignment> list4GroupSchoolEventGroup(SchoolEventGroup schoolEventGroup) {
//        return find("schoolEventGroup", schoolEventGroup).fetch();
//    }
}
