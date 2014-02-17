/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import com.blackrook.commons.Common;
import com.blackrook.commons.hash.CaseInsensitiveHash;
import com.blackrook.commons.hash.CountMap;
import com.blackrook.commons.list.List;
import com.blackrook.commons.math.RMath;
import com.blackrook.doom.DoomGraphicObject;
import com.blackrook.doom.DoomMap;
import com.blackrook.doom.DoomPK3;
import com.blackrook.doom.DoomWad;
import com.blackrook.doom.DoomWadEntry;
import com.blackrook.doom.WadBuffer;
import com.blackrook.doom.enums.GameType;
import com.blackrook.doom.enums.SourcePortType;
import com.blackrook.doom.struct.ColorMap;
import com.blackrook.doom.struct.EndDoom;
import com.blackrook.doom.struct.Linedef;
import com.blackrook.doom.struct.Palette;
import com.blackrook.doom.struct.Sector;
import com.blackrook.doom.struct.Sidedef;
import com.blackrook.doom.struct.Texture;
import com.blackrook.doom.struct.TextureLump;
import com.blackrook.doom.udmf.UDMFReader;
import com.blackrook.doom.udmf.UDMFStruct;
import com.blackrook.doom.udmf.UDMFTable;
import com.blackrook.doom.udmf.UDMFUtil;
import com.blackrook.doom.udmf.namespace.UDMFNamespace;
import com.blackrook.io.SuperReader;

/**
* Holds a bunch of constants and utility methods 
* used by the DoomStruct classes or end-users.
* @author Matthew Tropiano
*/
public final class DoomUtil implements DoomUtilTables
{
	/** Default transparent color RGBA (0,0,0,0). */
	public static final Color COLOR_BLANK = new Color(0, 0, 0, 0);
	/** Alternate transparent color commonly used in editors and stuff, RGBA (0,255,255,255) ("cyan"). */
	public static final Color COLOR_CYAN = new Color(0, 255, 255, 255);
	/** The size of a Wad entry name in bytes. */
	public static final int ENTRY_NAME_SIZE = 8;
	/** Blank texture name. */
	public static final String BLANK_TEXTURE = "-";
	
	private static final Color[] ANSI_COLORS = {
		new Color(0,0,0),		//black
		new Color(0,0,171),		//blue
		new Color(0,171,0),		//green
		new Color(0,153,153),	//cyan
		new Color(171,0,0),		//red
		new Color(153,0,153), 	//magenta
		new Color(153,102,0),	//brown
		new Color(171,171,171),	//light gray
		new Color(84,84,84),	//dark gray
		new Color(102,102,255),	//light blue
		new Color(102,255,102),	//light green
		new Color(102,255,255),	//light cyan
		new Color(255,102,102),	//light red
		new Color(255,102,255),	//light magenta
		new Color(255,255,102),	//yellow
		new Color(255,255,255)	//white
};
	
	private DoomUtil() {}
	
	/**
	 * Coerces a String into a Doom-standard 8-character format.
	 * Stops at first non-alphanumeric, non-whitespace, non-square-bracket, non-underscore character.
	 * Carats (^) are converted to backslashes. 
	 * <ul>
	 * <li>"Music File.wav" would become "MUSIC"</li>
	 * <li>"d_runnin.mus" would become "D_RUNNIN"</li>
	 * <li>"vile^1" would become "VILE\1"</li>
	 * </ul>
	 * @param s the input string.
	 * @return	a formatted string.
	 */
	public static String coerceToEntrySize(String s)
	{
		return coerceToEntry(s).trim();
	}
	
	/**
	 * Coerces a String into a Doom-standard 8-character format.
	 * Stops at first non-alphanumeric, non-whitespace, non-square-bracket, non-underscore character.
	 * Carats (^) are converted to backslashes. 
	 * <ul>
	 * <li>"Music File.wav" would become "MUSIC" with three null characters.</li>
	 * <li>"d_runnin.mus" would become "D_RUNNIN"</li>
	 * <li>"vile^1" would become "VILE\1" with two null characters.</li>
	 * </ul>
	 * @param s the input string.
	 * @return	a formatted string WITH TRAILING NULLS.
	 */
	public static String coerceToEntry(String s)
	{
		char[] out = new char[ENTRY_NAME_SIZE];
		char[] sc = s.trim().toUpperCase().toCharArray();
		int i = 0, c = 0;
		while (c < sc.length && validEntryChar(sc[c]) && i < ENTRY_NAME_SIZE)
		{
			if (sc[c] == '^')
				sc[c] = '\\';
			out[i++] = sc[c];
			c++;
		}
		return new String(out);
	}

	private static boolean validEntryChar(char ch)
	{
		return ch == '^' || ch == '[' || ch == ']' || ch == '\\' ||
			ch == '_' || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '-';
	}
	
	/**
	 * Cuts a string at the first null character 
	 * or non-alphanumeric/underscore character.
	 */
	public static String nameFix(String s)
	{
		int n = s.indexOf('\0');
		return n >= 0 ? s.substring(0, n).toUpperCase() : s.toUpperCase();
	}
	
	/**
	 * Returns a list of "sequenced" names given a start name and an end name.
	 * The names are assumed to contain a numeric value, used for defining the
	 * sequence. The input names are included in the result. Bad sequences just return
	 * the input strings.
	 * <p>
	 * For example, <code>getSequenceNames("SLIME01", "SLIME04")</code> would return
	 * <code>{"SLIME01", "SLIME02", "SLIME03", "SLIME04"}</code>.
	 * @param start the starting name in the sequence.
	 * @param end the ending name on the sequence.
	 * @return a set of names that are the result of the input sequence.
	 * @since 2.7.1
	 */
	public static String[] getSequenceNames(String start, String end)
	{
		StringBuilder sbPrefix = new StringBuilder();
		StringBuilder sbNumber = new StringBuilder();
		StringBuilder sbSuffix = new StringBuilder();
		int digits = 0;
		
		final int STATE_PREFIX = 0;
		final int STATE_NUMBER = 1;
		final int STATE_SUFFIX = 2;
		int state = STATE_PREFIX;
		int i = 0;
		
		while (i < start.length())
		{
			char c = start.charAt(i);
			
			switch (state)
			{
				case STATE_PREFIX:
					if (Character.isDigit(c))
						state = STATE_NUMBER;
					else
					{
						sbPrefix.append(c);
						i++;
					}
					break;
				case STATE_NUMBER:
					if (Character.isDigit(c))
					{
						sbNumber.append(c);
						digits++;
						i++;
					}
					else
						state = STATE_SUFFIX;
					break;
				case STATE_SUFFIX:
					sbSuffix.append(c);
					i++;
					break;
			}
		}
		
		if (state == STATE_PREFIX)
			return new String[]{start, end};
		
		int startNumber = Integer.parseInt(sbNumber.toString());
		String prefix = sbPrefix.toString(); 
		String suffix = sbSuffix.toString();
		
		boolean endGood = end.toLowerCase().startsWith(prefix.toLowerCase())
				&& end.toLowerCase().endsWith(suffix.toLowerCase());
		
		if (!endGood)
			return new String[]{start, end};

		int endNumber = startNumber;
		try {
			endNumber = Integer.parseInt(end.substring(prefix.length(), prefix.length() + digits), 10);
		} catch (NumberFormatException e) {
			return new String[]{start, end};
		}
		
		int min = Math.min(startNumber, endNumber);
		int max = Math.max(startNumber, endNumber);
		
		String[] out = new String[(max - min) + 1];
		String fmtString = prefix + "%0" + digits + "d" + suffix;
		for (int n = min; n <= max; n++)
			out[n - min] = String.format(fmtString, n);

		return out;
	}
	
