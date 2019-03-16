package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @since 16.03.19
 */
@Entity
public class SchoolEventGroup extends Model {

    public String name;

    @ManyToOne
    SchoolEventGroupProposal schoolEventGroupProposal;

    public SchoolEventGroup(SchoolEventGroupProposal schoolEventGroupProposal, String name) {
        this.name = name;
        this.schoolEventGroupProposal = schoolEventGroupProposal;
    }
}
