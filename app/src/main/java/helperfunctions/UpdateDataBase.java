package helperfunctions;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import models.AppSpecificDetails;
import models.Location;
import models.Quadrant;
import models.Sound;
import models.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static helperfunctions.Util.getCurrDateString;

/**
 * Created by aprillebestglover on 10/12/17.
 */

public class UpdateDataBase extends AsyncTask<Void, Void, String> {

    Realm realm;
    File BloisUserDir;
    File BloisSoundDir;
    File BloisDir;
    String BloisUserDirPath;
    String BloisSoundDirPath;


    public UpdateDataBase(Context context) {

    }

    @Override
    protected void onPreExecute() {
        Log.e("myApp", "onPreExecute() " );
        BloisUserDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/users");
        BloisDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData");


//        not for production because deletes WHOLE REALM IF PROBLEM !!!!



    }

    @Override
    protected String doInBackground(Void... params) {
        Response responseUser;
        String resultUser ="";

        DataInputStream in=null;
        DataOutputStream out=null;
        FileOutputStream fOut=null;


        Log.e("myApp", "doInBackground ");


        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .build();

        Request requestUser = new Request.Builder()
                .url("http://savoir-ecouter.aprille.net/wp-content/uploads/updateUsers.csv")
                .build();

        try {
            responseUser = client.newCall(requestUser).execute();
            //Log.e("myApp", "doInBackground " + responseUser.body().string());
            resultUser = responseUser.body().string();
            responseUser.close();

        } catch (Exception e) {
            Log.e("myApp :: ", "inside catch responseUUser ");
            e.printStackTrace();
        }


        if (resultUser != "") {
            Log.e("myApp", "doInBackground " + resultUser);
            String[] separatedLInes = resultUser.replaceAll("\\r", "\\n")
                    .replaceAll("\\n{2,}", "\\n")
                    .split("\\n");

            Log.e("myApp", "separatedLInes.length " + String.valueOf(separatedLInes.length));
            for (int i = 0; i < separatedLInes.length; i++) {
                Log.e("myApp", "int i " + String.valueOf(i));
                String[] row = separatedLInes[i].split("\\|");
                int len = row.length;
                Log.e("myApp", "Value length of row " + len);
                Log.e("myApp", "row[0] " + row[0]);
                realm = Realm.getDefaultInstance();
                try {
                    // Work with Realm
                    User newUser = realm.where(User.class).equalTo("userID", row[0]).findFirst();
                    if (newUser == null) {

                        realm.beginTransaction();
                        newUser = realm.createObject(User.class, row[0]);

                        Log.e("myApp", "Value of new user name " + row[1]);
                        newUser.setUserName(row[1]);
                        if (row[2] != null) {
                            newUser.setUserDesc(row[2]);
                        }
                        newUser.setTimeJoined(getCurrDateString());
                        newUser.setUserPhoto(row[3]);
                        newUser.setUserPhotoDesc(row[1]);
                        newUser.setPrimaryUserBoolean(false);
                        newUser.setNumUserSounds(0);
                        realm.commitTransaction();

                    } else {
                        Log.e("myApp", "Value of existing user name " + row[1]);
                    }
                } finally {
                    realm.close();
                }


            }
        }

        // place

        try{
            URL remoteFile=new URL("http://savoir-ecouter.aprille.net/wp-content/uploads/updatePlaces.csv");
            URLConnection fileStream=remoteFile.openConnection();
            File BloisUpdateCSV = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/UpdatePlace.csv");
            // Open the input streams for the remote file
            fOut=new FileOutputStream(BloisUpdateCSV);

            // Open the output streams for saving this file on disk
            out=new DataOutputStream(fOut);

            in=new DataInputStream(fileStream.getInputStream());

            // Read the remote on save save the file
            int data;
            while((data=in.read())!=-1){
                fOut.write(data);
            }
            System.out.println("Download of " + "http://savoir-ecouter.aprille.net/wp-content/uploads/updatePlaces.csv" + " is complete." );
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                in.close();
                fOut.flush();
                fOut.close();
            } catch(Exception e){e.printStackTrace();}

        }
        File BloisUpdateCSV = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/UpdatePlace.csv");
        InputStream inputStream = null;

