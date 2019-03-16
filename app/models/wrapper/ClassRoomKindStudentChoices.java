package models.wrapper;

import models.ClassRoomKind;

import java.util.List;

/**
 * @author rcoqueugniot
 * @since 16.03.19
 */
public class ClassRoomKindStudentChoices {
    private final ClassRoomKind classroomKind;
    private final List<ClassroomStudentChoices> classroomStudentChoices;

    public ClassRoomKindStudentChoices(ClassRoomKind classroomKind, List<ClassroomStudentChoices> classroomStudentChoices) {

        this.classroomKind = classroomKind;
        this.classroomStudentChoices = classroomStudentChoices;
    }
}
