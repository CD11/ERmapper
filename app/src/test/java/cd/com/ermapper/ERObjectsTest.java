package cd.com.ermapper;

/**
 * Created by CD on 11/28/2017.
 */

import android.content.Context;
import android.widget.EditText;
import android.widget.RelativeLayout;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import cd.com.ermapper.Components.Attribute;
import cd.com.ermapper.Components.Entity;
import cd.com.ermapper.Components.Relationship;
import cd.com.ermapper.Components.ShapeObject;
import cd.com.ermapper.Logic.ERDiagram;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ERObjectsTest {
    /* Global vas */
    ERDiagram diagram;

    @Mock
    RelativeLayout textLayer;

    @Mock
    Context c;

    @Mock
    EditText et;

    @Before
    public void Setup()
    {
        diagram = new ERDiagram("ERDiagram");
    }

    @Test
    public void addEntity() throws Exception {
        ShapeObject e = new Entity("Enitty");
        diagram.addObject(e);
        assertThat(true, is(diagram.getObjects().contains(e)));
    }

    @Test
    public void removeEntity() throws  Exception{
        ShapeObject e = new Entity("Entity");
        diagram.addObject(e);
        assertThat(true, is(diagram.getObjects().contains(e)));
        diagram.deleteO(e);
        assertThat(false, is(diagram.getObjects().contains(e)));
    }

    @Test
    public void removeEntityWithAttriubte() throws  Exception{
        ShapeObject e = new Entity("Enitty");
        ShapeObject a = new Attribute("Attribute");
        diagram.addObject(e);
        ((Entity)e).addAttribute((Attribute) a);
        assertThat(true, is(diagram.getObjects().contains(e)));
        assertThat(true, is(((Entity)e).getAttr().contains((Attribute)a)));
        diagram.removeO(e,textLayer);
        assertThat("entity:", false, is(diagram.getObjects().contains(e)));
        assertThat(true, is(diagram.getObjects().contains((Attribute)a)));
    }
    @Test
    public void addAttribute() throws Exception {
        ShapeObject a = new Attribute("Attribute");
        diagram.addObject(a);
        assertThat(true, is(diagram.getObjects().contains(a)));
    }

    @Test
    public void removeAttribute() throws  Exception{
        ShapeObject a = new Entity("Attribute");
        diagram.addObject(a);
        assertThat(true, is(diagram.getObjects().contains(a)));
        diagram.deleteO(a);
        assertThat(false, is(diagram.getObjects().contains(a)));
    }


    @Test
    public void setPrimaryAttribute() throws Exception {
        ShapeObject a = new Attribute("Attribute");
        diagram.addObject(a);
        assertThat(true, is(diagram.getObjects().contains(a)));
    }



    @Test
    public void AddAttributeToEntity() throws Exception{
        ShapeObject e = new Entity("Enitty1");
        ShapeObject a = new Attribute("Attribute1");
        diagram.addObject(e);
        ((Entity)e).addAttribute((Attribute) a);
        // entity is added to diagram objects
        // attribute is added to e.
        assertThat(true, is(diagram.getObjects().contains(e)));
        assertThat(false, is(diagram.getObjects().contains(a)));  // a should not be in the list of diagram objects
        assertThat(true, is(((Entity)e).getAttr().contains((Attribute)a)));
    }

    @Test
    public void removeAttriubteFromEntity() throws  Exception{
        ShapeObject e = new Entity("Enitty");
        ShapeObject a = new Attribute("Attribute");
        diagram.addObject(e);
        ((Entity)e).addAttribute((Attribute) a);
        assertThat("Diagram contains entity" ,true, is(diagram.getObjects().contains(e)));
        assertThat("Entity contains attribute", true, is(((Entity)e).getAttr().contains((Attribute)a)));
        ((Entity)e).removeObj((Attribute) a, textLayer);
        assertThat("Diagram removed entity", true, is(diagram.getObjects().contains(e)));
        assertThat("diagram contains attribute", false, is(((Entity)e).getAttr().contains((Attribute)a)));
    }



    @Test
    public void addRelationship() throws  Exception{
        ShapeObject e = new Entity("Enitty2");
        ShapeObject e1 = new Entity("Enitty3");
        Relationship r = new Relationship("relationship1",  (Entity)e, (Entity)e1);
        diagram.addObject(r);
        assertThat(true, is(diagram.getObjects().contains(r)));
    }

    @Test
    public void CreateWeakEntity() throws  Exception{
        ShapeObject e = new Entity("Enitty4");
        ShapeObject e1 = new Entity("Enitty5");
        ((Entity)e).addEntity(e1);
        diagram.addObject(e);
        assertThat(true, is(diagram.getObjects().contains(e)));
        assertThat(true, is(((Entity)e).getWeak().contains(e1)));
        assertThat(false,is(diagram.getObjects().contains(e1)));
    }

    @Test
    public void removeEntityFromBinaryRelationship() throws Exception{
        ShapeObject e = new Entity("Enitty6");
        ShapeObject e1 = new Entity("Enitty7");
        Relationship r = new Relationship("relationship2",  (Entity)e, (Entity)e1);
        diagram.addObject(r);
        assertThat("The diagram contains the relationship", true, is(diagram.getObjects().contains(r)));
        diagram.removeO(e,textLayer);
        assertThat("The diagram removed the relationship, because it only had one object", true, is(diagram.getObjects().contains(r)));
        assertThat("The diagram has the second entity object", true, is(diagram.getObjects().contains(e1)));
        assertThat("The diagram does not have the entity object", false, is(diagram.getObjects().contains(e)));
    }


    @Test
    public void removeEntityFromTernaryRelationship() throws Exception{
        ShapeObject e = new Entity("Enitty6");
        ShapeObject e1 = new Entity("Enitty7");
        ShapeObject e2 = new Entity("Enitty8");
        Relationship r = new Relationship("relationship2",  (Entity)e, (Entity)e1);
        ArrayList<Entity> temp = new ArrayList<>();
        temp.add((Entity) e);
        temp.add((Entity) e1);
        temp.add((Entity) e2);
        r.addObj((Entity)e2,null);
        diagram.addObject(r);
        assertThat("has all objects", true, is(r.getallobjects().equals(temp)));
        assertThat("diagram contains the relationship",true, is(diagram.getObjects().contains(r)));
        assertThat("The relaitonship has entity object",true, is(r.getObjs().contains((Entity)e)));
        assertThat("The relaitonship has entity object 1", true, is(r.getObjs().contains((Entity)e1)));
        assertThat("The relaitonship has entity object 2", true, is(r.getObjs().contains((Entity)e2)));
        diagram.removeO(e,textLayer);
        assertThat("diagram still has the relationship", true, is(diagram.getObjects().contains(r)));
        assertThat("The relaitonship does not have the object", false, is(r.getObjs().contains((Entity)e1)));
    }
}

