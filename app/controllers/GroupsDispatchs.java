package controllers;

import models.*;
import models.wrapper.ClassroomSummary;
import play.Logger;
import play.db.jpa.JPABase;
import play.mvc.Controller;
import sun.rmi.runtime.Log;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 *
 */
public class GroupsDispatchs extends Controller {


    private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public static void list() {
        List<SchoolEventProposal> schoolEventProposals = SchoolEventProposal.findAll();

        render(schoolEventProposals);
    }

    public static void init() {
        List<ClassroomSummary> classrooms = ClassroomSummary.wrap(Classroom.findAll());

        List<Long> selected =
                Classroom.find("select c.id from Classroom c where c.kind in :kinds ")
                        .bind("kinds",
                                stream(ClassRoomKind.values())
                                        .filter(value -> value.ordinal() >= ClassRoomKind.GRANDE_MOYENNE_SECTION.ordinal())
                                        .collect(Collectors.toList()))
                        .fetch();

        List<SchoolEvent> schoolEvents = SchoolEvent.findAll();
        render(classrooms, selected, schoolEvents);
    }

    public static void dispatch(int maximumStudents, boolean siblingKept, int groupNumber, long schoolEventId, List<Long> classrooms) {
        Logger.info("Prepare Group dispatch in school event %d for %d groups, selected classrooms %s maximumStudents %d",
                schoolEventId, groupNumber, classrooms, maximumStudents);

        SchoolEvent schoolEvent = SchoolEvent.findById(schoolEventId);

        SchoolEventProposal proposal = new SchoolEventProposal(schoolEvent, "Group proposal from " + sdf.format(new Date())).save();

        for (int i = 1; i <= groupNumber; i++) {
            SchoolEventGroup schoolEventGroup = new SchoolEventGroup(proposal, "Group " + i).save();
            proposal.groups.add(schoolEventGroup);
            for (SchoolEventActivity schoolEventActivity : SchoolEventActivity.listAllOrdered(schoolEventId)) {
                schoolEventGroup.activities.add(new SchoolEventGroupActivity(schoolEventGroup, schoolEventActivity).save());
            }
        }

        List<StudentChoices> students = StudentChoices.listStudentsChoicesInClassrooms(schoolEvent, classrooms);
        if (siblingKept) {
            dispatchStudentChoicesWithSiblings(proposal.groups, students, maximumStudents);
        }
        dispatchStudentChoices(proposal.groups, students, maximumStudents);

        proposal.save();
        edit(proposal.id);
    }


    public static void edit(long id) {
        SchoolEventProposal proposal = SchoolEventProposal.findById(id);
        if (proposal == null) badRequest("Unknown SchoolEventProposal id " + id);

        HashMap<Long, Map<Long, Integer>> activitiesCountMap = new HashMap<>();
        for (SchoolEventGroup group : proposal.groups) {
            Map<Long, Integer> groupActivitiesMap = activitiesCountMap.computeIfAbsent(group.id, (k) -> new HashMap<>());
            for (SchoolEventGroupActivity activity : group.activities) {
                groupActivitiesMap.put(activity.schoolEventActivity.id, activity.assignments.size());
            }
        }

        render(proposal, activitiesCountMap);
    }

    public static void delete(long id) {
        SchoolEventProposal schoolEventProposal = SchoolEventProposal.findById(id);
        if (schoolEventProposal == null) badRequest("Unknown SchoolEventProposal id " + id);
        for (SchoolEventGroup group : schoolEventProposal.groups) {
            for (SchoolEventGroupActivity activity : group.activities) {
                for (SchoolEventGroupStudentAssignment assignment : activity.assignments) {
                    assignment.delete();
                }
                activity.delete();
            }
            group.delete();
        }
        schoolEventProposal.delete();
        list();
//        for (SchoolEventGroup schoolEventGroup : SchoolEventGroup.list4Proposal(schoolEventProposal)) {
//            for (SchoolEventGroupStudentAssignment schoolEventGroupStudentAssignment : SchoolEventGroupStudentAssignment.list4GroupSchoolEventGroup(schoolEventGroup)) {
//                schoolEventGroupStudentAssignment.delete();
//            }
//            schoolEventGroup.delete();
//        }
//        schoolEventProposal.delete();
//        list();
    }

    private static void dispatchStudentChoicesWithSiblings(List<SchoolEventGroup> groups,
                                                           List<StudentChoices> students,
                                                           int maximumStudents) {
        Map<String, Set<StudentChoices>> siblings = StudentChoices.buildSiblings(students);
        int lastGroupId = 0;
        for (Map.Entry<String, Set<StudentChoices>> fratrie : siblings.entrySet()) {
            for (StudentChoices studentChoices : fratrie.getValue()) {
                dispatchSudentChoices(studentChoices, groups.get(lastGroupId).activities, maximumStudents);
            }
            students.removeAll(fratrie.getValue());
            lastGroupId++;
            if (lastGroupId >= groups.size()) lastGroupId = 0;
        }
    }

