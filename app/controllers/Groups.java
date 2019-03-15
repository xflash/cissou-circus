package controllers;

import models.SiblingStudent;
import models.Student;
import play.mvc.Controller;

import java.util.*;

/**
 */
public class Groups extends Controller {


    public static void createGroups(List<Long> classrooms) {
        System.out.println("classrooms = " + classrooms);

        List<Student> students =
                Student.find("select s from Student s where s.classroom.id in :classrooms order by s.name")
                        .bind("classrooms", classrooms)
                        .fetch();

        List<Student> studentGroupA = new ArrayList<>();
        List<Student> studentGroupB = new ArrayList<>();

        Map<String, Set<Student>> fraties = Student.buildFratries(students);

        List<Student> target;
        target = studentGroupA;
        for (Map.Entry<String, Set<Student>> fratrie : fraties.entrySet()) {
            target.addAll(fratrie.getValue());
            students.removeAll(fratrie.getValue());
            if (target.equals(studentGroupA)) target = studentGroupB;
            else if (target.equals(studentGroupB)) target = studentGroupA;
        }

        //Collections.shuffle(students);
        target = studentGroupA;
        for (Student student : students) {
            target.add(student);
            if (target.equals(studentGroupA)) target = studentGroupB;
            else if (target.equals(studentGroupB)) target = studentGroupA;
        }

        Collections.sort(studentGroupA, Comparator.comparing((Student o) -> o.name));
        Collections.sort(studentGroupB, Comparator.comparing((Student o) -> o.name));

        List<SiblingStudent> groupA = SiblingStudent.wrapSiblings(studentGroupA);
        List<SiblingStudent> groupB = SiblingStudent.wrapSiblings(studentGroupB);

        render( groupA, groupB, students);
    }


}
