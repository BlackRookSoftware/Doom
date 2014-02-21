# Black Rook Doom Struct

Copyright (c) 2009-2014 Black Rook Software. All rights reserved.  
[http://blackrooksoftware.com/projects.htm?name=doom](http://blackrooksoftware.com/projects.htm?name=doom)  
[https://github.com/BlackRookSoftware/Doom](https://github.com/BlackRookSoftware/Doom)

### Required Libraries

Black Rook Commons 2.14.0+  
[https://github.com/BlackRookSoftware/Common](https://github.com/BlackRookSoftware/Common)

Black Rook Common I/O 2.3.0+  
[https://github.com/BlackRookSoftware/CommonIO](https://github.com/BlackRookSoftware/CommonIO)

Black Rook Common Lang 2.3.0+  
[https://github.com/BlackRookSoftware/CommonLang](https://github.com/BlackRookSoftware/CommonLang)

### Introduction

The purpose of the Doom Struct project is to provide a means to read/write
data structures for the Doom Engine and similar derivatives.

### Features

- Reads all Doom map and data structures in Doom, Hexen/ZDoom, or Strife 
  formats. This includes textures, patches, lines, vertices, things, sectors,
  nodes, palettes, colormaps, text, PNG data, MUS data, flats, blockmaps,
  reject, and even ENDOOM-type VGA lumps.
- Can read/edit Boom-engine data lumps like ANIMATED and SWITCHES. 
- Contains libraries for predicting game types for maps and Wads.
- Can read both WAD and PK3 package types.
- Contains a utility class for converting Doom graphics to standard Java
  graphics structures.
- Contains a utility class for visualizing maps for graphic export or
  rendering to a canvas.
- Full UDMF parsing/writing support.

### Library

Contained in this release is a series of libraries that allow reading, writing,
and extracting data in Doom Engine structures, found in the com.blackrook.doom 
packages. 

### Other

This program and the accompanying materials
are made available under the terms of the GNU Lesser Public License v2.1
which accompanies this distribution, and is available at
http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html

A copy of the LGPL should have been included in this release (LICENSE.txt).
If it was not, please contact us for a copy, or to notify us of a distribution
that has not included it. 
