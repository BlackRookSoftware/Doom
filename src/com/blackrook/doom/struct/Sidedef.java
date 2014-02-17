/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.struct;

import java.io.*;

import com.blackrook.doom.*;
import com.blackrook.doom.util.DoomUtil;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

import static com.blackrook.doom.DoomObjectUtils.*;


/**
 * This class holds Sidedef information: its texture offsets, its upper, lower, and middle
 * texture names, and its Sector reference.
 * @author Matthew Tropiano
 */
public class Sidedef implements DoomObject
{
	/** Blank texture name. */
	public static final String BLANK_TEXTURE = "-";
	
	/** Texture X-Offset. */
	protected int xOffs;
	/** Texture Y-Offset. */
	protected int yOffs;
	/** Upper texture name. */
	protected String upperTex;
	/** Lower texture name. */
	protected String lowerTex;
	/** Middle texture name. */
	protected String middleTex;
	/** Sector reference number. */
	protected int sectorRef;
	
	/** This object's comment. */
	protected String comment;

	/** Scaling factor for the upper texture (X-axis). */
	protected float upperTextureScaleX;
	/** Scaling factor for the upper texture (Y-axis). */
	protected float upperTextureScaleY;
	/** Scaling factor for the middle texture (X-axis). */
	protected float middleTextureScaleX;
	/** Scaling factor for the middle texture (Y-axis). */
	protected float middleTextureScaleY;
	/** Scaling factor for the lower texture (X-axis). */
	protected float lowerTextureScaleX;
	/** Scaling factor for the lower texture (Y-axis). */
	protected float lowerTextureScaleY;
	/** Offset for the upper texture (X-axis). */
	protected float upperTextureOffsetX;
	/** Offset for the upper texture (Y-axis). */
	protected float upperTextureOffsetY;
	/** Offset for the middle texture (X-axis). */
	protected float middleTextureOffsetX;
	/** Offset for the middle texture (Y-axis). */
	protected float middleTextureOffsetY;
	/** Offset for the lower texture (X-axis). */
	protected float lowerTextureOffsetX;
	/** Offset for the lower texture (Y-axis). */
	protected float lowerTextureOffsetY;
	/** Light level for this sidedef. */
	protected int light;
	/** Is the light level absolute and not related to the sector's light level? */
	protected boolean lightAbsolute;
	/** Is "fake contrast" used on this side? */
	protected boolean noFakeContrast;
	/** Is "smooth lighting" used on this side? */
	protected boolean smoothLighting;
	/** Clips middle textures at the floor and ceiling? */
	protected boolean clipMiddleTexture;
	/** Wraps middle textures regardless of being see-through or not? */
	protected boolean wrapMiddleTexture;
	/** Disables use of decal application. */
	protected boolean noDecals;
	
	/**
	 * Creates a new Sidedef.
	 */	
	public Sidedef()
	{
		reset();
	}

	/**
	 * Resets this object's data to its defaults.
	 * Note that it may not necessarily set the object's data to valid information.
	 */
	public void reset()
	{
		xOffs = 0;
		yOffs = 0;
		upperTex = BLANK_TEXTURE;
		lowerTex = BLANK_TEXTURE;
		middleTex = BLANK_TEXTURE;
		sectorRef = -1;
		comment = "";
		
		upperTextureScaleX = 1.0f;
		upperTextureScaleY = 1.0f;
		middleTextureScaleX = 1.0f;
		middleTextureScaleY = 1.0f;
		lowerTextureScaleX = 1.0f;
		lowerTextureScaleY = 1.0f;
		upperTextureOffsetX = 0.0f;
		upperTextureOffsetY = 0.0f;
		middleTextureOffsetX = 0.0f;
		middleTextureOffsetY = 0.0f;
		lowerTextureOffsetX = 0.0f;
		lowerTextureOffsetY = 0.0f;
		light = 0;
		lightAbsolute = false;
		noFakeContrast = false;
		smoothLighting = false;
		clipMiddleTexture = false;
		wrapMiddleTexture = false;
		noDecals = false;
	}
	
