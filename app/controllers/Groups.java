package controllers;

import models.Student;
import play.mvc.Controller;

import java.util.Arrays;
import java.util.List;

/**
 */
public class Groups extends Controller {


    public static void createGroups(List<Long> classrooms) {
        System.out.println("classrooms = " + classrooms);

        List<Student> students =
                Student.find("select s from Student s where s.classroom.id in :classrooms order by s.name")
                        .bind("classrooms",classrooms)
                .fetch();

        render(students);
    }


}
