package cd.com.ermapper;

import android.app.Application;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;




    /**
     * Created by ldnel_000 on 2015-07-27.
     */
    public class FXApplicationMain extends AppCompatActivity {

        /*
         * Database Normalizer
         * copyright Louis D. Nel 2013-2015.
         *
         * This code is for classroom illustration in my COMP 3005 Database course
         * at Carleton University.
         *
         * It has not been made robust against input file format errors and may have
         * intentional omissions for illustration purposes. That said, please report any
         * bugs you find to me: ldnel@scs.carleton.ca
         *
         * This programs analyses a set of functional dependencies provided in a
         * data file and determines the following:
         *
         * -A minimal cover for the set of dependencies.
         *
         * -A set of candidate keys for an imaginary table consisting of all the attributes
         * with respect to the functional dependences.
         *
         * -A dependency preserving 3NF decomposition of a table consisting of
         * all the original attributes
         *
         * -A lossless-join, dependency preserving, 3NF decomposition with redundant
         * tables removed.
         *
         *
         */

        /*
        //GUI menus
        MenuBar menubar = new MenuBar();
        Menu fileMenu = new Menu("File");

        //GUI list, text fields, labels, and buttons
        Label wordLabel = new Label("  FD:");
        TextField wordTextField = new TextField();
        Button enterButton = new Button("Add FD");
        Button deleteButton = new Button("Delete");
        Button normalizeButton = new Button("Normalize");
        Button closureButton = new Button("{A,B,C}+");

        //list of functional dependencies
        private FunctionalDependency selectedDependency;
        private ObservableList<FunctionalDependency> functionalDependencies = FXCollections.observableArrayList();
        Label fdListLabel = new Label("Functional Dependencies:");
        public static TextArea consoleTextArea;
        Label outputTextAreaLabel = new Label("3NF Normalization Results:");
        ListView<FunctionalDependency> fdList = new ListView<FunctionalDependency>();

        //list of functional dependencies
        private ObservableList<Attribute> attributes = FXCollections.observableArrayList();
        Label attributeListLabel = new Label("Attributes:");
        ListView<Attribute> attributeList = new ListView<Attribute>();

        public static void printlnToConsole(String s){
            consoleTextArea.appendText(s + "\n");
        }

        private void buildMenus(Stage theStage){
            //build the menus for the menu bar

            //Build File menu items
            MenuItem aboutMenuItem = new MenuItem("About This App");
            fileMenu.getItems().addAll(aboutMenuItem);
            aboutMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Database Normalizer");
                    alert.setHeaderText(null);
                    alert.setContentText("Ver 1.0 \u00A9 L.D. Nel 2015\nldnel@scs.carleton.ca");
                    alert.showAndWait();
                }
            });

            fileMenu.getItems().addAll(new SeparatorMenuItem());

            MenuItem newMenuItem = new MenuItem("New");
            fileMenu.getItems().addAll(newMenuItem);
            newMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    functionalDependencies.clear();
                    attributes.clear();
                    selectedDependency = null;
                    consoleTextArea.clear();
                }
            });

            MenuItem openMenuItem = new MenuItem("Open");
            fileMenu.getItems().addAll(openMenuItem);
            openMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    System.out.println("openFile()");
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Open File");
                    String currentDirectoryProperty = System.getProperty("user.dir");
                    File currentDirectory = new File(currentDirectoryProperty);
                    fileChooser.setInitialDirectory(currentDirectory);
                    File selectedFile = fileChooser.showOpenDialog(theStage);
                    System.out.println("opened file: " + selectedFile);
                    if (selectedFile != null) openFile(selectedFile);
                }
            });

            MenuItem saveMenuItem = new MenuItem("Save As");
            fileMenu.getItems().addAll(saveMenuItem);
            saveMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {

                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save As");
                    String currentDirectoryProperty = System.getProperty("user.dir");
                    File currentDirectory = new File(currentDirectoryProperty);
                    fileChooser.setInitialDirectory(currentDirectory);
                    File selectedFile = fileChooser.showSaveDialog(theStage);


                    System.out.println("save to file: " + selectedFile);
                    saveFile(selectedFile);

                }
            });

            fileMenu.getItems().addAll(new SeparatorMenuItem());
        }

        //required by any Application subclass
        @Override
        public void start(Stage mainStage){

            //Here we do most of the initialization for the application

            mainStage.setTitle("Database Normalizer");

            VBox root = new VBox(); //root group node of scene graph (vertical layout box)
            Scene theScene = new Scene(root); //our GUI scene
            mainStage.setScene(theScene); //add scene to our app's stage

            //build application menus
            //add menus to menu bar object
            menubar.getMenus().add(fileMenu);

            //add menu bar object to application scene root
            root.getChildren().add(menubar); //add menubar to GUI
            buildMenus(mainStage); //add menu items to menus

            consoleTextArea = new TextArea();

            //create canvas the size of background image
            HBox listBox = new HBox();
            VBox fdListVBox = new VBox();
            root.getChildren().addAll(listBox);

            //put some words in the items list
            VBox attributeListVBox = new VBox();
            listBox.getChildren().addAll(attributeListVBox);
            attributeListVBox.setPrefWidth(200);
            attributeListVBox.setPrefHeight(300);
            attributeList.setItems(attributes); //put items into GUI list
            attributeListVBox.getChildren().addAll(attributeListLabel, attributeList);
            //add selection listener to list
            attributeList.getSelectionModel().selectedItemProperty()
                    .addListener(new ChangeListener<Attribute>() {
                        public void changed(ObservableValue<? extends Attribute> observable,
                                            Attribute oldValue, Attribute newValue) {
                            if(newValue != null) {
                                String wordText = wordTextField.getText().trim();
                                if(!wordText.isEmpty()) wordTextField.appendText(",");
                                wordTextField.appendText(newValue.toString().trim());
                                //System.out.println("selection changed");
                            }
                        }
                    });

            listBox.getChildren().addAll(fdListVBox);
            //put some words in the items list
            fdList.setItems(functionalDependencies); //put items into GUI list
            fdListVBox.setPrefWidth(600);
            fdListVBox.setPrefHeight(300);
            fdListVBox.getChildren().addAll(fdListLabel, fdList);
            //add selection listener to list
            fdList.getSelectionModel().selectedItemProperty()
                    .addListener(new ChangeListener<FunctionalDependency>() {
                        public void changed(ObservableValue<? extends FunctionalDependency> observable,
                                            FunctionalDependency oldValue, FunctionalDependency newValue) {
                            System.out.println("selection changed");
                            selectedDependency = newValue;
                            if(selectedDependency != null)
                                wordTextField.setText(selectedDependency.toString());
                        }
                    });

            fdList.setOnKeyPressed(new EventHandler<KeyEvent>() {
                public void handle(KeyEvent ke) {
                    //listen for arrow keys using key press event
                    //arrow keys don't show up in KeyTyped events
                    String text = "";
                    if (ke.getCode() == KeyCode.RIGHT) text += "RIGHT";
                    else if (ke.getCode() == KeyCode.LEFT) text += "LEFT";
                    else if (ke.getCode() == KeyCode.UP) text += "UP";
                    else if (ke.getCode() == KeyCode.DOWN) text += "DOWN";
                    else if (ke.getCode() == KeyCode.DELETE) {
                        text += "DELETE";
                        if (selectedDependency == null) return;
                        functionalDependencies.remove(selectedDependency);
                        selectedDependency = fdList.getSelectionModel().getSelectedItem();
                        if(selectedDependency != null) wordTextField.setText(selectedDependency.toString());
                        if(!functionalDependencies.contains(selectedDependency)) wordTextField.clear();
                        rebuiltAttributesList();
                    }
                    else text += ke.getCharacter();

                    System.out.println("key press: " + text);

                    ke.consume(); //don't let keyboard event propogate
                }
            });

            //Add text area
            consoleTextArea.setPrefHeight(300);
            root.getChildren().addAll(outputTextAreaLabel, consoleTextArea);

            //create text entry field and enter button
            HBox wordEntryBox = new HBox(); //horizontal layout box
            wordEntryBox.setSpacing(20); //space elements
            wordEntryBox.setAlignment(Pos.TOP_LEFT);
            wordTextField.setPrefWidth(450);
            wordEntryBox.getChildren().addAll(wordLabel, wordTextField, enterButton, deleteButton, normalizeButton, closureButton);
            root.getChildren().addAll(wordEntryBox);

            wordTextField.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    String anFDString = wordTextField.getText().trim();
                    if (anFDString != null && anFDString.length() > 0) {
                        FunctionalDependency fd = Normalizer.parseFDString(anFDString);
                        if (fd != null) addFD(fd);
                    }
                    wordTextField.clear(); //clear the text field
                }
            });

            enterButton.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    String anFDString = wordTextField.getText().trim();
                    if (anFDString != null && anFDString.length() > 0) {
                        FunctionalDependency fd = Normalizer.parseFDString(anFDString);
                        if (fd != null) addFD(fd);
                    }
                    wordTextField.clear(); //clear the text field
                }
            });

            deleteButton.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    if (selectedDependency == null) return;
                    functionalDependencies.remove(selectedDependency);
                    selectedDependency = fdList.getSelectionModel().getSelectedItem();
                    if(selectedDependency != null) wordTextField.setText(selectedDependency.toString());
                    if(!functionalDependencies.contains(selectedDependency)) wordTextField.clear();
                    rebuiltAttributesList();

                    //wordTextField.clear(); //clear the text field
                }
            });

            normalizeButton.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    performNormalization();
                }
            });
            closureButton.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    String attributesString = wordTextField.getText().trim();
                    if (attributesString != null && attributesString.length() > 0) {
                        String[] attributes = attributesString.split(",");
                        AttributeSet leftAttributes = new AttributeSet();
                        for (String s : attributes) leftAttributes.add(new Attribute(s.trim()));
                        performAttributeClosure(leftAttributes);

                    }
                    wordTextField.clear(); //clear the text field
                }
            });

            mainStage.show(); //show the application window
        }

        private void addFD(FunctionalDependency anFD){
            functionalDependencies.addAll(anFD);
            rebuiltAttributesList();
        }
        private void rebuiltAttributesList(){
            //Rebuild the attributes list
            attributes.clear();
            DependencySet FDs = new DependencySet();
            //Gather all the functional dependencies
            for(FunctionalDependency fd : functionalDependencies){FDs.add(fd);}
            AttributeSet allAttributes = FDs.getAllAttributes();
            Collections.sort(allAttributes.getElements());
            for (Attribute a : allAttributes.getElements()) attributes.add(a);
        }

        private void performAttributeClosure(AttributeSet leftAttributes){
            consoleTextArea.clear(); //clear the console of previous results
            DependencySet FDs = new DependencySet();
            //Gather all the functional dependencies
            for(FunctionalDependency fd : functionalDependencies){FDs.add(fd);}
            //print all the attributes
            AttributeSet allAttributes = FDs.getAllAttributes();
            Collections.sort(allAttributes.getElements());
            //allAttributes.printToSystemOut();

            //print all the attributes
            printlnToConsole("ATTRIBUTES:");
            for(Attribute att : allAttributes.getElements())
                printlnToConsole(att.toString());
            printlnToConsole("==================================================");


            //print all the functional dependencies created from data file
            printlnToConsole("FUNCTIONAL DEPENDENCIES:");
            FDs.printToTextArea(consoleTextArea);
            printlnToConsole("==================================================");

            printlnToConsole("ATTRIBUTE CLOSURE:");
            printlnToConsole("CLOSURE {" + leftAttributes + "}+");
            AttributeSet closureSet = leftAttributes.closure(FDs);
            printlnToConsole(closureSet.toString());
            if(closureSet.containsAll(allAttributes)){
                printlnToConsole("{" + leftAttributes + "} is a SUPERKEY");
                boolean isMinimal = true;
                for(Attribute a : leftAttributes.getElements()){
                    AttributeSet attributesCopy = new AttributeSet(leftAttributes);
                    attributesCopy.remove(a);
                    AttributeSet c = attributesCopy.closure(FDs);
                    if(c.containsAll(allAttributes)) isMinimal = false;
                }
                if(isMinimal)
                    printlnToConsole("{" + leftAttributes + "} is MINIMAL (i.e. is CANDIDATE KEY)");
                else
                    printlnToConsole("{" + leftAttributes + "} is NOT MINIMAL");
            }

        }

        private void performNormalization(){

            consoleTextArea.clear(); //clear the console of previous results
            DependencySet FDs = new DependencySet();
            //Gather all the functional dependencies
            for(FunctionalDependency fd : functionalDependencies){FDs.add(fd);}
            //print all the attributes
            AttributeSet allAttributes = FDs.getAllAttributes();
            Collections.sort(allAttributes.getElements());
            //allAttributes.printToSystemOut();

            //print all the attributes
            printlnToConsole("ATTRIBUTES:");
            for(Attribute att : allAttributes.getElements())
                printlnToConsole(att.toString());
            printlnToConsole("==================================================");


            //print all the functional dependencies created from data file
            printlnToConsole("FUNCTIONAL DEPENDENCIES:");
            FDs.printToTextArea(consoleTextArea);
            printlnToConsole("==================================================");

            DependencySet minCover = FDs.minCover();

            printlnToConsole("==================================================");
            printlnToConsole("Minimal Cover:");
            minCover.printToTextArea(consoleTextArea);

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
            printlnToConsole("============================");
            printlnToConsole("MINIMAL COVER: MERGED LHS");

            minCover.printToTextArea(consoleTextArea);
*/
            //check that minimal cover and original FD's are in fact equivalent
        /*
        if(FDs.equals(minCover))
            printlnToConsole("FD Sets are Equivalent");
        else
            printlnToConsole("FD Sets are NOT Equivalent");
        */

         /*   //find all the candidate keys of a table consisting of all
            //the attributes with respect to the functional dependencies
            printlnToConsole("\n-------------------------------------------------------------");
            printlnToConsole("CANDIDATE KEY FOR ALL ATTRIBUTES:");
            AttributeSet candidateKey = allAttributes.findCandidateKey(minCover);
            printlnToConsole(candidateKey.toString());

            printlnToConsole("\n-------------------------------------------------------------");
            printlnToConsole("ALL CANDIDATE KEYS (FOR SMALL EXAMPLES ONLY):");

            SetOfAttributeSets candidateKeys = allAttributes.allCandidateKeys(minCover);
            if(candidateKeys != null)
                for (AttributeSet aKey : candidateKeys.getElements())
                    printlnToConsole(aKey.toString());
*/
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

		/*
            printlnToConsole("\n=======================================================");
            printlnToConsole("Dependency Preserving, 3NF tables");

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
                printlnToConsole(r.toString());

            printlnToConsole("\n=======================================================");
            printlnToConsole("Lossless-Join, Dependency Preserving, 3NF tables");
*/
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
/*

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
                printlnToConsole("\nRemoving Redundant table: " + redunantTable);

            }

            for(Relation r : database_3nf_lossless_join_dep_preserving)
                printlnToConsole(r.toString());

        }

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

            consoleTextArea.clear();

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


        public static void main(String[] args) {
            //entry point for javaFX application
            System.out.println("starting main application");
            launch(args); //will cause application's to start and
            // run it's start() method
            System.out.println("main application is finished");
        }
    }
*/
}
