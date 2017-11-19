package net.aprille.bloissavoirecouter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import helperfunctions.CSVFile;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import io.realm.Sort;
import models.AppSpecificDetails;
import models.Location;
import models.Quadrant;
import models.Sound;
import models.User;

import static helperfunctions.Util.getCurrDateString;

public class PeopleActivity extends AppCompatActivity {

    int sector;
    String thisPrimaryId;
    String thisPrimaryName;
    String thisPrimaryDesc;
    String thisPrimaryPhoto;
    Realm realm;
    RealmConfiguration nRealmConfig;
    RealmRecyclerView nUsers;
    RealmConfiguration config;

    RealmResults<Sound> user_Sound_Results;
    RealmList<Sound> user_Sound_List;

    int num_of_Users;

    RealmResults<User> primarySoundResults;
    RealmList primarySoundList;

    RealmResults<Sound> fixSoundResults;

    RealmResults<User> userClassResults;
    RealmList<User> userClassList;
    RealmList<Sound>  userSoundList;
    User primaryUser;

    boolean isNotDebugging = false;


    public String DirectoryFinal;
    public File BloisUserDir;
    public File BloisSoundDir;
    public File BloisDir;
    public String BloisUserDirPath;
    public String BloisSoundDirPath;
    public String thisSoundImageFilePath;
    public String thisIconThumbFilePath;
    String BloisSoundWebUrl;
    boolean isLandscape;
    Context context = this;

    public String userPrimaryThumbnail = "myimage.png";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nUsers = (RealmRecyclerView) findViewById(R.id.sound_realm_recycler_view);

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
        BloisSoundWebUrl = "http://savoir-ecouter.aprille.net/wp-content/uploads/";

        AppSpecificDetails thisAppDetails = realm.where(AppSpecificDetails.class).findFirst();
        thisPrimaryId = thisAppDetails.getPrimaryUserID();
        primaryUser  = realm.where(User.class).equalTo("userID", thisPrimaryId).findFirst();

