#!/bin/zsh

javac -classpath . *.java -g
java -Xmx1024M server
