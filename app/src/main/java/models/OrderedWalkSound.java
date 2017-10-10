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
    private String walkSoundID;

    @Index
    private int orderNumberInWalk;

    @LinkingObjects("orderedWalkSounds")
    private final RealmResults<Sound> soundOrderedWalk = null;




}
