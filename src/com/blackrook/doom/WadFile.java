/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom;

import java.io.*;
import java.util.Iterator;

import com.blackrook.commons.Common;
import com.blackrook.commons.hash.CaseInsensitiveHashedQueueMap;
import com.blackrook.commons.linkedlist.Queue;
import com.blackrook.commons.list.List;
import com.blackrook.doom.util.DoomUtil;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * The class that reads WadFile information and provides random access to Wad files.
 * <p>
 * Use of this class is recommended for reading WAD information or small additions of data, as the overhead needed to
 * do so is minimal in this class. Bulk reads/additions/writes/changes are best left for the {@link WadBuffer} class. 
 * Many writing I/O operations will cause the opened file to be changed many times, the length of time of 
 * which being dictated by the length of the entry list (as the list grows, so does the time it takes to write/change it).
 * </p>
 * @author Matthew Tropiano
 */
public class WadFile extends RandomAccessFile implements DoomWad, Closeable
{
	/** WAD File's name (equivalent to File.getName()). */
	private String fileName;
	/** WAD File's path (equivalent to File.getPath()). */
	private String filePath;
	/** WAD File's absolute path (equivalent to File.getAbsolutePath()). */
	private String fileAbsolutePath;
	
	/** List of this Wad's entries. */
	private List<DoomWadEntry> entries;

	/** A Hashtable of this Wad's entries. */
	private CaseInsensitiveHashedQueueMap<DoomWadEntry> entryTable;

	/** Type of Wad File (IWAD or PWAD). */
	private Type type;

	/** Offset of the beginning of the entry list. */
	private int entryListOffset;
	
	/**
	 * Opens a WadFile from a file specified by "path."
	 * @param path	the path to the File;
	 * @throws IOException if the file can't be read.
	 * @throws FileNotFoundException if the file can't be found.
	 * @throws SecurityException if you don't have permission to access the file.
	 * @throws WadException if the file isn't a Wad file.
	 * @throws NullPointerException if "path" is null.
	 */
	public WadFile(String path) throws IOException, WadException
	{
		this(new File(path));
	}

	/**
	 * Opens a WadFile from a file.
	 * @param f	the file.
	 * @throws IOException if the file can't be read.
	 * @throws FileNotFoundException if the file can't be found.
	 * @throws SecurityException if you don't have permission to access the file.
	 * @throws WadException if the file isn't a Wad file.
	 * @throws NullPointerException if "f" is null.
	 */
	public WadFile(File f) throws IOException, WadException
	{
		super(f,"rws");
		byte[] buffer = new byte[4];

		// read header
		read(buffer);
		String head = new String(buffer,"ASCII");
		if (!head.equals(Type.IWAD.toString()) && !head.equals(Type.PWAD.toString()))
			throw new WadException("Not a Wad file or supported Wad file type.");

		if (head.equals(Type.IWAD.toString()))
			type = Type.IWAD;
			
		if (head.equals(Type.PWAD.toString()))
			type = Type.PWAD;
		
		fileName = f.getName();
		filePath = f.getPath();
		fileAbsolutePath = f.getAbsolutePath();
		
		read(buffer);
		int size = SuperReader.bytesToInt(buffer,SuperReader.LITTLE_ENDIAN);

		read(buffer);
		entryListOffset = SuperReader.bytesToInt(buffer,SuperReader.LITTLE_ENDIAN);
		
		entries = new List<DoomWadEntry>((size + 1) * 2);
		entryTable = new CaseInsensitiveHashedQueueMap<DoomWadEntry>(Math.max(3*size/4,2));
		
		// seek to entry list.
		seek(entryListOffset);
		
		// read entries.
		byte[] entrybytes = new byte[Entry.WADENTRY_LEN];
		for (int i = 0; i < size; i++)
		{
			read(entrybytes);
			Entry wfe = new Entry(entrybytes);
			if (wfe.getName().length() > 0 || wfe.getSize() > 0)
				entries.add(wfe);
			entryTable.enqueue(wfe.getName(), wfe);
		}
	}

