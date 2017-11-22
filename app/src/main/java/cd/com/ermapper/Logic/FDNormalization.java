package cd.com.ermapper.Logic;

import android.content.DialogInterface;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import cd.com.ermapper.R;
import cd.com.ermapper.relations.AttributeSet;
import cd.com.ermapper.relations.DependencySet;
import cd.com.ermapper.relations.EntitySet;
import cd.com.ermapper.relations.FunctionalDependency;
import cd.com.ermapper.relations.Relation;
import cd.com.ermapper.relations.SetOfAttributeSets;
import cd.com.ermapper.shapes.Attribute;
import cd.com.ermapper.shapes.Entity;
import cd.com.ermapper.shapes.Relationship;


public class FDNormalization extends AppCompatActivity {


    //list of functional dependencies
    private DependencySet functionalDependencies;
    private ArrayList<Relation> relations;
    private AttributeSet attributes;

    //to display lists to screen
    private ListView attributesView;
    private ListView functionalDependenciesView;
    private ArrayAdapter fdListAdapter;
    private ArrayAdapter attributesAdapter;

    private ERDiagram diagram;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fdnormalization);

        //list of functional dependencies
        functionalDependencies = new DependencySet();
        relations = new ArrayList<>();
        attributes = new AttributeSet();
        functionalDependenciesView= (ListView) findViewById(R.id.FDList);
        attributesView = (ListView) findViewById(R.id.AttributeList);
        diagram = this.getIntent().getParcelableExtra("diagram");
        // get the ER diagram
        functionalDependencies.addAll(findDependencies()); // get all functional dependencies

        //TODO: check these methods and decide how they work
         performNormalization();  // Perform normalization
         performAttributeClosure(functionalDependencies.getAllAttributes()); // perform Attribute Closure
    }

    private void addFD(FunctionalDependency anFD){
        functionalDependencies.add(anFD);
        rebuiltAttributesList();
    }


    public DependencySet findDependencies(){
        FunctionalDependency fd;
        EntitySet entities = diagram.getBinaryEntities(); // simplifies all N-ary Relationships
        ArrayList<Relationship> relationships = diagram.getRelationshipsObjs();
        // Temp values
        Relation tempR;
        AttributeSet primarkey = new AttributeSet();
        AttributeSet attributes = new AttributeSet();
    /* follows the ER->Relational mapping rules in fundamentals of DB systems by Ramex Elmasri & Shamkant B. Navathe
        1. For all Regular entity types
            a. assign a Relation, pick a primary key
                if the primary key is a complex attribute: all attributes will be included
           b. if  an attribute is complex, create a new relation
        2. For all Weak Entities, create a foreign key that references all Primary keys of its Strong relation
        3. For all 1:1 relationships do
            a. Choose one of the relations-say S-and include a foreign key in S the primary key of T.
            b. It is better to choose an entity type with total participation in R in the role of S.
        4. for All  1:N relationships do
           a. identify the relation S that represent the participating entity type at the N-side of the relationship type.
           b. Include as foreign key in S the primary key of the relation T that represents the 1 side of the relationship type
           c. Include any simple attributes of the 1:N relation type as attributes of S.
        5.  For all M:N
         a. For each regular binary M:N relationship type R, create a new relation S to represent R.
         b. Include as foreign key attributes in S the primary keys of the relations that represent the participating entity types; their combination will form the primary key of S.
         c. Also include any simple attributes of the M:N relationship type (or simple components of composite attributes) as attributes of S.
        6.  For multivariable attributes
            a. For each multivalued attribute A, create a new relation R.
            b.This relation R will include an attribute corresponding to A, plus the primary key attribute K-as a foreign key in R-of the relation that represents the entity type of relationship type that has A as an attribute.
            c. The primary key of R is the combination of A and K. If the multivalued attribute is composite, we include its simple components.

*/

        // Steps 3 -> 5 require modifying an existing relation based on the diagram entity
        // we will update the entity and then create the Relation per steps 1 and 2.
        for(Relationship r: relationships){
            if(r!= null && r.getObj1().getClass() == Entity.class && r.getObj2().getClass() == Entity.class) {
                Entity e1 = (Entity) r.getObj1();
                Entity e2 = (Entity) r.getObj2();

                ////////////////////// Step 3 //////////////////////////////////////
                //Choose one of the relations-say S-and include a foreign key in S the primary key of T.
                if(r.isOneToOne()) {
                    ((Entity) r.getObj1()).getAttr().addAll(((Entity) r.getObj2()).foreignAttrs());
                }
                /////////////////// Step 4  //////////////////////////////////
                //a. identify the relation S that represent the participating entity type at the N-side of the relationship type.
                if (r.isOneToN()) {
                   if(r.getTextObjs().get(0).getNum().getText().equals("N")) //  obj1 is S
                       //b. Include as foreign key in S the primary key of the relation T that represents the 1 side of the relationship type
                       ((Entity) r.getObj1()).getAttr().addAll(((Entity) r.getObj2()).foreignAttrs());
                   else// obj2 is s
                        //b. Include as foreign key in S the primary key of the relation T that represents the 1 side of the relationship type
                        ((Entity) r.getObj2()).getAttr().addAll(((Entity) r.getObj1()).foreignAttrs());


                    ///////////////////  Step 5 //////////////////////////////////////////////
                    //a. For each regular binary M:N relationship type R, create a new relation S to represent R.
                }else if(r.isMToN()){
                    //b. Include as foreign key attributes in S the primary keys of the relations that represent the participating entity types; their combination will form the primary key of S.
                    //c. Also include any simple attributes of the M:N relationship type (or simple components of composite attributes) as attributes of S.
                    Relation newRelation = new Relation((Entity)r.getObj1(), (Entity)r.getObj2());
                    relations.add(newRelation);
                }
            }
        }


        for( Entity e: entities.getElements()) {

            //////////////////////// Step 1
            // get attributes for Strong Entities;  strong -> FD
            if (!e.isWeak()) {
                for (Attribute a : e.getAttr().getElements()) {
                    if (a.isPrimary() ||a.isForeign() && a.getName() != "-1") {
                        primarkey.add(a);
                    }
                    attributes.add(a);
                }
                tempR = new Relation(attributes, primarkey, e.getName());
                relations.add(tempR);
            }

            primarkey.clear();
            attributes.clear();

            /////////////////// Step 2
            // Check weak Entities
            if (!e.equals(null) || !e.getWeak().isEmpty()) {
                /* if entity has weak Entities add the primary key of its strong relation to it */
                for (Entity eW : e.getWeak()) {
                    // add Strong entity primary key as key to weak entity
                    for (Attribute a : e.getAttr().getElements()) {
                        if (a.isPrimary() || a.isForeign() && a.getName() != "-1")
                            eW.getAttr().add(a);
                    }
                    // add attributes of weak entites;   weak -> FD
                    for (Attribute a : eW.getAttr().getElements()) {
                        if (a.isPrimary() || a.isForeign() && a.getName() != "-1") {
                            primarkey.add(a);
                        }
                        attributes.add(a);
                    }
                    //    fd = new FunctionalDependency(primarkey, attributes, e.getName());
                    //   if(fd != null || !fd.isTrivial()) functionalDependencies.add(fd);
                    tempR = new Relation(attributes, primarkey, e.getName());
                    relations.add(tempR);
                }
            }
        }


        ////////////// Step 6 ///////////////////////////
        // check for multiple attributes.
       // For each multivalued attribute A, create a new relation R.
       // This relation R will include an attribute corresponding to A,
        // plus the primary key attribute K-as a foreign key in R-of the relation that represents the entity type of relationship type that has A as an attribute.
       // The primary key of R is the combination of A and K. If the multivalued attribute is composite, we include its simple components.

        for(Entity e: entities.getElements()){
            for(Attribute a: e.getAttr().getElements()){ // for each attribute a in e
                primarkey.clear();
                attributes.clear();
                if(!a.getValues().isEmpty()) {            // check if a is complex and create its own relation.
                    primarkey.add(a);
                    attributes.add(a);
                    attributes.addAll(a.getValuesSet());
                    Log.d("name", String.valueOf(attributes.containsAll(primarkey)));
                    tempR = new Relation(attributes, primarkey, a.getName());
                    relations.add(tempR);
                }

            }
        }

        //////////////////// To FD //////////////////////////////////////////////
        // Relations arraylist now contains all proper Relations  we can now find the FDs
        for(Relation r: relations) {
            fd = new FunctionalDependency(r.getPrimaryKey(), r.getAttributes(), r.getName());
            if (fd != null || !fd.isTrivial()) functionalDependencies.add(fd);
        }
        return functionalDependencies;
    }

    private void rebuiltAttributesList(){
        //Rebuild the attributes list
        attributes.clear();
        DependencySet FDs = new DependencySet();
        //Gather all the functional dependencies
        for(FunctionalDependency fd : functionalDependencies.getElements()){FDs.add(fd);}
        AttributeSet allAttributes = functionalDependencies.getAllAttributes();
       // Collections.sort(allAttributes.getElements());
           for (Attribute a : allAttributes.getElements()) attributes.add(a);
    }

    private void performAttributeClosure(AttributeSet leftAttributes){
        //consoleTextArea.clear(); //clear the console of previous results
        DependencySet FDs = new DependencySet();
        //Gather all the functional dependencies
        for(FunctionalDependency fd : functionalDependencies.getElements()){FDs.add(fd);}
        //print all the attributes
        AttributeSet allAttributes = FDs.getAllAttributes();
      //  Collections.sort(allAttributes.getElements());

        //print all the attributes
        System.out.print("ATTRIBUTES:\n");
        for(Attribute att : allAttributes.getElements())
            System.out.print(att.toString());
        System.out.print("\n==================================================\n");


        //print all the functional dependencies created from data file
        System.out.print("FUNCTIONAL DEPENDENCIES:\n");
        System.out.print("\n==================================================\n");

        System.out.print("ATTRIBUTE CLOSURE:\n");
        System.out.print("CLOSURE {" + leftAttributes + "}+");
        AttributeSet closureSet = leftAttributes.closure(FDs);
        System.out.print(closureSet.toString());
        if(closureSet.containsAll(allAttributes)){
            System.out.print("{" + leftAttributes + "} is a SUPERKEY");
            boolean isMinimal = true;
            for(Attribute a : leftAttributes.getElements()){
                AttributeSet attributesCopy = new AttributeSet(leftAttributes);
                attributesCopy.remove(a);
                AttributeSet c = attributesCopy.closure(FDs);
                if(c.containsAll(allAttributes)) isMinimal = false;
            }
            if(isMinimal)
                System.out.print("{" + leftAttributes + "} is MINIMAL (i.e. is CANDIDATE KEY)");
            else
                System.out.print("{" + leftAttributes + "} is NOT MINIMAL");
        }

    }

    private void performNormalization(){
        String returnstring = " ";
        DependencySet FDs = new DependencySet();
        TextView tv  = (TextView) findViewById(R.id.results);
        tv.setMovementMethod(new ScrollingMovementMethod());


        //Gather all the functional dependencies
        for(FunctionalDependency fd : functionalDependencies.getElements()){FDs.add(fd);}

        //print all the attributes
        AttributeSet allAttributes = FDs.getAllAttributes();
        fdListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, FDs.getStringElements());
        functionalDependenciesView.setAdapter(fdListAdapter);
        attributesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allAttributes.getElements());
        attributesView.setAdapter(attributesAdapter);


        //print all the attributes
        returnstring += "ATTRIBUTES:\n";
        for(Attribute att : allAttributes.getElements())
            returnstring += att.toString();
        returnstring += "\n==================================================\n";


        //print all the functional dependencies created from ER diagram
        returnstring += "FUNCTIONAL DEPENDENCIES:\n";
        returnstring += (FDs.toString());
        returnstring += ("\n==================================================\n");

        DependencySet minCover = FDs.minCover();
        returnstring += ("Minimal Cover:");
        returnstring += ( minCover.toString());

        DependencySet toMerge = minCover.copy();
        DependencySet newMinCover = new DependencySet();

        while(!toMerge.isEmpty()){
            FunctionalDependency fd = toMerge.getElements().get(0);
            toMerge.remove(fd);
            minCover.remove(fd);
            newMinCover.add(fd);
            for(FunctionalDependency fd2 : toMerge.getElements()){
                if(fd.getLHS().equals(fd2.getLHS())) {
                    fd.getRHS().addAll(fd2.getRHS());
                    minCover.remove(fd2);
                }
            }
            toMerge = minCover.copy();
        }

        minCover = newMinCover;

        //Minimal Cover with LHS's merged
        returnstring += ("\n============================\n");
        returnstring += ("MINIMAL COVER: MERGED LHS");

        returnstring += (minCover.toString());
        //check that minimal cover and original FD's are in fact equivalent

        if(FDs.equals(minCover))
            returnstring += ("FD Sets are Equivalent");
        else
            returnstring += ("FD Sets are NOT Equivalent");


            //find all the candidate keys of a table consisting of all
            //the attributes with respect to the functional dependencies
        returnstring += ("\n-------------------------------------------------------------\n");
        returnstring += ("CANDIDATE KEY FOR ALL ATTRIBUTES:\n");
        AttributeSet candidateKey = allAttributes.findCandidateKey(minCover);
        returnstring += (candidateKey.toString());

        returnstring += ("\n-------------------------------------------------------------\n");
        returnstring += ("ALL CANDIDATE KEYS (FOR SMALL EXAMPLES ONLY):\n");

            SetOfAttributeSets candidateKeys = allAttributes.allCandidateKeys(minCover);
            if(candidateKeys != null)
                for (AttributeSet aKey : candidateKeys.getElements())
                    System.out.print(aKey.toString());

        //Create Dependency Preserving 3NF tables
		/*
		 * This is the 3 step algorithm 16.4 presented in
		 * Elmasri and Navathe 6th ed. Which decomposes a set of attributes
		 * (universal relation) with respect to functional dependencies F.
		 *
		 *  Step 1) Find a minimal cover Fm of F
		 *
		 *  Step 2) For each left hand side X of FD in Fm
		 *  create with columns X U A1 U A2 U ...An where
		 *  X->A1, X->A2,... X->An are all the dependencies in Fm
		 *  with left hand side X
		 *
		 *  Step 3) Place any attributes not found in F in a table
		 *  of their own. (Note, that does not happen in this code since
		 *  the attributes are obtained from the functional dependencies on input.
		 */


            returnstring +=("\n=======================================================\n");
            returnstring += ("Dependency Preserving, 3NF tables\n");

            //Step 1: already done above
            //Step 2:
            ArrayList<Relation> database_3nf_dep_preserving = new ArrayList<Relation>();
            for(FunctionalDependency fd : minCover.getElements()){
                Relation table = new Relation(fd);
                database_3nf_dep_preserving.add(table);
            }
            //Step 3:
            AttributeSet minCoverAttributes = minCover.getAllAttributes();
            AttributeSet leftOverAttributes = new AttributeSet();
            for(Attribute a : allAttributes.getElements())
                if(!minCoverAttributes.contains(a)) leftOverAttributes.add(a);
            if(!leftOverAttributes.isEmpty()){
                Relation tableOfLeftOverAttributes = new Relation(leftOverAttributes,leftOverAttributes, "relation");
                database_3nf_dep_preserving.add(tableOfLeftOverAttributes);
            }

            for(Relation r : database_3nf_dep_preserving)
                returnstring += (r.toString());

                returnstring += ("\n=======================================================\n");
                returnstring += ("Lossless-Join, Dependency Preserving, 3NF tables\n");

        //Create Lossless-Join, Dependency Preserving 3NF tables
		/*
		 * This is based on the 4 step algorithm 16.6 presented in
		 * Elmasri and Navathe 6th ed. Which decomposes a set of attributes
		 * (universal relation) with respect to functional dependencies F.
		 *
		 *  Step 1) Find a minimal cover Fm of F
		 *
		 *  Step 2) For each left hand side X of FD in Fm
		 *  create with columns X U A1 U A2 U ...An where
		 *  X->A1, X->A2,... X->An are all the dependencies in Fm
		 *  with left hand side X
		 *
		 *  Step 3) If none of the tables created in Step 2 contains a
		 *  candidate key for the universal relation consisting of all the
		 *  attributes, then create a table consisting of a candidate key
		 *
		 *  Step 4) Remove redundant tables. If any table is a projection of another (has all its columns
		 *  appearing in another tables, then remove that table from the decomposition
		 */


            //Step 1 & 2
            ArrayList<Relation> database_3nf_lossless_join_dep_preserving = new ArrayList<Relation>();
            for(FunctionalDependency fd : minCover.getElements()) {
                Relation table = new Relation(fd);
                database_3nf_lossless_join_dep_preserving.add(table);
            }

            //Step 3: Ensure decomposition contains a key for an imaginary table
            //        consisting of all the attributes
            boolean keyFound = false;
            for (Relation table : database_3nf_lossless_join_dep_preserving){
                AttributeSet columns = table.getAttributes();
                if(columns.containsAll(candidateKey)) {
                    keyFound = true;
                    break;
                }

            }
            if(!keyFound)
                database_3nf_lossless_join_dep_preserving.add(new Relation(candidateKey,candidateKey, "name"));

            //Step 4: Remove any redundant tables
            //A table is redundant if all of its attributes appears in some other table.

            Relation redunantTable = null;
            while((redunantTable = Normalizer.findRedunantTable(database_3nf_lossless_join_dep_preserving)) != null){
                database_3nf_lossless_join_dep_preserving.remove(redunantTable);
                returnstring += ("\nRemoving Redundant table: " + redunantTable);

            }

            for(Relation r : database_3nf_lossless_join_dep_preserving)
                returnstring += (r.toString());

            tv.setText(returnstring);
        }


        public void createDB(View v){

            DatabaseHandler db = new DatabaseHandler(this.getBaseContext(), diagram.getName(), null, 1, relations);
            AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.setTitle("DB Creation");
            ad.setMessage(db.getDatabaseName() +" Successfully created");
            ad.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            ad.show();


    }



}
