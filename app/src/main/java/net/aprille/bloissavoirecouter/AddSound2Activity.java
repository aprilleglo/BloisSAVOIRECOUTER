package net.aprille.bloissavoirecouter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import io.realm.Realm;
import models.Location;

import static helperfunctions.Util.getCurrDateString;

public class AddSound2Activity extends AppCompatActivity {

    final Context context = this;
    int ADD_PLACE_REQUEST_CODE = 101;
    String thisPlaceID;
    String REQUEST_PLACE_ID;
    int sector;
    String sectorId;

    Location thisLocation;

    Realm realm;
    ImageView viewSoundImage;

    String thisSoundID;
    String thisSoundName ="";
    String thisSoundDesc;
    String thisSoundFile;
    String thisSoundPhoto;
    String thisSoundPhotoDesc = "";
    String thisTimeCreated;
    boolean thisLocalizeMedia = true;
    boolean thisCreatedByPrimaryUser = true;
    int thisSoundLikes = 15;

    public File BloisSoundDir;

    String thsPrimaryUserKey;

    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    MediaPlayer mediaPlayer ;

    int permissionCheckStorage;
    int permissionCheckCamera;
    int permissionCheckMicrophone;
    int permissionCheckLocation;


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
        setContentView(R.layout.activity_add_sound2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //      get intent

        sector = 16;
        sectorId = "16";
        Intent i = getIntent();
        Bundle extras = i.getExtras();

        sector = extras.getInt("sectorNum");
        sectorId = String.valueOf(sector);
        Log.w("myApp", sectorId);

        //      get intent

        // HIDE VIEWS

        Button btnStopREC=(Button)findViewById(R.id.finshRecordButton);
        btnStopREC.setVisibility(View.INVISIBLE);
        Button bntSaveREC=(Button)findViewById(R.id.saveSoundButton);
        bntSaveREC.setVisibility(View.INVISIBLE);
        TextInputLayout bntEditSaveNameLayout = (TextInputLayout)findViewById(R.id.add_sound_name_input_layout2);
        bntEditSaveNameLayout.setVisibility(View.INVISIBLE);
        TextInputEditText bntEditSaveName =(TextInputEditText)findViewById(R.id.sound_name_imput);
        bntEditSaveName.setVisibility(View.INVISIBLE);
        TextInputLayout bntEditDescLayout = (TextInputLayout)findViewById(R.id.add_sound_desc_input_layout2);
        bntEditDescLayout.setVisibility(View.INVISIBLE);
        TextInputEditText bntEditEditDesc =(TextInputEditText)findViewById(R.id.sound_desc_imput);
        bntEditEditDesc.setVisibility(View.INVISIBLE);

        TextView tvPlaceNameTV =(TextView) findViewById(R.id.tvPlaceNameAddSound2);
        tvPlaceNameTV.setVisibility(View.INVISIBLE);
        TextView tvPlaceAddressTV =(TextView) findViewById(R.id.tvPlaceAddressAddSound2);
        tvPlaceAddressTV.setVisibility(View.INVISIBLE);
        TextView tvPlaceLongLatTV  =(TextView) findViewById(R.id.tvPlaceLongLatAddSound2);
        tvPlaceLongLatTV.setVisibility(View.INVISIBLE);
        ImageButton getPlaceButton = (ImageButton) findViewById(R.id.addSoundPlaceButton2);
        getPlaceButton.setVisibility(View.INVISIBLE);

        ImageView bntSoundImage= (ImageView)findViewById(R.id.addimageSoundButton2);
        bntSoundImage.setVisibility(View.INVISIBLE);
        ImageButton bntSavePhoto=(ImageButton)findViewById(R.id.saveSoundButton);
        bntSavePhoto.setVisibility(View.INVISIBLE);

        // HIDE VIEWS

        BloisSoundDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/sounds");
        if(BloisSoundDir.exists() && BloisSoundDir.isDirectory()) {
            Log.w("myApp", "sounds directory exist"  );
        } else {
            Log.w("myApp", "sound directory does't exist"  );
            BloisSoundDir.mkdirs();

        }

        // setup tempSound
        thisSoundID = getCurrDateString();
        thisSoundDesc = "Je t'aime Blois";
        thisSoundFile = thisSoundID +".m4a";
        thisSoundPhoto = thisSoundID +".jpg";
        thisTimeCreated = thisSoundID;






//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
