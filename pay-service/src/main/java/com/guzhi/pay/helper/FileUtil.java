package com.guzhi.pay.helper;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件读写操作工具类
 * 
 * <br>=
 * ========================= <br>
 * 公司：duowan <br>
 * 开发：xieyuanxi@gb.com <br>
 * 版本：1.0 <br>
 * 创建时间：2013-7-15 <br>=
 * =========================
 */
public abstract class FileUtil {

    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 读取指定文件的文本内容
     * 
     * @param filepath
     *            文件的路径
     * @return
     */
    public static String readFile(String filepath) {
        StringBuffer buffer = new StringBuffer();
        try {
            File file = new File(filepath);
            FileReader fr = new FileReader(file);
            BufferedReader in = new BufferedReader(fr);
            String line = in.readLine();
            while (line != null) {
                buffer.append(line);
                buffer.append("\n");
                line = in.readLine();
            }
            in.close();
            fr.close();
        } catch (IOException e) {
            LOG.error("the file name is error");
        }
        return buffer.toString();
    }

    /**
     * 把字符串写入文本文件
     * 
     * @param fileName
     * @param content
     * @param append
     * @throws IOException
     */
    public static void write(String fileName, String content, boolean append) throws IOException {
        File file = loadFile(fileName, true);
        BufferedWriter out = new BufferedWriter(new FileWriter(file, append));
        out.write(content);
        out.newLine();
        out.close();
    }

    /**
     * 将数组中的内容以文件行形式（数组的每个元素为一行）写入文件写文件
     * 
     * @param fileName
     * @param content
     * @throws IOException
     */
    public static void write(String fileName, String content[]) throws IOException {
        if (content == null) {
            LOG.warn("需要写入文件的内容为空！");
            return;
        }
        File file = loadFile(fileName, true);
        BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
        for (int i = 0; i < content.length; i++) {
            out.newLine();
            out.write(content[i]);
        }
        out.close();
    }

    /**
     * 将数组中的内容以文件行形式（数组的每个元素为一行）写入文件写文件
     * 
     * @param fileName
     * @param contents
     * @throws IOException
     */
    public static void write(String fileName, Collection<String> contents) throws IOException {
        if (contents == null || contents.isEmpty()) {
            LOG.warn("需要写入文件的内容为空！");
            return;
        }
        File file = loadFile(fileName, true);
        BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
        if (file.length() != 0) {
            out.newLine();
        }
        Iterator<String> iterator = contents.iterator();
        while (iterator.hasNext()) {
            out.write(iterator.next());
            out.newLine();
        }
        out.close();
    }

    /**
     * 将数组中的内容以文件行形式（数组的每个元素为一行）写入文件写文件
     * 
     * @param fileName
     * @param contents
     * @throws IOException
     */
    public static void write(String fileName, Set<Long> contents) throws IOException {
        if (contents == null || contents.isEmpty()) {
            LOG.warn("需要写入文件的内容为空！");
            return;
        }
        File file = loadFile(fileName, true);
        BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
        if (file.length() != 0) {
            out.newLine();
        }
        Iterator<Long> iterator = contents.iterator();
        while (iterator.hasNext()) {
            out.write(iterator.next().longValue() + "");
            out.newLine();
        }
        out.close();
    }

    /**
     * 根据文件名获取指定的文件
     * 
     * @param fileName 文件名
     * @param isCreate 如果文件不存在，是否需要创建
     * @return
     */
    public static File loadFile(String fileName, boolean isCreate) throws IOException {
        File file = new File(fileName);
        if (file.exists())
            return file;

        if (!isCreate)
            return null;

        File parentFile = file.getParentFile();
        if (parentFile == null)
            return null;

        if (!parentFile.exists() && parentFile.getPath() != null && !"".equals(parentFile.getPath()))
            if (!parentFile.mkdirs())
                return null;

        if (file.createNewFile())
            return file;

        return null;
    }

    public static void write(String fileName, String content) throws IOException {
        write(fileName, content, true);
    }

    /**
     * 将数据写入文件中
     * 
     * @param fileName
     * @param data
     * @throws IOException
     */
    public static void write(String fileName, byte[] data) throws IOException {
        File file = loadFile(fileName, true);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
        bufferedOutputStream.write(data);
        bufferedOutputStream.close();
    }

    /**
     * 路径指向的是否是一个文件件
     * 
     * @param path
     * @return
     */
    public static boolean mkDirs(String path) {
        File file = new File(path);
        if (!file.isDirectory()) {
            return file.mkdirs();
        }
        return file.isDirectory();
    }

