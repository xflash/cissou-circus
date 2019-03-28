package controllers;

import models.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import play.Logger;
import play.db.jpa.Model;
import play.mvc.Controller;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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


        Workbook workbook = WorkbookFactory.create(excel);

        deleteAllEntities();

        SchoolEvent schoolEvent = new SchoolEvent("Circus").save();

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


                            StudentChoices.createStudentChoices(schoolEvent, student,
                                    dataFormatter.formatCellValue(row.getCell(4)),
                                    dataFormatter.formatCellValue(row.getCell(5)),
                                    dataFormatter.formatCellValue(row.getCell(6)),
                                    dataFormatter.formatCellValue(row.getCell(7)),

                                    dataFormatter.formatCellValue(row.getCell(9)),
                                    dataFormatter.formatCellValue(row.getCell(10)),
                                    dataFormatter.formatCellValue(row.getCell(11)),
                                    dataFormatter.formatCellValue(row.getCell(12))
                            ).save();
                        }

                    });
                }
            }
        });
        Logger.info("Merging some known mistakes");
        merginKnownMistakes("acro dyn", Collections.singletonList("acro dyna"));
        merginKnownMistakes("clowns", Collections.singletonList("c;owns"));
        merginKnownMistakes("couture", Collections.singletonList("costume"));
        merginKnownMistakes("hula hoop", Arrays.asList("hula hoop trapeze", "hul hoop", "huka hoop"));
        merginKnownMistakes("jongle", Arrays.asList("jonglerie"));
        merginKnownMistakes("magie", Arrays.asList("magiciens", "magicien"));
        merginKnownMistakes("tonneaux", Arrays.asList("tonnaux"));
        Classrooms.list();
    }

    private static void merginKnownMistakes(String root, List<String> mistakes) {
        SchoolEventActivity acro_dyn = SchoolEventActivity.findByName(root);
        if (acro_dyn != null) {
            SchoolEventActivity.mergeAll(acro_dyn.id,
                    mistakes.stream()
                            .map(SchoolEventActivity::findByName)
                            .filter(Objects::nonNull)
                            .map(Model::getId)
                            .collect(Collectors.toList()));
        }
    }

    private static void deleteAllEntities() {
        SchoolEventGroupStudentAssignment.deleteAll();
        SchoolEventGroupActivity.deleteAll();
        SchoolEventGroup.deleteAll();
        SchoolEventProposal.deleteAll();

        StudentChoices.deleteAll();
        SchoolEventActivity.deleteAll();

        Student.deleteAll();
        Classroom.deleteAll();
        SchoolEvent.deleteAll();
    }

}