/*
  Created by: Matt Breckon and Dean Way
  File name: Cminus.java
  To run: 
    $ java Cminus [-a] [-s] [-c] [file_name.cm]
*/
   
import java.io.*;
import java.util.*;
   
class Cminus {
  /* list of legal command line arguments */
  static private List<String> cmds = Arrays.asList("-a", "-s", "-c");

  /* terminal argument error readout */
  static private void argsError() {
    System.out.println("usage: java Cminus [-a] [-s] [-c] [file_name.cm]");
    System.out.println("       -a: display abstract syntax tree");
    System.out.println("       -s: display symbol tables");
    System.out.println("       -c: compile file to assembly");
  }

  static public void main(String args[]) {
    String filename;
    List<String> args_list = new ArrayList<String>();

    /* validate number of arguments */
    if(args.length > cmds.size()) {
      System.out.println("Invalid number of arguments.");
      argsError();
      return;
    }
    /* validate command line arguments */
    for(int i = 0; i < args.length - 1; i++) {
      if(!cmds.contains(args[i])) {
        System.out.println(args[i] + " is not a valid argument.");
        argsError();
        return;
      }
      if(args[i].equals("-a")) args_list.add("-a");
      if(args[i].equals("-s")) args_list.add("-s");
      if(args[i].equals("-c")) args_list.add("-c");
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
      parser p = new parser(args_list, new Lexer(new FileReader(filename)));
      Object result = p.parse().value;      
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}