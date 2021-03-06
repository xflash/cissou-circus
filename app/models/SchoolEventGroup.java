package models;

import org.hibernate.annotations.SortComparator;
import play.db.jpa.Model;
import play.db.jpa.Transactional;

import javax.persistence.*;
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
    public
    SchoolEventProposal schoolEventProposal;

    @OneToMany(mappedBy = "schoolEventGroup")
    @SortComparator(SchoolEventGroupActivityComparator.class)
    public Set<SchoolEventGroupActivity> activities=new TreeSet<>(new SchoolEventGroupActivityComparator());

    public SchoolEventGroup(SchoolEventProposal schoolEventProposal, String name) {
        this.name = name;
        this.schoolEventProposal = schoolEventProposal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SchoolEventGroup that = (SchoolEventGroup) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(schoolEventProposal, that.schoolEventProposal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, schoolEventProposal);
    }

    public static List<SchoolEventGroup> list4Proposal(SchoolEventProposal schoolEventProposal) {
        return find("schoolEventProposal", schoolEventProposal).fetch();
    }

    @Transient
    public int getStudentCount(){
        int nb = 0;
        for (SchoolEventGroupActivity activity : activities) {
            nb+=activity.assignments.size();
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