	/**
	 * Creates a new, empty WadFile and returns a reference to it.
	 * @param path	the path of the new file in the form of a String.
	 * @return		a reference to the newly created WadFile, already open.
	 * @throws IOException if the file can't be written.
	 * @throws NullPointerException if "path" is null.
	 */
	public static WadFile createWadFile(String path) throws IOException
	{
		return createWadFile(new File(path));
	}

	/**
	 * Creates a new, empty WadFile (PWAD Type) and returns a reference to it.
	 * @param f		the file object referring to the new Wad.
	 * @return		a reference to the newly created WadFile, already open.
	 * @throws IOException if the file can't be written.
	 * @throws NullPointerException if "f" is null.
	 */
	public static WadFile createWadFile(File f) throws IOException
	{
		FileOutputStream fo = new FileOutputStream(f);
		SuperWriter sw = new SuperWriter(fo,SuperWriter.LITTLE_ENDIAN);
		sw.writeASCIIString(Type.PWAD.toString());
		sw.writeInt(0);		// number of entries.
		sw.writeInt(12);	// offset to entry list.
		fo.close();
		try{
			return new WadFile(f);
		// never reached (HOPEFULLY. if this happens, holy internal errors, Batman!)
		} catch (WadException e) {return null;}
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
		DoomWadEntry e = getEntry(n);
		return e != null ? getData(e) : null;
	}
	
