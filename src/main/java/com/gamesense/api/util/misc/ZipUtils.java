package com.gamesense.api.util.misc;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ZipUtils {
    public static void zip(File source, File dest) {
        List<String> list = new ArrayList<String>();
        createFileList(source, source, list);
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(dest));
            for (String file : list) {
                ZipEntry ze = new ZipEntry(file);
                FileInputStream in = new FileInputStream(file);
                byte buffer[] = new byte[1024];
                zos.putNextEntry(ze);
                while (true) {
                    int len = in.read(buffer);
                    if (len <= 0) break;
                    zos.write(buffer, 0, len);
                }
                in.close();
                zos.closeEntry();
            }
            zos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createFileList(File file, File source, List<String> list) {
        if (file.isFile()) {
            list.add(file.getPath());
        } else if (file.isDirectory()) {
            for (String subfile : file.list()) {
                createFileList(new File(file, subfile), source, list);
            }
        }
    }
}
