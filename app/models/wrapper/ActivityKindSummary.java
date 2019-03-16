package models.wrapper;

import models.SchoolEventActivity;
import models.StudentChoices;

import java.util.ArrayList;
import java.util.List;

public class ActivityKindSummary {
    private final SchoolEventActivity schoolEventActivity;
    private final int choice1;
    private final int choice2;
    private final int choice3;
    private final int choice4;

    public ActivityKindSummary(SchoolEventActivity schoolEventActivity, int choice1, int choice2, int choice3, int choice4) {
        this.schoolEventActivity = schoolEventActivity;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.choice4 = choice4;
    }

    public static List<ActivityKindSummary> computeSummaries(List<SchoolEventActivity> schoolEventActivities) {
        List<ActivityKindSummary> activityKindSummaries = new ArrayList<>();
        for (SchoolEventActivity schoolEventActivity : schoolEventActivities) {
            activityKindSummaries.add(new ActivityKindSummary(schoolEventActivity,
                    StudentChoices.findAllChoice1(schoolEventActivity).size(),
                    StudentChoices.findAllChoice2(schoolEventActivity).size(),
                    StudentChoices.findAllChoice3(schoolEventActivity).size(),
                    StudentChoices.findAllChoice4(schoolEventActivity).size()
            ));
        }
        return activityKindSummaries;
    }

    public long getId() {
        return schoolEventActivity.id;
    }
    public String getName() {
        return schoolEventActivity.name;
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
