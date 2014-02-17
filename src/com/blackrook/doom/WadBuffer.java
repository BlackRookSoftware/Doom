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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import com.blackrook.commons.Common;
import com.blackrook.commons.hash.CaseInsensitiveHashedQueueMap;
import com.blackrook.commons.linkedlist.Queue;
import com.blackrook.commons.list.DataList;
import com.blackrook.commons.list.List;
import com.blackrook.doom.util.DoomUtil;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * An implementation of DoomWad where any and all WAD information is manipulated in memory.
 * This loads everything in the WAD into memory as uninterpreted raw bytes.
 * @author Matthew Tropiano
 */
public class WadBuffer implements DoomWad
{
	/** Type of Wad File (IWAD or PWAD). */
	private Type type;
	/** The data itself. */
	protected DataList content;
	/** The list of entries. */
	protected List<DoomWadEntry> entries;
	/** A Hashtable of this Wad's entries. */
	private CaseInsensitiveHashedQueueMap<DoomWadEntry> entryTable;

	/**
	 * Creates an empty WadBuffer (as a PWAD).
	 */
	public WadBuffer()
	{
		this(Type.PWAD);
	}
	
	/**
	 * Creates an empty WadBuffer with a specific type.
	 */
	public WadBuffer(Type type)
	{
		this.type = type;
		content = new DataList();
		entries = new List<DoomWadEntry>();
		entryTable = new CaseInsensitiveHashedQueueMap<DoomWadEntry>();
	}
	
	/**
	 * Creates a new WadBuffer using the contents of a file, denoted by the path.
	 * @param path the path to the file to read.
	 */
	public WadBuffer(String path) throws IOException
	{
		this(new File(path));
	}
	
	/**
	 * Creates a new WadBuffer using the contents of a file.
	 * @param f the file to read.
	 */
	public WadBuffer(File f) throws IOException
	{
		this();
		FileInputStream fis = new FileInputStream(f);
		readWad(fis);
		fis.close();
	}
	
	/**
	 * Creates a new WadBuffer.
	 * @param in the input stream.
	 */
	public WadBuffer(InputStream in) throws IOException
	{
		this();
		readWad(in);
	}

	/**
	 * Reads in a wad from an InputStream.
	 * @param in
	 */
	private void readWad(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in, SuperReader.LITTLE_ENDIAN);
		content.clear();
		entries.clear();
		entryTable.clear();

		try {
			type = Type.valueOf(sr.readASCIIString(4));
		} catch (IllegalArgumentException e) {
			throw new WadException("Not a WAD file.");
		}
		int entries = sr.readInt();
		int contentsize = sr.readInt() - 12;
		
		byte[] buffer = new byte[65536];
		int bytes = 0;
		int n = 0;
		while (bytes < contentsize)
		{
			n = sr.readBytes(buffer, Math.min(contentsize - bytes, buffer.length));
			content.append(buffer, 0, n);
			bytes += n;
		}
		
