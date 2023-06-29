package com.walker.core.system.swing;

import javax.swing.*;
import java.io.File;
import java.util.Objects;

/**
 * This class demonstrates the DirectoryDialog class
 */
public class FileDialog {

    public static String getDirPath(String nowdir) {
        Objects.requireNonNull(nowdir);
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setCurrentDirectory(new File(nowdir));
        jfc.showDialog(new JLabel(), "选择文件夹");

        File file = jfc.getSelectedFile();
        Objects.requireNonNull(file);

        return file.getAbsolutePath();
    }

    public static String getDirOrFilePath(String nowdir) {
        Objects.requireNonNull(nowdir);
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jfc.setCurrentDirectory(new File(nowdir));
        jfc.showDialog(new JLabel(), "选择文件夹或者文件");
        File file = jfc.getSelectedFile();
        Objects.requireNonNull(file);

        return file.getAbsolutePath();
    }

    public static String getFilePath(String nowdir) {
       Objects.requireNonNull(nowdir);
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setCurrentDirectory(new File(nowdir));
        jfc.showDialog(new JLabel(), "选择文件");
        File file = jfc.getSelectedFile();
        Objects.requireNonNull(file);

        return file.getAbsolutePath();
    }


}
