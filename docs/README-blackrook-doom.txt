Doom Struct (C) Black Rook Software, All rights reserved.
http://www.blackrooksoftware.com

==== Dependent Libraries:

- Black Rook Commons v2.14.0+
- Black Rook Common I/O v2.3.0+
- Black Rook Common Lang v2.3.0+
http://blackrooksoftware.com

==== Introduction

The purpose of the Doom Struct project is to provide a means to read/write
data structures for the Doom Engine and similar derivatives.

==== Features

- Reads all Doom map and data structures in Doom, Hexen/ZDoom, or Strife 
  formats. This includes textures, patches, lines, vertices, things, sectors,
  nodes, palettes, colormaps, text, PNG data, MUS data, flats, blockmaps,
  reject, and even ENDOOM-type VGA lumps.
- Can read/edit Boom-engine data lumps like ANIMATED and SWITCHES. 
- Contains libraries for predicting game types for maps and Wads.
- Can read both WAD and PK3 package types, as well as assist in converting
  between the two.
- Contains a utility class for converting Doom graphics to standard Java
  graphics structures.
- Contains a utility class for visualizing maps for graphic export or
  rendering to a canvas.
- Full UDMF parsing/writing support.

==== Library

Contained in this release is a series of libraries that allow reading, writing,
and extracting data in Doom Engine structures, found in the com.blackrook.doom 
packages. 

==== Other

Copyright (c) 2009-2013 Black Rook Software.
All rights reserved. This program and the accompanying materials
are made available under the terms of the GNU Lesser Public License v2.1
which accompanies this distribution, and is available at
http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html

A copy of the LGPL should have been included in this release (blackrook-license.txt).
If it was not, please contact us for a copy, or to notify us of a distribution
that has not included it. 
 
Contributors:
	Matt Tropiano, Black Rook Software - initial API and implementation

Support: support@blackrooksoftware.com
