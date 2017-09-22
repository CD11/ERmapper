package cd.com.ermapper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void newER(View view){

        Intent intent = new Intent (MainActivity.this, ERDraw.class);
        ERDiagram diagram = new ERDiagram("erDiagram");
        intent.putExtra("diagram", (Serializable) diagram);
        intent.putExtra("Name", diagram.getName());
        intent.putExtra("Entity", diagram.getEntities());
        startActivity(intent);
    }
}
