package com.redhat.tester.api.views;

import java.beans.PropertyChangeListener;

public interface View extends PropertyChangeListener{
    void start();
    void stop();
}
