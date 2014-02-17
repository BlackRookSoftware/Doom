/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.blackrook.commons.Common;
import com.blackrook.commons.comparators.CaseInsensitiveComparator;
import com.blackrook.commons.hash.CaseInsensitiveHashedHashMap;
import com.blackrook.commons.hash.Hash;
import com.blackrook.commons.linkedlist.Queue;
import com.blackrook.commons.list.ComparatorList;
import com.blackrook.commons.list.List;
import com.blackrook.doom.DoomWad;
import com.blackrook.doom.WadException;
import com.blackrook.doom.struct.PatchNameLump;
import com.blackrook.doom.struct.Texture;
import com.blackrook.doom.struct.TextureLump;

/**
 * A helper class for reading/deciphering the ridiculous
 * TEXTUREx and PNAMES setup that Doom Texture definitions use.
 * Added textures are added to the texture lump at the end of the
 * internal list.
 * @author Matthew Tropiano
 * @since 2.7.0
 */
public class TextureSet
{
	/** The list of textures in this set, sorted. */
	private ComparatorList<String> textureNameList;
	/** A map of patches to textures. */
	private CaseInsensitiveHashedHashMap<String> patchToTextureList;
	
	/** The texture listing lumps. */
	private List<TextureLump> textureLumps;
	/** The PNAMES lump. */
	private PatchNameLump patchNames;
	
	/**
	 * Creates a new, empty TextureSet.
	 * @since 2.10.2
	 */
	public TextureSet()
	{
		this(new PatchNameLump(), new TextureLump());
	}
	
	/**
	 * Creates a new TextureSet using a Patch Name lump and a series of
	 * Texture Lumps.
	 * @param pnames the patch name lump.
	 * @param textureLumps the list of texture lumps.
	 */
	public TextureSet(PatchNameLump pnames, TextureLump ... textureLumps)
	{
		this.textureNameList = new ComparatorList<String>(CaseInsensitiveComparator.getInstance(), 100);
		this.patchToTextureList = new CaseInsensitiveHashedHashMap<String>(100);
		this.textureLumps = new List<TextureLump>(4);
		for (TextureLump tl : textureLumps)
			this.textureLumps.add(tl);
		this.patchNames = pnames;
		refreshMeta();
	}
	
	/**
	 * Creates a new TextureSet using a Patch Name lump and a series of
	 * Texture Lumps found in a WadFile.
	 * @param wad the {@link DoomWad} to read for the texture lump information.
	 * @throws WadException if the texture information could not be found.
	 * @throws IOException if the texture information could not be read.
	 */
	public TextureSet(DoomWad wad) throws WadException, IOException
	{
		this.textureNameList = new ComparatorList<String>(CaseInsensitiveComparator.getInstance(), 100);
		this.patchToTextureList = new CaseInsensitiveHashedHashMap<String>(100);
		this.textureLumps = new List<TextureLump>(2);
		
		boolean tex = wad.contains("texture1");
		boolean pn = wad.contains("pnames");
		
		if (!tex && !pn)
			throw new WadException("WAD does not contain texture data.");
		if (tex && !pn)
			throw new WadException("WAD does not contain PNAMES entry.");
		if (!tex && pn)
			throw new WadException("WAD does not contain TEXTUREx entry.");
		
		TextureLump tl = null;
		byte[] data = null;
		
		data = wad.getData("texture1");
		
		boolean strife = DoomUtil.isStrifeTextureData(data);
		
		tl = new TextureLump();
		if (strife)
			tl.readStrifeBytes(new ByteArrayInputStream(data));
		else
			tl.readDoomBytes(new ByteArrayInputStream(data));
		textureLumps.add(tl);

		
		data = wad.getData("texture2");
		if (data != null)
		{
			tl = new TextureLump();
			if (strife)
				tl.readStrifeBytes(new ByteArrayInputStream(data));
			else
				tl.readDoomBytes(new ByteArrayInputStream(data));
			textureLumps.add(tl);
		}
		
		data = wad.getData("pnames");
		patchNames = new PatchNameLump();
		patchNames.readDoomBytes(new ByteArrayInputStream(data));
		refreshMeta();
	}

	/**
	 * Checks an entry for a texture exists.
	 * @param textureName the texture name to search for.
	 * @return true if it exists, false otherwise.
	 */
	public boolean contains(String textureName)
	{
		TextureLump lump = getLump(textureName); 
		return lump != null;
	}
	
	/**
	 * Returns an entry for a texture by name.
	 * @param textureName the texture name to search for.
	 * @return an Entry with the composite information, or null if the texture could not be found.
	 */
	public Entry getEntry(String textureName)
	{
		TextureLump lump = getLump(textureName); 
		if (lump == null)
			return null;
		
		Texture texture = lump.getByName(textureName);
		return textureToEntry(texture);
	}
	
