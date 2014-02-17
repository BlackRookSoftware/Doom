/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Interface for all WAD file type implementations for 
 * reading and writing to WAD structures, be it in memory or on disk.
 * There may be some implementations of this structure that do not support certain operations,
 * so in those cases, those methods may throw an {@link UnsupportedOperationException}.
 * Also, certain implementations may be more suited for better tasks, so be sure to figure out
 * which implementation suits your needs!
 * @author Matthew Tropiano
 * @since 2.1.0
 */
public interface DoomWad
{
	/**
	 * Wad type enumeration.
	 * @author Matthew Tropiano
	 */
	public static enum Type
	{
		PWAD,
		IWAD
	}

	/**
	 * Returns the DoomWadEntry at index n.
	 * @param n		the index of the entry in the entry list.
	 * @throws ArrayIndexOutOfBoundsException if n < 0 or > size.
	 * @return		the entry at n.
	 */
	public DoomWadEntry getEntry(int n);

	/**
	 * Returns the first DoomWadEntry named s.
	 * @param s		the name of the entry (will be corrected automatically).
	 * @return		the first entry named s or null if not found.
	 */
	public DoomWadEntry getEntry(String s);

	/**
	 * Returns the first DoomWadEntry named s, starting from a particular index.
	 * @param s		the name of the entry (will be corrected automatically).
	 * @param startIndex the starting index to search from.
	 * @return		the first entry named s or null if not found.
	 * @since 2.6.0
	 */
	public DoomWadEntry getEntry(String s, int startIndex);

	/**
	 * Returns the last DoomWadEntry named s.
	 * @param s		the name of the entry (will be corrected automatically).
	 * @return		the last entry named s or null if not found.
	 */
	public DoomWadEntry getLastEntry(String s);

	/**
	 * Returns the n-th DoomWadEntry named s.
	 * @param s		the name of the entry (will be corrected automatically).
	 * @param n		the n-th occurrence to find.
	 * @return		the n-th entry named s or null if not found.
	 */
	public DoomWadEntry getNthEntry(String s, int n);

	/**
	 * Returns all DoomWadEntries.
	 * @return		an array of all of the DoomWadEntries.
	 */
	public DoomWadEntry[] getAllEntries();

	/**
	 * Returns all DoomWadEntries named s.
	 * @param s		the name of the entry (will be corrected automatically).
	 * @return		an array of all of the DoomWadEntries with the name s.
	 */
	public DoomWadEntry[] getAllEntries(String s);

	/**
	 * Returns the first index of an entry of name "entryname."
	 * @param entryname		the name of the entry to find.
	 * @return				the index of the entry in this file, or -1 if not found.
	 * @throws NullPointerException if "entryname" is null.
	 */
	public int getIndexOf(String entryname);

	/**
	 * Returns the first index of an entry of name "entryname" from a starting point.
	 * @param entryname		the name of the entry to find.
	 * @param start			the index with which to start the search.
	 * @throws ArrayIndexOutOfBoundsException if start < 0 or > size.
	 * @return				the index of the entry in this file, or -1 if not found.
	 * @throws NullPointerException if "entryname" is null.
	 */
	public int getIndexOf(String entryname, int start);

	/**
	 * Returns the last index of an entry of name "entryname."
	 * @param entryname		the name of the entry to find.
	 * @return				the index of the entry in this file, or -1 if not found.
	 * @throws NullPointerException if "entryname" is null.
	 */
	public int getLastIndexOf(String entryname);

	/**
	 * Retrieves the data of a particular entry index.
	 * @param n		the index of the entry in the file.
	 * @throws IOException if the data couldn't be retrieved.
	 * @throws ArrayIndexOutOfBoundsException if n < 0 or > size.
	 * @return		a byte array of the data.
	 */
	public byte[] getData(int n) throws IOException;

	/**
	 * Retrieves the data of the first occurrence of a particular entry.
	 * @param entry		the name of the entry to find.
	 * @return			a byte array of the data, or null if the entry doesn't exist.
	 * @throws IOException if the data couldn't be retrieved.		
	 * @throws NullPointerException if "entry" is null.
	 */
	public byte[] getData(String entry) throws IOException;

