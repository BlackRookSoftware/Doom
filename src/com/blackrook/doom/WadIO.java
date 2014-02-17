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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.blackrook.commons.ObjectPair;
import com.blackrook.commons.linkedlist.Queue;
import com.blackrook.commons.linkedlist.Stack;
import com.blackrook.commons.list.SortedMap;
import com.blackrook.commons.list.SortedList;
import com.blackrook.doom.enums.DataFormat;
import com.blackrook.doom.enums.GameType;
import com.blackrook.doom.struct.BSPNode;
import com.blackrook.doom.struct.BSPSegment;
import com.blackrook.doom.struct.BSPSubsector;
import com.blackrook.doom.struct.ColorMapLump;
import com.blackrook.doom.struct.EndDoom;
import com.blackrook.doom.struct.Flat;
import com.blackrook.doom.struct.Linedef;
import com.blackrook.doom.struct.MUSData;
import com.blackrook.doom.struct.PNGData;
import com.blackrook.doom.struct.PaletteLump;
import com.blackrook.doom.struct.Patch;
import com.blackrook.doom.struct.PatchNameLump;
import com.blackrook.doom.struct.RawData;
import com.blackrook.doom.struct.Sector;
import com.blackrook.doom.struct.Sidedef;
import com.blackrook.doom.struct.TextData;
import com.blackrook.doom.struct.TextureLump;
import com.blackrook.doom.struct.Thing;
import com.blackrook.doom.struct.Vertex;
import com.blackrook.doom.udmf.UDMFTable;
import com.blackrook.doom.udmf.UDMFUtil;
import com.blackrook.doom.udmf.UDMFWriter;
import com.blackrook.doom.udmf.namespace.UDMFNamespace;
import com.blackrook.doom.util.DoomUtil;
import com.blackrook.io.SuperWriter;

/**
 * Wad I/O class for assisting reading/writing Wad files.
 * @author Matthew Tropiano
 * @deprecated Since 2.6.0 - there really is no reason to use this class.
 */
public final class WadIO
{
	private WadIO() {}	// Can't instantiate.
	
	/**
	 * Writes this to an output stream in a Wad file format.
	 * @param wad		the BufferedWad to write.
	 * @param out		the file to write to.
	 * @param format	the overall format to write this 
	 * @param mapType	the map type format to write.
	 * @param wadType	the wad type.
	 * @throws IOException if something goes awry when a wad file gets written.
	 * @throws DataExportException if a data export goes wrong.
	 */
	public static void writeWad(
			BufferedWad wad, File out, 
			DataFormat format, DoomMap.Format mapType,
			WadFile.Type wadType) throws IOException
	{
		writeWad(wad, out, format, mapType, null, wadType);
	}
	
