# Created by: Matt Breckon and Dean Way
# File name: Makefile

JAVA=java
JAVAC=javac
JFLEX=jflex
CLASSPATH=-classpath ./java/cup.jar:.
CUP=$(JAVA) $(CLASSPATH) java_cup.Main

DIRS = programs
FILES = $(wildcard $(DIRS:=/*.cm))

all: Cminus.class

Cminus.class: symb/*.java symb/exceptions/*.java absyn/*.java parser.java sym.java Lexer.java Cminus.java

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

test_all:
	@for file in $(FILES) ; do \
		echo PROGRAM $$file: ; \
		java $(CLASSPATH) Cminus -a -s $$file ; \
		echo ----------------------- ; \
	done

test_tree:
	@for file in $(FILES) ; do \
		echo PROGRAM $$file: ; \
		java $(CLASSPATH) Cminus -a $$file ; \
		echo ----------------------- ; \
	done

test_table:
	@for file in $(FILES) ; do \
		echo PROGRAM $$file: ; \
		java $(CLASSPATH) Cminus -s $$file ; \
		echo ----------------------- ; \
	done

clean:
	rm -f parser.java Lexer.java sym.java *.class absyn/*.class symb/*.class *~