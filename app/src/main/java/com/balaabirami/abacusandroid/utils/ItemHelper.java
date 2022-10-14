package com.balaabirami.abacusandroid.utils;

import android.content.Context;
import com.balaabirami.abacusandroid.model.ProductItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ItemHelper {

    private static ItemHelper itemHelper;
    private ArrayList<ProductItem> productItems = new ArrayList<>();

    public static ItemHelper getInstance() {
        return itemHelper == null ? new ItemHelper() : itemHelper;
    }

    public ItemHelper() {
    }

    public ArrayList<ProductItem> getItems(Context context) {
        if (productItems.isEmpty()) {
            try {
                JSONObject obj = new JSONObject(Objects.requireNonNull(loadJSONFromAsset(context, "items.json")));
                JSONArray jsonItems = obj.getJSONArray("items");
                for (int i = 0; i < jsonItems.length(); i++) {
                    ProductItem productItem = new ProductItem();
                    JSONObject jsonItem = jsonItems.getJSONObject(i);
                    productItem.setName(jsonItem.getString("name"));
                    productItem.setIndex(jsonItem.getInt("index"));
                    if (jsonItem.has("level")) {
                        productItem.setLevel(jsonItem.getInt("level"));
                    }
                    productItems.add(productItem);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return productItems;
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

    public HashMap<String, Integer> getItemsMap(Context context) {
        HashMap<String, Integer> itemsMap = new HashMap<>();
        List<ProductItem> items = getItems(context);
        for (ProductItem productItem : items) {
            itemsMap.put(productItem.getName(), 0);
        }
        return itemsMap;
    }
}
