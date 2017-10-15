package net.aprille.bloissavoirecouter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Calendar;

import helperfunctions.CSVFile;
import helperfunctions.Util;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import models.AppSpecificDetails;
import models.Location;
import models.Quadrant;
import models.Sound;
import models.User;

import static helperfunctions.Util.getCurrDateString;

public class StartupActivity extends AppCompatActivity {


    final Context context = this;
    Realm realm;
    ImageView viewImage;
    int permissionCheckStorage;
    int permissionCheckCamera;
    int permissionCheckMicrophone;


    public File BloisUserDir;
    public File BloisSoundDir;
    public File BloisDir;
    public String BloisUserDirPath;
    public String DirectoryFinal;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BloisUserDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/users");
        BloisDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData");


//        not for production because deletes WHOLE REALM IF PROBLEM !!!!

        try {
            realm = Realm.getDefaultInstance();
        } catch (IllegalStateException fuckYouTooAndroid) {
            Realm.init(getApplicationContext());
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(config);
            realm = Realm.getDefaultInstance();
        }

        // Setup Database

        Toast.makeText(this, "@string/init", Toast.LENGTH_LONG).show();

        initializeSectors();
        addUserFromFile();
        addPlaceFromFile();
        Toast.makeText(this, "@string/init", Toast.LENGTH_LONG).show();
        addSoundsFromFile();



        // Assume check permissions for camera and storage
        permissionCheckCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        Log.w("myApp", "permissionCheck " + permissionCheckCamera );

        permissionCheckStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Log.w("myApp", "permissionCheckStorage " + permissionCheckStorage );


//        if (permissionCheckCamera == -1) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
//                REQUEST_CAMERA);
//            permissionCheckCamera = ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.CAMERA);
//            Log.w("myApp", "permissionCheck value after request" + permissionCheckCamera );
//
//        }

