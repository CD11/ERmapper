package cd.com.ermapper.Components;

import java.util.ArrayList;

/**
 * Created by CD on 11/22/2017.
 */

public class RelationSchema {
    /*
     Holds all relations that are produced by the ER Diagram and represents the database
     */
    // variables
    ArrayList<Relation>  relations;
    DependencySet dependencies;

    // Constructors
    public RelationSchema(){
        relations = new ArrayList<>();
        dependencies = new DependencySet();
    }
    public void add(Relation relation){
        if(!relations.contains(relation))
            this.relations.add(relation);
    }
    public void addAll(ArrayList<Relation> aRelationList){
        for(Relation r: aRelationList)
            add(r);
    }
    public ArrayList<Relation> getRelations(){return relations;}
    public DependencySet getDependencies(){return dependencies;}
    public void addDependencies(DependencySet d){ this.dependencies = d;}
    /*
    takes the ERDiagram  and creates the relational Schema
     */
    public RelationSchema(EntitySet entities, ArrayList<Relationship> relationships){
        this.relations = new ArrayList<>();
        dependencies = new DependencySet();
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
                    if(r.getTextObjs().get(0).getNum().getText().equals("N")) { //  obj1 is S
                        //b. Include as foreign key in S the primary key of the relation T that represents the 1 side of the relationship type
                        ((Entity) r.getObj1()).getAttr().addAll(((Entity) r.getObj2()).foreignAttrs());
                        ((Entity) r.getObj1()).getAttr().addAll((r.getAttrs()));
                    } else {// obj2 is s
                        //b. Include as foreign key in S the primary key of the relation T that represents the 1 side of the relationship type
                        ((Entity) r.getObj2()).getAttr().addAll(((Entity) r.getObj1()).foreignAttrs());
                        ((Entity) r.getObj2()).getAttr().addAll((r.getAttrs()));
                    }

                    ///////////////////  Step 5 //////////////////////////////////////////////
                    //a. For each regular binary M:N relationship type R, create a new relation S to represent R.
                }else if(r.isMToN()){
                    //b. Include as foreign key attributes in S the primary keys of the relations that represent the participating entity types; their combination will form the primary key of S.
                    //c. Also include any simple attributes of the M:N relationship type (or simple components of composite attributes) as attributes of S.
                    Relation newRelation = new Relation((Entity)r.getObj1(), (Entity)r.getObj2());
                    newRelation.getAttributes().addAll(r.getAttrs());
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
                    if(a.isPrimary()){
                        primarkey.addAll(a.getValuesSet());
                    }
                    attributes.add(a);
                    attributes.addAll(a.getValuesSet());
                    tempR = new Relation(attributes, primarkey, a.getName());
                    relations.add(tempR);
                }

            }
        }
    }


    public Relation findRedunantTable(){
        //Find and return any relation within database whose attributes are all contained within another
        //table
        for(Relation r : getRelations()){
            for(Relation r2 : this.getRelations()){
                if(r != r2 && r2.containsAll(r)) return r;

            }
        }
        return null;
    }

    public void removalAllTemp(){
        for(Relation r: relations) {
            r.getPrimaryKey().removeTemp();
            r.getAttributes().removeTemp();
        }
    }

    public void remove(Relation redunantTable) {
        this.getRelations().remove(redunantTable);
    }

    public int size() {
        return this.relations.size();
    }
}
