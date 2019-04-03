package controllers;

import models.*;
import models.wrapper.ClassroomSummary;
import play.mvc.Controller;
import play.mvc.With;

import java.util.List;

/**
 * @author rcoqueugniot
 * @since 14.03.19
 */
@With(Tracker.class)
public class Classrooms extends Controller {


    public static void list() {
        List<ClassroomSummary> classrooms = ClassroomSummary.wrap(Classroom.findAll());

        if (classrooms.isEmpty())
            Application.init();

        render(classrooms);
    }

    public static void openDetail(long id) {
        Classroom classroom = Classroom.findById(id);

        List<Student> students = Student.findByClassroomOrdered(classroom);

        render(classroom, students);

    }


}
