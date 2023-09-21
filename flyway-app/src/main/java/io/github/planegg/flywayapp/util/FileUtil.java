package io.github.planegg.flywayapp.util;

import java.io.File;

public class FileUtil {
    /**
     *
     * @param path
     * @param fileName
     * @return
     */
    public static File getFile(String path, String fileName){
        if (path == null){
            return null;
        }
        return getFile(path + File.separator + fileName);
    }

    /**
     *
     * @param filePath
     * @return
     */
    public static File getFile(String filePath){
        if (filePath == null){
            return null;
        }
        File file = new File(filePath);
        if (file.exists()){
            return file;
        }
        return null;
    }
}
