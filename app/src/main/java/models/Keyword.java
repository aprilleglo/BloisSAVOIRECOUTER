package models;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by aprillebestglover on 6/20/17.
 */
@RealmClass
public class Keyword extends RealmObject {

    @PrimaryKey
    private String keyID;

    @Index
    private String keyName;

    //  getters and setters

    public String getKeyID() {
        return keyID;
    }

    public void setKeyID(String keyID) {
        this.keyID = keyID;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }


}

