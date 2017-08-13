package net.aprille.bloissavoirecouter;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by aprillebestglover on 8/8/17.
 */

public class myApplication extends Application {
    public void onCreate() {
        super.onCreate();
        // Initialize Realm. Should only be done once when the application starts.
        Realm.init(this);

    }

}
