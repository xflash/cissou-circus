package controllers;

import models.TrackedAction;
import models.TrackedSession;
import play.db.jpa.JPABase;
import play.mvc.Controller;

import java.util.List;

/**
 * @author rcoqueugniot
 * @since 14.04.19
 */

public class Trackers extends Controller {

    public static void list() {

        List<TrackedSession> trackedSessions = TrackedSession.findAll();

        render(trackedSessions);
    }

    public static void open(Long id) {

        TrackedSession trackedSession = TrackedSession.findById(id);
        if(trackedSession==null)badRequest("Tracked session id not found "+id);

        List<TrackedAction> trackedActions= TrackedAction.findAllByTrackedSession(id);
        render(trackedSession, trackedActions);
    }
}
