package controllers;

import models.*;
import models.wrapper.ClassroomProposalActivities;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.ss.usermodel.Workbook;
import play.Logger;
import play.db.jpa.JPABase;
import play.exceptions.UnexpectedException;
import play.modules.excel.RenderExcel;
import play.mvc.Controller;
import play.mvc.With;

import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 */
@With(ExcelControllerHelper.class)
public class Exports extends Controller {

    public static void byClassroom(long proposalId) {
        Logger.info("Export byClassroom for proposal %d", proposalId);

        SchoolEventProposal proposal = SchoolEventProposal.findById(proposalId);

        Set<Classroom> classrooms = new TreeSet<>(Comparator.comparing(o -> o.name));
        proposal.forEachAssignment(assignment -> classrooms.add(assignment.studentChoices.student.classroom));

        render(proposal, classrooms);
    }


    public static void bySelectedClassroom(long proposalId, long classroomId) {
        Logger.info("Export byClassroom for proposal %d and selected classroom %d", proposalId, classroomId);

        Set<ClassroomProposalActivities> activities =
                ClassroomProposalActivities.wrap(SchoolEventGroupStudentAssignment.listByClassRoomAndProposal(proposalId, classroomId));

        SchoolEventProposal proposal = SchoolEventProposal.findById(proposalId);
        Classroom classroom = Classroom.findById(classroomId);

        Date date = new Date();

        String __EXCEL_FILE_NAME__ = "bySelectedClassroom.xlsx";
//        renderArgs.put(RenderExcel.RA_ASYNC, true);
//        render(__EXCEL_FILE_NAME__, proposal, date);
        request.format = "xlsx";
        render(__EXCEL_FILE_NAME__, classroom, proposal, activities, date);
    }

    public static void byActivities(long id) {

    }

}
