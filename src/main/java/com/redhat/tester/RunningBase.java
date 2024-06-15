package com.redhat.tester;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class RunningBase {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean running = false;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public boolean isRunning(){
        return running;
    }

    protected void setRunning(boolean running) {
        boolean oldValue = this.running;
        this.running = running;
        this.pcs.firePropertyChange("running", oldValue, running);
    }
}
