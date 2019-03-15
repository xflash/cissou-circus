package models;

import java.util.ArrayList;
import java.util.List;

public class ActivityKindSummary {
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

    public static List<ActivityKindSummary> computeSummaries(List<ActivityKind> activityKinds) {
        List<ActivityKindSummary> activityKindSummaries = new ArrayList<>();
        for (ActivityKind activityKind : activityKinds) {
            activityKindSummaries.add(new ActivityKindSummary(activityKind,
                    StudentChoices.findAllChoice1(activityKind).size(),
                    StudentChoices.findAllChoice2(activityKind).size(),
                    StudentChoices.findAllChoice3(activityKind).size(),
                    StudentChoices.findAllChoice4(activityKind).size()
            ));
        }
        return activityKindSummaries;
    }

    public long getId() {
        return activityKind.id;
    }
    public String getName() {
        return activityKind.name;
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
