package com.balaabirami.abacusandroid.utils;

import android.content.Context;

import com.balaabirami.abacusandroid.model.State;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StateHelper {

    private static StateHelper stateHelper;
    private ArrayList<State> states = new ArrayList<>();

    public static StateHelper getInstance() {
        return stateHelper == null ? new StateHelper() : stateHelper;
    }

    public StateHelper() {
    }

    public ArrayList<State> getStates(Context context) {
        if (states.isEmpty()) {
            try {
                JSONObject obj = new JSONObject(Objects.requireNonNull(loadJSONFromAsset(context)));
                JSONArray jsonStates = obj.getJSONArray("states");
                for (int i = 0; i < jsonStates.length(); i++) {
                    State state = new State();
                    JSONObject jo_inside = jsonStates.getJSONObject(i);
                    String jsonState = jo_inside.getString("state");
                    state.setName(jsonState);
                    JSONArray jsonDistricts = jo_inside.getJSONArray("districts");
                    List<String> districts = new ArrayList<>();
                    for (int j = 0; j < jsonDistricts.length(); j++) {
                        String district = jsonDistricts.getString(j);
                        districts.add(district);
                    }
                    state.setDistricts(districts);
                    states.add(state);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            State header = new State();
            header.setName("Select a State");
            states.add(0, header);
        }
        return states;
    }

    public static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = Objects.requireNonNull(context).getAssets().open("states.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public List<String> getStateNames(Context context) {
        List<String> stateNames = new ArrayList<>();
        List<State> states = getStates(context);
        stateNames.add("Select a state");
        for (State state : states) {
            stateNames.add(state.getName());
        }
        return stateNames;
    }
}
