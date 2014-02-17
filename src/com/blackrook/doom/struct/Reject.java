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

import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomObject;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * Represents the Reject lump.
 * The reject lump is a lookup grid that hold information on what sectors can
 * "see" other sectors on the map used for thing sight algorithms. 
 * @author Matthew Tropiano
 */
public class Reject implements DoomObject
{
	/** The reject grid itself. */
	private boolean[][] grid;
	
	/**
	 * Creates a new blank reject grid.
	 * @param sectors	the number of sectors.
	 */
	public Reject(int sectors)
	{
		grid = new boolean[sectors][sectors];
	}
	
	/**
	 * Checks whether a sector is visible from another.
	 */
	public boolean getSectorIsVisibleTo(int sectorIndex, int targetSectorIndex)
	{
		return grid[targetSectorIndex][sectorIndex];
	}
	
	/**
	 * Sets whether a sector is visible from another.
	 */
	public void setSectorIsVisibleTo(int sectorIndex, int targetSectorIndex, boolean flag)
	{
		grid[targetSectorIndex][sectorIndex] = flag;
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
		SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
		try{
			for (int i = 0; i < grid.length; i++)
				for (int j = 0; j < grid[i].length; j++)
					sw.writeBit(grid[i][j]);
			sw.flushBits();
		} catch (IOException e){}
		
		return bos.toByteArray();
	}

	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length && in.available() > 0; j++)
				grid[i][j] = sr.readBit();
		sr.byteAlign();
	}

	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

}
