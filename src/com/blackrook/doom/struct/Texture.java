/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.struct;

import static com.blackrook.doom.DoomObjectUtils.checkShort;
import static com.blackrook.doom.DoomObjectUtils.checkShortUnsigned;
import static com.blackrook.doom.DoomObjectUtils.checkString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.blackrook.commons.AbstractVector;
import com.blackrook.commons.comparators.CaseInsensitiveComparator;
import com.blackrook.commons.list.List;
import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomObject;
import com.blackrook.doom.StrifeObject;
import com.blackrook.doom.util.DoomUtil;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * This class represents a single texture 
 * entry in a TEXTURE1/TEXTURE2/TEXTURES lump.
 * @author Matthew Tropiano
 * @since 2.8.2 This class implements {@link Comparable}.
 */
public class Texture implements DoomObject, StrifeObject, Comparable<Texture>
{
	/** Texture name. */
	private String name;
	/** Width of texture. */
	private int width;
	/** Height of texture. */
	private int height;
	/** List of patches. */
	private List<Patch> patches;

	public Texture()
	{
		name = "UNNAMED";
		width = 0;
		height = 0;
		patches = new List<Patch>(2);
	}
	
	/**
	 * Gets the name of this texture.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this texture.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the width of this texture in bytes.
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * Sets the width of this texture in bytes.
	 */
	public void setWidth(int width)
	{
		this.width = width;
	}

	/**
	 * Gets the height of this texture in bytes.
	 */
	public int getHeight()
	{
		return height;
	}

	/**
	 * Sets the height of this texture in bytes.
	 */
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	/**
	 * Adds a patch entry to this texture.
	 * @param p	the patch to add.
	 */
	public void addPatch(Patch p)
	{
		patches.add(p);
	}

	/**
	 * Removes a patch entry from this texture.
	 * @param p	the patch to remove.
	 */
	public boolean removePatch(Patch p)
	{
		return patches.remove(p);
	}

	/**
	 * Removes a patch entry from this texture by index.
	 * @param i	the index of the patch to remove.
	 */
	public Patch removePatch(int i)
	{
		return patches.removeIndex(i);
	}
	
	/**
	 * Gets a patch from this texture.
	 * @param i		the index of the patch.
	 */
	public Patch getPatch(int i)
	{
		return patches.getByIndex(i);
	}

	/**
	 * Returns the amount of patches on this texture.
	 */
	public int getPatchCount()
	{
		return patches.size();
	}
	
	/**
	 * Gets the list of patches.
	 */
	public AbstractVector<Patch> getPatchList()
	{
		return patches;
	}
	
