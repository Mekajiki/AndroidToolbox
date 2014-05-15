package net.mekajiki.lib.android.models;

import java.util.List;

public interface INetworkListener<M> {
    public void onSuccess(List<M> list);
    public void onFailure(int resourceId);
}