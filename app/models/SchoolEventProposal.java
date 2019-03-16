package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author rcoqueugniot
 * @since 16.03.19
 */
@Entity
public class SchoolEventGroupProposal extends Model {

    public String name;

    @ManyToOne
    SchoolEvent schoolEvent;

    public SchoolEventGroupProposal(SchoolEvent schoolEvent) {
        this.schoolEvent = schoolEvent;
    }
}
