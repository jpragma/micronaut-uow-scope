package com.jpragma.uow.report;

import com.jpragma.uow.UnitOfWork;

public class ReportTask extends UnitOfWork {
    public static final String TYPE = "report";
    private final String taskId;

    public ReportTask(String taskId) {
        super(TYPE);
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }
}
