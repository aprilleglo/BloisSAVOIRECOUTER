package models;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Index;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

/**
 * Created by aprillebestglover on 10/7/17.
 */

public class OrderedWalkSound extends RealmObject {

    @PrimaryKey
    private String orderedWalkID;

    @Index
    private String orderedWalkSoundID;

    @Index
    private String orderedWalkPlaceID;

    private double distanceBeforeStage;

    private double distanceThisStage;

    private double distanceToFinish;

    @Index
    private int orderNumberInWalk;

    @LinkingObjects("orderedWalkSounds")
    private final RealmResults<Sound> soundOrderedWalk = null;



    public String getOrderedWalkID() {
        return orderedWalkID;
    }

    public void setOrderedWalkID(String orderedWalkID) {
        this.orderedWalkID = orderedWalkID;
    }

    public String getOrderedWalkSoundID() {
        return orderedWalkSoundID;
    }

    public void setOrderedWalkSoundID(String orderedWalkSoundID) {
        this.orderedWalkSoundID = orderedWalkSoundID;
    }

    public String getOrderedWalkPlaceID() {
        return orderedWalkPlaceID;
    }

    public void setOrderedWalkPlaceID(String orderedWalkPlaceID) {
        this.orderedWalkPlaceID = orderedWalkPlaceID;
    }

    public int getOrderNumberInWalk() {
        return orderNumberInWalk;
    }

    public void setOrderNumberInWalk(int orderNumberInWalk) {
        this.orderNumberInWalk = orderNumberInWalk;
    }

    public double getDistanceBeforeStage() {
        return distanceBeforeStage;
    }

    public void setDistanceBeforeStage(double distanceBeforeStage) {
        this.distanceBeforeStage = distanceBeforeStage;
    }

    public double getDistanceThisStage() {
        return distanceThisStage;
    }

    public void setDistanceThisStage(double distanceThisStage) {
        this.distanceThisStage = distanceThisStage;
    }

    public double getDistanceToFinish() {
        return distanceToFinish;
    }

    public void setDistanceToFinish(double distanceToFinish) {
        this.distanceToFinish = distanceToFinish;
    }

    public RealmResults<Sound> getSoundOrderedWalk() {
        return soundOrderedWalk;
    }
}
