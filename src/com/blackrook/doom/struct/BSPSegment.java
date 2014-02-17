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
 * BSP Segment information for a map in Doom.
 * @author Matthew Tropiano
 */
public class BSPSegment implements DoomObject
{
	/** Direction along linedef (same). */
	public final static int DIRECTION_SAME_AS_LINEDEF = 0;
	/** Direction along linedef (opposite). */
	public final static int DIRECTION_OPPOSITE_LINEDEF = 1;

	/** Binary angle. */
	public final static int ANGLE_EAST = 0;
	/** Binary angle. */
	public final static int ANGLE_NORTH = 16384;
	/** Binary angle. */
	public final static int ANGLE_SOUTH = -16384;
	/** Binary angle. */
	public final static int ANGLE_WEST = -32768;
	
	/** This Seg's start vertex index reference. */
	protected int vertexStart;
	/** This Seg's end vertex index reference. */
	protected int vertexEnd;
	/** This Seg's angle. */
	protected int angle;
	/** This Seg's linedef index. */
	protected int linedefIndex;
	/** This Seg's direction. */
	protected int direction;
	/** This Seg's offset along linedef. */
	protected int offset;

	/**
	 * Creates a new BSP Segment.
	 */
	public BSPSegment()
	{
		vertexStart = -1;
		vertexEnd = -1;
		angle = 0;
		linedefIndex = -1;
		direction = DIRECTION_SAME_AS_LINEDEF;
		offset = 0;
	}
	
	/** Sets this Seg's start vertex index reference. */
	public void setVertexStart(int val)		{vertexStart = val;}
	/** Sets this Seg's end vertex index reference. */
	public void setVertexEnd(int val)		{vertexEnd = val;}
	/** Sets this Seg's angle in degrees. */
	public void setAngle(int val)			{angle = val;}
	/** Sets this Seg's linedef index. */
	public void setLinedefIndex(int val)	{linedefIndex = val;}
	/** Sets this Seg's direction. */
	public void setDirection(int val)		{direction = val;}
	/** Sets this Seg's linedef offset. */
	public void setOffset(int val)			{offset = val;}

	/** Gets this Seg's start vertex index reference. */
	public int getVertexStart()				{return vertexStart;}
	/** Gets this Seg's end vertex index reference. */
	public int getVertexEnd()				{return vertexEnd;}
	/** Gets this Seg's angle in degrees. */
	public int getAngle()					{return angle;}
	/** Gets this Seg's linedef index. */
	public int getLinedefIndex()			{return linedefIndex;}
	/** Gets this Seg's direction. */
	public int getDirection()				{return direction;}
	/** Gets this Seg's linedef offset. */
	public int getOffset()					{return offset;}

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
			sw.writeShort((short)(((int)vertexStart) & 0x0ffff));
			sw.writeShort((short)(((int)vertexEnd) & 0x0ffff));
			sw.writeShort((short)(((int)angle) & 0x0ffff));
			sw.writeShort((short)(((int)linedefIndex) & 0x0ffff));
			sw.writeShort((short)(((int)direction) & 0x0ffff));
			sw.writeShort((short)(((int)offset) & 0x0ffff));
		} catch (IOException e){}
		
		return bos.toByteArray();
	}

	/**
	 * Checks this data structure for data export integrity for the Doom format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	protected void callDoomCompatibilityCheck() throws DataExportException
	{
		checkShortUnsigned("Vertex Start", vertexStart);
		checkShortUnsigned("Vertex End", vertexEnd);
		checkShortUnsigned("Angle", angle);
		checkShortUnsigned("Linedef Index", linedefIndex);
		checkShortUnsigned("Direction", direction);
		checkShortUnsigned("Linedef Offset", offset);
	}

	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		vertexStart = sr.readShort() & 0x0ffff;
		vertexEnd = sr.readShort() & 0x0ffff;
		angle = sr.readShort() & 0x0ffff;
		linedefIndex = sr.readShort() & 0x0ffff;
		direction = sr.readShort() & 0x0ffff;
		offset = sr.readShort() & 0x0ffff;
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
		return 12;
	}


}
