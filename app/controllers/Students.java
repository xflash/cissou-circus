package controllers;

import models.Student;
import play.db.jpa.JPABase;
import play.mvc.Controller;

import java.util.*;

/**
 *
 */
public class Students extends Controller {

    public static void openDetail(long id) {
        Student student = Student.findById(id);
        render(student);
    }
}
