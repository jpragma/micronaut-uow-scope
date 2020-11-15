package com.jpragma.uow;

import io.micronaut.context.event.ApplicationEvent;

import javax.validation.constraints.NotNull;

public class UnitOfWorkFinishedEvent extends ApplicationEvent {

    public UnitOfWorkFinishedEvent(@NotNull UnitOfWork source) {
        super(source);
    }

    @NotNull
    @Override
    public UnitOfWork getSource() {
        return (UnitOfWork) super.getSource();
    }
}
