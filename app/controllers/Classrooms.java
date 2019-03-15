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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author rcoqueugniot
 * @since 14.03.19
 */
public class Classrooms extends Controller {

    // Create a DataFormatter to format and get each cell's value as String
    static DataFormatter dataFormatter = new DataFormatter();

    public static void list() {
        List<Classroom> classrooms = Classroom.findAll();

        if (classrooms.isEmpty())
            init();

        List<Long> selected = Classroom.find("select c.id from Classroom c where c.name like 'GRANDE%'").fetch();
        render(classrooms, selected);
    }

    public static void init() {
        render();
    }

    public static void resetAll() {

        Student.deleteAll();
        Classroom.deleteAll();

        init();
    }

    public static void openDetail(long id) {
        Classroom classroom = Classroom.findById(id);

        List<Student> students =
                Student.find("select s from Student s where classroom.id = ?1 order by name, firstname", classroom.id)
                        .fetch();

        render(classroom, students);

    }

    public static void uploadData(String title, File excel) throws IOException, InvalidFormatException {
        Logger.info("Uploading file %s", excel);

        Workbook workbook = WorkbookFactory.create(excel);

        Student.deleteAll();
        Classroom.deleteAll();

        Pattern pattern = Pattern.compile("(.*)-[0-9]{2}");

        workbook.forEach(sheet -> {
            String sheetName = sheet.getSheetName();
            if (Stream.of(ClassRoomKind.values()).anyMatch(k -> sheetName.contains(k.name()))) {
                Matcher matcher = pattern.matcher(sheetName);
                if (!matcher.matches())
                    Logger.error("Bad classroom sheetname %s", sheetName);
                {
                    ClassRoomKind classRoomKind = ClassRoomKind.valueOf(matcher.group(1));
                    Logger.info("Creating classroom %s - %s", sheetName, classRoomKind.name());
                    sheet.forEach(row -> {
                        if (row.getRowNum() > 0) {
                            int cellnum = 0;
                            String identifiant = dataFormatter.formatCellValue(row.getCell(cellnum++));
                            String classe = dataFormatter.formatCellValue(row.getCell(cellnum++));
                            String nom = dataFormatter.formatCellValue(row.getCell(cellnum++));
                            String prenom = dataFormatter.formatCellValue(row.getCell(cellnum++));

                            Student student = new Student();
                            student.firstname = prenom;
                            student.name = nom;
                            student.identifiant = identifiant;
                            Classroom classroom = Classroom.find("byName", classe).first();
                            if (classroom == null) {
                                classroom = Classroom.createClassroom(classe, classRoomKind);
                            }
                            student.classroom = classroom;

                            student.save();
                        }

                    });
                }
            }
        });

        list();
    }

}
