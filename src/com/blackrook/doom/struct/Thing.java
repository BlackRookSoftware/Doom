/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.struct;

import java.io.*;

import com.blackrook.commons.Common;
import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomObject;
import com.blackrook.doom.HexenObject;
import com.blackrook.doom.StrifeObject;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

import static com.blackrook.doom.DoomObjectUtils.*;


/**
 * This class holds Thing information.
 * @author Matthew Tropiano
 */
public class Thing implements DoomObject, HexenObject, StrifeObject
{
	/** Thing angle: east. */
	public static final int ANGLE_EAST = 0;
	
	/** Thing angle: northeast. */
	public static final int ANGLE_NORTHEAST = 45;
	
	/** Thing angle: north. */
	public static final int ANGLE_NORTH = 90;
	
	/** Thing angle: northwest. */
	public static final int ANGLE_NORTHWEST = 135;
	
	/** Thing angle: west. */
	public static final int ANGLE_WEST = 180;
	
	/** Thing angle: southwest. */
	public static final int ANGLE_SOUTHWEST = 225;

	/** Thing angle: south. */
	public static final int ANGLE_SOUTH = 270;

	/** Thing angle: southeast. */
	public static final int ANGLE_SOUTHEAST = 315;

	/** Thing's X-coordinate. */
	protected float xpos;
	/** Thing's Y-coordinate. */
	protected float ypos;
	/** Thing's type (aka Number). */
	protected int type;
	/** Thing's angle. */
	protected int angle;

	/** Thing's ID. */
	protected int id;
	/** Thing's relative Z-coordinate. */
	protected float zpos;
	/** This Thing's special. */
	protected int special;
	/** This Linedef's first special argument. */
	protected int argument0;
	/** This Linedef's second special argument. */
	protected int argument1;
	/** This Linedef's third special argument. */
	protected int argument2;
	/** This Linedef's fourth special argument. */
	protected int argument3;
	/** This Linedef's fifth special argument. */
	protected int argument4;
	
	/** This object's comment. */
	protected String comment;

	/** Thing skill flags. */
	protected boolean appearsOnSkill1;
	/** Thing skill flags. */
	protected boolean appearsOnSkill2;
	/** Thing skill flags. */
	protected boolean appearsOnSkill3;
	/** Thing skill flags. */
	protected boolean appearsOnSkill4;
	/** Thing skill flags. */
	protected boolean appearsOnSkill5;
	/** Thing ambushes the player and does not respond to sound. */
	protected boolean ambusher;
	/** Thing appears in singleplayer. */
	protected boolean appearsOnSinglePlayer;
	/** Thing appears in co-operative. */
	protected boolean appearsOnCooperative;
	/** Thing appears in deathmatch. */
	protected boolean appearsOnDeathmatch;
	/** Thing class appearance flags. */
	protected boolean appearsForClass1;
	/** Thing class appearance flags. */
	protected boolean appearsForClass2;
	/** Thing class appearance flags. */
	protected boolean appearsForClass3;
	/** Thing is friendly. */
	protected boolean friendly;
	/** Thing stands still. */
	protected boolean standing;
	/** Thing is inactive. */
	protected boolean dormant;
	/** Thing is a Strife ally (not to be confused with "friendly"). */
	protected boolean strifeAlly;
	/** Thing is 25% translucent. */
	protected boolean translucent;
	/** Thing is invisible. */
	protected boolean invisible;
	
	/** Thing skill flags. */
	protected boolean appearsOnSkill6;
	/** Thing skill flags. */
	protected boolean appearsOnSkill7;
	/** Thing skill flags. */
	protected boolean appearsOnSkill8;
	/** Thing class appearance flags. */
	protected boolean appearsForClass4;
	/** Thing class appearance flags. */
	protected boolean appearsForClass5;
	/** Thing class appearance flags. */
	protected boolean appearsForClass6;
	/** Thing class appearance flags. */
	protected boolean appearsForClass7;
	/** Thing class appearance flags. */
	protected boolean appearsForClass8;
	
	/**
	 * Creates a new Thing.
	 */
	public Thing()
	{
		reset();
	}

