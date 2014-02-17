/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.struct;

import static com.blackrook.doom.DoomObjectUtils.checkRange;
import static com.blackrook.doom.DoomObjectUtils.checkShort;
import static com.blackrook.doom.DoomObjectUtils.checkShortUnsigned;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.blackrook.commons.Common;
import com.blackrook.commons.hash.HashMap;
import com.blackrook.commons.math.RMath;
import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomGraphicObject;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * Doom graphic patch data. Useful for editing/displaying graphics.
 * This is not to be confused with Texture.Patch, which encapsulates
 * the use of a patch in a texture.
 * @author Matthew Tropiano
 */
public class Patch implements DoomGraphicObject
{
	public static final short PIXEL_TRANSLUCENT = -1;
	
	/** The pixel data. */
	private short[][] pixels; 
	/** The offset from the center, horizontally, in pixels. */
	private int offsetX; 
	/** The offset from the center, vertically, in pixels. */
	private int offsetY; 

	/**
	 * Creates a new patch with dimensions (1, 1).
	 */
	public Patch()
	{
		this(1, 1);
	}
	
	/**
	 * Creates a new patch.
	 * @param width		the width of the patch in pixels.
	 * @param height	the height of the patch in pixels.
	 */
	public Patch(int width, int height)
	{
		if (width < 1 || height < 1)
			throw new IllegalArgumentException("Width or height cannot be less than 1.");
		offsetX = 0;
		offsetY = 0;
		setDimensions(width, height);
	}

	/**
	 * Sets the dimensions of this patch.
	 * WARNING: This will clear all of the data in the patch.
	 * @param width		the width of the patch in pixels.
	 * @param height	the height of the patch in pixels.
	 */
	public void setDimensions(int width, int height)
	{
		pixels = new short[width][height];
		for (int i = 0; i < pixels.length; i++)
			for (int j = 0; j < pixels[i].length; j++)
				pixels[i][j] = PIXEL_TRANSLUCENT;
	}
	
	/**
	 * Gets the offset from the center, horizontally, in pixels.
	 */
	public int getOffsetX()
	{
		return offsetX;
	}

	/**
	 * Sets the offset from the center, horizontally, in pixels.
	 */
	public void setOffsetX(int offsetX)
	{
		this.offsetX = offsetX;
	}

	/**
	 * Gets the offset from the center, vertically, in pixels.
	 */
	public int getOffsetY()
	{
		return offsetY;
	}

	/**
	 * Sets the offset from the center, vertically, in pixels.
	 */
	public void setOffsetY(int offsetY)
	{
		this.offsetY = offsetY;
	}

	/**
	 * Returns the width of this graphic in pixels.
	 */
	public int getWidth()
	{
		return pixels.length;
	}
	
	/**
	 * Returns the height of this graphic in pixels.
	 */
	public int getHeight()
	{
		return pixels[0].length;
	}
	
	/**
	 * Sets the pixel data at a location in the patch.
	 * Valid values are in the range of -1 to 255, with
	 * 0 to 255 being palette indexes and -1 being translucent
	 * pixel information. Values outside this range are CLAMPED into the
	 * range.
	 * @param x	patch x-coordinate.
	 * @param y	patch y-coordinate.
	 * @param value	the value to set.
	 */
	public void setPixel(int x, int y, int value)
	{
		pixels[x][y] = (short)RMath.clampValue(value, -1, 255);
	}
	
	@Override
	public void setImage(BufferedImage image, Palette palette)
	{
		setDimensions(image.getWidth(), image.getHeight());
		for (int i = 0; i < image.getWidth(); i++)
			for (int j = 0; j < image.getHeight(); j++)
				setPixelColor(i, j, Common.argbToColor(image.getRGB(i, j)), palette);
	}

	@Override
	public void setPixelColor(int x, int y, Color color, Palette palette)
	{
		setPixel(x, y, palette.getNearestColorIndex(color));
	}

	/**
	 * Gets the pixel data at a location in the patch.
	 * Returns a palette index value from 0 to 255 or PIXEL_TRANSLUCENT
	 * if the pixel is not filled in.
	 * @param x	patch x-coordinate.
	 * @param y	patch y-coordinate.
	 */
	public short getPixel(int x, int y)
	{
		return pixels[x][y];
	}
	
	@Override
	public Color getPixelColor(int x, int y, Palette pal, ColorMap colormap, Color transparentColor)
	{
		if (colormap == null)
			return pixels[x][y] >= 0 ? pal.getColor(pixels[x][y]) : transparentColor;
		else
			return pixels[x][y] >= 0 ? pal.getColor(colormap.getPaletteIndex(pixels[x][y])) : transparentColor;
	}
	
