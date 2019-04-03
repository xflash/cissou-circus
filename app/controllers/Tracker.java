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

    @Before
    public static void checkTrackingId() {
        if (!session.contains("TRACKER")) {
            Logger.info("Creating new session");
            TrackedSession trackedSession = new TrackedSession(request.host, request.remoteAddress);
            trackedSession.save();
            session.put("TRACKER", trackedSession.id);
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