	/**
	 * Resets this object's data to its defaults.
	 * Note that it may not necessarily set the object's data to valid information.
	 */
	public void reset()
	{
		xpos = 0f;
		ypos = 0f;
		angle = 0;
		type = 1;
		id = 0;
		zpos = 0f;
		special = 0;
		argument0 = 0;
		argument1 = 0;
		argument2 = 0;
		argument3 = 0;
		argument4 = 0;
		appearsOnSkill1 = true;
		appearsOnSkill2 = true;
		appearsOnSkill3 = true;
		appearsOnSkill4 = true;
		appearsOnSkill5 = true;
		appearsOnSkill6 = false;
		appearsOnSkill7 = false;
		appearsOnSkill8 = false;
		appearsForClass1 = true;
		appearsForClass2 = true;
		appearsForClass3 = true;
		appearsForClass4 = false;
		appearsForClass5 = false;
		appearsForClass6 = false;
		appearsForClass7 = false;
		appearsForClass8 = false;
		ambusher = false;
	}
	
	/** Gets this Thing's X position.*/
	public float getX()						{return xpos;}
	/** Gets this Thing's Y position.*/
	public float getY()						{return ypos;}
	/** Gets this Thing's angle.*/
	public int getAngle()					{return angle;}
	/** Gets this Thing's Type.*/
	public int getType()					{return type;}
	/** Gets this Thing's id.*/
	public int getId()						{return id;}
	/** Gets this Thing's Z position.*/
	public float getZ()						{return zpos;}
	/** Gets this Thing's special. */
	public int getSpecial()					{return special;}
	/** Gets this Thing's first special argument. */
	public int getArgument0()				{return argument0;}
	/** Gets this Thing's second special argument. */
	public int getArgument1()				{return argument1;}
	/** Gets this Thing's third special argument. */
	public int getArgument2()				{return argument2;}
	/** Gets this Thing's fourth special argument. */
	public int getArgument3()				{return argument3;}
	/** Gets this Thing's fifth special argument. */
	public int getArgument4()				{return argument4;}
	
	/** Sets this Thing's X position.*/
	public void setX(float val)				{xpos = val;}
	/** Sets this Thing's Y position.*/
	public void setY(float val)				{ypos = val;}
	/** Sets this Thing's angle.*/
	public void setAngle(int val)			{angle = val;}
	/** Sets this Thing's Type/ id.*/
	public void setType(int val)			{type = val;}
	/** Sets this Thing's id.*/
	public void setId(int val)				{id = val;}
	/** Sets this Thing's Z position.*/
	public void setZ(float val)				{zpos = val;}
	/** Sets this Thing's special. */
	public void setSpecial(int val)			{special = val;}
	/** Sets this Thing's first special argument. */
	public void setArgument0(int val)		{argument0 = val;}
	/** Sets this Thing's second special argument. */
	public void setArgument1(int val)		{argument1 = val;}
	/** Sets this Thing's third special argument. */
	public void setArgument2(int val)		{argument2 = val;}
	/** Sets this Thing's fourth special argument. */
	public void setArgument3(int val)		{argument3 = val;}
	/** Sets this Thing's fifth special argument. */
	public void setArgument4(int val)		{argument4 = val;}

	/** Gets this object's comment. */
	public String getComment()				{return comment;}
	/** Gets this object's comment. */
	public void setComment(String val)		{comment = val;}

	/** Does this thing appear on skill 1? */
	public boolean getSkill1()
	{
		return appearsOnSkill1;
	}

	/** Does this thing appear on skill 2? */
	public boolean getSkill2()
	{
		return appearsOnSkill2;
	}

	/** Does this thing appear on skill 3? */
	public boolean getSkill3()
	{
		return appearsOnSkill3;
	}

	/** Does this thing appear on skill 4? */
	public boolean getSkill4()
	{
		return appearsOnSkill4;
	}

	/** Does this thing appear on skill 5? */
	public boolean getSkill5()
	{
		return appearsOnSkill5;
	}

	/** Does this thing appear on skill 6? */
	public boolean getSkill6()
	{
		return appearsOnSkill6;
	}

	/** Does this thing appear on skill 7? */
	public boolean getSkill7()
	{
		return appearsOnSkill7;
	}

	/** Does this thing appear on skill 8? */
	public boolean getSkill8()
	{
		return appearsOnSkill8;
	}

