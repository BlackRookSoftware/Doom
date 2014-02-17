/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.struct;

import static com.blackrook.doom.DoomObjectUtils.checkString;
import static com.blackrook.doom.DoomObjectUtils.checkRange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.blackrook.commons.list.List;
import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomObject;
import com.blackrook.doom.util.DoomUtil;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * This class represents the contents of a Boom Engine ANIMATED
 * lump. This lump contains extended information regarding animated
 * flats and textures.
 * <p>
 * NOTE: readDoomBytes() will read chunks of 23 bytes until it detects
 * the end of the ANIMATED entry list, NOT once it detects the end of 
 * the stream.
 * 
 * @author Matthew Tropiano
 */
public class Animated implements DoomObject
{
	/**
	 * Enumeration of Animated Entry Texture types. 
	 */
	public static enum TextureType
	{
		FLAT,
		TEXTURE;
	}
	
	/** List of flats. */
	protected List<Entry> flatList;
	/** List of textures. */
	protected List<Entry> textureList;
	
	/**
	 * Creates a new ANIMATED lump.
	 */
	public Animated()
	{
		flatList = new List<Entry>(20);
		textureList = new List<Entry>(20);
	}
	
	/**
	 * Returns a reference to the list of flat entries.
	 */
	public List<Entry> getFlatList()
	{
		return flatList;
	}

	/**
	 * Returns a reference to the list of texture entries.
	 */
	public List<Entry> getTextureList()
	{
		return textureList;
	}

	/**
	 * Returns an ANIMATED flat entry at a specific index.
	 * @param i the index of the entry to return.
	 * @throws IndexOutOfBoundsException  if the index is out of range (< 0 or >= getFlatCount()).
	 */
	public Entry getFlatEntry(int i)
	{
		return flatList.getByIndex(i);
	}
	
	/**
	 * Removes an ANIMATED flat entry at a specific index.
	 * @param i the index of the entry to remove.
	 * @throws IndexOutOfBoundsException  if the index is out of range (< 0 or >= getFlatCount()).
	 */
	public Entry removeFlatEntry(int i)
	{
		return flatList.getByIndex(i);
	}
	
	/**
	 * Returns the amount of flat entries in this lump.
	 */
	public int getFlatCount()
	{
		return flatList.size();
	}
	
	/**
	 * Adds a flat entry to this lump.
	 * The names must include a number and be 8 characters or less.
	 * @param lastName	the last name in the sequence.
	 * @param firstName the first name in the sequence.
	 * @param ticks the amount of ticks between each frame.
	 */
	public void addFlat(String lastName, String firstName, int ticks)
	{
		flatList.add(new Entry(false, lastName, firstName, ticks));
	}
	
	/**
	 * Returns an ANIMATED texture entry at a specific index.
	 * @param i the index of the entry to return.
	 * @throws IndexOutOfBoundsException  if the index is out of range (< 0 or >= getFlatCount()).
	 */
	public Entry getTextureEntry(int i)
	{
		return textureList.getByIndex(i);
	}
	
	/**
	 * Removes an ANIMATED texture entry at a specific index.
	 * @param i the index of the entry to remove.
	 * @throws IndexOutOfBoundsException  if the index is out of range (< 0 or >= getTextureCount()).
	 */
	public Entry removeTextureEntry(int i)
	{
		return textureList.getByIndex(i);
	}
	
	/**
	 * Returns the amount of texture entries in this lump.
	 */
	public int getTextureCount()
	{
		return textureList.size();
	}
	
	/**
	 * Adds a texture entry to this lump.
	 * The names must be 8 characters or less.
	 * @param lastName	the last name in the sequence.
	 * @param firstName the first name in the sequence.
	 * @param ticks the amount of ticks between each frame.
	 */
	public void addTexture(String lastName, String firstName, int ticks)
	{
		addTexture(lastName, firstName, ticks, false);
	}
	
	/**
	 * Adds a texture entry to this lump.
	 * The names must be 8 characters or less.
	 * @param lastName	the last name in the sequence.
	 * @param firstName the first name in the sequence.
	 * @param ticks the amount of ticks between each frame.
	 * @param decals if true, allows decals to be placed on this texture, false if not.
	 */
	public void addTexture(String lastName, String firstName, int ticks, boolean decals)
	{
		textureList.add(new Entry(true, decals, lastName, firstName, ticks));
	}
	
