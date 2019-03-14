package models;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUnsignedDecimalNumber;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * @author rcoqueugniot
 * @since 14.03.19
 */
@Entity
public class Classroom extends Model {
    public String name;

    @OneToMany(mappedBy = "classroom")
    List<Student> students;
}
