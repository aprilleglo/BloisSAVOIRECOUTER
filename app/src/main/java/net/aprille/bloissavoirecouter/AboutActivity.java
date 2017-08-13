package net.aprille.bloissavoirecouter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Email = new Intent(Intent.ACTION_SEND);
                Email.setType("text/email");
                Email.putExtra(Intent.EXTRA_EMAIL,
                        new String[]{"aprile@aprille.net"});  //developer 's email
                Email.putExtra(Intent.EXTRA_SUBJECT,
                        "Subjet"); // Email 's Subject
                Email.putExtra(Intent.EXTRA_TEXT, "Bonjour Aprille Glover" + "");  //Email 's Greeting text
                startActivity(Intent.createChooser(Email, "Send Feedback:"));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
