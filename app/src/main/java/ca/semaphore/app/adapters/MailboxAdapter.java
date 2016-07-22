package ca.semaphore.app.adapters;

import android.content.Context;
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

    private Context mContext;
    private List<Mailbox> mMailboxList;

    public MailboxAdapter(Context context) {
        mContext = context;
        mMailboxList = new ArrayList<>();
        Mailbox all = new Mailbox("", "All Mailboxes", "");
        mMailboxList.add(all);
    }

    @Override
    public int getCount() {
        return mMailboxList.size();
    }

    @Override
    public Mailbox getItem(int i) {
        return mMailboxList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addItem(Mailbox mailbox) {
        if (!mMailboxList.contains(mailbox)) {
            mMailboxList.add(mailbox);
            notifyDataSetChanged();
        }
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !TextUtils.equals(view.getTag().toString(), "DROPDOWN")) {
            view = ViewUtils.inflate(R.layout.item_actionbar_spinner, parent, false);
            view.setTag("DROPDOWN");
        }

        TextView textView = (TextView) view;
        textView.setText(getItem(position).getName());

        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !TextUtils.equals(view.getTag().toString(), "NON_DROPDOWN")) {
            view = ViewUtils.inflate(R.layout.view_actionbar_spinner, parent, false);
            view.setTag("NON_DROPDOWN");
        }
        TextView textView = (TextView) view;
        textView.setText(getItem(position).getName());
        return view;
    }
}