package models;

import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author rcoqueugniot
 * @since 03.04.19
 */
@Entity
public class TrackedActionArgs extends Model {
    @ManyToOne
    TrackedAction trackedAction;

    String arg;

    @Column(length = 2024)
    String value;

    public TrackedActionArgs(TrackedAction trackedAction, String arg, String value) {
        this.trackedAction = trackedAction;
        this.arg = arg;
        this.value = value;
    }
}
