package com.walker.util;


import com.walker.core.exception.ExceptionUtil;
import com.walker.mode.Property;

import javax.swing.*;
import java.io.File;

/**
 * This class demonstrates the DirectoryDialog class
 */
public class FileDialog {

    public static String getDirPath(String nowdir) {
        ExceptionUtil.blankThrow(Property.buildDir(nowdir));
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setCurrentDirectory(new File(nowdir));
        jfc.showDialog(new JLabel(), "选择文件夹");

        File file = jfc.getSelectedFile();
        ExceptionUtil.blankThrow(Property.buildFile(file));

        return file.getAbsolutePath();
    }

    public static String getDirOrFilePath(String nowdir) {
        ExceptionUtil.blankThrow(Property.buildDir(nowdir));
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jfc.setCurrentDirectory(new File(nowdir));
        jfc.showDialog(new JLabel(), "选择文件夹或者文件");
        File file = jfc.getSelectedFile();
        ExceptionUtil.blankThrow(Property.buildFile(file));

        return file.getAbsolutePath();
    }

    public static String getFilePath(String nowdir) {
        ExceptionUtil.blankThrow(Property.buildDir(nowdir));
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setCurrentDirectory(new File(nowdir));
        jfc.showDialog(new JLabel(), "选择文件");
        File file = jfc.getSelectedFile();
        ExceptionUtil.blankThrow(Property.buildFile(file));

        return file.getAbsolutePath();
    }


}
