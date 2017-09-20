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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import helperfunctions.Util;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import models.AppSpecificDetails;
import models.User;

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
            newPrimaryKey = Util.getCurrDateString().trim();
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




}
