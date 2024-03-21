# CBWS Translator
This project seeks to make balance patch mods for PSASBR more accessible. It is a command line utility that
can parse and translate CBWS files for greater readability.

## Dependencies
- JDK 17+
- Maven 3.8.x+

## Building
From the project root directory, which contains the `pom.xml` file, run:
```mvn clean package```
An executable JAR file will be created in the `target` directory.

## Usage
The program is run from the command line. The first argument is the path to the CBWS file to be translated.
Example:
```java -jar cbws-translator-1.0.0.jar -f "path/to/file.cbws"``` The program will list options in the command line from 
there

## What are CBWS files?
CBWS are binary files that define attributes of character actions, such as attack/super hit volumes, 
animation speed, sound effects played, and special effects spawned. Since these files are mostly raw bytes, 
they are difficult to read and understand.

## Features
- Translate CBWS file and display (mostly) in order execution of its contents.
- Limited modification of CBWS file contents.
  - Alter Hit Volumes
  - Alter Super Armor
  - Alter Animation Speed
  - Remove Functions