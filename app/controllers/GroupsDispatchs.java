package controllers;

import models.*;
import models.wrapper.ClassroomSummary;
import play.Logger;
import play.mvc.Controller;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
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

    public static void dispatch(boolean siblingKept, int groupNumber, long schoolEventId, List<Long> classrooms) {
        Logger.info("Prepare Group dispatch in school event %d for %d groups, selected classroms %s", schoolEventId, groupNumber, classrooms);

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
            dispatchStudentChoicesWithSiblings(proposal.groups, students);
        }
        dispatchStudentChoices(proposal.groups, students);

        proposal.save();
        edit(proposal.id);
    }

    public static void edit(long id) {
        SchoolEventProposal proposal = SchoolEventProposal.findById(id);
        if (proposal == null) badRequest("Unknown SchoolEventProposal id " + id);
        render(proposal);
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

    private static void dispatchStudentChoicesWithSiblings(List<SchoolEventGroup> groups, List<StudentChoices> students) {
        Map<String, Set<StudentChoices>> siblings = StudentChoices.buildSiblings(students);
        int lastGroupId = 0;
        for (Map.Entry<String, Set<StudentChoices>> fratrie : siblings.entrySet()) {
            for (StudentChoices studentChoices : fratrie.getValue()) {
                dispatchThem(groups.get(lastGroupId), studentChoices);
            }
            students.removeAll(fratrie.getValue());
            lastGroupId++;
            if (lastGroupId >= groups.size()) lastGroupId = 0;
        }
    }

    private static void dispatchStudentChoices(List<SchoolEventGroup> groups, List<StudentChoices> studentChoicesList) {
        int lastGroupId = 0;
        for (StudentChoices studentChoices : studentChoicesList) {
            dispatchThem(groups.get(lastGroupId), studentChoices);
            lastGroupId++;
            if (lastGroupId >= groups.size()) lastGroupId = 0;
        }
    }

    private static void dispatchThem(SchoolEventGroup schoolEventGroup, StudentChoices studentChoices) {
        boolean added = false;
        for (SchoolEventGroupActivity groupActivity : schoolEventGroup.activities) {
//            int i = ThreadLocalRandom.current().nextInt(1, 4 + 1);
            if (studentChoices.getChoice1().equals(groupActivity.schoolEventActivity))
                added = groupActivity.assignments.add(new SchoolEventGroupStudentAssignment(groupActivity, studentChoices).save());
            else if (studentChoices.getChoice2().equals(groupActivity.schoolEventActivity))
                added = groupActivity.assignments.add(new SchoolEventGroupStudentAssignment(groupActivity, studentChoices).save());
            else if (studentChoices.getChoice3().equals(groupActivity.schoolEventActivity))
                added = groupActivity.assignments.add(new SchoolEventGroupStudentAssignment(groupActivity, studentChoices).save());
            else if (studentChoices.getChoice4().equals(groupActivity.schoolEventActivity))
                added = groupActivity.assignments.add(new SchoolEventGroupStudentAssignment(groupActivity, studentChoices).save());
            if (added) break;
        }
    }

    public static void saveDispatch(long schoolEventId, Map dispatch) {
        Logger.info("Save group dispatch in school event %d", schoolEventId);

        SchoolEvent schoolEvent = SchoolEvent.findById(schoolEventId);

//        SchoolEventProposal schoolEventProposal = new SchoolEventProposal(schoolEvent).save();
//
//        SchoolEventGroup schoolEventGroupA = new SchoolEventGroup(schoolEventProposal, "Groupe A").save();
//        SchoolEventGroup schoolEventGroupB = new SchoolEventGroup(schoolEventProposal, "Groupe B").save();

//        new SchoolEventGroupStudentAssignment(schoolEventGroupA, )

//        SchoolEventGroup schoolEventGroupA = new SchoolEventGroup(schoolEvent, "Group A").save();
//        SchoolEventGroup schoolEventGroupB = new SchoolEventGroup(schoolEvent, "Group B").save();

        badRequest();

    }
}
