package com.jpragma.uow.report;

import com.jpragma.uow.GlobalRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static com.jpragma.utils.RandomUtils.randomAlphanumeric;

public class ReportJobService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String id = randomAlphanumeric(7);
    private final Set<String> values = new HashSet<>();

    private final GlobalRegistry globalRegistry;

    public ReportJobService(GlobalRegistry globalRegistry) {
        this.globalRegistry = globalRegistry;
    }

    public String createReport() {
        String value = "report-" + randomAlphanumeric(5);
        values.add(value);
        globalRegistry.addToData(value);
        return "ReportResult-" + id;
    }

    public void cleanup() {
        log.warn("Cleaning up ReportJobService {}", id);
        values.forEach(v -> globalRegistry.removeFromData(v));
    }
}
