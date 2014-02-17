/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.struct;

import java.awt.Color;
import java.io.*;

import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomObject;
import com.blackrook.doom.util.DoomUtil;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

import static com.blackrook.doom.DoomObjectUtils.*;


/**
 * This class holds Sector information: floor height, ceiling height, floor texture, ceiling texture,
 * light level, sector special, and tag.
 * @author Matthew Tropiano
 */
public class Sector implements DoomObject
{
	/** Absolute height of sector's floor. */
	protected int floorHeight;
	/** Absolute height of sector's ceiling. */
	protected int ceilingHeight;
	/** Sector's floor texture name. */
	protected String floorTex;
	/** Sector's ceiling texture name. */
	protected String ceilingTex;
	/** Sector's light level (actually goes up to 255).*/
	protected int lightLev;
	/** Sector's special. */
	protected int special;
	/** Sector's id. */
	protected int id;
	
	/** This object's comment. */
	protected String comment;

	/** Sector's Floor panning value X. */
	protected float floorOffsetX;
	/** Sector's Floor panning value Y. */
	protected float floorOffsetY;
	/** Sector's Ceiling panning value X. */
	protected float ceilingOffsetX;
	/** Sector's Ceiling panning value Y. */
	protected float ceilingOffsetY;
	/** Sector's Floor scale value X. */
	protected float floorScaleX;
	/** Sector's Floor scale value Y. */
	protected float floorScaleY;
	/** Sector's Ceiling scale value X. */
	protected float ceilingScaleX;
	/** Sector's Ceiling scale value Y. */
	protected float ceilingScaleY;
	/** Sector's Floor rotation value. */
	protected float floorRotation;
	/** Sector's Ceiling rotation value. */
	protected float ceilingRotation;
	/** Sector's Floor lighting value. */
	protected int floorLighting;
	/** Sector's Ceiling lighting value. */
	protected int ceilingLighting;
	/** Is Floor lighting absolute? */
	protected boolean floorLightingAbsolute;
	/** Is Ceiling lighting absolute? */
	protected boolean ceilingLightingAbsolute;
	/** Sector's gravity. */
	protected float gravity;
	/** Sector's light color. */
	protected Color lightColor;
	/** Sector's fade color. */
	protected Color fadeColor;
	/** Sector's color desaturation. */
	protected float desaturation;
	/** Is sector silent? */
	protected boolean silent;
	/** Does the sector protect vs. falling damage? */
	protected boolean noFallingDamage;
	/** Does the sector drop actors if it falls? */
	protected boolean dropsActors;
	/** Can the player not respawn here? */
	protected boolean noRespawn;
	
	/**
	 * Creates a new Sector. 
	 */
	public Sector()
	{
		reset();
	}

	/**
	 * Resets this object's data to its defaults.
	 * Note that it may not necessarily set the object's data to valid information.
	 */
	public void reset()
	{
		floorHeight = 0;
		ceilingHeight = 0;
		floorTex = "";
		ceilingTex = "";
		lightLev = 160;
		special = 0;
		id = 0;
		comment = "";
		
		floorOffsetX = 0.0f;
		floorOffsetY = 0.0f;
		ceilingOffsetX = 0.0f;
		ceilingOffsetY = 0.0f;
		floorScaleX = 1.0f;
		floorScaleY = 1.0f;
		ceilingScaleX = 1.0f;
		ceilingScaleY = 1.0f;
		floorRotation = 0.0f;
		ceilingRotation = 0.0f;
		floorLighting = 0;
		ceilingLighting = 0;
		floorLightingAbsolute = false;
		ceilingLightingAbsolute = false;
		gravity = 1.0f;
		lightColor = Color.WHITE;
		fadeColor = Color.BLACK;
		desaturation = 0.0f;
		silent = false;
		noFallingDamage = false;
		dropsActors = false;
		noRespawn = false;
	}
	