	@Override	
	public byte[] getData(String entry) throws IOException
	{
		DoomWadEntry e = getEntry(entry);
		return e != null ? getData(e) : null;
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
		seek(entry.getOffset());
		byte[] out = new byte[entry.getSize()];
		if (read(out) < entry.getSize())
			throw new IOException("Reached unexpected end of file.");
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
		byte[] b = getData(entry);
		return b != null ? new ByteArrayInputStream(b) : null;
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
	
	/**
	 * Adds a file to this WadFile.
	 * The entry name that is used for this addition is a corrected version of 
	 * the name of the file, extension omitted.
	 * The overhead for multiple additions may be expensive I/O-wise.
	 * @param f							the file to add.
	 * @throws FileNotFoundException	if the file cannot be found.
	 * @throws IOException				if the file cannot be read/written.
	 * @throws NullPointerException 	if "f" is null.
	 */
	public void add(File f) throws IOException
	{
		String fname = f.getName();
		int i = fname.indexOf(".");
		fname = i != -1 ? fname.substring(0,i) : fname;
		addAs(f,fname);
	}
	
	/**
	 * Adds a file to this WadFile, using s as the name of the entry.
	 * The overhead for multiple additions may be expensive I/O-wise.
	 * @param f				the file to add.
	 * @param s				the name of the entry to add this as (corrected automatically).
	 * @throws FileNotFoundException	if the file cannot be found.
	 * @throws IOException				if the file cannot be read/written.
	 * @throws NullPointerException 	if "f" or "s" is null.
	 */
	public void addAs(File f, String s) throws IOException
	{
		add(s, Common.getBinaryContents(f));
	}
	
	@Override	
	public void add(String entryname, byte[] data) throws IOException
	{
		Entry entry = new Entry(entryname, data.length, entryListOffset);
		entries.add(entry);
		entryTable.enqueue(entry.getName(),entry);

		// add the data.
		seek(entryListOffset);
		write(data);
		entryListOffset += data.length;
		
		// rewrite the list.
		writeEntryList();

		// write the number of entries and the new offset
		writeHeader();
	}
	
	@Override
	public void addAt(int index, String entryname, byte[] data) throws IOException
	{
		Entry entry = new Entry(entryname, data.length, entryListOffset);
		entries.add(index, entry);
		entryTable.enqueue(entry.getName(),entry);

		// add the data.
		seek(entryListOffset);
		write(data);
		entryListOffset += data.length;
		
		// rewrite the list.
		writeEntryList();

		// write the number of entries and the new offset
		writeHeader();
	}

	@Override
	public void addAll(String[] entrynames, byte[][] data) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		int ofs = entryListOffset;
		int dlen = 0;
		
		for (int i = 0; i < entrynames.length; i++)
		{
			Entry entry = new Entry(entrynames[i], data[i].length, ofs);
			entries.add(entry);
			entryTable.enqueue(entry.getName(), entry);
			dlen += data[i].length;
			ofs += data[i].length;
			bos.write(data[i]);
		}
		
		// add the data.
		seek(entryListOffset);
		write(bos.toByteArray());
		entryListOffset += dlen;
		
		// rewrite the list.
		writeEntryList();

		// write the number of entries and the new offset
		writeHeader();
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
	public void renameEntry(int index, String newName) throws IOException
	{
		Entry entry = (Entry)getEntry(index);
		if (entry == null)
			throw new IOException("Index is out of range.");
		
		entry.name = DoomUtil.coerceToEntrySize(newName);
		
		seek(entryListOffset + (index * DoomWadEntry.WADENTRY_LEN) + 8);
		write(DoomUtil.coerceToEntry(newName).getBytes("ASCII"));
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
	public void deleteEntry(int n) throws IOException
	{
		// get removed entry.
		DoomWadEntry wfe = removeEntry(n);
		if (wfe == null)
			throw new IOException("Index is out of range.");

		// allocate space for data shift.
		int entryEnd = wfe.getOffset()+wfe.getSize();
		byte[] leftoverContent = new byte[entryListOffset-entryEnd];
		
		// store shifted content.
		seek(entryEnd);
		read(leftoverContent);
		
		// adjust offsets from last entry.
		for (int i = n; i < entries.size(); i++)
		{
			Entry e = (WadFile.Entry)entries.getByIndex(i);
			e.offset -= wfe.getSize();
		}
		
		// start shift.
		seek(wfe.getOffset());
		write(leftoverContent);
		entryListOffset -= wfe.getSize();
		writeEntryList();
		writeHeader();
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
		writeEntryList();
		writeHeader(); // just in case size of list is affected.
	}
	
	@Override
	public void setEntries(DoomWadEntry[] entryList) throws IOException
	{
		entries.clear();
		for (DoomWadEntry entry : entryList)
			entries.add(entry);
		writeEntryList();
		writeHeader(); // just in case size of list is affected.
	}

	@Override
	public Iterator<DoomWadEntry> getEntryIterator()
	{
		return (Iterator<DoomWadEntry>)entries.iterator();
	}
	
	/**
	 * Returns this Wad's file name. 
	 */
	public String getFileName()
	{
		return fileName;
	}
	
	/**
	 * Returns this Wad's file path. 
	 */
	public String getFilePath()
	{
		return filePath;
	}

	/**
	 * Returns this Wad's file absolute path. 
	 */
	public String getFileAbsolutePath()
	{
		return fileAbsolutePath;
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
	 * Closes the file once it is cleaned up by gc().
	 */
	public void finalize() throws Throwable
	{
		try{
		close();
		super.finalize();
		} catch (IOException e){}
	}

	private DoomWadEntry removeEntry(int n)
	{
		DoomWadEntry wfe = entries.removeIndex(n);
		return wfe;
	}

	private void writeEntryList() throws IOException
	{
		seek(entryListOffset);
		for (DoomWadEntry wfe : entries)
			write(wfe.toBytes());
		if (getFilePointer() < length())
			setLength(getFilePointer());
	}

	private void writeHeader() throws IOException
	{
		seek(4);
		write(SuperWriter.intToBytes(entries.size(),SuperWriter.LITTLE_ENDIAN));
		write(SuperWriter.intToBytes(entryListOffset,SuperWriter.LITTLE_ENDIAN));
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
		private Entry(String n, int s, int o)
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
		private Entry(byte[] b) throws WadException
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
