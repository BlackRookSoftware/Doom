/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.struct;

import static com.blackrook.doom.DoomObjectUtils.*;

import java.io.*;

import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomObject;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * This class holds Vertex information: an x-coordinate and a y-coordinate.
 * @author Matthew Tropiano
 */
public class Vertex implements DoomObject
{
	/** Vertex x-position. */
	protected float xpos;
	/** Vertex y-position. */
	protected float ypos;

	/**
	* Creates a new Vertex.
	*/	
	public Vertex()
	{
		reset();
	}

	/**
	 * Resets this object's data to its defaults.
	 * Note that it may not necessarily set the object's data to valid information.
	 */
	public void reset()
	{
		xpos = 0;
		ypos = 0;
	}
	
	/** Gets the X-coordinate of this vertex. */
	public float getX() 		{return xpos;}
	/** Sets the X-coordinate of this vertex. */
	public void setX(float x)	{xpos = x;}
	/** Gets the Y-coordinate of this vertex. */
	public float getY() 		{return ypos;}
	/** Sets the Y-coordinate of this vertex. */
	public void setY(float y)	{ypos = y;}

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
			sw.writeShort((short)(((int)xpos) & 0x0ffff));
			sw.writeShort((short)(((int)ypos) & 0x0ffff));
		} catch (IOException e){}
		
		return bos.toByteArray();
	}

	/**
	 * Checks this data structure for data export integrity for the Doom format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	protected void callDoomCompatibilityCheck() throws DataExportException
	{
		checkWhole("X-position", xpos);
		checkShort("X-position", (short)xpos);
		checkWhole("Y-position", ypos);
		checkShort("Y-position", (short)ypos);
	}

	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in, SuperReader.LITTLE_ENDIAN);
		xpos = sr.readShort();
		ypos = sr.readShort();
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

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Vertex ");
		sb.append(" (");
		sb.append(xpos);
		sb.append(", ");
		sb.append(ypos);
		sb.append(")");
		return sb.toString();
	}
	
}
