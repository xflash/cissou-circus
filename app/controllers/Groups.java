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
        render(classrooms, selected);
    }

    public static void dispatch(List<Long> classrooms) {
        Logger.info("classrooms = " + classrooms);

        List<StudentChoices> students = listStudentsChoicesInClassrooms(classrooms);

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

    private static List<StudentChoices> listStudentsChoicesInClassrooms(List<Long> classrooms) {
        return StudentChoices.find(
                        "select sc " +
                        "from StudentChoices as sc inner join sc.student as s inner join s.classroom as cr " +
                        "where cr.id in :classrooms " +
                        "and sc.choice1 is not null " +
                        "and sc.choice2 is not null " +
                        "order by s.name")
                .bind("classrooms", classrooms)
                .fetch();
    }


}
