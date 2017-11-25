package cd.com.ermapper.Logic;

/**
 * Created by CD on 11/15/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import cd.com.ermapper.Components.Relation;
import cd.com.ermapper.Components.Attribute;

public class DatabaseHandler extends SQLiteOpenHelper {

    // DatabaseHandler Version
    private final int DATABASE_VERSION = 1;
    // DatabaseHandler Name
    private String DATABASE_NAME;
    private ArrayList<Relation> relations;


    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, ArrayList<Relation> relations) {
        super(context, name, null, version);
        this.DATABASE_NAME = name;
        this.relations = relations;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(Relation r : this.relations) {
            String tablename = r.getName();
            String type = "Text";

            String columns = "( ";

            for (Attribute a : r.getPrimaryKey().getElements()) {
                columns += type + " Primary Key " + a.getName() + ", ";
            }
            for (Attribute a : r.getAttributes().getElements()) {
                columns += type + " " + a.getName() + ", ";
            }

            columns += " )";


            String createtable = "CREATE TABLE" + tablename + " " + columns;
            db.execSQL(createtable);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