        if (primaryUser.getUserID() != null) {

            ImageView iVprimaryUserImageView = (ImageView) findViewById(R.id.primaryImageView);
            String thisUserImageFilePath = BloisUserDirPath + "/" + primaryUser.getUserPhoto();
            Log.e("myApp :: ", "BloisUerDirPath with primary " + thisSoundImageFilePath );

            Picasso.with(this)
                    .load(new File(thisUserImageFilePath))
                    .resize(150, 150)
                    .centerCrop()
                    .placeholder(R.drawable.user_default_image)
                    .into(iVprimaryUserImageView);


            TextView tvPrimName = (TextView) findViewById(R.id.primaryUserName);
            tvPrimName.setText(primaryUser.getUserName());
            TextView tvPrimdescTextView = (TextView) findViewById(R.id.primaryUserDescr);
            tvPrimdescTextView.setText(primaryUser.getUserDesc());

            user_Sound_List = primaryUser.getUserSounds();

            user_Sound_Results =  user_Sound_List.where().findAllSorted("soundLikes", Sort.DESCENDING);


            if ((String.valueOf(primaryUser.getNumUserSounds()) == null)) {
                realm.beginTransaction();
                primaryUser.setNumUserSounds(0);
                realm.commitTransaction();
            }

            if (primaryUser.getNumUserSounds() != user_Sound_Results.size() ) {
                int ResultSize = user_Sound_Results.size();
                Log.e("myApp :: ", "User_for_Sounds.getNumUserSounds()" + String.valueOf(primaryUser.getNumUserSounds()) );
                Log.e("myApp :: ", "user_Sound_Results.size() " + String.valueOf(user_Sound_Results.size()) );
                realm.beginTransaction();
                primaryUser.setNumUserSounds(ResultSize);
                realm.commitTransaction();
            }


            TextView numSoundsPrimaryTextView = (TextView) findViewById(R.id.numSoundPrimaryTextView);
            Log.e("myApp :: ", "BloisUerDirPath with primary " + thisSoundImageFilePath );

            String nString = getString(R.string.sounds);
            String makeNumSoundsString = String.valueOf(primaryUser.getNumUserSounds());
            numSoundsPrimaryTextView.setText( nString + " " + makeNumSoundsString );

            userClassResults = realm.where(User.class).notEqualTo("userID", thisPrimaryId).findAll();

            nUsers = (RealmRecyclerView) findViewById(R.id.users_realm_recycler_view);


            UserRecyclerViewAdapter soundAdapter = new UserRecyclerViewAdapter(getBaseContext(), userClassResults, true, false);
            nUsers.setAdapter(soundAdapter);


        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (isNotDebugging) {
            fab.setVisibility(View.INVISIBLE);
        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addUserFromFile();
                    addSoundsFromFile();
                    fixPhotoFilesSad();
                    Snackbar.make(view, "Replace with add users", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
        realm = null;

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
                // User chose back/UP button...
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

    public void buttonClickPrimary(View v) {


        Intent intent = new Intent(getApplicationContext(), PrimaryUserActivity.class);
        intent.putExtra("userID", thisPrimaryId);
        startActivity(intent);

    }

    public void fixPhotoFilesSad() {
        fixSoundResults = realm.where(Sound.class).findAll();
        for (int i = 0; i < fixSoundResults.size(); i++) {
            Log.e("myApp ", "inside fixPhoto "+ fixSoundResults.get(i).getSoundPhoto()  );
            Log.e("myApp ", "inside sound "+ fixSoundResults.get(i).getSoundFile()  );
            String wierdString = fixSoundResults.get(i).getSoundID() +"jpg";
            if (fixSoundResults.get(i).getSoundPhoto() == fixSoundResults.get(i).getSoundFile() ) {
                String fixString = fixSoundResults.get(i).getSoundID() + ".jpg";
                Log.e("myApp ","fixstring!!!!!" + fixString   );
                realm.beginTransaction();
                fixSoundResults.get(i).setSoundPhoto(fixString);
                realm.commitTransaction();
                Log.e("myApp ","fixstring" + fixSoundResults.get(i).getSoundPhoto()  );
            }

        }


    }

    public interface IMyViewHolderClicks {
        public void onLike(View caller);
        public void onPlay(ImageView callerImage);
    }

    public class UserRecyclerViewAdapter extends RealmBasedRecyclerViewAdapter<
                User, UserRecyclerViewAdapter.ViewHolder> {
        User thisUser;

        Uri thisUserUri;
        String thisUserPhotoString;

        public UserRecyclerViewAdapter(
                Context context,
                RealmResults<User> realmResults,
                boolean automaticUpdate,
                boolean animateIdType ) {
            super(context, realmResults, automaticUpdate, animateIdType);
        }

        public class ViewHolder extends RealmViewHolder implements View.OnClickListener {

            private ImageView uImage;
            private ImageView uIconSound;
            private ImageButton mButton_likes;
            private TextView uTitle;
            private TextView uSounds;
            private LinearLayout uSoundLibPress;
            private ImageView uMore;
            File thisUserFileNamePath;
            public IMyViewHolderClicks uListener;


            public ViewHolder(LinearLayout container) {
                super(container);

                Log.e("myApp ", "ViewHolder "   );
                uImage = (ImageView) container.findViewById(R.id.iV_user_image);
                this.uTitle = (TextView) container.findViewById(R.id.user_name_grid);
                uIconSound = (ImageView) container.findViewById(R.id.icon_sounds);
                uSoundLibPress = (LinearLayout)  container.findViewById(R.id.num_sounds_press);
                uSounds = (TextView) container.findViewById(R.id.sound_number_for_user);
                uMore = (ImageView) container.findViewById(R.id.moreInfoUser);
                uImage.setOnClickListener(this);
                uSoundLibPress.setOnClickListener(this);
                container.setOnClickListener(this);
                uMore.setOnClickListener(this);

            }



            @Override
            public void onClick(View v) {
                thisUser = realmResults.get(getAdapterPosition());
                thisUserPhotoString = BloisUserDirPath + "/" + thisUser.getUserPhoto();
                thisUserUri = Uri.parse(thisUserPhotoString);
                Log.e("myApp :: ", " onclick " + getAdapterPosition() );
                thisUserPhotoString = BloisUserDirPath + "/" + thisUser.getUserPhoto();

                thisUserUri = Uri.parse(thisUserPhotoString);

                Intent intent = new Intent(getApplicationContext(), UserSoundsActivity.class);
                intent.putExtra("userID", thisUser.getUserID());
                startActivity(intent);

                if (v instanceof ImageView){
                    Log.e("myApp ", "onplay inside onclick " + getAdapterPosition() );

//                    thisUserPhotoString = BloisUserDirPath + "/" + thisUser.getUserPhoto();
//
//                    thisUserUri = Uri.parse(thisUserPhotoString);
//
//                    Intent intent = new Intent(getApplicationContext(), UserSoundsActivity.class);
//                    intent.putExtra("userID", thisUser.getUserID());
//                    startActivity(intent);



                    Log.e("myApp", "onplay inside onclick " + thisUser.getUserName()) ;
                    //           mListener.onPlay((ImageView)v);
                } else {
                    Log.e("myApp: ", "onlike inside onclick " +""  );

                    //                   Sound realmSound = realm.where(Sound.class).equalTo("SoundID", thisSound.getSoundID()).findFirst();



                }
            }



        }

        // The Viewholder which we inflate here the layout for items in recycleview here note_item

        @Override
        public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
            Log.e("myApp:: ", "onCreateRealmViewHolder " );
            View v = inflater.inflate(R.layout.grid_item_view_user, viewGroup, false);

            return new ViewHolder((LinearLayout) v);
        }

        @Override
        public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {
            final User user = realmResults.get(position);


            Log.e("myApp :: ", "BloisUserDirPath IN ONBIND " + thisSoundImageFilePath );
            if (user.isPrimaryUserBoolean()) {
                thisSoundImageFilePath = BloisUserDirPath + "/" + user.getUserPhoto();
                Picasso.with(viewHolder.uImage.getContext())
                        .load(new File(thisSoundImageFilePath))
                        .centerCrop()
                        .resize(150, 150)
                        .placeholder(R.drawable.sound_defaul_image)
                        .into(viewHolder.uImage);
            } else {
                thisSoundImageFilePath = BloisUserDirPath + "/" + user.getUserPhoto();
                File checkImageFile = new File(thisSoundImageFilePath);
                if (checkImageFile.exists() ){
                    Picasso.with(viewHolder.uImage.getContext())
                            .load(new File(thisSoundImageFilePath))
                            .resize(150, 150)
                            .centerCrop()
                            .placeholder(R.drawable.sound_defaul_image)
                            .into(viewHolder.uImage);

                } else {
                    thisSoundImageFilePath = BloisSoundWebUrl + "/" + user.getUserPhoto();
                    Picasso.with(viewHolder.uImage.getContext())
                            .load(thisSoundImageFilePath)
                            .resize(150, 150)
                            .centerCrop()
                            .placeholder(R.drawable.user_default_image)
                            .into(viewHolder.uImage);
                }

            }


            viewHolder.uTitle.setText(user.getUserName());

            viewHolder.uIconSound.setImageResource(R.drawable.ic_library_music_black_24dp);

            viewHolder.uMore.setImageResource(R.drawable.ic_more_horiz_black_24dp);

            userSoundList = user.getUserSounds();
            int thisis14 = 14;
            int thisUserNumSoundComputed = userSoundList.size();
            int thisUserNumSoundRetreived = user.getNumUserSounds();
            if (thisUserNumSoundRetreived != thisUserNumSoundComputed) {
//                String numSoundString = Integer.toString( thisUserNumSoundComputed );
//                realm.beginTransaction();
//                user.setNumUserSounds(thisUserNumSoundComputed);
//                realm.commitTransaction();
//                Log.w("myApp :: ", "Huston we have a problem commputed " + thisUserNumSoundComputed );
                Log.w("myApp :: ", "Huston we have a problem retrieved " + thisUserNumSoundComputed );
                Log.w("myApp :: ", "Huston we have a problem string " + thisis14 );
//                Log.w("myApp :: ", "Huston we have a problem string " + ?numSoundString );

            } else {
                String numSoundString = Integer.toString( thisUserNumSoundComputed  );
//                Log.w("myApp :: ", "Huston we don't have a problem commputed " + thisUserNumSoundComputed );
//                Log.w("myApp :: ", "Huston we don't have a problem retrieved " + thisUserNumSoundComputed );
//                Log.w("myApp :: ", "Huston don'twe have a problem string " + numSoundString );


            }

            String numSoundString = Integer.toString( thisUserNumSoundComputed );
            viewHolder.uSounds.setText( numSoundString );


        }
    }

    private void addUserFromFile() {

        InputStream inputStream = getResources().openRawResource(R.raw.user);
        CSVFile csvFileSound = new CSVFile(inputStream);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            int rowCount = 1;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split("\\|");

                int len = row.length;
                Log.w("myApp :: ", "Value length of row " + len );

                User newUser  = realm.where(User.class).equalTo("userID", row[0]).findFirst();
                if (newUser == null ){
                    Log.w("myApp :: ", "Value length of row 0 " + row[0] );
                    Log.w("myApp :: ", "Value length of row 1 " + row[1] );
                    Log.w("myApp :: ", "Value length of row 2 " + row[2] );
                    Log.w("myApp :: ", "Value length of row 3 " + row[3] );
                    realm.beginTransaction();
                    newUser = realm.createObject(User.class, row[0]);
                    Log.w("myApp :: ", "Value length of row 1 " + row[1] );
                    newUser.setUserName(row[1]);
                    if (row[2] != null){
                        newUser.setUserDesc(row[2]);
                    }
                    newUser.setTimeJoined(getCurrDateString());
                    newUser.setUserPhoto(row[3]);
                    newUser.setUserPhotoDesc(row[1]);
                    newUser.setPrimaryUserBoolean(false);
                    newUser.setNumUserSounds(0);
                    realm.commitTransaction();

                }

            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }

    }

    private void addPlaceFromFile() {

        InputStream inputStream = getResources().openRawResource(R.raw.places);
        CSVFile csvFileSound = new CSVFile(inputStream);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            int rowCount = 1;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split("\\|");

                int len = row.length;
                Log.w("myApp :: ", "Value length of row " + len );

                Location newUPlace  = realm.where(Location.class).equalTo("locationID", row[0]).findFirst();
                if (newUPlace == null ){

                    realm.beginTransaction();
                    newUPlace = realm.createObject(Location.class, row[0]);
                    Log.w("myApp :: ", "after create " + row[1] );
                    newUPlace.setLocationName(row[1]);
                    newUPlace.setLocationAddress(row[2]);
                    newUPlace.setLongitiude( Double.parseDouble(row[4]) );
                    newUPlace.setLatitude( Double.parseDouble(row[3])  );
                    newUPlace.setLocationNumSounds(0);
                    newUPlace.setSharedLocation(true);
                    newUPlace.setLocationSearchText(row[1] + " " + row[2] );
                    realm.commitTransaction();

                } else {
                    Log.w("myApp :: ", "place exists " + newUPlace.getLocationName() );
                }

            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }

    }


