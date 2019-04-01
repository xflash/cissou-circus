package models;

import play.db.jpa.Model;

import javax.persistence.*;
import java.beans.Transient;
import java.util.*;
import java.util.function.Consumer;

/**
 *
 */
@Entity
public class SchoolEventProposal extends Model {

    public String name;
    @OneToMany(mappedBy = "schoolEventProposal")
    @OrderBy("name")
    public List<SchoolEventGroup> groups = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    SchoolEvent schoolEvent;
    Date creationDate;

    public SchoolEventProposal(SchoolEvent schoolEvent, String name) {
        this.schoolEvent = schoolEvent;
        this.name = name;
        creationDate = new Date();
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public static SchoolEventProposal loadById(long id) {
        return findById(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SchoolEventProposal that = (SchoolEventProposal) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(schoolEvent, that.schoolEvent) &&
                Objects.equals(creationDate, that.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, schoolEvent, creationDate);
    }

    @Transient
    public SchoolEventGroup guessNextGroup(SchoolEventGroup schoolEventGroup, int way) {
        int i1 = this.groups.indexOf(schoolEventGroup);
        i1 += way;
        if (i1 >= this.groups.size())
            i1 = 0;
        else if (i1 < 0)
            i1 = this.groups.size() - 1;
        int i = i1;
        return this.groups.get(i);
    }

    @Transient
    public int getStudentCount() {
        int nb = 0;
        for (SchoolEventGroup group : groups) {
            nb += group.getStudentCount();
        }
        return nb;
    }


    public void forEachAssignment(Consumer<SchoolEventGroupStudentAssignment> consumer) {
        for (SchoolEventGroup group : this.groups) {
            for (SchoolEventGroupActivity activity : group.activities) {
                for (SchoolEventGroupStudentAssignment assignment : activity.assignments) {
                    consumer.accept(assignment);
                }
            }
        }
    }

}
