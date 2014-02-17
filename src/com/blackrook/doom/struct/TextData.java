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
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.blackrook.commons.Common;
import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomObject;

/**
 * Holds textual information. In the Java world, this data is natively UTF-16 encoded.
 * In a Doom data structure, this is completely ASCII.
 * <p>
 * NOTE: The readDoomBytes() method will read until it reaches the end of the stream for the text
 * data. DO NOT use this method on an InputStream without a defined end. 
 * @author Matthew Tropiano
 */
public class TextData implements DoomObject
{
	/** Internal buffer that this class wraps. */
	private StringBuilder builder;
	
	/**
	 * Creates an new instance of mutable text data.
	 */
	public TextData()
	{
		builder = new StringBuilder();
	}
	
	/**
	 * Creates an new instance of mutable text data
	 * with data already inside it.
	 */
	public TextData(CharSequence seq)
	{
		this();
		builder.append(seq);
	}
	
	/**
	 * Returns the encapsulated StringBuilder that contains this class's data.
	 */
	public StringBuilder getBuffer()
	{
		return builder;
	}
	
	@Override
	public byte[] getDoomBytes() throws DataExportException
	{
		try {
			return builder.toString().getBytes("ASCII");
		} catch (UnsupportedEncodingException e)
		{
			DataExportException ex = new DataExportException("This platform doesn't seem to support ASCII encoding.");
			ex.initCause(e);
			throw ex;
		}
	}

	@Override
	public boolean isDoomCompatible()
	{
		return Charset.isSupported("ASCII");
	}

	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		builder.delete(0, builder.length());
		builder.append(Common.getTextualContents(in, "ASCII"));
	}

	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

	@Override
	public String toString()
	{
		return builder.toString();
	}

}
