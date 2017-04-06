/**************************************************************************************************
 * Semaphore - Android
 * Android application accompanying Semaphore
 * See https://shlchoi.github.io/semaphore/ for more information about Semaphore
 *
 * DeliveryViewModel.java
 * Copyright (C) 2017 Samson H. Choi
 *
 * See https://github.com/shlchoi/semaphore-android/blob/master/LICENSE for license information
 *************************************************************************************************/

package ca.semaphore.app.viewmodels;

import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;

import ca.semaphore.app.R;
import ca.semaphore.app.utils.ViewUtils;
import ca.semaphore.app.viewholders.DeliveryViewHolder;

public class DeliveryViewModel {

    @IntRange(from = 0) public int position;
    @IntRange(from = 0) public int numItems;
    public boolean isCategorising;
    @Nullable public String mLettersText;
    @Nullable public String mMagazineText;
    @Nullable public String mNewspapersText;
    @Nullable public String mParcelsText;
    @Nullable public String mDateText;
    @Nullable public String mItemsText;

    public void bind(final DeliveryViewHolder viewHolder) {
        bind(viewHolder, false);
    }

    public void bind(final DeliveryViewHolder viewHolder, final boolean expanded) {
        @DrawableRes int drawableResource;
        if (position > 0) {
            drawableResource = R.color.colorPrimary;
        } else {
            drawableResource = R.drawable.ic_timeline_start;
        }
        viewHolder.timeline.setImageDrawable(ResourcesCompat.getDrawable(viewHolder.timeline.getResources(),
                                                                         drawableResource,
                                                                         null));
        ViewUtils.setTextAndUpdateVisibility(viewHolder.dateText, mDateText);
        ViewUtils.setTextAndUpdateVisibility(viewHolder.amountText, mItemsText);
        ViewUtils.setTextAndUpdateVisibility(viewHolder.lettersText, mLettersText);
        ViewUtils.setTextAndUpdateVisibility(viewHolder.magazinesText, mMagazineText);
        ViewUtils.setTextAndUpdateVisibility(viewHolder.newspapersText, mNewspapersText);
        ViewUtils.setTextAndUpdateVisibility(viewHolder.parcelsText, mParcelsText);
        viewHolder.itemView.setOnClickListener(v -> viewHolder.toggleExpandableLayout());
        if (expanded) {
            viewHolder.expandExpandableLayout();
        } else {
            viewHolder.collapseExpandableLayout();
        }
    }

}
