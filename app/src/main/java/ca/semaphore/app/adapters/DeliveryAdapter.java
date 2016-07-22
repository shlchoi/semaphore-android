package ca.semaphore.app.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ca.semaphore.app.R;
import ca.semaphore.app.models.Delivery;
import ca.semaphore.app.transformers.DeliveryTransformer;
import ca.semaphore.app.viewholders.DeliveryViewHolder;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryViewHolder> {

    private Context context;
    private List<Delivery> deliveries;

    public DeliveryAdapter(@NonNull Context context) {
        this.context = context;
        deliveries = new ArrayList<>();
    }

    @Override
    public DeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history,
                                                                     parent,
                                                                     false);
        return new DeliveryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position) {
        DeliveryTransformer.toViewModel(context, deliveries.get(position)).bind(holder);
    }

    @Override
    public void onViewRecycled(@NonNull DeliveryViewHolder holder) {
        super.onViewRecycled(holder);
        holder.init();
    }

    @Override
    public int getItemCount() {
        return deliveries.size();
    }

    public void setDeliveries(@NonNull List<Delivery> deliveries) {
        this.deliveries = deliveries;
        notifyDataSetChanged();
    }
}
