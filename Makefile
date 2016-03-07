# Created by: Matt Breckon and Dean Way
# File name: Makefile

JAVA=java
JAVAC=javac
JFLEX=jflex
CLASSPATH=-classpath ./java/cup.jar:.
CUP=$(JAVA) $(CLASSPATH) java_cup.Main

DIRS = programs
FILES = $(wildcard $(DIRS:=/*.cm))

all: Main.class

Main.class: absyn/*.java parser.java sym.java Lexer.java Main.java

%.class: %.java
	$(JAVAC) $(CLASSPATH)  $^

Lexer.java: c-.flex
	$(JFLEX) c-.flex

parser.java: c-.cup
	$(CUP) -dump -expect 3 < c-.cup

test:
	@for file in $(FILES) ; do \
		echo PROGRAM $$file: ; \
		java $(CLASSPATH) Main $$file ; \
		echo ----------------------- ; \
	done

clean:
	rm -f parser.java Lexer.java sym.java *.class absyn/*.class *~