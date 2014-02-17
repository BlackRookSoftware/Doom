/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import com.blackrook.commons.hash.CaseInsensitiveHashedQueueMap;
import com.blackrook.commons.linkedlist.Queue;
import com.blackrook.commons.list.List;
import com.blackrook.doom.util.DoomUtil;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * This is just a basic mapping of WAD entries to a file.
 * The file is NOT kept open after the read, and the file or
 * stream used to gather the WAD metadata is not kept.
 * <p>
 * This may not be added to or changed, and its data may not be read directly,
 * because this is just a mapping of entries. Individual entries may be read
 * for data offset information and then read from the corresponding file or
 * stream.
 * <p>
 * Despite the name, this is not a structure that reads Doom Map information.
 * Use {@link DoomMap} for that purpose.  
 * @since 2.8.0
 * @author Matthew Tropiano
 */
public class WadMap implements DoomWad
{
	/** Type of Wad File (IWAD or PWAD). */
	private Type type;
	/** The list of entries. */
	protected List<DoomWadEntry> entries;
	/** A Hashtable of this Wad's entries. */
	private CaseInsensitiveHashedQueueMap<DoomWadEntry> entryTable;

	private WadMap()
	{
		entries = new List<DoomWadEntry>();
		entryTable = new CaseInsensitiveHashedQueueMap<DoomWadEntry>();
	}
	
	/**
	 * Creates a new WadMap using the contents of a file, denoted by the path.
	 * @param path the path to the file to read.
	 */
	public WadMap(String path) throws IOException
	{
		this(new File(path));
	}
	
	/**
	 * Creates a new WadMap using the contents of a file.
	 * @param f the file to read.
	 */
	public WadMap(File f) throws IOException
	{
		this();
		FileInputStream fis = new FileInputStream(f);
		readWad(fis);
		fis.close();
	}
	
	/**
	 * Creates a new WadMap.
	 * @param in the input stream.
	 */
	public WadMap(InputStream in) throws IOException
	{
		this();
		readWad(in);
	}

	/**
	 * Reads in a WAD structure from an InputStream.
	 * @param in
	 */
	private void readWad(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in, SuperReader.LITTLE_ENDIAN);
		entries.clear();
		entryTable.clear();

		try {
			type = Type.valueOf(sr.readASCIIString(4));
		} catch (IllegalArgumentException e) {
			throw new WadException("Not a WAD file.");
		}
		int entryCount = sr.readInt();
		int contentsize = sr.readInt() - 12;
		
		entries.setCapacity(entryCount);
		
		// skip content.
		in.skip(contentsize);
		
