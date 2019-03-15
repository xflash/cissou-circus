package controllers;

import models.ClassRoomKind;
import models.Classroom;
import models.Student;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import play.Logger;
import play.mvc.Controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.*;

/**
 * @author rcoqueugniot
 * @since 14.03.19
 */
public class Classrooms extends Controller {


    public static void list() {
        List<Classroom> classrooms = Classroom.findAll();

        if (classrooms.isEmpty())
            Application.init();

        List<Long> selected =
                Classroom.find("select c.id from Classroom c where c.kind in :kinds ")
                        .bind("kinds",
                                stream(ClassRoomKind.values())
                                        .filter(value -> value.ordinal() >= ClassRoomKind.GRANDE_MOYENNE_SECTION.ordinal())
                                        .collect(Collectors.toList()))
                        .fetch();
        render(classrooms, selected);
    }

    public static void openDetail(long id) {
        Classroom classroom = Classroom.findById(id);

        List<Student> students =
                Student.find("select s from Student s where classroom.id = ?1 order by name, firstname", classroom.id)
                        .fetch();

        render(classroom, students);

    }



}
