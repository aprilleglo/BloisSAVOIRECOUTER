package models;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by aprillebestglover on 10/6/17.
 */

public class Walk extends RealmObject {
    @PrimaryKey
    private String walkID;

    @Index
    private String walkName;

    private String walkDesc;

    private String walkPhoto;

    private String walkPhotoDesc;

    private String walkTime;

    private String walkDistance;

    private boolean accessiable;

    private boolean loop;

    private boolean localizedMedia;

    private int numPlacesInWalk;




    public String getWalkID() {
        return walkID;
    }

    public void setWalkID(String walkID) {
        this.walkID = walkID;
    }

    public String getWalkName() {
        return walkName;
    }

    public void setWalkName(String walkName) {
        this.walkName = walkName;
    }

    public String getWalkDesc() {
        return walkDesc;
    }

    public void setWalkDesc(String walkDesc) {
        this.walkDesc = walkDesc;
    }

    public String getWalkPhoto() {
        return walkPhoto;
    }

    public void setWalkPhoto(String walkPhoto) {
        this.walkPhoto = walkPhoto;
    }

    public String getWalkPhotoDesc() {
        return walkPhotoDesc;
    }

    public void setWalkPhotoDesc(String walkPhotoDesc) {
        this.walkPhotoDesc = walkPhotoDesc;
    }

    public String getWalkTime() {
        return walkTime;
    }

    public void setWalkTime(String walkTime) {
        this.walkTime = walkTime;
    }

    public String getWalkDistance() {
        return walkDistance;
    }

    public void setWalkDistance(String walkDistance) {
        this.walkDistance = walkDistance;
    }





}
