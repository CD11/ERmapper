package cd.com.ermapper.Logic;

import android.content.Context;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import cd.com.ermapper.relations.DependencySet;
import cd.com.ermapper.relations.FunctionalDependency;
import cd.com.ermapper.shapes.Attribute;
import cd.com.ermapper.shapes.Entity;
import cd.com.ermapper.shapes.Relationship;

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
    private void saveFile(DependencySet functionalDependencies, String name){
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

        } catch (IOException e) {
            System.out.println("Error: Cannot write to file: " + outputFileStream);

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
            for (Relationship o: diagram.getRelationships()) {
                serializer.startTag(null, "Relationship");

                for (Entity e : o.getObjs().getElements()) {
                    serializer.startTag("", "Entity");
                    entityToXML(e, serializer);
                    serializer.endTag("", "Entity");
                }

                if (!o.getAttrs().isEmpty()) {
                    serializer.startTag("", "Attributes");
                    for (Attribute a : o.getAttrs().getElements()) {
                        attributeToXML(a, serializer);
                    }
                    serializer.endTag(" ", "Attributes");

                }
                serializer.endTag(null, "Relationship");
            }

            serializer.endTag("", "ERDiagram");
        serializer.endDocument();

        serializer.flush();
        xml = serializer.toString();
        fos.close();

        } catch (IOException e)  {
            e.printStackTrace();
            result = false;
        }


    try {
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
      //      DOMSource source = new DOMSource(xml);
        //    StreamResult r = new StreamResult(f);
       //     tf.transform(source,r);
    }catch(TransformerException e){
        e.printStackTrace();
    }

        return result;
    }



    public void attributeToXML(Attribute a, XmlSerializer serializer) {
        try {
            serializer.attribute("", "name", a.getName());
            serializer.attribute("", "coordinates", a.getCoordinates().toString());
            if (a.isPrimary())
                serializer.attribute("", "primary", "true");
            if (a.isForeign())
                serializer.attribute("", "foriegn", "true");
            if (!a.getValues().isEmpty()) {
                serializer.startTag(" ", "multiAttribute");
                for (Attribute subA : a.getValues()) {
                    attributeToXML(subA, serializer);
                }
                serializer.endTag("", "multiAttribyte");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void entityToXML(Entity e, XmlSerializer serializer){
        try {
            if (!e.isWeak())
                serializer.attribute("", "Name", e.getName());
                serializer.attribute("", "coordinates", e.getCoordinates().toString());

            if(!e.getAttr().isEmpty()){
                serializer.startTag("", "Attributes");
                for (Attribute a : e.getAttr().getElements()) {
                    attributeToXML(a, serializer);
                }
                serializer.endTag(" ", "Attributes");
            }
            if(!e.getWeak().isEmpty()) {
                    serializer.startTag(" ", "weakEntites");
                    for (Entity subE : e.getWeak()) {
                        entityToXML(subE, serializer);
                    }
                    serializer.endTag("","weakEntities");
                }    ;

        }catch (IOException exception){
            exception.printStackTrace();
        }


    }
}
