package com.jpragma.uow.calculation;

import com.jpragma.uow.UnitOfWork;

public class CalculationTask extends UnitOfWork {
    public static final String TYPE = "calculation";
    private final String taskId;

    public CalculationTask(String taskId) {
        super(TYPE);
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }
}
