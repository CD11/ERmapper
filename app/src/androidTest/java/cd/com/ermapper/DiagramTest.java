package cd.com.ermapper;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.RelativeLayout;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import cd.com.ermapper.Components.Attribute;
import cd.com.ermapper.Components.Entity;
import cd.com.ermapper.Logic.DrawObjects;
import cd.com.ermapper.Logic.ERDiagram;
import cd.com.ermapper.Logic.ERDraw;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by CD on 11/30/2017.
 */

@RunWith(AndroidJUnit4.class)
public class DiagramTest {
    Context appContext = InstrumentationRegistry.getTargetContext();
    ERDiagram diagram;
    ERDraw drawActivity;
    DrawObjects draw;
    @Rule
    public ActivityTestRule<ERDraw> mActivityRule = new ActivityTestRule<>(
            ERDraw.class);

    @Before
    public void setup(){
        diagram = new ERDiagram("erDiagram");
        drawActivity = mActivityRule.getActivity();
        drawActivity.setDiagram(diagram);

        draw = new DrawObjects(appContext, diagram, 0, (RelativeLayout)drawActivity.findViewById(R.id.textLayout));
        drawActivity.setOnDraw(draw);
    }


    @Test
    public void CreateEntity(){
        onView(withId(R.id.entity)).perform(click());
        assertThat(1, is(diagram.getObjects().size()));
        assertThat(Entity.class, CoreMatchers.<Class<Entity>>is((Class<Entity>) diagram.getObjects().get(0).getClass()));


    }

    @Test
    public void CreateAttribute(){
        onView(withId(R.id.attribute)).perform(click());
        assertThat(1, is(diagram.getObjects().size()));
        assertThat(Attribute.class, CoreMatchers.<Class<Attribute>>is((Class<Attribute>) diagram.getObjects().get(0).getClass()));
    }

    @Test
    public void CreateRelationship(){
        onView(withId(R.id.relationship)).perform(click());

        // It is ready for you to draw a relationship
        assertThat(draw.getState(), is(3));

    }


    @Test
    public void DeleteObject(){

        onView(withId(R.id.delete)).perform(click());

        // It is ready for you delete an object
        assertThat(draw.getState(), is(5));

    }

    @Test
    public void SelectObject(){
        onView(withId(R.id.Select)).perform(click());

        // It is ready for you to to Select an object
        assertThat(draw.getState(), is(4));

    }

    @Test
    public void save(){
        onView(withId(R.id.Save)).perform(click());

        // It is ready for you to to Select an object
        File file  = new File("files/"+ diagram.getName() +".xml");
        assertTrue(!file.equals(null));
        //TODO : add more test to check the XML file is a valid demonstration of the ER Diagram


    }
}