	/**
	 * Finds all entries within a WAD entry namespace.
	 * A namespace is marked by one or two characters and "_START" or "_END" as a suffix.
	 * All entries in between are considered part of the "namespace."
	 * <p>
	 * The returned entries are valid only to the provided WAD. Using entry information with unassociated WADs
	 * could create undesired results.
	 * @param prefix the namespace prefix to use (e.g. "f" or "ff" for flats, "p" or "pp" for textures, etc.).
	 * @param wad the WAD file to scan.
	 * @return an array of all entries in the namespace, or an empty array if none are found.
	 * @since 2.8.2
	 */
	public static DoomWadEntry[] getEntriesInNamespace(String prefix, DoomWad wad)
	{
		return getEntriesInNamespace(prefix, null, wad);
	}
	
	/**
	 * Finds all entries within a WAD entry namespace.
	 * A namespace is marked by one or two characters and "_START" or "_END" as a suffix.
	 * All entries in between are considered part of the "namespace."
	 * <p>
	 * The returned entries are valid only to the provided WAD. Using entry information with unassociated WADs
	 * could create undesired results.
	 * @param prefix the namespace prefix to use (e.g. "f" or "ff" for flats, "p" or "pp" for textures, etc.).
	 * @param wad the WAD file to scan.
	 * @param ignorePattern the regex pattern to use for deciding which entries in the namespace to ignore.
	 * @return an array of all entries in the namespace, or an empty array if none are found.
	 * @since 2.8.2
	 */
	public static DoomWadEntry[] getEntriesInNamespace(String prefix, Pattern ignorePattern, DoomWad wad)
	{
		List<DoomWadEntry> entryList = new List<DoomWadEntry>(100);
		
		int start = wad.getIndexOf(prefix+"_start");
		if (start > 0)
		{
			int end = wad.getIndexOf(prefix+"_end");
			if (end > 0)
			{
				for (int i = start + 1; i < end; i++)
				{
					DoomWadEntry entry = wad.getEntry(i);
					if (ignorePattern != null && ignorePattern.matcher(entry.getName()).matches())
						continue;
					entryList.add(entry);
				}
			}
		}
		
		DoomWadEntry[] entry = new DoomWadEntry[entryList.size()];
		entryList.toArray(entry);
		return entry;
	}
	
	/**
	 * Returns graphic data as an image that humans can look at.
	 * The transparent pixels are colored (0,0,0,0), RGBA.
	 * @param pal		the lookup palette to use for filling in the color information.
	 * @param graphic	the graphic information to convert.
	 * @return	a BufferedImage that represents the graphic image in RGB color (including transparency).
	 */
	public static BufferedImage getImageForGraphic(Palette pal, DoomGraphicObject graphic)
	{
		return getImageForGraphic(pal, null, graphic);
	}
	
	/**
	 * Returns graphic data as an image that humans can look at.
	 * The transparent pixels are colored (0,0,0,0), RGBA.
	 * This will use a ColorMap for looking up the correct palette index before pixel color is picked.
	 * If the ColorMap is <code>null</code>, it will not be used.
	 * @param pal		the lookup palette to use for filling in the color information.
	 * @param colormap	the color map to use for converting pixel information before the pixel is selected.
	 * @param graphic	the graphic information to convert.
	 * @return	a BufferedImage that represents the graphic image in RGB color (including transparency).
	 */
	public static BufferedImage getImageForGraphic(Palette pal, ColorMap colormap, DoomGraphicObject graphic)
	{
		return getImageForGraphic(pal, colormap, graphic, COLOR_BLANK);
	}
	
	/**
	 * Returns graphic data as an image that humans can look at.
	 * The transparent pixels are colored using a specific color.
	 * @param pal			the lookup palette to use for filling in the color information.
	 * @param graphic			the graphic information to convert.
	 * @param transparent	the color to use for transparent pixels.
	 * @return	a BufferedImage that represents the graphic image in RGB color (including transparency).
	 */
	public static BufferedImage getImageForGraphic(Palette pal, DoomGraphicObject graphic, Color transparent)
	{
		return getImageForGraphic(pal, null, graphic, transparent);
	}

	/**
	 * Returns graphic data as an image that humans can look at.
	 * The transparent pixels are colored using a specific color.
	 * This will use a ColorMap for looking up the correct palette index before pixel color is picked.
	 * If the ColorMap is <code>null</code>, it will not be used.
	 * @param pal			the lookup palette to use for filling in the color information.
	 * @param colormap		the color map to use for converting pixel information before the pixel is selected.
	 * @param graphic		the graphic information to convert.
	 * @param transparent	the color to use for transparent pixels.
	 * @return	a BufferedImage that represents the graphic image in RGB color (including transparency).
	 */
	public static BufferedImage getImageForGraphic(Palette pal, ColorMap colormap, DoomGraphicObject graphic, Color transparent)
	{
		BufferedImage out = new BufferedImage(
				graphic.getWidth(), graphic.getHeight(), BufferedImage.TYPE_INT_ARGB); 
		for (int x = 0; x < graphic.getWidth(); x++)
			for (int y = 0; y < graphic.getHeight(); y++)
				out.setRGB(x, y, graphic.getPixelColor(x, y, pal, colormap, transparent).getRGB());
		
		return out;
	}

	/**
	 * Returns the EndDoom data rendered to a BufferedImage.
	 * @param endoom	the EndDoom lump to render.
	 * @param blinking	if true, this will render the "blinking" characters.
	 * @return			a BufferedImage that represents the graphic image in RGB color (including transparency).
	 */
	public static BufferedImage getImageForEndDoom(EndDoom endoom, boolean blinking)
	{
		BufferedImage out = new BufferedImage(640, 300, BufferedImage.TYPE_INT_ARGB);
		Font font = new Font("Courier", Font.PLAIN, 13);
		char[] ch = new char[1];
		Graphics2D g = (Graphics2D)out.getGraphics();
		g.setFont(font);
		g.setColor(ANSI_COLORS[0]);
		g.fillRect(0, 0, 640, 300);
		
		for (int r = 0; r < 25; r++)
			for (int c = 0; c < 80; c++)
			{
				g.setColor(ANSI_COLORS[endoom.getBackgroundColor(r, c)]);
				g.fillRect(c*8, r*12, 8, 12);
			}
		
		for (int r = 24; r >= 0; r--)
			for (int c = 79; c >= 0; c--)
			{
				if (blinking || (!blinking && endoom.getBlinking(r, c)))
				{
					g.setColor(ANSI_COLORS[endoom.getForegroundColor(r, c)]);
					ch[0] = endoom.getCharAt(r, c);
					g.drawChars(ch, 0, 1, c*8, r*12+10);
				}
			}
		return out;
	}
	
