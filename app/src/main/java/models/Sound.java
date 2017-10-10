package models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Index;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by aprillebestglover on 6/19/17.
 */

@RealmClass
public class Sound extends RealmObject {

    @PrimaryKey
    private String soundID;

    @Index
    private String soundName;

    private String soundDesc;

    private String soundFile;

    private String soundPhoto;

    private String soundPhotoDesc;

    public String timeCreated;

    private boolean localizeMedia;

    private boolean createdByPrimaryUser;

    private String soundSearchText;

    @Index
    private int soundLikes = 10;

    @LinkingObjects("userSounds")
    private final RealmResults<User> soundUser = null;

    @LinkingObjects("quadSounds")
    private final RealmResults<Quadrant> soundQuad = null;

    @LinkingObjects("locationSounds")
    private final RealmResults<Location> soundLocation = null;

    private RealmList<Keyword> keywords;

    private RealmList<OrderedWalkSound> orderedWalkSounds;

    //  getters and setters

    public String getSoundID() {
        return soundID;
    }

    public void setSoundID(String soundID) {
        this.soundID = soundID;
    }

    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }

    public String getSoundDesc() {
        return soundDesc;
    }

    public void setSoundDesc(String soundDesc) {
        this.soundDesc = soundDesc;
    }

    public String getSoundFile() {
        return soundFile;
    }

    public void setSoundFile(String soundFile) {
        this.soundFile = soundFile;
    }

    public String getSoundPhoto() {
        return soundPhoto;
    }

    public void setSoundPhoto(String soundPhoto)  {
        this.soundPhoto = soundPhoto;
    }

    public String getSoundPhotoDesc() {
        return soundPhotoDesc;
    }

    public void setSoundPhotoDesc(String soundPhotoDesc) {
        this.soundPhotoDesc = soundPhotoDesc;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public boolean isCreatedByPrimaryUser() {
        return createdByPrimaryUser;
    }

    public void setCreatedByPrimaryUser(boolean createdByPrimaryUser) {
        this.createdByPrimaryUser = createdByPrimaryUser;
    }

    public boolean isLocalizeMedia() {
        return localizeMedia;
    }

    public void setLocalizeMedia(boolean localizeMedia) {
        this.localizeMedia = localizeMedia;
    }

    public String getSoundSearchText() {
        return soundSearchText;
    }

    public void setSoundSearchText(String soundSearchText) {
        this.soundSearchText = soundSearchText;
    }

    public int getSoundLikes() {
        return soundLikes;
    }

    public void setSoundLikes(int soundLikes) {
        this.soundLikes = soundLikes;
    }

    public RealmResults<User> getSoundUser() {
        return soundUser;
    }

    public RealmResults<Quadrant> getSoundQuad() {
        return soundQuad;
    }

    public RealmResults<Location> getSoundLocation() {
        return soundLocation;
    }

    public RealmList<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(RealmList<Keyword> keywords) {
        this.keywords = keywords;
    }

    public RealmList<OrderedWalkSound> getOrderedWalkSounds() {
        return orderedWalkSounds;
    }

    public void setOrderedWalkSounds(RealmList<OrderedWalkSound> orderedWalkSounds) {
        this.orderedWalkSounds = orderedWalkSounds;
    }
}