	/** Does this thing appear on easy (Skill 1 OR Skill 2)? */
	public boolean appearsOnEasy()
	{
		return appearsOnSkill1 || appearsOnSkill2;
	}

	/** Does this thing appear on normal (Skill 3)? */
	public boolean appearsOnNormal()
	{
		return appearsOnSkill3;
	}
	
	/** Does this thing appear on hard (Skill 4 OR Skill 5)? */
	public boolean appearsOnHard()
	{
		return appearsOnSkill4 || appearsOnSkill5;
	}
	
	/** Does this thing appear on net games only (deathmatch and co-op, but NOT single player)? */
	public boolean appearsOnNetOnly()
	{
		return !appearsOnSinglePlayer && appearsOnCooperative && appearsOnDeathmatch;
	}
	
	/** Does this thing appear in single player mode? */
	public boolean appearsOnSinglePlayer()
	{
		return appearsOnSinglePlayer;
	}

	/** Does this thing appear in cooperative mode? */
	public boolean appearsOnCooperative()
	{
		return appearsOnCooperative;
	}

	/** Does this thing appear in deathmatch mode? */
	public boolean appearsOnDeathmatch()
	{
		return appearsOnDeathmatch;
	}

	/** Is this thing a friendly thing? */
	public boolean isFriendly()
	{
		return friendly;
	}
	
	/** Is this thing an ambusher? */
	public boolean isAmbusher()
	{
		return ambusher;
	}
	
	/** Does this thing stand still? */
	public boolean standsStill()
	{
		return standing;
	}
	
	/** Is this thing a Strife ally? */
	public boolean isStrifeAlly()
	{
		return strifeAlly;
	}
	
	/** Is this thing 25% translucent? */
	public boolean isTranslucent()
	{
		return translucent;
	}
	
	/** Is this thing invisible? */
	public boolean isInvisible()
	{
		return invisible;
	}
	
	/** Is this thing a dormant monster? */
	public boolean isDormant()
	{
		return dormant;
	}
	
	/** Does this thing appear if a class 1 player is present? */
	public boolean appearsForClass1()
	{
		return appearsForClass1;
	}
	
	/** Does this thing appear if a class 2 player is present? */
	public boolean appearsForClass2()
	{
		return appearsForClass2;
	}
	
	/** Does this thing appear if a class 3 player is present? */
	public boolean appearsForClass3()
	{
		return appearsForClass3;
	}
	
	/** Does this thing appear if a class 4 player is present? */
	public boolean appearsForClass4()
	{
		return appearsForClass4;
	}
	
	/** Does this thing appear if a class 5 player is present? */
	public boolean appearsForClass5()
	{
		return appearsForClass5;
	}
	
	/** Does this thing appear if a class 6 player is present? */
	public boolean appearsForClass6()
	{
		return appearsForClass6;
	}
	
	/** Does this thing appear if a class 7 player is present? */
	public boolean appearsForClass7()
	{
		return appearsForClass7;
	}
	
	/** Does this thing appear if a class 8 player is present? */
	public boolean appearsForClass8()
	{
		return appearsForClass8;
	}
	
	/** Does this thing appear if a Fighter class player is present (same as appearsForClass1())? */
	public boolean appearsForFighters()
	{
		return appearsForClass1;
	}
	
	/** Does this thing appear if a Cleric class player is present (same as appearsForClass2())? */
	public boolean appearsForClerics()
	{
		return appearsForClass2;
	}
	
	/** Does this thing appear if a Mage class player is present (same as appearsForClass3())? */
	public boolean appearsForMages()
	{
		return appearsForClass3;
	}
	
	/** Sets if this Thing appears on skill 1? */
	public void setSkill1(boolean flag)
	{
		appearsOnSkill1 = flag;
	}

	/** Sets if this Thing appears on skill 2? */
	public void setSkill2(boolean flag)
	{
		appearsOnSkill2 = flag;
	}

	/** Sets if this Thing appears on skill 3? */
	public void setSkill3(boolean flag)
	{
		appearsOnSkill3 = flag;
	}

	/** Sets if this Thing appears on skill 4? */
	public void setSkill4(boolean flag)
	{
		appearsOnSkill4 = flag;
	}

