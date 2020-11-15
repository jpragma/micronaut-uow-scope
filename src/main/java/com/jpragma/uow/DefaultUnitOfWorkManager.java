package com.jpragma.uow;

import io.micronaut.context.event.ApplicationEventPublisher;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.*;

@Singleton
public class DefaultUnitOfWorkManager implements CurrentUnitOfWorkProvider, UnitOfWorkManager {
    private static final ThreadLocal<Map<String, UnitOfWork>> THREAD_UOWS = new ThreadLocal<>();

    private final ApplicationEventPublisher applicationEventPublisher;

    public DefaultUnitOfWorkManager(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public Optional<UnitOfWork> currentUnitOfWork(String uowType) {
        Map<String, UnitOfWork> currentUoWs = THREAD_UOWS.get();
        if (currentUoWs == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(currentUoWs.get(uowType));
        }
    }

    @Override
    public Collection<UnitOfWork> allCurrentUnitsOfWork() {
        Map<String, UnitOfWork> currentUoWs = THREAD_UOWS.get();
        if (currentUoWs == null) {
            return Collections.emptySet();
        } else {
            return currentUoWs.values();
        }
    }

    @Override
    public void start(@NotNull UnitOfWork unitOfWork) {
        Map<String, UnitOfWork> units = THREAD_UOWS.get();
        if (units == null) {
            units = new HashMap<>();
            THREAD_UOWS.set(units);
        }
        if (units.containsKey(unitOfWork.getType())) {
            throw new IllegalStateException("Active unitOfWork[" + unitOfWork.getType() + "] already exists. Must call stop() method first");
        }
        units.put(unitOfWork.getType(), unitOfWork);
    }

    @Override
    public void stop(@NotNull String uowType) {
        Map<String, UnitOfWork> units = THREAD_UOWS.get();
        if (units != null) {
            UnitOfWork removed = units.remove(uowType);
            applicationEventPublisher.publishEvent(new UnitOfWorkFinishedEvent(removed));
            if (units.isEmpty()) {
                THREAD_UOWS.remove();
            }
        }
    }
}