    private void addSoundsFromFile() {

        InputStream inputStreamSound = getResources().openRawResource(R.raw.sounds);
        CSVFile csvFileSound = new CSVFile(inputStreamSound);

        BufferedReader readerSound = new BufferedReader(new InputStreamReader(inputStreamSound));
        try {
            String csvLine;
            int rowCount = 1;
            while ((csvLine = readerSound.readLine()) != null) {
                String[] row = csvLine.split("\\|");
                String locationAddress = " ";
                int len = row.length;
                Log.w("myApp :: ", "Value  of csvLine " + csvLine );
                Log.w("myApp :: ", "Value length of row " + len );
                Sound newSound = realm.where(Sound.class).equalTo("soundID", row[0]).findFirst();
                Log.w("myApp :: ", "UserID " + row[8] );
                User newUser  = realm.where(User.class).equalTo("userID", row[9]).findFirst();
       //         Log.w("myApp :: ", "user " + newUser.getUserName() );
                Quadrant newQuadrant = realm.where(Quadrant.class).equalTo("quadID", row[11]).findFirst();
       //         Log.w("myApp :: ", "Quad " + newQuadrant.getQuadID() );

                if ((newQuadrant != null ) && (newUser != null ) && (newSound == null) ){

//                   0 soundID| 1 soundName| 2 soundAbout| 3 soundFile| 4 soundPhoto|5 soundQuadrant| 6 soundUserID| 7 soundUserName| 8 soundUserBio| 9 soundUserPhoto
//                   0 soundID| 1 soundName| 2 soundAbout | 3 soundFile| 4 soundPhoto|5 soundPhotoDesc|6 localizeMedia|7 createdByPrimaryUser| 8 soundLikes| 9 userID| 10 userName| 11 quadID| 12 locationID| 13 locationName


                    realm.beginTransaction();
                    Sound newSound1 = realm.createObject(Sound.class, row[0]);
                    newSound1.setSoundName(row[1]);
                    newSound1.setSoundDesc(row[2]);
                    newSound1.setSoundFile(row[3]);
                    newSound1.setSoundPhoto(row[4]);
                    newSound1.setSoundPhotoDesc( row[5]);
                    newSound1.setTimeCreated(getCurrDateString());
                    if (isNotDebugging) {
                        newSound1.setLocalizeMedia(false);
                    } else {
                        newSound1.setLocalizeMedia(true);
                    }

                    newSound1.setCreatedByPrimaryUser(false);
                    newSound1.setSoundLikes( Integer.parseInt(row[8]) );
                    Location newlocation = realm.where(Location.class).equalTo("locationID", row[12]).findFirst();
                    if (newlocation  != null ) {
                        newlocation.getLocationSounds().add(newSound1);
                        locationAddress = newlocation.getLocationAddress();
                    } else {
                        locationAddress = " ";
                    }
                    newUser.getUserSounds().add(newSound1);

                    newQuadrant.getQuadSounds().add(newSound1);
                    String textForSearch = row[1] + " " + row[2] + " " + row[10] + " " + row[12] + " " + newUser.getUserDesc() + " " + locationAddress;
                    newSound1.setSoundSearchText(textForSearch);
                    realm.commitTransaction();
                } else {
                    Log.e("myApp :: ", "problem with sound " + row[1]  );
                    if (newQuadrant == null) {
                        Log.e("myApp :: ", "quad is NULL! " + row[12]  );
                        Log.e("myApp :: ", "null problem with sound " + row[1]  );
                    }
                    if (newUser == null) {
                        Log.e("myApp :: ", "newUser is NULL! " + row[9]   );
                        Log.e("myApp :: ", "null problem with sound " + row[1]  );
                    }



                }
            }

        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStreamSound.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }

    }





}
