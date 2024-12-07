package edu.birzeit.jetset.utils;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalPagerAdapter extends RecyclerView.Adapter<VerticalPagerAdapter.ViewHolder> {

    private final int[] layouts;
    private final SparseArray<View> instantiatedViews = new SparseArray<>();

    public VerticalPagerAdapter(int[] layouts) {
        this.layouts = layouts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        instantiatedViews.put(position, holder.itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return layouts[position];
    }

    @Override
    public int getItemCount() {
        return layouts.length;
    }

    public View getViewAtPosition(int position) {
        return instantiatedViews.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

