package net.aprille.bloissavoirecouter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import models.AppSpecificDetails;
import models.Location;
import models.Quadrant;
import models.Sound;
import models.User;

public class PlanActivity extends AppCompatActivity {

    Realm realm;
    RealmResults<Sound> allSounds;
    RealmResults<Location> allPlaces;
    RealmResults<User> allUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        not for production because deletes WHOLE REALM IF PROBLEM !!!!

        try {
            realm = Realm.getDefaultInstance();
        } catch (IllegalStateException fuckYouTooAndroid) {
            Realm.init(getApplicationContext());
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(config);
            realm = Realm.getDefaultInstance();
        }
//        just to get rid of primary user for testing !!!!

//        realm.beginTransaction();
//        AppSpecificDetails deleOobj = realm.where(AppSpecificDetails.class).findFirst();
//        deleOobj.deleteFromRealm();
//        realm.commitTransaction();
//
//        realm.beginTransaction();
//        RealmResults<Sound> deleteAllSounds = realm.where(Sound.class).findAll();
//        deleteAllSounds.deleteAllFromRealm();
//        realm.commitTransaction();
//
//        realm.beginTransaction();
//        RealmResults<Location> deleteAllPlaces = realm.where(Location.class).findAll();
//        deleteAllPlaces.deleteAllFromRealm();
//        realm.commitTransaction();

        initializeSectors();




        boolean isFirstRun = true;

        AppSpecificDetails thisAppDetails = realm.where(AppSpecificDetails.class).findFirst();


        if (thisAppDetails == null) {
            startActivity(new Intent(PlanActivity.this, StartupActivity.class));


        } else {
            Log.w("myApp", ".getPrimaryUserID " + thisAppDetails.getPrimaryUserID());
            Log.w("myApp", "getPrimaryUserName " + thisAppDetails.getPrimaryUserName());

        }
        allSounds = realm.where(Sound.class).findAll();
        Log.e("myApp", "sound total " + String.valueOf(allSounds.size()));

        allPlaces = realm.where(Location.class).findAll();
        Log.e("myApp", "place total " + String.valueOf(allPlaces.size()));

