package net.aprille.bloissavoirecouter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
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

import java.io.File;
import java.io.IOException;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import models.Quadrant;
import models.Sound;
import models.User;

public class SectorSoundsActivity extends AppCompatActivity {

    int sector;
    String sectorId;
    String sectorTitle;
    String my_image;
    Realm realm;
    RealmConfiguration nRealmConfig;
    RealmRecyclerView nSounds;
    RealmConfiguration config;

    Quadrant quadrant;

    int num_of_Sounds;


    RealmResults<Sound> sectoQuadSound;
    RealmList<Sound> sectorQuadSoundList;

    RealmResults<Sound> sounds;
    RealmResults<User> users;
    Sound newSound;
    User addUser;
    Quadrant newQuadrant;
    MediaPlayer mediaPlayer;
    Quadrant sectorQuadrant;

    public String DirectoryFinal;
    public File BloisUserDir;
    public File BloisSoundDir;
    public File BloisDir;
    public String BloisUserDirPath;
    public String BloisSoundDirPath;
    public String thisSoundImageFilePath;
    public String thisIconThumbFilePath;
    boolean isLandscape;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sector_sounds);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nSounds = (RealmRecyclerView) findViewById(R.id.sound_realm_recycler_view);

//      get intent
        sector = 16;
        sectorId = "16";
        Intent i = getIntent();
        Bundle extras = i.getExtras();

        sector = extras.getInt("sectorNum");
        sectorId = String.valueOf(sector);
        Log.w("myApp", sectorId);

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


        Quadrant sectorQuadrant = realm.where(Quadrant.class).equalTo("quadID", sectorId).findFirst();



        sectorQuadSoundList = sectorQuadrant.getQuadSounds();

        sectoQuadSound =  sectorQuadSoundList.where().findAll();


        sounds = realm.where(Sound.class).findAll();
        users = realm.where(User.class).findAll();

        if (sectoQuadSound != null) {
            num_of_Sounds = sectoQuadSound.size();
            Log.w("myApp", "num_of_Sounds "+ String.valueOf(num_of_Sounds));
            Log.w("myApp", "sectorQuadSoundList "+ String.valueOf(sectorQuadSoundList.size()));
            Log.w("myApp", "num of users for realm "+ String.valueOf(users.size()));
            Log.w("myApp", "num_of_Sounds for all sounds "+ String.valueOf(sounds.size()));


        }


        if (sectorQuadrant != null) {
                sectorTitle = sectorQuadrant.getQuadTitle();
                TextView tvSector = (TextView) findViewById(R.id.tVSectorInfo);
                tvSector.setText(sectorQuadrant.getQuadTitle());
                TextView descTextView = (TextView) findViewById(R.id.tVSectorDescr);
                descTextView.setText(sectorQuadrant.getQuadDesc());
                ImageView quadImageView = (ImageView) findViewById(R.id.sectorImageView);



                my_image = sectorQuadrant.getQuadPhoto();
                Log.w("myApp", my_image);
//                num_of_Sounds = sectorQuadrant.quadSounds().count();
                int id = getResources().getIdentifier("net.aprille.fixingconstraintview:drawable/" + my_image, null, null);



//set the image to the imageView
                quadImageView.setImageResource(id);

            } else {
                sectorTitle = "here is something from the else clause...";
                TextView tvSector = (TextView) findViewById(R.id.tVSectorInfo);
                tvSector.setText(sectorTitle);
            }



//

//       my edits for showing sector number



