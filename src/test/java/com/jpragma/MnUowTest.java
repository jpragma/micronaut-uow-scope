package com.jpragma;

import com.jpragma.uow.GlobalService;
import com.jpragma.uow.GlobalRegistry;
import com.jpragma.uow.UnitOfWorkManager;
import com.jpragma.uow.calculation.CalculationJobService;
import com.jpragma.uow.calculation.CalculationTask;
import com.jpragma.uow.report.ReportTask;
import com.jpragma.utils.RandomUtils;
import io.micronaut.context.BeanContext;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class MnUowTest {

    @Inject
    GlobalService globalService;
    @Inject
    UnitOfWorkManager unitOfWorkManager;
    @Inject
    BeanContext beanContext;
    @Inject
    GlobalRegistry globalRegistry;

    @Test
    void testItWorks() {
        assertTrue(globalRegistry.isEmpty());

        String calculationTaskId = RandomUtils.randomAlphanumeric(7);
        CalculationTask calculationTask = new CalculationTask(calculationTaskId);
        unitOfWorkManager.start(calculationTask);

        String reportTaskId = RandomUtils.randomAlphanumeric(7);
        ReportTask reportTask = new ReportTask(reportTaskId);
        unitOfWorkManager.start(reportTask);

        Map<String, String> res = globalService.invokeCalculationAndReporting();
        assertEquals(2, res.size());
        String calcResult = res.get(CalculationTask.TYPE);
        assertTrue(calcResult.startsWith("CalculationResult-foo"));
        String reportResult = res.get(ReportTask.TYPE);
        assertTrue(reportResult.startsWith("ReportResult-"));

        assertEquals(2, globalRegistry.getData().size());

        CalculationJobService calcJobBean = beanContext.getBean(CalculationJobService.class);
        assertEquals(calcResult, calcJobBean.calculate("foo"));

        unitOfWorkManager.stop(calculationTask);
        unitOfWorkManager.stop(reportTask); // TODO Currently bean definitions created by factories are not implementing DisposableBeanDefinition for some reason

        assertTrue(globalRegistry.isEmpty());
        assertThrows(IllegalStateException.class, () -> calcJobBean.calculate("foo"));
    }

}