	/** Gets the X-Offset of the textures on this sidedef. */
	public int getOffsetX() 					{return xOffs;}
	/** Gets the Y-Offset of the textures on this sidedef. */
	public int getOffsetY() 					{return yOffs;}
	/** Gets the upper texture of this sidedef. */
	public String getUpperTexture() 			{return upperTex;}
	/** Gets the lower texture of this sidedef. */
	public String getLowerTexture() 			{return lowerTex;}
	/** Gets the middle texture of this sidedef. */
	public String getMiddleTexture()			{return middleTex;}
	/** Gets the sector reference of this sidedef. */
	public int getSectorRef()					{return sectorRef;}

	/** Sets the X-Offset of the textures on this sidedef. */
	public void setOffsetX(int val)				{xOffs = val;}
	/** Sets the Y-Offset of the textures on this sidedef. */
	public void setOffsetY(int val)				{yOffs = val;}
	/** Sets the upper texture of this sidedef. */
	public void setUpperTexture(String val) 	{upperTex = val;}
	/** Sets the lower texture of this sidedef. */
	public void setLowerTexture(String val) 	{lowerTex = val;}
	/** Sets the middle texture of this sidedef. */
	public void setMiddleTexture(String val)	{middleTex = val;}
	/** Sets the sector reference of this sidedef. */
	public void setSectorRef(int val)			{sectorRef = val;}
	
	/** Gets this object's comment. */
	public String getComment()					{return comment;}
	/** Gets this object's comment. */
	public void setComment(String val)			{comment = val;}

	/** Gets the scaling factor for the upper texture (X-axis). */
	public float getUpperTextureScaleX()
	{
		return upperTextureScaleX;
	}

	/** Sets the scaling factor for the upper texture (X-axis). */
	public void setUpperTextureScaleX(float upperTextureScaleX)
	{
		this.upperTextureScaleX = upperTextureScaleX;
	}

	/** Gets the scaling factor for the upper texture (Y-axis). */
	public float getUpperTextureScaleY()
	{
		return upperTextureScaleY;
	}

	/** Sets the scaling factor for the upper texture (Y-axis). */
	public void setUpperTextureScaleY(float upperTextureScaleY)
	{
		this.upperTextureScaleY = upperTextureScaleY;
	}

	/** Gets the scaling factor for the middle texture (X-axis). */
	public float getMiddleTextureScaleX()
	{
		return middleTextureScaleX;
	}

	/** Sets the scaling factor for the middle texture (X-axis). */
	public void setMiddleTextureScaleX(float middleTextureScaleX)
	{
		this.middleTextureScaleX = middleTextureScaleX;
	}

	/** Gets the scaling factor for the middle texture (Y-axis). */
	public float getMiddleTextureScaleY()
	{
		return middleTextureScaleY;
	}

	/** Sets the scaling factor for the middle texture (Y-axis). */
	public void setMiddleTextureScaleY(float middleTextureScaleY)
	{
		this.middleTextureScaleY = middleTextureScaleY;
	}

	/** Gets the scaling factor for the lower texture (X-axis). */
	public float getLowerTextureScaleX()
	{
		return lowerTextureScaleX;
	}

	/** Sets the scaling factor for the lower texture (X-axis). */
	public void setLowerTextureScaleX(float lowerTextureScaleX)
	{
		this.lowerTextureScaleX = lowerTextureScaleX;
	}

	/** Gets the scaling factor for the lower texture (Y-axis). */
	public float getLowerTextureScaleY()
	{
		return lowerTextureScaleY;
	}

	/** Sets the scaling factor for the lower texture (Y-axis). */
	public void setLowerTextureScaleY(float lowerTextureScaleY)
	{
		this.lowerTextureScaleY = lowerTextureScaleY;
	}

	/** Gets the offset for the upper texture (X-axis). */
	public float getUpperTextureOffsetX()
	{
		return upperTextureOffsetX;
	}

	/** Sets the offset for the upper texture (X-axis). */
	public void setUpperTextureOffsetX(float upperTextureOffsetX)
	{
		this.upperTextureOffsetX = upperTextureOffsetX;
	}

