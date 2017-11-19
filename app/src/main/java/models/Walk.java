package models;

import io.realm.RealmList;
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

    private Double walkDistance;

    private boolean accessiable;

    private boolean loop;

    private boolean localizedMedia;

    private int numPlacesInWalk;

    private RealmList<OrderedWalkSound> orderedWalkSounds;




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

    public Double getWalkDistance() {
        return walkDistance;
    }

    public void setWalkDistance(Double walkDistance) {
        this.walkDistance = walkDistance;
    }

    public boolean isAccessiable() {
        return accessiable;
    }

    public void setAccessiable(boolean accessiable) {
        this.accessiable = accessiable;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isLocalizedMedia() {
        return localizedMedia;
    }

    public void setLocalizedMedia(boolean localizedMedia) {
        this.localizedMedia = localizedMedia;
    }

    public int getNumPlacesInWalk() {
        return numPlacesInWalk;
    }

    public void setNumPlacesInWalk(int numPlacesInWalk) {
        this.numPlacesInWalk = numPlacesInWalk;
    }

    public RealmList<OrderedWalkSound> getOrderedWalkSounds() {
        return orderedWalkSounds;
    }

    public void setOrderedWalkSounds(RealmList<OrderedWalkSound> orderedWalkSounds) {
        this.orderedWalkSounds = orderedWalkSounds;
    }
}
