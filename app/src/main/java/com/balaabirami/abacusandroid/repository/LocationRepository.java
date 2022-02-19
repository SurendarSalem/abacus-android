package com.balaabirami.abacusandroid.repository;

import android.content.Context;

import com.balaabirami.abacusandroid.model.State;
import com.balaabirami.abacusandroid.utils.StateHelper;

import java.util.ArrayList;
import java.util.List;

public class LocationRepository {
    private List<String> states = new ArrayList<>();
    private static LocationRepository locationRepository;
    Context context;

    public LocationRepository(Context context) {
        this.context = context;
    }

    public static LocationRepository newInstance(Context context) {
        if (locationRepository == null) {
            locationRepository = new LocationRepository(context);
        }
        return locationRepository;
    }

    public List<String> getStates() {
        if (states.isEmpty()) {
            states = createStateList(context);
        }
        return states;
    }

    private List<String> createStateList(Context context) {
        return StateHelper.getInstance().getStateNames(context);
    }

    public void setStates(List<String> states) {
        this.states = states;
    }
}
