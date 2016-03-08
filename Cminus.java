/*
  Created by: Matt Breckon and Dean Way
  File name: Cminus.java
  To run: 
    $ java Cminus [-a] [file_name.cm]
*/
   
import java.io.*;
   
class Cminus {
  static public void main(String args[]) {
    if(args.length > 2) {
      System.out.println("Invalid number of arguments.");
      System.out.println("usage: java Cminus [-a] [file_name.cm]");
      return;
    }
    /* args: -a and file */
    if(args.length == 2) {
      if(!args[0].equals("-a")) {
        System.out.println("Invalid argument: " + args[0]);
        System.out.println("usage: java Cminus [-a] [file_name.cm]");
        return;
      }
      else {
        try {
          parser p = new parser(args[0], new Lexer(new FileReader(args[1])));
          Object result = p.parse().value;      
        } catch(Exception e) {
          /* do cleanup here -- possibly rethrow e */
          e.printStackTrace();
        }
      }
    }
    /* args: file */
    if(args.length == 1) {
      try {
        parser p = new parser(new Lexer(new FileReader(args[0])));
        Object result = p.parse().value;      
      } catch(Exception e) {
        /* do cleanup here -- possibly rethrow e */
        e.printStackTrace();
      }
    }
  }
}