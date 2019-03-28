package controllers;

import models.SchoolEventProposal;
import play.Logger;
import play.db.jpa.JPABase;
import play.modules.excel.RenderExcel;
import play.mvc.Controller;
import play.mvc.With;

import java.util.Date;

/**
 * @author rcoqueugniot
 * @since 28.03.19
 */
@With(ExcelControllerHelper.class)
public class Exports extends Controller {

    public static void byClassroom(long proposalId) {
        Logger.info("Export byClassroom for proposal %d", proposalId);
        SchoolEventProposal proposal = SchoolEventProposal.findById(proposalId);
        Date date = new Date();
        String __EXCEL_FILE_NAME__ = "byClassroom.xlsx";
//        renderArgs.put(RenderExcel.RA_ASYNC, true);
//        renderExcel(__EXCEL_FILE_NAME__, proposal, date);
    }

    public static void byActivities(long id) {

    }

}
