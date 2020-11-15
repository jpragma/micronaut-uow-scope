package com.jpragma.uow;

import java.util.Collection;
import java.util.Optional;

public interface CurrentUnitOfWorkProvider {
    Optional<UnitOfWork> currentUnitOfWork(String uowType);

    Collection<UnitOfWork> allCurrentUnitsOfWork();
}
