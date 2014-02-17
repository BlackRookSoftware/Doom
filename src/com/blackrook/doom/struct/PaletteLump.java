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
import com.blackrook.doom.HexenObject;

/**
 * This lump holds a collection of palettes, representing those found in the
 * PLAYPAL lump of a standard IWAD.
 * @since 2.3.0, this now extends {@link AbstractVector}.
 * @author Matthew Tropiano
 */
public class PaletteLump extends AbstractVector<Palette> implements DoomObject, HexenObject
{
	public static final int DOOM_PALETTE_SIZE = 14;
	public static final int HEXEN_PALETTE_SIZE = 28;
	
	/**
	 * Creates a new PaletteLump with a default starting capacity.
	 */
	public PaletteLump()
	{
		super();
	}

	/**
	 * Creates a new PaletteLump with a specific starting capacity.
	 */
	public PaletteLump(int capacity)
	{
		super(capacity);
	}

	@Override
	public byte[] getDoomBytes() throws DataExportException
	{
		callDoomCompatibilityCheck();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try{
			for (Palette p : this)
				bos.write(p.getDoomBytes());
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
		if (size() != DOOM_PALETTE_SIZE)
			throw new DataExportException("Lump must have "+DOOM_PALETTE_SIZE+" palettes stored in it.");
	}

	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		clear();
		for (int i = 0; i < DOOM_PALETTE_SIZE; i++)
		{
			Palette p = new Palette();
			p.readDoomBytes(in);
			add(p);
		}
	}

	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

	@Override
	public byte[] getHexenBytes() throws DataExportException
	{
		callHexenCompatibilityCheck();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try{
			for (Palette p : this)
				bos.write(p.getDoomBytes());
		} catch (IOException e){}
		
		return bos.toByteArray();
	}

	@Override
	public boolean isHexenCompatible()
	{
		try {
			callHexenCompatibilityCheck();
		} catch (DataExportException e) {
			return false;
		}
		return true;
	}

	/**
	 * Checks this data structure for data export integrity for the ZDoom/Hexen format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	protected void callHexenCompatibilityCheck() throws DataExportException
	{
		if (size() != HEXEN_PALETTE_SIZE)
			throw new DataExportException("Lump must have "+HEXEN_PALETTE_SIZE+" palettes stored in it.");
	}

	@Override
	public void readHexenBytes(InputStream in) throws IOException
	{
		clear();
		for (int i = 0; i < HEXEN_PALETTE_SIZE; i++)
		{
			Palette p = new Palette();
			p.readDoomBytes(in);
			add(p);
		}
	}

	@Override
	public void writeHexenBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

}
