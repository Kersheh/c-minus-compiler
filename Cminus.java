/*
  Created by: Matt Breckon and Dean Way
  File name: Cminus.java
  To run: 
    $ java Cminus [-a] [-s] [file_name.cm]
*/
   
import java.io.*;
   
class Cminus {
  /* terminal argument error readout */
  static private void argsError() {
    System.out.println("usage: java Cminus [-a] [-s] [file_name.cm]");
    System.out.println("       -a: display abstract syntax tree");
    System.out.println("       -s: display symbol tables");
  }

  static public void main(String args[]) {
    String filename;
    boolean[] args_bool = new boolean[2]; // [0] is -a, [1] is -s

    /* validate number of arguments */
    if(args.length > 3) {
      System.out.println("Invalid number of arguments.");
      argsError();
      return;
    }
    /* validate command line arguments */
    for(int i = 0; i < args.length - 1; i++) {
      if(!args[i].equals("-a") && !args[i].equals("-s")) {
        System.out.println(args[i] + " is not a valid argument.");
        argsError();
        return;
      }
      if(args[i].equals("-a")) args_bool[0] = true;
      if(args[i].equals("-s")) args_bool[1] = true;
    }
    /* set filename */
    filename = args[args.length - 1];
    /* validate file exists */
    if(!new File(filename).isFile()) {
      System.out.println("File " + filename + " does not exist.");
      argsError();
      return;
    }
    /* validate filetype */
    if(!filename.contains(".cm")) {
      System.out.println("File error: filetype .cm required.");
      argsError();
      return;
    }
    /* execute parser */
    try {
      parser p = new parser(args_bool, new Lexer(new FileReader(filename)));
      Object result = p.parse().value;      
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}