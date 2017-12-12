package cd.com.ermapper.Logic;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
    private ERDiagram diagram;
    private DatabaseHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fdnormalization);
        AlertDialog ad = new AlertDialog.Builder(this).create();
        ad.setTitle("Error");
        diagram = this.getIntent().getParcelableExtra("diagram");
        try {
        EntitySet entities = diagram.relationshipDecomposition(); // simplifies all N-ary Relationships
        ArrayList<Relationship> relationships = diagram.getRelationshipsObjs();
        relationSchema = new RelationSchema(entities, relationships);
        findDependencies(); // find all dependencies for the relationschema
        relationSchema.removalAllTemp();
        performNormalization();  // Perform normalization
        }catch (NullPointerException e){

            ad.setMessage(e.getMessage());
            ad.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            ad.show();
        }catch (Exception e){
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


    public String performAttributeClosure(AttributeSet leftAttributes){
        String returnString = "";
        DependencySet FDs = new DependencySet();
        //Gather all the functional dependencies
        for(FunctionalDependency fd : relationSchema.getDependencies().getElements()){FDs.add(fd);}
        //print all the attributes
        AttributeSet allAttributes = FDs.getAllAttributes();
        //print all the attributes
        for(Attribute att : allAttributes.getElements())
            returnString +=(att.toString());

        //print all the functional dependencies created from data file
        returnString +=("CLOSURE {" + leftAttributes + "}+\n");
        AttributeSet closureSet = leftAttributes.closure(FDs);
        returnString +=(closureSet.toString());
        if(closureSet.containsAll(allAttributes)){
            returnString +=("{" + leftAttributes + "} is a SUPERKEY\n");
            boolean isMinimal = true;
            for(Attribute a : leftAttributes.getElements()){
                AttributeSet attributesCopy = new AttributeSet(leftAttributes);
                attributesCopy.remove(a);
                AttributeSet c = attributesCopy.closure(FDs);
                if(c.containsAll(allAttributes)) isMinimal = false;
            }
            if(isMinimal)
                returnString +=("{" + leftAttributes + "} is MINIMAL (i.e. is CANDIDATE KEY)\n");
            else
                returnString +=("{" + leftAttributes + "} is NOT MINIMAL\n");
        }

        return  returnString;
    }


    public DependencySet minCover(DependencySet FDs) {
        /*
         *  minimal cover of  a set of fuctional dependencies, is a set of fuctional dependencies that
         *   satisfies the property that every dependency is in its closure
         *
         * This is the 3 step algorithm 16.4 presented in
		 * Elmasri and Navathe 6th ed. Which decomposes a set of attributes
		 * (universal relation) with respect to functional dependencies F.
		 *
         */
        DependencySet minCover = FDs.minCover();

        DependencySet toMerge = minCover.copy();
        DependencySet newMinCover = new DependencySet();

        while (!toMerge.isEmpty()) {
            FunctionalDependency fd = toMerge.getElements().get(0);
            toMerge.remove(fd);
            minCover.remove(fd);
            newMinCover.add(fd);
            for (FunctionalDependency fd2 : toMerge.getElements()) {
                if (fd.getLHS().equals(fd2.getLHS())) {
                    fd.getRHS().addAll(fd2.getRHS());
                    minCover.remove(fd2);
                }
            }
            toMerge = minCover.copy();
        }

        minCover = newMinCover;
        return  minCover;
    }






    public RelationSchema DP3NF(DependencySet minCover, AttributeSet allAttributes) {
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
        for (FunctionalDependency fd : minCover.getElements()) {
            Relation table = new Relation(fd);
            database_3nf_dep_preserving.add(table);
        }
        //Step 3:
        AttributeSet minCoverAttributes = minCover.getAllAttributes();
        AttributeSet leftOverAttributes = new AttributeSet();
        for (Attribute a : allAttributes.getElements())
            if (!minCoverAttributes.contains(a)) leftOverAttributes.add(a);
        if (!leftOverAttributes.isEmpty()) {
            Relation tableOfLeftOverAttributes = new Relation(leftOverAttributes, leftOverAttributes, "relation");
            database_3nf_dep_preserving.add(tableOfLeftOverAttributes);
        }


        return database_3nf_dep_preserving;

    }

    public RelationSchema dp_lj(RelationSchema database_3nf_dep_preserving, DependencySet minCover, AttributeSet candidateKey){
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
            database_3nf_lossless_join_dep_preserving.add(new Relation(candidateKey,candidateKey, "Candidate"));

        //Step 4: Remove any redundant tables
        //A table is redundant if all of its attributes appears in some other table.
        Relation redunantTable = null;
        while((redunantTable = database_3nf_lossless_join_dep_preserving.findRedunantTable()) != null){
            database_3nf_lossless_join_dep_preserving.remove(redunantTable);
            //   returnstring += ("\nRemoving Redundant table:\n " + redunantTable);
        }
        return database_3nf_lossless_join_dep_preserving;
    }


    public void performNormalization(){
        DependencySet FDs = new DependencySet();
        String returnString = "";
        TextView attributes = findViewById(R.id.attributes);
        TextView ac = findViewById(R.id.AttributeClosure);
        TextView ck = findViewById(R.id.CandidateKeys);
        TextView fdr = findViewById(R.id.fds);
        TextView mc = findViewById(R.id.minCover);
        TextView dpT = findViewById(R.id.dptables);
        TextView lj_dpt = findViewById(R.id.LJDPtables);


        //functional dependencies
        for(FunctionalDependency fd : relationSchema.getDependencies().getElements()){FDs.add(fd);}
        fdr.setText(FDs.toString());

        //attributes
        AttributeSet allAttributes = FDs.getAllAttributes();
        attributes.setText(allAttributes.toString());


        // Min Cover
        DependencySet minCover = this.minCover(FDs);
        returnString = "";
        returnString += (minCover.toString());
        //check that minimal cover and original FD's are in fact equivalent

        if(FDs.equals(minCover))
            returnString += ("FD Sets are Equivalent");
        else
            returnString += ("FD Sets are NOT Equivalent");
        mc.setText(returnString);
        returnString ="";

        //candidate keys
        AttributeSet candidateKey = allAttributes.findCandidateKey(minCover);
        ck.setText(candidateKey.toString());

        //attribute Closure
        String attributeClosure = performAttributeClosure(candidateKey);
        ac.setText(attributeClosure);

        // Dependendency perserving 3NF table
        RelationSchema dependency_perserving_3NF_table = DP3NF(minCover, allAttributes);
        for(Relation r : dependency_perserving_3NF_table.getRelations())
            returnString += (r.toString()+"\n");

        dpT.setText(returnString);
        returnString ="";

        // LossLess Join Dependendency perserving 3NF table
        dependency_perserving_3NF_table = this.dp_lj(dependency_perserving_3NF_table, minCover, candidateKey);
        for(Relation r : dependency_perserving_3NF_table.getRelations())
            returnString += (r.toString()+"\n");

        lj_dpt.setText(returnString);
    }

    public void createDB(View v){

        AlertDialog ad = new AlertDialog.Builder(this).create();
        ad.setTitle("DB Creation");
        try {
            db = new DatabaseHandler(this.getBaseContext(), diagram.getName(), null, 1, relationSchema);
            db.onCreate(db.getWritableDatabase());
            ad.setMessage(db.getDatabaseName() + " Successfully created");
            ad.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

        }catch(NullPointerException e){
            ad.setMessage("Database Creation Error :"+e.getMessage());
            ad.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
        ad.show();
    }

    public void setDiagram(ERDiagram d){
            this.diagram = d;
    }

    public DatabaseHandler getDB(){return this.db;}
    public void setSchema(RelationSchema schema) {
        this.relationSchema = schema;
    }
}
