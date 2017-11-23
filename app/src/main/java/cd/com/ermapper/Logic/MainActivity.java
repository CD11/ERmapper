package cd.com.ermapper.Logic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cd.com.ermapper.R;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void newER(View view){

        Intent intent = new Intent (MainActivity.this, ERDraw.class);
        ERDiagram diagram = new ERDiagram("erDiagram");
        intent.putExtra("diagram", diagram);
        startActivity(intent);
    }
}
