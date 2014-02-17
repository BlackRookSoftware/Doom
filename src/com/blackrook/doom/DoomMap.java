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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import com.blackrook.commons.Common;
import com.blackrook.commons.list.List;
import com.blackrook.doom.enums.GameType;
import com.blackrook.doom.struct.BSPNode;
import com.blackrook.doom.struct.BSPSegment;
import com.blackrook.doom.struct.BSPSubsector;
import com.blackrook.doom.struct.Blockmap;
import com.blackrook.doom.struct.Linedef;
import com.blackrook.doom.struct.RawData;
import com.blackrook.doom.struct.Reject;
import com.blackrook.doom.struct.Sector;
import com.blackrook.doom.struct.Sidedef;
import com.blackrook.doom.struct.TextData;
import com.blackrook.doom.struct.Thing;
import com.blackrook.doom.struct.Vertex;
import com.blackrook.doom.udmf.UDMFParseException;
import com.blackrook.doom.udmf.UDMFReader;
import com.blackrook.doom.udmf.UDMFStruct;
import com.blackrook.doom.udmf.UDMFTable;
import com.blackrook.doom.udmf.UDMFUtil;
import com.blackrook.doom.udmf.UDMFWriter;
import com.blackrook.doom.udmf.namespace.UDMFNamespace;
import com.blackrook.doom.util.DoomUtil;

/**
 * This holds de-marshalled Doom Map Information. This is a heavyweight object, 
 * so it is not advisable to use this object to read map information unless you 
 * want to read map data in full.
 * <p>
 * Maps in Doom are held in multiple lump entries in a WAD file.
 * <p>
 * The first lump in a map is a marker entry, usually size 0, with a name like "MAPxx"
 * or "ExMy", where x and y are numbers from 0-9.
 * <p>
 * From this entry, the lumps THINGS, SECTORS, LINEDEFS, VERTEXES, and SIDEDEFS are 
 * found and loaded. 
 * <p>
 * If the TEXTMAP lump is found, the map is loaded as a UDMF formatted map.
 * <p>
 * If the BEHAVIOR lump is found, the map is loaded as a ZDoom/Hexen formatted map.
 * <p>
 * Optionally, SEGS, SSECTORS, NODES, REJECT, and BLOCKMAP are loaded
 * if they are found.
 * 
 * @author Matthew Tropiano
 */
public class DoomMap
{
	/**
	 * Enumeration of format types for Doom maps.
	 */
	public static enum Format
	{
		DOOM,
		HEXEN,
		STRIFE,
		UDMF
	}
	
	/** This map's vertices. */
	private List<Thing> things;
	/** This map's linedefs. */
	private List<Linedef> linedefs;
	/** This map's sidedefs. */
	private List<Sidedef> sidedefs;
	/** This map's vertices. */
	private List<Vertex> vertices;
	/** This map's sidedefs. */
	private List<Sector> sectors;

	/** This map's BSP nodes. */
	private List<BSPNode> nodes;
	/** This map's BSP segments. */
	private List<BSPSegment> segs;
	/** This map's BSP subsectors. */
	private List<BSPSubsector> subsectors;
	
	/** This map's reject lump. */
	private Reject reject;
	/** This map's blockmap lump. */
	private Blockmap blockmap;
	
	/** ZDoom Node chunk. */
	private RawData zNodes;
	/** Behavior chunk. */
	private RawData behavior;
	/** Script content. */
	private TextData scripts;
	
	/** The originating format. */
	private Format originalFormat;
	/** The originating UDMF Namespace, if any. */
	private String originalUDMFNamespace;

	/**
	 * Constructs a DoomMap from an existing map in a DoomWad.
	 * Please note that this constructor will try to figure out what format the data is in.
	 * @param wf					the DoomWad to use.
	 * @param gameType				the known gametype.
	 * @param headerName			the name of the map to read.
	 * @throws MapException 		if important data couldn't be retreived from the map.
	 * @throws WadException 		if headername is not a valid entry in wf.
	 * @throws IOException			if the wad file can't be read.
	 * @throws NullPointerException	if wf or headername is null.
	 * @since 2.8.3
	 */
	public DoomMap(DoomWad wf, GameType gameType, String headerName) throws IOException
	{
		blockmap = new Blockmap();
		behavior = new RawData();

		Format type = detectFormat(wf, headerName, gameType);
		if (type == null)
			throw new WadException("No such entry \""+headerName+"\" in the Wad.");
		
		int headerIndex = wf.getIndexOf(headerName);

		switch (type)
		{
			case DOOM:
				makeDoomMap(wf, headerIndex);
				break;
			case HEXEN:
				makeHexenMap(wf, headerIndex);
				break;
			case STRIFE:
				makeStrifeMap(wf, headerIndex);
				break;
			case UDMF:
				makeUDMFMap(wf, headerIndex);
				break;
		}
		
	}
	
	/**
	 * Constructs a DoomMap from an existing map in a DoomWad.
	 * Please note that this constructor will try to figure out what format the data is in.
	 * @param wf					the DoomWad to use.
	 * @param headerName			the name of the map to read.
	 * @throws MapException 		if important data couldn't be retreived from the map.
	 * @throws WadException 		if headername is not a valid entry in wf.
	 * @throws IOException			if the wad file can't be read.
	 * @throws NullPointerException	if wf or headername is null.
	 */
	public DoomMap(DoomWad wf, String headerName) throws IOException
	{
		blockmap = new Blockmap();
		behavior = new RawData();

		Format type = detectFormat(wf, headerName);
		if (type == null)
			throw new WadException("No such entry \""+headerName+"\" in the Wad.");
		
		int headerIndex = wf.getIndexOf(headerName);

		switch (type)
		{
			case DOOM:
				makeDoomMap(wf, headerIndex);
				break;
			case HEXEN:
				makeHexenMap(wf, headerIndex);
				break;
			case STRIFE:
				makeStrifeMap(wf, headerIndex);
				break;
			case UDMF:
				makeUDMFMap(wf, headerIndex);
				break;
		}
		
	}