	/**
	 * Adds an entry for a texture by name.
	 * @param entry the entry to add.
	 */
	public void addEntry(Entry entry)
	{
		// add to patch names.
		addPatchesInEntry(entry);
		
		TextureLump lump = getLump(entry.getName()); 
		if (lump != null)
		{
			int index = lump.getIndexUsingKey(entry.getName());

			Texture t = lump.removeIndex(index);
			for (Texture.Patch p : t.getPatchList())
			{
				String patchName = patchNames.getByIndex(p.getPatchIndex());
				if (patchName != null)
					patchToTextureList.removeValue(patchName, t.getName());
			}

			lump.add(index, entryToTexture(entry));
			for (Entry.Patch p : entry.patches)
				patchToTextureList.add(p.getName(), entry.getName());
		}
		else
		{
			lump = textureLumps.getByIndex(textureLumps.size() - 1);
			lump.add(entryToTexture(entry));
			textureNameList.add(entry.getName());
			for (Entry.Patch p : entry.patches)
				patchToTextureList.add(p.getName(), entry.getName());
		}
	}

	/**
	 * Returns a sequence of texture names. Order and list of entries
	 * are dependent on the alphabetical order of all of the textures
	 * in this set.
	 * @param firstName the first texture name in the sequence. 
	 * @param lastName the last texture name in the sequence.
	 * @return an array of all of the textures in the sequence, including
	 * 		the provided textures, or null, if either texture does not exist.
	 * @since 2.7.2 
	 */
	public String[] getSequence(String firstName, String lastName)
	{
		Queue<String> out = new Queue<String>();
		int index = textureNameList.getIndexOf(firstName);
		if (index >= 0)
		{
			int index2 = textureNameList.getIndexOf(lastName);
			if (index2 >= 0)
			{
				int min = Math.min(index, index2);
				int max = Math.max(index, index2);
				for (int i = min; i <= max; i++)
					out.add(textureNameList.getByIndex(i));
			}
			else
				return null;
		}
		else
			return null;
		
		String[] outList = new String[out.size()];
		out.toArray(outList);
		return outList;
	}
	
	/**
	 * Returns the list of textures that a patch belongs to.
	 * @param patchName the name of the patch to find.
	 * @return the list of texture names that use the patch, or null if the
	 * 		patch cannot be found.
	 * @since 2.7.2
	 */
	public String[] getTexturesByPatch(String patchName)
	{
		Hash<String> out = patchToTextureList.get(patchName);
		if (out == null)
			return null;
		
		Queue<String> outQueue = new Queue<String>();
		for (String s : out)
			outQueue.add(s);
		String[] outList = new String[outQueue.size()];
		outQueue.toArray(outList);
		return outList;
	}
	
	/**
	 * Returns the texture lumps in this set.
	 * It is strongly advised that this object not be altered outside of this class.
	 */
	public List<TextureLump> getTextureLumps()
	{
		return textureLumps;
	}

	/**
	 * Returns the patch names lump in this set.
	 * It is strongly advised that this object not be altered outside of this class.
	 */
	public PatchNameLump getPatchNames()
	{
		return patchNames;
	}
	
	/**
	 * Sorts the texture lumps in this set.
	 */
	public void sort()
	{
		for (TextureLump lump : textureLumps)
			lump.sort();
	}
	
	/**
	 * Refreshes the contents of the sorted texture list and
	 * patch map.
	 * @since 2.7.2
	 */
	protected void refreshMeta()
	{
		textureNameList.clear();
		patchToTextureList.clear();
		for (TextureLump tl : textureLumps)
		{
			for (Texture t : tl)
			{
				textureNameList.add(t.getName());
				for (Texture.Patch p : t.getPatchList())
				{
					String patchName = patchNames.getByIndex(p.getPatchIndex());
					patchToTextureList.add(patchName, t.getName());
				}
			}
		}
	}

	/**
	 * Factory method for creating an entry object.
	 * @param textureName the name of the texture to create.
	 * @return a new entry.
	 * @throws IllegalArgumentException if textureName is null or empty.
	 */
	public static Entry createEntry(String textureName)
	{
		if (Common.isEmpty(textureName))
			throw new IllegalArgumentException("texture name cannot be empty.");
		
		Entry out = new Entry();
		out.name = textureName;
		return out;
	}

	/**
	 * Gets the lump that houses a texture.
	 * @param textureName the name of the texture to create.
	 * @return the lump that contains the texture or null if not found.
	 */
	private TextureLump getLump(String textureName)
	{
		TextureLump lump = null;
		for (TextureLump tl : textureLumps)
		{
			if (tl.containsKey(textureName))
			{
				lump = tl;
				break;
			}
		}
		
		return lump;
	}

