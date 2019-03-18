package models;

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.*;

/**
 */
@Entity
public class SchoolEventProposal extends Model {

    public String name;

    @ManyToOne
    SchoolEvent schoolEvent;

    @OneToMany(mappedBy = "schoolEventProposal")
    @OrderBy("name")
    public List<SchoolEventGroup> groups=new ArrayList<>();

    public SchoolEventProposal(SchoolEvent schoolEvent, String name) {
        this.schoolEvent = schoolEvent;
        this.name = name;
    }
}
