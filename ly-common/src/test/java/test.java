import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class test {
    public static void main(String[] args) throws IOException {
        String word = "氹仔";
        if (word.contains("仔")) {
            word = word.replace("仔", "宰");
            System.out.println(word);
        }
        System.out.println(word);
    }

}
