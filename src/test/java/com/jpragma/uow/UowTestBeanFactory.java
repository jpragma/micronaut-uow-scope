package com.jpragma.uow;

import com.jpragma.uow.report.ReportJobService;
import com.jpragma.uow.report.ReportTask;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

@Factory
public class UowTestBeanFactory {

    @Bean(preDestroy = "cleanup")
    @UnitOfWorkScope(ReportTask.TYPE)
    public ReportJobService reportJobService(GlobalRegistry globalRegistry) {
        return new ReportJobService(globalRegistry);
    }

}
