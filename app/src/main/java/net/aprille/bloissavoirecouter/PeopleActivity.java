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
import models.AppSpecificDetails;
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


    int num_of_Users;

    RealmResults<User> primarySoundResults;
    RealmList primarySoundList;

    RealmResults<Sound> fixSoundResults;

    RealmResults<User> userClassResults;
    RealmList<User> userClassList;
    RealmList<Sound>  userSoundList;
    User primaryUser;


    public String DirectoryFinal;
    public File BloisUserDir;
    public File BloisSoundDir;
    public File BloisDir;
    public String BloisUserDirPath;
    public String BloisSoundDirPath;
    public String thisSoundImageFilePath;
    public String thisIconThumbFilePath;
    boolean isLandscape;
    Context context = this;

    public String userPrimaryThumbnail = "myimage.png";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

            TextView numSoundsPrimaryTextView = (TextView) findViewById(R.id.numSoundPrimaryTextView);

            String nString = getString(R.string.number_of_sounds);
            if ((String.valueOf(primaryUser.getNumUserSounds()) == null)) {
                primaryUser.setNumUserSounds(0);
            }
            String makeNumSoundsString = String.valueOf(primaryUser.getNumUserSounds());
            numSoundsPrimaryTextView.setText( nString + " " + makeNumSoundsString );

            userClassResults = realm.where(User.class).notEqualTo("userID", thisPrimaryId).findAll();

            nUsers = (RealmRecyclerView) findViewById(R.id.users_realm_recycler_view);


            UserRecyclerViewAdapter soundAdapter = new UserRecyclerViewAdapter(getBaseContext(), userClassResults, true, false);
            nUsers.setAdapter(soundAdapter);


        }





        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUserFromFile();;
                addSoundsFromFile();
                fixPhotoFilesSad();
                Snackbar.make(view, "Replace with add users", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
        realm = null;

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


            }



            @Override
            public void onClick(View v) {
                thisUser = realmResults.get(getAdapterPosition());
                thisUserPhotoString = BloisUserDirPath + "/" + thisUser.getUserPhoto();
                thisUserUri = Uri.parse(thisUserPhotoString);
                Log.e("myApp :: ", " onclick " + getAdapterPosition() );
                if (v instanceof ImageView){
                    Log.e("myApp ", "onplay inside onclick " + getAdapterPosition() );

                    thisUserPhotoString = BloisUserDirPath + "/" + thisUser.getUserPhoto();

                    thisUserUri = Uri.parse(thisUserPhotoString);

                    Intent intent = new Intent(getApplicationContext(), UserSoundsActivity.class);
                    intent.putExtra("userID", thisUser.getUserID());
                    startActivity(intent);



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

            thisSoundImageFilePath = BloisUserDirPath + "/" + user.getUserPhoto();
            Log.e("myApp :: ", "BloisUserDirPath IN ONBIND " + thisSoundImageFilePath );

            Picasso.with(viewHolder.uImage.getContext())
                    .load(new File(thisSoundImageFilePath))
                    .resize(120, 120)
                    .centerCrop()
                    .placeholder(R.drawable.sound_defaul_image)
                    .into(viewHolder.uImage);

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
//        List userList = csvFileUser.read();
//
//        int len = userList.size();
//        Log.w("myApp :: ", "Value length of list " + len );
//        for (int i = 0; i < len; ++i) {
//            String[] thisUserRow = userList.get(i);
//
//            Log.w("myApp :: ", "Value  row 0 " + userList[i][1]);
//       }
//
////        Log.w("myApp :: ", "Value  row 0 " + userList[0][1]);
////        for (int i = 0; i < len; ++i) {
////            Log.w("myApp :: ", "Value  row 0 " + userList[i][1]);
////        }

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

                int len = row.length;
                Log.w("myApp :: ", "Value  of csvLine " + csvLine );
                Log.w("myApp :: ", "Value length of row " + len );
                Sound newSound = realm.where(Sound.class).equalTo("soundID", row[0]).findFirst();
                User newUser  = realm.where(User.class).equalTo("userID", row[6]).findFirst();
                Quadrant newQuadrant = realm.where(Quadrant.class).equalTo("quadID", row[5]).findFirst();

                if ((newQuadrant != null ) && (newUser != null ) && (newSound == null) ){

//                   0 soundID| 1 soundName| 2soundAbout| 3soundFile| 4soundPhoto|5 soundQuadrant|
//                   6 soundUserID| 7 soundUserName| 8 soundUserBio| 9 soundUserPhoto

                    Log.w("myApp :: ", "Value length of row 0 " + row[0] );
                    Log.w("myApp :: ", "Value length of row 1 " + row[1] );
                    Log.w("myApp :: ", "Value length of row 2 " + row[2] );
                    Log.w("myApp :: ", "Value length of row 3 " + row[3] );
                    Log.w("myApp :: ", "Value length of row 4 " + row[4] );
                    Log.w("myApp :: ", "Value length of row 5 " + row[5] );
                    Log.w("myApp :: ", "Value length of row 5 " + row[6] );
                    realm.beginTransaction();
                    Sound newSound1 = realm.createObject(Sound.class, row[0]);
                    newSound1.setSoundName(row[1]);
                    newSound1.setSoundDesc(row[2]);
                    newSound1.setSoundFile(row[3]);
                    newSound1.setSoundPhoto(row[4]);
                    newSound1.setTimeCreated(getCurrDateString());
                    newSound1.setLocalizeMedia(true);
                    newSound1.setCreatedByPrimaryUser(false);
                    newSound1.setSoundLikes(10);

                    newUser.getUserSounds().add(newSound1);
                    newQuadrant.getQuadSounds().add(newSound1);
                    realm.commitTransaction();
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
