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
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.blackrook.commons.Common;
import com.blackrook.commons.hash.CaseInsensitiveHashMap;


/**
 * Doom PK3 file. Contains a whole bunch of Doom resources,
 * however, they are all zipped together. This class contains
 * a bunch of methods for getting file information from the PK3,
 * but if you want to read explicit info from it, see java.util.ZipFile.
 * @author Matthew Tropiano
 */
public class DoomPK3 extends ZipFile implements Closeable
{
	/** Directory for patch textures. */
	public static final String PATCHES_DIRECTORY = "patches";
	/** Directory for graphics. */
	public static final String GRAPHICS_DIRECTORY = "graphics";
	/** Directory for sounds. */
	public static final String SOUNDS_DIRECTORY = "sounds";
	/** Directory for music. */
	public static final String MUSIC_DIRECTORY = "music";
	/** Directory for maps. */
	public static final String MAPS_DIRECTORY = "maps";
	/** Directory for flats (floor/ceiling textures). */
	public static final String FLATS_DIRECTORY = "flats";
	/** Directory for sprites. */
	public static final String SPRITES_DIRECTORY = "sprites";
	/** Directory for immediate-mode textures. */
	public static final String TEXTURES_DIRECTORY = "textures";
	/** Directory for hi-res textures. */
	public static final String HIRES_DIRECTORY = "hires";
	/** Directory for colormaps. */
	public static final String COLORMAPS_DIRECTORY = "colormaps";
	/** Directory for ACS libraries. */
	public static final String ACS_DIRECTORY = "acs";
	/** Directory for Strife voice lumps. */
	public static final String VOICES_DIRECTORY = "voices";
	/** Directory for globals. */
	public static final String GLOBALS_DIRECTORY = "";

	/** File path. */
	private String filePath;
	/** File name. */
	private String fileName;
	/** Number of entries. */
	private int numEntries;
	
	/** Hashtable of PK3 directories and lists of internal files. */
	private CaseInsensitiveHashMap<CaseInsensitiveHashMap<ZipEntry>> entryTable;
	
	/**
	 * Opens a DoomPK3 file for reading and caches its contents.
	 * @param path the path to the file to open.		
	 * @throws ZipException
	 * @throws IOException
	 * @since 2.1.1
	 */
	public DoomPK3(String path) throws ZipException, IOException
	{
		this(new File(path));
	}
	
	/**
	 * Opens a DoomPK3 file for reading and caches its contents.
	 * @param pk3File		
	 * @throws ZipException
	 * @throws IOException
	 */
	public DoomPK3(File pk3File) throws ZipException, IOException
	{
		super(pk3File);
		entryTable = new CaseInsensitiveHashMap<CaseInsensitiveHashMap<ZipEntry>>();
		filePath = pk3File.getPath();
		fileName = pk3File.getName();
		numEntries = 0;
		prepareLists();
	}
	
	/**
	 * Prepares the internal lists.
	 * Called in the constructor.
	 */
	private void prepareLists()
	{
		Enumeration<? extends ZipEntry> entries = entries();
		while (entries.hasMoreElements())
		{
			ZipEntry ze = entries.nextElement();
			String entryName = ze.getName();
			String entryDir = new String(entryName);
			int firstslash = entryDir.indexOf("/");
			entryDir = firstslash >= 0 ? entryDir.substring(0, firstslash) : "";
			entryName = entryName.substring(firstslash+1);
			
			CaseInsensitiveHashMap<ZipEntry> fileList;
			if (entryTable.containsKey(entryDir))
				fileList = entryTable.get(entryDir);
			else
			{
				fileList = new CaseInsensitiveHashMap<ZipEntry>();
				entryTable.put(entryDir, fileList);
			}
			
			if (firstslash != entryName.length() && !ze.isDirectory())
			{
				fileList.put(entryName,ze);
				numEntries++;
			}
		}
	}

