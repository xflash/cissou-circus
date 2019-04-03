package controllers;

import models.Student;
import play.db.jpa.JPABase;
import play.mvc.Controller;
import play.mvc.With;

import java.util.*;

/**
 *
 */
@With(Tracker.class)
public class Students extends Controller {

    public static void openDetail(long id, String backUrl) {
        Student student = Student.findById(id);
        render(student, backUrl);
    }
}
