package net.aprille.bloissavoirecouter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import models.Location;
import models.Sound;

import static net.aprille.bloissavoirecouter.R.id.map;

public class AddLocationMapsActivity extends AppCompatActivity implements OnMarkerClickListener,
        OnMapReadyCallback {

    private GoogleMap mMap;

    final Context context = this;
    Realm realm;

    public File BloisUserDir;
    public File BloisSoundDir;
    public File BloisDir;
    public String BloisUserDirPath;
    public String DirectoryFinal;

    public LatLng centerPoint;
    public Location centerPlace;
    public String centerpointID;
    

    int permissionCheckStorage;
    int permissionCheckCamera;
    int permissionCheckMicrophone;
    int permissionCheckLocation;

    Marker marker;
    /**
     * Id to identify a camera permission request.
     */
    private static final int REQUEST_CAMERA = 0;

    /**
     * Id to identify a contacts permission request.
     */
    private static final int REQUEST_STORAGE = 1;

    /**
     * Id to identify a contacts permission request.
     */
    private static final int REQUEST_MICROPHONE = 2;

    /**
     * Id to identify a contacts permission request.
     */
    private static final int REQUEST_LOCATION = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        try {
            realm = Realm.getDefaultInstance();
        } catch (IllegalStateException fuckYouTooAndroid) {
            Realm.init(getApplicationContext());
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(config);
            realm = Realm.getDefaultInstance();
        }
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        centerpointID = extras.getString("placeID");

        Log.e("myApp", "centerpointID " + centerpointID );

        if (centerpointID == "Blois") {
            centerPoint = new LatLng(47.58866, 1.3350253);

        }
        if (centerpointID != "Blois") {
            centerPlace = realm.where(Location.class).equalTo("locationID", centerpointID).findFirst();

            if (centerPlace != null) {
                centerPoint = new LatLng(centerPlace.getLatitude(), centerPlace.getLongitiude());
            } else {

                centerPoint = new LatLng(47.58866, 1.3350253);
            }


        } else {
            centerPoint = new LatLng(47.58866, 1.3350253);

        }

        // Assume check permissions for camera and storage
        permissionCheckCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        Log.w("myApp", "permissionCheck " + permissionCheckCamera );

        permissionCheckStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Log.w("myApp", "permissionCheckStorage " + permissionCheckStorage );

        permissionCheckMicrophone = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
        Log.w("myApp", "permissionCheckMicrophone " + permissionCheckMicrophone );

        permissionCheckLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        Log.w("myApp", "permissionCheckMicrophone " + permissionCheckLocation );


        if (permissionCheckLocation == -1) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            permissionCheckMicrophone = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            Log.w("myApp", "permissionCheckMicrophone value after request " + permissionCheckLocation );
        }

        setContentView(R.layout.activity_add_location_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (permissionCheckLocation == -1) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            permissionCheckMicrophone = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            Log.w("myApp", "permissionCheckMicrophone value after request " + permissionCheckLocation );
        }

        if (permissionCheckLocation != -1) {
            // Add a marker in Sydney and move the camera


            RealmResults<Location> results = realm.where(Location.class).findAll();

            for (int i = 0; i < results.size(); i++) {
                Location loc = results.get(i);

                marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(loc.getLatitude(), loc.getLongitiude()))
                                .title(loc.getLocationName())
                                .snippet(String.valueOf("Sounds : "+ loc.getLocationSounds().size()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                marker.setTag(loc.getLocationID());
            }

 //           LatLng blois = new LatLng(47.58866, 1.3350253);
 //           mMap.addMarker(new MarkerOptions().position(blois).title("Blois : Savoir Écouter"));

            if (centerpointID == "Blois") {

                mMap.addMarker(new MarkerOptions().position(centerPoint).title("Blois : Savoir Écouter"));
                mMap.setMyLocationEnabled(true); // false to disable
                mMap.moveCamera(CameraUpdateFactory.newLatLng(centerPoint));
                mMap.setMyLocationEnabled(true); // false to disable

            } else {
                mMap.addMarker(new MarkerOptions().position(centerPoint).title(centerPlace.getLocationName()));
                mMap.setMyLocationEnabled(true); // false to disable
                mMap.moveCamera(CameraUpdateFactory.newLatLng(centerPoint));
                mMap.setMyLocationEnabled(true); // false to disable
            }





        }
        // Set a listener for marker click.
        mMap.setOnMarkerClickListener((OnMarkerClickListener) this);


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
        {
            @Override
            public void onInfoWindowClick(Marker marker) {

                String getID = String. valueOf(marker.getTag());
                Location clickLocation = realm.where(Location.class).equalTo("locationID", getID ).findFirst();

                if (clickLocation.getLocationSounds().size() > 2 ) {
                    Log.w("myApp", "Size Greater Than 1  " + clickLocation.getLocationSounds().size());
                    Intent intent = new Intent(getApplicationContext(), PlaceSoundsActivity.class);
                    intent.putExtra("locationID", clickLocation.getLocationID());
                    startActivity(intent);

                    Log.w("myApp", "Size Greater Than 1  " + clickLocation.getLocationSounds().size());
                } else if ( clickLocation.getLocationSounds().size() == 1 ) {
                    Log.w("myApp", "Size Equals 1  " + clickLocation.getLocationSounds().size());
                    Sound clickSound = clickLocation.getLocationSounds().first();
                    Intent intent = new Intent(AddLocationMapsActivity.this, ZDetailSoundActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("callingtype", "PLAN");
                    extras.putString("callingId", marker.getTag().toString());
                    extras.putString("soundID", clickSound.getSoundID());
                    intent.putExtras(extras);
                    startActivity(intent);
                }

                Log.w("myApp", "onInfoWindowClick  " + marker.getTag().toString() );

            }

        });








    }





    /** setup for options menu */

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




    /** Called when the user clicks a marker. */

    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.


        // Check if a click count was set, then display the click count.
        if (marker.getTag() != null) {

            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " +  marker.getTag(),
                    Toast.LENGTH_SHORT).show();
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }
}


