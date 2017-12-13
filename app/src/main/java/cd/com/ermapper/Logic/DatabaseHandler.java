package cd.com.ermapper.Logic;

/**
 * Created by CD on 11/15/2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import cd.com.ermapper.Components.Attribute;
import cd.com.ermapper.Components.Relation;
import cd.com.ermapper.Components.RelationSchema;

public class DatabaseHandler extends SQLiteOpenHelper {

    // DatabaseHandler Version
    private final int DATABASE_VERSION = 1;
    // DatabaseHandler Name
    private String DATABASE_NAME;
    private RelationSchema relations;
    ERDiagram diagram;

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, RelationSchema relations) {
        super(context, name, null, version);
        this.DATABASE_NAME = name;
        this.relations = relations;
    }
    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, version);
        this.DATABASE_NAME = name;
        this.relations = new RelationSchema();
    }

    public String getName() {
        return DATABASE_NAME;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            this.clean(db);// remove old database
        }catch (SQLiteException e){}
        for(Relation r : this.relations.getRelations()) {
            String tablename = r.getName();
            String columns = toString(r, this.relations);

            String createtable = "CREATE TABLE " + tablename + " " + columns;
            db.execSQL(createtable);

            try{
                String query = "SELECT * FROM "+tablename;
                Cursor c = db.rawQuery(query, null);
            }catch(NullPointerException e){
                throw new NullPointerException(tablename+" Was not created");
            }
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String toString(Relation r, RelationSchema schema) {

            String type = "Text";

            String columns = "( ";
            for (Attribute a : r.getPrimaryKey().getElements()) {
               if(a.isPrimary()) {
                   columns += a.getName() + " " + type + " PRIMARY KEY, ";
               }
               else if(a.isForeign()){
                    columns += a.getName() + " " + type + " REFERENCES " + schema.getForeignString(a)+",";
                }
            }
            for (Attribute a : r.getAttributes().getElements()) {
                if (!a.isPrimary() && !r.getPrimaryKey().contains(a))
                    columns += a.getName() + " " + type + " , ";
            }
            columns = columns.substring(0, columns.length() - 2); // strip off last comma

            columns += " )";

        return columns;
    }






    public void setRelations(RelationSchema relations) {
        this.relations = relations;
    }

    public void clean(SQLiteDatabase db) {
       for (Relation r: this.relations.getRelations()) {
           String s = "DROP TABLE " + r.getName() + ";";
           db.execSQL(s);
       }
       }
}
