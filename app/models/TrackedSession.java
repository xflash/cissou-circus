package models;

import play.db.jpa.Model;

import javax.persistence.Entity;

/**
 * @author rcoqueugniot
 * @since 03.04.19
 */
@Entity
public class TrackedSession extends Model {
    private final String host;
    private final String remoteAddress;

    public TrackedSession(String host, String remoteAddress) {
        this.host = host;
        this.remoteAddress = remoteAddress;
    }
}
