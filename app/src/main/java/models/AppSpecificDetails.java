package models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by aprillebestglover on 8/8/17.
 */

public class AppSpecificDetails extends RealmObject {

    @PrimaryKey
    private String appID;

    @Index
    private String primaryUserID;

    private String primaryUserName;

    private String primaryUserAbout;

    private String primaryPhoto;

    private int timesOpened;

    private int versionCount;

    private Date firstOpenedApp;

    private Date lastUpdated;

    private String email;

    private String loginNameWP;

    private String passwordWP;

    private boolean readPrivacyStatement;

    private boolean sharing;

    private boolean developing;


//  getters and setters


    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getPrimaryUserID() {
        return primaryUserID;
    }

    public void setPrimaryUserID(String primaryUserID) {
        this.primaryUserID = primaryUserID;
    }

    public String getPrimaryUserName() {
        return primaryUserName;
    }

    public void setPrimaryUserName(String primaryUserName) {
        this.primaryUserName = primaryUserName;
    }

    public String getPrimaryUserAbout() {
        return primaryUserAbout;
    }

    public void setPrimaryUserAbout(String primaryUserAbout) {
        this.primaryUserAbout = primaryUserAbout;
    }

    public String getPrimaryPhoto() {
        return primaryPhoto;
    }

    public void setPrimaryPhoto(String primaryPhoto) {
        this.primaryPhoto = primaryPhoto;
    }

    public int getTimesOpened() {
        return timesOpened;
    }

    public void setTimesOpened(int timesOpened) {
        this.timesOpened = timesOpened;
    }

    public int getVersionCount() {
        return versionCount;
    }

    public void setVersionCount(int versionCount) {
        this.versionCount = versionCount;
    }

    public Date getFirstOpenedApp() {
        return firstOpenedApp;
    }

    public void setFirstOpenedApp(Date firstOpenedApp) {
        this.firstOpenedApp = firstOpenedApp;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLoginNameWP() {
        return loginNameWP;
    }

    public void setLoginNameWP(String loginNameWP) {
        this.loginNameWP = loginNameWP;
    }

    public String getPasswordWP() {
        return passwordWP;
    }

    public void setPasswordWP(String passwordWP) {
        this.passwordWP = passwordWP;
    }

    public boolean isSharing() {
        return sharing;
    }

    public void setSharing(boolean sharing) {
        this.sharing = sharing;
    }

    public boolean isDeveloping() {
        return developing;
    }

    public void setDeveloping(boolean developing) {
        this.developing = developing;
    }

    public boolean isReadPrivacyStatement() {
        return readPrivacyStatement;
    }

    public void setReadPrivacyStatement(boolean readPrivacyStatement) {
        this.readPrivacyStatement = readPrivacyStatement;
    }
}


