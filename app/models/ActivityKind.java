package models;

import org.apache.commons.lang.StringUtils;
import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
//@Table(uniqueConstraints = {
//        @UniqueConstraint(columnNames = "name")
//})
public class ActivityKind extends Model {

    @Column(unique = true)
    String name;

    public static ActivityKind findByName(String name) {
        return ActivityKind.find("byName", name).first();
    }

    public static ActivityKind create(String name) {
        ActivityKind activityKind = new ActivityKind();
        activityKind.name=name;
        return activityKind.save();
    }

    static ActivityKind findOrCreate(String choix) {
        if (StringUtils.isNotBlank(choix)) {
            ActivityKind activityKind = findByName(choix);
            return activityKind == null ? create(choix) : activityKind;
        } else
            return null;
    }
}
