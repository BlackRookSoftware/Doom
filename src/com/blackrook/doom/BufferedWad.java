/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom;

import com.blackrook.commons.list.SortedMap;
import com.blackrook.doom.struct.ColorMapLump;

/**
 * The class that serves as an abstract container for an entire Wad file.
 * This is an extremely heavyweight object.
 * <p>
 * Use of this class is recommended for reading/changing bulk Wad information and NOT small bits of data.
 * This class holds an entire Wad in memory and is useful for building whole Wad files.
 * <p>
 * This class's data is laid out in such a way that its export becomes a relatively smooth operation,
 * as Doom PK3s (read by ZDoom and associated ports) have this data organized explicitly and WADs use
 * entry header namespaces to define other data.
 * <p>
 * The entry names used in this structure are exported as-is to PK3s
 * (with ".lmp" added to names with no file extension) and if exporting 
 * to a Wad file, the names are truncated to 8 characters or the first 
 * non-alphanumeric, non-whitespace, non-underscore, non-square-bracket character.
 * <p>
 * Maps are exported as entries in Wads, and individual Wads in PK3s.
 * @author Matthew Tropiano
 * @deprecated Since 2.6.0 - there really is no reason to use this class.
 */
public class BufferedWad
{
	/** List of global, "other" lumps in this Wad. */
	private SortedMap<String, DoomObject> globalList;
	/** List of maps in this Wad. */
	private SortedMap<String, DoomMap> mapList;
	/** List of music lumps in this Wad. */
	private SortedMap<String, DoomObject> musicList;
	/** List of sound lumps in this Wad. */
	private SortedMap<String, DoomObject> soundList;
	/** List of patch lumps in this Wad. */
	private SortedMap<String, DoomGraphicObject> patchList;
	/** List of graphic lumps in this Wad. */
	private SortedMap<String, DoomGraphicObject> graphicList;
	/** List of flat lumps in this Wad. */
	private SortedMap<String, DoomGraphicObject> flatList;
	/** List of sprite lumps in this Wad. */
	private SortedMap<String, DoomGraphicObject> spriteList;
	/** List of texture lumps in this Wad. */
	private SortedMap<String, DoomGraphicObject> textureList;
	/** List of hi-res textures lumps in this Wad. */
	private SortedMap<String, DoomGraphicObject> hiresTexList;
	/** List of colormap lumps in this Wad. */
	private SortedMap<String, ColorMapLump> colormapList;
	/** List of ACS library lumps in this Wad. */
	private SortedMap<String, DoomObject> acsLibList;
	/** List of graphic lumps in this Wad. */
	private SortedMap<String, DoomObject> voiceList;

	/**
	 * Creates a new BufferedWad from nothing.
	 */
	public BufferedWad()
	{
		globalList = new SortedMap<String, DoomObject>();
		mapList = new SortedMap<String, DoomMap>();
		musicList = new SortedMap<String, DoomObject>();
		soundList = new SortedMap<String, DoomObject>();
		patchList = new SortedMap<String, DoomGraphicObject>();
		graphicList = new SortedMap<String, DoomGraphicObject>();
		flatList = new SortedMap<String, DoomGraphicObject>();
		spriteList = new SortedMap<String, DoomGraphicObject>();
		textureList = new SortedMap<String, DoomGraphicObject>();
		hiresTexList = new SortedMap<String, DoomGraphicObject>();
		colormapList = new SortedMap<String, ColorMapLump>();
		acsLibList = new SortedMap<String, DoomObject>();
		voiceList = new SortedMap<String, DoomObject>();
	}
	
	/**
	 * Adds a map to this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @param map		the map data.
	 */
	public void addMap(String entryName, DoomMap map)
	{
		mapList.replace(entryName, map);
	}
	
	/**
	 * Removes a map from this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @return	the Doom map mapped to that entry name, or null if no map exists by that name.
	 */
	public DoomMap removeMap(String entryName)
	{
		return mapList.remove(entryName);
	}
	
