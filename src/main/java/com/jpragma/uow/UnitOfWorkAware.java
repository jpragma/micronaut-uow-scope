package com.jpragma.uow;

public interface UnitOfWorkAware {
    void setUnitOfWork(UnitOfWork uow);
}
