package ca.semaphore.app.viewholders;

import android.view.View;
import android.widget.TextView;

import net.cachapa.expandablelayout.ExpandableLayout;

import butterknife.BindView;
import ca.semaphore.app.R;
import ca.semaphore.app.utils.ViewUtils;

public class DeliveryViewHolder extends BaseViewHolder {

    @BindView(R.id.date_text)
    public TextView mDateTextView;

    @BindView(R.id.items_text)
    public TextView mItemsTextView;

    @BindView(R.id.main_letters)
    public TextView mLettersTextView;

    @BindView(R.id.main_magazines)
    public TextView mMagazinesTextView;

    @BindView(R.id.main_newspapers)
    public TextView mNewspapersTextView;

    @BindView(R.id.main_parcels)
    public TextView mParcelsTextView;

    @BindView(R.id.items_collapsible)
    public ExpandableLayout mExpandableLinearLayout;

    public DeliveryViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void init() {
        mExpandableLinearLayout.collapse(false);
        mItemsTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand, 0);
        mDateTextView.setOnClickListener(null);
        ViewUtils.setTextAndUpdateVisibility(mDateTextView, null);
        ViewUtils.setTextAndUpdateVisibility(mItemsTextView, null);
        ViewUtils.setTextAndUpdateVisibility(mLettersTextView, null);
        ViewUtils.setTextAndUpdateVisibility(mMagazinesTextView, null);
        ViewUtils.setTextAndUpdateVisibility(mNewspapersTextView, null);
        ViewUtils.setTextAndUpdateVisibility(mParcelsTextView, null);
    }
}
