/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.struct;

import static com.blackrook.doom.DoomObjectUtils.checkString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.blackrook.commons.AbstractVector;
import com.blackrook.commons.map.CaseInsensitiveMappedVector;
import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomObject;
import com.blackrook.doom.util.DoomUtil;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * A list of patches that make up textures.
 * This contains the info for the names of all graphics that are considered patches,
 * which is graphical data eligible for use in textures.
 * @since 2.3.0, this now extends {@link AbstractVector}.
 * @author Matthew Tropiano
 */
public class PatchNameLump extends CaseInsensitiveMappedVector<String> implements DoomObject
{
	/**
	 * Creates a new PatchNameLump with a default starting capacity.
	 */
	public PatchNameLump()
	{
		super();
	}

	/**
	 * Creates a new PatchNameLump with a specific starting capacity.
	 */
	public PatchNameLump(int capacity)
	{
		super(capacity);
	}

	/**
	 * Gets the index of a patch name in this lump by its name.
	 * @param name the name of the patch.
	 * @return a valid index of found, or -1 if not.
	 */
	public int getIndex(String name)
	{
		return getIndexUsingKey(name);
	}
	
	@Override
	public byte[] getDoomBytes() throws DataExportException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
		try{
			sw.writeInt(size());
			for (String s : this)
				sw.writeASCIIString(DoomUtil.coerceToEntry(s));
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
		for (String s : this)
			checkString("Patch name " + (i++), s);
	}

	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		clear();
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		int n = sr.readInt();
		while (n-- > 0)
			add(DoomUtil.nameFix(sr.readASCIIString(DoomUtil.ENTRY_NAME_SIZE)));
	}

	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

	@Override
	protected String getMappingKey(String object)
	{
		return object.toString();
	}

}
