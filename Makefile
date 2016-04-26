# Makefile for parser.jar

all:
	./gradlew jar

clean:
	./gradlew clean
	rm compiler.jar
