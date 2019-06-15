package com.aglushkov.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.Map;
import java.util.WeakHashMap;

public class RepositoryCommandHolder {
    private WeakHashMap<LiveData<?>, Long> liveDataIdMap = new WeakHashMap<>();
    private WeakHashMap<LiveData<?>, RepositoryCommand<?>> liveDataCommandMap = new WeakHashMap<>();

    @NonNull
    public RepositoryCommand putCommand(@NonNull RepositoryCommand<?> cmd) {
        RepositoryCommand<?> oldCmd = getCommand(cmd.getCommandId());
        if (oldCmd != null) {
            cancel(oldCmd.getLiveData());
        }

        liveDataIdMap.put(cmd.getLiveData(), cmd.getCommandId());
        liveDataCommandMap.put(cmd.getLiveData(), cmd);
        return cmd;
    }

    @Nullable
    public RepositoryCommand<?> getCommand(@Nullable LiveData<?> liveData) {
        return liveDataCommandMap.get(liveData);
    }

    @Nullable
    public RepositoryCommand<?> getCommand(long id) {
        RepositoryCommand<?> cmd = null;
        for (RepositoryCommand<?> c : liveDataCommandMap.values()) {
            if (c != null && c.getCommandId() == id) {
                cmd = c;
            }
        }

        return cmd;
    }

    @Nullable
    public <T extends LiveData<?>> T getLiveData(long id) {
        T result = null;
        for (Map.Entry<LiveData<?>, Long> entry : liveDataIdMap.entrySet()) {
            if (entry.getValue() == id) {
                result = (T)entry.getKey();
                break;
            }
        }

        return result;
    }

    public void putLiveData(long id, LiveData<?> liveData) {
        liveDataIdMap.put(liveData, id);
        liveDataCommandMap.put(liveData, null);
    }

    public void cancel(@Nullable LiveData<?> liveData) {
        RepositoryCommand cmd = getCommand(liveData);
        if (cmd != null) {
            cmd.cancel();
            liveDataIdMap.remove(liveData);
            liveDataCommandMap.remove(liveData);
        }
    }
}