//       adding and

        if (sectoQuadSound != null) {
            mediaPlayer = new MediaPlayer();
            SoundRecyclerViewAdapter soundAdapter = new SoundRecyclerViewAdapter(getBaseContext(), sectoQuadSound, true, false);
            nSounds.setAdapter(soundAdapter);

        }




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

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
        realm = null;
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    public interface IMyViewHolderClicks {
        public void onLike(View caller);
        public void onPlay(ImageView callerImage);
    }

    public class SoundRecyclerViewAdapter extends RealmBasedRecyclerViewAdapter<
                Sound, SoundRecyclerViewAdapter.ViewHolder> {
        Sound thisSound;

        Uri thisSoundUri;
        String thisSoundFileString;

        public SoundRecyclerViewAdapter(
                Context context,
                RealmResults<Sound> realmResults,
                boolean automaticUpdate,
                boolean animateIdType ) {
            super(context, realmResults, automaticUpdate, animateIdType);
        }

        public class ViewHolder extends RealmViewHolder implements View.OnClickListener {

            private ImageView mImage;
            private ImageView mIconLike;
            private ImageButton mButton_likes;
            private TextView mTitle;
            private TextView mLikes;
            private LinearLayout mLikePress;
            private ImageButton mButton_plays;
            File thisSoundFileNamePath;
            public IMyViewHolderClicks mListener;


            public ViewHolder(LinearLayout container) {
                super(container);

                Log.e("myApp ", "ViewHolder "   );
                mImage = (ImageView) container.findViewById(R.id.sound_image);
                this.mTitle = (TextView) container.findViewById(R.id.sound_title);
                mIconLike = (ImageView) container.findViewById(R.id.icon_likes);
                mLikePress = (LinearLayout)  container.findViewById(R.id.likes_press);
                mLikes = (TextView) container.findViewById(R.id.sound_likes);
                mImage.setOnClickListener(this);
                mLikePress.setOnClickListener(this);

            }



            @Override
            public void onClick(View v) {
                thisSound = realmResults.get(getAdapterPosition());
                thisSoundFileString = BloisSoundDirPath + "/" + thisSound.getSoundFile();
                thisSoundUri = Uri.parse(thisSoundFileString);
                Log.e("myApp :: ", " onclick " + getAdapterPosition() );
                if (v instanceof ImageView){
                    Log.e("myApp ", "onplay inside onclick " + getAdapterPosition() );

                    thisSoundFileString = BloisSoundDirPath + "/" + thisSound.getSoundFile();

                    thisSoundUri = Uri.parse(thisSoundFileString);
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }

                    try {
                        mediaPlayer.setDataSource(getContext(), thisSoundUri);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();

                    }

                    mediaPlayer.start();

                    Log.e("myApp", "onplay inside onclick " + thisSound.getSoundName()) ;
                    //           mListener.onPlay((ImageView)v);
                } else {
                    Log.e("myApp: ", "onlike inside onclick " +""  );

 //                   Sound realmSound = realm.where(Sound.class).equalTo("SoundID", thisSound.getSoundID()).findFirst();


                    realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    thisSound.setSoundLikes(thisSound.getSoundLikes()+1);
                    realm.commitTransaction();
                    Log.e("myApp:: ", "onlike inside onclick " + thisSound.getSoundLikes() );

                }
            }



        }

        // The Viewholder which we inflate here the layout for items in recycleview here note_item

        @Override
        public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
            Log.e("myApp:: ", "onCreateRealmViewHolder " );
            View v = inflater.inflate(R.layout.grid_item_view, viewGroup, false);

            return new ViewHolder((LinearLayout) v);
        }

        @Override
        public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {
            final Sound sound = realmResults.get(position);

            thisSoundImageFilePath = BloisSoundDirPath + "/" + sound.getSoundPhoto();
            Log.e("myApp :: ", "BloisSoundDirPath IN ONBIND " + thisSoundImageFilePath );
            Picasso.with(viewHolder.mImage.getContext())
                    .load(new File(thisSoundImageFilePath))
                    .resize(120, 120)
                    .centerCrop()
                    .placeholder(R.drawable.people_placeholder)
                    .into(viewHolder.mImage);

            viewHolder.mTitle.setText(sound.getSoundName());
            thisIconThumbFilePath = BloisDir + "/ic_thump_up.png";
            Picasso.with(viewHolder.mIconLike.getContext())
                    .load(new File(thisIconThumbFilePath))
                    .resize(24, 24)
                    .into(viewHolder.mIconLike);
            viewHolder.mLikes.setText(String.valueOf(sound.getSoundLikes()));


        }
    }



}
