package controllers;

import models.*;
import models.wrapper.ClassroomProposalActivities;
import play.Logger;
import play.i18n.Lang;
import play.modules.excel.RenderExcel;
import play.mvc.Controller;
import play.mvc.With;

import java.util.*;

/**
 *
 */
@With({ExcelControllerHelper.class, Tracker.class})
public class Exports extends Controller {


    public static void byClassroom(long proposalId) {
        Logger.info("Export byClassroom for proposal %d in %s", proposalId, Lang.get());

        SchoolEventProposal proposal = SchoolEventProposal.findById(proposalId);

        Set<Classroom> classrooms = new TreeSet<>(Comparator.comparing(o -> o.name));
        proposal.forEachAssignment(assignment -> classrooms.add(assignment.studentChoices.student.classroom));

        render(proposal, classrooms);
    }


    public static void bySelectedClassroom(long proposalId, long classroomId) {
        Logger.info("Export byClassroom for proposal %d and selected classroom %d", proposalId, classroomId);

        SchoolEventProposal proposal = SchoolEventProposal.findById(proposalId);
        if (proposal == null) badRequest("No proposal ID: " + proposalId);
        Classroom classroom = Classroom.findById(classroomId);
        if (proposal == null) badRequest("No classroom ID: " + classroomId);


        Set<Student> absents = new TreeSet<>(Comparator.comparing(o -> o.name));

        List<SchoolEventGroupStudentAssignment> studentAssignments = SchoolEventGroupStudentAssignment.listByClassRoomAndProposal(proposalId, classroomId);
        Set<ClassroomProposalActivities> activities =
                ClassroomProposalActivities.wrap(studentAssignments);

        for (Student student : classroom.students) {
            absents.add(student);
            for (SchoolEventGroupStudentAssignment studentAssignment : studentAssignments) {
                absents.remove(studentAssignment.studentChoices.student);
            }
        }


        Date date = new Date();
        String __EXCEL_FILE_NAME__ = "bySelectedClassroom.xlsx";

//        renderArgs.put(RenderExcel.RA_ASYNC, true);
        renderArgs.put(RenderExcel.RA_FILENAME, String.format("%s-Classroom-export-proposal-%s.xlsx", classroom.name, proposal.name));
        request.format = "xlsx";
        render(__EXCEL_FILE_NAME__, classroom, proposal, activities, date, absents);
    }



    public static void byActivities(long proposalId) {

        Logger.info("Export byActivities for proposal %d", proposalId);

        SchoolEventProposal proposal = SchoolEventProposal.findById(proposalId);
        if (proposal == null) badRequest("No proposal ID: " + proposalId);

        Set<SchoolEventActivity> activities = new TreeSet<>(Comparator.comparing(o -> o.name));
        proposal.forEachAssignment(assignment -> activities.add(assignment.schoolEventGroupActivity.schoolEventActivity));

        render(proposal, activities);
    }

    public static void bySelectedActivity(long proposalId, long activityId) {
        Logger.info("Export bySelectedActivity for proposal %d and selected activity %d", proposalId, activityId);


        List<SchoolEventGroupStudentAssignment> assignments =
                SchoolEventGroupStudentAssignment.listByProposalAndActivity(proposalId, activityId);

        assignments.sort(
                Comparator.<SchoolEventGroupStudentAssignment,Integer>comparing(assignment->assignment.studentChoices.student.classroom.kind.ordinal())
                        .thenComparing(assignment->assignment.studentChoices.student.name)
        );

        SchoolEventProposal proposal = SchoolEventProposal.findById(proposalId);
        SchoolEventActivity activity = SchoolEventActivity.findById(activityId);


        Date date = new Date();
        String __EXCEL_FILE_NAME__ = "bySelectedActivity.xlsx";

        renderArgs.put(RenderExcel.RA_FILENAME, String.format("%s-Activity-export-proposal-%s.xlsx", activity.name, proposal.name));
        request.format = "xlsx";
        render(__EXCEL_FILE_NAME__, activity, proposal, assignments, date);

    }
}