	private void makeDoomMap(DoomWad wf, int headerIndex) throws IOException
	{
		byte[] lumpData;
		
		originalFormat = Format.DOOM;
		
		lumpData = wf.getData("things", headerIndex);
		if (lumpData == null)
			throw new MapException("Thing data couldn't be loaded.");
		things = readDoomThingLump(lumpData);
		
		lumpData = wf.getData("linedefs", headerIndex);
		if (lumpData == null)
			throw new MapException("Linedef data couldn't be loaded.");
		linedefs = readDoomLinedefLump(lumpData);
		
		finishMap(wf, headerIndex);
	}

	private void makeHexenMap(DoomWad wf, int headerIndex) throws IOException
	{
		byte[] lumpData;
		
		originalFormat = Format.HEXEN;
		
		lumpData = wf.getData("things", headerIndex);
		if (lumpData == null)
			throw new MapException("Thing data couldn't be loaded.");
		things = readHexenThingLump(lumpData);
		
		lumpData = wf.getData("linedefs", headerIndex);
		if (lumpData == null)
			throw new MapException("Linedef data couldn't be loaded.");
		linedefs = readHexenLinedefLump(lumpData);
		
		finishMap(wf, headerIndex);
		finishMisc(wf, headerIndex);
	}

	private void makeStrifeMap(DoomWad wf, int headerIndex) throws IOException
	{
		byte[] lumpData;
		
		originalFormat = Format.STRIFE;
		
		lumpData = wf.getData("things", headerIndex);
		if (lumpData == null)
			throw new MapException("Thing data couldn't be loaded.");
		things = readStrifeThingLump(lumpData);
		
		lumpData = wf.getData("linedefs", headerIndex);
		if (lumpData == null)
			throw new MapException("Linedef data couldn't be loaded.");
		linedefs = readStrifeLinedefLump(lumpData);
		
		finishMap(wf, headerIndex);
	}

	private void makeUDMFMap(DoomWad wf, int headerIndex) throws IOException
	{
		originalFormat = Format.UDMF;
		
		int endIndex = wf.getIndexOf("endmap", headerIndex);
		if (endIndex == -1)
			throw new MapException("No such entry \"ENDMAP\" in the WAD: TEXTMAP without ENDMAP.");
	
		int textIndex = wf.getIndexOf("textmap", headerIndex);
	
		if (textIndex > endIndex)
			throw new MapException("Malformed WAD: TEXTMAP after ENDMAP.");

		
		UDMFTable udmfTable = readUDMFTable(wf.getData(textIndex));
		UDMFNamespace udmfNamespace = readUDMFNamespace(udmfTable);

		originalUDMFNamespace = udmfNamespace.getName();
	
		linedefs = readUDMFLinedefs(udmfNamespace, udmfTable);
		sidedefs = readUDMFSidedefs(udmfNamespace, udmfTable);
		things = readUDMFThings(udmfNamespace, udmfTable);
		vertices = readUDMFVertices(udmfNamespace, udmfTable);
		sectors = readUDMFSectors(udmfNamespace, udmfTable);
		
		finishNodes(wf, headerIndex);
		finishMisc(wf, headerIndex);
	}

	private void finishMap(DoomWad wf, int headerIndex) throws IOException
	{
		byte[] lumpData;

		lumpData = wf.getData("sidedefs", headerIndex);
		if (lumpData == null)
			throw new MapException("Sidedefs data couldn't be loaded.");
		sidedefs = readSidedefLump(lumpData);
		
		lumpData = wf.getData("vertexes", headerIndex);
		if (lumpData == null)
			throw new MapException("Vertex data couldn't be loaded.");
		vertices = readVertexLump(lumpData);
		
		lumpData = wf.getData("sectors", headerIndex);
		if (lumpData == null)
			throw new MapException("Sector data couldn't be loaded.");
		sectors = readSectorLump(lumpData);

		finishNodes(wf, headerIndex);
	}
	
	private void finishNodes(DoomWad wf, int headerIndex) throws IOException
	{
		byte[] lumpData;

		lumpData = wf.getData("znodes", headerIndex);
		if (lumpData != null)
			zNodes = new RawData(lumpData);

		lumpData = wf.getData("nodes", headerIndex);
		if (lumpData != null)
			nodes = readBSPNodeLump(lumpData);
	
		lumpData = wf.getData("segs", headerIndex);
		if (lumpData != null)
			segs = readBSPSegmentLump(lumpData);
	
		lumpData = wf.getData("ssectors", headerIndex);
		if (lumpData != null)
			subsectors = readBSPSubsectorLump(lumpData);
	
		lumpData = wf.getData("reject", headerIndex);
		if (lumpData != null)
		{
			if (lumpData.length == 0)
				reject = null;
			else
			{
				ByteArrayInputStream bis = new ByteArrayInputStream(lumpData);
				reject = new Reject(sectors.size());
				reject.readDoomBytes(bis);
			}
		}
	
		lumpData = wf.getData("blockmap", headerIndex);
		if (lumpData != null)
		{
			if (lumpData.length == 0)
				blockmap = null;
			else
			{
				ByteArrayInputStream bis = new ByteArrayInputStream(lumpData);
				blockmap = new Blockmap();
				blockmap.readDoomBytes(bis);
			}
		}
	}

