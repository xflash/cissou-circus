package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

/**
 * @author rcoqueugniot
 * @since 03.04.19
 */
@Entity
public class TrackedAction extends Model {
    @ManyToOne
    TrackedSession trackedSession;

    String action;
    Date date;
    private final String querystring;

    public TrackedAction(TrackedSession trackedSession, String action, Date date, String querystring) {
        this.trackedSession = trackedSession;
        this.action = action;
        this.date = date;
        this.querystring = querystring;
    }

    public static List<TrackedAction> findAllByTrackedSession(Long id) {
        return find("select ta from TrackedAction ta where ta.trackedSession.id = :id")
                .bind("id", id)
                .fetch();
    }
}
