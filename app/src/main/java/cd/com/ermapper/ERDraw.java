package cd.com.ermapper;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DrawableUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.*;

import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import static android.R.attr.focusable;


public class ERDraw extends AppCompatActivity {

    public final int xOffset = 50;
    public final int yOffset = 50;
    public ERDiagram diagram;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erdraw);
        diagram = (ERDiagram) this.getIntent().getSerializableExtra("diagram");


    }



    public void addEntity(View view){

        LinearLayout layout = (LinearLayout) findViewById(R.id.diagramLayout);
        layout.requestFocus();
        LinearLayout textLayer = (LinearLayout) findViewById(R.id.textLayout);
        final EditText et = new EditText(this.getApplicationContext());
        ShapeObject entity;

        // set Edit text
        et.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        et.setHint("Name");
        et.setX(xOffset);
        et.setY(yOffset);
        et.bringToFront();
        et.setFocusable(true);
        et.setSingleLine();

        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {

                if(!view.hasFocus()){
                    for(Entity e: diagram.getEntities()){

                        if(e.getNameEdit() == view){
                            e.setName(String.valueOf(et.getText()));

                            break;

                        }
                    }
                }
            }
        });

        if(diagram == null){
            Log.d("DiagramErrors ", " diagram is Null");
            entity = null;
        }else{
            entity = new Entity(et);

            if(entity != null){
                diagram.addEntity((Entity) entity);
                diagram.addObject(entity);
            }else{
                Log.d("DiagramErrors ", "entity is Null");

            }
        }

        // display name and Entity
        DrawObjects newEntity = new DrawObjects(this, diagram); // Create Entity
        layout.addView(newEntity);
        textLayer.addView(et);
        textLayer.bringToFront();




    }

    public void addAttribute(View view){

        LinearLayout layout = (LinearLayout) findViewById(R.id.diagramLayout);
        layout.requestFocus();
        LinearLayout textLayer = (LinearLayout) findViewById(R.id.textLayout);
        final EditText et = new EditText(this.getApplicationContext());
        ShapeObject attribute;

        // set Edit text
        et.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        et.setHint("Name");
        et.setX(xOffset);
        et.setY(yOffset);
        et.bringToFront();
        et.setFocusable(true);
        et.setSingleLine();

        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {

                if(!view.hasFocus()){
                    for(Attribute e: diagram.getAttributes()){

                        if(e.getNameEdit() == view){
                            e.setName(String.valueOf(et.getText()));

                            break;

                        }
                    }
                }
            }
        });

        if(diagram == null){
            Log.d("DiagramErrors ", " diagram is Null");
            attribute = null;
        }else{
             attribute= new Attribute(et);

            if(attribute != null){
                diagram.addAttribute((Attribute) attribute);
                diagram.addObject(attribute);
            }else{
                Log.d("DiagramErrors ", "entity is Null");

            }
        }

        // display name and Entity
        DrawObjects newAttribute = new DrawObjects(this, diagram);
        layout.addView(newAttribute);
        textLayer.addView(et);
        textLayer.bringToFront();




    }

    public void addRelationship(View view){


        LinearLayout layout = (LinearLayout) findViewById(R.id.diagramLayout);
        LinearLayout textLayer = (LinearLayout) findViewById(R.id.textLayout);
        final EditText et = new EditText(this.getApplicationContext());
        ShapeObject relationship;

        // set Edit text
        et.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        et.setHint("Name");
        et.setX(xOffset);
        et.setY(yOffset);
        et.bringToFront();
        et.setFocusable(true);
        et.setSingleLine();

        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {

                if(!view.hasFocus()){
                    for(Attribute e: diagram.getAttributes()){

                        if(e.getNameEdit() == view){
                            e.setName(String.valueOf(et.getText()));

                            break;

                        }
                    }
                }
            }
        });



        if(diagram == null){
            Log.d("DiagramErrors ", " diagram is Null");
            relationship = null;
        }else{
            relationship= new Relationship(et);

            if(relationship != null){
               diagram.addObject(relationship);
            }else{
                Log.d("DiagramErrors ", "entity is Null");

            }
        }

        // display name and Entity
        DrawObjects newAttribute = new DrawObjects(this, diagram);

       // newAttribute.onCreate((Relationship) relationship);


       // newAttribute.createRelationship();
        layout.addView(newAttribute);
        textLayer.addView(et);
        textLayer.bringToFront();




    }

    public void SelectObject(View  v){



    }

}
