package controllers;

import models.ClassRoomKind;
import models.Classroom;
import models.SchoolEvent;
import models.StudentChoices;
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
                List<StudentChoices> studentChoices = StudentChoices.listStudentsChoicesInClassrooms(schoolEvent, Arrays.asList(classroom.id));
                if (!studentChoices.isEmpty())
                    classroomStudentChoices.add(new ClassroomStudentChoices(classroom, studentChoices));
            }
            if (!classroomStudentChoices.isEmpty())
                classroomKindStudentChoices.add(new ClassRoomKindStudentChoices(classRoomKind, classroomStudentChoices));
        }

        render(schoolEvent, classroomKindStudentChoices);

    }
}
