package com.jpragma.uow;

import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class GlobalRegistry {
    private final Set<String> data = new HashSet<>();

    public Set<String> getData() {
        return data;
    }

    public void addToData(String value) {
        data.add(value);
    }

    public void removeFromData(String value) {
        data.remove(value);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
