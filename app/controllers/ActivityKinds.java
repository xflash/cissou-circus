package controllers;

import models.*;
import play.mvc.Controller;

import java.util.List;

import static java.util.Arrays.stream;

/**
 * @author rcoqueugniot
 * @since 14.03.19
 */
public class ActivityKinds extends Controller {


    public static void list() {
        List<ActivityKind> activityKinds = ActivityKind.listAllOrdered();

        render(activityKinds);
    }
    public static void detail(long id) {

        ActivityKind activityKind = ActivityKind.findById(id);

        List<SiblingStudent> choice1Students = SiblingStudent.wrapSiblings(StudentChoices.findAllChoice1(activityKind));
        List<SiblingStudent> choice2Students = SiblingStudent.wrapSiblings(StudentChoices.findAllChoice2(activityKind));
        List<SiblingStudent> choice3Students = SiblingStudent.wrapSiblings(StudentChoices.findAllChoice3(activityKind));
        List<SiblingStudent> choice4Students = SiblingStudent.wrapSiblings(StudentChoices.findAllChoice4(activityKind));

        render(activityKind, choice1Students, choice2Students, choice3Students, choice4Students);
    }


}
