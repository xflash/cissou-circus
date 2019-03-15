package controllers;

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

        List<Student> groupA = new ArrayList<>();
        List<Student> groupB = new ArrayList<>();

        Map<String, Set<Student>> fraties = new HashMap<>();

        Student laststudent = null;
        for (Student student : students) {
            if (laststudent != null) {
                if (laststudent.name.equals(student.name)) {
                    Set<Student> fra = fraties.computeIfAbsent(student.name, k -> new TreeSet<>(Comparator.comparing((Student o) -> o.firstname)));
                    fra.add(student);
                    fra.add(laststudent);
                }
            }
            laststudent = student;
        }

        List<Student> target;
        target = groupA;
        for (Map.Entry<String, Set<Student>> fratrie : fraties.entrySet()) {
            target.addAll(fratrie.getValue());
            students.removeAll(fratrie.getValue());
            if (target.equals(groupA)) target = groupB;
            else if (target.equals(groupB)) target = groupA;
        }

        Collections.shuffle(students);
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
