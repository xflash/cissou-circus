package controllers;

import models.*;
import play.Logger;
import play.mvc.Controller;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 */
public class Groups extends Controller {


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
        render(classrooms, selected,schoolEvents);
    }

    public static void dispatch(int groupNumber, long schoolEventId, List<Long> classrooms) {
        Logger.info("Prepare Group dispatch in school event %d for %d groups, selected classroms %s", schoolEventId, groupNumber, classrooms);

        SchoolEvent schoolEvent = SchoolEvent.findById(schoolEventId);

        List<StudentChoices> students = StudentChoices.listStudentsChoicesInClassrooms(schoolEvent, classrooms);

        List<StudentChoices> groupA = new ArrayList<>();
        List<StudentChoices> groupB = new ArrayList<>();

        Map<String, Set<StudentChoices>> siblings = StudentChoices.buildSiblings(students);

        List<StudentChoices> target = groupA;
        for (Map.Entry<String, Set<StudentChoices>> fratrie : siblings.entrySet()) {
            target.addAll(fratrie.getValue());
            students.removeAll(fratrie.getValue());
            if (target.equals(groupA)) target = groupB;
            else if (target.equals(groupB)) target = groupA;
        }

        //Collections.shuffle(students);
        target = groupA;
        for (StudentChoices studentChoice : students) {
            target.add(studentChoice);
            if (target.equals(groupA)) target = groupB;
            else if (target.equals(groupB)) target = groupA;
        }

        Collections.sort(groupA, Comparator.comparing((StudentChoices o) -> o.student.name));
        Collections.sort(groupB, Comparator.comparing((StudentChoices o) -> o.student.name));

        render(groupA, groupB, students);
    }


}