	/**
	 * Converts a short angle to radians.
	 * @param angle	the short angle, usually from a BSP Segment.
	 */
	public static double shortAngleToRadians(short angle)
	{
		int a = (+angle) < 0 ? +angle + 65536 : angle;
		return (double)RMath.linearInterpolate(RMath.getInterpolationFactor(a, 0, 65535), 0, 2*Math.PI);
	}

	/**
	 * Returns the nearest color to a color in a specific palette.
	 * If a color is not opaque, the transparentColor will be returned.
	 * @param color		the color to match.
	 * @param palette	the palette to use. if null, a palette is not used to correct the color.
	 * @param colormap	the colormap to use for the index lookup, before palette lookup. if null, no map is used.
	 * @param transparentColor the transparency color to use if the color is transparent.
	 * @return		the nearest color.
	 */
	public static Color getNearestColor(Color color, Palette palette, ColorMap colormap, Color transparentColor)
	{
		if (palette == null)
			return color;
		int index = getNearestColorIndex(color, palette);
		return getIndexedColor(index, palette, colormap, transparentColor);
	}

	/**
	 * Returns the nearest color to a color in a specific palette.
	 * If a color is not opaque, the transparentColor will be returned.
	 * @param color		the color to match.
	 * @param palette	the palette to use.
	 * @return			the index of the palette color to use, or -1 if transparent.
	 */
	public static int getNearestColorIndex(Color color, Palette palette)
	{
		return palette.getNearestColorIndex(color);
	}
	
	/**
	 * Returns the color at a specific color index in a palette.
	 * @param index				the palette color index. if < 0, transparent.
	 * @param palette 			the palette to use for color lookup.
	 * @param transparentColor	the transparency color to use if the color index is less than 0.
	 * @return	the color.
	 */
	public static Color getIndexedColor(int index, Palette palette, Color transparentColor)
	{
		return getIndexedColor(index, palette, null, transparentColor);
	}
	
	/**
	 * Returns the color at a specific color index in a palette, but first filtered
	 * through a color map, if it is provided.
	 * @param index				the palette color index. if < 0, transparent.
	 * @param palette 			the palette to use for color lookup.
	 * @param colormap			the colormap to use for the index lookup, before palette lookup. if null, no map is used.
	 * @param transparentColor	the transparency color to use if the color index is less than 0.
	 * @return	the color.
	 */
	public static Color getIndexedColor(int index, Palette palette, ColorMap colormap, Color transparentColor)
	{
		if (colormap == null)
			return index >= 0 ? palette.getColor(index) : transparentColor;
		else
			return index >= 0 ? palette.getColor(colormap.getPaletteIndex(index)) : transparentColor;
	}
	
	/**
	 * Alters the color information of a buffered image so that its
	 * colorspace fits in a palette of your choice.
	 * NOTE: This can take a VERY LONG amount of time, depending on the size of the image.
	 * @param image 			the image to convert.
	 * @param palette 			the palette to use for color lookup.
	 * @param colormap			the colormap to use for the index lookup, before palette lookup. if null, no map is used.
	 * @param transparentColor	the transparency color to use if the color index is less than 0.
	 * @return	a new BufferedImage with its color information re-evaluated.
	 */
	public static BufferedImage convertToPalette(BufferedImage image, Palette palette, ColorMap colormap, Color transparentColor)
	{
		BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int x = 0; x < image.getWidth(); x++)
			for (int y = 0; y < image.getHeight(); y++)
			{
				Color c = Common.argbToColor(image.getRGB(x, y));
				out.setRGB(x, y, getNearestColor(c, palette, colormap, transparentColor).getRGB());
			}