	/** Gets this Sector's floor height.*/
	public int getFloorHeight()					{return floorHeight;}
	/** Gets this Sector's ceiling height.*/
	public int getCeilingHeight()				{return ceilingHeight;}
	/** Gets this Sector's floor texture.*/
	public String getFloorTexture()				{return floorTex;}
	/** Gets this Sector's ceiling texture.*/
	public String getCeilingTexture()			{return ceilingTex;}
	/** Gets this Sector's light level.*/
	public int getLightLevel()					{return lightLev;}
	/** Gets this Sector's special.*/
	public int getSpecial()						{return special;}
	/** Gets this Sector's tag.*/
	public int getTag()							{return id;}
	
	/** Sets this Sector's floor height.*/
	public void setFloorHeight(int val)			{floorHeight = val;}
	/** Sets this Sector's ceiling height.*/
	public void setCeilingHeight(int val)		{ceilingHeight = val;}
	/** Sets this Sector's floor texture.*/
	public void setFloorTexture(String val)		{floorTex = val;}
	/** Sets this Sector's ceiling texture.*/
	public void setCeilingTexture(String val)	{ceilingTex = val;}
	/** Sets this Sector's light level.*/
	public void setLightLevel(int val)			{lightLev = val;}
	/** Sets this Sector's special.*/
	public void setSpecial(int val)				{special = val;}
	/** Sets this Sector's tag.*/
	public void setTag(int val)					{id = val;}
	
	/** Gets the Sector's Floor panning value (X axis). */
	public float getFloorOffsetX()
	{
		return floorOffsetX;
	}

	/** Sets the Sector's Floor panning value (X axis). */
	public void setFloorOffsetX(float floorOffsetX)
	{
		this.floorOffsetX = floorOffsetX;
	}

	/** Gets the Sector's Floor panning value (Y axis). */
	public float getFloorOffsetY()
	{
		return floorOffsetY;
	}

	/** Sets the Sector's Floor panning value (Y axis). */
	public void setFloorOffsetY(float floorOffsetY)
	{
		this.floorOffsetY = floorOffsetY;
	}

	/** Gets the Sector's Ceiling panning value (X axis). */
	public float getCeilingOffsetX()
	{
		return ceilingOffsetX;
	}

	/** Sets the Sector's Ceiling panning value (X axis). */
	public void setCeilingOffsetX(float ceilingOffsetX)
	{
		this.ceilingOffsetX = ceilingOffsetX;
	}

	/** Gets the Sector's Ceiling panning value (Y axis). */
	public float getCeilingOffsetY()
	{
		return ceilingOffsetY;
	}

	/** Sets the Sector's Ceiling panning value (Y axis). */
	public void setCeilingOffsetY(float ceilingOffsetY)
	{
		this.ceilingOffsetY = ceilingOffsetY;
	}

	/** Gets the Sector's Floor scale value (X axis). */
	public float getFloorScaleX()
	{
		return floorScaleX;
	}

	/** Sets the Sector's Floor scale value (X axis). */
	public void setFloorScaleX(float floorScaleX)
	{
		this.floorScaleX = floorScaleX;
	}

	/** Gets the Sector's Floor scale value (Y axis). */
	public float getFloorScaleY()
	{
		return floorScaleY;
	}

	/** Sets the Sector's Floor scale value (Y axis). */
	public void setFloorScaleY(float floorScaleY)
	{
		this.floorScaleY = floorScaleY;
	}

	/** Gets the Sector's Ceiling scale value (X axis). */
	public float getCeilingScaleX()
	{
		return ceilingScaleX;
	}

	/** Sets the Sector's Ceiling scale value (X axis). */
	public void setCeilingScaleX(float ceilingScaleX)
	{
		this.ceilingScaleX = ceilingScaleX;
	}

	/** Gets the Sector's Ceiling scale value (Y axis). */
	public float getCeilingScaleY()
	{
		return ceilingScaleY;
	}

	/** Sets the Sector's Ceiling scale value (Y axis). */
	public void setCeilingScaleY(float ceilingScaleY)
	{
		this.ceilingScaleY = ceilingScaleY;
	}

	/** Gets the Sector's Floor rotation value. */
	public float getFloorRotation()
	{
		return floorRotation;
	}

