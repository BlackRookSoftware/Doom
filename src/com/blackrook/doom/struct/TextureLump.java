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

import com.blackrook.commons.map.CaseInsensitiveMappedVector;
import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomObject;
import com.blackrook.doom.StrifeObject;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * This is the lump that contains a bunch of textures.
 * All textures are stored in here, usually named TEXTURE1 or TEXTURE2 in the WAD.
 * Strife stores its texture lump (and its textures) a little differently, so be sure
 * to read it correctly.
 * @since 2.7.0, this now extends {@link CaseInsensitiveMappedVector}.
 * @author Matthew Tropiano
 */
public class TextureLump extends CaseInsensitiveMappedVector<Texture> implements DoomObject, StrifeObject
{
	/**
	 * Creates a new TextureLump with a default starting capacity.
	 */
	public TextureLump()
	{
		super();
	}

	/**
	 * Creates a new TextureLump with a specific starting capacity.
	 */
	public TextureLump(int capacity)
	{
		super(capacity);
	}

	/**
	 * Gets the index of a texture in this lump by its name.
	 * @param name the name of the texture.
	 * @return a valid index of found, or -1 if not.
	 */
	public int getTextureIndex(String name)
	{
		return getIndexUsingKey(name);
	}
	
	/**
	 * Gets the index of a texture in this lump by its name.
	 * @param name the name of the texture.
	 * @return a valid index of found, or -1 if not.
	 */
	public Texture getByName(String name)
	{
		return getUsingKey(name);
	}
	
	@Override
	public byte[] getDoomBytes() throws DataExportException
	{
		callDoomCompatibilityCheck();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
		try{
			sw.writeInt(size());
			
			byte[][] data = new byte[size()][];

			int n = 0;
			for (Texture t : this)
				data[n++] = t.getDoomBytes();
			
			int offset = (size()+1) * 4;
			
			for (byte[] b : data)
			{
				sw.writeInt(offset);
				offset += b.length;
			}

			for (byte[] b : data)
				sw.writeBytes(b);
			
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
		int i = 0;
		for (Texture t : this)
		{
			try { t.callDoomCompatibilityCheck(); 	} catch (DataExportException e)
			{
				throw new DataExportException("Texture "+i+" in Texture Lump: "+e.getMessage());
			}
		}
	}

	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		clear();
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		int n = sr.readInt();
		
		in.skip(n*4);
		
		while(n-- > 0)
		{
			Texture t = new Texture();
			t.readDoomBytes(in);
			add(t);
		}
	}

	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

	@Override
	public byte[] getStrifeBytes() throws DataExportException
	{
		callDoomCompatibilityCheck();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
		try{
			sw.writeInt(size());
			
			byte[][] data = new byte[size()][];

			int n = 0;
			for (Texture t : this)
				data[n++] = t.getStrifeBytes();
			
			int offset = (size()+1) * 4;
			
			for (byte[] b : data)
			{
				sw.writeInt(offset);
				offset += b.length;
			}

			for (byte[] b : data)
				sw.writeBytes(b);
			
		} catch (IOException e){}
		
		return bos.toByteArray();
	}

	@Override
	public boolean isStrifeCompatible()
	{
		return isDoomCompatible();
	}

	@Override
	public void readStrifeBytes(InputStream in) throws IOException
	{
		clear();
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		int n = sr.readInt();
		
		in.skip(n*4);
		
		while(n-- > 0)
		{
			Texture t = new Texture();
			t.readStrifeBytes(in);
			add(t);
		}
	}
	
	@Override
	public void writeStrifeBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getStrifeBytes());
	}

	@Override
	protected String getMappingKey(Texture object)
	{
		return object.getName();
	}

}