	/** Sets if this Thing appears on skill 5? */
	public void setSkill5(boolean flag)
	{
		appearsOnSkill5 = flag;
	}

	/** Sets if this Thing appears on skill 6? */
	public void setSkill6(boolean flag)
	{
		appearsOnSkill6 = flag;
	}

	/** Sets if this Thing appears on skill 7? */
	public void setSkill7(boolean flag)
	{
		appearsOnSkill7 = flag;
	}

	/** Sets if this Thing appears on skill 8? */
	public void setSkill8(boolean flag)
	{
		appearsOnSkill8 = flag;
	}

	/** Sets if this Thing appears on easy? */
	public void setAppearsOnEasy(boolean flag)
	{
		setSkill1(flag);
		setSkill2(flag);
	}

	/** Sets if this Thing appears on normal? */
	public void setAppearsOnNormal(boolean flag)
	{
		setSkill3(flag);
	}
	
	/** Sets if this Thing appears on hard? */
	public void setAppearsOnHard(boolean flag)
	{
		setSkill4(flag);
		setSkill5(flag);
	}
	
	/** Sets if this Thing appears on net games only? */
	public void setAppearsOnNetOnly(boolean flag)
	{
		if (flag)
		{
			setAppearsOnSinglePlayer(false);
			setAppearsOnCooperative(true);
			setAppearsOnDeathmatch(true);
		}
		else
		{
			setAppearsOnSinglePlayer(true);
			setAppearsOnCooperative(true);
			setAppearsOnDeathmatch(true);
		}
	}
	
	/** Sets if this thing does not appear in deathmatch? */
	public void setDoesNotAppearOnDeathmatch(boolean flag)
	{
		setAppearsOnDeathmatch(!flag);
	}
	
	/** Sets if this thing does not appear in cooperative? */
	public void setDoesNotAppearOnCooperative(boolean flag)
	{
		setAppearsOnCooperative(!flag);
	}
	
	/** Sets if this thing is a friendly thing? */
	public void setFriendly(boolean flag)
	{
		friendly = flag;
	}
	
	/** Sets if this thing is an ambusher? */
	public void setAmbusher(boolean flag)
	{
		ambusher = flag;
	}
	
	/** Sets if this thing stand still? */
	public void setStandsStill(boolean flag)
	{
		standing = flag;
	}
	
	/** Sets if this thing is an allied thing? */
	public void setStrifeAlly(boolean flag)
	{
		strifeAlly = flag;
	}
	
	/** Sets if this thing is 25% translucent? */
	public void setTranslucent(boolean flag)
	{
		translucent = flag;
	}
	
	/** Sets if this thing is invisible? */
	public void setInvisible(boolean flag)
	{
		invisible = flag;
	}
	
	/** Sets if this thing is a dormant monster (NOTE: This uses the same flag setting as "net", non-Hexen)? */
	public void setDormant(boolean flag)
	{
		dormant = flag;
	}
	
	/** Sets if this Thing appears in single player mode? */
	public void setAppearsOnSinglePlayer(boolean flag)
	{
		appearsOnSinglePlayer = flag;
	}
	
	/** Sets if this Thing appears in cooperative mode? */
	public void setAppearsOnCooperative(boolean flag)
	{
		appearsOnCooperative = flag;
	}
	
	/** Sets if this Thing appears in deathmatch mode? */
	public void setAppearsOnDeathmatch(boolean flag)
	{
		appearsOnDeathmatch = flag;
	}
	
	/** Sets if this Thing appears if a Fighter class player is present? */
	public void setAppearsForFighters(boolean flag)
	{
		setClass1(flag);
	}
	
	/** Sets if this Thing appears if a Cleric class player is present? */
	public void setAppearsForClerics(boolean flag)
	{
		setClass2(flag);
	}
	
	/** Sets if this Thing appears if a Mage class player is present? */
	public void setAppearsForMages(boolean flag)
	{
		setClass3(flag);
	}
	
	/** Sets if this Thing appears for class 1? */
	public void setClass1(boolean flag)
	{
		appearsForClass1 = flag;
	}
	
	/** Sets if this Thing appears for class 2? */
	public void setClass2(boolean flag)
	{
		appearsForClass2 = flag;
	}
	