	@Override
	public byte[] getDoomBytes() throws DataExportException
	{
		callDoomCompatibilityCheck();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
		try{
			sw.writeUnsignedShort(pixels.length);
			sw.writeUnsignedShort(pixels[0].length);
			sw.writeShort((short)offsetX);
			sw.writeShort((short)offsetY);
			
			int[] columnOffsets = new int[getWidth()];

			final int STATE_TRANS = 0;
			final int STATE_COLOR = 1;
			
			int columnOffs = 8 + (4 * getWidth());
			ByteArrayOutputStream dataBytes = new ByteArrayOutputStream();
			
			for (int i = 0; i < columnOffsets.length; i++)
			{
				columnOffsets[i] = columnOffs;
				ByteArrayOutputStream columnBytes = new ByteArrayOutputStream();
				ByteArrayOutputStream pbytes = new ByteArrayOutputStream();
				short[] col = pixels[i];
				int STATE = STATE_TRANS;
				int span = 0;

				for (int offs = 0; offs < col.length; offs++)
				{
					switch (STATE)
					{
						case STATE_TRANS:
							if (col[offs] != PIXEL_TRANSLUCENT)
							{
								span = 0;
								columnBytes.write(offs & 0x0ff);
								pbytes = new ByteArrayOutputStream();
								STATE = STATE_COLOR;
								offs--;	// state change. keep index.
							}
							break;
							
						case STATE_COLOR:
							if (col[offs] == PIXEL_TRANSLUCENT)
							{
								columnBytes.write(span & 0x0ff);
								columnBytes.write(0);
								columnBytes.write(pbytes.toByteArray());
								columnBytes.write(0);
								pbytes.reset();
								STATE = STATE_TRANS;
								offs--;	// state change. keep index.
							}
							else
							{
								pbytes.write(col[offs] & 0x0ff);
								span++;
							}
							break;
					}
				}
				
				if (pbytes.size() > 0)
				{
					columnBytes.write(span & 0x0ff);
					columnBytes.write(0);
					columnBytes.write(pbytes.toByteArray());
					columnBytes.write(0);
				}
				columnBytes.write(-1);
				columnOffs += columnBytes.size();
				dataBytes.write(columnBytes.toByteArray());
				columnBytes.reset();
			}
			
			for (int n : columnOffsets)
				sw.writeInt(n);
			
			sw.writeBytes(dataBytes.toByteArray());
			
		} catch (IOException e){}
		
		return bos.toByteArray();
	}

	@Override
	public boolean isDoomCompatible()
	{
		try {
			callDoomCompatibilityCheck();
		} catch (DataExportException e) {
			return false;
		}
		return true;
	}

	/**
	 * Checks this data structure for data export integrity for the Doom format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	public void callDoomCompatibilityCheck() throws DataExportException
	{
		checkShortUnsigned("Width", pixels.length);
		checkShortUnsigned("Height", pixels[0].length);
		checkShort("Offset X", offsetX);
		checkShort("Offset Y", offsetY);
		for (int i = 0; i < pixels.length; i++)
			for (int j = 0; j < pixels[i].length; j++)
				checkRange("Pixel ("+i+", "+j+")", -1, 255, pixels[i][j]);
	}
	
	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		setDimensions(sr.readUnsignedShort(), sr.readUnsignedShort());
		offsetX = sr.readShort();
		offsetY = sr.readShort();

		// load offset table.
		int[] columnOffsets = sr.readInts(getWidth());
		
		// data must be treated as a stream: find highest short offset so that the reading can stop.
		int offMax = -1;
		for (int i : columnOffsets)
		{
			offMax = i > offMax ? i : offMax;
		}
		
		// precache columns at each particular offset: patch may be compressed.
		HashMap<Integer, byte[]> columnData = new HashMap<Integer, byte[]>();

		for (int i = 0; i < columnOffsets.length; i++)
			columnData.put(columnOffsets[i],columnRead(sr));
			
		for (int x = 0; x < columnOffsets.length; x++)
		{
			int y = 0;
			byte[] b = columnData.get(columnOffsets[x]);
			for (int i = 0; i < b.length; i++)
			{
				y = b[i++] & 0x0ff;
				int span = b[i++] & 0x0ff;
				for (int j = 0; j < span; j++)
					pixels[x][y+j] = (short)(b[i+j] & 0x0ff);
				i += span-1;
			}
		}
				
	}

	/**
	 * Reads a patch column from the reader.
	 */
	protected byte[] columnRead(SuperReader sr) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 

		int offs = 0;
		int span = 0;

		offs = (sr.readByte() & 0x0ff);
		while (offs != 255)
		{
			span = (sr.readByte() & 0x0ff);
			sr.readByte();
			
			out.write(offs);
			out.write(span);
			out.write(sr.readBytes(span));
			sr.readByte();
			
			offs = (sr.readByte() & 0x0ff);
		}
		
		return out.toByteArray();
	}
	
	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

}
