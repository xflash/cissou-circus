package models;

import play.db.jpa.Model;
import play.db.jpa.Transactional;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.beans.Transient;
import java.util.*;

/**
 *
 */
@Entity
public class SchoolEventGroup extends Model {

    public String name;

    @ManyToOne(optional = false)
    SchoolEventProposal schoolEventProposal;

    @OneToMany(mappedBy = "schoolEventGroup")
    public Set<SchoolEventGroupActivity> activities=new TreeSet<>(Comparator.comparing(o -> o.schoolEventActivity.name));

    public SchoolEventGroup(SchoolEventProposal schoolEventProposal, String name) {
        this.name = name;
        this.schoolEventProposal = schoolEventProposal;
    }

    public static List<SchoolEventGroup> list4Proposal(SchoolEventProposal schoolEventProposal) {
        return find("schoolEventProposal", schoolEventProposal).fetch();
    }

    @Transient
    public int getStudentCount(){
        int nb = 0;
        for (SchoolEventGroupActivity activity : activities) {
            for (SchoolEventGroupStudentAssignment assignment : activity.assignments) {
                nb++;
            }
        }
        return nb;
    }
}