	/** Sets if this Thing appears for class 3? */
	public void setClass3(boolean flag)
	{
		appearsForClass3 = flag;
	}
	
	/** Sets if this Thing appears for class 4? */
	public void setClass4(boolean flag)
	{
		appearsForClass4 = flag;
	}
	
	/** Sets if this Thing appears for class 5? */
	public void setClass5(boolean flag)
	{
		appearsForClass5 = flag;
	}
	
	/** Sets if this Thing appears for class 6? */
	public void setClass6(boolean flag)
	{
		appearsForClass6 = flag;
	}
	
	/** Sets if this Thing appears for class 7? */
	public void setClass7(boolean flag)
	{
		appearsForClass7 = flag;
	}
	
	/** Sets if this Thing appears for class 8? */
	public void setClass8(boolean flag)
	{
		appearsForClass8 = flag;
	}
	
	/**
	 * Returns the booleans in this object as a set of bitflags.
	 * Note that some flags are not convertible, as they may not
	 * have adequate representation in a serialized Doom data structure.
	 * <p>Uses the following functions for flags:</p>
	 * <ul>
	 * <li>appearsOnEasy()</li> 
	 * <li>appearsOnNormal()</li> 
	 * <li>appearsOnHard()</li>
	 * <li>isAmbusher()</li>
	 * <li>!appearsOnNetOnly()</li>
	 * <li>!appearsOnCooperative()</li>
	 * <li>!appearsOnDeathmatch()</li>
	 * <li>isFriendly()</li>
	 * </ul>
	 */
	public short getDoomBitFlags()
	{
		int flags = Common.booleansToInt(
				appearsOnEasy(), 
				appearsOnNormal(), 
				appearsOnHard(),
				isAmbusher(),
				!appearsOnSinglePlayer(),
				!appearsOnCooperative(),
				!appearsOnDeathmatch(),
				isFriendly()
				);
		return (short)(flags & 0x0ffff);
	}
	
	/**
	 * Returns the booleans in this object as a set of bitflags.
	 * Note that some flags are not convertible, as they may not
	 * have adequate representation in a serialized Doom data structure.
	 * <p>Uses the following functions for flags:</p>
	 * <ul>
	 * <li>appearsOnEasy()</li> 
	 * <li>appearsOnNormal()</li> 
	 * <li>appearsOnHard()</li>
	 * <li>isAmbusher()</li>
	 * <li>isDormant()</li>
	 * <li>appearsForFighters()</li>
	 * <li>appearsForClerics()</li>
	 * <li>appearsForMages()</li>
	 * <li>appearsOnSinglePlayer()</li>
	 * <li>appearsOnCooperative()</li>
	 * <li>appearsOnDeathmatch()</li>
	 * <li>isTranslucent()</li>
	 * <li>isInvisible()</li>
	 * <li>isFriendly()</li>
	 * <li>standsStill()</li>
	 * </ul>
	 */
	public short getHexenBitFlags()
	{
		int flags = Common.booleansToInt(
				appearsOnEasy(), 
				appearsOnNormal(), 
				appearsOnHard(),
				isAmbusher(),
				isDormant(),
				appearsForFighters(),
				appearsForClerics(),
				appearsForMages(),
				appearsOnSinglePlayer(),
				appearsOnCooperative(),
				appearsOnDeathmatch(),
				isTranslucent(),
				isInvisible(),
				isFriendly(),
				standsStill()
				);
		return (short)(flags & 0x0ffff);
	}
	
	/**
	 * Returns the booleans in this object as a set of bitflags.
	 * Note that some flags are not convertible, as they may not
	 * have adequate representation in a serialized Doom data structure.
	 * <p>Uses the following functions for flags:</p>
	 * <ul>
	 * <li>appearsOnEasy()</li> 
	 * <li>appearsOnNormal()</li> 
	 * <li>appearsOnHard()</li>
	 * <li>standsStill()</li>
	 * <li>!appearsOnSinglePlayer()</li>
	 * <li>isAmbusher()</li>
	 * <li>isStrifeAlly()</li>
	 * <li>isTranslucent()</li>
	 * <li>isInvisible()</li>
	 * </ul>
	 */
	public short getStrifeBitFlags()
	{
		int flags = Common.booleansToInt(
				appearsOnEasy(), 
				appearsOnNormal(), 
				appearsOnHard(),
				standsStill(),
				!appearsOnSinglePlayer(),
				isAmbusher(),
				Boolean.FALSE,
				isStrifeAlly(),
				isTranslucent(),
				isInvisible()
				);
		return (short)(flags & 0x0ffff);
	}

