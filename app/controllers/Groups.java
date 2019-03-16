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

        List<Student> students =
                Student.find("select s from Student s where s.classroom.id in :classrooms order by s.name")
                        .bind("classrooms", classrooms)
                        .fetch();

        List<Student> groupA = new ArrayList<>();
        List<Student> groupB = new ArrayList<>();

        Map<String, Set<Student>> siblings = Student.buildSiblings(students);

        List<Student> target = groupA;
        for (Map.Entry<String, Set<Student>> fratrie : siblings.entrySet()) {
            target.addAll(fratrie.getValue());
            students.removeAll(fratrie.getValue());
            if (target.equals(groupA)) target = groupB;
            else if (target.equals(groupB)) target = groupA;
        }

        //Collections.shuffle(students);
        target = groupA;
        for (Student student : students) {
            target.add(student);
            if (target.equals(groupA)) target = groupB;
            else if (target.equals(groupB)) target = groupA;
        }

        Collections.sort(groupA, Comparator.comparing((Student o) -> o.name));
        Collections.sort(groupB, Comparator.comparing((Student o) -> o.name));

        render( groupA, groupB, students);
    }


}
