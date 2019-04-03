package controllers;

import models.*;
import models.wrapper.ActivityKindSummary;
import play.Logger;
import play.db.jpa.JPABase;
import play.mvc.Controller;
import play.mvc.With;
import sun.rmi.runtime.Log;

import java.util.List;


/**
 */
@With(Tracker.class)
public class SchoolEventActivities extends Controller {

    public static void root() {

        List<SchoolEvent> schoolEvents = SchoolEvent.findAll();

        Logger.info("SchoolEvent %d", schoolEvents.size());

        if (schoolEvents.size() == 0) {
            Application.init();
        }

        if (schoolEvents.size() > 1)
            badRequest("TODO handle " + schoolEvents.size() + " school events");

        list(schoolEvents.get(0).id);
    }

    public static void list(long schoolEventId) {
        Logger.info("Listing activities for schoolevent %d", schoolEventId);

        SchoolEvent schoolEvent = SchoolEvent.findById(schoolEventId);

        List<ActivityKindSummary> activityKindSummaries =
                ActivityKindSummary.computeSummaries(SchoolEventActivity.listAllOrdered(schoolEventId));

        render(schoolEvent, activityKindSummaries);
    }

    public static void prepareBatch(long schoolEventId, String batchAction, List<Long> selection) {
        Logger.info("Preparing batch %s on %s ", batchAction, selection);
        SchoolEvent schoolEvent = SchoolEvent.findById(schoolEventId);

        if (batchAction.equals("merge")) {
            List<ActivityKindSummary> activityKindSummaries =
                    ActivityKindSummary.computeSummaries(SchoolEventActivity.listOrdered(selection));
            render(schoolEvent, batchAction, activityKindSummaries);
        } else if (batchAction.equals("deletion")) {
            SchoolEventActivity.deleteAll(selection);

            list(schoolEventId);
        }

        error(400, "Unknown batch action : " + batchAction);
    }


    public static void batchAction(long schoolEventId, String batchAction, long root, List<Long> selection) {

        Logger.info("Running batch %s on %s with root %d", batchAction, selection, root);

        if (batchAction.equals("merge")) SchoolEventActivity.mergeAll(root, selection);
        else if (batchAction.equals("deletion")) SchoolEventActivity.deleteAll(selection);
        else error(400, "Bad batch action : " + batchAction);
        list(schoolEventId);
    }


    public static void detail(long id) {

        SchoolEventActivity schoolEventActivity = SchoolEventActivity.findById(id);

        List<Student> choice1Students = StudentChoices.getStudents(StudentChoices.findAllChoice1(schoolEventActivity));
        List<Student> choice2Students = StudentChoices.getStudents(StudentChoices.findAllChoice2(schoolEventActivity));
        List<Student> choice3Students = StudentChoices.getStudents(StudentChoices.findAllChoice3(schoolEventActivity));
        List<Student> choice4Students = StudentChoices.getStudents(StudentChoices.findAllChoice4(schoolEventActivity));

        render(schoolEventActivity, choice1Students, choice2Students, choice3Students, choice4Students);
    }


}