	/** Sets the Sector's Floor rotation value. */
	public void setFloorRotation(float floorRotation)
	{
		this.floorRotation = floorRotation;
	}

	/** Gets the Sector's Ceiling rotation value. */
	public float getCeilingRotation()
	{
		return ceilingRotation;
	}

	/** Sets the Sector's Ceiling rotation value. */
	public void setCeilingRotation(float ceilingRotation)
	{
		this.ceilingRotation = ceilingRotation;
	}

	/** Gets the Sector's Floor lighting value. */
	public int getFloorLighting()
	{
		return floorLighting;
	}

	/** Sets the Sector's Floor lighting value. */
	public void setFloorLighting(int floorLighting)
	{
		this.floorLighting = floorLighting;
	}

	/** Gets the Sector's Ceiling lighting value. */
	public int getCeilingLighting()
	{
		return ceilingLighting;
	}

	/** Sets the Sector's Ceiling lighting value. */
	public void setCeilingLighting(int ceilingLighting)
	{
		this.ceilingLighting = ceilingLighting;
	}

	/** Gets if this Sector's floor's lighting is absolute. */
	public boolean isFloorLightingAbsolute()
	{
		return floorLightingAbsolute;
	}

	/** Sets if this Sector's floor's lighting is absolute. */
	public void setFloorLightingAbsolute(boolean floorLightingAbsolute)
	{
		this.floorLightingAbsolute = floorLightingAbsolute;
	}

	/** Gets if this Sector's ceiling's lighting is absolute. */
	public boolean isCeilingLightingAbsolute()
	{
		return ceilingLightingAbsolute;
	}

	/** Sets if this Sector's ceiling's lighting is absolute. */
	public void setCeilingLightingAbsolute(boolean ceilingLightingAbsolute)
	{
		this.ceilingLightingAbsolute = ceilingLightingAbsolute;
	}

	/** Gets the Sector's gravity. */
	public float getGravity()
	{
		return gravity;
	}

	/** Sets the Sector's gravity. */
	public void setGravity(float gravity)
	{
		this.gravity = gravity;
	}

	/** Gets the Sector's light color. */
	public Color getLightColor()
	{
		return lightColor;
	}

	/** Sets the Sector's light color. */
	public void setLightColor(Color lightColor)
	{
		this.lightColor = lightColor;
	}

	/** Gets the Sector's fade color. */
	public Color getFadeColor()
	{
		return fadeColor;
	}

	/** Sets the Sector's fade color. */
	public void setFadeColor(Color fadeColor)
	{
		this.fadeColor = fadeColor;
	}

	/** Gets the Sector's color desaturation. */
	public float getDesaturation()
	{
		return desaturation;
	}

	/** Sets the Sector's color desaturation. */
	public void setDesaturation(float desaturation)
	{
		this.desaturation = desaturation;
	}

	/** Gets if this Sector is silent. */
	public boolean isSilent()
	{
		return silent;
	}

	/** Sets if this Sector is silent. */
	public void setSilent(boolean silent)
	{
		this.silent = silent;
	}

	/** Gets if the sector protects vs. falling damage. */
	public boolean doesNoFallingDamage()
	{
		return noFallingDamage;
	}

	/** Sets if the sector protects vs. falling damage. */
	public void setNoFallingDamage(boolean noFallingDamage)
	{
		this.noFallingDamage = noFallingDamage;
	}

	/** Gets if the sector drops actors if it falls. */
	public boolean dropsActors()
	{
		return dropsActors;
	}

	/** Sets if the sector drops actors if it falls. */
	public void setDropsActors(boolean dropsActors)
	{
		this.dropsActors = dropsActors;
	}

	/** Gets if the player cannot respawn in this sector. */
	public boolean isNoRespawn()
	{
		return noRespawn;
	}

	/** Sets if the player cannot respawn in this sector. */
	public void setNoRespawn(boolean noRespawn)
	{
		this.noRespawn = noRespawn;
	}