        try {
            inputStream = new BufferedInputStream(new FileInputStream(BloisUpdateCSV));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        CSVFile csvFileSound = new CSVFile(inputStream);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;

            int rowCount = 1;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split("\\|");

                int len = row.length;
                Log.w("myApp :: ", "Value length of place row " + len );
                realm = Realm.getDefaultInstance();
                try {
                    // Work with Realm

                    Location newUPlace = realm.where(Location.class).equalTo("locationID", row[0]).findFirst();
                    if (newUPlace == null) {

                        realm.beginTransaction();
                        newUPlace = realm.createObject(Location.class, row[0]);
                        Log.w("myApp :: ", "after create " + row[1]);
                        newUPlace.setLocationName(row[1]);
                        newUPlace.setLocationAddress(row[2]);
                        newUPlace.setLongitiude(Double.parseDouble(row[4]));
                        newUPlace.setLatitude(Double.parseDouble(row[3]));
                        newUPlace.setLocationNumSounds(0);
                        newUPlace.setSharedLocation(true);
                        newUPlace.setLocationSearchText(row[1] + " " + row[2]);
                        realm.commitTransaction();

                    } else {
                        Log.e("myApp :: ", "place exists " + newUPlace.getLocationName());
                    }
                } finally {
                    realm.close();
                    Log.e("myApp ", "place  finally catch " );
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

        // sounds

        try{
            URL remoteFile=new URL("http://savoir-ecouter.aprille.net/wp-content/uploads/UpdateSounds.csv");
            URLConnection fileStream=remoteFile.openConnection();
            File BloisUpdateSoundCSV = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/UpdateSounds.csv");
            // Open the input streams for the remote file
            fOut=new FileOutputStream(BloisUpdateSoundCSV);

            // Open the output streams for saving this file on disk
            out=new DataOutputStream(fOut);

            in=new DataInputStream(fileStream.getInputStream());

            // Read the remote on save save the file
            int data;
            while((data=in.read())!=-1){
                fOut.write(data);
            }
            System.out.println("Download of " + "http://savoir-ecouter.aprille.net/wp-content/uploads/UpdateSounds.csv" + " is complete." );

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                in.close();
                fOut.flush();
                fOut.close();
            } catch(Exception e){e.printStackTrace();}

        }
        File BloisUpdateSoundCSV = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/UpdateSounds.csv");
        InputStream inputStreamSound = null;

        try {
            inputStreamSound = new BufferedInputStream(new FileInputStream(BloisUpdateSoundCSV));
        } catch (FileNotFoundException e) {
            Log.e("myApp :: ", "inputStreamSound fileNotFoundException " );
            e.printStackTrace();
        }


        BufferedReader readerSound = new BufferedReader(new InputStreamReader(inputStreamSound));
        String locationAddress = " ";
        try {
            String csvLineSound;

            int rowCount = 1;
            while ((csvLineSound = readerSound.readLine()) != null) {

                String[] row = csvLineSound.split("\\|");
                Log.e("myApp :: ", "inside while sound row[0] " + row[0]);
                Log.e("myApp :: ", "inside while sound row[1] " + row[1]);
                Log.e("myApp :: ", "inside while sound row[2] " + row[2]);
                Log.e("myApp :: ", "inside while sound row[0] " + row[3]);
                Log.e("myApp :: ", "inside while sound row[0] " + row[4]);
                Log.e("myApp :: ", "inside while sound row[1] " + row[1]);
                Log.e("myApp :: ", "inside while sound row[2] " + row[2]);
                Log.e("myApp :: ", "inside while sound row[0] " + row[4]);
                int len = row.length;
                Log.e("myApp :: ", "Value length of  Sound row " + len );
                realm = Realm.getDefaultInstance();
                try {
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

                } finally {
                    realm.close();
                    Log.e("myApp ", "sound  finally  " );
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

        //

        realm = Realm.getDefaultInstance();
        try {

            AppSpecificDetails thisAppDetails = realm.where(AppSpecificDetails.class).findFirst();
            realm.beginTransaction();
            Date currentUpdateTime = Calendar.getInstance().getTime();
            thisAppDetails.setLastUpdated(currentUpdateTime);
            realm.commitTransaction();

        } finally {
            realm.close();
            Log.e("myApp ", "app time  finally  " );
        }
        // return


        return null;
    }

        @Override
    protected void onPostExecute(String result) {
        Log.e("myApp", "onPostExecute " );

    }
}