	/**
	 * Retrieves the data of the first occurrence of a particular entry from a starting index.
	 * @param entry		the name of the entry to find.
	 * @param start		the index with which to start the search.
	 * @return			a byte array of the data, or null if the entry doesn't exist.
	 * @throws IOException if the data couldn't be retrieved.		
	 * @throws NullPointerException if "entry" is null.
	 * @throws ArrayIndexOutOfBoundsException if start < 0 or > size.
	 */
	public byte[] getData(String entry, int start) throws IOException;

	/**
	 * Retrieves the data of the specified entry.
	 * @param entry	the entry to use.
	 * @return a byte array of the data.
	 * @throws IOException if the data couldn't be retrieved 
	 * or the entry's offsets breach the file extents.		
	 * @throws NullPointerException if "entry" is null.
	 */
	public byte[] getData(DoomWadEntry entry) throws IOException;

	/**
	 * Retrieves the data of a particular entry index and returns it as a stream.
	 * @param n	the index of the entry in the file.
	 * @return	a ByteArrayInputStream of the data, or null if it can't be retrieved.
	 * @throws IOException if the data couldn't be retrieved.
	 * @throws ArrayIndexOutOfBoundsException if n < 0 or > size.
	 */
	public InputStream getDataAsStream(int n) throws IOException;

	/**
	 * Retrieves the data of the first occurance of a particular entry and returns it as a stream.
	 * @param entry	the name of the entry to find.
	 * @return		a ByteArrayInputStream of the data, or null if it can't be retrieved.
	 * @throws IOException if the data couldn't be retrieved.		
	 * @throws NullPointerException if "entry" is null.
	 */
	public InputStream getDataAsStream(String entry) throws IOException;

	/**
	 * Retrieves the data of the first occurance of a particular entry from a starting index and returns it as a stream.
	 * @param entry	the name of the entry to find.
	 * @param start	the index with which to start the search.
	 * @return a ByteArrayInputStream of the data, or null if it can't be retrieved.
	 * @throws IOException if the data couldn't be retrieved.		
	 * @throws NullPointerException if "entry" is null.
	 * @throws ArrayIndexOutOfBoundsException if start < 0 or > size.
	 */
	public InputStream getDataAsStream(String entry, int start) throws IOException;

	/**
	 * Retrieves the data of the specified entry from a starting index and returns it as a stream.
	 * @param entry	the entry to use.
	 * @return a ByteArrayInputStream of the data.
	 * @throws IOException if the data couldn't be retrieved 
	 * or the entry's offsets breach the file extents.		
	 * @throws NullPointerException if "entry" is null.
	 */
	public InputStream getDataAsStream(DoomWadEntry entry) throws IOException;

	/**
	 * Returns the number of entries in this Wad file.
	 */
	public int getSize();

	/**
	 * Returns true if this WadFile contains a particular entry, false otherwise.
	 * @param entry the name of the entry (automatically corrected).
	 */
	public boolean contains(String entry);

	/**
	 * Returns true if this WadFile contains a particular entry 
	 * from a starting entry index, false otherwise.
	 * @param entry the name of the entry (automatically corrected).
	 */
	public boolean contains(String entry, int index);

	/**
	 * Adds data to this WadFile, using entryname as the name of the entry.
	 * The overhead for multiple additions may be expensive I/O-wise
	 * depending on the DoomWad implementation.
	 * @param entryname		the name of the entry to add this as (corrected automatically).
	 * @param data			the bytes of data to add as this wad's data.
	 * @throws IOException	if the data cannot be written.
	 * @throws NullPointerException if "entryname" or "data" is null.
	 */
	public void add(String entryname, byte[] data) throws IOException;

	/**
	 * Adds data to this WadFile at a particular entry offset, 
	 * using entryname as the name of the entry. The rest of the entries
	 * in the wad are shifted down one index.
	 * The overhead for multiple additions may be expensive I/O-wise
	 * depending on the DoomWad implementation.
	 * @param index			the index at which to add the entry.
	 * @param entryname		the name of the entry to add this as (corrected automatically).
	 * @param data			the bytes of data to add as this wad's data.
	 * @throws IOException	if the data cannot be written.
	 * @throws NullPointerException if "entryname" or "data" is null.
	 * @since 2.6.0
	 */
	public void addAt(int index, String entryname, byte[] data) throws IOException;

