package com.balaabirami.abacusandroid.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.model.Level;

import java.util.List;

public class LevelAdapter extends ArrayAdapter<Level> {

    private final Context context;
    private final List<Level> levels;
    LayoutInflater flater;

    public LevelAdapter(Context context, int resouceId, int title_id, List<Level> list) {
        super(context, resouceId, title_id);
        this.levels = list;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return rowview(convertView, position);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return rowview(convertView, position);
    }

    @Override
    public int getCount() {
        return levels.size();
    }

    private View rowview(View convertView, int position) {

        Level rowItem = getItem(position);

        ViewHolder holder;
        View rowview = convertView;
        if (rowview == null) {
            holder = new ViewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = flater.inflate(R.layout.user_type_custom_item, null, false);
            holder.txtTitle = (TextView) rowview.findViewById(R.id.title);
            rowview.setTag(holder);
        } else {
            holder = (ViewHolder) rowview.getTag();
        }
        holder.txtTitle.setText(rowItem.getName());

        return rowview;
    }

    @Nullable
    @Override
    public Level getItem(int position) {
        return levels.get(position);
    }

    private class ViewHolder {
        TextView txtTitle;
    }
}