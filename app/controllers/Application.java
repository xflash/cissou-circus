package controllers;

import models.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import play.Logger;
import play.mvc.Controller;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Application extends Controller {

    // Create a DataFormatter to format and get each cell's value as String
    static DataFormatter dataFormatter = new DataFormatter();

    public static void index() {

//        render();
        Classrooms.list();
    }


    public static void resetAll() {

        deleteAllEntities();

        init();
    }

    public static void init() {
        render();
    }

    public static void uploadData(String title, File excel) throws IOException, InvalidFormatException {
        Logger.info("Uploading file %s", excel);

        SchoolEvent schoolEvent = new SchoolEvent("Circus").save();

        Workbook workbook = WorkbookFactory.create(excel);

        deleteAllEntities();

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
                        boolean involvedClassroom = false;
                        if (row.getRowNum() == 0) {
                            String choix1 = dataFormatter.formatCellValue(row.getCell(4));
                            if (choix1.equals("choix1")) involvedClassroom = true;
//                            String choix2 = dataFormatter.formatCellValue(row.getCell(5));
//                            String choix3 = dataFormatter.formatCellValue(row.getCell(6));
//                            String choix4 = dataFormatter.formatCellValue(row.getCell(7));

                        }
                        if (row.getRowNum() > 0) {
                            String identifiant = dataFormatter.formatCellValue(row.getCell(0));
                            String classe = dataFormatter.formatCellValue(row.getCell(1));
                            String nom = dataFormatter.formatCellValue(row.getCell(2));
                            String prenom = dataFormatter.formatCellValue(row.getCell(3));

                            Student student = new Student();
                            student.firstname = prenom;
                            student.name = nom;
                            student.identifiant = identifiant;
                            student.classroom = Classroom.findOrCreate(classRoomKind, classe);
                            student.save();


                            StudentChoices.createStudentChoices(student,
                                    dataFormatter.formatCellValue(row.getCell(4)),
                                    dataFormatter.formatCellValue(row.getCell(5)),
                                    dataFormatter.formatCellValue(row.getCell(6)),
                                    dataFormatter.formatCellValue(row.getCell(7)), schoolEvent)
                                    .save();
                        }

                    });
                }
            }
        });

        Classrooms.list();
    }

    private static void deleteAllEntities() {
        StudentChoices.deleteAll();
        Student.deleteAll();
        ActivityKind.deleteAll();
        Classroom.deleteAll();
    }

}