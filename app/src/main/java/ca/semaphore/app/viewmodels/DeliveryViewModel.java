package ca.semaphore.app.viewmodels;

import android.support.annotation.Nullable;
import android.view.View;

import ca.semaphore.app.R;
import ca.semaphore.app.utils.ViewUtils;
import ca.semaphore.app.viewholders.DeliveryViewHolder;

public class DeliveryViewModel {

    @Nullable public String mLettersText;
    @Nullable public String mMagazineText;
    @Nullable public String mNewspapersText;
    @Nullable public String mParcelsText;
    @Nullable public String mDateText;
    @Nullable public String mItemsText;

    public void bind(final DeliveryViewHolder viewHolder) {
        ViewUtils.setTextAndUpdateVisibility(viewHolder.mDateTextView, mDateText);
        ViewUtils.setTextAndUpdateVisibility(viewHolder.mItemsTextView, mItemsText);
        ViewUtils.setTextAndUpdateVisibility(viewHolder.mLettersTextView, mLettersText);
        ViewUtils.setTextAndUpdateVisibility(viewHolder.mMagazinesTextView, mMagazineText);
        ViewUtils.setTextAndUpdateVisibility(viewHolder.mNewspapersTextView, mNewspapersText);
        ViewUtils.setTextAndUpdateVisibility(viewHolder.mParcelsTextView, mParcelsText);
        viewHolder.mItemsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int drawableId;
                if (viewHolder.mExpandableLinearLayout.isExpanded()) {
                    drawableId = R.drawable.ic_expand;
                } else {
                    drawableId = R.drawable.ic_collapse;
                }
                viewHolder.mItemsTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableId, 0);
                viewHolder.mExpandableLinearLayout.toggle();
            }
        });
    }

}
