/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.struct;

import static com.blackrook.doom.DoomObjectUtils.checkString;
import static com.blackrook.doom.DoomObjectUtils.checkNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.blackrook.commons.AbstractVector;
import com.blackrook.commons.list.List;
import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomObject;
import com.blackrook.doom.util.DoomUtil;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * This class represents the contents of a Boom Engine SWITCHES
 * lump. This lump contains extended information regarding textures
 * used for in-game switches.
 * <p>
 * NOTE: readDoomBytes() will read chunks of 20 bytes until it detects
 * the end of the SWITCHES entry list, NOT once it detects the end of 
 * the stream. A SWITCHES entry that contains the list terminal code is not
 * used, as per the the specs outlined in boomref.txt (in the BOOM developer
 * notes).
 * 
 * @author Matthew Tropiano
 */
public class Switches implements DoomObject
{
	/** Enumeration of game types. */
	public static enum Game
	{
		/** No entry should contain this - internal use only. */
		TERMINAL_SPECIAL,
		SHAREWARE_DOOM,
		DOOM,
		ALL;
	}
	
	/** List of entries. */
	protected AbstractVector<Entry> entryList;
	
	/**
	 * Creates a new SWITCHES lump.
	 */
	public Switches()
	{
		entryList = new List<Entry>(20);
	}
	
	/**
	 * Returns a reference to the list of switch entries.
	 */
	public AbstractVector<Entry> getSwitchList()
	{
		return entryList;
	}

	/**
	 * Returns a switch entry at a specific index.
	 * @param i the index of the entry to return.
	 * @throws IndexOutOfBoundsException  if the index is out of range (< 0 or >= getFlatCount()).
	 */
	public Entry getSwitchEntry(int i)
	{
		return entryList.getByIndex(i);
	}
	
	/**
	 * Removes a switch entry at a specific index.
	 * @param i the index of the entry to remove.
	 * @throws IndexOutOfBoundsException  if the index is out of range (< 0 or >= getSwitchCount()).
	 */
	public Entry removeSwitchEntry(int i)
	{
		return entryList.getByIndex(i);
	}
	
	/**
	 * Returns the amount of switch entries in this lump.
	 */
	public int getSwitchCount()
	{
		return entryList.size();
	}
	
	/**
	 * Adds a switch entry to this lump.
	 * The names must be 8 characters or less.
	 * @param offName	the last name in the sequence.
	 * @param onName the first name in the sequence.
	 * @param game the game type that this switch works with.
	 */
	public void addSwitch(String offName, String onName, Game game)
	{
		entryList.add(new Entry(offName, onName, game));
	}
	
	@Override
	public byte[] getDoomBytes() throws DataExportException
	{
		callDoomCompatibilityCheck();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			for (Entry e : entryList) 
				e.writeDoomBytes(bos);
			(new Entry()).writeDoomBytes(bos); // write blank terminal.
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
		for (Entry e : entryList) e.callDoomCompatibilityCheck();
	}

	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		entryList.clear();
		Entry e = null;
		do {
			e = new Entry();
			e.readDoomBytes(in);
			if (e.game != Game.TERMINAL_SPECIAL)
				entryList.add(e);
	} while (e.game != Game.TERMINAL_SPECIAL);
	}

	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

	/** Entry for Switches. */
	public static class Entry implements DoomObject
	{
		/** The "off" texture name. */
		protected String offName;
		/** The "on" texture name. */
		protected String onName;
		/** The game that this is used for. */
		protected Game game;
		
		/**
		 * Creates a new Entry.
		 */
		Entry()
		{
			offName = "";
			onName = "";
			game = Game.TERMINAL_SPECIAL;
		}

		/**
		 * Creates a new Entry.
		 * @param offName the name of the switch "off" texture.
		 * @param onName the name of the switch "on" texture.
		 * @param game the game type that this switch is used for.
		 */
		Entry(String offName, String onName, Game game)
		{
			this.offName = offName;
			this.onName = onName;
			this.game = game;
		}

		@Override
		public byte[] getDoomBytes() throws DataExportException
		{
			callDoomCompatibilityCheck();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
			try{
				sw.writeASCIIString(DoomUtil.coerceToEntry(offName));
				sw.writeBoolean(false);  // ensure null terminal
				sw.writeASCIIString(DoomUtil.coerceToEntry(onName));
				sw.writeBoolean(false);  // ensure null terminal
				sw.writeShort((short)game.ordinal());
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
		 * Returns the switch "off" position texture.  
		 */
		public String getOffName()
		{
			return offName;
		}

		/**
		 * Returns the switch "on" position texture.  
		 */
		public String getOnName()
		{
			return onName;
		}

		/**
		 * Returns the active game type of the switch.  
		 */
		public Game getGame()
		{
			return game;
		}

		/**
		 * Checks this data structure for data export integrity for the Doom format. 
		 * @throws DataExportException if a bad criterion is found.
		 */
		protected void callDoomCompatibilityCheck() throws DataExportException
		{
			checkNotNull("Game Type", game);
			if (game == Game.TERMINAL_SPECIAL)
				return;
			
			checkString("\"Off\" Texture Name", offName);
			checkString("\"On\" Texture Name", onName);
		}

		@Override
		public void readDoomBytes(InputStream in) throws IOException
		{
			SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
			offName = DoomUtil.nameFix(sr.readASCIIString(9));
			onName = DoomUtil.nameFix(sr.readASCIIString(9));
			game = Game.values()[sr.readShort()];
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
			sb.append("SWITCH "); 
			sb.append(offName);
			sb.append(' ');
			sb.append(onName);
			sb.append(' ');
			sb.append(game.name());
			return sb.toString();
		}
		
	}
	
}
