package controllers;

import models.*;
import models.wrapper.ClassroomSummary;
import org.slf4j.LoggerFactory;
import play.Logger;
import play.db.jpa.JPABase;
import play.jobs.Job;
import play.libs.F;
import play.mvc.Controller;

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

        String proposalName = "Proposal #" + SchoolEventProposal.count() + 1;

        List<SchoolEvent> schoolEvents = SchoolEvent.findAll();

        render(classrooms, selected, schoolEvents, proposalName);
    }

    public static void edit(long id) {
        Logger.info("Showing Proposal %d", id);

        SchoolEventProposal proposal = SchoolEventProposal.loadById(id);
        if (proposal == null) badRequest("Unknown SchoolEventProposal id " + id);

        if(proposal.groups.isEmpty())
            badRequest("SchoolEventProposal id " + id + " have no groups");

        editWithGroup(proposal.groups.get(0).id);
    }

    public static void editWithGroup(long groupId) {
        Logger.info("Showing Proposal group %d", groupId);

        SchoolEventGroup schoolEventGroup = SchoolEventGroup.findById(groupId);
        if (schoolEventGroup == null) badRequest("Unknown SchoolEventGroup id " + groupId);

        SchoolEventProposal proposal = schoolEventGroup.schoolEventProposal;


        Map<Long, Map<Long, Integer>> activitiesCountMap = new HashMap<>();
        Map<Long, Integer> groupActivitiesMap = activitiesCountMap.computeIfAbsent(schoolEventGroup.id, (k) -> new HashMap<>());
        for (SchoolEventGroupActivity activity : schoolEventGroup.activities) {
            groupActivitiesMap.put(activity.schoolEventActivity.id, activity.assignments.size());
        }

        render(proposal, schoolEventGroup, activitiesCountMap);
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
    }

    public static void dispatch(final int maximumStudentss, boolean siblingKept, int groupNumber, long schoolEventId, List<Long> classrooms, String proposalName) {
        Logger.info("Prepare Group dispatch in school event %d for %d groups, selected classrooms %s maximumStudents %d",
                schoolEventId, groupNumber, classrooms, maximumStudentss);

        SchoolEvent schoolEvent = SchoolEvent.findById(schoolEventId);

        int studentsmaxnb = maximumStudentss;

        SchoolEventProposal proposal = new SchoolEventProposal(schoolEvent, proposalName).save();

        for (int i = 1; i <= groupNumber; i++) {
            SchoolEventGroup schoolEventGroup = new SchoolEventGroup(proposal, "Group " + i).save();
            proposal.groups.add(schoolEventGroup);
            for (SchoolEventActivity schoolEventActivity : SchoolEventActivity.listAllOrdered(schoolEventId)) {
                schoolEventGroup.activities.add(new SchoolEventGroupActivity(schoolEventGroup, schoolEventActivity).save());
            }
        }

        Map<SchoolEventGroup, List<StudentChoices>> studentsGroupMap =
                buildStudentGroupMap(
                        proposal,
                        StudentChoices.listStudentsChoicesInClassrooms(schoolEvent, classrooms),
                        siblingKept);

        for (Map.Entry<SchoolEventGroup, List<StudentChoices>> entry : studentsGroupMap.entrySet()) {
            SchoolEventGroup eventGroup = entry.getKey();
            List<StudentChoices> groupStudentChoices = entry.getValue();

            while (!groupStudentChoices.isEmpty()) {
                dispatchGroup(studentsmaxnb, eventGroup, groupStudentChoices);

                for (StudentChoices studentChoices : groupStudentChoices) {
                    SchoolEventGroupActivity eventGroupActivity1 = findEventGroupActivity(eventGroup, studentChoices.choice1);
                    SchoolEventGroupActivity eventGroupActivity2 = findEventGroupActivity(eventGroup, studentChoices.choice2);
                    SchoolEventGroupActivity eventGroupActivity3 = findEventGroupActivity(eventGroup, studentChoices.choice3);
                    SchoolEventGroupActivity eventGroupActivity4 = findEventGroupActivity(eventGroup, studentChoices.choice4);
                    if (eventGroupActivity1.assignments.size() >= studentsmaxnb
                            && eventGroupActivity2.assignments.size() >= studentsmaxnb
                            && eventGroupActivity3.assignments.size() >= studentsmaxnb
                            && eventGroupActivity4.assignments.size() >= studentsmaxnb
                    )
                        studentsmaxnb++;
                }
            }


        }

        proposal.save();


        edit(proposal.id);
    }

    private static SchoolEventGroupActivity findEventGroupActivity(SchoolEventGroup eventGroup, SchoolEventActivity activity) {
        for (SchoolEventGroupActivity eventGroupActivity : eventGroup.activities) {
            if (eventGroupActivity.schoolEventActivity.id.equals(activity.id)) {
                return eventGroupActivity;
            }
        }
        return null;
    }

    private static void dispatchGroup(int maximumStudents, SchoolEventGroup eventGroup, List<StudentChoices> groupStudentChoices) {
        for (SchoolEventGroupActivity eventGroupActivity : eventGroup.activities) {
            SchoolEventActivity eventActivity = eventGroupActivity.schoolEventActivity;

            Set<StudentChoices> handled = new HashSet<>();

            for (StudentChoices studentChoices : groupStudentChoices) {
                boolean added = false;
                if (studentChoices.choice1.id.equals(eventActivity.id)) {
                    if (eventGroupActivity.assignments.size() < maximumStudents) {
                        added = eventGroupActivity.assignments.add(new SchoolEventGroupStudentAssignment(eventGroupActivity, studentChoices).save());
                    }
                } else if (!added && studentChoices.choice2.id.equals(eventActivity.id)) {
                    if (eventGroupActivity.assignments.size() < maximumStudents) {
                        added = eventGroupActivity.assignments.add(new SchoolEventGroupStudentAssignment(eventGroupActivity, studentChoices).save());
                    }
                } else if (!added && studentChoices.choice3.id.equals(eventActivity.id)) {
                    if (eventGroupActivity.assignments.size() < maximumStudents) {
                        added = eventGroupActivity.assignments.add(new SchoolEventGroupStudentAssignment(eventGroupActivity, studentChoices).save());
                    }
                } else if (!added && studentChoices.choice4.id.equals(eventActivity.id)) {
                    if (eventGroupActivity.assignments.size() < maximumStudents) {
                        added = eventGroupActivity.assignments.add(new SchoolEventGroupStudentAssignment(eventGroupActivity, studentChoices).save());
                    }
                }

                if (added) {
                    handled.add(studentChoices);
                    break;
                }
            }
            groupStudentChoices.removeAll(handled);
        }
    }

    private static Map<SchoolEventGroup, List<StudentChoices>>
    buildStudentGroupMap(SchoolEventProposal proposal, List<StudentChoices> students, boolean siblingKept) {

        Map<SchoolEventGroup, List<StudentChoices>> map = new HashMap<>();

        if (siblingKept) {
            int lastGroupId = 0;
            for (Set<StudentChoices> familly : StudentChoices.buildSiblings(students).values()) {
                map.computeIfAbsent(proposal.groups.get(lastGroupId), k -> new ArrayList<>())
                        .addAll(familly);

                for (StudentChoices studentChoices : familly) {
                    students.remove(studentChoices);
                }
                lastGroupId++;
                if (lastGroupId >= proposal.groups.size()) lastGroupId = 0;
            }
        }
        int lastGroupId = 0;
        for (StudentChoices studentChoices : students) {
            map.computeIfAbsent(proposal.groups.get(lastGroupId), k -> new ArrayList<>())
                    .add(studentChoices);
            lastGroupId++;
            if (lastGroupId >= proposal.groups.size()) lastGroupId = 0;
        }

        return map;
    }


    public static void swapStudentActivity(long schoolEventGroupStudentAssignmentId, long newActivityId) {
        Logger.info("Swap activity for schoolEventGroupStudentAssignmentId %d newWctivityId %d",
                schoolEventGroupStudentAssignmentId, newActivityId);
        SchoolEventGroupStudentAssignment schoolEventGroupStudentAssignment =
                SchoolEventGroupStudentAssignment.findById(schoolEventGroupStudentAssignmentId);
        if (schoolEventGroupStudentAssignment == null)
            badRequest("Unknwon schoolEventGroupStudentAssignment " + schoolEventGroupStudentAssignmentId);

        SchoolEventGroup schoolEventGroup = schoolEventGroupStudentAssignment.schoolEventGroupActivity.schoolEventGroup;
        for (SchoolEventGroupActivity activity : schoolEventGroup.activities) {
            if (activity.schoolEventActivity.id.equals(newActivityId)) {
                schoolEventGroupStudentAssignment.schoolEventGroupActivity = activity;
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
        if (schoolEventGroupStudentAssignment == null)
            badRequest("Unknown schoolEventGroupStudentAssignment " + schoolEventGroupStudentAssignmentId);

        SchoolEventGroup schoolEventGroup = schoolEventGroupStudentAssignment.schoolEventGroupActivity.schoolEventGroup;

        SchoolEventProposal schoolEventProposal = schoolEventGroup.schoolEventProposal;
        SchoolEventGroup newSchoolEventGroup = schoolEventProposal.guessNextGroup(schoolEventGroup, way);


        Student student = schoolEventGroupStudentAssignment.studentChoices.student;
        HashSet<SchoolEventGroupStudentAssignment> fratriesInGroup = new HashSet<>();
        for (SchoolEventGroupActivity schoolEventGroupActivity : schoolEventGroupStudentAssignment.schoolEventGroupActivity.schoolEventGroup.activities) {
            for (SchoolEventGroupStudentAssignment assignment : schoolEventGroupActivity.assignments) {
                if (!schoolEventGroupStudentAssignment.id.equals(assignment.id)
                        && assignment.studentChoices.student.name.equals(student.name)) {
                    fratriesInGroup.add(assignment);
                }
            }
        }
        if (!fratriesInGroup.isEmpty()) {
            render(schoolEventGroupStudentAssignment, fratriesInGroup, schoolEventGroupStudentAssignment, newSchoolEventGroup, schoolEventProposal);
        }

        moveAssignmentTo(schoolEventGroupStudentAssignment, newSchoolEventGroup);

        edit(schoolEventProposal.id);
    }


    public static void confirmMoveStudentNextGroup(long schoolEventGroupStudentAssignmentId,
                                                   long newSchoolEventGroupId,
                                                   String chosenChoice,
                                                   List<Long> selectedToMove) {
        Logger.info("Confirmation of moving student assignment $d to a new group $%d with action %s",
                schoolEventGroupStudentAssignmentId, newSchoolEventGroupId, chosenChoice);

        SchoolEventGroupStudentAssignment schoolEventGroupStudentAssignment =
                SchoolEventGroupStudentAssignment.findById(schoolEventGroupStudentAssignmentId);
        if (schoolEventGroupStudentAssignment == null)
            badRequest("Unknown schoolEventGroupStudentAssignment " + schoolEventGroupStudentAssignmentId);

        SchoolEventGroup newSchoolEventGroup = SchoolEventGroup.findById(newSchoolEventGroupId);
        if (newSchoolEventGroup == null)
            badRequest("Unknown newSchoolEventGroup " + newSchoolEventGroup);

        switch (chosenChoice) {
            case "moveAllFamily":
                if (selectedToMove == null)
                    badRequest("Unknown selectedToMove ");
                for (SchoolEventGroupActivity schoolEventGroupActivity : schoolEventGroupStudentAssignment.schoolEventGroupActivity.schoolEventGroup.activities) {
                    for (SchoolEventGroupStudentAssignment assignment : schoolEventGroupActivity.assignments) {
                        if (selectedToMove.contains(assignment.id))
                            moveAssignmentTo(assignment, newSchoolEventGroup);
                    }
                }
                edit(schoolEventGroupStudentAssignment.schoolEventGroupActivity.schoolEventGroup.schoolEventProposal.id);
                break;
            case "moveConfirmed":
                moveAssignmentTo(schoolEventGroupStudentAssignment, newSchoolEventGroup);
                edit(schoolEventGroupStudentAssignment.schoolEventGroupActivity.schoolEventGroup.schoolEventProposal.id);
                break;
            default:
                badRequest("Unknown chosen confirmation " + chosenChoice);
        }


    }


    private static void moveAssignmentTo(SchoolEventGroupStudentAssignment schoolEventGroupStudentAssignment, SchoolEventGroup newSchoolEventGroup) {
        for (SchoolEventGroupActivity schoolEventGroupActivity : newSchoolEventGroup.activities) {
            if (schoolEventGroupActivity.schoolEventActivity.id.equals(schoolEventGroupStudentAssignment.schoolEventGroupActivity.schoolEventActivity.id)) {
                schoolEventGroupStudentAssignment.schoolEventGroupActivity = schoolEventGroupActivity;
                schoolEventGroupStudentAssignment.save();
                break;
            }
        }
    }


}
