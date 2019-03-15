package controllers;

import models.*;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Scope;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.stream;

/**
 * @author rcoqueugniot
 * @since 14.03.19
 */
public class ActivityKinds extends Controller {

    public static void list() {
        Logger.info("Listing ActivityKindSummary");
        List<ActivityKindSummary> activityKindSummaries = new ArrayList<>();
        for (ActivityKind activityKind : ActivityKind.listAllOrdered()) {
            activityKindSummaries.add(new ActivityKindSummary(activityKind,
                    StudentChoices.findAllChoice1(activityKind).size(),
                    StudentChoices.findAllChoice2(activityKind).size(),
                    StudentChoices.findAllChoice3(activityKind).size(),
                    StudentChoices.findAllChoice4(activityKind).size()
            ));
        }

        render(activityKindSummaries);
    }

    public static void batchAction(List<Long> selection) {
        Logger.info("params", Scope.Params.current());

        String action="";
        Logger.info("Running batch %s on %s ", action, selection);
        if (action == null) error(400, "Bad batch action");

        if (action.equals("merge")) ActivityKind.mergeAll(selection);
        else if (action.equals("deletion")) ActivityKind.deleteAll(selection);
        else error(400, "Bad batch action : " + action);
        list();
    }


    public static void detail(long id) {

        ActivityKind activityKind = ActivityKind.findById(id);

        List<SiblingStudent> choice1Students = SiblingStudent.wrapSiblings(StudentChoices.findAllChoice1(activityKind));
        List<SiblingStudent> choice2Students = SiblingStudent.wrapSiblings(StudentChoices.findAllChoice2(activityKind));
        List<SiblingStudent> choice3Students = SiblingStudent.wrapSiblings(StudentChoices.findAllChoice3(activityKind));
        List<SiblingStudent> choice4Students = SiblingStudent.wrapSiblings(StudentChoices.findAllChoice4(activityKind));

        render(activityKind, choice1Students, choice2Students, choice3Students, choice4Students);
    }


    private static class ActivityKindSummary {
        private final ActivityKind activityKind;
        private final int choice1;
        private final int choice2;
        private final int choice3;
        private final int choice4;

        public ActivityKindSummary(ActivityKind activityKind, int choice1, int choice2, int choice3, int choice4) {
            this.activityKind = activityKind;
            this.choice1 = choice1;
            this.choice2 = choice2;
            this.choice3 = choice3;
            this.choice4 = choice4;
        }


        public int getChoice1() {
            return choice1;
        }

        public int getChoice2() {
            return choice2;
        }

        public int getChoice3() {
            return choice3;
        }

        public int getChoice4() {
            return choice4;
        }
    }


}
