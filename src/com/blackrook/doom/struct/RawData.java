/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.struct;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.blackrook.commons.Common;
import com.blackrook.commons.list.DataList;
import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomObject;

/**
 * Class for holding "other" Doom data that does not have an explicit type.
 * This could even be for different file types or other binary data that has no
 * object class capable of reading the desired data in this library.
 * <p>
 * NOTE: The readDoomBytes() method will read until it reaches the end of the stream for the raw
 * data. DO NOT use this method on an InputStream without a defined end.
 * @author Matthew Tropiano
 */
public class RawData implements DoomObject
{
	/** Byte data buffer. */
	public DataList data;
	
	/**
	 * Constructs a new raw data handling object instance. 
	 */
	public RawData()
	{
		data = new DataList(2048);
	}
	
	/**
	 * Constructs a new raw data handling object instance with existing data. 
	 */
	public RawData(byte[] b)
	{
		data = new DataList(b.length);
		data.append(b);
	}
	
	/**
	 * Returns the encapsulated DataVector that contains this class's data.
	 */
	public DataList getBuffer()
	{
		return data;
	}

	@Override
	public byte[] getDoomBytes() throws DataExportException
	{
		return data.toByteArray();
	}

	@Override
	public boolean isDoomCompatible()
	{
		return true;
	}

	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		data.append(Common.getBinaryContents(in));
	}

	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

}
