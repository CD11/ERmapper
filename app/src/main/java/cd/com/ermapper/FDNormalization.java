package cd.com.ermapper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;


public class FDNormalization extends AppCompatActivity {


    //list of functional dependencies
    ArrayList<FunctionalDependency> functionalDependencies = new ArrayList<>();
    ArrayList<FunctionalDependency> fdList = new ArrayList<>();
    ArrayList<Attribute> attributes = new ArrayList<>();
    ArrayList<Entity> entities = new ArrayList<>();
    ArrayList<Relation> relations = new ArrayList<>();

    private FunctionalDependency selectedDependency;
    ListView functionalDependenciesView = (ListView) findViewById(R.id.ResultsList);
    ListView fdListView = (ListView) findViewById(R.id.FDList);
    ListView attributeListView = (ListView) findViewById(R.id.AttributeList);
    ArrayAdapter<Attribute> attrAdapter;
    ArrayAdapter<FunctionalDependency> resultsAdapter;
    ArrayAdapter<FunctionalDependency> fdListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fdnormalization);

        ERDiagram diagram = (ERDiagram) this.getIntent().getSerializableExtra("diagram");
        entities = diagram.getEntities();
        attributes = diagram.getAttributes();

        for(Entity e: entities){
            relations.add(new Relation(e.getAttr(), e.getPrimary()));
            FunctionalDependency fd = new FunctionalDependency(e.getPrimary(), e.getAttr());
            functionalDependencies.add(fd);
            performAttributeClosure(e.getAttr());
        }

        attrAdapter= new ArrayAdapter<Attribute>(this, android.R.layout.simple_list_item_1, attributes);
        attributeListView.setAdapter(attrAdapter);

        fdListAdapter= new ArrayAdapter<FunctionalDependency>(this, android.R.layout.simple_list_item_1, fdList);
        fdListView.setAdapter(attrAdapter);


        resultsAdapter = new ArrayAdapter<FunctionalDependency>(this, android.R.layout.simple_list_item_1, functionalDependencies);
        functionalDependenciesView.setAdapter(attrAdapter);


        performNormalization();



    }
    private void addFD(FunctionalDependency anFD){
        functionalDependencies.add(anFD);
        rebuiltAttributesList();
    }
    private void rebuiltAttributesList(){
        //Rebuild the attributes list
        attributes.clear();
        DependencySet FDs = new DependencySet();
        //Gather all the functional dependencies
        for(FunctionalDependency fd : functionalDependencies){FDs.add(fd);}
        AttributeSet allAttributes = FDs.getAllAttributes();
       // Collections.sort(allAttributes.getElements());
        for (Attribute a : allAttributes.getElements()) attributes.add(a);
    }

    private void performAttributeClosure(AttributeSet leftAttributes){
        //consoleTextArea.clear(); //clear the console of previous results
        DependencySet FDs = new DependencySet();
        //Gather all the functional dependencies
        for(FunctionalDependency fd : functionalDependencies){FDs.add(fd);}
        //print all the attributes
        AttributeSet allAttributes = FDs.getAllAttributes();
      //  Collections.sort(allAttributes.getElements());
        //allAttributes.printToSystemOut();

        //print all the attributes
        System.out.print("ATTRIBUTES:");
        for(Attribute att : allAttributes.getElements())
            System.out.print(att.toString());
        System.out.print("==================================================");


        //print all the functional dependencies created from data file
        System.out.print("FUNCTIONAL DEPENDENCIES:");
        System.out.print("==================================================");

        System.out.print("ATTRIBUTE CLOSURE:");
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

      //  consoleTextArea.clear(); //clear the console of previous results
        DependencySet FDs = new DependencySet();
        //Gather all the functional dependencies
        for(FunctionalDependency fd : functionalDependencies){FDs.add(fd);}
        //print all the attributes
        AttributeSet allAttributes = FDs.getAllAttributes();
        //Collections.sort(allAttributes.getElements());
        //allAttributes.printToSystemOut();

        //print all the attributes
        System.out.print("ATTRIBUTES:");
        for(Attribute att : allAttributes.getElements())
        System.out.print(att.toString());
        System.out.print("==================================================");


        //print all the functional dependencies created from data file
        System.out.print("FUNCTIONAL DEPENDENCIES:");
        System.out.print(FDs.toString());
        System.out.print("==================================================");

        DependencySet minCover = FDs.minCover();

        System.out.print("==================================================");
        System.out.print("Minimal Cover:");
        System.out.print( minCover.toString());

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
        System.out.print("============================");
        System.out.print("MINIMAL COVER: MERGED LHS");

        System.out.print(minCover.toString());
        //check that minimal cover and original FD's are in fact equivalent

        if(FDs.equals(minCover))
            System.out.print("FD Sets are Equivalent");
        else
            System.out.print("FD Sets are NOT Equivalent");


            //find all the candidate keys of a table consisting of all
            //the attributes with respect to the functional dependencies
        System.out.print("\n-------------------------------------------------------------");
        System.out.print("CANDIDATE KEY FOR ALL ATTRIBUTES:");
            AttributeSet candidateKey = allAttributes.findCandidateKey(minCover);
        System.out.print(candidateKey.toString());

        System.out.print("\n-------------------------------------------------------------");
        System.out.print("ALL CANDIDATE KEYS (FOR SMALL EXAMPLES ONLY):");

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


             System.out.print("\n=======================================================");
             System.out.print("Dependency Preserving, 3NF tables");

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
                Relation tableOfLeftOverAttributes = new Relation(leftOverAttributes,leftOverAttributes);
                database_3nf_dep_preserving.add(tableOfLeftOverAttributes);
            }

            for(Relation r : database_3nf_dep_preserving)
                 System.out.print(r.toString());

             System.out.print("\n=======================================================");
             System.out.print("Lossless-Join, Dependency Preserving, 3NF tables");

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
                database_3nf_lossless_join_dep_preserving.add(new Relation(candidateKey,candidateKey));

            //Step 4: Remove any redundant tables
            //A table is redundant if all of its attributes appears in some other table.

            Relation redunantTable = null;
            while((redunantTable = Normalizer.findRedunantTable(database_3nf_lossless_join_dep_preserving)) != null){
                database_3nf_lossless_join_dep_preserving.remove(redunantTable);
                System.out.print("\nRemoving Redundant table: " + redunantTable);

            }

            for(Relation r : database_3nf_lossless_join_dep_preserving)
                System.out.print(r.toString());

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