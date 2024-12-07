package edu.birzeit.jetset.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import java.util.List;

import edu.birzeit.jetset.R;

public class CustomArrayAdapter extends ArrayAdapter<String> {
    private float fontSize;

    public CustomArrayAdapter(Context context, int resource, List<String> objects, float fontSize) {
        super(context, resource, objects);
        this.fontSize = fontSize;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.text1);
        if (textView != null) {
            textView.setTextSize(fontSize);
            textView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_medium));
            textView.setText(getItem(position));
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.text1);
        if (textView != null) {
            textView.setTextSize(fontSize);
            textView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_medium));
            textView.setText(getItem(position));
            textView.setPadding(16, 4, 16, 4);
        }
        return convertView;
    }
}
