/**************************************************************************************************
 * Semaphore - Android
 * Android application accompanying Semaphore
 * See https://shlchoi.github.io/semaphore/ for more information about Semaphore
 *
 * DeliveryViewHolder.java
 * Copyright (C) 2017 Samson H. Choi
 *
 * See https://github.com/shlchoi/semaphore-android/blob/master/LICENSE for license information
 *************************************************************************************************/

package ca.semaphore.app.viewholders;

import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.cachapa.expandablelayout.ExpandableLayout;

import butterknife.BindView;
import ca.semaphore.app.R;
import ca.semaphore.app.R2;
import ca.semaphore.app.utils.ViewUtils;

public class DeliveryViewHolder extends BaseViewHolder {

    @BindView(R2.id.history_timeline)
    public ImageView timeline;

    @BindView(R2.id.history_date)
    public TextView dateText;

    @BindView(R2.id.history_amount)
    public TextView amountText;

    @BindView(R2.id.history_letters)
    public TextView lettersText;

    @BindView(R2.id.history_magazines)
    public TextView magazinesText;

    @BindView(R2.id.history_newspapers)
    public TextView newspapersText;

    @BindView(R2.id.history_parcels)
    public TextView parcelsText;

    @BindView(R2.id.history_expand_icon)
    AppCompatImageView expandCollapseIcon;

    @BindView(R2.id.history_expandable_view)
    ExpandableLayout expandableLayout;

    private AnimatedVectorDrawableCompat expandIcon;
    private AnimatedVectorDrawableCompat collapseIcon;

    public DeliveryViewHolder(View itemView) {
        super(itemView);
        expandIcon = AnimatedVectorDrawableCompat.create(itemView.getContext(), R.drawable.ic_expand_animated);
        collapseIcon = AnimatedVectorDrawableCompat.create(itemView.getContext(), R.drawable.ic_collapse_animated);
    }

    @Override
    public void init() {
        timeline.setImageDrawable(null);
        itemView.setOnClickListener(null);
        ViewUtils.setTextAndUpdateVisibility(dateText, null);
        ViewUtils.setTextAndUpdateVisibility(amountText, null);
        ViewUtils.setTextAndUpdateVisibility(lettersText, null);
        ViewUtils.setTextAndUpdateVisibility(magazinesText, null);
        ViewUtils.setTextAndUpdateVisibility(newspapersText, null);
        ViewUtils.setTextAndUpdateVisibility(parcelsText, null);
        collapseExpandableLayout();
    }

    public boolean isExpanded() {
        return expandableLayout.isExpanded();
    }

    public void toggleExpandableLayout() {
        if (expandableLayout.isExpanded()) {
            expandCollapseIcon.setImageDrawable(collapseIcon);
            collapseIcon.start();
        } else {
            expandCollapseIcon.setImageDrawable(expandIcon);
            expandIcon.start();
        }
        expandableLayout.toggle();
    }

    public void collapseExpandableLayout() {
        expandCollapseIcon.setImageResource(R.drawable.ic_expand);
        expandableLayout.collapse(false);
    }

    public void expandExpandableLayout() {
        expandCollapseIcon.setImageResource(R.drawable.ic_collapse);
        expandableLayout.expand(false);
    }
}
