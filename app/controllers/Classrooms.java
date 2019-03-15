package controllers;

import models.Classroom;
import models.Student;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import play.Logger;
import play.mvc.Controller;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * @author rcoqueugniot
 * @since 14.03.19
 */
public class Classrooms extends Controller {

    // Create a DataFormatter to format and get each cell's value as String
    static DataFormatter dataFormatter = new DataFormatter();

    public static void list() {
        List<Classroom> classrooms = Classroom.findAll();

        if(classrooms.isEmpty())
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

        workbook.forEach(sheet -> {
            String sheetName = sheet.getSheetName();
            if (sheetName.contains("SECTION")) {
                System.out.println("=> " + sheetName);
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
                            classroom = new Classroom();
                            classroom.name = classe;
                            classroom.save();
                        }
                        student.classroom = classroom;

                        student.save();
                    }

                });
            }
        });


        // 1. You can obtain a sheetIterator and iterate over it
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        System.out.println("Retrieving Sheets using Iterator");
//        while (sheetIterator.hasNext()) {
        Sheet sheet = sheetIterator.next();
        System.out.println("=> " + sheet.getSheetName());


        list();
    }
}
