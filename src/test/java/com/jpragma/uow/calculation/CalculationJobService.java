package com.jpragma.uow.calculation;

import com.jpragma.uow.GlobalRegistry;
import com.jpragma.uow.UnitOfWork;
import com.jpragma.uow.UnitOfWorkAware;
import com.jpragma.uow.UnitOfWorkScope;
import com.jpragma.utils.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

@UnitOfWorkScope(CalculationTask.TYPE)
public class CalculationJobService implements UnitOfWorkAware {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String id = RandomUtils.randomAlphanumeric(7);
    private CalculationTask task;

    @Inject
    private GlobalRegistry globalRegistry;

    public String calculate(String request) {
        globalRegistry.addToData(task.getTaskId());
        return "CalculationResult-" + request + "-" + id;
    }

    @Override
    public void setUnitOfWork(UnitOfWork uow) {
        if (uow instanceof CalculationTask) {
            this.task = (CalculationTask) uow;
        } else {
            throw new IllegalArgumentException("Wrong uow type");
        }
    }

    @PreDestroy
    void cleanup() {
        log.warn("Cleaning up CalculationJobService {} for uow {}", id, task.getTaskId());
        globalRegistry.removeFromData(task.getTaskId());
    }
}
