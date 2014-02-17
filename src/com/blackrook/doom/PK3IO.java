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
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.blackrook.commons.Common;
import com.blackrook.commons.ObjectPair;
import com.blackrook.commons.list.SortedMap;
import com.blackrook.doom.enums.DataFormat;
import com.blackrook.doom.enums.GameType;
import com.blackrook.doom.struct.ColorMapLump;
import com.blackrook.doom.struct.EndDoom;
import com.blackrook.doom.struct.Flat;
import com.blackrook.doom.struct.MUSData;
import com.blackrook.doom.struct.PNGData;
import com.blackrook.doom.struct.PaletteLump;
import com.blackrook.doom.struct.Patch;
import com.blackrook.doom.struct.PatchNameLump;
import com.blackrook.doom.struct.RawData;
import com.blackrook.doom.struct.TextData;
import com.blackrook.doom.struct.TextureLump;
import com.blackrook.doom.udmf.namespace.UDMFNamespace;
import com.blackrook.doom.util.DoomUtil;

/**
 * PK3 I/O class for assisting reading/writing ZDoom PK3s.
 * @author Matthew Tropiano
 * @deprecated Since 2.6.0 - there really is no reason to use this class.
 */
public final class PK3IO
{
	private PK3IO() {}

	/**
	 * Writes a PK3 of a buffered wad.
	 * The entries written to the PK3 will have ".lmp" extensions written to the end,
	 * except for known entry names and types and maps.
	 * @param wad				the buffered wad object with all of the data.
	 * @param file				the file to write the data.
	 * @param format			the overall format to write this 
	 * @param mapType			the map type format to write.
	 * @throws IOException		if the write fails for any reason.
	 */
	public static void writePK3(BufferedWad wad, File file, DataFormat format, DoomMap.Format mapType) throws IOException
	{
		writePK3(wad, file, format, mapType, null);
	}

	/**
	 * Writes a PK3 of a buffered wad.
	 * The entries written to the PK3 will have ".lmp" extensions written to the end,
	 * except for known entry names and types and maps.
	 * @param wad				the buffered wad object with all of the data.
	 * @param file				the file to write the data.
	 * @param format			the overall format to write this 
	 * @param mapType			the map type format to write.
	 * @param udmfNamespace		if UDMF map type, this is the namespace for the UDMF.
	 * @throws IOException		if the write fails for any reason.
	 */
	public static void writePK3(BufferedWad wad, 
			File file, DataFormat format, DoomMap.Format mapType, 
			UDMFNamespace udmfNamespace) throws IOException
	{
		writePK3(wad, file, format, mapType, udmfNamespace, Deflater.BEST_COMPRESSION);
	}
	