    /**
     * 删除指定的文件
     * 
     * @param fileName
     * @return
     */
    public static boolean delete(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * 文件是否存在
     * 
     * @param path
     * @return
     */
    public static boolean isFileExists(String path) {
        if (path == null || path.trim().equals(""))
            return false;
        File file = new File(path);
        if (file.exists()) {
            return file.isDirectory() ? false : true;
        } else {
            return false;
        }
    }

    /**
     * 是否是文本文件名称
     * 
     * @param filename
     * @return
     */
    public static boolean isTextFilename(String filename) {
        if (filename == null)
            return false;
        filename = filename.toLowerCase();
        return filename.endsWith("txt") || filename.endsWith("text") || filename.endsWith("log");
    }

    /**
     * 内容过滤
     * 
     * @param sourceFile
     * @param targetFile
     * @param filterCharacters
     */
    public static void contentFilter(String sourceFile, String targetFile, String filterCharacters) {
        contentFilter(sourceFile, targetFile, filterCharacters, null);
    }

    /**
     * 内容过滤
     * 
     * @param sourceFile
     * @param targetFile
     * @param filterCharacters
     * @param ignoreFilterCharacters
     */
    public static void contentFilter(String sourceFile, String targetFile, String filterCharacters,
            String ignoreFilterCharacters) {
        contentFilter(sourceFile, targetFile, filterCharacters, null, 0);
    }

    /**
     * 内容过滤
     * 
     * @param sourceFile
     * @param targetFile
     * @param filterCharacters
     * @param ignoreFilterCharacters
     * @param includeMatchRecodeNumber
     */
    public static void contentFilter(String sourceFile, String targetFile, String filterCharacters,
            String ignoreFilterCharacters, int includeMatchRecodeNumber) {
        if (sourceFile == null)
            return;
        File file = new File(sourceFile);
        if (!file.exists())
            return;
        if (file.isFile() && !(isTextFilename(sourceFile)))
            return;

        StringBuilder sourceFiles = new StringBuilder();
        if (file.isDirectory()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return isTextFilename(name);
                }
            };
            String[] filenames = file.list(filter);
            for (String filename : filenames) {
                sourceFiles.append(file.getPath()).append("/").append(filename).append(",");
            }
            sourceFiles.delete(sourceFiles.length() - 1, sourceFiles.length());
        } else {
            sourceFiles.append(sourceFile);
        }

        RandomAccessFile targetRaf = null;
        try {
            File newFile = new File(targetFile);
            if (!newFile.exists() && !newFile.createNewFile()) {
                LOG.warn("创建文件失败targetFile{}", targetFile);
                return;
            }
            targetRaf = new RandomAccessFile(newFile, "rwd");
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String[] filenames = sourceFiles.toString().split(",");
        for (String filename : filenames) {
            contentFilter(filename, targetRaf, filenames.length > 1, filterCharacters, ignoreFilterCharacters,
                    includeMatchRecodeNumber);
        }
    }

    /**
     * 内容过滤
     * 
     * @param filename
     * @param targetRaf
     * @param isAppendMsg
     * @param filterCharacters
     * @param ignoreFilterCharacters
     * @param includeMatchRecodeNumber
     */
    private static void contentFilter(String filename, RandomAccessFile targetRaf, boolean isAppendMsg,
            String filterCharacters, String ignoreFilterCharacters, int includeMatchRecodeNumber) {
        RandomAccessFile raf = null;
        try {
            if (isAppendMsg) {
                targetRaf.writeBytes("=======================================================\n");
                targetRaf.writeBytes(filename);
                targetRaf.writeBytes("\n");
                targetRaf.writeBytes("=======================================================\n");
            }
            int count = 0;
            raf = new RandomAccessFile(filename, "r");
            String lineContent = raf.readLine();
            if (ignoreFilterCharacters != null) {
                if (filterCharacters == null) {
                    while (lineContent != null) {
                        if (lineContent.indexOf(ignoreFilterCharacters) == -1) {
                            targetRaf.writeBytes(lineContent);
                            targetRaf.writeBytes("\n");
                        }
                        lineContent = raf.readLine();
                    }
                } else {
                    while (lineContent != null) {
                        if (lineContent.indexOf(ignoreFilterCharacters) == -1) {
                            if (filterCharacters != null && lineContent.indexOf(filterCharacters) != -1) {
                                targetRaf.writeBytes(lineContent);
                                targetRaf.writeBytes("\n");
                                count = includeMatchRecodeNumber;
                            } else {
                                if (count-- > 0) {
                                    targetRaf.writeBytes(lineContent);
                                    targetRaf.writeBytes("\n");
                                }
                            }
                        }
                        lineContent = raf.readLine();
                    }
                }
            } else {
                while (lineContent != null) {
                    if (lineContent.indexOf(filterCharacters) != -1) {
                        targetRaf.writeBytes(lineContent);
                        targetRaf.writeBytes("\n");
                        count = includeMatchRecodeNumber;
                    } else {
                        if (count-- > 0) {
                            targetRaf.writeBytes(lineContent);
                            targetRaf.writeBytes("\n");
                        }
                    }
                    lineContent = raf.readLine();
                }
            }
            targetRaf.writeBytes("\n");

        } catch (FileNotFoundException e) {
            try {
                if (raf != null)
                    raf.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            try {
                if (raf != null)
                    raf.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (raf != null)
                    raf.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

}
