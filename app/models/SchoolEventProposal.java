package models;

import play.db.jpa.Model;

import javax.persistence.*;
import java.beans.Transient;
import java.util.*;

/**
 */
@Entity
public class SchoolEventProposal extends Model {

    public String name;

    @ManyToOne
    SchoolEvent schoolEvent;

    Date creationDate;

    @OneToMany(mappedBy = "schoolEventProposal")
    @OrderBy("name")
    public List<SchoolEventGroup> groups=new ArrayList<>();

    public SchoolEventProposal(SchoolEvent schoolEvent, String name) {
        this.schoolEvent = schoolEvent;
        this.name = name;
        creationDate = new Date();
    }
    @Transient
    public  SchoolEventGroup guessNextGroup(SchoolEventGroup schoolEventGroup, int way) {
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
    public int getStudentCount(){
        int nb = 0;
        for (SchoolEventGroup group : groups) {
            nb += group.getStudentCount();
        }
        return nb;
    }

}
