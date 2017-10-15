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

import io.realm.Realm;
import models.Location;

/**
 * Created by aprillebestglover on 10/13/17.
 */

public class UpdatePlaceDataBase extends AsyncTask<Void, Void, String> {
    Realm realm;

    public UpdatePlaceDataBase(Context context) {

    }

    @Override
    protected void onPreExecute() {
        Log.e("myApp", "onPreExecute() " );
    }

    @Override
    protected String doInBackground(Void... params) {
        DataInputStream in=null;
        DataOutputStream out=null;
        FileOutputStream fOut=null;

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
        InputStream  inputStream = null;

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
                Log.w("myApp :: ", "Value length of row " + len );
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


        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.e("myApp", "onPostExecute " );

    }


}
