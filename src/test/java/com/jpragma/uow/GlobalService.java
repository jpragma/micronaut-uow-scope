package com.jpragma.uow;

import com.jpragma.uow.calculation.CalculationJobService;
import com.jpragma.uow.calculation.CalculationTask;
import com.jpragma.uow.report.ReportJobService;
import com.jpragma.uow.report.ReportTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class GlobalService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final CalculationJobService calculationJobService;
    private final ReportJobService reportJobService;

    public GlobalService(CalculationJobService calculationJobService, ReportJobService reportJobService) {
        this.calculationJobService = calculationJobService;
        this.reportJobService = reportJobService;
    }

    public Map<String, String> invokeCalculationAndReporting() {
        log.info("Global service calling 2 UOW scoped beans");
        String calcResult = calculationJobService.calculate("foo");
        String reportResult = reportJobService.createReport();
        Map<String, String> res = new HashMap<>();
        res.put(CalculationTask.TYPE, calcResult);
        res.put(ReportTask.TYPE, reportResult);
        return res;
    }
}
