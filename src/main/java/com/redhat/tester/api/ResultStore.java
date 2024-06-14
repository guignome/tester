package com.redhat.tester.api;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ResultStore {
    File folder;

    public ResultStore(String folder) {
        this.folder = new File(folder);
    }

    public List<String> listResults() {
        return Arrays.asList(folder.list());
    }
}
