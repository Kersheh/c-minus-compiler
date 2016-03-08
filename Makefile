# Created by: Matt Breckon and Dean Way
# File name: Makefile

JAVA=java
JAVAC=javac
JFLEX=jflex
CLASSPATH=-classpath ./java/cup.jar:.
CUP=$(JAVA) $(CLASSPATH) java_cup.Cminus

DIRS = programs
FILES = $(wildcard $(DIRS:=/*.cm))

all: Cminus.class

Cminus.class: absyn/*.java parser.java sym.java Lexer.java Cminus.java

%.class: %.java
	$(JAVAC) $(CLASSPATH)  $^

Lexer.java: c-.flex
	$(JFLEX) c-.flex

parser.java: c-.cup
	$(CUP) -expect 3 < c-.cup

test:
	@for file in $(FILES) ; do \
		echo PROGRAM $$file: ; \
		java $(CLASSPATH) Cminus $$file ; \
		echo ----------------------- ; \
	done

clean:
	rm -f parser.java Lexer.java sym.java *.class absyn/*.class *~