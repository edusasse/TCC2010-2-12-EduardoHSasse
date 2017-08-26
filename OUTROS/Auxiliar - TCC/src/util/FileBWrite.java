package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileBWrite {
  public static void grava(File aFile, String s){
     
    FileOutputStream outputFile = null;  
    try {
      outputFile = new FileOutputStream(aFile, true);
      System.out.println("File stream created successfully.");
    } catch (FileNotFoundException e) {
      e.printStackTrace(System.err);
    } 

    FileChannel outChannel = outputFile.getChannel();
    
    ByteBuffer buf = ByteBuffer.allocate(1024);
    System.out.println("New buffer:           position = " + buf.position()
                       + "\tLimit = " + buf.limit() + "\tcapacity = "
                       + buf.capacity());

    // Load the data into the buffer
    for (char ch : s.toCharArray()) {
      buf.putChar(ch);
    }
    System.out.println("Buffer after loading: position = " + buf.position()
                       + "\tLimit = " + buf.limit() + "\tcapacity = "
                       + buf.capacity());
    buf.flip();
    System.out.println("Buffer after flip:    position = " + buf.position() 
                       + "\tLimit = " + buf.limit() + "\tcapacity = " 
                       + buf.capacity());

    try {
      outChannel.write(buf);
      outputFile.close();
      System.out.println("Buffer contents written to file.");
    } catch (IOException e) {
      e.printStackTrace(System.err);
    }
  }
}