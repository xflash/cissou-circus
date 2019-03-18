package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 */
@Entity
public class SchoolEventProposal extends Model {

    public String name;

    @ManyToOne
    SchoolEvent schoolEvent;

    @OneToMany(mappedBy = "schoolEventProposal")
    public Set<SchoolEventGroup> groups=new TreeSet<>(Comparator.comparing(o -> o.name));

    public SchoolEventProposal(SchoolEvent schoolEvent) {
        this.schoolEvent = schoolEvent;
    }
}
