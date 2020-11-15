package com.jpragma.uow;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("rawtypes")
public abstract class UnitOfWork {
    private final String type;
    private final Map scopedBeans = new ConcurrentHashMap(5);

    protected UnitOfWork(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Map getScopedBeans() {
        return scopedBeans;
    }
}