        if (permissionCheckStorage == -1) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE);
            permissionCheckStorage = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Log.w("myApp", "permissionCheckStorage value after request" + permissionCheckStorage);
        }

        setContentView(R.layout.activity_startup);
        viewImage = (ImageView)findViewById(R.id.primaryUserImageView);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(realm != null) { // guard against weird low-budget phones
            realm.close();
            realm = null;
        }
    }



    public void buttonClickSave(View v)
    {

        setupPrimaryUser();
        Intent intent = new Intent(getApplicationContext(), PlanActivity.class);
        startActivity(intent);
    }


    public void buttonClickAddPhoto(View v)
    {
        if (permissionCheckCamera == -1) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
            permissionCheckCamera = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA);
            Log.w("myApp", "permissionCheck value after request" + permissionCheckCamera );

        }
        if ((permissionCheckCamera == -1) && (permissionCheckStorage == 0)) {
            selectImageNoCamera();
        } else {
            selectImage();
        }


    }

    private void setupPrimaryUser() {

        AppSpecificDetails newAppSpecificDetails;
        User newPrimaryUser;
        String newPrimaryKey;


        EditText myEditText = (EditText) findViewById(R.id.signup_input_name);
        EditText myAboutEditText = (EditText) findViewById(R.id.signup_input_about);
        if (myEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "You did not enter a value!", Toast.LENGTH_LONG).show();
            return;
        } else {
            newPrimaryKey = getCurrDateString().trim();
            Log.w("myApp", "newPrimaryKey" + newPrimaryKey);
            boolean didSaveUserFile = saveUserImageFile(newPrimaryKey);

            if ( didSaveUserFile ) {

                // saveUserImageFile returned a true and save primary image file
                String userImagefileName = newPrimaryKey +".jpg";

                realm.beginTransaction();
                newAppSpecificDetails = realm.createObject(AppSpecificDetails.class, newPrimaryKey);
                newAppSpecificDetails.setPrimaryUserID(newPrimaryKey);
                newAppSpecificDetails.setPrimaryUserName(myEditText.getText().toString());
                newAppSpecificDetails.setPrimaryUserAbout(myAboutEditText.getText().toString());
                newAppSpecificDetails.setPrimaryPhoto(userImagefileName);

                newPrimaryUser = realm.createObject(User.class, newPrimaryKey);
                newPrimaryUser.setUserName(myEditText.getText().toString());
                newPrimaryUser.setUserDesc(myAboutEditText.getText().toString());
                newPrimaryUser.setTimeJoined(newPrimaryKey);
                newAppSpecificDetails.setLastUpdated(Calendar.getInstance().getTime());
                newPrimaryUser.setUserPhoto(userImagefileName);
                newPrimaryUser.setPrimaryUserBoolean(true);
                realm.commitTransaction();

            } else {
                Log.w("myApp", "newPrimaryKey didn't work" + newPrimaryKey);

            }

        }



        Log.w("myApp", "inside setup Primary User");


    }

    private void selectImage() {

        final String addPhotoDialogTitle = getString(R.string.addPhoto);
        final String takePhotoDialog = getString(R.string.takePhoto);
        final String choosePhotoDialog = getString(R.string.choosePhotoGallery);
        final String cancelDialog = getString(R.string.cancel);

        final CharSequence[] options = { takePhotoDialog, choosePhotoDialog, cancelDialog };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(addPhotoDialogTitle);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(takePhotoDialog ))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                    Uri photoURI = FileProvider.getUriForFile(context,
                            BuildConfig.APPLICATION_ID + ".provider",
                            f);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoURI));
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals(choosePhotoDialog))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                }
                else if (options[item].equals(cancelDialog)) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void selectImageNoCamera() {

        final String addPhotoDialogTitle = getString(R.string.addPhoto);
        final String choosePhotoDialog = getString(R.string.choosePhotoGallery);
        final String cancelDialog = getString(R.string.cancel);

        final CharSequence[] options = { choosePhotoDialog, cancelDialog };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(addPhotoDialogTitle);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(choosePhotoDialog))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                }
                else if (options[item].equals(cancelDialog)) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    viewImage.setImageBitmap(bitmap);

                    String path = Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.w("myApp", "path of image from gallery" + picturePath + "");
                viewImage.setImageBitmap(thumbnail);
            }
        }
    }

    public boolean saveUserImageFile(String imgFileName){
        File BloisUserDir;
        viewImage.buildDrawingCache();
        Bitmap bitmap = viewImage.getDrawingCache();
        Log.w("myApp", "inside saveUserImageFile" + imgFileName);
        BloisUserDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/users");
        if(BloisUserDir.exists() && BloisUserDir.isDirectory()) {
            Log.w("myApp", "directory exist"  );
        } else {
            Log.w("myApp", "directory does't exist"  );
            BloisUserDir.mkdirs();

        }

        Log.w("myApp", "path of image from BloisUserDir " + BloisUserDir );
        String filename = imgFileName + ".jpg";
        Log.w("myApp", "external storage is writable"  );
        if ( Util.isExternalStorageWritable() ) {
            Log.w("myApp", "external storage is writable"  );
        } else {
            Log.w("myApp", "bummer external storage IS NOT writable"  );
        }
        File file = new File(BloisUserDir, filename);
        if(file.exists() ) {
            Log.w("myApp", "file exist"  );
        } else {
            Log.w("myApp", "file  doesn exist"  );
        }
        Log.w("myApp", "AbsolutePath from file " + file.getAbsolutePath() );

        try {
            FileOutputStream fos = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Log.w("myApp", "Success we seem to have written the primary user! "  );
        }
        catch (FileNotFoundException e) {
            Log.w("myApp", "IOException FileNotFoundException" + file.getAbsolutePath() );
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            Log.w("myApp", "IOException catch " + file.getAbsolutePath() );
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Add Data
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
                Log.e("myApp :: ", "Value  of csvLine " + csvLine );
                Log.e("myApp :: ", "Value length of row " + len );
                Sound newSound = realm.where(Sound.class).equalTo("soundID", row[0]).findFirst();

                User newUser  = realm.where(User.class).equalTo("userID", row[9]).findFirst();

                Quadrant newQuadrant = realm.where(Quadrant.class).equalTo("quadID", row[11]).findFirst();


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
                    newSound1.setLocalizeMedia(false);
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
                    String textForSearch = row[1] + " " + row[2] + " " + row[10] + " " + row[13] + " " + newUser.getUserDesc() + " " + locationAddress;
                    newSound1.setSoundSearchText(textForSearch);
                    realm.commitTransaction();
                } else {
                    Log.w("myApp :: ", "uproblem in sound  " + row[9] );
                    if (newUser == null) {
                        Log.e("myApp :: ", "user null!!  " + row[9] );

                    }

                    if (newQuadrant == null) {
                        Log.e("myApp :: ", "Quad is null " + row[11] );
                    }
                    if (newSound != null) {
                        Log.e("myApp :: ", "Sound is NOT null " + row[1] );

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