	private void finishMisc(DoomWad wf, int headerIndex) throws IOException
	{
		byte[] lumpData = wf.getData("behavior", headerIndex);
		if (lumpData != null)
			behavior = new RawData(lumpData);
		lumpData = wf.getData("scripts", headerIndex);
		if (lumpData != null)
			scripts = new TextData(Common.getTextualContents(new ByteArrayInputStream(lumpData), "ASCII"));
	}
	
	/**
	 * Returns the map's originating format type.
	 */
	public Format getOriginalFormat()
	{
		return originalFormat;
	}

	/** 
	 * The originating UDMF Namespace, if any (null if not from UDMF).
	 */
	public String getOriginalUDMFNamespace()
	{
		return originalUDMFNamespace;
	}

	/**
	 * Detects the map type for a map in a Wad file.
	 * @param wad the wad to check.
	 * @param headerName the map header name.
	 * @return a {@link Format}, or null if the map does not exist.
	 * @since 2.6.0
	 */
	public static Format detectFormat(DoomWad wad, String headerName)
	{
		return detectFormat(wad, headerName, null);
	}
	
	/**
	 * Detects the map type for a map in a Wad file.
	 * @param wad the wad to check.
	 * @param gameType the known game type. If null, it will make an attempt to find it.
	 * @param headerName the map header name.
	 * @return a {@link Format}, or null if the map does not exist.
	 * @since 2.6.0
	 */
	public static Format detectFormat(DoomWad wad, String headerName, GameType gameType)
	{
		int headerIndex = wad.getIndexOf(headerName);
		if (headerIndex == -1)
			return null;
		
		if (wad.contains("textmap",headerIndex))
			return Format.UDMF;
		else if (wad.contains("behavior",headerIndex))
			return Format.HEXEN;
		else
		{
			GameType gt = gameType != null ? gameType : DoomUtil.intuitGameType(wad);
			if (gt == GameType.STRIFE)
				return Format.STRIFE;
			else
				return Format.DOOM;
		}
	}
	
	/**
	 * Returns all of the indices of every map in the wad.
	 * This algorithm checks for "THINGS" or "TEXTMAP" lumps - the first 
	 * entry in a map. If it finds one, the previous entry is the header.
	 * @param wf the DoomWad to search in.
	 * @return an array of all of the entry indices of maps. 
	 */
	public static int[] getAllMapIndices(DoomWad wf)
	{
		List<Integer> indices = new List<Integer>(32);
		int z = 0;
		DoomWadEntry e = null;
		Iterator<DoomWadEntry> it = wf.getEntryIterator();
		while (it.hasNext())
		{
			e = it.next();
			if ((e.getName().equalsIgnoreCase("things") || e.getName().equalsIgnoreCase("textmap")) && z > 0)
				indices.add(z-1);
			z++;
		}
		
		int[] out = new int[indices.size()];
		int x = 0;
		for (Integer i : indices)
			out[x++] = i;
		return out;
	}
	
	/**
	 * Returns all of the entry names of every map in the wad.
	 * This algorithm checks for "THINGS" or "TEXTMAP" lumps - the typical 
	 * first entry in a map. If it finds one, the previous entry is the header.
	 * @param wf the DoomWad to search in.
	 * @return an array of all of the entry indices of maps. 
	 */
	public static String[] getAllMapEntries(DoomWad wf)
	{
		int[] entryIndices = getAllMapIndices(wf);
		String[] out = new String[entryIndices.length];
		int i = 0;
		for (int index : entryIndices)
			out[i++] = wf.getEntry(index).getName();
		return out;
	}

	/**
	 * Returns the amount of entries in a map, including the header.
	 * @param wad the WAD to inspect.
	 * @param headerName the map header name.
	 * @return the length, in entries, of the contiguous map data.
	 * @since 2.9.0
	 */
	public static int getMapContentIndices(DoomWad wad, String headerName)
	{
		int start = wad.getIndexOf(headerName);
		int end = 0;
		
		if (start + 1 == wad.getSize())
			return 1;
		else if (wad.getEntry(start + 1).getName().equalsIgnoreCase("textmap"))
		{
			end = start + 1;
			while (end < wad.getSize() && !wad.getEntry(end).getName().equalsIgnoreCase("endmap"))
				end++;
			end++;
		}
		else
		{
			end = start + 1;
			while (end < wad.getSize() && DoomUtil.isMapDataLump(wad.getEntry(end).getName()))
				end++;
		}
		
		return end - start;
	}
	
	/**
	 * Reads in a Doom Linedef lump and returns it as a list of Doom Linedefs.
	 * @param in	the input stream to use.
	 * @throws IOException if a read error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static List<Linedef> readDoomLinedefLump(InputStream in) throws IOException
	{
		return (List<Linedef>)readDoomObjects(Linedef.class, in, in.available()/Linedef.getDoomLength());
	}
	
	/**
	 * Reads in a Doom Linedef lump and returns it as a list of Doom Linedefs.
	 * @param b		the bytes that make up the lump.
	 * @throws IOException if a read error occurs.
	 */
	public static List<Linedef> readDoomLinedefLump(byte[] b) throws IOException
	{
		return readDoomLinedefLump(new ByteArrayInputStream(b));
	}
	
