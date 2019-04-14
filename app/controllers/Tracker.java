package controllers;

import models.TrackedAction;
import models.TrackedActionArgs;
import models.TrackedSession;
import play.Logger;
import play.db.jpa.JPABase;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;

import java.util.Arrays;
import java.util.Map;

/**
 * @author rcoqueugniot
 * @since 03.04.19
 */
public class Tracker extends Controller {

    private static final String TRACKER = "TRACKER";

    @Before
    public static void checkTrackingId() {
        String s = session.get(TRACKER);
        if (s==null) {
            Logger.info("Creating new session");
            session.put(TRACKER, ((TrackedSession)new TrackedSession(request.host, request.remoteAddress).save()).id);
        } else {
            TrackedSession existingSession = TrackedSession.findById(Long.valueOf(s));
            if(existingSession==null) {
                Logger.info("Recreating session");
                session.put(TRACKER, ((TrackedSession)new TrackedSession(request.host, request.remoteAddress).save()).id);
            }
        }
    }


    @After
    public static void after() {
        Logger.info("Tracker after", request.action);
        TrackedSession trackedSession = TrackedSession.findById(Long.valueOf(session.get("TRACKER")));

        TrackedAction trackedAction = new TrackedAction(trackedSession, request.action, request.date, request.querystring).save();

        for (Map.Entry<String, String[]> entry : request.params.all().entrySet()) {
            new TrackedActionArgs(trackedAction, entry.getKey(), Arrays.toString(entry.getValue())).save();
        }

    }
}