	/** Gets the offset for the upper texture (Y-axis). */
	public float getUpperTextureOffsetY()
	{
		return upperTextureOffsetY;
	}

	/** Sets the offset for the upper texture (Y-axis). */
	public void setUpperTextureOffsetY(float upperTextureOffsetY)
	{
		this.upperTextureOffsetY = upperTextureOffsetY;
	}

	/** Gets the offset for the middle texture (X-axis). */
	public float getMiddleTextureOffsetX()
	{
		return middleTextureOffsetX;
	}

	/** Sets the offset for the middle texture (X-axis). */
	public void setMiddleTextureOffsetX(float middleTextureOffsetX)
	{
		this.middleTextureOffsetX = middleTextureOffsetX;
	}

	/** Gets the offset for the middle texture (Y-axis). */
	public float getMiddleTextureOffsetY()
	{
		return middleTextureOffsetY;
	}

	/** Sets the offset for the middle texture (Y-axis). */
	public void setMiddleTextureOffsetY(float middleTextureOffsetY)
	{
		this.middleTextureOffsetY = middleTextureOffsetY;
	}

	/** Gets the offset for the lower texture (X-axis). */
	public float getLowerTextureOffsetX()
	{
		return lowerTextureOffsetX;
	}

	/** Sets the offset for the lower texture (X-axis). */
	public void setLowerTextureOffsetX(float lowerTextureOffsetX)
	{
		this.lowerTextureOffsetX = lowerTextureOffsetX;
	}

	/** Gets the offset for the lower texture (Y-axis). */
	public float getLowerTextureOffsetY()
	{
		return lowerTextureOffsetY;
	}

	/** Sets the offset for the lower texture (Y-axis). */
	public void setLowerTextureOffsetY(float lowerTextureOffsetY)
	{
		this.lowerTextureOffsetY = lowerTextureOffsetY;
	}

	/** Gets the light level for this sidedef. */
	public int getLight()
	{
		return light;
	}

	/** Sets the light level for this sidedef. */
	public void setLight(int light)
	{
		this.light = light;
	}

	/** Gets if the light level is absolute and not related to the sector's light level? */
	public boolean isLightAbsolute()
	{
		return lightAbsolute;
	}

	/** Sets if the light level is absolute and not related to the sector's light level? */
	public void setLightAbsolute(boolean lightAbsolute)
	{
		this.lightAbsolute = lightAbsolute;
	}

	/** Gets if "fake contrast" is used on this side? */
	public boolean notUseFakeContrast()
	{
		return noFakeContrast;
	}

	/** Sets if "fake contrast" is used on this side? */
	public void setNotUseFakeContrast(boolean noFakeContrast)
	{
		this.noFakeContrast = noFakeContrast;
	}

	/** Gets if "smooth lighting" is used on this side? */
	public boolean isSmoothlyLit()
	{
		return smoothLighting;
	}

	/** Sets if "smooth lighting" is used on this side? */
	public void setSmoothlyLit(boolean smoothLighting)
	{
		this.smoothLighting = smoothLighting;
	}

	/** Gets if this clips middle textures at the floor and ceiling. */
	public boolean clipsMiddleTexture()
	{
		return clipMiddleTexture;
	}

	/** Sets if this clips middle textures at the floor and ceiling. */
	public void setClipMiddleTexture(boolean clipMiddleTexture)
	{
		this.clipMiddleTexture = clipMiddleTexture;
	}

	/** Gets if this wraps middle textures regardless of being see-through or not. */
	public boolean wrapsMiddleTexture()
	{
		return wrapMiddleTexture;
	}

	/** Sets if this wraps middle textures regardless of being see-through or not. */
	public void setWrapMiddleTexture(boolean wrapMiddleTexture)
	{
		this.wrapMiddleTexture = wrapMiddleTexture;
	}

	/** Gets if this side disables use of decal application. */
	public boolean isNoDecals()
	{
		return noDecals;
	}