	/**
	 * Reads in a Doom Thing lump and returns it as a list of Doom Things.
	 * @param in	the input stream to use.
	 * @throws IOException if a read error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static List<Thing> readDoomThingLump(InputStream in) throws IOException
	{
		return (List<Thing>)readDoomObjects(Thing.class, in, in.available()/Thing.getDoomLength());
	}
	
	/**
	 * Reads in a Doom Thing lump and returns it as a list of Doom Things.
	 * @param b		the bytes that make up the lump.
	 * @throws IOException if a read error occurs.
	 */
	public static List<Thing> readDoomThingLump(byte[] b) throws IOException
	{
		return readDoomThingLump(new ByteArrayInputStream(b));
	}
	
	/**
	 * Reads in a Hexen Linedef lump and returns it as a list of Hexen Linedefs.
	 * @param in	the input stream to use.
	 * @throws IOException if a read error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static List<Linedef> readHexenLinedefLump(InputStream in) throws IOException
	{
		return (List<Linedef>)readHexenObjects(Linedef.class, in, in.available()/Linedef.getHexenLength());
	}
	
	/**
	 * Reads in a Hexen Linedef lump and returns it as a list of Hexen Linedefs.
	 * @param b		the bytes that make up the lump.
	 * @throws IOException if a read error occurs.
	 */
	public static List<Linedef> readHexenLinedefLump(byte[] b) throws IOException
	{
		return readHexenLinedefLump(new ByteArrayInputStream(b));
	}
	
	/**
	 * Reads in a Hexen Thing lump and returns it as a list of Hexen Things.
	 * @param in	the input stream to use.
	 * @throws IOException if a read error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static List<Thing> readHexenThingLump(InputStream in) throws IOException
	{
		return (List<Thing>)readHexenObjects(Thing.class, in, in.available()/Thing.getHexenLength());
	}
	
	/**
	 * Reads in a Hexen Thing lump and returns it as a list of Hexen Things.
	 * @param b		the bytes that make up the lump.
	 * @throws IOException if a read error occurs.
	 */
	public static List<Thing> readHexenThingLump(byte[] b) throws IOException
	{
		return readHexenThingLump(new ByteArrayInputStream(b));
	}
	
	/**
	 * Reads in a Strife Linedef lump and returns it as a list of Strife Linedefs.
	 * @param in	the input stream to use.
	 * @throws IOException if a read error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static List<Linedef> readStrifeLinedefLump(InputStream in) throws IOException
	{
		return (List<Linedef>)readStrifeObjects(Linedef.class, in, in.available()/Linedef.getStrifeLength());
	}
	
	/**
	 * Reads in a Strife Linedef lump and returns it as a list of Strife Linedefs.
	 * @param b		the bytes that make up the lump.
	 * @throws IOException if a read error occurs.
	 */
	public static List<Linedef> readStrifeLinedefLump(byte[] b) throws IOException
	{
		return readStrifeLinedefLump(new ByteArrayInputStream(b));
	}
	
	/**
	 * Reads in a Strife Thing lump and returns it as a list of Strife Things.
	 * @param in	the input stream to use.
	 * @throws IOException if a read error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static List<Thing> readStrifeThingLump(InputStream in) throws IOException
	{
		return (List<Thing>)readStrifeObjects(Thing.class, in, in.available()/Thing.getStrifeLength());
	}
	
	/**
	 * Reads in a Strife Thing lump and returns it as a list of Strife Things.
	 * @param b		the bytes that make up the lump.
	 * @throws IOException if a read error occurs.
	 */
	public static List<Thing> readStrifeThingLump(byte[] b) throws IOException
	{
		return readStrifeThingLump(new ByteArrayInputStream(b));
	}
	
	/**
	 * Reads in a Sidedef lump and returns it as a list of Sidedefs.
	 * @param in	the input stream to use.
	 * @throws IOException if a read error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static List<Sidedef> readSidedefLump(InputStream in) throws IOException
	{
		return (List<Sidedef>)readDoomObjects(Sidedef.class, in, in.available()/Sidedef.getDoomLength());
	}
	
	/**
	 * Reads in a Sidedef lump and returns it as a list of Sidedefs.
	 * @param b		the bytes that make up the lump.
	 * @throws IOException if a read error occurs.
	 */
	public static List<Sidedef> readSidedefLump(byte[] b) throws IOException
	{
		return readSidedefLump(new ByteArrayInputStream(b));
	}
	
	/**
	 * Reads in a Vertex lump and returns it as a list of Vertices.
	 * @param in	the input stream to use.
	 * @throws IOException if a read error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static List<Vertex> readVertexLump(InputStream in) throws IOException
	{
		return (List<Vertex>)readDoomObjects(Vertex.class, in, in.available()/Vertex.getDoomLength());
	}
	
	/**
	 * Reads in a Vertex lump and returns it as a list of Vertexs.
	 * @param b		the bytes that make up the lump.
	 * @throws IOException if a read error occurs.
	 */
	public static List<Vertex> readVertexLump(byte[] b) throws IOException
	{
		return readVertexLump(new ByteArrayInputStream(b));
	}
	
	/**
	 * Reads in a Sector lump and returns it as a list of Sectors.
	 * @param in	the input stream to use.
	 * @throws IOException if a read error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static List<Sector> readSectorLump(InputStream in) throws IOException
	{
		return (List<Sector>)readDoomObjects(Sector.class, in, in.available()/Sector.getDoomLength());
	}
	
	/**
	 * Reads in a Sector lump and returns it as a list of Sectors.
	 * @param b		the bytes that make up the lump.
	 * @throws IOException if a read error occurs.
	 */
	public static List<Sector> readSectorLump(byte[] b) throws IOException
	{
		return readSectorLump(new ByteArrayInputStream(b));
	}

