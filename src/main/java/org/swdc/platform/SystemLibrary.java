package org.swdc.platform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;

public class SystemLibrary {

    private String platform;
    private String descriptor;
    private int arch;
    private LibraryItem desc;

    @JsonIgnore
    private File moduleFolder;

    public File getModuleFolder() {
        if (moduleFolder == null) {
            return null;
        }
        File file = new File(moduleFolder.getAbsolutePath() +
                File.separator +
                descriptor);
        return file;
    }

    public void setModuleFolder(File moduleFolder) {
        this.moduleFolder = moduleFolder;
    }

    public void setArch(int arch) {
        this.arch = arch;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getArch() {
        return arch;
    }

    public String getPlatform() {
        return platform;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public LibraryItem loadDescriptor() {
        if (desc != null) {
            return desc;
        }
        if (moduleFolder == null) {
            return null;
        }
        File file = new File(getModuleFolder() + File.separator + "descriptor.json");
        if (!file.exists()) {
            return null;
        }
        try {
            byte[] data = Files.readAllBytes(file.toPath());
            ObjectMapper mapper = new ObjectMapper();
            desc = mapper.readValue(data,LibraryItem.class);
            return desc;
        }catch (Exception e) {
            return null;
        }
    }
}
