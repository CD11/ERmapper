package cd.com.ermapper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import cd.com.ermapper.Components.Attribute;
import cd.com.ermapper.Components.DependencySet;
import cd.com.ermapper.Components.Entity;
import cd.com.ermapper.Components.EntitySet;
import cd.com.ermapper.Components.RelationSchema;
import cd.com.ermapper.Components.Relationship;
import cd.com.ermapper.Components.ShapeObject;
import cd.com.ermapper.Logic.DatabaseHandler;
import cd.com.ermapper.Logic.ERDiagram;
import cd.com.ermapper.Logic.FDNormalization;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by CD on 11/28/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class NormalizationTest {

    ERDiagram diagram;
    ShapeObject student, teacher, course;
    RelationSchema r;

    FDNormalization fd= new FDNormalization();
    DatabaseHandler  dbhandler;
    Context context;
    SQLiteDatabase db;
    @Mock
    EditText et;





    @Before
    public void setup(){
        /* This sets up an ERDiagram with 3 Entities Student, Teacher and Course
           - Student :  primary key : id,  candidate keys : dob (multivalued, year, month day)
           - Teacher:   primary key : tId, candidate keys : name
             - will be a weak entity
           - Course :   primary key : code, candidate keys : room
           - all three will be part of the same relationship


           */

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

        teacher = new Entity("Teacher");
        ShapeObject tId = new Attribute("tId");
        ShapeObject name = new Attribute("name");
        ((Attribute)tId).setPrimary(true);
        ((Entity)teacher).addAttribute((Attribute) tId);
        ((Entity)teacher).addAttribute((Attribute) name);



        course = new Entity("Course");
        ShapeObject code = new Attribute("code");
        ((Attribute)code).setPrimary(true);
        ShapeObject room = new Attribute("room");
        ((Entity)course).addAttribute((Attribute) code);
        ((Entity)course).addAttribute((Attribute) room);

        Relationship hasA = new Relationship("has a", (Entity) student, (Entity) course);

        // Sets cardinality so not null, values do not matter for this test
        hasA.getTextObjs().get(0).setNum(et);
        hasA.getTextObjs().get(0).setO(teacher);
        hasA.getTextObjs().get(1).setNum(et);
        hasA.getTextObjs().get(1).setO(student);
        diagram.addObject(hasA);
        hasA.addObj((Entity) teacher,null);

        // these are called when passed to parcel
        diagram.getRelationships();
        diagram.getAllEntities();
    }


    @Test
    public void BinaryDecomposition(){
        EntitySet actual = diagram.relationshipDecomposition();
        EntitySet expected = new EntitySet();
        expected.add((Entity) student);
        expected.add((Entity) teacher);
        expected.add((Entity) course);

        // contains all defined entities
        assertThat(true, is(actual.containsAll(expected)));

        // contains 1 temproary
        assertThat(actual.size(), is(expected.size()+1));

    }


    @Test
    public void RelationSchema(){
        r = new RelationSchema(diagram.getAllEntities(), diagram.getRelationshipsObjs());
        assertThat(4, is(r.size()));
        assertThat("Student: [id  | dob ]  ", is(r.getRelations().get(0).toString()));
        assertThat("Course: [code  | room ]  ", is(r.getRelations().get(1).toString()));
        assertThat("Teacher: [tId  | name ]  ", is(r.getRelations().get(2).toString()));
        assertThat("dob: [dob  | year ,month ,day ]  ", is(r.getRelations().get(3).toString()));
        r.removalAllTemp();
    }

    @Test
    public void FunctionalDependencies(){
        DependencySet d = new DependencySet();
        r = new RelationSchema(diagram.getAllEntities(), diagram.getRelationshipsObjs());
        r.removalAllTemp();
        fd.setDiagram(diagram);
        fd.setSchema(r);
        fd.findDependencies();
        d.addAll( r.getDependencies());

        assertThat(4, is(d.size()));

        assertThat("id  -> dob", is(d.getElements().get(0).toString().replace("\\n", "").trim()));
        assertThat("code  -> room", is(d.getElements().get(1).toString().replace("\\n", "").trim()));
        assertThat("tId  -> name", is(d.getElements().get(2).toString().replace("\\n", "").trim()));
        assertThat("dob  -> year ,month ,day", is(d.getElements().get(3).toString().replace("\\n", "").trim()));
    }


    @Test
    public void MinCover(){
        DependencySet d = new DependencySet();
        r = new RelationSchema(diagram.getAllEntities(), diagram.getRelationshipsObjs());
        r.removalAllTemp();
        fd.setDiagram(diagram);
        fd.setSchema(r);
        fd.findDependencies();
        d.addAll( r.getDependencies());
        DependencySet min = fd.minCover(r.getDependencies());


        assertThat("id  -> dob", is(min.getElements().get(0).toString().replace("\\n", "").trim()));
        assertThat("code  -> room", is(min.getElements().get(1).toString().replace("\\n", "").trim()));
        assertThat("tId  -> name", is(min.getElements().get(2).toString().replace("\\n", "").trim()));
        assertThat("dob  -> year ,month ,day", is(min.getElements().get(3).toString().replace("\\n", "").trim()));

    }


    @Test
    public void CandidateKey(){



    }


    @Test
    public void thirdNF(){
         }


    @Test
    public void dp_lj(){
        }








}