	/**
	 * Reads a UDMFTable from "textmap" data.
	 * @param in	the input stream to use.
	 * @return a parsed UDMFTable object.
	 * @throws IOException if a read error occurs.
	 * @throws UDMFParseException if an error occurs during UDMF parsing.
	 * @since 2.6.0
	 */
	public static UDMFTable readUDMFTable(InputStream in) throws IOException, UDMFParseException
	{
		return UDMFReader.readData(in);
	}

	/**
	 * Reads a UDMFTable from "textmap" data.
	 * @param b		the bytes that make up the lump.
	 * @return a parsed UDMFTable object.
	 * @throws IOException if a read error occurs.
	 * @throws UDMFParseException if an error occurs during UDMF parsing.
	 * @since 2.6.0
	 */
	public static UDMFTable readUDMFTable(byte[] b) throws IOException, UDMFParseException
	{
		return readUDMFTable(new ByteArrayInputStream(b));
	}

	/**
	 * Pulls a namespace from a UDMFTable (and {@link UDMFUtil}.
	 * @param table the table to read from.
	 * @return a valid UDMFNamespace.
	 * @throws MapException if no namespace can be found, or done that correspond to the namespace declaration.
	 */
	public static UDMFNamespace readUDMFNamespace(UDMFTable table) throws MapException
	{
		String namespace = table.getGlobalFields().get("namespace");
		if (namespace == null)
			throw new MapException("Malformed UDMF Map: No global \"namespace\" declaration.");
		
		UDMFNamespace udmfNamespace = UDMFUtil.getNamespaceForName(namespace);
		if (udmfNamespace == null)
			throw new MapException("UDMF Parse Error: Unsupported Namespace \""+namespace+"\" declared.");

		return udmfNamespace;
	}
	
	/**
	 * Reads a list of thing objects from a UDMFTable.
	 * @param namespace the UDMF namespace to use.
	 * @param table the UDMF table of map contents.
	 * @return a list of things extracted from the table.
	 * @since 2.6.0
	 */
	public static List<Thing> readUDMFThings(UDMFNamespace namespace, UDMFTable table)
	{
		UDMFStruct[] structs = table.getStructs("thing");
		List<Thing> out = new List<Thing>(structs.length);
		for (UDMFStruct struct : structs)
		{
			Thing obj = new Thing();
			namespace.getThingAttribs(struct, obj);
			out.add(obj);
		}
		return out;
	}
	
	/**
	 * Reads a list of linedef objects from a UDMFTable.
	 * @param namespace the UDMF namespace to use.
	 * @param table the UDMF table of map contents.
	 * @return a list of things extracted from the table.
	 * @since 2.6.0
	 */
	public static List<Linedef> readUDMFLinedefs(UDMFNamespace namespace, UDMFTable table)
	{
		UDMFStruct[] structs = table.getStructs("linedef");
		List<Linedef> out = new List<Linedef>(structs.length);
		for (UDMFStruct struct : structs)
		{
			Linedef obj = new Linedef();
			namespace.getLinedefAttribs(struct, obj);
			out.add(obj);
		}
		return out;
	}
	
	/**
	 * Reads a list of sidedef objects from a UDMFTable.
	 * @param namespace the UDMF namespace to use.
	 * @param table the UDMF table of map contents.
	 * @return a list of things extracted from the table.
	 * @since 2.6.0
	 */
	public static List<Sidedef> readUDMFSidedefs(UDMFNamespace namespace, UDMFTable table)
	{
		UDMFStruct[] structs = table.getStructs("sidedef");
		List<Sidedef> out = new List<Sidedef>(structs.length);
		for (UDMFStruct struct : structs)
		{
			Sidedef obj = new Sidedef();
			namespace.getSidedefAttribs(struct, obj);
			out.add(obj);
		}
		return out;
	}
	
	/**
	 * Reads a list of vertex objects from a UDMFTable.
	 * @param namespace the UDMF namespace to use.
	 * @param table the UDMF table of map contents.
	 * @return a list of things extracted from the table.
	 * @since 2.6.0
	 */
	public static List<Vertex> readUDMFVertices(UDMFNamespace namespace, UDMFTable table)
	{
		UDMFStruct[] structs = table.getStructs("vertex");
		List<Vertex> out = new List<Vertex>(structs.length);
		for (UDMFStruct struct : structs)
		{
			Vertex obj = new Vertex();
			namespace.getVertexAttribs(struct, obj);
			out.add(obj);
		}
		return out;
	}
	
	/**
	 * Reads a list of sector objects from a UDMFTable.
	 * @param namespace the UDMF namespace to use.
	 * @param table the UDMF table of map contents.
	 * @return a list of things extracted from the table.
	 * @since 2.6.0
	 */
	public static List<Sector> readUDMFSectors(UDMFNamespace namespace, UDMFTable table)
	{
		UDMFStruct[] structs = table.getStructs("sector");
		List<Sector> out = new List<Sector>(structs.length);
		for (UDMFStruct struct : structs)
		{
			Sector obj = new Sector();
			namespace.getSectorAttribs(struct, obj);
			out.add(obj);
		}
		return out;
	}
	
	/**
	 * Reads in a BSPSegment lump and returns it as a list of BSPSegments.
	 * @param in	the input stream to use.
	 * @throws IOException if a read error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static List<BSPSegment> readBSPSegmentLump(InputStream in) throws IOException
	{
		return (List<BSPSegment>)readDoomObjects(BSPSegment.class, in, in.available()/BSPSegment.getDoomLength());
	}
	
	/**
	 * Reads in a BSPSegment lump and returns it as a list of BSPSegments.
	 * @param b		the byte that make up the lump.
	 * @throws IOException if a read error occurs.
	 */
	public static List<BSPSegment> readBSPSegmentLump(byte[] b) throws IOException
	{
		return readBSPSegmentLump(new ByteArrayInputStream(b));
	}
	