	/** Gets this object's comment. */
	public String getComment()					{return comment;}
	/** Gets this object's comment. */
	public void setComment(String val)			{comment = val;}

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
			sw.writeShort((short)(floorHeight & 0x0ffff));
			sw.writeShort((short)(ceilingHeight & 0x0ffff));
			sw.writeASCIIString(DoomUtil.coerceToEntry(floorTex));
			sw.writeASCIIString(DoomUtil.coerceToEntry(ceilingTex));
			sw.writeShort((short)(lightLev & 0x0ffff));
			sw.writeShort((short)(special & 0x0ffff));
			sw.writeShort((short)(id & 0x0ffff));			
		} catch (IOException e){}
		
		return bos.toByteArray();
	}

	/**
	 * Checks this data structure for data export integrity for the Doom format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	protected void callDoomCompatibilityCheck() throws DataExportException
	{
		checkShort("Floor height", floorHeight);
		checkShort("Ceiling height", ceilingHeight);
		checkString("Floor texture", floorTex);
		checkString("Ceiling texture", ceilingTex);
		checkShortUnsigned("Light level", lightLev);
		checkShortUnsigned("Special", special);
		checkShortUnsigned("Id", id);
		
		callZDoomUDMFCheck();
	}

	/**
	 * Checks this object's data for ZDoom-specific UDMF namespace significance.
	 * If it finds that a ZDoom-specific flag is set, it will throw an exception.
	 */
	protected void callZDoomUDMFCheck() throws DataExportException
	{
		checkEqual("Floor Offset X", 0.0f, floorOffsetX);
		checkEqual("Floor Offset Y", 0.0f, floorOffsetY);
		checkEqual("Ceiling Offset X", 0.0f, ceilingOffsetX);
		checkEqual("Ceiling Offset Y", 0.0f, ceilingOffsetY);
		checkEqual("Floor Scale X", 1.0f, floorScaleX);
		checkEqual("Floor Scale Y", 1.0f, floorScaleY);
		checkEqual("Ceiling Scale X", 1.0f, ceilingScaleX);
		checkEqual("Ceiling Scale Y", 1.0f, ceilingScaleY);
		checkEqual("Floor Rotation", 0.0f, floorRotation);
		checkEqual("Ceiling Rotation", 0.0f, ceilingRotation);
		checkZero("Floor Lighting", floorLighting);
		checkZero("Ceiling Lighting", ceilingLighting);
		checkFalse("Floor Lighting Absolute", floorLightingAbsolute);
		checkFalse("Ceiling Lighting Absolute", ceilingLightingAbsolute);
		checkEqual("Gravity", 1.0f, gravity);
		
		if (!lightColor.equals(Color.WHITE))
			throw new DataExportException("Lighting color is NOT white.");
		if (!fadeColor.equals(Color.BLACK))
			throw new DataExportException("Fade color is NOT black.");
		
		checkEqual("Desaturation", 0.0f, desaturation);
		checkFalse("Silent", silent);
		checkFalse("No Falling Damage", noFallingDamage);
		checkFalse("Drops Actors", dropsActors);
		checkFalse("No Respawn", noRespawn);
	}

	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		floorHeight = sr.readShort();
		ceilingHeight = sr.readShort();
		floorTex = DoomUtil.nameFix(sr.readASCIIString(DoomUtil.ENTRY_NAME_SIZE));
		ceilingTex = DoomUtil.nameFix(sr.readASCIIString(DoomUtil.ENTRY_NAME_SIZE));
		lightLev = sr.readShort() & 0x0ffff;
		special = sr.readShort() & 0x0ffff;
		id = sr.readShort() & 0x0ffff;
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
		return 26;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Sector f: ");
		sb.append(floorHeight);
		sb.append(" c: ");
		sb.append(ceilingHeight);
		sb.append(" [");
		sb.append(String.format("%8s, %8s", floorTex, ceilingTex));
		sb.append("] light: ");
		sb.append(lightLev);
		sb.append(" spec: ");
		sb.append(special);
		sb.append(" id: ");
		sb.append(id);
		return sb.toString();
	}

}