	/**
	 * Returns a list of all entries in a directory key. 
	 */
	private ZipEntry[] getWithName(String key)
	{
		if (!entryTable.containsKey(key))
			return new ZipEntry[0];
		
		CaseInsensitiveHashMap<ZipEntry> entryList = entryTable.get(key);
		ZipEntry[] out = new ZipEntry[entryList.size()];
		int x = 0;
		Iterator<ZipEntry> it = entryList.valueIterator();
		while (it.hasNext())
			out[x++] = it.next();
		return out;
	}
	
	/**
	 * Returns the number of all entries in a directory key. 
	 */
	protected int getEntryCount(String key)
	{
		if (!entryTable.containsKey(key))
			return 0;
		
		return entryTable.get(key).size();
	}
	
	/**
	 * Gets the data in one entry in the PK3.
	 * @param entry the entry to extract and return as a byte array.
	 * @return a byte array of the entry's data.
	 * @since 2.1.1
	 */
	public byte[] getData(ZipEntry entry) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream in = getInputStream(entry);
		Common.relay(in, bos);
		Common.close(in);
		return bos.toByteArray();
	}

	/**
	 * Gets the data in one entry in the PK3 as an input stream.
	 * The data is extracted fully before it is returned as a stream.
	 * @param entry the entry to extract and return as a byte array.
	 * @return an InputStream of the entry's data.
	 * @since 2.1.1
	 */
	public InputStream getDataAsStream(ZipEntry entry) throws IOException
	{
		return new ByteArrayInputStream(getData(entry));
	}
	
	/**
	 * Gets the data in one entry in the PK3 by entry name (path and all).
	 * @param entry the entry to extract and return as a byte array.
	 * @return a byte array of the entry's data.
	 * @since 2.1.1
	 */
	public byte[] getData(String entry) throws IOException
	{
		return getData(getEntry(entry));
	}

	/**
	 * Gets the data in one entry in the PK3 as an input stream by entry name (path and all).
	 * The data is extracted fully before it is returned as a stream.
	 * @param entry the entry to extract and return as a byte array.
	 * @return an InputStream of the entry's data.
	 * @since 2.1.1
	 */
	public InputStream getDataAsStream(String entry) throws IOException
	{
		return new ByteArrayInputStream(getData(entry));
	}
	
	/**
	 * Returns the "entry name" for a ZipEntry,
	 * which is just the filename itself minus extension.
	 */
	public static String getEntryName(ZipEntry ze)
	{
		String name = ze.getName();
		int pindex = name.lastIndexOf("/")+1;
		int eindex = name.lastIndexOf(".");
		if (eindex > -1)
			return name.substring(pindex, eindex);
		else
			return name.substring(pindex);
	}
	
	
	/**
	 * Retrieves all global-level data entries.
	 * @return	an array of all entries.
	 */
	public ZipEntry[] getGlobals()
	{
		return getWithName(GLOBALS_DIRECTORY);
	}
	
	/**
	 * Retrieves all patch data entries.
	 * @return	an array of all entries.
	 */
	public ZipEntry[] getPatches()
	{
		return getWithName(PATCHES_DIRECTORY);
	}
	
	/**
	 * Retrieves all graphical data entries.
	 * @return	an array of all entries.
	 */
	public ZipEntry[] getGraphics()
	{
		return getWithName(GRAPHICS_DIRECTORY);
	}
	
	/**
	 * Retrieves all sound data entries.
	 * @return	an array of all entries.
	 */
	public ZipEntry[] getSounds()
	{
		return getWithName(SOUNDS_DIRECTORY);
	}

	/**
	 * Retrieves all musical data entries.
	 * @return	an array of all entries.
	 */
	public ZipEntry[] getMusic()
	{
		return getWithName(MUSIC_DIRECTORY);
	}
	
	/**
	 * Retrieves all map data entries.
	 * @return	an array of all entries.
	 */
	public ZipEntry[] getMaps()
	{
		return getWithName(MAPS_DIRECTORY);
	}
	
	/**
	 * Retrieves all flat texture data entries.
	 * @return	an array of all entries.
	 */
	public ZipEntry[] getFlats()
	{
		return getWithName(FLATS_DIRECTORY);
	}
	
	/**
	 * Retrieves all sprite data entries.
	 * @return	an array of all entries.
	 */
	public ZipEntry[] getSprites()
	{
		return getWithName(SPRITES_DIRECTORY);
	}
	
	/**
	 * Retrieves all immediate-mode texture data entries.
	 * @return	an array of all entries.
	 */
	public ZipEntry[] getTextures()
	{
		return getWithName(TEXTURES_DIRECTORY);
	}
	
	/**
	 * Retrieves all hi-res textures data entries.
	 * @return	an array of all entries.
	 */
	public ZipEntry[] getHiResolutionTextures()
	{
		return getWithName(HIRES_DIRECTORY);
	}
	
	/**
	 * Retrieves all colormap data entries.
	 * @return	an array of all entries.
	 */
	public ZipEntry[] getColormaps()
	{
		return getWithName(COLORMAPS_DIRECTORY);
	}
	
	/**
	 * Retrieves all ACS library data entries.
	 * @return	an array of all entries.
	 */
	public ZipEntry[] getACSLibraries()
	{
		return getWithName(ACS_DIRECTORY);
	}
	
	/**
	 * Retrieves all voice data entries.
	 * @return	an array of all entries.
	 */
	public ZipEntry[] getVoices()
	{
		return getWithName(VOICES_DIRECTORY);
	}

	/**
	 * Retrieves the number of global-level data entries.
	 */
	public int getGlobalCount()
	{
		return getEntryCount(GLOBALS_DIRECTORY);
	}

	/**
	 * Retrieves the number of patch data entries.
	 */
	public int getPatchCount()
	{
		return getEntryCount(PATCHES_DIRECTORY);
	}

	/**
	 * Retrieves the number of graphical data entries.
	 */
	public int getGraphicCount()
	{
		return getEntryCount(GRAPHICS_DIRECTORY);
	}

	/**
	 * Retrieves the number of sound data entries.
	 */
	public int getSoundCount()
	{
		return getEntryCount(SOUNDS_DIRECTORY);
	}

	/**
	 * Retrieves the number of musical data entries.
	 */
	public int getMusicCount()
	{
		return getEntryCount(MUSIC_DIRECTORY);
	}

	/**
	 * Retrieves the number of map data entries.
	 */
	public int getMapCount()
	{
		return getEntryCount(MAPS_DIRECTORY);
	}

	/**
	 * Retrieves the number of flat texture data entries.
	 */
	public int getFlatCount()
	{
		return getEntryCount(FLATS_DIRECTORY);
	}

	/**
	 * Retrieves the number of sprite data entries.
	 */
	public int getSpriteCount()
	{
		return getEntryCount(SPRITES_DIRECTORY);
	}

	/**
	 * Retrieves the number of immediate-mode texture data entries.
	 */
	public int getTextureCount()
	{
		return getEntryCount(TEXTURES_DIRECTORY);
	}

	/**
	 * Retrieves the number of hi-res textures data entries.
	 */
	public int getHiResolutionTextureCount()
	{
		return getEntryCount(HIRES_DIRECTORY);
	}

	/**
	 * Retrieves the number of colormap data entries.
	 */
	public int getColormapCount()
	{
		return getEntryCount(COLORMAPS_DIRECTORY);
	}

	/**
	 * Retrieves the number of ACS library data entries.
	 */
	public int getACSLibraryCount()
	{
		return getEntryCount(ACS_DIRECTORY);
	}

	/**
	 * Retrieves the number of voice data entries.
	 */
	public int getVoiceCount()
	{
		return getEntryCount(VOICES_DIRECTORY);
	}

	/**
	 * Gets this file's path.
	 */
	public final String getFilePath()
	{
		return filePath;
	}

	/**
	 * Gets this file's name.
	 */
	public final String getFileName()
	{
		return fileName;
	}

	/**
	 * Gets the number of entries in the PK3.
	 */
	public final int getEntryCount()
	{
		return numEntries;
	}

}
