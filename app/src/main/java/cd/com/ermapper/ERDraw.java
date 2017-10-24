package cd.com.ermapper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.BadParcelableException;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.*;


import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import static android.R.attr.name;
import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;


public class ERDraw extends AppCompatActivity {

    public ERDiagram diagram;
    public DrawObjects object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erdraw);
        diagram = this.getIntent().getParcelableExtra("diagram");
        object = new DrawObjects(this, diagram, 0);
        LinearLayout layout = (LinearLayout) findViewById(R.id.diagramLayout);
        layout.addView(object);

    }

    // if add entity is pressed
    public void addEntity(View view){

        LinearLayout layout = (LinearLayout) findViewById(R.id.diagramLayout);
        layout.requestFocus();
        LinearLayout textLayer = (LinearLayout) findViewById(R.id.textLayout);
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
    }

    // if add attribute is pressed
    public void addAttribute(View view){
        LinearLayout textLayer = (LinearLayout) findViewById(R.id.textLayout);
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

        object.setState(2);
        object.invalidate();

        textLayer.addView(et);
        textLayer.bringToFront();
    }

    // if add relationship is pressed
    public void addRelationship(View view){
        LinearLayout layout = (LinearLayout) findViewById(R.id.diagramLayout);
        LinearLayout textLayer = (LinearLayout) findViewById(R.id.textLayout);
        final EditText et = new EditText(this.getApplicationContext());
        setET(et);
        et.setVisibility(View.INVISIBLE);
        ShapeObject relationship;

        if(diagram == null){
            Log.d("DiagramErrors ", " diagram is Null");
            relationship = null;
        }else {
            relationship = new Relationship(et, String.valueOf(et.getText()));
        }

        object.setState(3);
        object.setRelationship(relationship);
        relationship.moveName();
        object.invalidate();
        textLayer.addView(et);
        textLayer.bringToFront();

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
            diagram.findRelations();
            diagram.findDependencies();
            intent= new Intent(this, FDNormalization.class);
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

    // every object has an editable name set to this
    public void setET(final EditText et){
        // set Edit text
        et.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        et.setHint("Name");
        et.bringToFront();
        et.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et.setSingleLine();
        et.setShowSoftInputOnFocus(false);
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
