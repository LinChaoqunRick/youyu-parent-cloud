package com.youyu.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class LineCounter {
    public static void main(String[] args) {
        String targetDirectoryPath = "d:\\Users\\Desktop\\detect4\\龙海exp"; // 请输入你的目标文件夹路径。例如 D:\\test
        List<File> txtFiles = getAllTxtFiles(new File(targetDirectoryPath));
        for(File txtFile : txtFiles) {
            System.out.println("文件名：" + txtFile.getName() + ", 气孔数量：" + countLineBreaks(txtFile));
        }
    }

    public static List<File> getAllTxtFiles(File root) {
        List<File> txtFiles = new ArrayList<>();
        File[] files = root.listFiles();
        if(files!=null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    txtFiles.addAll(getAllTxtFiles(file));
                } else if (file.getName().endsWith(".txt")) {
                    txtFiles.add(file);
                }
            }
        }
        return txtFiles;
    }

    public static int countLineBreaks(File file) {
        int lineBreakCount = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            while(br.readLine()!=null) {
                lineBreakCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       return lineBreakCount;
    }
}
