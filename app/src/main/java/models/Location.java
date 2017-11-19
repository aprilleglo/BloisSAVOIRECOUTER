package models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by aprillebestglover on 6/19/17.
 */

@RealmClass
public class Location extends RealmObject {

    @PrimaryKey
    private String locationID;

    @Index
    private String locationName;

    private String locationAddress;

    private Double longitiude;

    private Double latitude;

    private Quadrant locationQuad;

    private int locationNumSounds;

    private String locationPhotoURL;

    private boolean sharedLocation;

    private String locationSearchText;

    private RealmList<Sound> locationSounds;


    //  getters and setters

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {this.locationAddress = locationAddress;}

    public Double getLongitiude() {
        return longitiude;
    }

    public void setLongitiude(Double longitiude) {
        this.longitiude = longitiude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Quadrant getLocationQuad() {
        return locationQuad;
    }

    public void setLocationQuad(Quadrant locationQuad) {
        this.locationQuad = locationQuad;
    }

    public String getLocationPhotoURL() {
        return locationPhotoURL;
    }

    public void setLocationPhotoURL(String locationPhotoURL) {
        this.locationPhotoURL = locationPhotoURL;
    }

    public boolean isSharedLocation() {
        return sharedLocation;
    }

    public void setSharedLocation(boolean sharedLocation) {
        this.sharedLocation = sharedLocation;
    }

    public String getLocationSearchText() {
        return locationSearchText;
    }

    public void setLocationSearchText(String locationSearchText) {
        this.locationSearchText = locationSearchText;
    }

    public RealmList<Sound> getLocationSounds() { return locationSounds; }

    public void setLocationSounds(RealmList<Sound> locationSounds) {
        this.locationSounds = locationSounds;
    }

    public int getLocationNumSounds() {
        return locationNumSounds;
    }

    public void setLocationNumSounds(int locationNumSounds) {
        this.locationNumSounds = locationNumSounds;
    }

}
