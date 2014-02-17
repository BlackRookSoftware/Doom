/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.struct;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.blackrook.commons.Common;
import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomGraphicObject;
import com.blackrook.doom.util.DoomUtil;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;
import com.blackrook.io.container.PNGContainerReader;
import com.blackrook.io.container.PNGContainerWriter;

/**
 * Represents PNG-formatted data.
 * The export functions write this data back as raw.
 * Pixel-color gathering functions will actually nearest-color-match the colors
 * to a palette-compatible color.
 * @author Matthew Tropiano
 */
public class PNGData implements DoomGraphicObject
{
	private static final String PNG_OFFSET_CHUNK = "grAb";
	
	/** The picture data. */
	private BufferedImage pngData;
	/** The offset from the center, horizontally, in pixels. */
	private int offsetX; 
	/** The offset from the center, vertically, in pixels. */
	private int offsetY; 

	/**
	 * Creates a new image with dimensions (1, 1).
	 */
	public PNGData()
	{
		this(1, 1);
	}

	/**
	 * Creates a new PNG data image.
	 * @param width		the width of the patch in pixels.
	 * @param height	the height of the patch in pixels.
	 */
	public PNGData(int width, int height)
	{
		if (width < 1 || height < 1)
			throw new IllegalArgumentException("Width or height cannot be less than 1.");
		setDimensions(width, height);
	}

	/**
	 * Sets the dimensions of this image.
	 * WARNING: This will clear all of the data in the image.
	 * @param width		the width of the image in pixels.
	 * @param height	the height of the image in pixels.
	 */
	public void setDimensions(int width, int height)
	{
		pngData = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * Sets the pixel data for this graphic using an Image.
	 * This does NOT auto-correct colors to a specific palette.
	 */
	public void setImage(BufferedImage image)
	{
		setImage(image, null);
	}

	@Override
	public void setImage(BufferedImage image, Palette palette)
	{
		pngData = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < pngData.getWidth(); i++)
			for (int j = 0; j < pngData.getHeight(); j++)
				setPixelColor(i, j, Common.argbToColor(image.getRGB(i, j)), palette);
	}

	/**
	 * Sets the pixel data at a location in the graphic using a particular palette and/or colormap.
	 * @param x			patch x-coordinate.
	 * @param y			patch y-coordinate.
	 * @param color		the color to set.
	 */
	public void setPixelColor(int x, int y, Color color)
	{
		pngData.setRGB(x, y, DoomUtil.getNearestColor(color, null, null, null).getRGB());
	}

	@Override
	public void setPixelColor(int x, int y, Color color, Palette palette)
	{
		pngData.setRGB(x, y, DoomUtil.getNearestColor(color, palette, null, null).getRGB());
	}

	/**
	 * Returns the BufferedImage that contains the uncompressed image data. 
	 */
	public BufferedImage getPNGData()
	{
		return pngData;
	}

	@Override
	public byte[] getDoomBytes() throws DataExportException
	{
		ByteArrayOutputStream cbos = new ByteArrayOutputStream();
		try {

			ByteArrayOutputStream ibos = new ByteArrayOutputStream();
			ImageIO.write(pngData, "PNG", ibos);
			ByteArrayInputStream ibis = new ByteArrayInputStream(ibos.toByteArray());
			PNGContainerReader pr = new PNGContainerReader(ibis);
			PNGContainerReader.Chunk cin = null;

			PNGContainerWriter pw = new PNGContainerWriter(cbos);

			cin = pr.nextChunk();	// IHDR
			pw.writeChunk(cin.getName(), cin.getData());
			
			ByteArrayOutputStream obos = new ByteArrayOutputStream();
			obos.write(SuperWriter.intToBytes(getOffsetX(), SuperWriter.BIG_ENDIAN));
			obos.write(SuperWriter.intToBytes(getOffsetY(), SuperWriter.BIG_ENDIAN));
			pw.writeChunk(PNG_OFFSET_CHUNK, obos.toByteArray());
			
			while ((cin = pr.nextChunk()) != null)
				pw.writeChunk(cin.getName(), cin.getData());
			
		} catch (IOException e) {
			throw new DataExportException(e.getLocalizedMessage());
		}
		
		return cbos.toByteArray();
	}

	@Override
	public boolean isDoomCompatible()
	{
		return true;
	}

	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		byte[] b = Common.getBinaryContents(in);
		PNGContainerReader pr = new PNGContainerReader(new ByteArrayInputStream(b));
		PNGContainerReader.Chunk cin = null;
		while ((cin = pr.nextChunk()) != null)
		{
			if (cin.getName().equals(PNG_OFFSET_CHUNK))
			{
				SuperReader sr = new SuperReader(
						new ByteArrayInputStream(cin.getData()), SuperReader.BIG_ENDIAN);
				setOffsetX(sr.readInt());
				setOffsetY(sr.readInt());
				break;
			}
		}

		pngData = ImageIO.read(new ByteArrayInputStream(b));
	}

	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

	@Override
	public int getWidth()
	{
		return pngData.getWidth();
	}

	@Override
	public int getHeight()
	{
		return pngData.getHeight();
	}

	/**
	 * Gets the pixel data at a location in the patch using a particular palette and/or colormap.
	 * Returns a Color value or the transparentColor if the pixel is transparent.
	 * This will use nearest color matching via Cartesian proximity to fit the desirde color into
	 * the specified palette, than filtered through a colormap.
	 * @param x					patch x-coordinate.
	 * @param y					patch y-coordinate.
	 * @param colormap			the colormap to use for the index lookup, before palette lookup. if null, no map is used.
	 * @param palette 			the palette used for color lookup.
	 * @param transparentColor	the transparency color to return if the pixel is translucent.
	 * @return the color of the requested pixel.
	 */
	@Override
	public Color getPixelColor(int x, int y, Palette palette, ColorMap colormap, Color transparentColor)
	{
		return DoomUtil.getNearestColor(Common.argbToColor(pngData.getRGB(x, y)), palette, colormap, transparentColor);
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

}
