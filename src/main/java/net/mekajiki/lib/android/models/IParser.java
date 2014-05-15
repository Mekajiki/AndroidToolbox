package net.mekajiki.lib.android.models;

import org.json.JSONException;

import java.util.List;

public interface IParser<M> {
    public List<M> parseJsonObject(Object json) throws IllegalAccessException, InstantiationException, JSONException;
}