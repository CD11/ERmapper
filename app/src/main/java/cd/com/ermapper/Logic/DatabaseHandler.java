package cd.com.ermapper.Logic;

/**
 * Created by CD on 11/15/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
        for(Relation r : this.relations.getRelations()) {
            String tablename = r.getName();
            String type = "Text";
            String columns = toString(r);
            String createtable = "CREATE TABLE " + tablename + " " + columns;
            db.execSQL(createtable);
        }
           /* "( ";

            for (Attribute a : r.getPrimaryKey().getElements()) {
                columns +=   a.getName() +" "  + type + " PRIMARY KEY , ";
            }
            for (Attribute a : r.getAttributes().getElements()) {
                if(!a.isPrimary()&& !r.getPrimaryKey().contains(a))
                   columns +=  a.getName()+ " "+ type + " , ";
            }
            columns = columns.substring(0, columns.length() - 2); // strip off last comma

            columns += " )";

            */
            ;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public String toString(Relation r) {

            String tablename = r.getName();
            String type = "Text";

            String columns = "( ";

            for (Attribute a : r.getPrimaryKey().getElements()) {
                columns += a.getName() + " " + type + " PRIMARY KEY , ";
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

}