	@Override
	public byte[] getDoomBytes() throws DataExportException
	{
		callDoomCompatibilityCheck();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
		try{
			sw.writeASCIIString(DoomUtil.coerceToEntry(name));
			sw.writeUnsignedShort(0);
			sw.writeUnsignedShort(0);
			sw.writeUnsignedShort(width);
			sw.writeUnsignedShort(height);
			sw.writeUnsignedShort(0);
			sw.writeUnsignedShort(0);
			sw.writeUnsignedShort(patches.size());
			for (Patch p : patches)
				sw.writeBytes(p.getDoomBytes());
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
		checkString("Name", name);
		checkShortUnsigned("Width", width);
		checkShortUnsigned("Height", height);
		checkShortUnsigned("Number of patches", patches.size());
	}
	
	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		name = DoomUtil.nameFix(sr.readASCIIString(DoomUtil.ENTRY_NAME_SIZE));
		sr.readShort();
		sr.readShort();
		width = sr.readUnsignedShort();
		height = sr.readUnsignedShort();
		sr.readShort();
		sr.readShort();
		
		patches.clear();
		
		int n = sr.readUnsignedShort();
		while (n-- > 0)
		{
			Patch p = new Patch();
			p.readDoomBytes(in);
			patches.add(p);
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
			sw.writeASCIIString(DoomUtil.coerceToEntry(name));
			sw.writeUnsignedShort(0);
			sw.writeUnsignedShort(0);
			sw.writeUnsignedShort(width);
			sw.writeUnsignedShort(height);
			sw.writeUnsignedShort(patches.size());
			for (Patch p : patches)
				sw.writeBytes(p.getStrifeBytes());
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
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		name = DoomUtil.nameFix(sr.readASCIIString(DoomUtil.ENTRY_NAME_SIZE));
		sr.readShort();
		sr.readShort();
		width = sr.readUnsignedShort();
		height = sr.readUnsignedShort();
		
		int n = sr.readUnsignedShort();
		while (n-- > 0)
		{
			Patch p = new Patch();
			p.readStrifeBytes(in);
			patches.add(p);
		}
	}

	@Override
	public void writeStrifeBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getStrifeBytes());
	}

	@Override
	public int compareTo(Texture t)
	{
		return CaseInsensitiveComparator.getInstance().compare(getName(), t.getName());
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Texture \"");
		sb.append(name);
		sb.append("\" (");
		sb.append(width);
		sb.append("x");
		sb.append(height);
		sb.append(") ");
		sb.append(patches.toString());
		return sb.toString();
	}
	
	/**
	 * Singular patch entry for a texture.
	 * @author Matthew Tropiano
	 */
	public static class Patch implements DoomObject, StrifeObject
	{
		/** Horizontal offset of the patch. */
		private int originX;
		/** Vertical offset of the patch. */
		private int originY;
		/** Index of patch in patch names lump to use. */
		private int patchIndex;

		public Patch()
		{
			originX = 0;
			originY = 0;
			patchIndex = 0;
		}
		
		/** Gets the horizontal offset of the patch. */
		public int getOriginX()
		{
			return originX;
		}

		/** Sets the horizontal offset of the patch. */
		public void setOriginX(int originX)
		{
			this.originX = originX;
		}

		/** Gets the vertictal offset of the patch. */
		public int getOriginY()
		{
			return originY;
		}

		/** Sets the vertical offset of the patch. */
		public void setOriginY(int originY)
		{
			this.originY = originY;
		}

		/** Gets the patch's index into the patch name lump. */
		public int getPatchIndex()
		{
			return patchIndex;
		}

		/** Sets the patch's index into the patch name lump. */
		public void setPatchIndex(int patchIndex)
		{
			this.patchIndex = patchIndex;
		}

		@Override
		public byte[] getDoomBytes() throws DataExportException
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
			try{
				sw.writeShort((short)originX);
				sw.writeShort((short)originY);
				sw.writeUnsignedShort(patchIndex);
				sw.writeUnsignedShort(1);
				sw.writeUnsignedShort(0);
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
			checkShort("Patch Origin X", originX);
			checkShort("Patch Origin Y", originY);
			checkShortUnsigned("Patch Index", patchIndex);
		}
		
		@Override
		public void readDoomBytes(InputStream in) throws IOException
		{
			SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
			originX = sr.readShort();
			originY = sr.readShort();
			patchIndex = sr.readUnsignedShort();
			sr.readShort();
			sr.readShort();
		}
		
		@Override
		public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
		{
			out.write(getDoomBytes());
		}

		@Override
		public byte[] getStrifeBytes() throws DataExportException
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
			try{
				sw.writeShort((short)originX);
				sw.writeShort((short)originY);
				sw.writeUnsignedShort(patchIndex);
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
			SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
			originX = sr.readShort();
			originY = sr.readShort();
			patchIndex = sr.readUnsignedShort();
		}

		@Override
		public void writeStrifeBytes(OutputStream out) throws IOException, DataExportException
		{
			out.write(getStrifeBytes());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("Patch ");
			sb.append(patchIndex);
			sb.append(" (");
			sb.append(originX);
			sb.append(", ");
			sb.append(originY);
			sb.append(")");
			return sb.toString();
		}
		
	}
	
}