	/**
	 * Writes this to an output stream in a Wad file format.
	 * @param wad			the BufferedWad to write.
	 * @param out			the file to write to.
	 * @param format		the overall format to write this 
	 * @param mapType		the map type format to write.
	 * @param udmfNamespace	if UDMF map type, this is the namespace for the UDMF.
	 * @param wadType		the wad type.
	 * @throws IOException if something goes awry when a wad file gets written.
	 * @throws DataExportException if a data export goes wrong.
	 */
	public static void writeWad(
			BufferedWad wad, File out, 
			DataFormat format, DoomMap.Format mapType, 
			UDMFNamespace udmfNamespace, WadFile.Type wadType) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(out);
		writeWad(wad, fos, format, mapType, udmfNamespace, wadType);
		fos.close();
	}
	
	/**
	 * Writes this to an output stream in a Wad file format.
	 * The stream is NOT closed afterwards.
	 * @param wad		the BufferedWad to write.
	 * @param out		the output stream to write to.
	 * @param format	the overall format to write this 
	 * @param mapType	the map type format to write.
	 * @param wadType	the wad type.
	 * @throws IOException if something goes awry when a wad file gets written.
	 * @throws DataExportException if a data export goes wrong.
	 */
	public static void writeWad(
			BufferedWad wad, OutputStream out, 
			DataFormat format, DoomMap.Format mapType,
			WadFile.Type wadType) throws IOException
	{
		writeWad(wad, out, format, mapType, null, wadType);
	}
	
	/**
	 * Writes this to an output stream in a Wad file format.
	 * The stream is NOT closed afterwards.
	 * @param wad			the BufferedWad to write.
	 * @param out			the output stream to write to.
	 * @param format		the overall format to write this 
	 * @param mapType		the map type format to write.
	 * @param udmfNamespace	if UDMF map type, this is the namespace for the UDMF.
	 * @param wadType		the wad type.
	 * @throws IOException if something goes awry when a wad file gets written.
	 * @throws DataExportException if a data export goes wrong.
	 */
	public static void writeWad(
			BufferedWad wad, OutputStream out, 
			DataFormat format, DoomMap.Format mapType, 
			UDMFNamespace udmfNamespace, WadFile.Type wadType) throws IOException
	{
		if (mapType == DoomMap.Format.UDMF && udmfNamespace == null)
			throw new IllegalArgumentException("UDMF map types need a vaild, non-null namespace.");
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Queue<Entry> entryList = new Queue<Entry>();
		
		writeObjectList(wad.getGlobalList(), bos, entryList, format);
		for (ObjectPair<String,DoomMap> obj : wad.getMapList())
			writeMap(DoomUtil.coerceToEntrySize(obj.getKey()), obj.getValue(), udmfNamespace, bos, entryList, mapType);
		writeObjectList(wad.getMusicList(), bos, entryList, format);
		writeObjectList(wad.getSoundList(), bos, entryList, format);
		writeObjectList(wad.getGraphicList(), bos, entryList, format);

		new Entry("pp_start", 0, bos.size()+12);
		writeObjectList(wad.getPatchList(), bos, entryList, format);
		new Entry("pp_end", 0, bos.size()+12);

		new Entry("ff_start", 0, bos.size()+12);
		writeObjectList(wad.getFlatList(), bos, entryList, format);
		new Entry("ff_end", 0, bos.size()+12);

		new Entry("tx_start", 0, bos.size()+12);
		writeObjectList(wad.getTextureList(), bos, entryList, format);
		new Entry("tx_end", 0, bos.size()+12);

		new Entry("hi_start", 0, bos.size()+12);
		writeObjectList(wad.getHighResolutionTextureList(), bos, entryList, format);
		new Entry("hi_end", 0, bos.size()+12);

		new Entry("c_start", 0, bos.size()+12);
		writeObjectList(wad.getColormapList(), bos, entryList, format);
		new Entry("c_end", 0, bos.size()+12);

		new Entry("a_start", 0, bos.size()+12);
		writeObjectList(wad.getACSLibraryList(), bos, entryList, format);
		new Entry("a_end", 0, bos.size()+12);

		new Entry("v_start", 0, bos.size()+12);
		writeObjectList(wad.getVoiceList(), bos, entryList, format);
		new Entry("v_end", 0, bos.size()+12);

		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);

		sw.writeASCIIString(wadType.toString());
		sw.writeInt(entryList.size());
		sw.writeInt(bos.size()+12);
		sw.writeBytes(bos.toByteArray());
		
		for (Entry entry : entryList)
			sw.writeBytes(entry.toBytes());
	}
	
	/**
	 * Reads in a wad, using intuition to figure out where each entry is classified,
	 * and returns a new BufferedWad with all of the data.
	 * @param inFile	the input file. Should be a Wad file.
	 * @param gameType	the expected game type.
	 */
	public static BufferedWad readWad(File inFile, GameType gameType) throws IOException, WadException
	{
		return readWad(new WadFile(inFile), gameType);
	}

	/**
	 * Reads in a wad, using intuition to figure out where each entry is classified,
	 * and returns a new BufferedWad with all of the data.
	 * @param wf		the input Wad file.
	 * @param gameType	the expected game type.
	 */
	public static BufferedWad readWad(DoomWad wf, GameType gameType) throws IOException, WadException
	{
		BufferedWad out = new BufferedWad();
		
		DoomWadEntry currentEntry = null;
	
		SortedList<Integer> mapIndices = new SortedList<Integer>();
		for (int i : DoomMap.getAllMapIndices(wf))
			mapIndices.add(i);
	
		Stack<String> stack = new Stack<String>(); 
		
		for (int i = 0; i < wf.getSize(); i++)
		{
			byte[] bytedata = wf.getData(i);
			currentEntry = wf.getEntry(i);
			String name = currentEntry.getName().toLowerCase();
			
			if (name.endsWith("_start"))
			{
				String prefix = name.substring(0, name.indexOf("_"));
				stack.push(prefix);
			}
			else if (name.endsWith("_end"))
			{
				String prefix = name.substring(0, name.indexOf("_"));
				if (!stack.peek().equals(prefix))
					throw new WadException("Marker mismatch: '"+prefix+"_end' before '"+stack.peek()+"_start'.");
				stack.pop();
			}
			else
			{
				String marktop = stack.peek();
				if (stack.isEmpty())
				{
					if (mapIndices.contains(i))
						out.addMap(name, new DoomMap(wf, name));
					else if (checkMusic(gameType, name))
						out.addMusic(name, readMusicInfo(bytedata));
					else if (checkSound(gameType, name))
						out.addSound(name, new RawData(bytedata));
					else if (checkGraphic(gameType, name))
						out.addGraphic(name, readGraphicObject(bytedata));
					else
					{
						if (DoomUtil.isColormapLump(name))
						{
							ColorMapLump lump = new ColorMapLump();
							readObjectInfo(lump, bytedata, gameType.getDefaultInternalFormat());
							out.addGlobal(name,lump);
						}
						else if (DoomUtil.isPaletteLump(name))
						{
							PaletteLump lump = new PaletteLump();
							readObjectInfo(lump, bytedata, gameType.getDefaultInternalFormat());
							out.addGlobal(name,lump);
						}
						else if (DoomUtil.isTextLump(name))
						{
							TextData lump = new TextData();
							readObjectInfo(lump, bytedata, gameType.getDefaultInternalFormat());
							out.addGlobal(name,lump);
						}
						else if (DoomUtil.isTextureLump(name))
						{
							TextureLump lump = new TextureLump();
							readObjectInfo(lump, bytedata, gameType.getDefaultInternalFormat());
							out.addGlobal(name,lump);
						}
						else if (DoomUtil.isPatchNamesLump(name))
						{
							PatchNameLump lump = new PatchNameLump();
							readObjectInfo(lump, bytedata, gameType.getDefaultInternalFormat());
							out.addGlobal(name,lump);
						}
						else if (DoomUtil.isEndoomLump(name))
						{
							EndDoom lump = new EndDoom();
							readObjectInfo(lump, bytedata, gameType.getDefaultInternalFormat());
							out.addGlobal(name,lump);
						}
						else if (!DoomUtil.isMapDataLump(name))
						{
							RawData lump = new RawData();
							readObjectInfo(lump, bytedata, gameType.getDefaultInternalFormat());
							out.addGlobal(name,lump);
						}
					}
				}
				else if (marktop.startsWith("p"))
				{
					out.addPatch(name, readGraphicObject(bytedata));
				}
				else if (marktop.startsWith("f"))
				{
					out.addFlat(name, readFlatObject(bytedata));
				}
				else if (marktop.startsWith("c"))
				{
					ColorMapLump c = new ColorMapLump();
					readObjectInfo(c, bytedata, gameType.getDefaultInternalFormat());
					out.addColormap(name, c);
				}
				else if (marktop.startsWith("a"))
				{
					RawData lump = new RawData();
					readObjectInfo(lump, bytedata, gameType.getDefaultInternalFormat());
					out.addACSLibrary(name, lump);
				}
				else if (marktop.startsWith("v"))
				{
					RawData lump = new RawData();
					readObjectInfo(lump, bytedata, gameType.getDefaultInternalFormat());
					out.addVoice(name, lump);
				}
				else if (marktop.startsWith("s"))
				{
					out.addSprite(name, readGraphicObject(bytedata));
				}
				else if (marktop.equals("tx"))
				{
					out.addTexture(name, readGraphicObject(bytedata));
				}
				else if (marktop.startsWith("h"))
				{
					out.addHighResolutionTexture(name, readGraphicObject(bytedata));
				}
			}
		}
		
		return out;
	}

	/**
	 * Writes an object list out to the data blob and entry list.
	 */
	private static void writeObjectList(
			SortedMap<String, ? extends DoomObject> list,
			ByteArrayOutputStream bos, Queue<Entry> entryList, 
			DataFormat format) throws IOException
	{
		for (ObjectPair<String, ? extends DoomObject> obj : list)
		{
			byte[] b = getObjectData(obj.getValue(), format);
			entryList.add(new Entry(DoomUtil.coerceToEntrySize(obj.getKey()), b.length, bos.size()+12));
			bos.write(b);
		}
	}
	
	private static byte[] getObjectData(DoomObject obj, DataFormat format) throws DataExportException
	{
		switch (format)
		{
			case HEXEN:
			{
				if (obj instanceof HexenObject)
					return ((HexenObject)obj).getHexenBytes();
				else
					return obj.getDoomBytes();
			}
			case STRIFE:
			{
				if (obj instanceof StrifeObject)
					return ((StrifeObject)obj).getStrifeBytes();
				else
					return obj.getDoomBytes();
			}
			case DOOM:
			default:
				return obj.getDoomBytes();
		}
	}
	
	/**
	 * Writes a map out to the data blob and entry list.
	 */
	private static void writeMap(
			String mapName, DoomMap map, UDMFNamespace udmfNamespace,
			ByteArrayOutputStream bos, Queue<Entry> entryList, 
			DoomMap.Format mapType) throws IOException
	{
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		entryList.add(new Entry(mapName,0,bos.size()+12));
		
		if (mapType == DoomMap.Format.UDMF)
		{
			UDMFTable table = UDMFUtil.mapToUDMF(udmfNamespace, map);
			UDMFWriter.writeData(table, data);
			entryList.add(new Entry("textmap",data.size(),bos.size()+12));
			bos.write(data.toByteArray());
			data.reset();

			// NOTE: Duped later because some old ports require a specific order.
			for (BSPSegment s : map.getBSPSegmentList())
				s.writeDoomBytes(data);
			if (data.size() > 0)
			{
				entryList.add(new Entry("segs",data.size(),bos.size()+12));
				bos.write(data.toByteArray());
				data.reset();
			}

			for (BSPSubsector s : map.getBSPSubsectorList())
				s.writeDoomBytes(data);
			if (data.size() > 0)
			{
				entryList.add(new Entry("ssectors",data.size(),bos.size()+12));
				bos.write(data.toByteArray());
				data.reset();
			}

			for (BSPNode s : map.getBSPNodeList())
				s.writeDoomBytes(data);
			if (data.size() > 0)
			{
				entryList.add(new Entry("nodes",data.size(),bos.size()+12));
				bos.write(data.toByteArray());
				data.reset();
			}
		}
		else 
		{
			switch (mapType)
			{
				case DOOM:
				{
					for (Thing t : map.getThingList())
						t.writeDoomBytes(data);
					entryList.add(new Entry("things",data.size(),bos.size()+12));
					bos.write(data.toByteArray());
					data.reset();
					
					for (Linedef l : map.getLinedefList())
						l.writeDoomBytes(data);
					entryList.add(new Entry("linedefs",data.size(),bos.size()+12));
					bos.write(data.toByteArray());
					data.reset();
				}
					break;
				case HEXEN:
				{
					for (Thing t : map.getThingList())
						t.writeHexenBytes(data);
					entryList.add(new Entry("things",data.size(),bos.size()+12));
					bos.write(data.toByteArray());
					data.reset();
					
					for (Linedef l : map.getLinedefList())
						l.writeHexenBytes(data);
					entryList.add(new Entry("linedefs",data.size(),bos.size()+12));
					bos.write(data.toByteArray());
					data.reset();
				}
					break;
				case STRIFE:
				{
					for (Thing t : map.getThingList())
						t.writeStrifeBytes(data);
					entryList.add(new Entry("things",data.size(),bos.size()+12));
					bos.write(data.toByteArray());
					data.reset();
					
					for (Linedef l : map.getLinedefList())
						l.writeStrifeBytes(data);
					entryList.add(new Entry("linedefs",data.size(),bos.size()+12));
					bos.write(data.toByteArray());
					data.reset();
				}
					break;
			}
			
			for (Sidedef s : map.getSidedefList())
				s.writeDoomBytes(data);
			entryList.add(new Entry("sidedefs",data.size(),bos.size()+12));
			bos.write(data.toByteArray());
			data.reset();
			
			for (Vertex v : map.getVertexList())
				v.writeDoomBytes(data);
			entryList.add(new Entry("vertexes",data.size(),bos.size()+12));
			bos.write(data.toByteArray());
			data.reset();

			for (BSPSegment s : map.getBSPSegmentList())
				s.writeDoomBytes(data);
			if (data.size() > 0)
			{
				entryList.add(new Entry("segs",data.size(),bos.size()+12));
				bos.write(data.toByteArray());
				data.reset();
			}

			for (BSPSubsector s : map.getBSPSubsectorList())
				s.writeDoomBytes(data);
			if (data.size() > 0)
			{
				entryList.add(new Entry("ssectors",data.size(),bos.size()+12));
				bos.write(data.toByteArray());
				data.reset();
			}

			for (BSPNode s : map.getBSPNodeList())
				s.writeDoomBytes(data);
			if (data.size() > 0)
			{
				entryList.add(new Entry("nodes",data.size(),bos.size()+12));
				bos.write(data.toByteArray());
				data.reset();
			}

			for (Sector s : map.getSectorList())
				s.writeDoomBytes(data);
			entryList.add(new Entry("sectors",data.size(),bos.size()+12));
			bos.write(data.toByteArray());
			data.reset();
		}
				
		if (map.getReject() != null)
		{
			map.getReject().writeDoomBytes(data);
			entryList.add(new Entry("reject",data.size(),bos.size()+12));
			bos.write(data.toByteArray());
			data.reset();
		}
		
		if (map.getBlockmap() != null)
		{
			map.getBlockmap().writeDoomBytes(data);
			entryList.add(new Entry("blockmap",data.size(),bos.size()+12));
			bos.write(data.toByteArray());
			data.reset();
		}

		if (mapType == DoomMap.Format.HEXEN || 
				(udmfNamespace != null && udmfNamespace.getName().equals(UDMFUtil.HEXEN_NAMESPACE.getName())))
		{
			map.getBehavior().writeDoomBytes(data);
			entryList.add(new Entry("behavior",data.size(),bos.size()+12));
			bos.write(data.toByteArray());
			data.reset();

			map.getScripts().writeDoomBytes(data);
			entryList.add(new Entry("scripts",data.size(),bos.size()+12));
			bos.write(data.toByteArray());
			data.reset();
		}
		
		if (mapType == DoomMap.Format.UDMF)
			entryList.add(new Entry("endmap",0,bos.size()+12));

	}
	
	private static boolean checkMusic(GameType gameType, String name)
	{
		switch (gameType)
		{
			case DOOM:
				return DoomUtil.isDoomMusicLump(name);
			case DOOM2:
			case TNT:
			case PLUTONIA:
				return DoomUtil.isDoom2MusicLump(name);
			case HERETIC:
				return DoomUtil.isHereticMusicLump(name);
			case HEXEN:
				return DoomUtil.isHexenMusicLump(name);
			case STRIFE:
				return DoomUtil.isStrifeMusicLump(name);
		}
		return false;
	}
	
	private static boolean checkSound(GameType gameType, String name)
	{
		switch (gameType)
		{
			case DOOM:
				return DoomUtil.isDoomSoundLump(name);
			case DOOM2:
			case TNT:
			case PLUTONIA:
				return DoomUtil.isDoom2SoundLump(name);
			case HERETIC:
				return DoomUtil.isHereticSoundLump(name);
			case HEXEN:
				return DoomUtil.isHexenSoundLump(name);
			case STRIFE:
				return DoomUtil.isStrifeSoundLump(name);
		}
		return false;
	}
	
	private static boolean checkGraphic(GameType gameType, String name)
	{
		switch (gameType)
		{
			case DOOM:
				return DoomUtil.isDoomGraphicLump(name);
			case DOOM2:
			case TNT:
			case PLUTONIA:
				return DoomUtil.isDoom2GraphicLump(name);
			case HERETIC:
				return DoomUtil.isHereticGraphicLump(name);
			case HEXEN:
				return DoomUtil.isHexenGraphicLump(name);
			case STRIFE:
				return DoomUtil.isStrifeGraphicLump(name);
		}
		return false;
	}
	
	private static DoomObject readMusicInfo(byte[] b) throws IOException
	{
		DoomObject out = null;

		if (b.length >= 4 && b[0] == 'M' && b[1] == 'U' && b[2] == 'S' && b[3] == 0x1a)
		{
			MUSData d = new MUSData();
			d.readDoomBytes(new ByteArrayInputStream(b));
			out = d;
		}
		else
		{
			RawData d = new RawData();
			d.readDoomBytes(new ByteArrayInputStream(b));
			out = d;
		}
		
		return out;
	}
	
	private static void readObjectInfo(DoomObject obj, byte[] b, DataFormat format) throws IOException
	{
		switch (format)
		{
			case HEXEN:
			{
				if (obj instanceof HexenObject)
					((HexenObject)obj).readHexenBytes(new ByteArrayInputStream(b));
				else
					obj.readDoomBytes(new ByteArrayInputStream(b));
			}
			case STRIFE:
			{
				if (obj instanceof StrifeObject)
					((StrifeObject)obj).readStrifeBytes(new ByteArrayInputStream(b));
				else
					obj.readDoomBytes(new ByteArrayInputStream(b));
			}
			case DOOM:
			default:
				obj.readDoomBytes(new ByteArrayInputStream(b));
		}
	}
	
	private static DoomGraphicObject readGraphicObject(byte[] b) throws IOException
	{
		DoomGraphicObject out = null;
		
		if (b.length >= 4 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G')
		{
			PNGData p = new PNGData();
			p.readDoomBytes(new ByteArrayInputStream(b));
			out = p;
		}
		else if (b.length == 64000)
		{
			Flat p = new Flat(320,200);
			p.readDoomBytes(new ByteArrayInputStream(b));
			out = p;
		}
		else
		{
			Patch p = new Patch();
			p.readDoomBytes(new ByteArrayInputStream(b));
			out = p;
		}
		return out;
	}
	
	private static DoomGraphicObject readFlatObject(byte[] b) throws IOException
	{
		DoomGraphicObject out = null;
		
		if (b.length >= 4 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G')
		{
			PNGData p = new PNGData();
			p.readDoomBytes(new ByteArrayInputStream(b));
			out = p;
		}
		else
		{
			int sq = (int)Math.sqrt(b.length);
			Flat p = new Flat(sq,sq);
			p.readDoomBytes(new ByteArrayInputStream(b));
			out = p;
		}
		return out;
	}

	/**
	 * This is the structure of WadFile entries.
	 * @author Matthew Tropiano
	 */
	private static class Entry implements DoomWadEntry
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
		
		public String toString()
		{
			return name+" Size: "+size+", Offset: "+offset;
		}
		
		/**
		 * Converts this WadEntry into bytes.
		 */
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

		@Override
		public String getName()
		{
			return name;
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
		
	}

}