	@Override
	public byte[] getDoomBytes() throws DataExportException
	{
		callDoomCompatibilityCheck();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try{
			for (Entry e : flatList) 
				e.writeDoomBytes(bos);
			for (Entry e : textureList) 
				e.writeDoomBytes(bos);
			(new Entry()).writeDoomBytes(bos);
		} catch (DataExportException e) {
			throw e;
		} catch (IOException e) {}

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
		for (Entry e : flatList) e.callDoomCompatibilityCheck();
		for (Entry e : textureList) e.callDoomCompatibilityCheck();
	}

	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		flatList.clear();
		textureList.clear();
		Entry e = null;
		do {
			e = new Entry();
			e.readDoomBytes(in);
			if (e.type != null)
			{
				if (e.type == TextureType.FLAT)
					flatList.add(e);
				else
					textureList.add(e);
			}
	} while (e.type != null);
	}

	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

	/** Flat entry for ANIMATED. */
	public static class Entry implements DoomObject
	{
		/** Is this a texture entry? If not, it's a flat. */
		protected TextureType type;
		/** The last texture name. */
		protected String lastName;
		/** The first texture name. */
		protected String firstName;
		/** Allows decals. */
		protected boolean allowsDecals;
		/** The amount of ticks between each frame. */
		protected int ticks;
		
		/**
		 * Creates a new Entry (terminal type).
		 */
		Entry()
		{
			this(null, "", "", 1);
		}
		
		/**
		 * Creates a new Entry.
		 * @param texture	is this a texture entry (as opposed to a flat)?
		 * @param lastName	the last name in the sequence.
		 * @param firstName the first name in the sequence.
		 * @param ticks the amount of ticks between each frame.
		 */
		Entry(boolean texture, String lastName, String firstName, int ticks)
		{
			this(texture ? TextureType.TEXTURE : TextureType.FLAT, false, lastName, firstName, ticks);
		}

		/**
		 * Creates a new Entry.
		 * @param texture	is this a texture entry (as opposed to a flat)?
		 * @param allowsDecals if true, this texture allows decals.
		 * @param lastName	the last name in the sequence.
		 * @param firstName the first name in the sequence.
		 * @param ticks the amount of ticks between each frame.
		 */
		Entry(boolean texture, boolean allowsDecals, String lastName, String firstName, int ticks)
		{
			this(texture ? TextureType.TEXTURE : TextureType.FLAT, lastName, firstName, ticks);
		}

		/**
		 * Creates a new Entry.
		 * @param type		what is the type of this animated entry (TEXTURE/FLAT)?
		 * @param lastName	the last name in the sequence.
		 * @param firstName the first name in the sequence.
		 * @param ticks the amount of ticks between each frame.
		 */
		Entry(TextureType type, String lastName, String firstName, int ticks)
		{
			this(type, false, lastName, firstName, ticks);
		}

		/**
		 * Creates a new Entry.
		 * @param type what is the type of this animated entry (TEXTURE/FLAT)?
		 * @param allowsDecals if true, this texture allows decals.
		 * @param lastName	the last name in the sequence.
		 * @param firstName the first name in the sequence.
		 * @param ticks the amount of ticks between each frame.
		 */
		Entry(TextureType type, boolean allowsDecals, String lastName, String firstName, int ticks)
		{
			this.type = type;
			this.allowsDecals = allowsDecals;
			this.lastName = lastName;
			this.firstName = firstName;
			this.ticks = ticks;
		}

		/**
		 * Is this a texture entry?
		 * @return true if it is, false if not (it's a flat, then).
		 */
		public boolean isTexture()
		{
			return type == TextureType.TEXTURE;
		}

		/**
		 * Returns the texture type of the entry (for FLAT or TEXTURE? null if terminal entry).
		 */
		public TextureType getType()
		{
			return type;
		}

		/**
		 * Returns if this texture allows decals on it, despite it being animated.
		 * True if so, false if not.
		 */
		public boolean getAllowsDecals()
		{
			return allowsDecals;
		}

		/**
		 * Returns the last texture/flat name in the animation sequence.
		 */
		public String getLastName()
		{
			return lastName;
		}

		/**
		 * Returns the first texture/flat name in the animation sequence.
		 */
		public String getFirstName()
		{
			return firstName;
		}

		/**
		 * Returns the amount of ticks between each frame.
		 */
		public int getTicks()
		{
			return ticks;
		}

		@Override
		public byte[] getDoomBytes() throws DataExportException
		{
			callDoomCompatibilityCheck();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
			try {
				if (type != null) 
				{
					byte b = (byte)type.ordinal();
					b |= allowsDecals ? 0x02 : 0x00;
					sw.writeByte(b);
				}
				else
					sw.writeByte((byte)-1);
				sw.writeASCIIString(DoomUtil.coerceToEntry(lastName));
				sw.writeBoolean(false); // ensure null terminal
				sw.writeASCIIString(DoomUtil.coerceToEntry(firstName));
				sw.writeBoolean(false); // ensure null terminal
				sw.writeInt(ticks);
			} catch (IOException e) {}
			
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
			if (type == null)
				return;
				
			checkString("Last Animation Name", lastName);
			checkString("First Animation Name", firstName);
			checkRange("Frame Ticks", 1, Integer.MAX_VALUE, ticks);
		}

		@Override
		public void readDoomBytes(InputStream in) throws IOException
		{
			SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
			byte b = sr.readByte();
			if (b != -1)
			{
				if ((b & 0x01) == 0)
					type = TextureType.FLAT;
				else if ((b & 0x01) != 0)
					type = TextureType.TEXTURE;
				
				if ((b & 0x02) != 0)
					allowsDecals = true;
			}
			else
			{
				type = null;
				return;
			}
			lastName = DoomUtil.nameFix(sr.readASCIIString(9));
			firstName = DoomUtil.nameFix(sr.readASCIIString(9));
			ticks = sr.readInt();
		}

		@Override
		public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
		{
			out.write(getDoomBytes());
		}
		
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("ANIMATED "); 
			sb.append(type != null ? TextureType.values()[type.ordinal()] : "[TERMINAL]");
			sb.append(' ');
			sb.append(lastName);
			sb.append(' ');
			sb.append(firstName);
			sb.append(' ');
			sb.append(ticks);
			sb.append(' ');
			sb.append(allowsDecals ? "DECALS" : "");
			return sb.toString();
		}
		
	}
	
}