		byte[] entrybuffer = new byte[DoomWadEntry.WADENTRY_LEN];
		for (int x = 0; x < entryCount; x++)
		{
			sr.readBytes(entrybuffer);
			Entry entry = new Entry(entrybuffer);
			addEntry(entry);
		}
	}

	private void addEntry(DoomWadEntry wfe)
	{
		entries.add(wfe);
		entryTable.enqueue(wfe.getName(),wfe);
	}

	@Override	
	public DoomWadEntry getEntry(int n)
	{
		return entries.getByIndex(n);
	}

	@Override	
	public DoomWadEntry getEntry(String s)
	{
		s = DoomUtil.coerceToEntrySize(s);
		Queue<DoomWadEntry> q = entryTable.get(s);
		return q != null ? q.head() : null;
	}

	@Override	
	public DoomWadEntry getEntry(String s, int startingIndex)
	{
		int i = getIndexOf(s, startingIndex);
		return i != -1 ? getEntry(i) : null;
	}

	@Override	
	public DoomWadEntry getLastEntry(String s)
	{
		s = DoomUtil.coerceToEntrySize(s);
		Queue<DoomWadEntry> q = entryTable.get(s);
		return q != null ? q.tail() : null;
	}

	@Override	
	public DoomWadEntry getNthEntry(String s, int n)
	{
		s = DoomUtil.coerceToEntrySize(s);
		Queue<DoomWadEntry> q = entryTable.get(s);
		return q != null ? q.get(n) : null;
	}

	@Override	
	public DoomWadEntry[] getAllEntries()
	{
		DoomWadEntry[] out = new DoomWadEntry[entries.size()];
		entries.toArray(out);
		return out;
	}
	
	@Override	
	public DoomWadEntry[] getAllEntries(String s)
	{
		s = DoomUtil.coerceToEntrySize(s);
		Queue<DoomWadEntry> w = entryTable.get(s);
		DoomWadEntry[] out = null;
		if (w != null)
		{
			out = new DoomWadEntry[w.size()];
			w.toArray(out);
		}
		else
			out = new DoomWadEntry[0];
		return out;
	}

	@Override	
	public int getIndexOf(String entryname)
	{
		return getIndexOf(entryname,0);
	}

	@Override	
	public int getIndexOf(String entryname, int start)
	{
		entryname = DoomUtil.coerceToEntrySize(entryname);
		for (int i = start; i < entries.size(); i++)
			if (entries.getByIndex(i).getName().equalsIgnoreCase(entryname))
				return i;
		return -1;
	}
	
	@Override	
	public int getLastIndexOf(String entryname)
	{
		entryname = DoomUtil.coerceToEntrySize(entryname);
		int out = -1;
		for (int i = 0; i < entries.size(); i++)
			if (entries.getByIndex(i).getName().equalsIgnoreCase(entryname))
				out = i;
		return out;
	}
	
	@Override
	public byte[] getData(int n) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support getData()");
	}

	@Override
	public byte[] getData(String entry) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support getData()");
	}

	@Override
	public byte[] getData(String entry, int start) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support getData()");
	}

	@Override
	public byte[] getData(DoomWadEntry entry) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support getData()");
	}

	@Override
	public InputStream getDataAsStream(int n) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support getDataAsStream()");
	}

	@Override
	public InputStream getDataAsStream(String entry) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support getDataAsStream()");
	}

	@Override
	public InputStream getDataAsStream(String entry, int start) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support getDataAsStream()");
	}

	@Override
	public InputStream getDataAsStream(DoomWadEntry entry) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support getDataAsStream()");
	}

	@Override
	public int getSize()
	{
		return entries.size();
	}

	@Override
	public boolean contains(String entry)
	{
		return entryTable.containsKey(entry);
	}

	@Override
	public boolean contains(String entry, int index)
	{
		return getIndexOf(entry, index) > -1;
	}

	@Override
	public void add(String entryname, byte[] data) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support add()");
	}

	@Override
	public void addAt(int index, String entryname, byte[] data) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support addAt()");
	}

	@Override
	public void addAll(String[] entrynames, byte[][] data) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support addAll()");
	}

	@Override
	public void addMarker(String name) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support addMarker()");
	}

	@Override
	public DoomWadEntry createMarker(String name)
	{
		throw new UnsupportedOperationException("This class does not support createMarker()");
	}

	@Override
	public void replaceEntry(int index, byte[] data) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support replaceEntry()");
	}

	@Override
	public void renameEntry(int index, String newName) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support renameEntry()");
	}

	@Override
	public void deleteEntry(int n) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support deleteEntry()");
	}

	@Override
	public DoomWadEntry[] mapEntries(int startIndex, int maxLength)
	{
		throw new UnsupportedOperationException("This class does not support mapEntries()");
	}

	@Override
	public void unmapEntries(int startIndex, DoomWadEntry[] entries) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support unmapEntries()");
	}

	@Override
	public void setEntries(DoomWadEntry[] entryList) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support setEntries()");
	}

	@Override
	public Iterator<DoomWadEntry> getEntryIterator()
	{
		return entries.iterator();
	}

	@Override
	public boolean isIWAD()
	{
		return type == Type.IWAD;
	}

	@Override
	public boolean isPWAD()
	{
		return type == Type.PWAD;
	}

	/**
	 * This is the structure of WadFile entries.
	 * @author Matthew Tropiano
	 */
	public class Entry implements DoomWadEntry
	{
		/** Entry name. */
		String name;
		/** Entry size.*/
		int size;
		/** Entry offset.*/
		int offset;
		
		/**
		 * Creates a new Entry.
		 * @param n		the name of the entry (coerced to 8 characters, no whitespace).
		 * @param s		the size of the entry.
		 * @param o		the offset of the entry's contents in the WadFile.
		 */
		public Entry(String n, int s, int o)
		{
			name = DoomUtil.coerceToEntrySize(n);
			size = s;
			offset = o;
		}
		
		/**
		 * Creates a new WadEntry.
		 * @param b					the array of bytes to use for the WadEntry.
		 * @throws WadException		if the array is not length WADENTRY_LEN.
		 */
		public Entry(byte[] b) throws WadException
		{
			if (b.length != WADENTRY_LEN)
				throw new WadException("Byte array should be length "+WADENTRY_LEN);
			
			ByteArrayInputStream bis = new ByteArrayInputStream(b);
			SuperReader sr = new SuperReader(bis,SuperReader.LITTLE_ENDIAN);
			try{
				offset = sr.readInt();
				size = sr.readInt();
				name = DoomUtil.nameFix(sr.readASCIIString(DoomUtil.ENTRY_NAME_SIZE));
			} catch (IOException e) {}
		}
		
		@Override
		public int getOffset()
		{
			return offset;
		}
		
		@Override
		public int getSize()
		{
			return size;
		}

		@Override
		public String getName()
		{
			return name;
		}
		
		public String toString()
		{
			return name+" Size: "+size+", Offset: "+offset;
		}

		@Override
		public byte[] toBytes()
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
			try{
				sw.writeInt(offset);
				sw.writeInt(size);
				sw.writeASCIIString(DoomUtil.coerceToEntry(name));
			} catch (IOException e){}
			
			return bos.toByteArray();
		}
	}
}