	/**
	 * Sets the booleans on this object from a set of bitflags.
	 * Note that some booleans are not convertible, as they may not
	 * have adequate representation in a serialized Doom data structure.
	 */
	public void setDoomBitFlags(short flags)
	{
		setAppearsOnEasy(Common.bitIsSet(flags, (1 << 0)));
		setAppearsOnNormal(Common.bitIsSet(flags, (1 << 1)));
		setAppearsOnHard(Common.bitIsSet(flags, (1 << 2)));
		setAmbusher(Common.bitIsSet(flags, (1 << 3)));
		setAppearsOnSinglePlayer(!Common.bitIsSet(flags, (1 << 4)));
		setAppearsOnDeathmatch(!Common.bitIsSet(flags, (1 << 5)));
		setAppearsOnCooperative(!Common.bitIsSet(flags, (1 << 6)));
		setFriendly(Common.bitIsSet(flags, (1 << 7)));
		setClass1(false);
		setClass2(false);
		setClass3(false);
		setClass4(false);
		setClass5(false);
		setClass6(false);
		setClass7(false);
		setClass8(false);
	}
	
	/**
	 * Sets the booleans on this object from a set of bitflags.
	 * Note that some booleans are not convertible, as they may not
	 * have adequate representation in a serialized Doom data structure.
	 */
	public void setHexenBitFlags(short flags)
	{
		setAppearsOnEasy(Common.bitIsSet(flags, (1 << 0)));
		setAppearsOnNormal(Common.bitIsSet(flags, (1 << 1)));
		setAppearsOnHard(Common.bitIsSet(flags, (1 << 2)));
		setAmbusher(Common.bitIsSet(flags, (1 << 3)));
		setDormant(Common.bitIsSet(flags, (1 << 4)));
		setAppearsForFighters(Common.bitIsSet(flags, (1 << 5)));
		setAppearsForClerics(Common.bitIsSet(flags, (1 << 6)));
		setAppearsForMages(Common.bitIsSet(flags, (1 << 7)));
		setAppearsOnSinglePlayer(Common.bitIsSet(flags, (1 << 8)));
		setAppearsOnCooperative(Common.bitIsSet(flags, (1 << 9)));
		setAppearsOnDeathmatch(Common.bitIsSet(flags, (1 << 10)));
		setTranslucent(Common.bitIsSet(flags, (1 << 11)));
		setInvisible(Common.bitIsSet(flags, (1 << 12)));
		setFriendly(Common.bitIsSet(flags, (1 << 13)));
		setStandsStill(Common.bitIsSet(flags, (1 << 14)));

		setClass4(false);
		setClass5(false);
		setClass6(false);
		setClass7(false);
		setClass8(false);
	}
	
	/**
	 * Sets the booleans on this object from a set of bitflags.
	 * Note that some booleans are not convertible, as they may not
	 * have adequate representation in a serialized Doom data structure.
	 */
	public void setStrifeBitFlags(short flags)
	{
		setAppearsOnEasy(Common.bitIsSet(flags, (1 << 0)));
		setAppearsOnNormal(Common.bitIsSet(flags, (1 << 1)));
		setAppearsOnHard(Common.bitIsSet(flags, (1 << 2)));
		setStandsStill(Common.bitIsSet(flags, (1 << 3)));
		setAppearsOnSinglePlayer(!Common.bitIsSet(flags, (1 << 4)));
		setAppearsOnCooperative(true);
		setAppearsOnDeathmatch(true);
		setAmbusher(Common.bitIsSet(flags, (1 << 5)));
		
		setStrifeAlly(Common.bitIsSet(flags, (1 << 7)));
		setTranslucent(Common.bitIsSet(flags, (1 << 8)));
		setInvisible(Common.bitIsSet(flags, (1 << 9)));

		setClass1(false);
		setClass2(false);
		setClass3(false);
		setClass4(false);
		setClass5(false);
		setClass6(false);
		setClass7(false);
		setClass8(false);
	}
	
