package cd.com.ermapper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import cd.com.ermapper.Components.Attribute;
import cd.com.ermapper.Components.Entity;
import cd.com.ermapper.Components.Relation;
import cd.com.ermapper.Components.RelationSchema;
import cd.com.ermapper.Components.Relationship;
import cd.com.ermapper.Components.ShapeObject;
import cd.com.ermapper.Logic.DatabaseHandler;
import cd.com.ermapper.Logic.ERDiagram;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by CD on 11/30/2017.
 */

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    private DatabaseHandler database;
    ERDiagram diagram;
    ShapeObject student, grade, course;
    RelationSchema r;
    Context appContext = InstrumentationRegistry.getTargetContext();
    EditText et;

    @Before
    public void setUp() throws Exception {
       // appContext.deleteDatabase(database.getName());

     /* This sets up an ERDiagram with 3 Entities Student, grade and Course
           - Student :  primary key : id,  attributes : dob (multivalued, year, month day)
           - Grade:   primary key : id, code, attributes : mark
             - will be a weak entity
           - Course :   primary key : code, attributes : room
           - all three will be part of the same relationship
           */
        et = new EditText(appContext);

        diagram = new ERDiagram("erdiagram");
        student = new Entity("Student");
        ShapeObject id = new Attribute("id");
        ShapeObject dob = new Attribute("dob");
        ShapeObject year = new Attribute("year");
        ShapeObject month = new Attribute("month");
        ShapeObject day = new Attribute("day");
        ((Attribute)id).setPrimary(true);
        ((Attribute)dob).addAttribute((Attribute) year);
        ((Attribute)dob).addAttribute((Attribute) month);
        ((Attribute)dob).addAttribute((Attribute) day);
        ((Entity)student).addAttribute((Attribute) id);
        ((Entity)student).addAttribute((Attribute) dob);

        grade = new Entity("Grade");
        ShapeObject gId = new Attribute("id");
        ShapeObject gCode = new Attribute("code");
        ShapeObject mark = new Attribute("mark");
        ((Attribute)gId).setPrimary(true);
        ((Attribute)gCode).setPrimary(true);
        ((Entity)grade).addAttribute((Attribute) gId);
        ((Entity)grade).addAttribute((Attribute) gCode);
        ((Entity)grade).addAttribute((Attribute) mark);
        ((Entity)grade).setWeak(true);


        course = new Entity("Course");
        ShapeObject code = new Attribute("code");
        ((Attribute)code).setPrimary(true);
        ShapeObject room = new Attribute("room");
        ((Entity)course).addAttribute((Attribute) code);
        ((Entity)course).addAttribute((Attribute) room);

        Relationship hasA = new Relationship("has a", (Entity) student, (Entity) course);

        // Sets cardinality so not null, values do not matter for this test
        hasA.getTextObjs().get(0).setNum(et);
        hasA.getTextObjs().get(0).setO(grade);
        hasA.getTextObjs().get(1).setNum(et);
        hasA.getTextObjs().get(1).setO(student);
        diagram.addObject(hasA);
        hasA.addObj((Entity) grade,null);

        // these are called when passed to parcel
        diagram.getRelationships();
        diagram.getAllEntities();

    }



    @Test
    public void createDB() throws Exception {
        RelationSchema r = new RelationSchema(diagram.getAllEntities(), diagram.getRelationshipsObjs());
        r.removalAllTemp();
        database = new DatabaseHandler(appContext, "erDiagram", null,1, null);
        database.setRelations(r);
        SQLiteDatabase db =  database.getWritableDatabase();

        for (Relation t : r.getRelations()) {
            String query = "SELECT * FROM "+t.getName();
            Cursor c = db.rawQuery(query, null);
            assertThat(c.getColumnCount(), is(t.getAttributes().size()));
            System.out.print(c.getColumnCount());
            for(int i = 0; i < t.getAttributes().size()-1; i++){
                String actual = c.getColumnName(i);
                assertTrue(t.getAttributes().hasName(actual));
                c.moveToNext();
            }
        }
    }

}
