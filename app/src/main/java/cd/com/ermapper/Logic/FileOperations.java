package cd.com.ermapper.Logic;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import cd.com.ermapper.Components.DependencySet;
import cd.com.ermapper.Components.FunctionalDependency;
import cd.com.ermapper.Components.ShapeObject;

/**
 * Created by CD on 11/15/2017.
 */

public class FileOperations {


    public FileOperations(){}
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
*/
    private void saveFile(DependencySet functionalDependencies, String name) throws IOException {
        System.out.println("saveFile()");
        File file  = new File("files/"+ name+".xml");
        if(file == null) return;
        //save the chartModel to disk
        PrintWriter outputFileStream = null;

        try{
            outputFileStream = new PrintWriter(new FileWriter(file));
            for(FunctionalDependency fd : functionalDependencies.getElements()){
                outputFileStream.println(fd);
            }
            outputFileStream.close();

        } catch (FileNotFoundException e) {
            System.out.println("Error: Cannot open file" + outputFileStream + " for writing.");
            throw e;

        } catch (IOException e) {
            System.out.println("Error: Cannot write to file: " + outputFileStream);
            throw e;
        }
    }


    ///////////// Create an XML File to Store ERDiagram information /////////////////////
    /*
        The XML file will store each entity object will its corresponding objs
        <ERDiagram>
            <Entity>
                <coordinates>
                <attributes>
                    <aCoordinates>
                            .....
                <weak>
       </>
     */
    public boolean SaveDiagram(ERDiagram diagram, Context context) throws IOException {
            boolean result = true;
        File f = new File(context.getFilesDir(), diagram.getName()+".xml");
        String xml = "";
        FileOutputStream fos = context.openFileOutput(f.getName().toString(), Context.MODE_PRIVATE);

        try {
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument(null, Boolean.valueOf(true));
            serializer.startTag(null, "ERdiagram");
            serializer.text(diagram.getName());
            for(ShapeObject o : diagram.getObjects()){
                o.shapeToXML(serializer);
            }
            serializer.endTag(null, "ERdiagram");
            serializer.endDocument();
            serializer.flush();
            xml = serializer.toString();
            fos.close();

        } catch (IOException e)  {
            e.printStackTrace();
            throw e;
        }



        return result;
    }

    ///////////// Create an XML File to Store ERDiagram information /////////////////////
    /*
        The XML file will store each entity object will its corresponding objs
        <ERDiagram>
            <Entity>
                <coordinates>
                <attributes>
                    <aCoordinates>
                            .....
                <weak>
       </>
     */




    /* this is from Lou Nels code
    public static FunctionalDependency parseFDString(String inputLine) {
        if (inputLine == null || inputLine.length() == 0) return null;

        //strip off comments
        int commentIndex = inputLine.indexOf("//");
        if (commentIndex > -1) inputLine = inputLine.substring(0, commentIndex).trim();

        if (inputLine.equals("")) return null;

        //Expecting inputLine like name,address -> property1,property2

        int arrowIndex = inputLine.indexOf("->");
        if(arrowIndex == -1) return null; //not valid functional dependency

        String LHS = inputLine.substring(0, arrowIndex).trim();
        String RHS = inputLine.substring(arrowIndex + 2, inputLine.length()).trim();

        //System.out.println(LHS + " -> " + RHS);

        String[] LHSAttributes = LHS.split(",");
        String[] RHSAttributes = RHS.split(",");
        AttributeSet LeftAttributes = new AttributeSet();
        AttributeSet RightAttributes = new AttributeSet();


        for (String s : LHSAttributes) {
            if(!s.trim().isEmpty())
                LeftAttributes.add(new Attribute(s.trim()));
        }
        for (String s : RHSAttributes) {
            if(!s.trim().isEmpty())
                RightAttributes.add(new Attribute(s.trim()));
        }

        if (!LeftAttributes.isEmpty() && !RightAttributes.isEmpty()) {
            return new FunctionalDependency(LeftAttributes, RightAttributes);
        }
        else
            return null;
    }
*/
    /*
    public static DependencySet parseInputFile(File inputFile){


		 * Parse the input data file and produce the set of functional dependencies it represents
		 *
		 * Input file is expected to be a text file with one dependency per line.
		 * Attributes are separated by commas
		 * Comments are any content at appears after "//" on a line
		 * Comments will be stripped away in the parse
		 *
		 * Example input file format:
		 *
		 *       //From previous midterm
         *       U,V->W,X,Y,Z  //U,V is superkey
         *       X->W
         *       V->X


        System.out.println("Parse File Data:");

        if(inputFile == null) return null;

        DependencySet aDependencySet = new DependencySet();

        BufferedReader inputFileReader;

        String inputLine; //current input line
        try{
            inputFileReader= new BufferedReader(new FileReader(inputFile));

            while((inputLine = inputFileReader.readLine()) != null){

                System.out.println(inputLine);
                FunctionalDependency fd = parseFDString(inputLine);
                if(fd != null) aDependencySet.add(fd);

            } //end while


        }catch (EOFException e) {
            System.out.println("File Read Error: EOF encountered, file may be corrupted.");
        } catch (IOException e) {
            System.out.println("File Read Error: Cannot read from file.");
        }


        System.out.println("END Data Parse");

        return aDependencySet;

    }
*/






}
