/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.struct;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.blackrook.commons.AbstractVector;
import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomObject;

/**
 * This lump holds a collection of ColorMaps, representing those found in the
 * COLORMAP lump of a standard IWAD.
 * @since 2.3.0, this now extends {@link AbstractVector}.
 * @author Matthew Tropiano
 */
public class ColorMapLump extends AbstractVector<ColorMap> implements DoomObject
{
	public static final int DOOM_COLORMAP_SIZE = 34;
	
	/**
	 * Creates a new ColorMapLump with a default starting capacity.
	 */
	public ColorMapLump()
	{
		super();
	}

	/**
	 * Creates a new ColorMapLump with a specific starting capacity.
	 */
	public ColorMapLump(int capacity)
	{
		super(capacity);
	}

	@Override
	public byte[] getDoomBytes() throws DataExportException
	{
		callDoomCompatibilityCheck();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try{
			for (ColorMap cm : this)
				bos.write(cm.getDoomBytes());
		} catch (IOException e){}
		
		return bos.toByteArray();
	}

	@Override
	public boolean isDoomCompatible()
	{
		try {
			callDoomCompatibilityCheck();
		} catch (DataExportException e) {
			return false;
		}
		return true;
	}

	/**
	 * Checks this data structure for data export integrity for the Doom format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	protected void callDoomCompatibilityCheck() throws DataExportException
	{
		if (size() != DOOM_COLORMAP_SIZE)
			throw new DataExportException("Lump must have "+DOOM_COLORMAP_SIZE+" color maps stored in it.");
	}

	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		clear();
		for (int i = 0; i < DOOM_COLORMAP_SIZE; i++)
		{
			ColorMap cm = new ColorMap();
			cm.readDoomBytes(in);
			add(cm);
		}
	}

	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

}
