package models;

import android.content.Context;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.aprille.bloissavoirecouter.R;

import java.io.File;

import butterknife.ButterKnife;

/**
 * Created by aprillebestglover on 9/19/17.
 */

public class SoundSearchItemView extends LinearLayout {

    public SoundSearchItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.grid_item_view_search_sounds, this);
        ButterKnife.bind(this);
    }

    public void bind(Sound sound) {
        TextView name = (TextView) findViewById(R.id.sound_titleSoundSearch);
        name.setText(sound.getSoundName());

        ImageView sImage = (ImageView) findViewById(R.id.sound_imageSoundSearch);
        File BloisSoundDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/sounds");
        String BloisSoundDirPath = BloisSoundDir.toString();
        String thisSoundImageFilePath = BloisSoundDirPath + "/" + sound.getSoundPhoto();
        Picasso.with(sImage.getContext())
                .load(new File(thisSoundImageFilePath))
                .resize(120, 120)
                .centerCrop()
                .placeholder(R.drawable.sound_defaul_image)
                .into(sImage);


    }


}