	/**
	 * Reads in a BSPSubsector lump and returns it as a list of BSPSubsectors.
	 * @param in	the input stream to use.
	 * @throws IOException if a read error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static List<BSPSubsector> readBSPSubsectorLump(InputStream in) throws IOException
	{
		return (List<BSPSubsector>)readDoomObjects(BSPSubsector.class, in, in.available()/BSPSubsector.getDoomLength());
	}
	
	/**
	 * Reads in a BSPSubsector lump and returns it as a lisBSPSubsectorectors.
	 * @param b		the byte that make up the lump.
	 * @throws IOException if a read error occurs.
	 */
	public static List<BSPSubsector> readBSPSubsectorLump(byte[] b) throws IOException
	{
		return readBSPSubsectorLump(new ByteArrayInputStream(b));
	}
	
	/**
	 * Reads in a BSPNode lump and returns it as a list of BSPNodes.
	 * @param in	the input stream to use.
	 * @throws IOException if a read error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static List<BSPNode> readBSPNodeLump(InputStream in) throws IOException
	{
		return (List<BSPNode>)readDoomObjects(BSPNode.class, in, in.available()/BSPNode.getDoomLength());
	}
	
	/**
	 * Reads in a BSPNode lump and returns it as a list of BSPNodes.
	 * @param b		the byte that make up the lump.
	 * @throws IOException if a read error occurs.
	 */
	public static List<BSPNode> readBSPNodeLump(byte[] b) throws IOException
	{
		return readBSPNodeLump(new ByteArrayInputStream(b));
	}
	
	// generic function for reading doom object lumps.
	private static List<? extends DoomObject> readDoomObjects(
			Class<? extends DoomObject> objClass, InputStream in, int objCount) throws IOException
	{
		List<DoomObject> out = new List<DoomObject>();

		for (int i = 0; i < objCount; i++)
		{
			DoomObject obj = null;
			try{
				obj = objClass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			obj.readDoomBytes(in);
			out.add(obj);
		}

		return out;
	}
	
	// generic function for reading hexen object lumps.
	private static List<? extends HexenObject> readHexenObjects(
			Class<? extends HexenObject> objClass, InputStream in, int objCount) throws IOException
	{
		List<HexenObject> out = new List<HexenObject>();

		for (int i = 0; i < objCount; i++)
		{
			HexenObject obj = null;
			try{
				obj = objClass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			obj.readHexenBytes(in);
			out.add(obj);
		}

		return out;
	}
	
	// generic function for reading strife object lumps.
	private static List<? extends StrifeObject> readStrifeObjects(
			Class<? extends StrifeObject> objClass, InputStream in, int objCount) throws IOException
	{
		List<StrifeObject> out = new List<StrifeObject>();

		for (int i = 0; i < objCount; i++)
		{
			StrifeObject obj = null;
			try{
				obj = objClass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			obj.readStrifeBytes(in);
			out.add(obj);
		}

		return out;
	}
	
	/**
	 * Writes a set of Doom-formatted objects to an output stream.
	 * @param objects the list of objects to write.
	 * @param out the output stream to write to.
	 * @throws IOException if an error occurred during the write.
	 * @throws DataExportException if the export results in a loss of precision or
	 * some object info is incompatible with the output format.
	 * @since 2.6.0
	 */
	public static void writeDoomObjects(List<? extends DoomObject> objects, OutputStream out) throws IOException
	{
		for (DoomObject dobj : objects)
			dobj.writeDoomBytes(out);
	}
	
	/**
	 * Writes a set of ZDoom/Hexen-formatted objects to an output stream.
	 * @param objects the list of objects to write.
	 * @param out the output stream to write to.
	 * @throws IOException if an error occurred during the write.
	 * @throws DataExportException if the export results in a loss of precision or
	 * some object info is incompatible with the output format.
	 * @since 2.6.0
	 */
	public static void writeHexenObjects(List<? extends HexenObject> objects, OutputStream out) throws IOException
	{
		for (HexenObject hobj : objects)
			hobj.writeHexenBytes(out);
	}
	
	/**
	 * Writes a set of Strife-formatted objects to an output stream.
	 * @param objects the list of objects to write.
	 * @param out the output stream to write to.
	 * @throws IOException if an error occurred during the write.
	 * @throws DataExportException if the export results in a loss of precision or
	 * some object info is incompatible with the output format.
	 * @since 2.6.0
	 */
	public static void writeStrifeObjects(List<? extends StrifeObject> objects, OutputStream out) throws IOException
	{
		for (StrifeObject sobj : objects)
			sobj.writeStrifeBytes(out);
	}
	
	/**
	 * Writes a set of Doom-formatted objects to a WAD file.
	 * @param entryName the name of the entry to write. This is autocorrected.
	 * @param objects the list of objects to write.
	 * @param wf the WAD file to write to.
	 * @throws IOException if an error occurs during the write.
	 * @throws DataExportException if the export results in a loss of precision or
	 * some object info is incompatible with the output format.
	 * @since 2.6.0
	 */
	public static void writeDoomObjectsToWad(String entryName, List<? extends DoomObject> objects, WadFile wf) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		writeDoomObjects(objects, bos);
		wf.add(entryName, bos.toByteArray());
		bos.close();
	}
	
	/**
	 * Writes a set of ZDoom/Hexen-formatted objects to a WAD file.
	 * @param entryName the name of the entry to write. This is autocorrected.
	 * @param objects the list of objects to write.
	 * @param wf the WAD file to write to.
	 * @throws IOException if an error occurs during the write.
	 * @throws DataExportException if the export results in a loss of precision or
	 * some object info is incompatible with the output format.
	 * @since 2.6.0
	 */
	public static void writeHexenObjectsToWad(String entryName, List<? extends HexenObject> objects, WadFile wf) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		writeHexenObjects(objects, bos);
		wf.add(entryName, bos.toByteArray());
		bos.close();
	}
	
	/**
	 * Writes a set of Strife-formatted objects to a WAD file.
	 * @param entryName the name of the entry to write. This is autocorrected.
	 * @param objects the list of objects to write.
	 * @param wf the WAD file to write to.
	 * @throws IOException if an error occurs during the write.
	 * @throws DataExportException if the export results in a loss of precision or
	 * some object info is incompatible with the output format.
	 * @since 2.6.0
	 */
	public static void writeStrifeObjectsToWad(String entryName, List<? extends StrifeObject> objects, WadFile wf) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		writeStrifeObjects(objects, bos);
		wf.add(entryName, bos.toByteArray());
		bos.close();
	}
	
