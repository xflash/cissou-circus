package models;

import java.util.ArrayList;
import java.util.List;

public class SiblingStudent {
    private Student student;

    public SiblingStudent(Student student) {
        this.student = student;
    }


    public boolean sibling;

    public static List<SiblingStudent> wrapSiblings(List<Student> students0) {
        List<SiblingStudent> students = new ArrayList<>();
        SiblingStudent last = null;
        for (Student student : students0) {
            SiblingStudent siblingStudent = new SiblingStudent(student);
            students.add(siblingStudent);
            if(last!=null) {
                if(student.isSiblingWith(last.student)) {
                    last.setSibling(true);
                    siblingStudent.setSibling(true);
                }
            }
            last=siblingStudent;
        }
        return students;
    }

    public boolean isSibling() {
        return sibling;
    }

    public void setSibling(boolean sibling) {
        this.sibling = sibling;
    }

    public String getIdentifiant() {
        return student.identifiant;
    }

    public String getName() {
        return student.name;
    }

    public String getFirstname() {
        return student.firstname;
    }

    public Classroom getClassroom() {
        return student.classroom;
    }


}
