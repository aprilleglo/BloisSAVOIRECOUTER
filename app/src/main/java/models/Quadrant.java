package models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by aprillebestglover on 6/19/17.
 */
@RealmClass
public class Quadrant extends RealmObject {

    @PrimaryKey
    private String quadID;

    private String quadTitle;

    private String quadDesc;

    private String quadPhoto;

    private String quadPhotoDesc;

    private RealmList<Sound> quadSounds;

    //  getters and setters

    public String getQuadID() {
        return quadID;
    }

    public void setQuadID(String quadID) {
        this.quadID = quadID;
    }

    public String getQuadTitle() {
        return quadTitle;
    }

    public void setQuadTitle(String quadTitle) {
        this.quadTitle = quadTitle;
    }

    public String getQuadDesc() {
        return quadDesc;
    }

    public void setQuadDesc(String quadDesc) {
        this.quadDesc = quadDesc;
    }

    public String getQuadPhoto() {
        return quadPhoto;
    }

    public void setQuadPhoto(String quadPhoto) {
        this.quadPhoto = quadPhoto;
    }

    public String getQuadPhotoDesc() {
        return quadPhotoDesc;
    }

    public void setQuadPhotoDesc(String quadPhotoDesc) {
        this.quadPhotoDesc = quadPhotoDesc;
    }

    public RealmList<Sound> getQuadSounds() {return quadSounds; }

    public void setQuadSounds(RealmList<Sound> quadSounds) {
        this.quadSounds = quadSounds;
    }
}
