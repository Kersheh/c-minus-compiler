package asm;

import absyn.*;
import symb.*;
import java.io.*;

abstract public class Asm {
  static private String asm = ""; //string output to .tm file

  static public void generateAssembly(String filename) {
    /* body of assembly generation calls here */
    /*                                        */
    /*                                        */

    /* output file name with path and .tm file type */
    String write = filename.substring(0, filename.lastIndexOf('.')) + ".tm";
    /* export assembly to external file */
    try {
      File file = new File(write);
      if(!file.exists()) 
        file.createNewFile();

      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(asm);
      bw.close();
      System.out.println("Compile complete, saved to " + write);
    } catch(IOException e) {
      System.out.println("Error: failed to write to " + write);
    }
  }
}