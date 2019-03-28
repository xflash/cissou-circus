package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 *
 */
@Entity
public class SchoolEventGroupStudentAssignment extends Model {

    @ManyToOne
    public
    SchoolEventGroupActivity schoolEventGroupActivity;

    @ManyToOne
    public
    StudentChoices studentChoices;

    public SchoolEventGroupStudentAssignment(SchoolEventGroupActivity schoolEventGroupActivity, StudentChoices studentChoices) {
        this.schoolEventGroupActivity = schoolEventGroupActivity;
        this.studentChoices = studentChoices;
    }

    public static List<SchoolEventGroupStudentAssignment> listByClassRoomAndProposal(long proposalId, long classroomId) {
        return find("select segsa " +
                "from SchoolEventGroupStudentAssignment segsa " +
                "inner join segsa.schoolEventGroupActivity sega " +
                "inner join sega.schoolEventGroup seg " +
                "inner join seg.schoolEventProposal sep " +
                "inner join segsa.studentChoices sc " +
                "inner join sc.student s " +
                "inner join s.classroom cr " +
                "where 1=1 " +
                "and cr.id = :classroomId " +
                "and sep.id = :proposalId ")
                .bind("classroomId", classroomId)
                .bind("proposalId", proposalId)
                .fetch();
    }
}
