package net.aprille.bloissavoirecouter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import models.Quadrant;
import models.Sound;
import models.User;

public class PlanActivity extends AppCompatActivity {

    Realm realm;
    RealmResults<Sound> allSounds;

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

        initializeSectors();
        initializeASoundPerSector();


        boolean isFirstRun = true;

        AppSpecificDetails thisAppDetails = realm.where(AppSpecificDetails.class).findFirst();


        if (thisAppDetails == null) {
            startActivity(new Intent(PlanActivity.this, StartupActivity.class));
            Toast.makeText(PlanActivity.this, "appDetails in null", Toast.LENGTH_LONG)
                    .show();

        } else {
            Log.w("myApp", ".getPrimaryUserID " + thisAppDetails.getPrimaryUserID());
            Log.w("myApp", "getPrimaryUserName " + thisAppDetails.getPrimaryUserName());

        }
        allSounds = realm.where(Sound.class).findAll();
        Log.w("myApp", "sound total " + String.valueOf(allSounds.size()));

        User AprilleUser  = realm.where(User.class).equalTo("userID", "20140424_0_Aprille").findFirst();

        if (AprilleUser == null) {
            Toast.makeText(PlanActivity.this, "AprilleUser is null", Toast.LENGTH_LONG)
                    .show();
            realm.beginTransaction();
            User newUser = realm.createObject(User.class, "20140424_0_Aprille");
            newUser.setUserName("Aprille Best Glover");
            newUser.setUserDesc("artiste");
            newUser.setTimeJoined("20140424");
            newUser.setUserPhoto("20140424_0.jpg");
            newUser.setUserPhotoDesc("Aprille Best Glover");
            newUser.setPrimaryUserBoolean(false);
            newUser.setNumUserSounds(0);
            realm.commitTransaction();

        } else {
            Toast.makeText(PlanActivity.this, "AprilleUser IS NOT null", Toast.LENGTH_LONG)
                    .show();
            Log.w("myApp", "From checking AprilleUser " + AprilleUser.getUserID());
            Log.w("myApp", "getuserName " + AprilleUser.getUserName());
            Log.w("myApp", "getuserSounds num " + AprilleUser.getUserSounds().size());
            Sound testSound1 = realm.where(Sound.class).findFirst();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void initializeASoundPerSector() {

        realm.beginTransaction();

        User AprilleUser  = realm.where(User.class).equalTo("userID", "20140424_0_Aprille").findFirst();
        Quadrant quadrant1 = realm.where(Quadrant.class).equalTo("quadID", "1").findFirst();
        Sound trysound = realm.where(Sound.class).equalTo("soundID", "20140424_Q1").findFirst();

        if ( (AprilleUser != null ) && ( quadrant1 != null ) && (trysound == null)){

            Sound newSound1 = realm.createObject(Sound.class, "20140424_Q1");
            newSound1.setSoundName("Q1 hôpital");
            newSound1.setSoundDesc("un son de début");
            newSound1.setSoundFile("20140424_Q1.wav");
            newSound1.setSoundPhoto("20140424_Q1.png");
            newSound1.setTimeCreated("20140424");
            newSound1.setLocalizeMedia(true);
            newSound1.setCreatedByPrimaryUser(false);
            newSound1.setSoundLikes(10);
            AprilleUser.getUserSounds().add(newSound1);
            quadrant1.getQuadSounds().add(newSound1);
        }
        if ((trysound != null ) && (trysound.getSoundName() == null) ){
            trysound.setSoundName("Q1 hôpital");
            trysound.setSoundDesc("un son de début");
            trysound.setSoundFile("20140424_Q1.wav");
            trysound.setSoundPhoto("20140424_Q1.png");
            trysound.setTimeCreated("20140424");
            trysound.setLocalizeMedia(true);
            trysound.setCreatedByPrimaryUser(false);
            trysound.setSoundLikes(10);
            AprilleUser.getUserSounds().add(trysound);
            quadrant1.getQuadSounds().add(trysound);
                Toast.makeText(PlanActivity.this, "Initalize 1st Sound", Toast.LENGTH_LONG)
                        .show();
        }



        realm.commitTransaction();

        realm.beginTransaction();

        Quadrant quadrant2 = realm.where(Quadrant.class).equalTo("quadID", "2").findFirst();
        Sound trysound2 = realm.where(Sound.class).equalTo("soundID", "20140424_Q2").findFirst();
        if ((trysound2 != null ) && (trysound2.getSoundName() == null) ){
            trysound2.setSoundName("Q2 rue résidentielle");
            trysound2.setSoundDesc("un son de début");
            trysound2.setSoundFile("20140424_Q2.wav");
            trysound2.setSoundPhoto("20140424_Q2.png");
            trysound2.setTimeCreated("20140424");
            trysound2.setLocalizeMedia(true);
            trysound2.setCreatedByPrimaryUser(false);
            trysound2.setSoundLikes(10);
            AprilleUser.getUserSounds().add(trysound2);
            quadrant2.getQuadSounds().add(trysound2);
            Toast.makeText(PlanActivity.this, "Initalize 1st Sound", Toast.LENGTH_LONG)
                    .show();
        }

        if ( (AprilleUser != null ) && ( quadrant1 != null ) && (trysound == null)){

            Sound newSound2 = realm.createObject(Sound.class, "20140424_Q2");
            newSound2.setSoundName("Q2 rue résidentielle");
            newSound2.setSoundDesc("un son de début");
            newSound2.setSoundFile("20140424_Q2.wav");
            newSound2.setSoundPhoto("20140424_Q2.png");
            newSound2.setTimeCreated("20140424");
            newSound2.setLocalizeMedia(true);
            newSound2.setCreatedByPrimaryUser(false);
            newSound2.setSoundLikes(10);
            AprilleUser.getUserSounds().add(newSound2);
            quadrant2.getQuadSounds().add(newSound2);
        }


        realm.commitTransaction();

        realm.beginTransaction();

        Quadrant quadrant3 = realm.where(Quadrant.class).equalTo("quadID", "3").findFirst();
        Sound trysound3 = realm.where(Sound.class).equalTo("soundID", "20140424_Q3").findFirst();
        if ((trysound3 != null ) && (trysound3.getSoundName() == null) ){
            trysound3.setSoundName("Q2 rue résidentielle");
            trysound3.setSoundDesc("un son de début");
            trysound3.setSoundFile("20140424_Q3.wav");
            trysound3.setSoundPhoto("20140424_Q3.png");
            trysound3.setTimeCreated("20140424");
            trysound3.setLocalizeMedia(true);
            trysound3.setCreatedByPrimaryUser(false);
            trysound3.setSoundLikes(10);
            AprilleUser.getUserSounds().add(trysound3);
            quadrant3.getQuadSounds().add(trysound3);

        }

        if ( (AprilleUser != null ) && ( quadrant3 != null ) && (trysound3 == null)){

            Sound newSound3 = realm.createObject(Sound.class, "20140424_Q3");
            newSound3.setSoundName("Q3 supermarché");
            newSound3.setSoundDesc("un son de début");
            newSound3.setSoundFile("20140424_Q3.wav");
            newSound3.setSoundPhoto("20140424_Q3.png");
            newSound3.setTimeCreated("20140424");
            newSound3.setLocalizeMedia(true);
            newSound3.setCreatedByPrimaryUser(false);
            newSound3.setSoundLikes(10);
            AprilleUser.getUserSounds().add(newSound3);
            quadrant3.getQuadSounds().add(newSound3);
        }


        realm.commitTransaction();

        realm.beginTransaction();

        Quadrant quadrant4 = realm.where(Quadrant.class).equalTo("quadID", "4").findFirst();
        Sound trysound4 = realm.where(Sound.class).equalTo("soundID", "20140424_Q4").findFirst();
        if ((trysound4 != null ) && (trysound4.getSoundName() == null) ){
            trysound4.setSoundName("Q4 Foundation de doubt");
            trysound4.setSoundDesc("un son de début");
            trysound4.setSoundFile("20140424_Q4.wav");
            trysound4.setSoundPhoto("20140424_Q4.png");
            trysound4.setTimeCreated("20140424");
            trysound4.setLocalizeMedia(true);
            trysound4.setCreatedByPrimaryUser(false);
            trysound4.setSoundLikes(10);
            AprilleUser.getUserSounds().add(trysound4);
            quadrant4.getQuadSounds().add(trysound4);
            Toast.makeText(PlanActivity.this, "Initalize 1st Sound", Toast.LENGTH_LONG)
                    .show();
        }

        if ( (AprilleUser != null ) && ( quadrant1 != null ) && (trysound4 == null)){

            Sound newSound4 = realm.createObject(Sound.class, "20140424_Q4");
            newSound4.setSoundName("Q4 Foundation de doubt");
            newSound4.setSoundDesc("un son de début");
            newSound4.setSoundFile("20140424_Q4.wav");
            newSound4.setSoundPhoto("20140424_Q4.png");
            newSound4.setTimeCreated("20140424");
            newSound4.setLocalizeMedia(true);
            newSound4.setCreatedByPrimaryUser(false);
            newSound4.setSoundLikes(10);
            AprilleUser.getUserSounds().add(newSound4);
            quadrant4.getQuadSounds().add(newSound4);
        }


        realm.commitTransaction();

        realm.beginTransaction();

        Quadrant quadrant5 = realm.where(Quadrant.class).equalTo("quadID", "5").findFirst();
        Sound trysound5 = realm.where(Sound.class).equalTo("soundID", "20140424_Q5").findFirst();
        if ((trysound5 != null ) && (trysound5.getSoundName() == null) ){
            trysound5.setSoundName("Q5 La bibliothèque Abbé-Grégoire");
            trysound5.setSoundDesc("un son de début");
            trysound5.setSoundFile("20140424_Q5.wav");
            trysound5.setSoundPhoto("20140424_Q5.png");
            trysound5.setTimeCreated("20140424");
            trysound5.setLocalizeMedia(true);
            trysound5.setCreatedByPrimaryUser(false);
            trysound5.setSoundLikes(10);
            AprilleUser.getUserSounds().add(trysound5);
            quadrant5.getQuadSounds().add(trysound5);
            Toast.makeText(PlanActivity.this, "Initalize 1st Sound", Toast.LENGTH_LONG)
                    .show();
        }

        if ( (AprilleUser != null ) && ( quadrant1 != null ) && (trysound5 == null)){

            Sound newSound5 = realm.createObject(Sound.class, "20140424_Q5");
            newSound5.setSoundName("Q5 La bibliothèque Abbé-Grégoire");
            newSound5.setSoundDesc("un son de début");
            newSound5.setSoundFile("20140424_Q5.wav");
            newSound5.setSoundPhoto("20140424_Q5.png");
            newSound5.setTimeCreated("20140424");
            newSound5.setLocalizeMedia(true);
            newSound5.setCreatedByPrimaryUser(false);
            newSound5.setSoundLikes(10);
            AprilleUser.getUserSounds().add(newSound5);
            quadrant5.getQuadSounds().add(newSound5);
        }


        realm.commitTransaction();

        realm.beginTransaction();

        Quadrant quadrant6 = realm.where(Quadrant.class).equalTo("quadID", "6").findFirst();
        Sound trysound6 = realm.where(Sound.class).equalTo("soundID", "20140424_Q6").findFirst();
        if ((trysound6 != null ) && (trysound6.getSoundName() == null) ){
            trysound6.setSoundName("Q6 La forêt domaniale de blois");
            trysound6.setSoundDesc("un son de début");
            trysound6.setSoundFile("20140424_Q6.wav");
            trysound6.setSoundPhoto("20140424_Q6.png");
            trysound6.setTimeCreated("20140424");
            trysound6.setLocalizeMedia(true);
            trysound6.setCreatedByPrimaryUser(false);
            trysound6.setSoundLikes(10);
            AprilleUser.getUserSounds().add(trysound6);
            quadrant6.getQuadSounds().add(trysound6);

        }

        if ( (AprilleUser != null ) && ( quadrant1 != null ) && (trysound6 == null)){

            Sound newSound6 = realm.createObject(Sound.class, "20140424_Q6");
            newSound6.setSoundName("Q6 La forêt domaniale de blois");
            newSound6.setSoundDesc("un son de début");
            newSound6.setSoundFile("20140424_Q6.wav");
            newSound6.setSoundPhoto("20140424_Q6.png");
            newSound6.setTimeCreated("20140424");
            newSound6.setLocalizeMedia(true);
            newSound6.setCreatedByPrimaryUser(false);
            newSound6.setSoundLikes(10);
            AprilleUser.getUserSounds().add(newSound6);
            quadrant6.getQuadSounds().add(newSound6);
        }


        realm.commitTransaction();

        realm.beginTransaction();

        Quadrant quadrant7 = realm.where(Quadrant.class).equalTo("quadID", "7").findFirst();
        Sound trysound7 = realm.where(Sound.class).equalTo("soundID", "20140424_Q7").findFirst();
        if ((trysound7 != null ) && (trysound7.getSoundName() == null) ){
            trysound7.setSoundName("Q7 l'atelier de sculpteur");
            trysound7.setSoundDesc("un son de début");
            trysound7.setSoundFile("20140424_Q7.wav");
            trysound7.setSoundPhoto("20140424_Q7.png");
            trysound7.setTimeCreated("20140424");
            trysound7.setLocalizeMedia(true);
            trysound7.setCreatedByPrimaryUser(false);
            trysound7.setSoundLikes(10);
            AprilleUser.getUserSounds().add(trysound7);
            quadrant7.getQuadSounds().add(trysound7);

        }

        if ( (AprilleUser != null ) && ( quadrant1 != null ) && (trysound7 == null)){

            Sound newSound7 = realm.createObject(Sound.class, "20140424_Q7");
            newSound7.setSoundName("Q7 l'atelier de sculpteur");
            newSound7.setSoundDesc("un son de début");
            newSound7.setSoundFile("20140424_Q7.wav");
            newSound7.setSoundPhoto("20140424_Q7.png");
            newSound7.setTimeCreated("20140424");
            newSound7.setLocalizeMedia(true);
            newSound7.setCreatedByPrimaryUser(false);
            newSound7.setSoundLikes(10);
            AprilleUser.getUserSounds().add(newSound7);
            quadrant7.getQuadSounds().add(newSound7);
        }
        realm.commitTransaction();

        realm.beginTransaction();
        Quadrant quadrant8 = realm.where(Quadrant.class).equalTo("quadID", "8").findFirst();
        Sound trysound8 = realm.where(Sound.class).equalTo("soundID", "20140424_Q8").findFirst();
        if ((trysound8 != null ) && (trysound8.getSoundName() == null) ){
            trysound8.setSoundName("Q8 le château de blois");
            trysound8.setSoundDesc("un son de début");
            trysound8.setSoundFile("20140424_Q8.wav");
            trysound8.setSoundPhoto("20140424_Q8.png");
            trysound8.setTimeCreated("20140424");
            trysound8.setLocalizeMedia(true);
            trysound8.setCreatedByPrimaryUser(false);
            trysound8.setSoundLikes(10);
            AprilleUser.getUserSounds().add(trysound8);
            quadrant8.getQuadSounds().add(trysound8);

        }

        if ( (AprilleUser != null ) && ( quadrant1 != null ) && (trysound8 == null)){

            Sound newSound8 = realm.createObject(Sound.class, "20140424_Q8");
            newSound8.setSoundName("Q8 le château de blois");
            newSound8.setSoundDesc("un son de début");
            newSound8.setSoundFile("20140424_Q8.wav");
            newSound8.setSoundPhoto("20140424_Q8.png");
            newSound8.setTimeCreated("20140424");
            newSound8.setLocalizeMedia(true);
            newSound8.setCreatedByPrimaryUser(false);
            newSound8.setSoundLikes(10);
            AprilleUser.getUserSounds().add(newSound8);
            quadrant8.getQuadSounds().add(newSound8);
        }
        realm.commitTransaction();

        realm.beginTransaction();
        Quadrant quadrant9 = realm.where(Quadrant.class).equalTo("quadID", "9").findFirst();
        Sound trysound9 = realm.where(Sound.class).equalTo("soundID", "20140424_Q9").findFirst();
        if ((trysound9 != null ) && (trysound9.getSoundName() == null) ){
            trysound9.setSoundName("Q9 un bistrot");
            trysound9.setSoundDesc("un son de début");
            trysound9.setSoundFile("20140424_Q9.wav");
            trysound9.setSoundPhoto("20140424_Q9.png");
            trysound9.setTimeCreated("20140424");
            trysound9.setLocalizeMedia(true);
            trysound9.setCreatedByPrimaryUser(false);
            trysound9.setSoundLikes(10);
            AprilleUser.getUserSounds().add(trysound9);
            quadrant9.getQuadSounds().add(trysound9);

        }

        if ( (AprilleUser != null ) && ( quadrant1 != null ) && (trysound9 == null)){

            Sound newSound9 = realm.createObject(Sound.class, "20140424_Q9");
            newSound9.setSoundName("Q10 La forêt domaniale de blois");
            newSound9.setSoundDesc("un son de début");
            newSound9.setSoundFile("20140424_Q9.wav");
            newSound9.setSoundPhoto("20140424_Q9.png");
            newSound9.setTimeCreated("20140424");
            newSound9.setLocalizeMedia(true);
            newSound9.setCreatedByPrimaryUser(false);
            newSound9.setSoundLikes(10);
            AprilleUser.getUserSounds().add(newSound9);
            quadrant9.getQuadSounds().add(newSound9);
        }
        realm.commitTransaction();

        realm.beginTransaction();
        Quadrant quadrant10 = realm.where(Quadrant.class).equalTo("quadID", "10").findFirst();
        Sound trysound10 = realm.where(Sound.class).equalTo("soundID", "20140424_Q10").findFirst();
        if ((trysound10 != null ) && (trysound10.getSoundName() == null) ){
            trysound10.setSoundName("Q10 La forêt domaniale de blois");
            trysound10.setSoundDesc("un son de début");
            trysound10.setSoundFile("20140424_Q10.wav");
            trysound10.setSoundPhoto("20140424_Q10.png");
            trysound10.setTimeCreated("20140424");
            trysound10.setLocalizeMedia(true);
            trysound10.setCreatedByPrimaryUser(false);
            trysound10.setSoundLikes(10);
            AprilleUser.getUserSounds().add(trysound10);
            quadrant10.getQuadSounds().add(trysound10);

        }

        if ( (AprilleUser != null ) && ( quadrant1 != null ) && (trysound10 == null)){

            Sound newSound10 = realm.createObject(Sound.class, "20140424_Q10");
            newSound10.setSoundName("Q10 La forêt domaniale de blois");
            newSound10.setSoundDesc("un son de début");
            newSound10.setSoundFile("20140424_Q10.wav");
            newSound10.setSoundPhoto("20140424_Q10.png");
            newSound10.setTimeCreated("20140424");
            newSound10.setLocalizeMedia(true);
            newSound10.setCreatedByPrimaryUser(false);
            newSound10.setSoundLikes(10);
            AprilleUser.getUserSounds().add(newSound10);
            quadrant10.getQuadSounds().add(newSound10);
        }
        realm.commitTransaction();

        realm.beginTransaction();
        Quadrant quadrant11 = realm.where(Quadrant.class).equalTo("quadID", "11").findFirst();
        Sound trysound11 = realm.where(Sound.class).equalTo("soundID", "20140424_Q11").findFirst();
        if ((trysound11 != null ) && (trysound11.getSoundName() == null) ){
            trysound11.setSoundName("Q11 tondre la pelouse");
            trysound11.setSoundDesc("un son de début");
            trysound11.setSoundFile("20140424_Q11.wav");
            trysound11.setSoundPhoto("20140424_Q11.png");
            trysound11.setTimeCreated("20140424");
            trysound11.setLocalizeMedia(true);
            trysound11.setCreatedByPrimaryUser(false);
            trysound11.setSoundLikes(10);
            AprilleUser.getUserSounds().add(trysound11);
            quadrant11.getQuadSounds().add(trysound11);

        }

        if ( (AprilleUser != null ) && ( quadrant1 != null ) && (trysound11 == null)){

            Sound newSound11 = realm.createObject(Sound.class, "20140424_Q11");
            newSound11.setSoundName("Q11 tondre la pelouse");
            newSound11.setSoundDesc("un son de début");
            newSound11.setSoundFile("20140424_Q11.wav");
            newSound11.setSoundPhoto("20140424_Q11.png");
            newSound11.setTimeCreated("20140424");
            newSound11.setLocalizeMedia(true);
            newSound11.setCreatedByPrimaryUser(false);
            newSound11.setSoundLikes(10);
            AprilleUser.getUserSounds().add(newSound11);
            quadrant11.getQuadSounds().add(newSound11);
        }
        realm.commitTransaction();

        realm.beginTransaction();
        Quadrant quadrant12 = realm.where(Quadrant.class).equalTo("quadID", "12").findFirst();
        Sound trysound12 = realm.where(Sound.class).equalTo("soundID", "20140424_Q12").findFirst();
        if ((trysound12 != null ) && (trysound12.getSoundName() == null) ){
            trysound12.setSoundName("Q12 un rond point");
            trysound12.setSoundDesc("un son de début");
            trysound12.setSoundFile("20140424_Q12.wav");
            trysound12.setSoundPhoto("20140424_Q12.png");
            trysound12.setTimeCreated("20140424");
            trysound12.setLocalizeMedia(true);
            trysound12.setCreatedByPrimaryUser(false);
            trysound12.setSoundLikes(10);
            AprilleUser.getUserSounds().add(trysound12);
            quadrant12.getQuadSounds().add(trysound12);

        }

        if ( (AprilleUser != null ) && ( quadrant1 != null ) && (trysound12 == null)){

            Sound newSound12 = realm.createObject(Sound.class, "20140424_Q12");
            newSound12.setSoundName("Q12 un rond point");
            newSound12.setSoundDesc("un son de début");
            newSound12.setSoundFile("20140424_Q12.wav");
            newSound12.setSoundPhoto("20140424_Q12.png");
            newSound12.setTimeCreated("20140424");
            newSound12.setLocalizeMedia(true);
            newSound12.setCreatedByPrimaryUser(false);
            newSound12.setSoundLikes(10);
            AprilleUser.getUserSounds().add(newSound12);
            quadrant12.getQuadSounds().add(newSound12);
        }
        realm.commitTransaction();

        realm.beginTransaction();
        Quadrant quadrant13 = realm.where(Quadrant.class).equalTo("quadID", "13").findFirst();
        Sound trysound13 = realm.where(Sound.class).equalTo("soundID", "20140424_Q13").findFirst();
        if ((trysound13 != null ) && (trysound13.getSoundName() == null) ){
            trysound13.setSoundName("Q13 une école");
            trysound13.setSoundDesc("un son de début");
            trysound13.setSoundFile("20140424_Q13.wav");
            trysound13.setSoundPhoto("20140424_Q13.png");
            trysound13.setTimeCreated("20140424");
            trysound13.setLocalizeMedia(true);
            trysound13.setCreatedByPrimaryUser(false);
            trysound13.setSoundLikes(10);
            AprilleUser.getUserSounds().add(trysound13);
            quadrant13.getQuadSounds().add(trysound13);

        }

        if ( (AprilleUser != null ) && ( quadrant1 != null ) && (trysound13 == null)){

            Sound newSound13 = realm.createObject(Sound.class, "20140424_Q13");
            newSound13.setSoundName("Q13 une école");
            newSound13.setSoundDesc("un son de début");
            newSound13.setSoundFile("20140424_Q13.wav");
            newSound13.setSoundPhoto("20140424_Q13.png");
            newSound13.setTimeCreated("20140424");
            newSound13.setLocalizeMedia(true);
            newSound13.setCreatedByPrimaryUser(false);
            newSound13.setSoundLikes(10);
            AprilleUser.getUserSounds().add(newSound13);
            quadrant13.getQuadSounds().add(newSound13);
        }
        realm.commitTransaction();

        realm.beginTransaction();
        Quadrant quadrant14 = realm.where(Quadrant.class).equalTo("quadID", "14").findFirst();
        Sound trysound14 = realm.where(Sound.class).equalTo("soundID", "20140424_Q14").findFirst();
        if ((trysound14 != null ) && (trysound14.getSoundName() == null) ){
            trysound14.setSoundName("Q14 La forêt domaniale de blois");
            trysound14.setSoundDesc("un son de début");
            trysound14.setSoundFile("20140424_Q14.wav");
            trysound14.setSoundPhoto("20140424_Q14.png");
            trysound14.setTimeCreated("20140424");
            trysound14.setLocalizeMedia(true);
            trysound14.setCreatedByPrimaryUser(false);
            trysound14.setSoundLikes(10);
            AprilleUser.getUserSounds().add(trysound14);
            quadrant14.getQuadSounds().add(trysound14);

        }

        if ( (AprilleUser != null ) && ( quadrant1 != null ) && (trysound14 == null)){

            Sound newSound14 = realm.createObject(Sound.class, "20140424_Q14");
            newSound14.setSoundName("Q14 La forêt domaniale de blois");
            newSound14.setSoundDesc("un son de début");
            newSound14.setSoundFile("20140424_Q14.wav");
            newSound14.setSoundPhoto("20140424_Q14.png");
            newSound14.setTimeCreated("20140424");
            newSound14.setLocalizeMedia(true);
            newSound14.setCreatedByPrimaryUser(false);
            newSound14.setSoundLikes(10);
            AprilleUser.getUserSounds().add(newSound14);
            quadrant14.getQuadSounds().add(newSound14);
        }
        realm.commitTransaction();

        realm.beginTransaction();
        Quadrant quadrant15 = realm.where(Quadrant.class).equalTo("quadID", "15").findFirst();
        Sound trysound15 = realm.where(Sound.class).equalTo("soundID", "20140424_Q15").findFirst();
        if ((trysound15 != null ) && (trysound15.getSoundName() == null) ){
            trysound15.setSoundName("Q15 les oiseaux de la loire");
            trysound15.setSoundDesc("un son de début");
            trysound15.setSoundFile("20140424_Q15.wav");
            trysound15.setSoundPhoto("20140424_Q15.png");
            trysound15.setTimeCreated("20140424");
            trysound15.setLocalizeMedia(true);
            trysound15.setCreatedByPrimaryUser(false);
            trysound15.setSoundLikes(10);
            AprilleUser.getUserSounds().add(trysound15);
            quadrant15.getQuadSounds().add(trysound15);

        }

        if ( (AprilleUser != null ) && ( quadrant1 != null ) && (trysound15 == null)){

            Sound newSound15 = realm.createObject(Sound.class, "20140424_Q15");
            newSound15.setSoundName("Q15 les oiseaux de la loire");
            newSound15.setSoundDesc("un son de début");
            newSound15.setSoundFile("20140424_Q15.wav");
            newSound15.setSoundPhoto("20140424_Q15.png");
            newSound15.setTimeCreated("20140424");
            newSound15.setLocalizeMedia(true);
            newSound15.setCreatedByPrimaryUser(false);
            newSound15.setSoundLikes(10);
            AprilleUser.getUserSounds().add(newSound15);
            quadrant15.getQuadSounds().add(newSound15);
        }
        realm.commitTransaction();

    }


}
