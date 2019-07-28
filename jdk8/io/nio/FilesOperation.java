package com.mxb.io.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.StandardCopyOption.*;

/**
 * @author moxianbin
 * @date 2019-07-28.
 */
public class FilesOperation {
    public static void main(String[] args) throws IOException {
        Path source = Paths.get("/tmp/source.txt");
        Path target = Paths.get("/tmp/source_file_copy.txt");

        Files.copy(source, target, REPLACE_EXISTING,COPY_ATTRIBUTES);

        //Files.delete(Paths.get("/tmp/test/test.txt"));
        //Files.delete(Paths.get("/tmp/test"));

        List<String> source_lines = Files.readAllLines(Paths.get("/tmp/source.txt"));
        source_lines.forEach(line-> System.out.println(line));
    }
}
