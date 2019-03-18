package models;

import org.hibernate.annotations.SortComparator;
import play.db.jpa.Model;
import play.db.jpa.Transactional;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.beans.Transient;
import java.util.*;
import java.util.function.Function;

/**
 *
 */
@Entity
public class SchoolEventGroup extends Model {

    public String name;

    @ManyToOne(optional = false)
    SchoolEventProposal schoolEventProposal;

    @OneToMany(mappedBy = "schoolEventGroup")
//    @OrderBy("schoolEventActivity.name")
    @SortComparator(SchoolEventGroupActivityComparator.class)
    public Set<SchoolEventGroupActivity> activities=new TreeSet<>(new SchoolEventGroupActivityComparator());

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

    public static class SchoolEventGroupActivityComparator implements Comparator<SchoolEventGroupActivity> {
        @Override
        public int compare(SchoolEventGroupActivity o1, SchoolEventGroupActivity o2) {
            return o1.schoolEventActivity.name.compareTo(o2.schoolEventActivity.name);
        }
    }
}
