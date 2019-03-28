package models;

import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
public class SchoolEventActivity extends Model {


    @ManyToOne
    SchoolEvent schoolEvent;

    public
    String name;

    String description;

    public SchoolEventActivity(SchoolEvent schoolEvent, String name) {
        this.schoolEvent = schoolEvent;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SchoolEventActivity that = (SchoolEventActivity) o;
        return Objects.equals(schoolEvent, that.schoolEvent) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), schoolEvent, name);
    }

    public static SchoolEventActivity findByName(String name) {
        return SchoolEventActivity.find("byName", name).first();
    }

    public static SchoolEventActivity create(SchoolEvent schoolEvent, String name) {
        return new SchoolEventActivity(schoolEvent, name).save();
    }

    static SchoolEventActivity findOrCreate(String name, SchoolEvent schoolEvent) {
        name = StringUtils.trim(name);
        if (StringUtils.isNotBlank(name)&&!name.equals("n")) {
            SchoolEventActivity schoolEventActivity = findByName(name);
            return schoolEventActivity == null ? create(schoolEvent, name) : schoolEventActivity;
        } else
            return null;
    }

    public static List<SchoolEventActivity> listAllOrdered(long schoolEventId) {
        return find("select ak from SchoolEventActivity ak where ak.schoolEvent.id = :schoolEventId order by ak.name")
                .bind("schoolEventId", schoolEventId)
                .fetch();
    }

    public static List<SchoolEventActivity> listOrdered(List<Long> selection) {
        return find("select ak from SchoolEventActivity ak where ak.id in :selection order by ak.name")
                .bind("selection", selection)
                .fetch();
    }

    public static void mergeAll(long rootId, List<Long> selection) {
        SchoolEventActivity root = findById(rootId);
        selection.remove(rootId);
        Logger.info("Merging all SchoolEventActivity from %s into %s", selection, root.name);
        for (Long id : selection) {
            SchoolEventActivity todelete = findById(id);
            mergeStudentChoices1(root, todelete);
            mergeStudentChoices2(root, todelete);
            mergeStudentChoices3(root, todelete);
            mergeStudentChoices4(root, todelete);
            todelete.delete();
        }
    }
    private static void mergeStudentChoices1(SchoolEventActivity root, SchoolEventActivity mergeMe) {
        for (StudentChoices studentChoices : StudentChoices.findAllChoice1(mergeMe)) {
            studentChoices.choice1=root;
            studentChoices.save();
        }
    }
    private static void mergeStudentChoices2(SchoolEventActivity root, SchoolEventActivity mergeMe) {
        for (StudentChoices studentChoices  : StudentChoices.findAllChoice2(mergeMe)) {
            studentChoices.choice2=root;
            studentChoices.save();
        }
    }
    private static void mergeStudentChoices3(SchoolEventActivity root, SchoolEventActivity mergeMe) {
        for (StudentChoices studentChoices  : StudentChoices.findAllChoice3(mergeMe)) {
            studentChoices.choice3=root;
            studentChoices.save();
        }
    }

    private static void mergeStudentChoices4(SchoolEventActivity root, SchoolEventActivity mergeMe) {
        for (StudentChoices studentChoices  : StudentChoices.findAllChoice4(mergeMe)) {
            studentChoices.choice4=root;
            studentChoices.save();
        }
    }

    public static void deleteAll(Collection<Long> selection) {
        Logger.info("Deleting all SchoolEventActivity from %s", selection);

        for (Long id : selection) {
            SchoolEventActivity schoolEventActivity = SchoolEventActivity.findById(id);
            List<StudentChoices> inAllChoices = StudentChoices.findInAllChoices(schoolEventActivity);
            if(inAllChoices.isEmpty())
                schoolEventActivity.delete();

        }
    }
}