	/**
	 * Adds multiple entries of data to this WadFile, using entrynames as the name 
	 * of the entry, using the same indices in the data array as the corresponding
	 * data.
	 * @param entrynames	the names of the entries to add (corrected automatically).
	 * @param data			the bytes of data to add as each entry's data.
	 * @throws IOException	if the data cannot be written.
	 * @throws ArrayIndexOutOfBoundsException 
	 * 		if the lengths of entrynames and data do not match.
	 * @throws NullPointerException if an object in "entrynames" or "data" is null.
	 * @since 2.6.3
	 */
	public void addAll(String[] entrynames, byte[][] data) throws IOException;

	/**
	 * Adds an entry marker to the WadFile (entry with 0 size, 0 offset).
	 * @param name 			Name of the entry.
	 * @throws IOException	if the entry cannot be written.
	 * @throws NullPointerException 	if "name" is null.
	 */
	public void addMarker(String name) throws IOException;

	/**
	 * Creates a new entry object that represents an empty marker.
	 * This does not add anything to the WAD - this is a factory method.
	 * @param name the name of the entry.
	 * @return a new DoomWadEntry representing an empty marker. 
	 * @since 2.9.1
	 */
	public DoomWadEntry createMarker(String name);
	
	/**
	 * Replaces the entry at an index in the WAD.
	 * @param index			the index of the entry to replace.
	 * @param data			the data to replace the entry with.
	 * @throws IOException	if the entry cannot be written.
	 * @throws NullPointerException if "data" is null.
	 * @since 2.6.2
	 */
	public void replaceEntry(int index, byte[] data) throws IOException;
	
	/**
	 * Renames the entry at an index in the WAD.
	 * @param index the index of the entry to rename.
	 * @param newName the new name of the entry.
	 * @throws IOException	if the entry cannot be renamed.
	 * @throws NullPointerException if "data" is null.
	 * @since 2.6.2
	 */
	public void renameEntry(int index, String newName) throws IOException;
	
	/**
	 * Deletes a Wad's entry and its contents.
	 * The overhead for multiple deletions may be expensive I/O-wise.
	 * @param n		the index of the entry to delete.
	 * @throws ArrayIndexOutOfBoundsException if n < 0 or > size.
	 * @throws IOException if the file cannot be altered in such a manner.
	 */
	public void deleteEntry(int n) throws IOException;
	
	/**
	 * Retrieves a contiguous set of entries from this Wad,
	 * starting from a desired index. If the amount of entries desired 
	 * goes outside the Wad's potential set of entries, this will retrieve
	 * up to those entries (for example, <code>mapEntries(5, 7)</code> in an 8-entry Wad
	 * will only return 3 entries).
	 * @param startIndex the starting index to map from (inclusive).
	 * @param maxLength the amount of entries to retrieve from the index position.
	 * @return an array of references to {@link DoomWadEntry} objects.
	 * @throws IllegalArgumentException if startIndex is less than 0.
	 * @since 2.9.0
	 */
	public DoomWadEntry[] mapEntries(int startIndex, int maxLength);
	
	/**
	 * Replaces a series of DoomWadEntries in this Wad, using the provided
	 * list of entries as the replacement list. If the list of entries plus the
	 * starting index would breach the original list of entries, the excess is
	 * appended to the Wad.  
	 * @param startIndex the starting index to replace from (inclusive).
	 * @param entryList the set of entries to replace (in order) from the starting index.  
	 * @throws IOException if the entries ould not be written.
	 * @throws IllegalArgumentException if startIndex is less than 0.
	 * @since 2.9.0
	 */
	public void unmapEntries(int startIndex, DoomWadEntry[] entryList) throws IOException;
	
	/**
	 * Completely replaces the list of entries in this Wad with a completely
	 * different set of entries.
	 * @param entryList the set of entries that will make up this Wad.  
	 * @throws IOException if the entries ould not be written.
	 * @throws IllegalArgumentException if startIndex is less than 0.
	 * @since 2.9.0
	 */
	public void setEntries(DoomWadEntry[] entryList) throws IOException;

	/**
	 * Returns a Wad file iterator for the entries in this file.
	 */
	public Iterator<DoomWadEntry> getEntryIterator();

	/**
	 * Is this wad an information WAD?
	 */
	public boolean isIWAD();

	/**
	 * Is this wad a patch WAD?
	 */
	public boolean isPWAD();

}