	/**
	 * Adds patch names that don't exist.
	 */
	private void addPatchesInEntry(Entry entry)
	{
		for (Entry.Patch patch : entry.patches)
		{
			if (!patchNames.containsKey(patch.getName()))
				patchNames.add(patch.getName());
		}
	}
	
	/**
	 * Texture to Entry.
	 */
	private Entry textureToEntry(Texture texture)
	{
		Entry entry = new Entry();
		entry.name = texture.getName();
		entry.width = texture.getWidth();
		entry.height = texture.getHeight();
		
		for (Texture.Patch p : texture.getPatchList())
		{
			Entry.Patch patch = new Entry.Patch();
			patch.originX = p.getOriginX();
			patch.originY = p.getOriginY();
			patch.name = patchNames.getByIndex(p.getPatchIndex());
			entry.patches.add(patch);
		}
		
		return entry;
	}
	
	/**
	 * Entry to Texture.
	 */
	private Texture entryToTexture(Entry entry)
	{
		Texture texture = new Texture();
		texture.setName(entry.getName());
		texture.setWidth(entry.getWidth());
		texture.setHeight(entry.getHeight());
		
		for (Entry.Patch p : entry.patches)
		{
			Texture.Patch patch = new Texture.Patch();
			patch.setOriginX(p.getOriginX());
			patch.setOriginY(p.getOriginY());
			patch.setPatchIndex(patchNames.getIndex(p.getName()));
			texture.addPatch(patch);
		}
		
		return texture;
	}
	
	/**
	 * A class that represents a single composite Texture entry.
	 */
	public static class Entry
	{
		/**
		 * Texture patch.
		 */
		public static class Patch
		{
			/** Patch name. */
			private String name;
			/** Offset X. */
			private int originX;
			/** Offset Y. */
			private int originY;
			
			private Patch()
			{
				name = "";
				originX = 0;
				originY = 0;
			}
			
			/** Returns the patch name. */
			public String getName()
			{
				return name;
			}

			/** Sets the patch name. */
			public void setName(String name)
			{
				this.name = name;
			}
			
			/** Returns the patch offset X. */
			public int getOriginX()
			{
				return originX;
			}
			
			/** Sets the patch offset X. */
			public void setOriginX(int originX)
			{
				this.originX = originX;
			}
			
			/** Returns the patch offset Y. */
			public int getOriginY()
			{
				return originY;
			}
			
			/** Sets the patch offset Y. */
			public void setOriginY(int originY)
			{
				this.originY = originY;
			}
			
		}
		
		/** Texture name. */
		private String name;
		/** Texture width. */
		private int width;
		/** Texture height. */
		private int height;
		
		/** Patch entry. */
		private List<Entry.Patch> patches;
		
		private Entry()
		{
			name = null;
			width = 0;
			height = 0;
			patches = new List<Entry.Patch>();
		}
		
		/** 
		 * Returns the texture entry name. 
		 */
		public String getName()
		{
			return name;
		}
		
		/**
		 * Sets the texture entry name.
		 */
		public void setName(String name)
		{
			this.name = name;
		}
		
		/**
		 * Returns the width of the texture in pixels.
		 */
		public int getWidth()
		{
			return width;
		}
		
		/**
		 * Sets the width of the texture in pixels.
		 */
		public void setWidth(int width)
		{
			this.width = width;
		}
		
		/**
		 * Returns the height of the texture in pixels.
		 */
		public int getHeight()
		{
			return height;
		}
		
		/**
		 * Sets the height of the texture in pixels.
		 */
		public void setHeight(int height)
		{
			this.height = height;
		}
		
		/**
		 * Adds a patch to this entry.
		 */
		public void addPatch(String name, int originX, int originY)
		{
			if (Common.isEmpty(name))
				throw new IllegalArgumentException("patch name cannot be empty.");

			Patch p = new Patch();
			p.name = name;
			p.originX = originX;
			p.originY = originY;
			patches.add(p);
		}
		
		/**
		 * Removes a patch at a particular index.
		 */
		public Patch removePatch(int index)
		{
			return patches.removeIndex(index);
		}

		/**
		 * Returns a patch at a particular index.
		 */
		public Patch getPatch(int index)
		{
			return patches.getByIndex(index);
		}
		
		/**
		 * Shifts the ordering of a patch
		 */
		public void shiftPatch(int index, int newIndex)
		{
			patches.shift(index, newIndex);
		}
		
		/**
		 * Returns how many patches are on this texture entry.
		 */
		public int getPatchCount()
		{
			return patches.size();
		}
		
	}
}
