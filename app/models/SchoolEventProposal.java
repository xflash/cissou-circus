package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 */
@Entity
public class SchoolEventProposal extends Model {

    public String name;

    @ManyToOne
    SchoolEvent schoolEvent;

    public SchoolEventProposal(SchoolEvent schoolEvent) {
        this.schoolEvent = schoolEvent;
    }
}