        allUsers = realm.where(User.class).findAll();
        Log.e("myApp", "user total " + String.valueOf(allUsers.size()));




//        FloatingActionButton fab = (FloatingActionButton) findViewById(fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plan2, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
        realm = null;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {

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

    public void buttonClickAbout(View v)
    {
        Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
        startActivity(intent);
    }

    public void buttonClickThanks(View v)
    {
        Intent intent = new Intent(getApplicationContext(), ThanksActivity.class);
        startActivity(intent);
    }

    public void buttonClickPeople(View v)
    {
        Intent intent = new Intent(getApplicationContext(), PeopleActivity.class);
        startActivity(intent);
    }

    public void buttonClickLegende(View v)
    {
        Intent intent = new Intent(getApplicationContext(), LegendeActivity.class);
        startActivity(intent);
    }

    public void buttonClickApp(View v)
    {
        Intent intent = new Intent(getApplicationContext(), AppActivity.class);
        startActivity(intent);
    }

    public void buttonClick1(View v)
    {
        Intent intent = new Intent(getApplicationContext(), SectorSoundsActivity.class);
        intent.putExtra("sectorNum", 1);
        startActivity(intent);
    }

    public void buttonClick2(View v)
    {
        Intent intent = new Intent(getApplicationContext(), SectorSoundsActivity.class);
        intent.putExtra("sectorNum", 2);
        startActivity(intent);
    }
    public void buttonClick3(View v)
    {
        Intent intent = new Intent(getApplicationContext(), SectorSoundsActivity.class);
        intent.putExtra("sectorNum", 3);
        startActivity(intent);
    }

    public void buttonClick4(View v)
    {
        Intent intent = new Intent(getApplicationContext(), SectorSoundsActivity.class);
        intent.putExtra("sectorNum", 4);
        startActivity(intent);
    }
    public void buttonClick5(View v)
    {
        Intent intent = new Intent(getApplicationContext(), SectorSoundsActivity.class);
        intent.putExtra("sectorNum", 5);
        startActivity(intent);
    }

    public void buttonClick6(View v)
    {
        Intent intent = new Intent(getApplicationContext(), SectorSoundsActivity.class);
        intent.putExtra("sectorNum", 6);
        startActivity(intent);
    }

    public void buttonClick7(View v)
    {
        Intent intent = new Intent(getApplicationContext(), SectorSoundsActivity.class);
        intent.putExtra("sectorNum", 7);
        startActivity(intent);
    }

    public void buttonClick8(View v)
    {
        Intent intent = new Intent(getApplicationContext(), SectorSoundsActivity.class);
        intent.putExtra("sectorNum", 8);
        startActivity(intent);
    }

    public void buttonClick9(View v)
    {
        Intent intent = new Intent(getApplicationContext(), SectorSoundsActivity.class);
        intent.putExtra("sectorNum", 9);
        startActivity(intent);
    }

    public void buttonClick10(View v)
    {
        Intent intent = new Intent(getApplicationContext(), SectorSoundsActivity.class);
        intent.putExtra("sectorNum", 10);
        startActivity(intent);
    }

    public void buttonClick11(View v)
    {
        Intent intent = new Intent(getApplicationContext(), SectorSoundsActivity.class);
        intent.putExtra("sectorNum", 11);
        startActivity(intent);
    }

    public void buttonClick12(View v)
    {
        Intent intent = new Intent(getApplicationContext(), SectorSoundsActivity.class);
        intent.putExtra("sectorNum", 12);
        startActivity(intent);
    }

    public void buttonClick13(View v)
    {
        Intent intent = new Intent(getApplicationContext(), SectorSoundsActivity.class);
        intent.putExtra("sectorNum", 13);
        startActivity(intent);
    }
    public void buttonClick14(View v)
    {
        Intent intent = new Intent(getApplicationContext(), SectorSoundsActivity.class);
        intent.putExtra("sectorNum", 14);
        startActivity(intent);
    }

    public void buttonClick15(View v)
    {
        Intent intent = new Intent(getApplicationContext(), SectorSoundsActivity.class);
        intent.putExtra("sectorNum", 15);
        startActivity(intent);
    }

    public void initializeSectors() {

        Quadrant tryQuad  = realm.where(Quadrant.class).findFirst();


        if (tryQuad == null) {

            realm.beginTransaction();
            Quadrant quadrant1 = realm.createObject(Quadrant.class, "1");
            quadrant1.setQuadTitle("Case 1");
            quadrant1.setQuadDesc("zone commercial et industrial");
            quadrant1.setQuadPhoto("plan_1");
            quadrant1.setQuadPhotoDesc("détail du plan de Blois");

            Quadrant quadrant2 = realm.createObject(Quadrant.class, "2");
            quadrant2.setQuadTitle("Case 2");
            quadrant2.setQuadDesc("zone commercial et industrial");
            quadrant2.setQuadPhoto("plan_2");
            quadrant2.setQuadPhotoDesc("détail du plan de Blois");

            Quadrant quadrant3 = realm.createObject(Quadrant.class, "3");
            quadrant3.setQuadTitle("Case 3");
            quadrant3.setQuadDesc("Médiathèque Maurice-Genevoix, Espace Mirabeau");
            quadrant3.setQuadPhoto("plan_3");
            quadrant3.setQuadPhotoDesc("détail du plan de Blois");


            Quadrant quadrant4 = realm.createObject(Quadrant.class, "4");
            quadrant4.setQuadTitle("Case 4");
            quadrant4.setQuadDesc("Maison de Bégon, l'APF 41");
            quadrant4.setQuadPhoto("plan_4");
            quadrant4.setQuadPhotoDesc("détail du plan de Blois");

            Quadrant quadrant5 = realm.createObject(Quadrant.class, "5");
            quadrant5.setQuadTitle("Case 5");
            quadrant5.setQuadDesc("Sector Provinces, Cantre Hôpitalier de Blois");
            quadrant5.setQuadPhoto("plan_5");
            quadrant5.setQuadPhotoDesc("détail du plan de Blois");

            Quadrant quadrant6 = realm.createObject(Quadrant.class, "6");
            quadrant6.setQuadTitle("Case 6");
            quadrant6.setQuadDesc("Fôret de Blois, chêne « les jumelles » ");
            quadrant6.setQuadPhoto("plan_6");
            quadrant6.setQuadPhotoDesc("détail du plan de Blois");

            Quadrant quadrant7 = realm.createObject(Quadrant.class, "7");
            quadrant7.setQuadTitle("Case 7");
            quadrant7.setQuadDesc("Sector Quinière");
            quadrant7.setQuadPhoto("plan_7");
            quadrant7.setQuadPhotoDesc("détail du plan de Blois");

            Quadrant quadrant8 = realm.createObject(Quadrant.class, "8");
            quadrant8.setQuadTitle("Case 8");
            quadrant8.setQuadDesc("Sector Centre Ville, Château royal de Blois, Maison de la magie, et La gare");
            quadrant8.setQuadPhoto("plan_8");
            quadrant8.setQuadPhotoDesc("détail du plan de Blois");

            Quadrant quadrant9 = realm.createObject(Quadrant.class, "9");
            quadrant9.setQuadTitle("Case 9");
            quadrant9.setQuadDesc("Sector Vienne, Port de la Creusille, et l'hôtel de ville");
            quadrant8.setQuadPhoto("plan_9");
            quadrant8.setQuadPhotoDesc("détail du plan de Blois");

            Quadrant quadrant10 = realm.createObject(Quadrant.class, "10");
            quadrant10.setQuadTitle("Case 10");
            quadrant10.setQuadDesc("Fôret de Blois");
            quadrant10.setQuadPhoto("plan_10");
            quadrant10.setQuadPhotoDesc("détail du plan de Blois");


            Quadrant quadrant11 = realm.createObject(Quadrant.class, "11");
            quadrant11.setQuadTitle("Case 11");
            quadrant11.setQuadDesc("Sector Les Granges");
            quadrant11.setQuadPhoto("plan_11");
            quadrant11.setQuadPhotoDesc("détail du plan de Blois");

            Quadrant quadrant12 = realm.createObject(Quadrant.class, "12");
            quadrant12.setQuadTitle("Case 12");
            quadrant12.setQuadDesc("quartier de Bas Riveiere, Lycée horticole de Blois");
            quadrant12.setQuadPhoto("plan_12");
            quadrant12.setQuadPhotoDesc("détail du plan de Blois");

            Quadrant quadrant13 = realm.createObject(Quadrant.class, "13");
            quadrant13.setQuadTitle("Case 13");
            quadrant13.setQuadDesc("Sector Vienne (sud)");
            quadrant13.setQuadPhoto("plan_13");
            quadrant13.setQuadPhotoDesc("détail du plan de Blois");


            Quadrant quadrant14 = realm.createObject(Quadrant.class, "14");
            quadrant14.setQuadTitle("Case 14");
            quadrant14.setQuadDesc("Fôret de Blois (sud)");
            quadrant14.setQuadPhoto("plan_14");
            quadrant14.setQuadPhotoDesc("détail du plan de Blois");

            Quadrant quadrant15 = realm.createObject(Quadrant.class, "15");
            quadrant15.setQuadTitle("Case 15");
            quadrant15.setQuadDesc("Sector Les Granges (sud)");
            quadrant15.setQuadPhoto("plan_15");
            quadrant15.setQuadPhotoDesc("détail du plan de Blois");

            realm.commitTransaction();

            Toast.makeText(PlanActivity.this, "Initalize Quads", Toast.LENGTH_LONG)
                    .show();
        }


    }


}