	@Override
	public byte[] getDoomBytes() throws DataExportException
	{
		callDoomCompatibilityCheck();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
		try{
			sw.writeShort((short)((int)xpos));
			sw.writeShort((short)((int)ypos));
			sw.writeShort((short)(((int)angle) & 0x0ffff));
			sw.writeShort((short)(((int)type) & 0x0ffff));
			sw.writeShort(getDoomBitFlags());
		} catch (IOException e){}
		
		return bos.toByteArray();
	}

	@Override
	public byte[] getHexenBytes() throws DataExportException
	{
		callHexenCompatibilityCheck();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
		try{
			sw.writeShort((short)(((int)id) & 0x0ffff));
			sw.writeShort((short)((int)xpos));
			sw.writeShort((short)((int)ypos));
			sw.writeShort((short)((int)zpos));
			sw.writeShort((short)(((int)angle) & 0x0ffff));
			sw.writeShort((short)(((int)type) & 0x0ffff));
			sw.writeShort(getHexenBitFlags());
			sw.writeByte((byte)(special & 0x0ff));
			sw.writeByte((byte)(argument0 & 0x0ff));
			sw.writeByte((byte)(argument1 & 0x0ff));
			sw.writeByte((byte)(argument2 & 0x0ff));
			sw.writeByte((byte)(argument3 & 0x0ff));
			sw.writeByte((byte)(argument4 & 0x0ff));
		} catch (IOException e){}
		
		return bos.toByteArray();
	}

