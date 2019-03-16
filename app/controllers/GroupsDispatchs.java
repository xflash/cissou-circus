package controllers;

import models.*;
import models.wrapper.ClassroomSummary;
import play.Logger;
import play.mvc.Controller;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 */
public class GroupsDispatchs extends Controller {


    public static void list() {
        List<SchoolEventProposal> schoolEventProposals = SchoolEventProposal.findAll();

        render(schoolEventProposals);
    }

    public static void edit(long id) {
        SchoolEventProposal schoolEventProposal = SchoolEventProposal.findById(id);
        List<SchoolEventGroup> schoolEventGroups =
                SchoolEventGroup.find("schoolEventProposal", schoolEventProposal).fetch();
        for (SchoolEventGroup schoolEventGroup : schoolEventGroups) {
            List<SchoolEventGroupStudentAssignment> schoolEventGroupStudentAssignments =
                    SchoolEventGroupStudentAssignment.find("schoolEventGroup", schoolEventGroup).fetch();
        }
        render(schoolEventProposal);
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

    public static void dispatch(int groupNumber, long schoolEventId, List<Long> classrooms) {
        Logger.info("Prepare Group dispatch in school event %d for %d groups, selected classroms %s", schoolEventId, groupNumber, classrooms);

        SchoolEvent schoolEvent = SchoolEvent.findById(schoolEventId);

        List<StudentChoices> students = StudentChoices.listStudentsChoicesInClassrooms(schoolEvent, classrooms);
        Map<String, Set<StudentChoices>> siblings = StudentChoices.buildSiblings(students);

        List<List<StudentChoices>> groups = new ArrayList<>();
        for (int i = 0; i < groupNumber; i++) {
            groups.add(new ArrayList<>());
        }

        int lastGroupId = 0;
        for (Map.Entry<String, Set<StudentChoices>> fratrie : siblings.entrySet()) {
            groups.get(lastGroupId).addAll(fratrie.getValue());
            students.removeAll(fratrie.getValue());
            lastGroupId++;
            if (lastGroupId >= groupNumber) lastGroupId = 0;
        }

        lastGroupId = 0;
        for (StudentChoices studentChoice : students) {
            groups.get(lastGroupId).add(studentChoice);
            lastGroupId++;
            if (lastGroupId >= groupNumber) lastGroupId = 0;
        }

        for (List<StudentChoices> group : groups) {
            Collections.sort(group, Comparator.comparing((StudentChoices o) -> o.student.name));
        }

        render(schoolEvent, groups);
    }

    public static void saveDispatch(long schoolEventId) {
        Logger.info("Save group dispatch in school event %d", schoolEventId);

        SchoolEvent schoolEvent = SchoolEvent.findById(schoolEventId);

        SchoolEventProposal schoolEventProposal = new SchoolEventProposal(schoolEvent).save();

        SchoolEventGroup schoolEventGroupA = new SchoolEventGroup(schoolEventProposal, "Groupe A").save();
        SchoolEventGroup schoolEventGroupB = new SchoolEventGroup(schoolEventProposal, "Groupe B").save();

//        new SchoolEventGroupStudentAssignment(schoolEventGroupA, )

//        SchoolEventGroup schoolEventGroupA = new SchoolEventGroup(schoolEvent, "Group A").save();
//        SchoolEventGroup schoolEventGroupB = new SchoolEventGroup(schoolEvent, "Group B").save();

        badRequest();

    }
}
