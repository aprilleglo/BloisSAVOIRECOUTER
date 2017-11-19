package net.aprille.bloissavoirecouter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import models.OrderedWalkSound;
import models.Sound;
import models.Walk;

public class WalksActivity extends AppCompatActivity {

    String DirectoryFinal;
    File BloisUserDir;
    File BloisSoundDir;
    File BloisDir;
    String BloisUserDirPath;
    String BloisSoundDirPath;
    String thisSoundImageFilePath;
    String BloisSoundWebUrl;

    String thisWalkID;

    Realm realm;
    RealmConfiguration nRealmConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            realm = Realm.getDefaultInstance();
        } catch (IllegalStateException fuckYouTooAndroid) {
            Realm.init(getApplicationContext());
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(config);
            realm = Realm.getDefaultInstance();
        }

        BloisUserDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/users");
        BloisSoundDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/sounds");
        BloisSoundDirPath = BloisSoundDir.toString();
        BloisDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData");

        BloisSoundWebUrl = "http://savoir-ecouter.aprille.net/wp-content/uploads/";


        thisWalkID = "11082017_Aprille";
        Walk thisFirstWalk = realm.where(Walk.class).equalTo("walkID", thisWalkID).findFirst();

        if (thisFirstWalk == null) {

       // setupWALK();

        } else {
            Log.e("myApp :: ", "else thisFirstWalk == null " + String.valueOf(thisFirstWalk.getOrderedWalkSounds().size()) );
        }




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.action_plan:
                // User chose the home icon...
                Intent intentPlan = new Intent(getApplicationContext(), PlanActivity.class);
                startActivity(intentPlan);
                return true;

            case R.id.explore_keyword:
                // User chose search by keyword
                Intent intentSearch = new Intent(getApplicationContext(), SearchSoundsActivity.class);
                startActivity(intentSearch);
                return true;

            case R.id.explore_geocoding:
                // User chose the "Favorite" action, mark the current item

                Intent intentGeo = new Intent(getApplicationContext(), AddLocationMapsActivity.class);
                intentGeo.putExtra("placeID", "8FV3H8QQ+7V33");
                startActivity(intentGeo);

                return true;


            case R.id.explore_people:
                // User chose the "Favorite" action, mark the current item

                Intent intentPeople = new Intent(getApplicationContext(), PeopleActivity.class);
                startActivity(intentPeople);
                return true;

            case R.id.action_walks:
                // User chose the "Favorite" action, mark the current item

                Intent intentWalks = new Intent(getApplicationContext(), WalksActivity.class);
                startActivity(intentWalks);
                return true;

            case R.id.action_privacy:
                // User chose the "Favorite" action, mark the current item

                Intent intentPrivacy = new Intent(getApplicationContext(), PrivacyActivity.class);
                startActivity(intentPrivacy);
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }


    }

    public void buttoncClickGoMap (View v) {

        Intent intentGoMap = new Intent(getApplicationContext(), WalkMapsActivity.class);
        intentGoMap.putExtra("walkID", "11082017_Aprille");

        startActivity(intentGoMap);

    }

    private void setupWALK() {


        realm.beginTransaction();
//
        Walk walk1 = realm.createObject(Walk.class, "thisWalkID");
//


        walk1.setWalkName("Sons d'animaux");
        walk1.setWalkDesc("Venez avec moi et découvrir les animaux du Lycee Horticole de Blois");
        walk1.setWalkPhoto("11082017_Aprille.jpg");
        walk1.setWalkPhotoDesc("photo du projet");
        walk1.setAccessiable(true);
        walk1.setLoop(true);
        walk1.setLocalizedMedia(true);
        String orderWalkSoundString = thisWalkID + "_1";
        OrderedWalkSound orderWalkSound1 = realm.createObject(OrderedWalkSound.class, orderWalkSoundString);
        orderWalkSound1.setOrderedWalkSoundID("07112017-183916-2030"); //07112017-183916-2030|Le son de l'eau dans la serre tropicale du lycée horticole
        orderWalkSound1.setOrderedWalkPlaceID("8FV3H88G+CH24"); // 8FV3H88G+CH24|la serre tropicale du Lycée Horticole
        orderWalkSound1.setOrderNumberInWalk(1);

        Sound newOrderSound = realm.where(Sound.class).equalTo("soundID", "07112017-183916-2030").findFirst(); //
        if (newOrderSound  != null ) {
            newOrderSound.getOrderedWalkSounds().add(orderWalkSound1);
        } else {

        }

        walk1.getOrderedWalkSounds().add(orderWalkSound1);


        Log.e("myApp :: ", "Sounds in Walk " + String.valueOf(walk1.getOrderedWalkSounds().size()) );


        realm.commitTransaction();

        realm.beginTransaction();

        orderWalkSoundString = thisWalkID + "_2";
        OrderedWalkSound orderWalkSound2 = realm.createObject(OrderedWalkSound.class, orderWalkSoundString);
        orderWalkSound2.setOrderedWalkSoundID("07112017-183929-5680"); //07112017-183929-5680|nous sommes dans le jardin du Lycée
        orderWalkSound2.setOrderedWalkPlaceID("8FV3H88F+3VH3"); // 8FV3H88F+3VH3|le jardin du Lycée Horticole
        orderWalkSound2.setOrderNumberInWalk(2);

        Sound newOrderSound2 = realm.where(Sound.class).equalTo("soundID", "07112017-183929-5680").findFirst(); //07112017-183929-5680|nous sommes dans le jardin du Lycée
        if (newOrderSound2  != null ) {
            newOrderSound2.getOrderedWalkSounds().add(orderWalkSound2);
        } else {

        }



        Log.e("myApp :: ", "Sounds in Walk " + String.valueOf(walk1.getOrderedWalkSounds().size()) );


        walk1.getOrderedWalkSounds().add(orderWalkSound2);
        Log.e("myApp :: ", "Sounds in Walk " + String.valueOf(walk1.getOrderedWalkSounds().size()) );

        realm.commitTransaction();

        realm.beginTransaction();

        orderWalkSoundString = thisWalkID + "_3";
        OrderedWalkSound orderWalkSound3 = realm.createObject(OrderedWalkSound.class, orderWalkSoundString);
        orderWalkSound3.setOrderedWalkSoundID("19102017-110441-5840"); //19102017-110441-5840|les abeilles bourdonnent autour de la ruche
        orderWalkSound3.setOrderedWalkPlaceID("8FV3H88F+6X5P"); //8FV3H88F+6X5P|les ruches du Lycée Horticole
        orderWalkSound3.setOrderNumberInWalk(3);

        Sound newOrderSound3 = realm.where(Sound.class).equalTo("soundID", "19102017-110441-5840").findFirst(); // 07112017-183922-0830|les pigeons du Lycée Horticole
        if (newOrderSound3  != null ) {
            newOrderSound3.getOrderedWalkSounds().add(orderWalkSound3);
        } else {

        }



        Log.e("myApp :: ", "Sounds in Walk " + String.valueOf(walk1.getOrderedWalkSounds().size()) );


        walk1.getOrderedWalkSounds().add(orderWalkSound3);
        Log.e("myApp :: ", "Sounds in Walk " + String.valueOf(walk1.getOrderedWalkSounds().size()) );

        realm.commitTransaction();

        realm.beginTransaction();

        orderWalkSoundString = thisWalkID + "_4";
        OrderedWalkSound orderWalkSound4 = realm.createObject(OrderedWalkSound.class, orderWalkSoundString);
        orderWalkSound4.setOrderedWalkSoundID("07112017-183841-7350"); //07112017-183841-7350|Expérimentation avec une chanson de baleine
        orderWalkSound4.setOrderedWalkPlaceID("8FV3H87F+FPFP"); // 8FV3H87F+FPFP|le champ expérimental
        orderWalkSound4.setOrderNumberInWalk(4);

        Sound newOrderSound4 = realm.where(Sound.class).equalTo("soundID", "07112017-183841-7350").findFirst(); // 07112017-183922-0830|les pigeons du Lycée Horticole
        if (newOrderSound4 != null ) {
            newOrderSound4.getOrderedWalkSounds().add(orderWalkSound4);
        } else {

        }



        Log.e("myApp :: ", "Sounds in Walk " + String.valueOf(walk1.getOrderedWalkSounds().size()) );


        walk1.getOrderedWalkSounds().add(orderWalkSound4);
        Log.e("myApp :: ", "Sounds in Walk " + String.valueOf(walk1.getOrderedWalkSounds().size()) );

        realm.commitTransaction();

        realm.beginTransaction();

        orderWalkSoundString = thisWalkID + "_5";
        OrderedWalkSound orderWalkSound5 = realm.createObject(OrderedWalkSound.class, orderWalkSoundString);
        orderWalkSound5.setOrderedWalkSoundID("07112017-183929-9270"); //07112017-183929-9270|nous sommes dans le secteur oiseaux
        orderWalkSound5.setOrderedWalkPlaceID("8FV3H88G+5J49"); // 8FV3H88G+5J49|l'animalerie du lycée
        orderWalkSound5.setOrderNumberInWalk(5);

        Sound newOrderSound5 = realm.where(Sound.class).equalTo("soundID", "07112017-183929-9270").findFirst(); // 07112017-183922-0830|les pigeons du Lycée Horticole
        if (newOrderSound5 != null ) {
            newOrderSound5.getOrderedWalkSounds().add(orderWalkSound5);
        } else {

        }



        Log.e("myApp :: ", "Sounds in Walk " + String.valueOf(walk1.getOrderedWalkSounds().size()) );


        walk1.getOrderedWalkSounds().add(orderWalkSound5);
        Log.e("myApp :: ", "Sounds in Walk " + String.valueOf(walk1.getOrderedWalkSounds().size()) );

        realm.commitTransaction();


    }


}
