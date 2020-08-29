package org.swdc.platform;

import java.util.List;

public class NativeModule {

    private String name;
    private String desc;

    private List<SystemLibrary> platforms;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<SystemLibrary> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<SystemLibrary> platforms) {
        this.platforms = platforms;
    }
}
