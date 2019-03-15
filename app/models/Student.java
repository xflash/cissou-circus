package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * @author rcoqueugniot
 * @since 14.03.19
 */
@Entity
public class Student extends Model {

   public  String identifiant;

    public  String name;

    public  String firstname;

    @ManyToOne()
    public  Classroom classroom;


}
