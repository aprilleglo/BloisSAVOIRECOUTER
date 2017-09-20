package net.aprille.bloissavoirecouter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class LegendeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legende);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void buttonClickGeoCodedPlan (View v)
    {

        Intent intent = new Intent(getApplicationContext(), AddLocationMapsActivity.class);
        intent.putExtra("placeID", "8FV3H8QQ+7V33");
        startActivity(intent);
    }



    public void buttonClickExploreParcours(View v)
    {
        Intent intent = new Intent(getApplicationContext(), PlaceSoundsActivity.class);
        intent.putExtra("locationID", "8FV3H8Q7+8CR9");
        startActivity(intent);
    }

    public void buttonClickExploreKeyword(View v)
    {
        Intent intent = new Intent(getApplicationContext(), PlaceSoundsActivity.class);
        intent.putExtra("locationID", "8FV3H8Q7+8CR9");
        startActivity(intent);
    }

    public void buttonClickAddKeyword(View v)
    {
        Intent intent = new Intent(getApplicationContext(), PlaceSoundsActivity.class);
        intent.putExtra("locationID", "8FV3H8Q7+8CR9");
        startActivity(intent);
    }
}
