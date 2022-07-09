package com.balaabirami.abacusandroid.utils;

import android.content.Context;
import android.util.Log;

import com.balaabirami.abacusandroid.model.State;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
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
                JSONObject obj = new JSONObject(Objects.requireNonNull(loadJSONFromAsset(context, "states.json")));
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
                    Collections.sort(districts);
                    state.setDistricts(districts);
                    states.add(state);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Collections.sort(states, (state1, state2) -> state1.getName().compareToIgnoreCase(state2.getName()));
            State header = new State();
            header.setName("Select a State");
            states.add(0, header);
        }
        return states;
    }


    public List<User> getFranchises(Context context) {
        List<User> users = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(Objects.requireNonNull(loadJSONFromAsset(context, "Alama.json")));
            JSONArray jsonUsers = obj.getJSONArray("users");
            for (int i = 0; i < jsonUsers.length(); i++) {
                String userStr = jsonUsers.getJSONObject(i).toString();
                User user = new Gson().fromJson(userStr, User.class);
                users.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<Student> getStudents(Context context) {
        List<Student> students = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(Objects.requireNonNull(loadJSONFromAsset(context, "data.json")));
            JSONArray jsonUsers = obj.getJSONArray("students");
            for (int i = 0; i < jsonUsers.length(); i++) {
                String userStr = jsonUsers.getJSONObject(i).toString();
                Student student = new Gson().fromJson(userStr, Student.class);
                students.add(student);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Log.d("SurenError", "" + students.size());
        } catch (JSONException e) {
        }

        Log.d("Suren", "" + students.size());
        return students;
    }

    public static String loadJSONFromAsset(Context context, String file) {
        String json = null;
        try {
            InputStream is = Objects.requireNonNull(context).getAssets().open(file);
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
        for (State state : states) {
            stateNames.add(state.getName());
        }
        return stateNames;
    }
}
