package models;

import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.List;

@Entity
public class ActivityKind extends Model {

    @Column(unique = true)
    public
    String name;

    public static ActivityKind findByName(String name) {
        return ActivityKind.find("byName", name).first();
    }

    public static ActivityKind create(String name) {
        ActivityKind activityKind = new ActivityKind();
        activityKind.name = name;
        return activityKind.save();
    }

    static ActivityKind findOrCreate(String choix) {
        if (StringUtils.isNotBlank(choix)) {
            choix = StringUtils.trim(choix);
            ActivityKind activityKind = findByName(choix);
            return activityKind == null ? create(choix) : activityKind;
        } else
            return null;
    }

    public static List<ActivityKind> listAllOrdered() {
        return find("select ak from ActivityKind ak order by ak.name").fetch();
    }

    public static void mergeAll(List<Long> selection) {
        if (selection.size() <= 1) return;
        ActivityKind root = findById(selection.get(0));
        List<Long> subList = selection.subList(1, selection.size() - 1);
        Logger.info("Merging all ActivityKind from %s into %s", subList, root.name);
        for (Long id : subList) {
            ActivityKind todelete = findById(id);
            mergeStudentChoices1(root, todelete);
            mergeStudentChoices2(root, todelete);
            mergeStudentChoices3(root, todelete);
            mergeStudentChoices4(root, todelete);
            todelete.delete();
        }
    }

    private static void mergeStudentChoices1(ActivityKind root, ActivityKind mergeMe) {
        for (Student student : StudentChoices.findAllChoice1(mergeMe)) {
            student.choices.choice1=root;
            student.save();
        }
    }
    private static void mergeStudentChoices2(ActivityKind root, ActivityKind mergeMe) {
        for (Student student : StudentChoices.findAllChoice2(mergeMe)) {
            student.choices.choice2=root;
            student.save();
        }
    }
    private static void mergeStudentChoices3(ActivityKind root, ActivityKind mergeMe) {
        for (Student student : StudentChoices.findAllChoice3(mergeMe)) {
            student.choices.choice3=root;
            student.save();
        }
    }
    private static void mergeStudentChoices4(ActivityKind root, ActivityKind mergeMe) {
        for (Student student : StudentChoices.findAllChoice4(mergeMe)) {
            student.choices.choice4=root;
            student.save();
        }
    }

    public static void deleteAll(List<Long> selection) {
        Logger.info("Deleting all ActivityKind from %s", selection);

    }
}