	/**
	 * Adds a global object to this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @param object	the object.
	 */
	public void addGlobal(String entryName, DoomObject object)
	{
		globalList.replace(entryName, object);
	}
	
	/**
	 * Removes a global object from this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @return	the Doom map mapped to that entry name, or null if no map exists by that name.
	 */
	public DoomObject removeGlobal(String entryName)
	{
		return globalList.remove(entryName);
	}
	
	/**
	 * Adds a music lump to this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @param object	the object.
	 */
	public void addMusic(String entryName, DoomObject object)
	{
		musicList.replace(entryName, object);
	}
	
	/**
	 * Removes a music lump from this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @return	the Doom map mapped to that entry name, or null if no map exists by that name.
	 */
	public DoomObject removeMusic(String entryName)
	{
		return musicList.remove(entryName);
	}
	
	/**
	 * Adds a sound lump to this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @param object	the object.
	 */
	public void addSound(String entryName, DoomObject object)
	{
		soundList.replace(entryName, object);
	}
	
	/**
	 * Removes a sound lump from this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @return	the Doom map mapped to that entry name, or null if no map exists by that name.
	 */
	public DoomObject removeSound(String entryName)
	{
		return soundList.remove(entryName);
	}
	
	/**
	 * Adds a graphic patch to this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @param object	the object.
	 */
	public void addPatch(String entryName, DoomGraphicObject object)
	{
		patchList.replace(entryName, object);
	}
	
	/**
	 * Removes a graphic patch from this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @return	the Doom map mapped to that entry name, or null if no map exists by that name.
	 */
	public DoomGraphicObject removePatch(String entryName)
	{
		return patchList.remove(entryName);
	}
	
	/**
	 * Adds a flat texture to this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @param object	the object.
	 */
	public void addFlat(String entryName, DoomGraphicObject object)
	{
		flatList.replace(entryName, object);
	}
	
	/**
	 * Removes a flat texture from this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @return	the Doom map mapped to that entry name, or null if no map exists by that name.
	 */
	public DoomGraphicObject removeFlat(String entryName)
	{
		return flatList.remove(entryName);
	}
	
	/**
	 * Adds a sprite patch to this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @param object	the object.
	 */
	public void addSprite(String entryName, DoomGraphicObject object)
	{
		spriteList.replace(entryName, object);
	}
	
	/**
	 * Removes a sprite patch from this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @return	the Doom map mapped to that entry name, or null if no map exists by that name.
	 */
	public DoomGraphicObject removeSprite(String entryName)
	{
		return spriteList.remove(entryName);
	}
	
	/**
	 * Adds a global graphic (like fonts or menu graphics or other non-world graphics) to this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @param object	the object.
	 */
	public void addGraphic(String entryName, DoomGraphicObject object)
	{
		graphicList.replace(entryName, object);
	}
	
	/**
	 * Removes a global graphic (like fonts or menu graphics or other non-world graphics) from this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @return	the Doom map mapped to that entry name, or null if no map exists by that name.
	 */
	public DoomGraphicObject removeGraphic(String entryName)
	{
		return graphicList.remove(entryName);
	}
	
	/**
	 * Adds a full texture entry to this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @param object	the object.
	 */
	public void addTexture(String entryName, DoomGraphicObject object)
	{
		textureList.replace(entryName, object);
	}
	
	/**
	 * Removes a full texture entry from this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @return	the Doom map mapped to that entry name, or null if no map exists by that name.
	 */
	public DoomGraphicObject removeTexture(String entryName)
	{
		return textureList.remove(entryName);
	}
	
	/**
	 * Adds a high-resolution texture entry to this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @param object	the object.
	 */
	public void addHighResolutionTexture(String entryName, DoomGraphicObject object)
	{
		hiresTexList.replace(entryName, object);
	}
	