	/**
	 * Writes this map to a WAD file.
	 * @param headerName the name of the map header. This is autocorrected.
	 * @param wf the WAD file to write to.
	 * @throws IOException if an error occurs during the write.
	 * @throws DataExportException if the export results in a loss of precision or
	 * some object info is incompatible with the output format.
	 * @since 2.6.0
	 */
	public void writeToWad(String headerName, WadFile wf) throws IOException 
	{
		wf.addMarker(headerName);
		Format type = getOriginalFormat();
		if (type != Format.UDMF)
		{
			switch (type)
			{
				case DOOM:
					writeDoomObjectsToWad("things", getThingList(), wf);
					writeDoomObjectsToWad("linedefs", getLinedefList(), wf);
					break;
				case HEXEN:
					writeHexenObjectsToWad("things", getThingList(), wf);
					writeHexenObjectsToWad("linedefs", getLinedefList(), wf);
					break;
				case STRIFE:
					writeStrifeObjectsToWad("things", getThingList(), wf);
					writeStrifeObjectsToWad("linedefs", getLinedefList(), wf);
					break;
			}
			
			writeDoomObjectsToWad("sidedefs", getSidedefList(), wf);
			writeDoomObjectsToWad("vertexes", getVertexList(), wf);
			if (segs != null)
				writeDoomObjectsToWad("segs", getBSPSegmentList(), wf);
			if (subsectors != null)
				writeDoomObjectsToWad("ssectors", getBSPSubsectorList(), wf);
			if (nodes != null)
				writeDoomObjectsToWad("nodes", getBSPNodeList(), wf);
			writeDoomObjectsToWad("sectors", getSectorList(), wf);
			
			if (reject != null)
				wf.add("reject", reject.getDoomBytes());
			if (blockmap != null)
				wf.add("blockmap", blockmap.getDoomBytes());
			if (behavior != null)
				wf.add("behavior", behavior.getDoomBytes());
			if (scripts != null)
				wf.add("scripts", scripts.getDoomBytes());
		}
		else // UDMF
		{
			UDMFTable table = UDMFUtil.mapToUDMF(UDMFUtil.getNamespaceForName(originalUDMFNamespace), this);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			UDMFWriter.writeData(table, bos);
			wf.add("textmap",bos.toByteArray());
			if (zNodes != null)
				wf.add("znodes", zNodes.getDoomBytes());
			if (behavior != null)
				wf.add("behavior", behavior.getDoomBytes());
			if (scripts != null)
				wf.add("scripts", scripts.getDoomBytes());
			wf.addMarker("endmap");
		}
	}
	

	/** Returns the list of {@link Thing}s. */
	public List<Thing> getThingList() 						{return things;}
	/** Returns the list of {@link Linedef}s. */
	public List<Linedef> getLinedefList() 					{return linedefs;}
	/** Returns the list of {@link Sidedef}s. */
	public List<Sidedef> getSidedefList()					{return sidedefs;}
	/** Returns the list of {@link Vertex}-es. */
	public List<Vertex> getVertexList()						{return vertices;}
	/** Returns the list of {@link Sector}s. */
	public List<Sector> getSectorList()						{return sectors;}
	
	/**
	 * Returns the BSP segments in the map.
	 * May be null if no nodes were loaded.  
	 */
	public List<BSPSegment> getBSPSegmentList()				{return segs;}
	/**
	 * Returns the BSP Subsectors in the map.
	 * May be null if no nodes were loaded.  
	 */
	public List<BSPSubsector> getBSPSubsectorList()			{return subsectors;}
	/**
	 * Returns the BSP nodes in the map.
	 * May be null if no nodes were loaded.  
	 */
	public List<BSPNode> getBSPNodeList()					{return nodes;}
	/** May return null if not loaded. */
	public Reject getReject()								{return reject;}
	/** May return null if not loaded. */
	public Blockmap getBlockmap()							{return blockmap;}
	
	/** 
	 * Returns raw ZDoom node data.
	 * @since 2.6.0
	 */
	public RawData getZNodes()								{return zNodes;}
	/** Returns the binary ACS executable lump. */
	public RawData getBehavior()							{return behavior;}
	/** Returns the uncompiled script lump. */
	public TextData getScripts()							{return scripts;}

