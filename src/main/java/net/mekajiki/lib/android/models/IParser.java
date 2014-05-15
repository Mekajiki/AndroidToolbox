package net.mekajiki.lib.android.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public interface IParser<M> {
    public List<M> parse(JSONObject json) throws IllegalAccessException, InstantiationException, JSONException;
}