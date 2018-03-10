package com.malikas.greenvision.viewpagercards;

import android.support.v7.widget.CardView;

/**
 * Created by Malik on 2018-03-09.
 */

public interface CardAdapter {

    int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();

}
