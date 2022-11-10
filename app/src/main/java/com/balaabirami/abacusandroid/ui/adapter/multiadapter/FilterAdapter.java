package com.balaabirami.abacusandroid.ui.adapter.multiadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.model.Book;
import com.balaabirami.abacusandroid.model.Level;
import com.balaabirami.abacusandroid.model.State;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter<Type> extends ArrayAdapter<Type> {
    private final Context mContext;
    private final ArrayList<Type> objects;
    private final ArrayList<Type> selectedObjects = new ArrayList<>();
    private boolean isFromView = false;

    public FilterAdapter(Context context, int resource, List<Type> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.objects = (ArrayList<Type>) objects;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.multi_choice_spiiner_item, null);
            holder = new ViewHolder();
            holder.mTextView = (TextView) convertView
                    .findViewById(R.id.text);
            holder.mCheckBox = (CheckBox) convertView
                    .findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        bindData(objects.get(position), holder, position);
        return convertView;
    }

    private void bindData(Type obj, ViewHolder holder, int position) {
        if ((position == 0)) {
            holder.mCheckBox.setVisibility(View.INVISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        }
        if (obj instanceof State) {
            holder.mTextView.setText(((State) obj).getName());
            isFromView = true;
            holder.mCheckBox.setChecked(((State) obj).isSelected());
            isFromView = false;
            holder.mCheckBox.setTag(position);
            holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isFromView) {
                    ((State) obj).setSelected(isChecked);
                    if (isChecked) {
                        selectedObjects.add(obj);
                    } else {
                        selectedObjects.remove(obj);
                    }
                }
            });
        } else if (obj instanceof User) {
            holder.mTextView.setText(((User) obj).getName());
            isFromView = true;
            holder.mCheckBox.setChecked(((User) obj).isSelected());
            isFromView = false;
            holder.mCheckBox.setTag(position);
            holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isFromView) {
                    ((User) obj).setSelected(isChecked);
                    if (isChecked) {
                        selectedObjects.add(obj);
                    } else {
                        selectedObjects.remove(obj);
                    }
                }
            });
        } else if (obj instanceof Stock) {
            holder.mTextView.setText(((Stock) obj).getName());
            isFromView = true;
            holder.mCheckBox.setChecked(((Stock) obj).isSelected());
            isFromView = false;
            holder.mCheckBox.setTag(position);
            holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isFromView) {
                    ((Stock) obj).setSelected(isChecked);
                    if (isChecked) {
                        selectedObjects.add(obj);
                    } else {
                        selectedObjects.remove(obj);
                    }
                }
            });
        } else if (obj instanceof Student) {
            holder.mTextView.setText(((Student) obj).getName());
            isFromView = true;
            holder.mCheckBox.setChecked(((Student) obj).isSelected());
            isFromView = false;
            holder.mCheckBox.setTag(position);
            holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isFromView) {
                    ((Student) obj).setSelected(isChecked);
                    if (isChecked) {
                        selectedObjects.add(obj);
                    } else {
                        selectedObjects.remove(obj);
                    }
                }
            });
        } else if (obj instanceof Level) {
            holder.mTextView.setText(((Level) obj).getName());
            isFromView = true;
            holder.mCheckBox.setChecked(((Level) obj).isSelected());
            isFromView = false;
            holder.mCheckBox.setTag(position);
            holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isFromView) {
                    ((Level) obj).setSelected(isChecked);
                    if (isChecked) {
                        selectedObjects.add(obj);
                    } else {
                        selectedObjects.remove(obj);
                    }
                }
            });
        } else if (obj instanceof Book) {
            holder.mTextView.setText(((Book) obj).getName());
            isFromView = true;
            holder.mCheckBox.setChecked(((Book) obj).isSelected());
            isFromView = false;
            holder.mCheckBox.setTag(position);
            holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isFromView) {
                    ((Book) obj).setSelected(isChecked);
                    if (isChecked) {
                        selectedObjects.add(obj);
                    } else {
                        selectedObjects.remove(obj);
                    }
                }
            });
        }
    }

    public void clearAll() {
        for (Type obj : objects) {
            if (obj instanceof State) {
                ((State) obj).setSelected(false);
            } else if (obj instanceof User) {
                ((User) obj).setSelected(false);
            } else if (obj instanceof Level) {
                ((Level) obj).setSelected(false);
            }
        }
        notifyDataSetChanged();
    }

    private class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }

    public ArrayList<Type> getSelectedObjects() {
        return selectedObjects;
    }
}