    private static void dispatchStudentChoices(List<SchoolEventGroup> groups,
                                               List<StudentChoices> studentChoicesList,
                                               int maximumStudents) {
        int lastGroupId = 0;
        for (StudentChoices studentChoices : studentChoicesList) {
            dispatchSudentChoices(studentChoices, groups.get(lastGroupId).activities, maximumStudents);
            lastGroupId++;
            if (lastGroupId >= groups.size()) lastGroupId = 0;
        }
    }

    private static void dispatchSudentChoices(StudentChoices studentChoices,
                                              Set<SchoolEventGroupActivity> eventGroupActivities,
                                              int maximumStudents) {
        boolean added = false;
        for (SchoolEventGroupActivity groupActivity : eventGroupActivities) {
            if(groupActivity.assignments.size() < maximumStudents) {
                if (studentChoices.getChoice1().equals(groupActivity.schoolEventActivity))
                    added = groupActivity.assignments.add(new SchoolEventGroupStudentAssignment(groupActivity, studentChoices).save());
                else if (studentChoices.getChoice2().equals(groupActivity.schoolEventActivity))
                    added = groupActivity.assignments.add(new SchoolEventGroupStudentAssignment(groupActivity, studentChoices).save());
                else if (studentChoices.getChoice3().equals(groupActivity.schoolEventActivity))
                    added = groupActivity.assignments.add(new SchoolEventGroupStudentAssignment(groupActivity, studentChoices).save());
                else if (studentChoices.getChoice4().equals(groupActivity.schoolEventActivity))
                    added = groupActivity.assignments.add(new SchoolEventGroupStudentAssignment(groupActivity, studentChoices).save());
            }
            if (added) break;
        }
        if(!added) {
            Logger.error("We have an unpllaned Student %s", studentChoices.student.identifiant);
        }
    }

    public static void swapStudentActivity(long schoolEventGroupStudentAssignmentId, long newActivityId) {
        Logger.info("Swap activity for schoolEventGroupStudentAssignmentId %d newWctivityId %d",
            schoolEventGroupStudentAssignmentId, newActivityId);
        SchoolEventGroupStudentAssignment schoolEventGroupStudentAssignment =
                SchoolEventGroupStudentAssignment.findById(schoolEventGroupStudentAssignmentId);
        if(schoolEventGroupStudentAssignment==null)badRequest("Unknwon schoolEventGroupStudentAssignment "+schoolEventGroupStudentAssignmentId);

        SchoolEventGroup schoolEventGroup = schoolEventGroupStudentAssignment.schoolEventGroupActivity.schoolEventGroup;
        for (SchoolEventGroupActivity activity : schoolEventGroup.activities) {
            if(activity.schoolEventActivity.id.equals(newActivityId)) {
                schoolEventGroupStudentAssignment.schoolEventGroupActivity=activity;
                schoolEventGroupStudentAssignment.save();
            }
        }
        edit(schoolEventGroup.schoolEventProposal.id);

    }


    public static void moveStudentNextGroup(long schoolEventGroupStudentAssignmentId, int way) {
        Logger.info("Move student to next group for schoolEventGroupStudentAssignmentId %d ",
            schoolEventGroupStudentAssignmentId);
        SchoolEventGroupStudentAssignment schoolEventGroupStudentAssignment =
                SchoolEventGroupStudentAssignment.findById(schoolEventGroupStudentAssignmentId);
        if(schoolEventGroupStudentAssignment==null)badRequest("Unknwon schoolEventGroupStudentAssignment "+schoolEventGroupStudentAssignmentId);


        SchoolEventGroup schoolEventGroup = schoolEventGroupStudentAssignment.schoolEventGroupActivity.schoolEventGroup;
        SchoolEventProposal schoolEventProposal = schoolEventGroup.schoolEventProposal;

        int i = schoolEventProposal.groups.indexOf(schoolEventGroup);
        i += way;
        if(i>=schoolEventProposal.groups.size())
            i=0;
        else if(i<0)
            i=schoolEventProposal.groups.size()-1;

        SchoolEventGroup newSchoolEventGroup = schoolEventProposal.groups.get(i);
        for (SchoolEventGroupActivity schoolEventGroupActivity : newSchoolEventGroup.activities) {
            if(schoolEventGroupActivity.schoolEventActivity.id.equals(schoolEventGroupStudentAssignment.schoolEventGroupActivity.schoolEventActivity.id)) {
                schoolEventGroupStudentAssignment.schoolEventGroupActivity=schoolEventGroupActivity;
                schoolEventGroupStudentAssignment.save();
                break;
            }
        }

        edit(schoolEventProposal.id);
    }


}