	/**
	 * Removes a high-resolution texture entry from this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @return	the Doom map mapped to that entry name, or null if no map exists by that name.
	 */
	public DoomGraphicObject removeHighResolutionTexture(String entryName)
	{
		return hiresTexList.remove(entryName);
	}
	
	/**
	 * Adds a colormap to this BufferedWad.
	 * This is for ADDITIONAL colormaps that are not the main lump "COLORMAP",
	 * which belongs in global.
	 * @param entryName	the name of the entry.
	 * @param object	the object.
	 */
	public void addColormap(String entryName, ColorMapLump object)
	{
		colormapList.replace(entryName, object);
	}
	
	/**
	 * Removes a colormap from this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @return	the Doom map mapped to that entry name, or null if no map exists by that name.
	 */
	public DoomObject removeColormap(String entryName)
	{
		return colormapList.remove(entryName);
	}
	
	/**
	 * Adds a global ACS Library to this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @param object	the object.
	 */
	public void addACSLibrary(String entryName, DoomObject object)
	{
		acsLibList.replace(entryName, object);
	}
	
	/**
	 * Removes a global ACS Library from this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @return	the Doom map mapped to that entry name, or null if no map exists by that name.
	 */
	public DoomObject removeACSLibrary(String entryName)
	{
		return acsLibList.remove(entryName);
	}
	
	/**
	 * Adds a voice entry to this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @param object	the object.
	 */
	public void addVoice(String entryName, DoomObject object)
	{
		voiceList.replace(entryName, object);
	}
	
	/**
	 * Removes a voice entry from this BufferedWad.
	 * @param entryName	the name of the entry.
	 * @return	the Doom map mapped to that entry name, or null if no map exists by that name.
	 */
	public DoomObject removeVoice(String entryName)
	{
		return voiceList.remove(entryName);
	}

	/**
	 * Returns a reference to the map list.
	 */
	public SortedMap<String, DoomMap> getMapList()
	{
		return mapList;
	}

	/**
	 * Returns a reference to the global entry list.
	 */
	public SortedMap<String, DoomObject> getGlobalList()
	{
		return globalList;
	}

	/**
	 * Returns a reference to the music list.
	 */
	public SortedMap<String, DoomObject> getMusicList()
	{
		return musicList;
	}

	/**
	 * Returns a reference to the sound list.
	 */
	public SortedMap<String, DoomObject> getSoundList()
	{
		return soundList;
	}

	/**
	 * Returns a reference to the patch list.
	 */
	public SortedMap<String, DoomGraphicObject> getPatchList()
	{
		return patchList;
	}

	/**
	 * Returns a reference to the global graphic (non-sprite, non-texture patch) list.
	 */
	public SortedMap<String, DoomGraphicObject> getGraphicList()
	{
		return graphicList;
	}

	/**
	 * Returns a reference to the flat list.
	 */
	public SortedMap<String, DoomGraphicObject> getFlatList()
	{
		return flatList;
	}

	/**
	 * Returns a reference to the sprite list.
	 */
	public SortedMap<String, DoomGraphicObject> getSpriteList()
	{
		return spriteList;
	}

	/**
	 * Returns a reference to the texture list.
	 */
	public SortedMap<String, DoomGraphicObject> getTextureList()
	{
		return textureList;
	}

	/**
	 * Returns a reference to the hi-res texture list.
	 */
	public SortedMap<String, DoomGraphicObject> getHighResolutionTextureList()
	{
		return hiresTexList;
	}

	/**
	 * Returns a reference to the colormap list.
	 */
	public SortedMap<String, ColorMapLump> getColormapList()
	{
		return colormapList;
	}
	
	/**
	 * Returns a reference to the ACS Library list.
	 */
	public SortedMap<String, DoomObject> getACSLibraryList()
	{
		return acsLibList;
	}

	/**
	 * Returns a reference to the voice list.
	 */
	public SortedMap<String, DoomObject> getVoiceList()
	{
		return voiceList;
	}
	
	
}
