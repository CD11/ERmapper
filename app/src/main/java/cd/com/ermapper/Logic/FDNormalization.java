package cd.com.ermapper.Logic;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import cd.com.ermapper.Components.Attribute;
import cd.com.ermapper.Components.AttributeSet;
import cd.com.ermapper.Components.DependencySet;
import cd.com.ermapper.Components.EntitySet;
import cd.com.ermapper.Components.FunctionalDependency;
import cd.com.ermapper.Components.Relation;
import cd.com.ermapper.Components.RelationSchema;
import cd.com.ermapper.Components.Relationship;
import cd.com.ermapper.R;


public class FDNormalization extends AppCompatActivity {

    private RelationSchema relationSchema;

    //to display lists to screen
    private ListView attributesView;
    private ListView functionalDependenciesView;
    private TextView resultsView;
    private ArrayAdapter fdListAdapter;
    private ArrayAdapter attributesAdapter;

    private ERDiagram diagram;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fdnormalization);

        // list views
       // resultsView  = (TextView) findViewById(R.id.results);
        //resultsView.setMovementMethod(new ScrollingMovementMethod());
        diagram = this.getIntent().getParcelableExtra("diagram");

        EntitySet entities = diagram.getBinaryEntities(); // simplifies all N-ary Relationships
        ArrayList<Relationship> relationships = diagram.getRelationshipsObjs();
        try {
        relationSchema = new RelationSchema(entities, relationships);
        relationSchema.removalAllTemp();
        findDependencies(); // find all dependencies for the relationschema

        performNormalization();  // Perform normalization
       // String closure = performAttributeClosure(relationSchema.getDependencies().getAllAttributes());
      //  resultsView.setText(resultsView.getText());
        }catch (NullPointerException e){
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



    public void findDependencies(){
        FunctionalDependency fd;
        //////////////////// To FD //////////////////////////////////////////////
        // Relations arraylist now contains all proper Relations  we can now find the FDs
        for(Relation r: relationSchema.getRelations()) {
            fd = new FunctionalDependency(r.getPrimaryKey(), r.getAttributes(), r.getName());
            if (fd != null || !fd.isTrivial()) relationSchema.getDependencies().add(fd);
        }
    }

    public DependencySet findDependencies(RelationSchema relationSchema){
        FunctionalDependency fd;
        DependencySet d =new DependencySet();
        //////////////////// To FD //////////////////////////////////////////////
        // Relations arraylist now contains all proper Relations  we can now find the FDs
        for(Relation r: relationSchema.getRelations()) {
            fd = new FunctionalDependency(r.getPrimaryKey(), r.getAttributes(), r.getName());
            if (fd != null || !fd.isTrivial()) d.add(fd);
        }
        return d;
    }
    private String performAttributeClosure(AttributeSet leftAttributes){
        String returnString = "";
        TextView ac = findViewById(R.id.CandidateKeys);
        DependencySet FDs = new DependencySet();
        //Gather all the functional dependencies
        for(FunctionalDependency fd : relationSchema.getDependencies().getElements()){FDs.add(fd);}
        //print all the attributes
        AttributeSet allAttributes = FDs.getAllAttributes();
        //print all the attributes
        for(Attribute att : allAttributes.getElements())
            returnString +=(att.toString());

        //print all the functional dependencies created from data file
        returnString +=("CLOSURE {" + leftAttributes + "}+");
        AttributeSet closureSet = leftAttributes.closure(FDs);
        returnString +=(closureSet.toString());
        if(closureSet.containsAll(allAttributes)){
            returnString +=("{" + leftAttributes + "} is a SUPERKEY");
            boolean isMinimal = true;
            for(Attribute a : leftAttributes.getElements()){
                AttributeSet attributesCopy = new AttributeSet(leftAttributes);
                attributesCopy.remove(a);
                AttributeSet c = attributesCopy.closure(FDs);
                if(c.containsAll(allAttributes)) isMinimal = false;
            }
            if(isMinimal)
                returnString +=("{" + leftAttributes + "} is MINIMAL (i.e. is CANDIDATE KEY)");
            else
                returnString +=("{" + leftAttributes + "} is NOT MINIMAL");
        }
        ac.setText(returnString);
        return  returnString;
    }

    private void performNormalization(){
        String returnstring = " ";
        DependencySet FDs = new DependencySet();
        TextView attributes = findViewById(R.id.attributes);
        TextView ck = findViewById(R.id.CandidateKeys);

        TextView fdr = findViewById(R.id.fds);
        TextView mc = findViewById(R.id.minCover);
        TextView dpT = findViewById(R.id.dptables);
        TextView lj_dpT = findViewById(R.id.LJDPtables);

        //Gather all the functional dependencies
        for(FunctionalDependency fd : relationSchema.getDependencies().getElements()){FDs.add(fd);}

        //print all the attributes
        AttributeSet allAttributes = FDs.getAllAttributes();

        //print all the attributes
        for(Attribute att : allAttributes.getElements())
            returnstring += att.toString();
        attributes.setText(returnstring);
        returnstring = " ";


        //print all the functional dependencies created from ER diagram
        returnstring += (FDs.toString()+"\n");
        fdr.setText(returnstring);
        returnstring = " ";


        /***********************************************************/
        DependencySet minCover = FDs.minCover();
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

        returnstring += (minCover.toString());
        //check that minimal cover and original FD's are in fact equivalent

        if(FDs.equals(minCover))
            returnstring += ("FD Sets are Equivalent");
        else
            returnstring += ("FD Sets are NOT Equivalent");

        mc.setText(returnstring);
        returnstring=" ";
        /*****************************************************/
            //find all the candidate keys of a table consisting of all
            //the attributes with respect to the functional dependencies
        AttributeSet candidateKey = allAttributes.findCandidateKey(minCover);
        returnstring += (candidateKey.toString());
        ck.setText(returnstring);
        returnstring =" ";
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



            //Step 1: already done above
            //Step 2:
           RelationSchema database_3nf_dep_preserving = new RelationSchema();
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

        returnstring+=database_3nf_dep_preserving.toString();
        dpT.setText(returnstring);
        returnstring ="";


            for(Relation r : database_3nf_dep_preserving.getRelations())
                returnstring += (r.toString() + "\n");

        dpT.setText(returnstring);
        returnstring= " ";
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
            RelationSchema database_3nf_lossless_join_dep_preserving = new RelationSchema();
            for(FunctionalDependency fd : minCover.getElements()) {
                Relation table = new Relation(fd);
                database_3nf_lossless_join_dep_preserving.add(table);
            }

            //Step 3: Ensure decomposition contains a key for an imaginary table
            //        consisting of all the attributes
            boolean keyFound = false;
            for (Relation table : database_3nf_lossless_join_dep_preserving.getRelations()){
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
            while((redunantTable = database_3nf_lossless_join_dep_preserving.findRedunantTable()) != null){
                database_3nf_lossless_join_dep_preserving.remove(redunantTable);
                returnstring += ("\nRemoving Redundant table: " + redunantTable);

            }

            for(Relation r : database_3nf_lossless_join_dep_preserving.getRelations())
                returnstring += (r.toString()+"\n");

        lj_dpT.setText(returnstring);
        returnstring = " ";
        }


        public void createDB(View v){
            DatabaseHandler db = new DatabaseHandler(this.getBaseContext(), diagram.getName(), null, 1, relationSchema);
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

    public void setDiagram(ERDiagram d){
            this.diagram = d;
    }


    public void setSchema(RelationSchema schema) {
        this.relationSchema = schema;
    }
}
