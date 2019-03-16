package models;

import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
public class ActivityKind extends Model {


    @ManyToOne
    SchoolEvent schoolEvent;

    public
    String name;

    public ActivityKind(SchoolEvent schoolEvent, String name) {
        this.schoolEvent = schoolEvent;
        this.name = name;
    }

    public static ActivityKind findByName(String name) {
        return ActivityKind.find("byName", name).first();
    }

    public static ActivityKind create(SchoolEvent schoolEvent, String name) {
        return new ActivityKind(schoolEvent, name).save();
    }

    static ActivityKind findOrCreate(String name, SchoolEvent schoolEvent) {
        name = StringUtils.trim(name);
        if (StringUtils.isNotBlank(name)&&!name.equals("n")) {
            ActivityKind activityKind = findByName(name);
            return activityKind == null ? create(schoolEvent, name) : activityKind;
        } else
            return null;
    }

    public static List<ActivityKind> listAllOrdered() {
        return find("select ak from ActivityKind ak order by ak.name").fetch();
    }

    public static List<ActivityKind> listOrdered(List<Long> selection) {
        return find("select ak from ActivityKind ak where ak.id in :selection order by ak.name")
                .bind("selection", selection)
                .fetch();
    }

    public static void mergeAll(long rootId, List<Long> selection) {
        ActivityKind root = findById(rootId);
        selection.remove(rootId);
        Logger.info("Merging all ActivityKind from %s into %s", selection, root.name);
        for (Long id : selection) {
            ActivityKind todelete = findById(id);
            mergeStudentChoices1(root, todelete);
            mergeStudentChoices2(root, todelete);
            mergeStudentChoices3(root, todelete);
            mergeStudentChoices4(root, todelete);
            todelete.delete();
        }
    }
    private static void mergeStudentChoices1(ActivityKind root, ActivityKind mergeMe) {
        for (StudentChoices studentChoices : StudentChoices.findAllChoice1(mergeMe)) {
            studentChoices.choice1=root;
            studentChoices.save();
        }
    }
    private static void mergeStudentChoices2(ActivityKind root, ActivityKind mergeMe) {
        for (StudentChoices studentChoices  : StudentChoices.findAllChoice2(mergeMe)) {
            studentChoices.choice2=root;
            studentChoices.save();
        }
    }
    private static void mergeStudentChoices3(ActivityKind root, ActivityKind mergeMe) {
        for (StudentChoices studentChoices  : StudentChoices.findAllChoice3(mergeMe)) {
            studentChoices.choice3=root;
            studentChoices.save();
        }
    }

    private static void mergeStudentChoices4(ActivityKind root, ActivityKind mergeMe) {
        for (StudentChoices studentChoices  : StudentChoices.findAllChoice4(mergeMe)) {
            studentChoices.choice4=root;
            studentChoices.save();
        }
    }

    public static void deleteAll(Collection<Long> selection) {
        Logger.info("Deleting all ActivityKind from %s", selection);
        for (Long id : selection) {
            ActivityKind activityKind = ActivityKind.findById(id);
            activityKind.delete();
        }
    }
}
