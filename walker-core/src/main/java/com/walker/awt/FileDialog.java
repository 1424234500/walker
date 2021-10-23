package com.walker.awt;


import com.walker.core.exception.ExceptionUtil;
import com.walker.util.Model;

import javax.swing.*;
import java.io.File;

/**
 * This class demonstrates the DirectoryDialog class
 */
public class FileDialog {

    public static String getDirPath(String nowdir) {
        ExceptionUtil.blankThrow(Model.buildDir(nowdir));
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setCurrentDirectory(new File(nowdir));
        jfc.showDialog(new JLabel(), "选择文件夹");

        File file = jfc.getSelectedFile();
        ExceptionUtil.blankThrow(Model.build("文件", file));

        return file.getAbsolutePath();
    }

    public static String getDirOrFilePath(String nowdir) {
        ExceptionUtil.blankThrow(Model.buildDir(nowdir));
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jfc.setCurrentDirectory(new File(nowdir));
        jfc.showDialog(new JLabel(), "选择文件夹或者文件");
        File file = jfc.getSelectedFile();
        ExceptionUtil.blankThrow(Model.build("文件", file));

        return file.getAbsolutePath();
    }

    public static String getFilePath(String nowdir) {
        ExceptionUtil.blankThrow(Model.buildDir(nowdir));
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setCurrentDirectory(new File(nowdir));
        jfc.showDialog(new JLabel(), "选择文件");
        File file = jfc.getSelectedFile();
        ExceptionUtil.blankThrow(Model.build("文件", file));

        return file.getAbsolutePath();
    }


}
