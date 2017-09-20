package mapfunctions;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import net.aprille.bloissavoirecouter.AddLocationMapsActivity;

import java.util.List;

/**
 * Created by aprillebestglover on 9/17/17.
 */

public abstract class LinkMarkerLongClickListener implements GoogleMap.OnMarkerDragListener {

    private int previousIndex = -1;

    private Marker cachedMarker = null;
    private LatLng cachedDefaultPostion = null;

    private List<Marker> markerList;
    private List<LatLng> defaultPostions;

    public LinkMarkerLongClickListener(Marker marker){

        marker.setDraggable(true);
//        this.markerList = new ArrayList<>(markerList);
//        
//        defaultPostions = new ArrayList<>(markerList.size());
//        for (Marker marker : markerList) {
//            defaultPostions.add(marker.getPosition());
//            marker.setDraggable(true);
//        }
    }

    public LinkMarkerLongClickListener(AddLocationMapsActivity addLocationMapsActivity) {
    }

    public abstract void onLongClickListener(Marker marker);

    @Override
    public void onMarkerDragStart(Marker marker) {
        onLongClickListener(marker);
        setDefaultPostion(markerList.indexOf(marker));
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        setDefaultPostion(markerList.indexOf(marker));
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        setDefaultPostion(markerList.indexOf(marker));
    }


    private void setDefaultPostion(int markerIndex) {
        if(previousIndex == -1 || previousIndex != markerIndex){
            cachedMarker = markerList.get(markerIndex);
            cachedDefaultPostion = defaultPostions.get(markerIndex);
            previousIndex = markerIndex;
        }
        cachedMarker.setPosition(cachedDefaultPostion);
    }
}