		return out;
	}
	
	/**
	 * Tests for a name of a texture name that can exist 
	 * in all Doom commercial IWADs.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a texture.
	 */
	public static boolean isCommonDoomTextureName(String name)
	{
		return DOOM_COMMON_TEXTURES.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a texture name in a Doom commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a texture.
	 */
	public static boolean isDoomTextureName(String name)
	{
		return 
			DOOM_COMMON_TEXTURES.contains(name.toLowerCase()) ||
			DOOM_TEXTURES.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a texture name in a Doom commercial IWAD
	 * that only appears in Doom.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a texture.
	 */
	public static boolean isExclusiveDoomTextureName(String name)
	{
		return DOOM_TEXTURES.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a music lump in a Doom commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a music lump.
	 */
	public static boolean isDoomMusicLump(String name)
	{
		return DOOM_MUSIC.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a sound lump in a Doom commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a sound lump.
	 */
	public static boolean isDoomSoundLump(String name)
	{
		return DOOM_COMMON_SOUNDS.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a graphic lump in a Doom commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a graphic lump.
	 */
	public static boolean isDoomGraphicLump(String name)
	{
		return 
			DOOM_GRAPHICS.contains(name.toLowerCase()) ||
			DOOM_COMMON_GRAPHICS.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a graphic lump that exists 
	 * in all Doom commercial IWADs.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return	if this is the name of a graphic lump.
	 */
	public static boolean isCommonDoomGraphicLump(String name)
	{
		return DOOM_COMMON_GRAPHICS.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a graphic lump in a Doom commercial IWAD that
	 * is not found in other Doom-series IWADs.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return	if this is the name of a graphic lump.
	 */
	public static boolean isExclusiveDoomGraphicLump(String name)
	{
		return DOOM_GRAPHICS.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a flat lump in a Doom commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return	if this is the name of a flat lump.
	 */
	public static boolean isDoomFlatLump(String name)
	{
		return DOOM_COMMON_FLATS.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a flat lump in a Doom 2 commercial IWAD that
	 * is not found in other Doom-series IWADs.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return	if this is the name of a flat lump.
	 */
	public static boolean isExclusiveDoom2FlatLump(String name)
	{
		return DOOM2_FLATS.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a flat lump in a Doom 2 commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return	if this is the name of a flat lump.
	 */
	public static boolean isDoom2FlatLump(String name)
	{
		return
			DOOM_COMMON_FLATS.contains(name.toLowerCase()) ||
			DOOM2_FLATS.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a texture name in a Doom 2 commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return	if this is the name of a texture.
	 */
	public static boolean isDoom2TextureName(String name)
	{
		return 
			DOOM_COMMON_TEXTURES.contains(name.toLowerCase()) ||
			DOOM2_TEXTURES.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a texture name in a Doom 2 commercial IWAD
	 * that only appears in Doom 2.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a texture.
	 */
	public static boolean isExclusiveDoom2TextureName(String name)
	{
		return DOOM2_TEXTURES.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a texture name in a TNT: Evilution commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a texture.
	 */
	public static boolean isTNTTextureName(String name)
	{
		return 
			DOOM_COMMON_TEXTURES.contains(name.toLowerCase()) ||
			DOOM2_TEXTURES.contains(name.toLowerCase()) ||
			TNT_TEXTURES.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a texture name in a TNT: Evilution commercial IWAD
	 * that only appears in TNT.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a texture.
	 */
	public static boolean isExclusiveTNTTextureName(String name)
	{
		return TNT_TEXTURES.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a texture name in a Plutonia commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a texture.
	 */
	public static boolean isPlutoniaTextureName(String name)
	{
		return 
			DOOM_COMMON_TEXTURES.contains(name.toLowerCase()) ||
			DOOM2_TEXTURES.contains(name.toLowerCase()) ||
			PLUTONIA_TEXTURES.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a texture name in a Plutonia commercial IWAD
	 * that only appears in Plutonia.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a texture.
	 */
	public static boolean isExclusivePlutoniaTextureName(String name)
	{
		return PLUTONIA_TEXTURES.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a music lump in a Doom 2 commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a music lump.
	 */
	public static boolean isDoom2MusicLump(String name)
	{
		return DOOM2_MUSIC.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a sound lump in a Doom 2 commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a sound lump.
	 */
	public static boolean isDoom2SoundLump(String name)
	{
		return
			DOOM2_SOUNDS.contains(name.toLowerCase()) ||
			DOOM_COMMON_SOUNDS.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a graphic lump in a Doom 2 commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a graphic lump.
	 */
	public static boolean isDoom2GraphicLump(String name)
	{
		return 
			DOOM_COMMON_GRAPHICS.contains(name.toLowerCase()) ||
			DOOM2_GRAPHICS.contains(name.toLowerCase());
	}

	/**
	 * Tests for a name of a graphic lump in a Doom 2 commercial IWAD that
	 * is not found in other Doom-series IWADs.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a graphic lump.
	 */
	public static boolean isExclusiveDoom2GraphicLump(String name)
	{
		return DOOM2_GRAPHICS.contains(name.toLowerCase());
	}
	

	/**
	 * Tests for a name of a texture name in a Heretic commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a texture.
	 */
	public static boolean isHereticTextureName(String name)
	{
		return HERETIC_TEXTURES.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a flat name in a Heretic commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a flat.
	 */
	public static boolean isHereticFlatLump(String name)
	{
		return HERETIC_FLATS.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a music lump in a Heretic commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a music lump.
	 */
	public static boolean isHereticMusicLump(String name)
	{
		return HERETIC_MUSIC.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a sound lump in a Heretic commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a sound lump.
	 */
	public static boolean isHereticSoundLump(String name)
	{
		return HERETIC_SOUNDS.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a graphic lump in a Heretic commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a graphic lump.
	 */
	public static boolean isHereticGraphicLump(String name)
	{
		return HERETIC_GRAPHICS.contains(name.toLowerCase());
	}

	/**
	 * Tests for a name of a texture name in a Hexen commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a texture.
	 */
	public static boolean isHexenTextureName(String name)
	{
		return HEXEN_TEXTURES.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a flat name in a Hexen commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a flat.
	 */
	public static boolean isHexenFlatLump(String name)
	{
		return HEXEN_FLATS.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a music lump in a Hexen commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a music lump.
	 */
	public static boolean isHexenMusicLump(String name)
	{
		return HEXEN_MUSIC.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a sound lump in a Hexen commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a sound lump.
	 */
	public static boolean isHexenSoundLump(String name)
	{
		return HEXEN_SOUNDS.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a graphic lump in a Hexen commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a graphic lump.
	 */
	public static boolean isHexenGraphicLump(String name)
	{
		return HEXEN_GRAPHICS.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a texture name in a Strife commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a texture.
	 */
	public static boolean isStrifeTextureName(String name)
	{
		return STRIFE_TEXTURES.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a flat name in a Strife commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a flat.
	 */
	public static boolean isStrifeFlatLump(String name)
	{
		return STRIFE_FLATS.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a music lump in a Strife commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a music lump.
	 */
	public static boolean isStrifeMusicLump(String name)
	{
		return STRIFE_MUSIC.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a sound lump in a Strife commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a sound lump.
	 */
	public static boolean isStrifeSoundLump(String name)
	{
		return STRIFE_SOUNDS.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a graphic lump in a Strife commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a graphic lump.
	 */
	public static boolean isStrifeGraphicLump(String name)
	{
		return STRIFE_GRAPHICS.contains(name.toLowerCase());
	}

	/**
	 * Tests if this name is a name of a sound lump in a commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a sound lump.
	 */
	public static boolean isSoundLump(String name)
	{
		return
			isDoomSoundLump(name) ||
			isDoom2SoundLump(name) ||
			isHereticSoundLump(name) ||
			isHexenSoundLump(name) ||
			isStrifeSoundLump(name);
	}

	/**
	 * Tests if this name is a name of a music lump in a commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a music lump.
	 */
	public static boolean isMusicLump(String name)
	{
		return
			isDoomMusicLump(name) ||
			isDoom2MusicLump(name) ||
			isHereticMusicLump(name) ||
			isHexenMusicLump(name) ||
			isStrifeMusicLump(name);
	}
	
	/**
	 * Tests if this name is a name of a flat lump in a commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a flat lump.
	 */
	public static boolean isFlatLump(String name)
	{
		return
			isDoomFlatLump(name) ||
			isDoom2FlatLump(name) ||
			isHereticFlatLump(name) ||
			isHexenFlatLump(name) ||
			isStrifeFlatLump(name);
	}
	
	
	/**
	 * Tests for a name of a colormap in a commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a colormap lump.
	 */
	public static boolean isColormap(String name)
	{
		return COLORMAP_SPECIAL.contains(name.toLowerCase());
	}

	/**
	 * Tests for a name of a colormap lump (group) in a commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a colormap lump.
	 */
	public static boolean isColormapLump(String name)
	{
		return COLORMAP_LUMP_SPECIAL.contains(name.toLowerCase());
	}

	/**
	 * Tests for a name of a palette lump in a commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a palette lump.
	 */
	public static boolean isPaletteLump(String name)
	{
		return PLAYPAL_SPECIAL.contains(name.toLowerCase());
	}

	/**
	 * Tests for a name of a ENDOOM type lump in a commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a ENDOOM lump.
	 */
	public static boolean isEndoomLump(String name)
	{
		return ENDOOM_SPECIAL.contains(name.toLowerCase());
	}

	/**
	 * Tests for a name of a texture lump in a commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a texture lump.
	 */
	public static boolean isTextureLump(String name)
	{
		return TEXTURE_SPECIAL.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a patch name lump in a commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a patch name lump.
	 */
	public static boolean isPatchNamesLump(String name)
	{
		return PATCHNAMES_SPECIAL.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a text lump in a commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a text lump.
	 */
	public static boolean isTextLump(String name)
	{
		return TEXT_SPECIAL.contains(name.toLowerCase());
	}
	
	/**
	 * Tests for a name of a map data lump in a commercial IWAD.
	 * Used to intuit wad types and games.
	 * @param name	the lump name.
	 * @return		if this is the name of a map data lump.
	 */
	public static boolean isMapDataLump(String name)
	{
		return name.startsWith("gx_") || name.startsWith("script") || MAP_SPECIAL.contains(name.toLowerCase());
	}
	
	/**
	 * Scans through texture lump data in order to detect whether it is for Strife
	 * or not.
	 * @param b the texture lump data.
	 * @return true if it is Strife texture data, false if not.
	 */
	public static boolean isStrifeTextureData(byte[] b)
	{
		int ptr = 0;
		byte[] buf = new byte[4];

		System.arraycopy(b, ptr, buf, 0, 4);
		int textureCount = SuperReader.bytesToInt(buf, SuperReader.LITTLE_ENDIAN);
		ptr = (textureCount * 4) + 20;
		
		boolean good = true;
		while (ptr < b.length && good)
		{
			System.arraycopy(b, ptr, buf, 0, 4);
			
			// test for unused texture data.
			if (SuperReader.bytesToInt(buf, SuperReader.LITTLE_ENDIAN) != 0)
				good = false;

			// test for unused patch data.
			else
			{
				ptr += 4;
				System.arraycopy(b, ptr, buf, 0, 2);
				int patches = SuperReader.bytesToInt(buf, SuperReader.LITTLE_ENDIAN);
				ptr += 2;
				while (patches > 0)
				{
					ptr += 6;
					System.arraycopy(b, ptr, buf, 0, 4);
					int x = SuperReader.bytesToInt(buf, SuperReader.LITTLE_ENDIAN);
					if (x > 1 || x < 0)
						good = false;
					ptr += 4;
					patches--;
				}
				ptr += 16;
			}
		}
		
		return !good;
	}
	
	/**
	 * Attempts to figure out what game a particular wad belongs to.
	 * If nothing in the WAD is a tell-tale giveaway, or is too ambiguous,
	 * GameType.UNKNOWN is returned. This is still not 100% accurate, and will return the game type
	 * of most common compatibility (for instance if someone made a map for Final Doom,
	 * but it contained only Doom 2 textures, Doom 2 may be returned).
	 * @param wad	the Wad file to inspect.
	 */
	public static GameType intuitGameType(DoomWad wad)
	{
		GameTypeGuess guess = new GameTypeGuess();
		countMapReplacement(wad, guess);
		countOtherReplacement(wad, guess);
		countCheckInconsistencies(wad, guess);
		return getFinalGuess(guess);
	}

	/**
	 * Attempts to figure out what game a particular PK3 belongs to.
	 * If nothing in the WAD is a tell-tale giveaway, or is too ambiguous,
	 * GameType.UNKNOWN is returned. This is still not 100% accurate, and will return the game type
	 * of most common compatibility (for instance if someone made a map for Final Doom,
	 * but it contained only Doom 2 textures, Doom 2 may be returned).
	 * @param pk3	the PK3 file to inspect.
	 */
	public static GameType intuitGameType(DoomPK3 pk3)
	{
		GameTypeGuess guess = new GameTypeGuess();
		countMapReplacement(pk3, guess);
		countOtherReplacement(pk3, guess);
		countCheckInconsistencies(pk3, guess);
		return getFinalGuess(guess);
	}

	private static GameType getFinalGuess(GameTypeGuess guess)
	{
		GameType[] gt = null;
		CountMap<GameType> count = new CountMap<GameType>();
		for (int i = 0; i < 6; i++)
		{
			switch (i)
			{
				case 0: gt = getBestFrom(guess.mapTotal, guess.mapCount); break;
				case 1: gt = getBestFrom(guess.textureTotal, guess.textureCount); break;
				case 2: gt = getBestFrom(guess.flatTotal, guess.flatCount); break;
				case 3: gt = getBestFrom(guess.graphicsTotal, guess.graphicsCount); break;
				case 4: gt = getBestFrom(guess.musicTotal, guess.musicCount); break;
				case 5: gt = getBestFrom(guess.soundTotal, guess.soundCount); break;
			}
			if (gt.length > 0)
				for (GameType t : gt)
				count.give(t);
		}
		GameType[] out = getBestFrom(6, count); 
		return out.length > 0 ? out[0] : GameType.UNKNOWN;
	}
	
	private static GameType[] getBestFrom(int total, CountMap<GameType> typeCount)
	{
		List<GameType> best = new List<GameType>(3);
		int diff = Integer.MAX_VALUE;
		Iterator<GameType> it = typeCount.keyIterator();
		while (it.hasNext())
		{
			GameType type = it.next();
			int d = total - typeCount.getCount(type);
			if (d < diff)
			{
				diff = d;
				best.clear();
				best.add(type);
			}
			else if (d == diff)
				best.add(type);
		}
		GameType[] out = new GameType[best.size()];
		best.toArray(out);
		return out;
	}
	
	private static void countMapReplacement(DoomWad wf, GameTypeGuess guess)
	{
		int[] maps = DoomMap.getAllMapIndices(wf);
		for (int i = 0; i < maps.length; i++)
		{
			int n = maps[i];
			countMapReplacement(n, wf, guess);
		}
	}
	
	private static void countMapReplacement(int mapIndex, DoomWad wf, GameTypeGuess guess)
	{
		DoomWadEntry entry = wf.getEntry(mapIndex);
		countMapEntry(entry.getName(), guess);
		
		List<Sidedef> sidedefs = new List<Sidedef>();
		
		try {
			guessGetSidedefs(wf, mapIndex, sidedefs);
			for (Sidedef side : sidedefs)
				countSidedef(side, guess);
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<Sector> sectors = new List<Sector>();
		
		try {
			guessGetSectors(wf, mapIndex, sectors);
			for (Sector side : sectors)
				countSector(side, guess);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void countMapReplacement(DoomPK3 pk3, GameTypeGuess guess)
	{
		ZipEntry[] maps = pk3.getMaps();
		for (int i = 0; i < maps.length; i++)
		{
			ZipEntry entry = maps[i];
			try {
				countMapEntry(DoomPK3.getEntryName(entry), guess);
				DoomWad wf = new WadBuffer(pk3.getDataAsStream(entry));
				List<Sidedef> sidedefs = new List<Sidedef>();
				guessGetSidedefs(wf, 0, sidedefs);
				for (Sidedef side : sidedefs)
					countSidedef(side, guess);
				List<Sector> sectors = new List<Sector>();
				guessGetSectors(wf, 0, sectors);
				for (Sector sect : sectors)
					countSector(sect, guess);
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}

	private static void countCheckInconsistencies(DoomWad wad, GameTypeGuess guess)
	{
		// check if textures in a texture replacement are a base for either Plutonia or TNT
		CaseInsensitiveHash hash = new CaseInsensitiveHash(200);
		countReadTextures(wad, hash);
		boolean plutoniaTest = true;
		boolean tntTest = true;
		for (String tex : PLUTONIA_TEXTURES)
			if (!hash.contains(tex))
			{
				plutoniaTest = false;
				break;
			}

		for (String tex : TNT_TEXTURES)
			if (!hash.contains(tex))
			{
				tntTest = false;
				break;
			}
		
		if (!plutoniaTest)
			guess.textureCount.takeAll(GameType.PLUTONIA);
		if (!tntTest)
			guess.textureCount.takeAll(GameType.TNT);
		
	}
	
	private static void countReadTextures(DoomWad wad, CaseInsensitiveHash hash)
	{
		TextureLump textureLump = null;
		try{
			textureLump = new TextureLump();

			byte[] b = wad.getData("texture1");
			if (b != null)
			{
				if (isStrifeTextureData(b))
					textureLump.readStrifeBytes(new ByteArrayInputStream(b));
				else
					textureLump.readDoomBytes(new ByteArrayInputStream(b));
				
				for (Texture t : textureLump)
					hash.put(t.getName());
			}

			b = wad.getData("texture2");
			if (b != null)
			{
				if (isStrifeTextureData(b))
					textureLump.readStrifeBytes(new ByteArrayInputStream(b));
				else
					textureLump.readDoomBytes(new ByteArrayInputStream(b));
				
				for (Texture t : textureLump)
					hash.put(t.getName());
			}
			
		} catch (IOException e) {}
	}
	
	private static void countCheckInconsistencies(DoomPK3 pk3, GameTypeGuess guess)
	{
		// check for replacement inconsistencies.
		if (guess.textureCount.getCount(GameType.PLUTONIA) > 0 && guess.textureCount.getCount(GameType.TNT) > 0)
		{
			// can't be Plutonia or TNT as a base if it contains both of those textures.
			guess.textureCount.takeAll(GameType.PLUTONIA);
			guess.textureCount.takeAll(GameType.TNT);
		}
	}
	
	private static void countOtherReplacement(DoomWad wad, GameTypeGuess guess)
	{
		for (DoomWadEntry entry : wad.getAllEntries())
		{
			countGraphic(entry.getName(), guess);
			countMusicName(entry.getName(), guess);
			countSoundName(entry.getName(), guess);
		}
	}

	private static void countOtherReplacement(DoomPK3 pk3, GameTypeGuess guess)
	{
		for (ZipEntry entry : pk3.getGraphics())
			countGraphic(DoomPK3.getEntryName(entry), guess);
		for (ZipEntry entry : pk3.getMusic())
			countMusicName(DoomPK3.getEntryName(entry), guess);
		for (ZipEntry entry : pk3.getSounds())
			countSoundName(DoomPK3.getEntryName(entry), guess);
	}

	private static void countMapEntry(String entry, GameTypeGuess guess)
	{
		guess.mapTotal++;
		
		if (DOOM_MAPS.contains(entry))
			guess.mapCount.give(GameType.DOOM);

		if (DOOM2_MAPS.contains(entry))
		{
			guess.mapCount.give(GameType.DOOM2);
			guess.mapCount.give(GameType.PLUTONIA);
			guess.mapCount.give(GameType.TNT);
		}
		
		if (HERETIC_MAPS.contains(entry))
			guess.mapCount.give(GameType.HERETIC);
		
		if (HEXEN_MAPS.contains(entry))
			guess.mapCount.give(GameType.HEXEN);
		
		if (STRIFE_MAPS.contains(entry))
			guess.mapCount.give(GameType.STRIFE);
	}

	// Return true: stop search. False, keep going.
	private static void countSidedef(Sidedef side, GameTypeGuess guess)
	{
		countTextureName(side.getUpperTexture(), guess);
		countTextureName(side.getMiddleTexture(), guess);
		countTextureName(side.getLowerTexture(), guess);
	}

	// Return true: stop search. False, keep going.
	private static void countSector(Sector sector, GameTypeGuess guess)
	{
		countFlatName(sector.getCeilingTexture(), guess);
		countFlatName(sector.getFloorTexture(), guess);
	}

	private static void countTextureName(String textureName, GameTypeGuess guess)
	{
		if (!textureName.equals(BLANK_TEXTURE))
		{
			guess.textureTotal++;
			
			if (isDoomTextureName(textureName))
				guess.textureCount.give(GameType.DOOM);
			if (isDoom2TextureName(textureName))
				guess.textureCount.give(GameType.DOOM2);
			if (isPlutoniaTextureName(textureName))
				guess.textureCount.give(GameType.PLUTONIA);
			if (isTNTTextureName(textureName))
				guess.textureCount.give(GameType.TNT);
			if (isHereticTextureName(textureName))
				guess.textureCount.give(GameType.HERETIC);
			if (isHexenTextureName(textureName))
				guess.textureCount.give(GameType.HEXEN);
			if (isStrifeTextureName(textureName))
				guess.textureCount.give(GameType.STRIFE);
		}
	}

	private static void countFlatName(String textureName, GameTypeGuess guess)
	{
		guess.flatTotal++;
		
		if (isDoomFlatLump(textureName))
			guess.flatCount.give(GameType.DOOM);
		if (isDoom2FlatLump(textureName))
		{
			guess.flatCount.give(GameType.DOOM2);
			guess.flatCount.give(GameType.PLUTONIA);
			guess.flatCount.give(GameType.TNT);
		}
		if (isHereticFlatLump(textureName))
			guess.flatCount.give(GameType.HERETIC);
		if (isHexenFlatLump(textureName))
			guess.flatCount.give(GameType.HEXEN);
		if (isStrifeFlatLump(textureName))
			guess.flatCount.give(GameType.STRIFE);
	}

	private static void countGraphic(String entry, GameTypeGuess guess)
	{
		guess.graphicsTotal++;
		
		if (isDoomGraphicLump(entry))
			guess.graphicsCount.give(GameType.DOOM);
		if (isDoom2GraphicLump(entry))
		{
			guess.graphicsCount.give(GameType.DOOM2);
			guess.graphicsCount.give(GameType.PLUTONIA);
			guess.graphicsCount.give(GameType.TNT);
		}
		if (isHereticGraphicLump(entry))
			guess.graphicsCount.give(GameType.HERETIC);
		if (isHexenGraphicLump(entry))
			guess.graphicsCount.give(GameType.HEXEN);
		if (isStrifeGraphicLump(entry))
			guess.graphicsCount.give(GameType.STRIFE);
	}
	
	private static void countMusicName(String entry, GameTypeGuess guess)
	{
		guess.musicTotal++;
		
		if (isDoomMusicLump(entry))
			guess.musicCount.give(GameType.DOOM);
		if (isDoom2MusicLump(entry))
		{
			guess.musicCount.give(GameType.DOOM2);
			guess.musicCount.give(GameType.PLUTONIA);
			guess.musicCount.give(GameType.TNT);
		}
		if (isHereticMusicLump(entry))
			guess.musicCount.give(GameType.HERETIC);
		if (isHexenMusicLump(entry))
			guess.musicCount.give(GameType.HEXEN);
		if (isStrifeMusicLump(entry))
			guess.musicCount.give(GameType.STRIFE);
	}
	
	private static void countSoundName(String entry, GameTypeGuess guess)
	{
		guess.soundTotal++;
		
		if (isDoomSoundLump(entry))
			guess.soundCount.give(GameType.DOOM);
		if (isDoom2SoundLump(entry))
		{
			guess.soundCount.give(GameType.DOOM2);
			guess.soundCount.give(GameType.PLUTONIA);
			guess.soundCount.give(GameType.TNT);
		}
		if (isHereticSoundLump(entry))
			guess.soundCount.give(GameType.HERETIC);
		if (isHexenSoundLump(entry))
			guess.soundCount.give(GameType.HEXEN);
		if (isStrifeSoundLump(entry))
			guess.soundCount.give(GameType.STRIFE);
	}
	
	// Loads and puts sidedefs into a vector.
	private static void guessGetSidedefs(DoomWad wad, int offset, List<Sidedef> sidedefOut) throws IOException
	{
		if (wad.contains("sidedefs", offset))
		{
			InputStream in = wad.getDataAsStream("sidedefs", offset);
			for (Sidedef sd : DoomMap.readSidedefLump(in))
				sidedefOut.add(sd);
		}
		else if (wad.contains("textmap", offset))
		{
			InputStream in = wad.getDataAsStream("textmap", offset);
			UDMFTable t = UDMFReader.readData(in);
			UDMFNamespace nms = UDMFUtil.getNamespaceForName(t.getGlobalFields().get("namespace"));
			if (nms != null)
			{
				for (UDMFStruct str : t.getStructs("sidedef"))
				{
					Sidedef side = new Sidedef();
					nms.getSidedefAttribs(str, side);
					sidedefOut.add(side);
				}
			}
		}
		
	}

	// Loads and puts sectors into a vector.
	private static void guessGetSectors(DoomWad wad, int offset, List<Sector> sectorOut) throws IOException
	{
		if (wad.contains("sectors", offset))
		{
			InputStream in = wad.getDataAsStream("sectors", offset);
			for (Sector sect : DoomMap.readSectorLump(in))
				sectorOut.add(sect);
		}
		else if (wad.contains("textmap", offset))
		{
			InputStream in = wad.getDataAsStream("textmap", offset);
			UDMFTable t = UDMFReader.readData(in);
			UDMFNamespace nms = UDMFUtil.getNamespaceForName(t.getGlobalFields().get("namespace"));
			if (nms != null)
			{
				for (UDMFStruct str : t.getStructs("sector"))
				{
					Sector sect = new Sector();
					nms.getSectorAttribs(str, sect);
					sectorOut.add(sect);
				}
			}
		}
		
	}

	/**
	 * Attempts to figure out what game a particular wad belongs to.
	 * If nothing in the WAD is a tell-tale giveaway, or is too ambiguous,
	 * null is returned. This is still not 100% accurate, and will return 
	 * the port type of most common compatibility.
	 * <p>
	 * This will call intuitGameType(wf) in order to get a clue via the game type.
	 * @param wf	the Wad file to inspect.
	 */
	public static SourcePortType intuitPortType(DoomWad wf)
	{
		return intuitPortType(wf, intuitGameType(wf));
	}

	/**
	 * Attempts to figure out what game a particular wad belongs to.
	 * If nothing in the WAD is a tell-tale giveaway, or is too ambiguous,
	 * SourcePortType.UNKNOWN is returned. This is still not 100% accurate, 
	 * and will return the port type of most common compatibility.
	 * @param wf	the Wad file to inspect.
	 * @param gtype	the gametype to use as a clue.
	 */
	public static SourcePortType intuitPortType(DoomWad wf, GameType gtype)
	{
		SourcePortType[] currentGuess = new SourcePortType[]{SourcePortType.UNKNOWN};
		guessPortByGameType(wf, gtype, currentGuess);
		return currentGuess[0];
	}
	
	/**
	 * Attempts to figure out what game a particular wad belongs to.
	 * If nothing in the WAD is a tell-tale giveaway, or is too ambiguous,
	 * SourcePortType.UNKNOWN is returned. This is still not 100% accurate, 
	 * and will return the port type of most common compatibility.
	 * @param pk3	the PK3 file to inspect.
	 * @param gtype	the gametype to use as a clue.
	 */
	public static SourcePortType intuitPortType(DoomPK3 pk3, GameType gtype)
	{
		SourcePortType[] currentGuess = new SourcePortType[]{SourcePortType.UNKNOWN};
		guessPortByMap(pk3, currentGuess);
		return currentGuess[0];
	}

	// Narrows down the source port by the game type.
	private static boolean guessPortByGameType(DoomWad wf, GameType gtype, SourcePortType[] currentGuess)
	{
		if (gtype == null)
			currentGuess[0] = SourcePortType.UNKNOWN;
		else switch (gtype)
		{
			case DOOM:
				return guessPortByDoomGame(wf, gtype, currentGuess);
			case DOOM2:
			case TNT:
			case PLUTONIA:
				return guessPortByDoom2Game(wf, gtype, currentGuess);
			case HERETIC:
				return guessPortByHereticGame(wf, gtype, currentGuess);
			case HEXEN:
				return guessPortByHexenGame(wf, gtype, currentGuess);
			case STRIFE:
				return guessPortByStrifeGame(wf, gtype, currentGuess);
		}
		
		return false;
	}
	
	// Narrows down the source port by the game type.
	private static boolean guessPortByDoomGame(DoomWad wf, GameType gtype, SourcePortType[] currentGuess)
	{
		currentGuess[0] = SourcePortType.DOOM;
		guessPortByMap(wf, gtype, currentGuess);
		return false;
	}
	
	// Narrows down the source port by the game type.
	private static boolean guessPortByDoom2Game(DoomWad wf, GameType gtype, SourcePortType[] currentGuess)
	{
		currentGuess[0] = SourcePortType.DOOM2;
		guessPortByMap(wf, gtype, currentGuess);
		return false;
	}
	
	// Narrows down the source port by the game type.
	private static boolean guessPortByHereticGame(DoomWad wf, GameType gtype, SourcePortType[] currentGuess)
	{
		currentGuess[0] = SourcePortType.HERETIC;
		guessPortByMap(wf, gtype, currentGuess);
		return false;
	}
	
	// Narrows down the source port by the game type.
	private static boolean guessPortByHexenGame(DoomWad wf, GameType gtype, SourcePortType[] currentGuess)
	{
		currentGuess[0] = SourcePortType.HEXEN;
		guessPortByMap(wf, gtype, currentGuess);
		return false;
	}
	
	// Narrows down the source port by the game type.
	private static boolean guessPortByStrifeGame(DoomWad wf, GameType gtype, SourcePortType[] currentGuess)
	{
		currentGuess[0] = SourcePortType.STRIFE;
		guessPortByMap(wf, gtype, currentGuess);
		return false;
	}
	
	private static boolean guessPortByMap(DoomWad wf, GameType gtype, SourcePortType[] currentGuess)
	{
		String[] maps = DoomMap.getAllMapEntries(wf);
		for (String s : maps)
		{
			try {
				DoomMap dm = new DoomMap(wf, gtype, s);
				if (dm.getOriginalFormat() == DoomMap.Format.DOOM)
				{
					for (Linedef line : dm.getLinedefList())
					{
						if ((BOOM_LINE_TABLE.containsKey(line.getSpecial()) || line.getSpecial() >= 8192) && 
							currentGuess[0] != SourcePortType.LEGACY && 
							currentGuess[0] != SourcePortType.EDGE)
						{
							currentGuess[0] = SourcePortType.BOOM;
						}
						else if (LEGACY_LINE_TABLE.containsKey(line.getSpecial()) &&
							currentGuess[0] != SourcePortType.EDGE)
						{
							currentGuess[0] = SourcePortType.LEGACY;
						}
						else if (EDGE_LINE_TABLE.containsKey(line.getSpecial()))
						{
							currentGuess[0] = SourcePortType.EDGE;
							return true;
						}
					}
				}
				else if (dm.getOriginalFormat() == DoomMap.Format.HEXEN)
				{
					for (Linedef line : dm.getLinedefList())
					{
						if (ZDOOM_LINE_TABLE.containsKey(line.getSpecial()) && currentGuess[0] != SourcePortType.GZDOOM)
						{
							currentGuess[0] = SourcePortType.ZDOOM;
						}
						else if (GZDOOM_LINE_TABLE.containsKey(line.getSpecial()))
						{
							currentGuess[0] = SourcePortType.GZDOOM;
							return true;
						}
					}
				}
				else if (dm.getOriginalFormat() == DoomMap.Format.UDMF)
				{
					String namespace = dm.getOriginalUDMFNamespace();
					if (namespace.equalsIgnoreCase("zdoom"))
					{
						currentGuess[0] = SourcePortType.ZDOOM;
						return true;
					}
					else if (namespace.equalsIgnoreCase("zdoomtranslated"))
					{
						currentGuess[0] = SourcePortType.ZDOOM;
						return true;
					}
					else for (Linedef line : dm.getLinedefList())
					{
						if (ZDOOM_LINE_TABLE.containsKey(line.getSpecial()) && currentGuess[0] != SourcePortType.GZDOOM)
						{
							currentGuess[0] = SourcePortType.ZDOOM;
						}
						else if (GZDOOM_LINE_TABLE.containsKey(line.getSpecial()))
						{
							currentGuess[0] = SourcePortType.GZDOOM;
							return true;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private static boolean guessPortByMap(DoomPK3 pk3, SourcePortType[] currentGuess)
	{
		ZipEntry[] maps = pk3.getMaps();
		for (ZipEntry entry : maps)
		{
			try {
				DoomWad wad = new WadBuffer(pk3.getDataAsStream(entry));
				DoomMap dm = new DoomMap(wad, DoomMap.getAllMapEntries(wad)[0]);
				
				if (dm.getOriginalFormat() == DoomMap.Format.HEXEN)
				{
					for (Linedef line : dm.getLinedefList())
					{
						if ((HEXEN_LINE_TABLE.containsKey(line.getSpecial()) || ZDOOM_LINE_TABLE.containsKey(line.getSpecial())) && currentGuess[0] != SourcePortType.GZDOOM)
						{
							currentGuess[0] = SourcePortType.ZDOOM;
						}
						else if (GZDOOM_LINE_TABLE.containsKey(line.getSpecial()))
						{
							currentGuess[0] = SourcePortType.GZDOOM;
							return true;
						}
					}
				}
				else if (dm.getOriginalFormat() == DoomMap.Format.UDMF)
				{
					String namespace = dm.getOriginalUDMFNamespace();
					if (namespace.equalsIgnoreCase("zdoom"))
					{
						currentGuess[0] = SourcePortType.ZDOOM;
						return true;
					}
					else if (namespace.equalsIgnoreCase("zdoomtranslated"))
					{
						currentGuess[0] = SourcePortType.ZDOOM;
						return true;
					}
					else for (Linedef line : dm.getLinedefList())
					{
						if ((HEXEN_LINE_TABLE.containsKey(line.getSpecial()) || ZDOOM_LINE_TABLE.containsKey(line.getSpecial())) && currentGuess[0] != SourcePortType.GZDOOM)
						{
							currentGuess[0] = SourcePortType.ZDOOM;
						}
						else if (GZDOOM_LINE_TABLE.containsKey(line.getSpecial()))
						{
							currentGuess[0] = SourcePortType.GZDOOM;
							return true;
						}
					}
				}
				else
					currentGuess[0] = SourcePortType.ZDOOM;
					
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Game type guess.
	 */
	private static class GameTypeGuess
	{
		public int mapTotal;
		public int textureTotal;
		public int graphicsTotal;
		public int flatTotal;
		public int musicTotal;
		public int soundTotal;
		
		public CountMap<GameType> mapCount;
		public CountMap<GameType> textureCount;
		public CountMap<GameType> graphicsCount;
		public CountMap<GameType> flatCount;
		public CountMap<GameType> musicCount;
		public CountMap<GameType> soundCount;
		
		public GameTypeGuess()
		{
			mapTotal = 0;
			textureTotal = 0;
			graphicsTotal = 0;
			flatTotal = 0;
			musicTotal = 0;
			soundTotal = 0;
			
			mapCount = new CountMap<GameType>();
			textureCount = new CountMap<GameType>();
			graphicsCount = new CountMap<GameType>();
			flatCount = new CountMap<GameType>();
			musicCount = new CountMap<GameType>();
			soundCount = new CountMap<GameType>();
		}
	}
	
}