	/** Sets if this side disables use of decal application. */
	public void setNoDecals(boolean noDecals)
	{
		this.noDecals = noDecals;
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

	@Override
	public byte[] getDoomBytes() throws DataExportException
	{
		callDoomCompatibilityCheck();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
		try{
			sw.writeShort((short)(xOffs & 0x0ffff));
			sw.writeShort((short)(yOffs & 0x0ffff));
			sw.writeASCIIString(DoomUtil.coerceToEntry(upperTex));
			sw.writeASCIIString(DoomUtil.coerceToEntry(lowerTex));
			sw.writeASCIIString(DoomUtil.coerceToEntry(middleTex));
			sw.writeShort((short)(sectorRef & 0x0ffff));
		} catch (IOException e){}
		
		return bos.toByteArray();
	}

	/**
	 * Checks this data structure for data export integrity for the Doom format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	protected void callDoomCompatibilityCheck() throws DataExportException
	{
		checkShort("X-position", xOffs);
		checkShort("Y-position", yOffs);
		checkString("Upper texture", upperTex);
		checkString("Lower texture", lowerTex);
		checkString("Middle texture", middleTex);
		checkShortUnsigned("Sector reference", sectorRef);

		callZDoomUDMFCheck();
	}

	/**
	 * Checks this data structure for data export integrity for the ZDoom/Hexen format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	protected void callHexenCompatibilityCheck() throws DataExportException
	{
		callDoomCompatibilityCheck();
	}

	/**
	 * Checks this object's data for ZDoom-specific UDMF namespace significance.
	 * If it finds that a ZDoom-specific flag is set, it will throw an exception.
	 */
	protected void callZDoomUDMFCheck() throws DataExportException
	{
		checkEqual("Upper Texture Scale X", 1.0f, upperTextureScaleX);
		checkEqual("Upper Texture Scale Y", 1.0f, upperTextureScaleY);
		checkEqual("Middle Texture Scale X", 1.0f, middleTextureScaleX);
		checkEqual("Middle Texture Scale Y", 1.0f, middleTextureScaleY);
		checkEqual("Lower Texture Scale X", 1.0f, lowerTextureScaleX);
		checkEqual("Lower Texture Scale Y", 1.0f, lowerTextureScaleY);
		checkEqual("Upper Texture Offset X", 0.0f, upperTextureOffsetX);
		checkEqual("Upper Texture Offset Y", 0.0f, upperTextureOffsetY);
		checkEqual("Middle Texture Offset X", 0.0f, middleTextureOffsetX);
		checkEqual("Middle Texture Offset Y", 0.0f, middleTextureOffsetY);
		checkEqual("Lower Texture Offset X", 0.0f, lowerTextureOffsetX);
		checkEqual("Lower Texture Offset Y", 0.0f, lowerTextureOffsetY);
		checkZero("Light", light);
		checkFalse("Light Absolute", lightAbsolute);
		checkFalse("No Fake Contrast", noFakeContrast);
		checkFalse("Smooth Lighting", smoothLighting);
		checkFalse("Clips Middle Texture", clipMiddleTexture);
		checkFalse("Wraps Middle Texture", wrapMiddleTexture);
		checkFalse("No Decals", noDecals);
	}
	
	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		xOffs = sr.readShort();
		yOffs = sr.readShort();
		upperTex = DoomUtil.nameFix(sr.readASCIIString(DoomUtil.ENTRY_NAME_SIZE));
		lowerTex = DoomUtil.nameFix(sr.readASCIIString(DoomUtil.ENTRY_NAME_SIZE));
		middleTex = DoomUtil.nameFix(sr.readASCIIString(DoomUtil.ENTRY_NAME_SIZE));
		sectorRef = sr.readShort() & 0x0ffff;
	}

	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

	/**
	 * Returns the length of this structure in Doom-formatted bytes. 
	 */
	public static int getDoomLength()
	{
		return 30;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Sidedef offset: (");
		sb.append(xOffs);
		sb.append(", ");
		sb.append(yOffs);
		sb.append(") [");
		sb.append(String.format("%8s, %8s, %8s", upperTex, middleTex, lowerTex));
		sb.append("] sector ");
		sb.append(sectorRef);
		return sb.toString();
	}
	
}
