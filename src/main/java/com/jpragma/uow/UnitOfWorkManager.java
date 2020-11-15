package com.jpragma.uow;

public interface UnitOfWorkManager {
    void start(UnitOfWork unitOfWork);

    void stop(String uowType);

    default void stop(UnitOfWork unitOfWork) {
        stop(unitOfWork.getType());
    }
}
