package cd.com.ermapper;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.*;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.Serializable;

import static android.os.Build.VERSION_CODES.N;
import static cd.com.ermapper.R.id.New;
import static cd.com.ermapper.R.id.start;


public class ERDraw extends AppCompatActivity {

    public final int xOffset = 50;
    public final int yOffset = 50;
    public ERDiagram diagram;
    public DrawObjects object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erdraw);
        diagram = (ERDiagram) this.getIntent().getSerializableExtra("diagram");
        object = new DrawObjects(this, diagram, 0);
        LinearLayout layout = (LinearLayout) findViewById(R.id.diagramLayout);
        layout.addView(object);


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
        et.setSingleLine();
        et.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        et.setImeOptions(EditorInfo.IME_ACTION_DONE);


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
            entity = new Entity(et,50, 50, 150, 180);

            if(entity != null){
                diagram.addEntity((Entity) entity);
                diagram.addObject(entity);
            }else{
                Log.d("DiagramErrors ", "entity is Null");

            }
        }


        object.setState(1);
        object.invalidate();
        textLayer.addView(et);
        textLayer.bringToFront();
    }

    public void addAttribute(View view){


        LinearLayout textLayer = (LinearLayout) findViewById(R.id.textLayout);
        final EditText et = new EditText(this.getApplicationContext());
        ShapeObject attribute;

        // set Edit text
        et.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        et.setHint("Name");
        et.setX(xOffset);
        et.setY(yOffset);
        et.bringToFront();
        et.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        et.setSingleLine();
        et.setImeOptions(EditorInfo.IME_ACTION_DONE);

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
             attribute= new Attribute(et, 50, 50, 150, 180);

            if(attribute != null){
                diagram.addObject(attribute);
            }else{
                Log.d("DiagramErrors ", "entity is Null");

            }
        }

        object.setState(2);
        object.invalidate();

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
        et.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        et.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et.setSingleLine();
        et.setVisibility(View.INVISIBLE);

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

        object.setState(3);

        object.setRelationship(relationship);

        object.invalidate();
        textLayer.addView(et);
        textLayer.bringToFront();

    }

    public void SelectObject(View  v){
        Log.d("State", Integer.toString(4));

        object.setState(4);
        object.invalidate();

    }

    public void Normalize(View  v){
        Log.d("State", Integer.toString(4));
        Intent i = new Intent(this, FDNormalization.class);

        i.putExtra("diagram", (Serializable)diagram);
        startActivity(i);

    }

}
