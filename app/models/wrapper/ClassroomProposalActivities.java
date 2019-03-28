package models.wrapper;

import models.SchoolEventActivity;
import models.SchoolEventGroupStudentAssignment;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ClassroomProposalActivities {
    private SchoolEventActivity schoolEventActivity;
    private Set<SchoolEventGroupStudentAssignment> assignments = new TreeSet<>(Comparator.comparing(o -> o.studentChoices.student.name));

    public ClassroomProposalActivities(SchoolEventActivity schoolEventActivity) {
        this.schoolEventActivity = schoolEventActivity;
    }

    public static Set<ClassroomProposalActivities> wrap(List<SchoolEventGroupStudentAssignment> listByClassRoomAndProposal) {

        Set<ClassroomProposalActivities> classroomProposalActivities = new TreeSet<>(Comparator.comparing(o -> o.schoolEventActivity.name));

        listByClassRoomAndProposal.sort(Comparator.comparing(o -> o.schoolEventGroupActivity.schoolEventActivity.name));

        ClassroomProposalActivities lastClassroomProposalActivities = null;
        for (SchoolEventGroupStudentAssignment schoolEventGroupStudentAssignment : listByClassRoomAndProposal) {
            SchoolEventActivity schoolEventActivity = schoolEventGroupStudentAssignment.schoolEventGroupActivity.schoolEventActivity;
            if (lastClassroomProposalActivities == null || (!lastClassroomProposalActivities.schoolEventActivity.id.equals(schoolEventActivity.id))) {
                lastClassroomProposalActivities = new ClassroomProposalActivities(schoolEventActivity);
                classroomProposalActivities.add(lastClassroomProposalActivities);
            }

            lastClassroomProposalActivities.add(schoolEventGroupStudentAssignment);
        }
        return classroomProposalActivities;
    }

    private void add(SchoolEventGroupStudentAssignment schoolEventGroupStudentAssignment) {
        assignments.add(schoolEventGroupStudentAssignment);
    }

    public SchoolEventActivity getSchoolEventActivity() {
        return schoolEventActivity;
    }

    public Set<SchoolEventGroupStudentAssignment> getAssignments() {
        return assignments;
    }
}
