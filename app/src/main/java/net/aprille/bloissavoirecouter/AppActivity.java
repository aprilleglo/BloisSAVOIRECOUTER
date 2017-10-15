package net.aprille.bloissavoirecouter;

import android.content.Context;
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

import helperfunctions.Util;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import models.Location;
import models.Quadrant;
import models.Sound;
import models.User;

import static helperfunctions.Util.generateCSVOnSD;

public class AppActivity extends AppCompatActivity {

    RealmResults<Sound> theseSounds;
    RealmResults<Location> thesePlaces;
    RealmResults<User>  theseUsers;

    public File BloisUserDir;
    public File BloisSoundDir;
    public File BloisDir;
    public String BloisUserDirPath;
    public String BloisSoundDirPath;
    public File BloisShareDir;
    public String BloisShareDirPath;
    public String thisSoundImageFilePath;
    public String thisSoundFilePath;
    public String thisIconThumbFilePath;
    boolean isLandscape;
    Context context = this;
    Realm realm;

    boolean isNotDebugging = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
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
        BloisUserDirPath = BloisUserDir.toString();
        BloisSoundDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/sounds");
        BloisSoundDirPath = BloisSoundDir.toString();
        BloisShareDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/share");
        BloisShareDirPath = BloisShareDir.toString();

        BloisDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (isNotDebugging) {
            fab.setVisibility(View.INVISIBLE);
        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cvsFilesExport();
                    Snackbar.make(view, "Export  csv files complete !", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }


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

    public void buttonClickTour (View v) {

    }

    public void buttonClickPrivacy (View v) {

        Intent intentPrivacy = new Intent(getApplicationContext(), PrivacyActivity.class);
        startActivity(intentPrivacy);

    }


    public void buttonClickAddLocation(View v) {

        Intent intent = new Intent(getApplicationContext(), AddLocationActivity.class);
        startActivity(intent);
    }


    public void cvsFilesExport() {
        Location loopLocation;
        String loopPlaceID;
        String loopPlaceName;
        String loopUserID;
        String loopUserName;
        String loopQuadID;

        theseUsers = realm.where(User.class).findAll();

        String userDataForExport = "userID|userDesc|userPhoto|userPhotoDesc|timeJoined\n";

        for(int i=0; i<theseUsers.size(); i++){

            Log.e("myApp :: ", "checkUserID " + theseUsers.get(i).getUserID());

            userDataForExport = userDataForExport + theseUsers.get(i).getUserID() + "|" + theseUsers.get(i).getUserName()+ "|" + theseUsers.get(i).getUserDesc()+ "|" +theseUsers.get(i).getTimeJoined()+ "\n";

        }

        generateCSVOnSD(context, "userExport.csv", userDataForExport);

        thesePlaces= realm.where(Location.class).findAll();

        String placeDataForExport = "locationID|locationName|locationAddress|longitiude|latitude|locationQuad|locationNumSounds|locationPhotoURL\n";

        for(int i=0; i <thesePlaces.size(); i++){

            Log.e("myApp :: ", "checkUserID " + thesePlaces.get(i).getLocationID());

            placeDataForExport = placeDataForExport + thesePlaces.get(i).getLocationID()+ "|" + thesePlaces.get(i).getLocationName() + "|" + thesePlaces.get(i).getLocationAddress()+ "|" +thesePlaces.get(i).getLatitude() + "|" + thesePlaces.get(i).getLongitiude()+ "|" + thesePlaces.get(i).getLocationQuad()+ "|" + thesePlaces.get(i).getLocationNumSounds()+ "|" +thesePlaces.get(i).getLocationPhotoURL() + "\n";

        }

        generateCSVOnSD(context, "locationExport.csv", placeDataForExport);

        theseSounds = realm.where(Sound.class).findAll();

        String soundDataForExport = "soundID|soundName|soundDesc|soundFile|soundPhoto|soundPhotoDesc|localizeMedia|createdByPrimaryUser|soundLikes|userID|userName|quadID|locationID|locationName\n";

        for(int i=0; i < theseSounds.size(); i++){

            Log.e("myApp :: ", "checkUserID " + theseSounds.get(i).getSoundID());


            if (Util.placeInfoExists(theseSounds.get(i))) {
                loopLocation = theseSounds.get(i).getSoundLocation().last();
                loopPlaceID = loopLocation.getLocationID();
                loopPlaceName = loopLocation.getLocationName();
            } else {
                loopPlaceID = "FAILED no place";
                loopPlaceName = "FAILED no place";
            }
            if (Util.userInfoExists(theseSounds.get(i))) {
                User loopUser = theseSounds.get(i).getSoundUser().first();
                loopUserID = loopUser.getUserID();
                loopUserName = loopUser.getUserName();
            } else {
                loopUserID = "FAILED User";
                loopUserName = "FAILED User";
            }

            if (Util.quadInfoExists(theseSounds.get(i))) {
                Quadrant loopQuad = theseSounds.get(i).getSoundQuad().first();
                loopQuadID =loopQuad.getQuadID();
            } else {
                loopQuadID = "FAILED no Quad";
            }

           soundDataForExport = soundDataForExport  + theseSounds.get(i).getSoundID()+ "|" + theseSounds.get(i).getSoundName() + "|" + theseSounds.get(i).getSoundDesc() + "|" + theseSounds.get(i).getSoundFile()+ "|" + theseSounds.get(i).getSoundPhoto()+ "|" + theseSounds.get(i).getSoundPhotoDesc()+ "|" + String.valueOf(theseSounds.get(i).isLocalizeMedia() )+ "|" +String.valueOf(theseSounds.get(i).isCreatedByPrimaryUser() ) + "|" + String.valueOf(theseSounds.get(i).getSoundLikes() ) + "|" + loopUserID + "|" + loopUserName + "|" +  loopQuadID + "|" + loopPlaceID + "|" + loopPlaceName +"\n";
        }

        generateCSVOnSD(context, "soundExport.csv", soundDataForExport);

    }




}
