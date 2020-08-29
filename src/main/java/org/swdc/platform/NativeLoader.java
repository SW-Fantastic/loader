package org.swdc.platform;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * 从自定义位置加载本地类库（dll，so，dylib之类的）
 * 本地加载器
 */
public class NativeLoader {

    private Map<String,LibraryItem> loadedLibrary = new HashMap<>();

    private File libraryLocation;

    public NativeLoader(File file) {
        this.libraryLocation = file;
    }

    public boolean load(String targetName) throws Exception{
        File target = new File(libraryLocation.getAbsolutePath() + File.separator + targetName);
        if (target.isFile()) {
            String extension = target.getName().split("[.]")[1];
            if (!extension.equalsIgnoreCase("jar") &&
                    !extension.equalsIgnoreCase("zip")) {
                // 不是指定压缩格式，尝试直接加载
                LibraryItem item = new LibraryItem();
                item.setLibrary(getLibraryName(target));
                resolveNative(target.getParentFile().getAbsolutePath(),item);
                loadedLibrary.put(getLibraryName(target),item);
                return true;
            }
            // 压缩格式处理
            File folder = new File(libraryLocation.getAbsolutePath() + File.separator + "shared");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File extracted = new File(folder.getAbsolutePath() + File.separator + target.getName().split("[.]")[0]);
            if (!extracted.exists()) {
                ZipFile archive = new ZipFile(target);
                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(target));
                ZipEntry entry = zipInputStream.getNextEntry();
                while (entry != null) {
                    if (entry.isDirectory()) {
                        File file = new File(folder.getAbsolutePath() + File.separator + entry.getName());
                        file.mkdirs();
                        entry = zipInputStream.getNextEntry();
                        continue;
                    }
                    archive.getInputStream(entry).transferTo(new FileOutputStream(folder.getAbsolutePath() + File.separator + entry.getName()));
                    entry = zipInputStream.getNextEntry();
                }
                target = extracted;
            } else {
                target = extracted;
            }
        }
        byte[] data = Files.readAllBytes(new File(target + File.separator + "module.json").toPath());
        ObjectMapper mapper = new ObjectMapper();
        NativeModule nativeModule = mapper.readValue(data,NativeModule.class);
        String sysName = System.getProperty("os.name").toLowerCase();
        Integer bit = Integer.valueOf(System.getProperty("sun.arch.data.model"));
        SystemLibrary library = null;
        for (SystemLibrary lib: nativeModule.getPlatforms()) {
            if (lib.getArch() != bit) {
                continue;
            }
            if (!sysName.contains(sysName)) {
                continue;
            }
            lib.setModuleFolder(target);
            library = lib;
            break;
        }
        if (library == null) {
            throw new Exception("can not found library for current platform");
        }
        resolveNative(library.getModuleFolder().getAbsolutePath(),library.loadDescriptor());
        return true;
    }

    private void resolveNative(String basePath,LibraryItem desc) throws Exception {
        if (desc.getDependencies() == null || desc.getDependencies().isEmpty()) {
            try {
                File libraryFile = new File(basePath + File.separator + resolveLibName(desc.getLibrary()));
                if (loadedLibrary.containsKey(getLibraryName(libraryFile))) {
                    return;
                }
                System.load(libraryFile.getAbsolutePath());
                loadedLibrary.put(getLibraryName(libraryFile),desc);
                return;
            }catch (Throwable e) {
                e.printStackTrace();
                throw new Exception("can not load library：" + desc.getLibrary());
            }
        }
        List<LibraryItem> depend = desc.getDependencies();
        for (LibraryItem item: depend) {
            resolveNative(basePath,item);
        }
        try {
            File libraryFile = new File(basePath + File.separator + resolveLibName(desc.getLibrary()));
            if (loadedLibrary.containsKey(getLibraryName(libraryFile))) {
                return;
            }
            System.load(libraryFile.getAbsolutePath());
            loadedLibrary.put(getLibraryName(libraryFile),desc);
        }catch (Throwable e) {
            e.printStackTrace();
            throw new Exception("can not load library：" + desc.getLibrary());
        }
    }


    private String resolveLibName(String name) {
        String sysName = System.getProperty("os.name");
        String subFix = "";
        String preFix = "";
        if (sysName.toLowerCase().contains("mac")) {
            preFix = "lib";
            subFix = "dylib";
        } else if (sysName.toLowerCase().contains("windows")){
            subFix = "dll";
        } else if (sysName.toLowerCase().contains("linux")) {
            preFix = "lib";
            subFix = "so";
        }
        return preFix + name + "." + subFix;
    }

    private String getLibraryName(File file) {
        String sysName = System.getProperty("os.name");
        String subFix = "";
        String preFix = "";
        if (sysName.toLowerCase().contains("mac")) {
            preFix = "lib";
            subFix = "dylib";
        } else if (sysName.toLowerCase().contains("windows")){
            subFix = "dll";
        } else if (sysName.toLowerCase().contains("linux")) {
            preFix = "lib";
            subFix = "so";
        }
        return file.getName().replace(preFix,"").replace(subFix,"").split("[.]")[0];
    }

}
