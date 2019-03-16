package controllers;

import models.*;
import models.wrapper.ActivityKindSummary;
import play.Logger;
import play.mvc.Controller;

import java.util.List;


/**
 * @author rcoqueugniot
 * @since 14.03.19
 */
public class ActivityKinds extends Controller {

    public static void list() {
        Logger.info("Listing ActivityKindSummary");

        List<ActivityKindSummary> activityKindSummaries =
                ActivityKindSummary.computeSummaries(ActivityKind.listAllOrdered());

        render(activityKindSummaries);
    }

    public static void prepareBatch(String batchAction, List<Long> selection) {
        Logger.info("Preparing batch %s on %s ", batchAction, selection);

        if (batchAction.equals("merge")) {
            List<ActivityKindSummary> activityKindSummaries =
                    ActivityKindSummary.computeSummaries(ActivityKind.listOrdered(selection));
            render(batchAction, activityKindSummaries);
        }
        else if (batchAction.equals("deletion")) {
            ActivityKind.deleteAll(selection);

            list();
        }

        error(400, "Unknown batch action : " + batchAction);
    }


    public static void batchAction(String batchAction, long root, List<Long> selection) {

        Logger.info("Running batch %s on %s with root %d", batchAction, selection, root);

        if (batchAction.equals("merge")) ActivityKind.mergeAll(root, selection);
        else if (batchAction.equals("deletion")) ActivityKind.deleteAll(selection);
        else error(400, "Bad batch action : " + batchAction);
        list();
    }


    public static void detail(long id) {

        ActivityKind activityKind = ActivityKind.findById(id);

        List<Student> choice1Students = StudentChoices.getStudents(StudentChoices.findAllChoice1(activityKind));
        List<Student> choice2Students = StudentChoices.getStudents(StudentChoices.findAllChoice2(activityKind));
        List<Student> choice3Students = StudentChoices.getStudents(StudentChoices.findAllChoice3(activityKind));
        List<Student> choice4Students = StudentChoices.getStudents(StudentChoices.findAllChoice4(activityKind));

        render(activityKind, choice1Students, choice2Students, choice3Students, choice4Students);
    }


}