	/**
	 * Writes a PK3 of a buffered wad.
	 * The entries written to the PK3 will have ".lmp" extensions written to the end,
	 * except for known entry names and types and maps.
	 * @param wad				the buffered wad object with all of the data.
	 * @param file				the file to write the data.
	 * @param format			the overall format to write this 
	 * @param mapType			the map type format to write.
	 * @param udmfNamespace		if UDMF map type, this is the namespace for the UDMF.
	 * @param deflationLevel	the java.util.Deflator level to use.
	 * @throws IOException		if the write fails for any reason.
	 */
	public static void writePK3(BufferedWad wad, 
			File file, DataFormat format, DoomMap.Format mapType, 
			UDMFNamespace udmfNamespace, int deflationLevel) throws IOException
	{
		ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(file));
		zout.setLevel(deflationLevel);
		writePK3(wad, zout, format, mapType, udmfNamespace);
		zout.close();
	}
	
	/**
	 * Writes a PK3 of a buffered wad.
	 * The OutputStream will NOT be closed at the end of this.
	 * The entries written to the PK3 will have ".lmp" extensions written to the end,
	 * except for known entry names and types.
	 * @param wad				the buffered wad object with all of the data.
	 * @param zout				the zipped output stream to write the data.
	 * @param format			the overall format to write this 
	 * @param mapType			the map type format to write.
	 * @param udmfNamespace		if UDMF map type, this is the namespace for the UDMF.
	 * @throws IOException		if the write fails for any reason.
	 */
	public static void writePK3(BufferedWad wad, ZipOutputStream zout, DataFormat format, 
			DoomMap.Format mapType, UDMFNamespace udmfNamespace) throws IOException
	{
		writeObjects(zout, "", wad.getGlobalList(), format);
		writeObjects(zout, DoomPK3.ACS_DIRECTORY+"/", wad.getACSLibraryList(), format);
		writeObjects(zout, DoomPK3.COLORMAPS_DIRECTORY+"/", wad.getColormapList(), format);
		writeObjects(zout, DoomPK3.FLATS_DIRECTORY+"/", wad.getFlatList(), format);
		writeObjects(zout, DoomPK3.GRAPHICS_DIRECTORY+"/", wad.getGraphicList(), format);
		writeObjects(zout, DoomPK3.HIRES_DIRECTORY+"/", wad.getHighResolutionTextureList(), format);
		writeObjects(zout, DoomPK3.MUSIC_DIRECTORY+"/", wad.getMusicList(), format);
		writeObjects(zout, DoomPK3.PATCHES_DIRECTORY+"/", wad.getPatchList(), format);
		writeObjects(zout, DoomPK3.SOUNDS_DIRECTORY+"/", wad.getSoundList(), format);
		writeObjects(zout, DoomPK3.SPRITES_DIRECTORY+"/", wad.getSpriteList(), format);
		writeObjects(zout, DoomPK3.TEXTURES_DIRECTORY+"/", wad.getTextureList(), format);
		writeObjects(zout, DoomPK3.VOICES_DIRECTORY+"/", wad.getVoiceList(), format);

		writeMaps(zout, DoomPK3.MAPS_DIRECTORY+"/", wad.getMapList(), format, mapType, udmfNamespace);
	}
	
	private static void writeMaps(ZipOutputStream zout, String directoryPrefix, 
			SortedMap<String, DoomMap> list, DataFormat format, DoomMap.Format mapType, UDMFNamespace udmfNamespace) throws IOException
	{
		for (ObjectPair<String, DoomMap> sn : list)
		{
			String name = directoryPrefix+sn.getKey().toLowerCase();
			name = name.concat(".wad");
			writeMap(zout, name, sn.getKey(), sn.getValue(), format, mapType, udmfNamespace);
		}
	}

	private static void writeObjects(ZipOutputStream zout, String directoryPrefix, 
			SortedMap<String, ? extends DoomObject> list, DataFormat format) throws IOException
	{
		for (ObjectPair<String, ? extends DoomObject> sn : list)
		{
			String name = directoryPrefix+sn.getKey().toLowerCase();
			name = name.concat(getExtensionForKnownType(name));
			writeEntry(zout, name, sn.getValue(), format);
		}
	}
	
	private static void writeMap(ZipOutputStream zout, String entryName, String mapName, 
			DoomMap map, DataFormat format, DoomMap.Format mapType, UDMFNamespace udmfNamespace) throws IOException
	{
		zout.putNextEntry(new ZipEntry(entryName));
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BufferedWad bw = new BufferedWad();
		bw.addMap(mapName, map);
		WadIO.writeWad(bw, bos, format, mapType, udmfNamespace, WadFile.Type.PWAD);
		zout.write(bos.toByteArray());
		
		zout.closeEntry();
	}
	
	private static void writeEntry(ZipOutputStream zout, String name, DoomObject obj, DataFormat format) throws IOException
	{
		zout.putNextEntry(new ZipEntry(name));
		switch (format)
		{
			case DOOM:
				obj.writeDoomBytes(zout);
				break;
			case HEXEN:
			{
				if (obj instanceof HexenObject)
					((HexenObject)obj).writeHexenBytes(zout);
				else
					obj.writeDoomBytes(zout);
			}
				break;
			case STRIFE:
			{
				if (obj instanceof StrifeObject)
					((StrifeObject)obj).writeStrifeBytes(zout);
				else
					obj.writeDoomBytes(zout);
			}
				break;
		}
		zout.closeEntry();
	}

	private static String getExtensionForKnownType(String name)
	{
		if (DoomUtil.isTextLump(name))
			return ".txt";
		return ".lmp";
	}
	
	/**
	 * Reads in a PK3 and converts it to a BufferedWad.
	 * @param pk3		the input PK3 file.
	 * @param gameType	the source game type to use as a guide for reading the data. 
	 * @throws WadException if one the map files contained in the PK3 is not a map file.
	 */
	public static BufferedWad readPK3(File pk3, GameType gameType) throws IOException, WadException
	{
		return readPK3(new DoomPK3(pk3), gameType);
	}
	
	/**
	 * Reads in a PK3 and converts it to a BufferedWad.
	 * @param pk3		the input PK3 file.
	 * @param gameType	the source game type to use as a guide for reading the data. 
	 * @throws WadException if one the map files contained in the PK3 is not a map file.
	 */
	public static BufferedWad readPK3(DoomPK3 pk3, GameType gameType) throws IOException, WadException
	{
		BufferedWad out = new BufferedWad();
		
		for (ZipEntry ze : pk3.getGlobals())
		{
			String name = getLumpName(ze);
			byte[] bytedata = Common.getBinaryContents(pk3.getInputStream(ze));
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
			else
			{
				RawData lump = new RawData();
				readObjectInfo(lump, bytedata, gameType.getDefaultInternalFormat());
				out.addGlobal(name,lump);
			}
		}
		
		for (ZipEntry ze : pk3.getPatches())
		{
			String name = getLumpName(ze);
			byte[] bytedata = Common.getBinaryContents(pk3.getInputStream(ze));
			out.addPatch(name, readGraphicObject(bytedata));
		}
		
		for (ZipEntry ze : pk3.getGraphics())
		{
			String name = getLumpName(ze);
			byte[] bytedata = Common.getBinaryContents(pk3.getInputStream(ze));
			out.addGraphic(name, readGraphicObject(bytedata));
		}
		
		for (ZipEntry ze : pk3.getTextures())
		{
			String name = getLumpName(ze);
			byte[] bytedata = Common.getBinaryContents(pk3.getInputStream(ze));
			out.addTexture(name, readGraphicObject(bytedata));
		}

		for (ZipEntry ze : pk3.getHiResolutionTextures())
		{
			String name = getLumpName(ze);
			byte[] bytedata = Common.getBinaryContents(pk3.getInputStream(ze));
			out.addHighResolutionTexture(name, readGraphicObject(bytedata));
		}
		
		for (ZipEntry ze : pk3.getSprites())
		{
			String name = getLumpName(ze);
			byte[] bytedata = Common.getBinaryContents(pk3.getInputStream(ze));
			out.addSprite(name, readGraphicObject(bytedata));
		}
		
		for (ZipEntry ze : pk3.getFlats())
		{
			String name = getLumpName(ze);
			byte[] bytedata = Common.getBinaryContents(pk3.getInputStream(ze));
			out.addFlat(name,readFlatObject(bytedata));
		}

		for (ZipEntry ze : pk3.getColormaps())
		{
			String name = getLumpName(ze);
			byte[] bytedata = Common.getBinaryContents(pk3.getInputStream(ze));
			ColorMapLump c = new ColorMapLump();
			readObjectInfo(c, bytedata, gameType.getDefaultInternalFormat());
			out.addColormap(name, c);
		}
		
		for (ZipEntry ze : pk3.getSounds())
		{
			String name = getLumpName(ze);
			byte[] bytedata = Common.getBinaryContents(pk3.getInputStream(ze));
			RawData lump = new RawData();
			readObjectInfo(lump, bytedata, gameType.getDefaultInternalFormat());
			out.addSound(name, lump);
		}

		for (ZipEntry ze : pk3.getMusic())
		{
			String name = getLumpName(ze);
			byte[] bytedata = Common.getBinaryContents(pk3.getInputStream(ze));
			out.addMusic(name, readMusicInfo(bytedata));
		}
		
		for (ZipEntry ze : pk3.getACSLibraries())
		{
			String name = getLumpName(ze);
			byte[] bytedata = Common.getBinaryContents(pk3.getInputStream(ze));
			RawData lump = new RawData();
			readObjectInfo(lump, bytedata, gameType.getDefaultInternalFormat());
			out.addACSLibrary(name, lump);
		}
		
		for (ZipEntry ze : pk3.getVoices())
		{
			String name = getLumpName(ze);
			byte[] bytedata = Common.getBinaryContents(pk3.getInputStream(ze));
			RawData lump = new RawData();
			readObjectInfo(lump, bytedata, gameType.getDefaultInternalFormat());
			out.addVoice(name, lump);
		}
		
		for (ZipEntry ze : pk3.getMaps())
		{
			String name = getLumpName(ze);
			byte[] bytedata = Common.getBinaryContents(pk3.getInputStream(ze));

			File temp = null;
			try {
				temp = File.createTempFile("BRPK3IO", "wad");
				FileOutputStream fos = new FileOutputStream(temp);
				fos.write(bytedata);
				fos.close();
				DoomMap map = new DoomMap(new WadFile(temp), name);
				out.addMap(name, map);
			} catch (WadException e) {
				throw new WadException("Entry '"+ze.getName()+"' in "+pk3.getFileName()+" is NOT a Wad file.");
			} catch (IOException e) {
				// I know that if I do this "finally" will always execute. 
				// Could be wrong, and this may be unnecessary.
				throw e;	
				} finally {
				temp.delete();
			}
		}
		
		return out;
	}
	
	private static String getLumpName(ZipEntry ze)
	{
		String name = ze.getName().toLowerCase();
		int slashIndex = name.lastIndexOf('/');
		slashIndex = slashIndex == -1 ? name.lastIndexOf('\\') : slashIndex;
		int dotIndex = name.indexOf('.');
		if (dotIndex >= 0)
			name = name.substring(slashIndex+1, dotIndex);
		else
			name = name.substring(slashIndex+1);
		return name;
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
		ByteArrayInputStream in = new ByteArrayInputStream(b);
		switch (format)
		{
			case HEXEN:
			{
				if (obj instanceof HexenObject)
					((HexenObject)obj).readHexenBytes(in);
				else
					obj.readDoomBytes(in);
			}
			case STRIFE:
			{
				if (obj instanceof StrifeObject)
					((StrifeObject)obj).readStrifeBytes(in);
				else
					obj.readDoomBytes(in);
			}
			case DOOM:
			default:
				obj.readDoomBytes(in);
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
	

	
}
