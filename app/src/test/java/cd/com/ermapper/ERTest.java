package cd.com.ermapper;

/**
 * Created by CD on 11/28/2017.
 */

import org.junit.Before;
import org.junit.Test;

import cd.com.ermapper.Components.Attribute;
import cd.com.ermapper.Components.Entity;
import cd.com.ermapper.Components.Relationship;
import cd.com.ermapper.Logic.ERDiagram;
import cd.com.ermapper.Logic.ERDraw;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ERTest {
    /* Global vas */
    ERDiagram diagram;

    @Before
    public void Setup(){
        diagram = new ERDiagram("ERDiagram");
    }

    @Test
    public void addEntity() throws Exception {
        Entity e = new Entity(null,"Enitty",0,0);
        diagram.addObject(e);
        assertThat(true, is(diagram.getObjects().contains(e)));
    }

    @Test
    public void addAttribute() throws Exception {
        Attribute a = new Attribute(null,"Attribute",0,0);
        diagram.addObject(a);
        assertThat(true, is(diagram.getObjects().contains(a)));
    }

    @Test
    public void setPrimaryAttribute() throws Exception {
        Attribute a = new Attribute(null,"Attribute",0,0);
        diagram.addObject(a);

        assertThat(true, is(diagram.getObjects().contains(a)));
    }



    @Test
    public void AddAttributeToEntity() throws Exception{
        Entity e = new Entity(null,"Enitty1",0,0);
        Attribute a = new Attribute(null,"Attribute1",0,0);
        diagram.addObject(e);
        e.addAttribute(a);
        // entity is added to diagram objects
        // attribute is added to e.
        assertThat(true, is(diagram.getObjects().contains(e)));
        assertThat(false, is(diagram.getObjects().contains(a)));  // a should not be in the list of diagram objects
        assertThat(true, is(e.getAttr().contains(a)));
    }


    @Test
    public void addRelationship() throws  Exception{
        Entity e = new Entity(null,"Enitty2",0,0);
        Entity e1 = new Entity(null,"Enitty3",0,0);

        Relationship r = new Relationship("relationship1",  e,e1);
        diagram.addObject(r);
        assertThat(true, is(diagram.getObjects().contains(r)));




    }

}

