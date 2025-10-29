# poor-mans-unarchiver

A simple command-line tool to extract various archive formats using built-in system utilities.

The program will scan the specified directory for supported archive files and extract them in place, under an "Unzipped" directory.

## Supported Formats
- .zip
- .gz

## How to Use

Change the path of the directory that contains the archives you want to extract:

```
// Inside App.java, line 3
String baseDirPath = "your/directory/path/here";
```

Compile and run the program.

## Personal use case

I recovered some hundreds of GB of data from some old drives - part of the data was archived.
I had a bunch of .zip and .gz files that I needed to extract quickly without installing any additional software.

## Note
The tool is multithreaded and capped at 70% CPU usage to avoid overloading the system. Can change this in the code if needed.
```
// Inside App.java, line 7
int maxParallelism = (int) (cores * 0.7);
```