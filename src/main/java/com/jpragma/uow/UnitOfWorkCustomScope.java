package com.jpragma.uow;

import io.micronaut.context.BeanContext;
import io.micronaut.context.BeanResolutionContext;
import io.micronaut.context.LifeCycle;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.scope.CustomScope;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.BeanIdentifier;
import io.micronaut.inject.DisposableBeanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"rawtypes", "unchecked"})
@Singleton
public class UnitOfWorkCustomScope implements CustomScope<UnitOfWorkScope>, LifeCycle<UnitOfWorkCustomScope>, ApplicationEventListener<UnitOfWorkFinishedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(UnitOfWorkScope.class);
    private final BeanContext beanContext;
    private final CurrentUnitOfWorkProvider currentUnitOfWorkProvider;

    public UnitOfWorkCustomScope(BeanContext beanContext, CurrentUnitOfWorkProvider currentUnitOfWorkProvider) {
        this.beanContext = beanContext;
        this.currentUnitOfWorkProvider = currentUnitOfWorkProvider;
    }

    @Override
    public Class<UnitOfWorkScope> annotationType() {
        return UnitOfWorkScope.class;
    }

    @Override
    public <T> T get(BeanResolutionContext resolutionContext, BeanDefinition<T> beanDefinition, BeanIdentifier identifier, Provider<T> provider) {
        String uowType = extractAnnotationType(beanDefinition);
        UnitOfWork unitOfWork = currentUnitOfWorkProvider.currentUnitOfWork(uowType)
                .orElseThrow(() -> new IllegalStateException("Bean of type " + beanDefinition.getBeanType() + " can not be created. No current unit of work (type=" + uowType + ") is found"));
        Map scopedBeanMap = this.getScopedBeansForThisUow(unitOfWork);
        T bean = (T) scopedBeanMap.get(identifier);
        if (bean == null) {
            synchronized (this) {
                bean = (T) scopedBeanMap.get(identifier);
                if (bean == null) {
                    bean = provider.get();
                    if (bean instanceof UnitOfWorkAware) {
                        ((UnitOfWorkAware) bean).setUnitOfWork(unitOfWork);
                    }
                    scopedBeanMap.put(identifier, bean);
                }
            }
        }
        return bean;
    }

    private synchronized Map getScopedBeansForThisUow(UnitOfWork unitOfWork) {
        return unitOfWork.getScopedBeans();
    }

    private <T> String extractAnnotationType(BeanDefinition<T> beanDefinition) {
        AnnotationValue<UnitOfWorkScope> annotation = beanDefinition.findAnnotation(UnitOfWorkScope.class)
                .orElseThrow(() -> new IllegalStateException(UnitOfWorkScope.class.getSimpleName() + " annotation is not found on " + beanDefinition.getBeanType().getSimpleName()));
        return annotation.getRequiredValue(String.class);
    }

    @Override
    public <T> Optional<T> remove(BeanIdentifier identifier) {
        for (UnitOfWork uow : currentUnitOfWorkProvider.allCurrentUnitsOfWork()) {
            Optional<T> removedBean = this.destroyUnitOfWorkScopedBean(identifier, uow.getScopedBeans());
            if (removedBean.isPresent()) {
                return removedBean;
            }
        }
        return Optional.empty();
    }

    @Override
    public UnitOfWorkCustomScope stop() {
        currentUnitOfWorkProvider.allCurrentUnitsOfWork().forEach(this::destroyBeans);
        return this;
    }

    @Override
    public boolean isRunning() {
        return true;
    }

    @Override
    public void onApplicationEvent(UnitOfWorkFinishedEvent event) {
        LOG.info("UnitOfWork " + event.getSource().getType() + " is finished");
        this.destroyBeans(event.getSource());
    }

    private void destroyBeans(UnitOfWork uow) {
        Map beans = uow.getScopedBeans();
        if (CollectionUtils.isNotEmpty(beans)) {
            beans.keySet().forEach((beanId) -> {
                if (beanId instanceof BeanIdentifier) {
                    this.destroyUnitOfWorkScopedBean(((BeanIdentifier) beanId), beans);
                }
            } );
        }
    }

    private <T> Optional<T> destroyUnitOfWorkScopedBean(BeanIdentifier identifier, Map beans) {
        T bean = (T) beans.remove(identifier);
        if (bean != null) {
            this.beanContext.findBeanDefinition(bean.getClass()).ifPresent((beanDefinition) -> {
                if (beanDefinition instanceof DisposableBeanDefinition) {
                    try {
                        ((DisposableBeanDefinition) beanDefinition).dispose(this.beanContext, bean);
                    } catch (Exception e) {
                        LOG.error("Error disposing UnitOfWork scoped bean: {}", bean, e);
                    }
                }
                if (bean instanceof AutoCloseable) {
                    try {
                        ((AutoCloseable) bean).close();
                    } catch (Exception e) {
                        LOG.error("Error closing UnitOfWork scoped bean: {}", bean, e);
                    }
                }
            });
        }
        return Optional.ofNullable(bean);
    }
}
