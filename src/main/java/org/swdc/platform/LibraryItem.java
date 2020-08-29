package org.swdc.platform;

import java.util.List;

public class LibraryItem {

    private String library;

    private List<LibraryItem> dependencies;

    public List<LibraryItem> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<LibraryItem> dependencies) {
        this.dependencies = dependencies;
    }

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }
}