	@Override
	public byte[] getStrifeBytes() throws DataExportException
	{
		callStrifeCompatibilityCheck();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
		try{
			sw.writeShort((short)((int)xpos));
			sw.writeShort((short)((int)ypos));
			sw.writeShort((short)(((int)angle) & 0x0ffff));
			sw.writeShort((short)(((int)type) & 0x0ffff));
			sw.writeShort(getStrifeBitFlags());
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

	@Override
	public boolean isHexenCompatible()
	{
		try {
			callHexenCompatibilityCheck();
		} catch (DataExportException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isStrifeCompatible()
	{
		try {
			callStrifeCompatibilityCheck();
		} catch (DataExportException e) {
			return false;
		}
		return true;
	}

	/**
	 * Checks this data structure for data export integrity for the Doom format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	protected void callDoomCompatibilityCheck() throws DataExportException
	{
		checkWhole("X-position", xpos);
		checkShort("X-position", (int)xpos);
		checkWhole("Y-position", ypos);
		checkShort("Y-position", (int)ypos);
		checkShortUnsigned("Angle", angle);
		checkShortUnsigned("Type", type);
		
		checkZero("Id", id);
		checkZero("Z-position", (int)zpos);
		checkZero("Special", special);
		checkZero("Argument 0", argument0);
		checkZero("Argument 1", argument1);
		checkZero("Argument 2", argument2);
		checkZero("Argument 3", argument3);
		checkZero("Argument 4", argument4);
		
		checkFalse("Dormant", dormant);
		checkFalse("Strife Ally", strifeAlly);
		checkFalse("Stands Still", standing);
		checkFalse("Invisible", invisible);
		checkFalse("Translucent", translucent);
		checkFalse("Class 1", appearsForClass1);
		checkFalse("Class 2", appearsForClass2);
		checkFalse("Class 3", appearsForClass3);
		
		callZDoomUDMFCheck();
	}

	/**
	 * Checks this data structure for data export integrity for the ZDoom/Hexen format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	protected void callHexenCompatibilityCheck() throws DataExportException
	{
		checkWhole("X-position", xpos);
		checkShort("X-position", (short)xpos);
		checkWhole("Y-position", ypos);
		checkShort("Y-position", (short)ypos);
		checkWhole("Z-position", zpos);
		checkShort("Z-position", (short)zpos);
		checkShortUnsigned("Angle", angle);
		checkShortUnsigned("Type", type);
		checkShortUnsigned("Id", id);
		checkByteUnsigned("Special", special);
		checkByteUnsigned("Argument 0", argument0);
		checkByteUnsigned("Argument 1", argument1);
		checkByteUnsigned("Argument 2", argument2);
		checkByteUnsigned("Argument 3", argument3);
		checkByteUnsigned("Argument 4", argument4);
		
		checkFalse("Strife Ally", strifeAlly);

		callZDoomUDMFCheck();
	}

	/**
	 * Checks this data structure for data export integrity for the Strife format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	protected void callStrifeCompatibilityCheck() throws DataExportException
	{
		checkWhole("X-position", xpos);
		checkShort("X-position", (int)xpos);
		checkWhole("Y-position", ypos);
		checkShort("Y-position", (int)ypos);
		checkShortUnsigned("Angle", angle);
		checkShortUnsigned("Type", type);
		
		checkZero("Id", id);
		checkZero("Z-position", (int)zpos);
		checkZero("Special", special);
		checkZero("Argument 0", argument0);
		checkZero("Argument 1", argument1);
		checkZero("Argument 2", argument2);
		checkZero("Argument 3", argument3);
		checkZero("Argument 4", argument4);
		
		checkFalse("Dormant", dormant);
		checkFalse("Class 1", appearsForClass1);
		checkFalse("Class 2", appearsForClass2);
		checkFalse("Class 3", appearsForClass3);

		callZDoomUDMFCheck();
	}

	/**
	 * Checks this object's data for ZDoom-specific UDMF namespace significance.
	 * If it finds that a ZDoom-specific flag is set, it will throw an exception.
	 */
	protected void callZDoomUDMFCheck() throws DataExportException
	{
		checkFalse("Class 4", appearsForClass4);
		checkFalse("Class 5", appearsForClass5);
		checkFalse("Class 6", appearsForClass6);
		checkFalse("Class 7", appearsForClass7);
		checkFalse("Class 8", appearsForClass8);
		checkFalse("Skill 6", appearsOnSkill6);
		checkFalse("Skill 7", appearsOnSkill7);
		checkFalse("Skill 8", appearsOnSkill8);
	}
	
	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		xpos = sr.readShort();
		ypos = sr.readShort();
		angle = sr.readShort() & 0x0ffff;
		type = sr.readShort() & 0x0ffff;
		setDoomBitFlags(sr.readShort());
	}

	@Override
	public void readHexenBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		id = sr.readShort() & 0x0ffff;
		xpos = sr.readShort();
		ypos = sr.readShort();
		zpos = sr.readShort();
		angle = sr.readShort() & 0x0ffff;
		type = sr.readShort() & 0x0ffff;
		setHexenBitFlags(sr.readShort());
		special = sr.readByte() & 0x00ff;
		argument0 = sr.readByte() & 0x00ff;
		argument1 = sr.readByte() & 0x00ff;
		argument2 = sr.readByte() & 0x00ff;
		argument3 = sr.readByte() & 0x00ff;
		argument4 = sr.readByte() & 0x00ff;
	}

	@Override
	public void readStrifeBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		xpos = sr.readShort();
		ypos = sr.readShort();
		angle = sr.readShort() & 0x0ffff;
		type = sr.readShort() & 0x0ffff;
		setStrifeBitFlags(sr.readShort());
	}

	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

	@Override
	public void writeHexenBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getHexenBytes());
	}
	
	@Override
	public void writeStrifeBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getStrifeBytes());
	}

	/**
	 * Returns the length of this structure in Doom-formatted bytes. 
	 */
	public static int getDoomLength()
	{
		return 10;
	}

	/**
	 * Returns the length of this structure in ZDoom/Hexen-formatted bytes. 
	 */
	public static int getHexenLength()
	{
		return 20;
	}

	/**
	 * Returns the length of this structure in Strife-formatted bytes. 
	 */
	public static int getStrifeLength()
	{
		return 10;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Thing type: ");
		sb.append(type);
		sb.append(" (");
		sb.append(xpos);
		sb.append(", ");
		sb.append(ypos);
		sb.append(", ");
		sb.append(zpos);
		sb.append(") angle: ");
		sb.append(angle);
		sb.append(" id: ");
		sb.append(id);
		sb.append(" special: ");
		sb.append(special);
		sb.append(String.format("[%d, %d, %d, %d, %d]", 
				argument0, argument1, argument2, argument3, argument4));
		return sb.toString();
	}
	
}
