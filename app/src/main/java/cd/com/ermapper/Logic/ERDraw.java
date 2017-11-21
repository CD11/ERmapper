package cd.com.ermapper.Logic;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.*;


import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.IOException;

import cd.com.ermapper.R;
import cd.com.ermapper.shapes.Attribute;
import cd.com.ermapper.shapes.Entity;
import cd.com.ermapper.shapes.Relationship;
import cd.com.ermapper.shapes.ShapeObject;

import static android.graphics.Color.BLACK;


public class ERDraw extends AppCompatActivity {
    public ERDiagram diagram;
    public DrawObjects object;
    public LinearLayout layout;
    public RelativeLayout textLayer;


    // This creates the ERDraw activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erdraw);
        diagram = this.getIntent().getParcelableExtra("diagram");

        layout = (LinearLayout) findViewById(R.id.diagramLayout);
        textLayer = (RelativeLayout) findViewById(R.id.textLayout);
        object = new DrawObjects(this, diagram, 0, textLayer);
        layout.addView(object);
    }

    // if add entity is pressed
    public void addEntity(View view){

        final EditText et = new EditText(this.getApplicationContext());
        setET(et);
        ShapeObject entity;

        if(diagram == null){
            Log.d("DiagramErrors ", " diagram is Null");
            entity = null;
        }else{
            entity = new Entity(et, String.valueOf(et.getText()),50, 50);

            if(entity != null){
                diagram.addObject(entity);
            }else{
                Log.d("DiagramErrors ", "entity is Null");

            }
        }
        object.setState(1);
        object.invalidate();
        textLayer.addView(et);
        textLayer.bringToFront();
        Log.d("er Context", this.toString() +" " + textLayer.getChildCount());

    }

    // if add attribute is pressed
    public void addAttribute(View view){
         final EditText et = new EditText(this.getApplicationContext());
        setET(et);
        ShapeObject attribute;

        if(diagram == null){
            Log.d("DiagramErrors ", " diagram is Null");
            attribute = null;
        }else{
            attribute= new Attribute(et, String.valueOf(et.getText()), 50, 50);
        if(attribute != null) {
            diagram.addObject(attribute);
        }
        }

        textLayer.addView(et);
        object.setState(2);
        object.invalidate();

    }

    // if add relationship is pressed
    public void addRelationship(View view){
        ShapeObject relationship;

        if(diagram == null){
            Log.d("DiagramErrors ", " diagram is Null");
            relationship = null;
        }else {
            relationship = new Relationship();
        }

        object.setState(3);
        object.setRelationship(relationship);
        object.invalidate();


    }

    // if select button is pressed
    public void SelectObject(View  v){
        Log.d("State", Integer.toString(4));
        object.setState(4);
        object.invalidate();

    }

    // when normalize button is clicked
    public void Normalize(View  v){
        Intent intent;
       try {
           intent = new Intent(this, FDNormalization.class);
           intent.putExtra("diagram", diagram);
           startActivity(intent);
       }catch (NullPointerException e){
            LinearLayout layout = (LinearLayout) v.findViewById(R.id.diagramLayout);
            AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.setTitle("Error");
            ad.setMessage(e.getMessage());
            ad.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            ad.show();


       }

    }

    public void delete(View  v) {
        object.setState(5);
    }

    public void SavetoXml(View v){
        AlertDialog ad = new AlertDialog.Builder(this).create();
        ad.setTitle("Save to XML");
        FileOperations f = new FileOperations();
        try {
             f.SaveDiagram(diagram, this);
            ad.setMessage("File Successfully Saved");
        }catch(IOException e) {
            ad.setMessage("Error Saving File "+ e);

        }


        ad.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        ad.show();
    }
    // every object has an editable name set to this
    public void setET(final EditText et){
        // set Edit text
        et.setHint("Name");
        et.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et.setSingleLine();
        et.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        et.setTextColor(BLACK);
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {

                if(!view.hasFocus()){
                    for(ShapeObject e: diagram.getObjects()){
                        if(e.getEditId() == view){
                            e.setName(String.valueOf(et.getText()));
                            break;

                        }
                    }
                }
            }
        });

    }

}
