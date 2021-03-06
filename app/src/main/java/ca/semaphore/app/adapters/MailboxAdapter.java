/**************************************************************************************************
 * Semaphore - Android
 * Android application accompanying Semaphore
 * See https://shlchoi.github.io/semaphore/ for more information about Semaphore
 *
 * MailboxAdapter.java
 * Copyright (C) 2017 Samson H. Choi
 *
 * See https://github.com/shlchoi/semaphore-android/blob/master/LICENSE for license information
 *************************************************************************************************/

package ca.semaphore.app.adapters;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.semaphore.app.R;
import ca.semaphore.app.models.Mailbox;
import ca.semaphore.app.utils.ViewUtils;

public class MailboxAdapter extends BaseAdapter {

    private List<Mailbox> mMailboxList;

    public MailboxAdapter() {
        mMailboxList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mMailboxList.size();
    }

    @Override
    public Mailbox getItem(@IntRange(from=0) int i) {
        return mMailboxList.get(i);
    }

    @Override
    public long getItemId(@IntRange(from=0) int i) {
        return 0;
    }

    public void addItem(@NonNull Mailbox mailbox) {
        if (!mMailboxList.contains(mailbox)) {
            mMailboxList.add(mailbox);
            notifyDataSetChanged();
        }
    }

    public int getItemPosition(@NonNull String mailboxId) {
        Mailbox mailbox = new Mailbox(mailboxId, "");
        return mMailboxList.indexOf(mailbox);
    }

    @Override
    public View getDropDownView(@IntRange(from=0) int position,
                                @Nullable View view,
                                @Nullable ViewGroup parent) {
        if (view == null || !TextUtils.equals(view.getTag().toString(), "DROPDOWN")) {
            view = ViewUtils.inflate(R.layout.item_actionbar_spinner, parent, false);
            view.setTag("DROPDOWN");
        }

        TextView textView = (TextView) view;
        textView.setText(getItem(position).getName());

        return view;
    }

    @Override
    public View getView(@IntRange(from=0) int position,
                        @Nullable View view,
                        @Nullable ViewGroup parent) {
        if (view == null || !TextUtils.equals(view.getTag().toString(), "NON_DROPDOWN")) {
            view = ViewUtils.inflate(R.layout.view_actionbar_spinner, parent, false);
            view.setTag("NON_DROPDOWN");
        }
        TextView textView = (TextView) view.findViewById(R.id.spinner_text_view);
        textView.setText(getItem(position).getName());
        return view;
    }
}
