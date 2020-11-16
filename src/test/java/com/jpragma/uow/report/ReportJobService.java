package com.jpragma.uow.report;

import com.jpragma.uow.GlobalRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static com.jpragma.utils.RandomUtils.randomAlphanumeric;

// BeanDefinitions created by factories to not call preDestroy method
// See https://github.com/micronaut-projects/micronaut-core/issues/4489
// Therefore we added support for AutoClosable as well, we will call close() method when the scope is over
public class ReportJobService implements AutoCloseable {
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
        values.forEach(globalRegistry::removeFromData);
    }

    @Override
    public void close() {
        cleanup();
    }
}
