package com.getting.util.executor;

public abstract class ParametersImp implements Parameters {

    private boolean hasDone;

    @Override
    public boolean hasDone() {
        return hasDone;
    }

    @Override
    public void setHasDone() {
        hasDone = true;
    }

}
