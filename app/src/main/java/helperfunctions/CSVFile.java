package helperfunctions;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aprillebestglover on 8/21/17.
 */

public class CSVFile {
    InputStream inputStream;

    public CSVFile(InputStream inputStream){
        this.inputStream = inputStream;
    }

    public List read(){
        List resultList = new ArrayList();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split("\\|");

                Log.w("myApp :: ", "csvLine  " + csvLine);
                resultList.add(row);

//              print the row data
                String printItem = "";

                int len = row.length;
                Log.w("myApp :: ", "Value length of row " + len );

                for (int i = 0; i < len; ++i) {
                    Log.w("myApp :: ", "Value  row 0 " + row[i]);
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
        return resultList;
    }
}