		byte[] entrybuffer = new byte[16];
		for (int x = 0; x < entries; x++)
		{
			sr.readBytes(entrybuffer);
			Entry entry = new Entry(entrybuffer);
			addEntry(entry);
		}
	}
	
	/**
	 * Converts a DoomWadEntry offset to the offset into the data vector.
	 */
	private int getContentOffset(DoomWadEntry entry)
	{
		return entry.getOffset() - 12; 
	}
	
	/**
	 * Converts a content offset to a DoomWadEntry offset.
	 */
	private int toEntryOffset(int contentOffset)
	{
		return contentOffset + 12; 
	}
	
	private void addEntry(DoomWadEntry wfe)
	{
		entries.add(wfe);
		entryTable.enqueue(wfe.getName(),wfe);
	}
	
	private DoomWadEntry removeEntry(int n)
	{
		DoomWadEntry wfe = entries.removeIndex(n);
		return wfe;
	}

	/**
	 * Writes the contents of this buffer out to an output stream in Wad format.
	 * Does not close the stream.
	 * @param out the output stream to write to.
	 * @throws IOException if a problem occurs during the write.
	 * @since 2.5.0
	 */
	public void writeToStream(OutputStream out) throws IOException
	{
		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		sw.writeASCIIString(type.name());
		sw.writeInt(entries.size());		// number of entries.
		sw.writeInt(12 + content.size());	// offset to entry list.
		sw.writeBytes(content.toByteArray());
		for (DoomWadEntry entry : entries)
		{
			sw.writeInt(entry.getOffset());
			sw.writeInt(entry.getSize());
			sw.writeASCIIString(DoomUtil.coerceToEntry(entry.getName()));
		}
	}
	
	/**
	 * Writes the contents of this buffer out to a file in Wad format.
	 * The target file will be overwritten.
	 * @param f the file to write to.
	 * @throws IOException if a problem occurs during the write.
	 * @since 2.5.0
	 */
	public void writeToFile(File f) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(f);
		writeToStream(fos);
		Common.close(fos);
	}
	
	@Override
	public void add(String entryname, byte[] data) throws IOException
	{
		String name = DoomUtil.coerceToEntrySize(entryname);
		int ofs = toEntryOffset(content.size());
		Entry e = new Entry(name, data.length, ofs);
		content.append(data);
		entries.add(e);
		entryTable.enqueue(name, e);
	}

	@Override
	public void addAt(int index, String entryname, byte[] data) throws IOException
	{
		String name = DoomUtil.coerceToEntrySize(entryname);
		int ofs = toEntryOffset(content.size());
		Entry e = new Entry(name, data.length, ofs);
		content.append(data);
		entries.add(index, e);
		entryTable.enqueue(name, e);
	}
	
	@Override
	public void addAll(String[] entrynames, byte[][] data) throws IOException
	{
		for (int i = 0; i < entrynames.length; i++)
			add(entrynames[i], data[i]);
	}

	@Override
	public void addMarker(String name) throws IOException
	{
		add(name, new byte[0]);
	}

	@Override
	public DoomWadEntry createMarker(String name)
	{
		return new Entry(name, 0, 0);
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
	public void deleteEntry(int n) throws IOException
	{
		// get removed entry.
		DoomWadEntry wfe = removeEntry(n);
		if (wfe == null)
			throw new IOException("Index is out of range.");

		int cofs = getContentOffset(wfe);

		content.delete(cofs, wfe.getSize());
		
		// adjust offsets from last entry.
		for (int i = n; i < entries.size(); i++)
		{
			Entry e = (Entry)entries.getByIndex(i);
			e.offset -= wfe.getSize();
		}
	}

	@Override
	public void renameEntry(int index, String newName) throws IOException
	{
		Entry entry = (Entry)getEntry(index);
		if (entry == null)
			throw new IOException("Index is out of range.");
		
		entry.name = DoomUtil.coerceToEntrySize(newName);
	}

	@Override
	public void replaceEntry(int index, byte[] data) throws IOException
	{
		DoomWadEntry entry = removeEntry(index);
		if (entry == null)
			throw new IOException("Index is out of range.");
		
		String name = entry.getName();
		addAt(index, name, data);
	}

	@Override
	public DoomWadEntry[] mapEntries(int startIndex, int maxLength)
	{
		if (startIndex < 0)
			throw new IllegalArgumentException("Starting index cannot be less than 0.");

		int len = Math.min(maxLength, getSize() - startIndex);
		if (len <= 0)
			return new DoomWadEntry[0];
		DoomWadEntry[] out = new DoomWadEntry[len];
		for (int i = 0; i < len; i++)
			out[i] = getEntry(startIndex + i);
		return out;
	}

	@Override
	public void unmapEntries(int startIndex, DoomWadEntry[] entryList) throws IOException
	{
		for (int i = 0; i < entryList.length; i++)
			entries.replace(startIndex + i, entryList[i]);
	}

	@Override
	public void setEntries(DoomWadEntry[] entryList) throws IOException
	{
		entries.clear();
		for (DoomWadEntry entry : entryList)
			entries.add(entry);
	}

	@Override	
	public byte[] getData(int n) throws IOException
	{
		return getData(getEntry(n));
	}
	
	@Override	
	public byte[] getData(String entry) throws IOException
	{
		return getData(getEntry(entry));
	}

	@Override	
	public byte[] getData(String entry, int start) throws IOException
	{
		int i = getIndexOf(entry,start);
		return i != -1 ? getData(i) : null;
	}
	
	@Override	
	public byte[] getData(DoomWadEntry entry) throws IOException
	{
		byte[] out = new byte[entry.getSize()];
		try {
			content.getData(getContentOffset(entry), out);
		} catch (IndexOutOfBoundsException e) {
			throw new IOException(e);
		}
		return out;
	}
	
	@Override	
	public InputStream getDataAsStream(int n) throws IOException
	{
		DoomWadEntry e = getEntry(n);
		if (e == null)
			return null;
		byte[] b = getData(e); 
		if (b == null)
			return null;
		return new ByteArrayInputStream(b);
	}
	
	@Override	
	public InputStream getDataAsStream(String entry) throws IOException
	{
		DoomWadEntry e = getEntry(entry);
		if (e == null)
			return null;
		byte[] b = getData(e); 
		if (b == null)
			return null;
		return new ByteArrayInputStream(b);
	}

	@Override	
	public InputStream getDataAsStream(String entry, int start) throws IOException
	{
		int i = getIndexOf(entry,start);
		byte[] b = i != -1 ? getData(i) : null;
		if (b == null)
			return null;
		return new ByteArrayInputStream(b);
	}
	
	@Override	
	public InputStream getDataAsStream(DoomWadEntry entry) throws IOException
	{
		return new ByteArrayInputStream(getData(entry));
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
	public Iterator<DoomWadEntry> getEntryIterator()
	{
		return (Iterator<DoomWadEntry>)entries.iterator();
	}
	
	/**
	 * Sets the type of WAD that this is.
	 * @param type the new type.
	 * @since 2.5.0
	 */
	public void setType(Type type)
	{
		this.type = type;
	}
	
	/**
	 * Gets the type of WAD that this is.
	 * @since 2.5.0
	 */
	public Type getType()
	{
		return type;
	}
	
	@Override
	public boolean isIWAD()
	{
		return getType() == Type.IWAD;
	}
	
	@Override
	public boolean isPWAD()
	{
		return getType() == Type.PWAD;
	}
	
	@Override
	public int getSize()
	{
		return entries.size();
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
		
		public void setName(String val)
		{
			name = val;
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
