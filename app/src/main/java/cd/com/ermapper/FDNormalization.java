package cd.com.ermapper;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static cd.com.ermapper.R.string.relationships;


public class FDNormalization extends AppCompatActivity {


    //list of functional dependencies
    private  DependencySet functionalDependencies;
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
        attributesView= (ListView) findViewById(R.id.AttributeList);

        // get the ER diagram
        diagram  = this.getIntent().getParcelableExtra("diagram");
        //relations.addAll(findRelations(diagram.getEntityObj()));  // get all relations
        functionalDependencies.addAll(findDependencies()); // get all functional dependencies

        //TODO: check these methods and decide how they work
         performNormalization();  // Perform normalization
        // performAttributeClosure(relations.get(0).getPrimaryKey()); // perform Attribute Closure

        //attributes = functionalDependencies.getAllAttributes();

    }

    private void addFD(FunctionalDependency anFD){
        functionalDependencies.add(anFD);
        rebuiltAttributesList();
    }


    public DependencySet findDependencies(){
        FunctionalDependency fd;
    /* follows the ER->Relational mappint rules in fundamentals of DB systems by Ramex Elmasri & Shamkant B. Navathe
        1. For all Regular entity types
            a. assign a Relation, pick a primary key
                if the primary key is a complex attribute: all attributes will be included
           b. if  an attributeis complex, create a new relation

     */

        ////////////// Step 1 ///////////////////////////
        // check for multiple attributes.
        for(Entity e: diagram.getEntityObj()){
            for(Attribute a: e.getAttr().getElements()){ // for each attribute a in e
                if(!a.getValues().isEmpty()){            // check if a is complex and create its own relation.
                    fd = a.toFD();
                    if(fd != null || !fd.isTrivial()) functionalDependencies.add(fd);

                }
            }

        }


        ///////////////  Step 2: weak Entities
        for( Entity e: diagram.getEntityObj()) {
            AttributeSet primarkey = new AttributeSet();
            AttributeSet attributes = new AttributeSet();
            // Check weak Entities
            if (!e.equals(null) || !e.getWeak().isEmpty()) {
                /* if entity has weak Entities add the primary key of its strong relation to it */
                for (Entity eW : e.getWeak()) {
                    // add Strong entity primary key as key to weak entity
                    for (Attribute a : e.getAttr().getElements()) {
                        if (a.isPrimary())
                            eW.getAttr().add(a);
                    }
                    // add attributes of weak entites;   weak -> FD
                    for (Attribute a : eW.getAttr().getElements()){
                        if (a.isPrimary()) {
                            primarkey.add(a);
                        }
                        attributes.add(a);
                    }
                    fd = new FunctionalDependency(primarkey, attributes, e.getName());
                    if(fd != null || !fd.isTrivial()) functionalDependencies.add(fd);

                }
            }
            primarkey.clear();
            attributes.clear();
            // get attributes for Strong Entities;  strong -> FD
            for (Attribute a : e.getAttr().getElements()){
                if (a.isPrimary()) {
                    primarkey.add(a);
                }
                attributes.add(a);
            }



            fd = new FunctionalDependency(primarkey, attributes,e.getName());
            if(fd != null || !fd.isTrivial()) functionalDependencies.add(fd);
        }
        return functionalDependencies;
    }

    private void rebuiltAttributesList(){
        //Rebuild the attributes list
        attributes.clear();
        DependencySet FDs = new DependencySet();
        //Gather all the functional dependencies
        for(FunctionalDependency fd : functionalDependencies.getElements()){FDs.add(fd);}
        AttributeSet allAttributes = FDs.getAllAttributes();
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


/*
        private void openFile(File dataFile){
            //System.out.println("openFile()");
            //Parse the data file
            functionalDependencies.clear();
            selectedDependency = null;
            DependencySet FDs = Normalizer.parseInputFile(dataFile);
            for(FunctionalDependency fd : FDs.getElements()){
                functionalDependencies.addAll(fd);
            }

            AttributeSet allAttributes = FDs.getAllAttributes();
            Collections.sort(allAttributes.getElements());
            attributes.clear();
            for(Attribute a : allAttributes.getElements()) attributes.add(a);
            //allAttributes.printToSystemOut();



        }

        private void saveFile(File aFile){
            System.out.println("saveFile()");
            if(aFile == null) return;
            //save the chartModel to disk
            PrintWriter outputFileStream = null;

            try{
                outputFileStream = new PrintWriter(new FileWriter(aFile));
                for(FunctionalDependency fd : functionalDependencies){
                    outputFileStream.println(fd);
                }
                outputFileStream.close();

            } catch (FileNotFoundException e) {
                System.out.println("Error: Cannot open file" + outputFileStream + " for writing.");

            } catch (IOException e) {
                System.out.println("Error: Cannot write to file: " + outputFileStream);

            }
        }

*/
}
