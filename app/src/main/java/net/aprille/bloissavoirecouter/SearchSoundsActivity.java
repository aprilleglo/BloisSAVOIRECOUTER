package net.aprille.bloissavoirecouter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;

import co.moonmonkeylabs.realmsearchview.RealmSearchAdapter;
import co.moonmonkeylabs.realmsearchview.RealmSearchView;
import co.moonmonkeylabs.realmsearchview.RealmSearchViewHolder;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import models.Sound;
import models.SoundSearchItemView;

public class SearchSoundsActivity extends AppCompatActivity {

    public String DirectoryFinal;
    public File BloisUserDir;
    public File BloisSoundDir;
    public File BloisDir;
    public String BloisUserDirPath;
    public String BloisSoundDirPath;
    public String thisSoundImageFilePath;
    public String thisIconThumbFilePath;
    boolean isLandscape;

    MediaPlayer mediaPlayer;

    final Context context = this;

    public String userPrimaryThumbnail = "myimage.png";
    private RealmSearchView realmSearchView;
    private SoundSearchRecyclerViewAdapter adapter;
    private Realm realm;

    RealmResults<Sound> soundSearchClassResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sounds);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        resetRealm();

        // set up media player before inflating recycleview

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //Do the work after completion of audio
                mediaPlayer.reset();
            }
        });

        realmSearchView = (RealmSearchView) findViewById(R.id.sound_search_view);

        adapter = new SoundSearchRecyclerViewAdapter(this, realm, "soundSearchText");
        realmSearchView.setAdapter(adapter);



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

    private void resetRealm() {

        try {
            realm = Realm.getDefaultInstance();
        } catch (IllegalStateException fuckYouTooAndroid) {
            Realm.init(getApplicationContext());
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(config);
            realm = Realm.getDefaultInstance();
        }
    }

    public class SoundSearchRecyclerViewAdapter extends RealmSearchAdapter<
                Sound, SoundSearchRecyclerViewAdapter.ViewHolder> {


        public SoundSearchRecyclerViewAdapter(
                Context context,
                Realm realm,
                String soundName) {
            super(context, realm, soundName);
        }



        public class ViewHolder extends RealmSearchViewHolder {

            private SoundSearchItemView soundSearchItemView;


            public ViewHolder(FrameLayout container, TextView footerTextView) {
                super(container, footerTextView);
            }

            public ViewHolder(SoundSearchItemView soundSearchItemView) {
                super(soundSearchItemView);
                this.soundSearchItemView = soundSearchItemView;


            }
        }

        // The Viewholder which we inflate here the layout for items in recycleview here note_item



        @Override
        public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
            ViewHolder vh = new ViewHolder(new SoundSearchItemView(viewGroup.getContext()));
            return vh;
        }


        @Override
        public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {
            final Sound thisSound = realmResults.get(position);
            viewHolder.soundSearchItemView.bind(thisSound);
            viewHolder.itemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("myApp", "onplay inside onclick " + thisSound.getSoundName()) ;

                            Intent intent = new Intent(v.getContext(), ZDetailSoundActivity.class);
                            Bundle extras = new Bundle();
                            extras.putString("callingtype", "SEARCH");
                            extras.putString("callingId", "Search");
                            extras.putString("soundID", thisSound.getSoundID());
                            intent.putExtras(extras);
                            startActivity(intent);

                        }
                    }
            );


            Log.e("myApp:: ", "onBindRealmViewHolder name " + thisSound.getSoundName());


        }

        @Override
        public ViewHolder onCreateFooterViewHolder(ViewGroup viewGroup) {
            View v = inflater.inflate(R.layout.footer_view, viewGroup, false);
            return new ViewHolder(
                    (FrameLayout) v,
                    (TextView) v.findViewById(R.id.footer_text_view));
        }

        @Override
        public void onBindFooterViewHolder(ViewHolder holder, final int position) {
            super.onBindFooterViewHolder(holder, position);
            holder.itemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Log.e("myApp", "Item clicked: " + position);
                        }
                    }
            );
        }


    }

}
