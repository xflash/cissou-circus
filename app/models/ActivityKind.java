package models;

import org.apache.commons.lang.StringUtils;
import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.List;

@Entity
public class ActivityKind extends Model {

    @Column(unique = true)
    public
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
            choix = StringUtils.trim(choix);
            ActivityKind activityKind = findByName(choix);
            return activityKind == null ? create(choix) : activityKind;
        } else
            return null;
    }

    public static List<ActivityKind> listAllOrdered() {
        return find("select ak from ActivityKind ak order by ak.name").fetch();
    }
}
