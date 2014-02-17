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

import static com.blackrook.doom.DoomObjectUtils.*;

/**
 * BSP Subsector information that lists all of the BSP
 * segment indices for a sector. 
 * @author Matthew Tropiano
 */
public class BSPSubsector implements DoomObject
{
	/** This Subsector's BSP Segment index list. */
	protected int segCount;
	/** This Subsector's BSP Segment index list. */
	protected int segStartIndex;

	/**
	 * Creates a new BSP Segment.
	 */
	public BSPSubsector()
	{
		segStartIndex = -1;
	}
	
	/**
	 * Gets the amount of BSPSegments pointed to by this subsector.
	 */
	public int getSegCount()
	{
		return segCount;
	}

	/**
	 * Sets the amount of BSPSegments pointed to by this subsector.
	 */
	public void setSegCount(int segCount)
	{
		this.segCount = segCount;
	}

	/**
	 * Gets the starting offset index of this subsector's BSPSegments in the Segs lump.
	 */
	public int getSegStartIndex()
	{
		return segStartIndex;
	}

	/**
	 * Sets the starting offset index of this subsector's BSPSegments in the Segs lump.
	 */
	public void setSegStartIndex(int segStartIndex)
	{
		this.segStartIndex = segStartIndex;
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

	@Override
	public byte[] getDoomBytes() throws DataExportException
	{
		callDoomCompatibilityCheck();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
		try{
			sw.writeShort((short)(segCount & 0x0ffff));
			sw.writeShort((short)(segStartIndex & 0x0ffff));
		} catch (IOException e){}
		
		return bos.toByteArray();
	}

	/**
	 * Checks this data structure for data export integrity for the Doom format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	protected void callDoomCompatibilityCheck() throws DataExportException
	{
		checkShortUnsigned("Segment Count", segCount);
		checkShortUnsigned("Segment Start Index ", segStartIndex);
	}

	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		segCount = sr.readShort() & 0x0ffff;
		segStartIndex = sr.readShort() & 0x0ffff;
	}

	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

	/**
	 * Returns the length of this structure in Doom-formatted bytes. 
	 */
	public static int getDoomLength()
	{
		return 4;
	}

}
