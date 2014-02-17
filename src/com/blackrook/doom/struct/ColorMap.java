/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.struct;

import static com.blackrook.doom.DoomObjectUtils.checkByteUnsigned;

import java.io.*;

import com.blackrook.commons.math.RMath;
import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomObject;
import com.blackrook.io.SuperReader;

/**
 * This is a single entry that indexes the palette indices for color lookup.
 * This is NOT the representation of the complete COLORMAP lump, see ColorMapLump
 * for that implementation. This is merely a single entry in that lump. Other commercial
 * IWAD lumps that are colormaps are the TRANTBL lumps in Hexen.
 * @author Matthew Tropiano
 */
public class ColorMap implements DoomObject
{
	/** The number of total indices in a standard Doom color map. */
	public static final int
	NUM_INDICES = 256;
	
	/** The index list in this map. */
	protected int[] indices;

	/**
	 * Creates a new colormap where all indices point to palette color 0.
	 */
	public ColorMap()
	{
		indices = new int[NUM_INDICES];
		for (int i = 0; i < NUM_INDICES; i++)
			indices[i] = 0;
	}
	
	/**
	 * Creates a new colormap by copying the contents of another.
	 */
	public ColorMap(ColorMap map)
	{
		System.arraycopy(map.indices, 0, indices, 0, NUM_INDICES);
	}
	
	/**
	 * Resets the color map to where each color is mapped to its own index
	 * (index 0 is palette color 0 ... index 255 is palette color 255).
	 */
	public void setIdentity()
	{
		for (int i = 0; i < NUM_INDICES; i++)
			indices[i] = i;
	}
	
	/**
	 * Creates a color map where each color is mapped to its own index
	 * (index 0 is palette color 0 ... index 255 is palette color 255).
	 * @return a new color map with the specified indices already mapped.
	 */
	public static ColorMap createIdentityMap()
	{
		ColorMap out = new ColorMap();
		out.setIdentity();
		return out;
	}

	/**
	 * Sets a colormap translation by remapping groups of contiguous indices.
	 * @param startIndex the starting replacement index.
	 * @param endIndex the ending replacement index.
	 * @param startValue the starting replacement value.
	 * @param endValue the ending replacement value.
	 */
	public void setTranslation(int startIndex, int endIndex, int startValue, int endValue)
	{
		int min = Math.min(startIndex, endIndex);
		int max = Math.max(startIndex, endIndex);
		
		float len = Math.abs(startValue - endValue) + 1f;
		
		for (int i = min; i <= max; i++)
			indices[i] = (int)RMath.linearInterpolate((i - min) / len, startValue, endValue);
	}
	
	/**
	 * Returns the palette index of a specific index in the map.
	 * @param index	the index number of the entry.
	 * @throws ArrayIndexOutOfBoundsException if index > NUM_INDICES or < 0.
	 */
	public int getPaletteIndex(int index)
	{
		return indices[index];
	}
	
	/**
	 * Sets the palette index of a specific index in the map.
	 * @param index	the index number of the entry.
	 * @param paletteIndex the new index.
	 * @throws ArrayIndexOutOfBoundsException if index > NUM_INDICES or < 0.
	 */
	public void setPaletteIndex(int index, int paletteIndex)
	{
		indices[index] = paletteIndex;
	}
	
	@Override
	public boolean isDoomCompatible()
	{
		return true;
	}

	@Override
	public byte[] getDoomBytes() throws DataExportException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (int i : indices)
			bos.write(i & 0x0ff);
		return bos.toByteArray();
	}

	/**
	 * Checks this data structure for data export integrity for the Doom format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	protected void callDoomCompatibilityCheck() throws DataExportException
	{
		int i = 0;
		for (int x : indices)
			checkByteUnsigned("Color map index " + (i++), x);
	}

	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in, SuperReader.LITTLE_ENDIAN); 
		for (int i = 0; i < NUM_INDICES; i++)
			indices[i] = sr.readByte() & 0x0ff;
	}

	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

	@Override
	public String toString()
	{
		return java.util.Arrays.toString(indices);
	}
	
}
