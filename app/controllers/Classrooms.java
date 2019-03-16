package controllers;

import models.*;
import play.mvc.Controller;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.*;

/**
 * @author rcoqueugniot
 * @since 14.03.19
 */
public class Classrooms extends Controller {


    public static void list() {
        List<ClassroomSummary> classrooms = ClassroomSummary.wrap(Classroom.findAll());

        if (classrooms.isEmpty())
            Application.init();

        render(classrooms);
    }

    public static void openDetail(long id) {
        Classroom classroom = Classroom.findById(id);

        List<SiblingStudent> students = SiblingStudent.wrapSiblings(
                Student.find("select s from Student s where classroom.id = ?1 order by name, firstname", classroom.id)
                .fetch());

        render(classroom, students);

    }


}
