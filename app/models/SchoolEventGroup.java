package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 */
@Entity
public class SchoolEventGroup extends Model {

    public String name;

    @ManyToOne
    SchoolEventProposal schoolEventProposal;

    public SchoolEventGroup(SchoolEventProposal schoolEventProposal, String name) {
        this.name = name;
        this.schoolEventProposal = schoolEventProposal;
    }

    public static List<SchoolEventGroup> list4Proposal(SchoolEventProposal schoolEventProposal) {
        return find("schoolEventProposal", schoolEventProposal).fetch();
    }
}
