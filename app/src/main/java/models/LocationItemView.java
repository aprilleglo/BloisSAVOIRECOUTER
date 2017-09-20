package models;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.aprille.bloissavoirecouter.R;

import butterknife.ButterKnife;

/**
 * Created by aprillebestglover on 9/19/17.
 */

public class LocationItemView extends LinearLayout {

    public LocationItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.grid_item_view_location, this);
        ButterKnife.bind(this);
    }

    public void bind(Location location) {
        TextView name = (TextView) findViewById(R.id.tvPlaceName);
        name.setText(location.getLocationName());
        TextView address = (TextView) findViewById(R.id.tvPlaceAddressItem);
        address.setText(location.getLocationAddress());
        TextView numberSounds = (TextView) findViewById(R.id.tvPlaceLongLatItem);
        numberSounds.setText("Number of Sounds " + String.valueOf(location.getLocationNumSounds()));
    }


}
