package controllers;

import models.*;
import models.wrapper.ClassRoomKindStudentChoices;
import models.wrapper.ClassroomStudentChoices;
import play.Logger;
import play.db.jpa.JPABase;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 */
public class SchoolEventStudentChoices extends Controller {

    public static void root() {
        List<SchoolEvent> schoolEvents = SchoolEvent.findAll();
        if (schoolEvents.size() == 0)
            Application.init();

        if (schoolEvents.size() > 1)
            badRequest("TODO handle " + schoolEvents.size() + " school events");

        list(schoolEvents.get(0).id);
    }

    public static void list(long schoolEventId) {
        SchoolEvent schoolEvent = SchoolEvent.findById(schoolEventId);
        ArrayList<ClassRoomKindStudentChoices> classroomKindStudentChoices = new ArrayList<>();

        for (ClassRoomKind classRoomKind : ClassRoomKind.values()) {
            List<ClassroomStudentChoices> classroomStudentChoices = new ArrayList<>();
            for (Classroom classroom : Classroom.findByKind(classRoomKind)) {
                List<StudentChoices> studentChoices = StudentChoices.listAllStudentsChoicesInClassroom(schoolEvent, classroom.id);
                if (!studentChoices.isEmpty())
                    classroomStudentChoices.add(new ClassroomStudentChoices(classroom, studentChoices));
            }
            if (!classroomStudentChoices.isEmpty())
                classroomKindStudentChoices.add(new ClassRoomKindStudentChoices(classRoomKind, classroomStudentChoices));
        }

        render(schoolEvent, classroomKindStudentChoices);

    }

    public static void edit(long schoolEventId, long studentChoicesId) {

        StudentChoices studentChoices = StudentChoices.findById(studentChoicesId);
        if (studentChoices == null) badRequest("StudentChoices id " + studentChoicesId);


        List<SchoolEventActivity> activities = SchoolEventActivity.listAllOrdered(schoolEventId);

        render(schoolEventId, studentChoices, activities);
    }

    public static void save(long schoolEventId, long studentChoicesId, long choice1Id, long choice2Id, long choice3Id, long choice4Id, boolean absentFriday, boolean absentSaturday, boolean absent) {

        StudentChoices studentChoices = StudentChoices.findById(studentChoicesId);
        if (studentChoices == null) badRequest("StudentChoices id " + studentChoicesId);

        SchoolEventActivity choice1 = SchoolEventActivity.findById(choice1Id);
        if (choice1 == null) badRequest("SchoolEventActivity id " + choice1Id);

        SchoolEventActivity choice2 = SchoolEventActivity.findById(choice2Id);
        if (choice2 == null) badRequest("SchoolEventActivity id " + choice2Id);

        SchoolEventActivity choice3 = SchoolEventActivity.findById(choice3Id);
        if (choice3 == null) badRequest("SchoolEventActivity id " + choice3Id);

        SchoolEventActivity choice4 = SchoolEventActivity.findById(choice4Id);
        if (choice4 == null) badRequest("SchoolEventActivity id " + choice4Id);

        if(choice1.equals(choice2)) edit(schoolEventId, studentChoicesId);
        if(choice1.equals(choice3)) edit(schoolEventId, studentChoicesId);
        if(choice1.equals(choice4)) edit(schoolEventId, studentChoicesId);

        if(choice2.equals(choice1)) edit(schoolEventId, studentChoicesId);
        if(choice2.equals(choice3)) edit(schoolEventId, studentChoicesId);
        if(choice2.equals(choice4)) edit(schoolEventId, studentChoicesId);

        if(choice3.equals(choice1)) edit(schoolEventId, studentChoicesId);
        if(choice3.equals(choice2)) edit(schoolEventId, studentChoicesId);
        if(choice3.equals(choice4)) edit(schoolEventId, studentChoicesId);

        if(choice4.equals(choice1)) edit(schoolEventId, studentChoicesId);
        if(choice4.equals(choice2)) edit(schoolEventId, studentChoicesId);
        if(choice4.equals(choice3)) edit(schoolEventId, studentChoicesId);

        studentChoices.choice1=choice1;
        studentChoices.choice2=choice2;
        studentChoices.choice3=choice3;
        studentChoices.choice4=choice4;

        if(absentFriday && absentSaturday) {
            absentFriday=false;
            absentSaturday=false;
            absent = true;
        }
        studentChoices.absent = absent;
        studentChoices.absentFriday = absentFriday;
        studentChoices.absentSaturday = absentSaturday;

        studentChoices.save();

        list(schoolEventId);
    }
}
