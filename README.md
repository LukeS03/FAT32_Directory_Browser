# FAT32 Directory Entry Browser

A FAT32 directory entry browser written in Java. Very rough and unpolished. I started writing this to rescue some save-files from a MicroSD card I was using on my 3DS.

# ToDo:
* Properly comment my code 

# Milestones
* Read a directory table entry
* Read a directory table
* Load a random access image file.
* Locate the partition table and create partition table entries.
* Select a partition and load a directory or file from it.

## Intended Features
* Browse directories
* Manually set CHS values or extract them from file system image.
* Mark files to be extracted
* Extract files using only their start cluster and their file size.
* CLI parameters controlled by GNU getopt.

## Super duper distant hypothetical ideas
* Search for deleted or 'orphaned' directories in a file-system image.

## Copyright Licence
This project is licensed under the terms of the MIT licence as included in [LICENCE.md](LICENCE.md)