	public Thing getThing(int n)							{return things.getByIndex(n);}
	public Linedef getLinedef(int n)						{return linedefs.getByIndex(n);}
	public Sidedef getSidedef(int n)						{return sidedefs.getByIndex(n);}
	public Vertex getVertex(int n)							{return vertices.getByIndex(n);}
	public Sector getSector(int n)							{return sectors.getByIndex(n);}
	public BSPSegment getBSPSegment(int n)					{return segs.getByIndex(n);}
	public BSPSubsector getBSPSubsector(int n)				{return subsectors.getByIndex(n);}
	public BSPNode getBSPNode(int n)						{return nodes.getByIndex(n);}
	
	/** Returns the number of Things in the List. */
	public int getThingCount()								{return things.size();}
	/** Returns the number of Linedefs in the List. */
	public int getLinedefCount()							{return linedefs.size();}
	/** Returns the number of Sidedefs in the List. */
	public int getSidedefCount()							{return sidedefs.size();}
	/** Returns the number of Vertices in the List. */
	public int getVertexCount()								{return vertices.size();}
	/** Returns the number of Sectors in the List. */
	public int getSectorCount()								{return sectors.size();}
	/** Returns the number of BSP segments in the List. */
	public int getBSPSegmentCount()							{return segs.size();}
	/** Returns the number of BSP subsectors in the List. */
	public int getBSPSubsectorCount()						{return subsectors.size();}
	/** Returns the number of BSP nodes in the List. */
	public int getBSPNodesCount()							{return nodes.size();}

	/**
	 * Returns the front sidedef for a specific linedef.
	 * @param linedef	the linedef to use.
	 * @return	the associated sidedef or null if no sidedef on this side.
	 * @throws	ArrayIndexOutOfBoundsException if the referenced object is a bad reference.
	 */
	public Sidedef getFrontSidedefFor(Linedef linedef)
	{
		if (linedef.getFrontSidedef() >= 0)
			return getSidedef(linedef.getFrontSidedef());
		return null;
	}
	
	/**
	 * Returns the back sidedef for a specific linedef.
	 * @param linedef	the linedef to use.
	 * @return	the associated sidedef or null if no sidedef on this side.
	 * @throws	ArrayIndexOutOfBoundsException if the referenced object is a bad reference.
	 */
	public Sidedef getBackSidedefFor(Linedef linedef)
	{
		if (linedef.getBackSidedef() >= 0)
			return getSidedef(linedef.getBackSidedef());
		return null;
	}
	
	/**
	 * Returns the starting vertex associated with a specific linedef.
	 * @param linedef	the linedef to use.
	 * @return	the associated vertex or null if no vertex is associated with this.
	 * @throws	ArrayIndexOutOfBoundsException if the referenced object is a bad reference.
	 */
	public Vertex getStartingVertexFor(Linedef linedef)
	{
		if (linedef.getVertexStart() >= 0)
			return getVertex(linedef.getVertexStart());
		return null;
	}

	/**
	 * Returns the ending vertex associated with a specific linedef.
	 * @param linedef	the linedef to use.
	 * @return	the associated vertex or null if no vertex is associated with this.
	 * @throws	ArrayIndexOutOfBoundsException if the referenced object is a bad reference.
	 */
	public Vertex getEndingVertexFor(Linedef linedef)
	{
		if (linedef.getVertexEnd() >= 0)
			return getVertex(linedef.getVertexEnd());
		return null;
	}

	/**
	 * Returns the starting vertex associated with a specific segment.
	 * @param segment	the segment to use.
	 * @return	the associated vertex or null if no vertex is associated with this.
	 * @throws	ArrayIndexOutOfBoundsException if the referenced object is a bad reference.
	 */
	public Vertex getStartingVertexFor(BSPSegment segment)
	{
		if (segment.getVertexStart() >= 0)
			return getVertex(segment.getVertexStart());
		return null;
	}

	/**
	 * Returns the ending vertex associated with a specific segment.
	 * @param segment	the segment to use.
	 * @return	the associated vertex or null if no vertex is associated with this.
	 * @throws	ArrayIndexOutOfBoundsException if the referenced object is a bad reference.
	 */
	public Vertex getEndingVertexFor(BSPSegment segment)
	{
		if (segment.getVertexEnd() >= 0)
			return getVertex(segment.getVertexEnd());
		return null;
	}

	/**
	 * Returns the linedef associated with a specific segment.
	 * @param segment	the segment to use.
	 * @return	the associated linedef or null if no linedef is associated with this.
	 * @throws	ArrayIndexOutOfBoundsException if the referenced object is a bad reference.
	 */
	public Linedef getLinedefFor(BSPSegment segment)
	{
		if (segment.getLinedefIndex() >= 0)
			return getLinedef(segment.getLinedefIndex());
		return null;
	}

	/**
	 * Returns the sector associated with a specific sidedef.
	 * @param sidedef	the sidedef to use.
	 * @return	the associated sector or null if no sector is associated with this.
	 * @throws	ArrayIndexOutOfBoundsException if the referenced object is a bad reference.
	 */
	public Sector getSectorFor(Sidedef sidedef)
	{
		if (sidedef.getSectorRef() >= 0)
			return getSector(sidedef.getSectorRef());
		return null;
	}
	
	/**
	 * Returns the BSP segments associated with a specific subsector.
	 * @param subsector	the subsector to use.
	 * @return	the associated segments.
	 * @throws	ArrayIndexOutOfBoundsException if the referenced object contains bad references.
	 */
	public BSPSegment[] getSegmentsFor(BSPSubsector subsector)
	{
		BSPSegment[] out = new BSPSegment[subsector.getSegCount()];
		for (int i = 0; i < out.length; i++)
			out[i] = getBSPSegment(subsector.getSegStartIndex()+i);
		return out;
	}
	
}
