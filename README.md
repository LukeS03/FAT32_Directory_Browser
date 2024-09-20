# FAT32 Directory Entry Browser
## Overview
This program aims to be able to browse FAT32 file-systems without having to use the eponymous File Allocation Table. This
is based on the principle that although computers *can* store files in discontinuous blocks, they generally prefer
to store them in contiguous blocks for reasons of efficiency - especially with old magnetic hard drives where fragmentation
could be a much bigger issue than with modern SSD cards. Most file recovery software seems to rely on using file headers
to extract files from a corrupted disk. However, this method requires those files to have a recognisable header and to
be supported by whatever software you may be using. However, assuming that the directory entries are still intact, it 
should be possible to extract a good majority of files (on a disk with low fragmentation) by simply extracting the
file-size and sector of that file from the directory table and using those to extract the file from a disk image.

In the future I think it'd be interesting to see if it's possible to scan a disk for directory tables and use those to
construct a tree of that file-system, allowing for files to be recovered even if the BPB has been corrupted and thus
obscured the root directory. In reality though, this is an extremely extremely extremely niche use-case, but I think it's
a fun and interesting project to work on. The FAT32 file system is surprisingly simple, meaning it's a solid file-system
for someone with even beginner programming knowledge to work on. It has been fun trawling through online documentation
to construct an idea of how this file-system works and to translate that into code and to see that code work.

## Background
Why did I make this? I have a 3DS whose memory card I managed to corrupt in a very weird way. I think what happened to it
was that I yanked out a previous memory card out of my computer while it was still being written to, so that when I put my
3DS' memory card into my computer it started writing that data to my memory card. When I put my 3DS' memory card into 
my computer, it started to appear with the partition type and name of that other memory card that had been in my computer
previously. An extremely strange bug.

I can reformat my card easily and just throw it in my 3DS. But I really really really wanted my Pokemon saves. A very 
kind person at the Nintendo Homebrew Discord helped guide me through trying to recover my files from the SD Card using 
TestDisk, and by playing with TestDisk a little bit I was able to browse my directories! Hooray! I could even use
TestDisk to extract my files from the SD card.

But when I went to extract those files from my SD card, there was a big issue: the files were corrupted and they would
cause errors in any program I tried to put them into. Examining the raw hex bytes and comparing them to previous backup
files I had lying around my computer, I realised that the files were the same after around ~65536 bytes before the
corruption started. I observed this same phenomenom across several files that I was able to extract.

I got reading, and I realised that the FAT32 file system used on the Nintendo 3DS' SD card is stupidly simple. I quickly
realised that the ~65536 bytes I was able to extract corresponded to the amount of bytes that one entry in the FAT table
represented. Interesting. I figured that this meant that the FAT table had likely been corrupted, but that the directories 
and files themselves were still completely usable. Perhaps somewhere in the vast array of bits and whatever the heck
SD cards store their data in, my Pokemon saves and other data was just sitting there, obscured by a corrupted FAT
but in contiguous data blocks.

## Milestones
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

