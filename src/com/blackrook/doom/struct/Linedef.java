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
import com.blackrook.commons.list.List;
import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomObject;
import com.blackrook.doom.HexenObject;
import com.blackrook.doom.StrifeObject;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

import static com.blackrook.doom.DoomObjectUtils.*;

/**
 * This class holds Linedef information.
 * @author Matthew Tropiano
 */
public class Linedef implements DoomObject, HexenObject, StrifeObject
{
	/** Renderstyle: additive blending. */
	public static final String RENDERSTYLE_ADD = "add";
	/** Renderstyle: alpha blending. */
	public static final String RENDERSTYLE_TRANSLUCENT = "translucent";
	
	/** This Linedef's start vertex index reference. */
	protected int vertexStart;
	/** This Linedef's end vertex index reference. */
	protected int vertexEnd;
	/** This Linedef's special. */
	protected int special;
	/** This Linedef's id (tag). */
	protected int id;
	/** This Linedef's front sidedef sector reference. */
	protected int sideFrontRef;
	/** This Linedef's back sidedef sector reference. */
	protected int sideBackRef;
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

	/** Linedef can't be crossed. */
	protected boolean impassable;
	/** Linedef blocks monsters. */
	protected boolean blocksMonsters;
	/** Linedef is Two-Sided. */
	protected boolean twoSided;
	/** Linedef is rendered from the top down. */
	protected boolean upperUnpegged;
	/** Linedef is rendered from the bottom up. */
	protected boolean lowerUnpegged;
	/** Linedef is shown as impassible. */
	protected boolean secret;
	/** Linedef blocks sound. */
	protected boolean blocksSound;
	/** Linedef is never drawn on automap. */
	protected boolean neverDrawn;
	/** Linedef is always drawn on the automap. */
	protected boolean alwaysDrawn;

	/** Linedef does not terminate the "use" algorithm here. */
	protected boolean passThru;
	
	/** Linedef has a repeatable special (flag, not based on special). */
	protected boolean repeatable;
	
	/** Activation flag: Player crosses line. */
	protected boolean playerCross;
	/** Activation flag: Player uses line. */
	protected boolean playerUse;
	/** Activation flag: Monster crosses line. */
	protected boolean monsterCross;
	/** Activation flag: Monster uses line. */
	protected boolean monsterUse;
	/** Activation flag: Missile hits line. */
	protected boolean missileImpact;
	/** Activation flag: Player pushes line. */
	protected boolean playerPush;
	/** Activation flag: Monster pushes line. */
	protected boolean monsterPush;
	/** Activation flag: Missile crosses line. */
	protected boolean missileCross;
	
	/** Linedef is a railing. */
	protected boolean railing;
	/** Linedef blocks floaters. */
	protected boolean blockFloaters;
	/** Linedef clips the middle texture. */
	protected boolean clipMiddleTexture;
	/** Linedef is translucent. */
	protected boolean translucent;
	
	/** Line's translucency factor. */
	protected float alpha;
	/** Rendering style for the line. */
	protected String renderStyle;
	/** Activation flag: Anything non-projectile crosses line. */
	protected boolean anyCross;
	/** Activation flag: Monsters can activate line. */
	protected boolean monsterActivate;
	/** Linedef blocks players. */
	protected boolean blocksPlayers;
	/** Linedef blocks all hitscan attacks, monsters, players, and projectiles. */
	protected boolean blocksAll;
	/** Linedef activates on its first side only. */
	protected boolean firstSideOnly;
	/** Linedef is a boundary for reverb zones. */
	protected boolean zoneBoundary;
	/** Linedef wraps the middle texture, no matter what. */
	protected boolean wrapMiddleTexture;
	/** Linedef has 3D middle textures: things can be supported by them. */
	protected boolean middleTexture3D;
	/** Linedef's switch activation can only be activated on their surface; must be vertically reachable. */
	protected boolean checkSwitchRange;
	/** Linedef blocks projectiles. */
	protected boolean blocksProjectiles;
	/** Linedef blocks "use" checks. */
	protected boolean blocksUse;


	/**
	* Creates a new Linedef.
	*/
	public Linedef()
	{
		reset();
	}
	
	/**
	 * Resets this object's data to its defaults.
	 * Note that it may not necessarily set the object's data to valid information.
	 */
	public void reset()
	{
		vertexStart = -1;
		vertexEnd = -1;
		special = 0;
		id = 0;
		sideFrontRef = -1;
		sideBackRef = -1;
		argument0 = 0;
		argument1 = 0;
		argument2 = 0;
		argument3 = 0;
		argument4 = 0;
		
		comment = "";
		impassable = false;
		blocksMonsters = false;
		twoSided = false;
		upperUnpegged = false;
		lowerUnpegged = false;
		secret = false;
		blocksSound = false;
		neverDrawn = false;
		alwaysDrawn = false;
		passThru = false;
		repeatable = false;
		playerCross = true;
		playerUse = false;
		monsterCross = false;
		monsterUse = false;
		missileImpact = false;
		playerPush = false;
		monsterPush = false;
		missileCross = false;
		railing = false;
		blockFloaters = false;
		clipMiddleTexture = false;
		translucent = false;
		
		alpha = 1;
		renderStyle = RENDERSTYLE_TRANSLUCENT;

		anyCross = false;
		monsterActivate = false;
		blocksPlayers = false;
		blocksAll = false;
		firstSideOnly = false;
		zoneBoundary = false;
		wrapMiddleTexture = false;
		middleTexture3D = false;
		checkSwitchRange = false;
		blocksProjectiles = false;
		blocksUse = false;
	}

	/** Gets this Linedef's starting vertex reference. */
	public int getVertexStart()				{return vertexStart;}
	/** Gets this Linedef's ending vertex reference. */
	public int getVertexEnd()				{return vertexEnd;}
	/** Gets this Linedef's special. */
	public int getSpecial()					{return special;}
	/** Sets this Linedef's first special argument. */
	public int getArgument0()				{return argument0;}
	/** Sets this Linedef's second special argument. */
	public int getArgument1()				{return argument1;}
	/** Sets this Linedef's third special argument. */
	public int getArgument2()				{return argument2;}
	/** Sets this Linedef's fourth special argument. */
	public int getArgument3()				{return argument3;}
	/** Sets this Linedef's fifth special argument. */
	public int getArgument4()				{return argument4;}
	/** Gets this Linedef's id/tag. */
	public int getId()						{return id;}
	/** Gets this Linedef's front sidedef reference. */
	public int getFrontSidedef()			{return sideFrontRef;}
	/** Gets this Linedef's back sidedef reference. */
	public int getBackSidedef()				{return sideBackRef;}
	
	/** Sets this Linedef's starting vertex reference. */
	public void setVertexStart(int val)		{vertexStart = val;}
	/** Sets this Linedef's ending vertex reference. */
	public void setVertexEnd(int val)		{vertexEnd = val;}
	/** Sets this Linedef's special. */
	public void setSpecial(int val)			{special = val;}
	/** Sets this Linedef's first special argument. */
	public void setArgument0(int val)		{argument0 = val;}
	/** Sets this Linedef's second special argument. */
	public void setArgument1(int val)		{argument1 = val;}
	/** Sets this Linedef's third special argument. */
	public void setArgument2(int val)		{argument2 = val;}
	/** Sets this Linedef's fourth special argument. */
	public void setArgument3(int val)		{argument3 = val;}
	/** Sets this Linedef's fifth special argument. */
	public void setArgument4(int val)		{argument4 = val;}
	/** Sets this Linedef's id/tag. */
	public void setId(int val)				{id = val;}
	/** Sets this Linedef's front sidedef reference. */
	public void setFrontSidedef(int val)	{sideFrontRef = val;}
	/** Sets this Linedef's back sidedef reference. */
	public void setBackSidedef(int val)		{sideBackRef = val;}
	
	/** Gets this object's comment. */
	public String getComment()				{return comment;}
	/** Gets this object's comment. */
	public void setComment(String val)		{comment = val;}

	/** Does the "use" key pass through this line, potentially triggering more than one special? */
	public boolean isPassThru()
	{
		return passThru;
	}

	/** Should the "use" key pass through this line, and potentially trigger more than one special? */
	public void setPassThru(boolean flag)
	{
		this.passThru = flag;
	}

	/** Does this line activate by a player crossing it? */
	public boolean activatesByPlayerCross()
	{
		return playerCross;
	}

	/** Should this line activate by a player crossing it? */
	public void setActivatesByPlayerCross(boolean flag)
	{
		this.playerCross = flag;
	}

	/** Does this line activate by a player using it? */
	public boolean activatesByPlayerUse()
	{
		return playerUse;
	}

	/** Should this line activate by a player using it? */
	public void setActivatesByPlayerUse(boolean flag)
	{
		this.playerUse = flag;
	}

	/** Does this line activate by a monster crossing it? */
	public boolean activatesByMonsterCross()
	{
		return monsterCross;
	}

	/** Should this line activate by a monster crossing it? */
	public void setActivatesByMonsterCross(boolean monsterCross)
	{
		this.monsterCross = monsterCross;
	}

	/** Does this line activate by a monster using it? */
	public boolean activatesByMonsterUse()
	{
		return monsterUse;
	}

	/** Should this line activate by a monster using it? */
	public void setActivatesByMonsterUse(boolean monsterUse)
	{
		this.monsterUse = monsterUse;
	}

	/** Does this line activate by a missile hits it? */
	public boolean activatesByMissileImpact()
	{
		return missileImpact;
	}

	/** Should this line activate by a missile hits it? */
	public void setActivatesByMissileImpact(boolean missileImpact)
	{
		this.missileImpact = missileImpact;
	}

	/** Does this line activate by a player pushing it? */
	public boolean activatesByPlayerPush()
	{
		return playerPush;
	}

	/** Should this line activate by a player pushing it? */
	public void setActivatesByPlayerPush(boolean playerPush)
	{
		this.playerPush = playerPush;
	}

	/** Does this line activate by a monster pushing it? */
	public boolean activatesByMonsterPush()
	{
		return monsterPush;
	}

	/** Should this line activate by a monster pushing it? */
	public void setActivatesByMonsterPush(boolean monsterPush)
	{
		this.monsterPush = monsterPush;
	}

	/** Does this line activate by a missile crossing it? */
	public boolean activatesByMissileCross()
	{
		return missileCross;
	}

	/** Should this line activate by a missile crossing it? */
	public void setActivatesByMissileCross(boolean missileCross)
	{
		this.missileCross = missileCross;
	}

	/** Is this line be activatable by monster? */
	public boolean activatableByMonster()
	{
		return monsterActivate;
	}

	/** Should this line be activatable by monster? */
	public void setActivatableByMonster(boolean flag)
	{
		this.monsterActivate = flag;
	}

	/** Is this line impassable? */
	public boolean isImpassable()
	{
		return impassable;
	}

	/** Does this line collide with monsters? */
	public boolean blocksMonsters()
	{
		return blocksMonsters;
	}

	/** Is this line two-sided? */
	public boolean isTwoSided()
	{
		return twoSided;
	}

	/** Is this line upper-unpegged (upper texture is drawn from the top, down)? */
	public boolean isUpperUnpegged()
	{
		return upperUnpegged;
	}
	
	/** Is this line lower-unpegged (lower texture is drawn from the bottom, up)? */
	public boolean isLowerUnpegged()
	{
		return lowerUnpegged;
	}

	/** Is this line drawn as a one-sided line in the automap? */
	public boolean isSecret()
	{
		return secret;
	}
	
	/** Does this line block sound (sound must cross two of these lines to stop the sound)? */
	public boolean blocksSound()
	{
		return blocksSound;
	}

	/** Is this line never drawn on the automap? */
	public boolean isNeverDrawn()
	{
		return neverDrawn;
	}

	/** Is this line always drawn on the automap? */
	public boolean isAlwaysDrawn()
	{
		return alwaysDrawn;
	}

	/** Does line use passthru logic (Boom)? */
	public boolean isBoomPassThru()
	{
		return passThru;
	}

	/** Does this line have a repeatable special (ZDoom/Hexen flag, does not evaluate special)? */
	public boolean isRepeatable()
	{
		return repeatable;
	}

	/** Does this line block everything (including hitscan attacks on two-sided lines, but except sound) (ZDoom)? */
	public boolean blocksAll()
	{
		return blocksAll;
	}

	/** Sets if this line is impassable. */
	public void setImpassable(boolean flag)
	{
		impassable = flag;
	}

	/** Sets if this line collides with monsters. */
	public void setBlocksMonsters(boolean flag)
	{
		blocksMonsters = flag;
	}

	/** Sets if this line is two-sided. */
	public void setTwoSided(boolean flag)
	{
		twoSided = flag;
	}

	/** Sets if this line is upper-unpegged (upper texture is drawn from the top, down). */
	public void setUpperUnpegged(boolean flag)
	{
		upperUnpegged = flag;
	}

	/** Sets if this line is lower-unpegged (lower texture is drawn from the bottom, up). */
	public void setLowerUnpegged(boolean flag)
	{
		lowerUnpegged = flag;
	}

	/** Sets if this line is drawn as a one-sided line in the automap. */
	public void setSecret(boolean flag)
	{
		secret = flag;
	}

	/** Sets if this line blocks sound (sound must cross two of these lines to stop the sound). */
	public void setBlocksSound(boolean flag)
	{
		blocksSound = flag;
	}

	/** Sets if this line is never drawn on the automap. */
	public void setNeverDrawn(boolean flag)
	{
		neverDrawn = flag;
	}

	/** Sets if this line is always drawn on the automap. */
	public void setAlwaysDrawn(boolean flag)
	{
		alwaysDrawn = flag;
	}

	/** Sets if this line uses passthru logic (Boom). */
	public void setBoomPassThru(boolean flag)
	{
		passThru = flag;
	}

	/** Sets if this line has a repeatable special (ZDoom/Hexen flag, does not evaluate special). */
	public void setRepeatable(boolean flag)
	{
		repeatable = flag;
	}

	/** Sets if this line blocks everything (including hitscan attacks on two-sided lines, but except sound) (ZDoom). */
	public void setBlocksAll(boolean flag)
	{
		blocksAll = flag;
	}

	/** Does this line use Strife railing collision logic? */
	public boolean isRailing()
	{
		return railing;
	}

	/** Sets if this line uses Strife railing collision logic. */
	public void setRailing(boolean railing)
	{
		this.railing = railing;
	}

	/** Does this line block floating enemies? */
	public boolean blocksFloaters()
	{
		return blockFloaters;
	}

	/** Sets this line blocks floating enemies. */
	public void setBlockFloaters(boolean blockFloaters)
	{
		this.blockFloaters = blockFloaters;
	}

	/** 
	 * Does this line clip its middle textures at the ceiling and floor
	 * (ordinarily, it is drawn until the start of another texture)?
	 */
	public boolean clipsMiddleTexture()
	{
		return clipMiddleTexture;
	}

	/** 
	 * Sets if this line clips its middle textures at the ceiling and floor
	 * (ordinarily, it is drawn until the start of another texture).
	 */
	public void setClipMiddleTexture(boolean clipMidTex)
	{
		this.clipMiddleTexture = clipMidTex;
	}

	/** Is this line rendered at 25% translucency? */
	public boolean isTranslucent()
	{
		return translucent;
	}

	/** Sets if this line is rendered at 25% translucency? */
	public void setTranslucent(boolean translucent)
	{
		this.translucent = translucent;
	}

	/** Gets this line's translucency factor (0 to 1). */
	public float getAlpha()
	{
		return alpha;
	}

	/** Sets this line's translucency factor (0 to 1). */
	public void setAlpha(float alpha)
	{
		this.alpha = alpha;
	}

	/** Gets the rendering style for the line, either RENDERSTYLE_TRANSLUCENT or RENDERSTYLE_ADD. */
	public String getRenderStyle()
	{
		return renderStyle;
	}

	/** Sets the rendering style for the line, either RENDERSTYLE_TRANSLUCENT or RENDERSTYLE_ADD. */
	public void setRenderStyle(String renderStyle)
	{
		this.renderStyle = renderStyle;
	}

	/** Gets if the line should activate by anything non-projectile crossing it. */
	public boolean activatesByAnyCross()
	{
		return anyCross;
	}

	/** Sets if the line should activate by anything non-projectile crossing it. */
	public void setActivateByAnyCross(boolean anyCross)
	{
		this.anyCross = anyCross;
	}

	/** Gets if the Linedef blocks players. */
	public boolean blocksPlayers()
	{
		return blocksPlayers;
	}

	/** Sets if the Linedef blocks players. */
	public void setBlocksPlayers(boolean blocksPlayers)
	{
		this.blocksPlayers = blocksPlayers;
	}

	/** Gets if the Linedef activates on its first side only. */
	public boolean isFirstSideOnly()
	{
		return firstSideOnly;
	}

	/** Sets if the Linedef activates on its first side only. */
	public void setFirstSideOnly(boolean firstSideOnly)
	{
		this.firstSideOnly = firstSideOnly;
	}

	/** Gets if the Linedef is a boundary for reverb zones. */
	public boolean isZoneBoundary()
	{
		return zoneBoundary;
	}

	/** Sets if the Linedef is a boundary for reverb zones. */
	public void setZoneBoundary(boolean zoneBoundary)
	{
		this.zoneBoundary = zoneBoundary;
	}

	/** 
	 * Gets if the Linedef wraps the middle texture, no matter what.
	 * Usually used with see-through middle textures. 
	 */
	public boolean wrapsMiddleTexture()
	{
		return wrapMiddleTexture;
	}

	/** 
	 * Sets if the Linedef wraps the middle texture, no matter what.
	 * Usually used with see-through middle textures. 
	 */
	public void setWrapMiddleTexture(boolean wrapMiddleTexture)
	{
		this.wrapMiddleTexture = wrapMiddleTexture;
	}

	/** Gets if the Linedef has 3D middle textures: things can be supported by them. */
	public boolean middleTextureIs3D()
	{
		return middleTexture3D;
	}

	/** Sets if the Linedef has 3D middle textures: things can be supported by them. */
	public void setMiddleTextureIs3D(boolean middleTexture3D)
	{
		this.middleTexture3D = middleTexture3D;
	}

	/** Gets if this Linedef's switch activation can only be activated on their surface; must be vertically reachable. */
	public boolean checksSwitchRange()
	{
		return checkSwitchRange;
	}

	/** Sets if this Linedef's switch activation can only be activated on their surface; must be vertically reachable. */
	public void setCheckSwitchRange(boolean checkSwitchRange)
	{
		this.checkSwitchRange = checkSwitchRange;
	}

	/** Gets if this Linedef blocks projectiles (not hitscan attacks). */
	public boolean blocksProjectiles()
	{
		return blocksProjectiles;
	}

	/** Sets if this Linedef blocks projectiles (not hitscan attacks). */
	public void setBlocksProjectiles(boolean blocksProjectiles)
	{
		this.blocksProjectiles = blocksProjectiles;
	}

	/** 
	 * Gets if the Linedef blocks "use" checks.
	 * This is the complete polar opposite to "pass-thru".
	 */
	public boolean blocksUse()
	{
		return blocksUse;
	}

	/** 
	 * Sets if the Linedef blocks "use" checks.
	 * This is the complete polar opposite to "pass-thru".
	 */
	public void setBlocksUse(boolean blocksUse)
	{
		this.blocksUse = blocksUse;
	}

	/**
	 * Returns the booleans in this object as a set of bitflags.
	 * Note that some flags are not convertible, as they may not
	 * have adequate representation in a serialized Doom data structure.
	 */
	public short getDoomBitFlags()
	{
		int doomFlags = Common.booleansToInt(
				impassable, 
				blocksMonsters, 
				twoSided, 
				upperUnpegged,
				lowerUnpegged, 
				secret, 
				blocksSound, 
				neverDrawn, 
				alwaysDrawn,
				passThru);
		return (short)(doomFlags & 0x0ffff);
	}
	
	/**
	 * Returns the booleans in this object as a set of bitflags.
	 * Note that some flags are not convertible, as they may not
	 * have adequate representation in a serialized Doom data structure.
	 */
	public short getHexenBitFlags()
	{
		int doomFlags = Common.booleansToInt(
				impassable, 
				blocksMonsters, 
				twoSided, 
				upperUnpegged,
				lowerUnpegged, 
				secret, 
				blocksSound, 
				neverDrawn, 
				alwaysDrawn,
				repeatable,
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				monsterActivate,
				blocksPlayers,
				blocksAll
				);
		
		int activeFlags = 0;
		
		if (playerCross)
			activeFlags = 0x0000;
		else if (playerUse && passThru)
			activeFlags = 0x1800;
		else if (playerUse)
			activeFlags = 0x0400;
		else if (monsterCross)
			activeFlags = 0x0800;
		else if (missileImpact)
			activeFlags = 0x0C00;
		else if (playerPush)
			activeFlags = 0x1000;
		else if (missileCross)
			activeFlags = 0x1400;
		
		return (short)((doomFlags | activeFlags) & 0x0ffff);
	}

	/**
	 * Returns the booleans in this object as a set of bitflags.
	 * Note that some flags are not convertible, as they may not
	 * have adequate representation in a serialized Doom data structure.
	 */
	public short getStrifeBitFlags()
	{
		int doomFlags = Common.booleansToInt(
				impassable, 
				blocksMonsters, 
				twoSided, 
				upperUnpegged,
				lowerUnpegged, 
				secret, 
				blocksSound, 
				neverDrawn, 
				alwaysDrawn,
				railing,
				blockFloaters,
				clipMiddleTexture,
				translucent
				);
		
		return (short)(doomFlags & 0x0ffff);
	}

	/**
	 * Sets the booleans on this object from a set of bitflags.
	 * Note that some booleans are not convertible, as they may not
	 * have adequate representation in a serialized Doom data structure.
	 */
	public void setDoomBitFlags(short flags)
	{
		impassable = Common.bitIsSet(flags, (1 << 0));
		blocksMonsters = Common.bitIsSet(flags, (1 << 1));
		twoSided = Common.bitIsSet(flags, (1 << 2));
		upperUnpegged = Common.bitIsSet(flags, (1 << 3));
		lowerUnpegged = Common.bitIsSet(flags, (1 << 4));
		secret = Common.bitIsSet(flags, (1 << 5));
		blocksSound = Common.bitIsSet(flags, (1 << 6));
		neverDrawn = Common.bitIsSet(flags, (1 << 7));
		alwaysDrawn = Common.bitIsSet(flags, (1 << 8));
		passThru = Common.bitIsSet(flags, (1 << 9));

		playerCross = false;
		playerUse = false;
		passThru = false;
		playerUse = false;
		monsterCross = false;
		missileImpact = false;
		playerPush = false;
		missileCross = false;
		
		blocksAll = false;
	}

	/**
	 * Sets the booleans on this object from a set of bitflags.
	 * Note that some booleans are not convertible, as they may not
	 * have adequate representation in a serialized Doom data structure.
	 */
	public void setHexenBitFlags(short flags)
	{
		impassable = Common.bitIsSet(flags, (1 << 0));
		blocksMonsters = Common.bitIsSet(flags, (1 << 1));
		twoSided = Common.bitIsSet(flags, (1 << 2));
		upperUnpegged = Common.bitIsSet(flags, (1 << 3));
		lowerUnpegged = Common.bitIsSet(flags, (1 << 4));
		secret = Common.bitIsSet(flags, (1 << 5));
		blocksSound = Common.bitIsSet(flags, (1 << 6));
		neverDrawn = Common.bitIsSet(flags, (1 << 7));
		alwaysDrawn = Common.bitIsSet(flags, (1 << 8));
		
		repeatable = Common.bitIsSet(flags, (1 << 9));

		int activeFlags = flags & 0x1C00;
		
		playerCross = false;
		playerUse = false;
		passThru = false;
		playerUse = false;
		monsterCross = false;
		missileImpact = false;
		playerPush = false;
		missileCross = false;

		if (activeFlags == 0x0000)
			playerCross = true;
		else if (activeFlags == 0x1800)
			playerUse = passThru = true;
		else if (activeFlags == 0x0400)
			playerUse = true;
		else if (activeFlags == 0x0800)
			monsterCross = true;
		else if (activeFlags == 0x0C00)
			missileImpact = true;
		else if (activeFlags == 0x1000)
			playerPush = true;
		else if (activeFlags == 0x1400)
			missileCross = true;
		
		monsterActivate = Common.bitIsSet(flags, (1 << 13));
		blocksPlayers = Common.bitIsSet(flags, (1 << 14));
		blocksAll = Common.bitIsSet(flags, (1 << 15));
	}

	/**
	 * Sets the booleans on this object from a set of bitflags.
	 * Note that some booleans are not convertible, as they may not
	 * have adequate representation in a serialized Strife data structure.
	 */
	public void setStrifeBitFlags(short flags)
	{
		impassable = Common.bitIsSet(flags, (1 << 0));
		blocksMonsters = Common.bitIsSet(flags, (1 << 1));
		twoSided = Common.bitIsSet(flags, (1 << 2));
		upperUnpegged = Common.bitIsSet(flags, (1 << 3));
		lowerUnpegged = Common.bitIsSet(flags, (1 << 4));
		secret = Common.bitIsSet(flags, (1 << 5));
		blocksSound = Common.bitIsSet(flags, (1 << 6));
		neverDrawn = Common.bitIsSet(flags, (1 << 7));
		alwaysDrawn = Common.bitIsSet(flags, (1 << 8));
		railing = Common.bitIsSet(flags, (1 << 9));
		blockFloaters = Common.bitIsSet(flags, (1 << 10));
		clipMiddleTexture = Common.bitIsSet(flags, (1 << 11));
		translucent = Common.bitIsSet(flags, (1 << 12));

		playerCross = false;
		playerUse = false;
		passThru = false;
		playerUse = false;
		monsterCross = false;
		missileImpact = false;
		playerPush = false;
		missileCross = false;
		blocksAll = false;
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

	@Override
	public byte[] getDoomBytes() throws DataExportException
	{
		callDoomCompatibilityCheck();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
		try{
			sw.writeShort((short)(vertexStart & 0x0ffff));
			sw.writeShort((short)(vertexEnd & 0x0ffff));
			sw.writeShort((short)(getDoomBitFlags() & 0x0ffff));
			sw.writeShort((short)(special & 0x0ffff));
			sw.writeShort((short)(id & 0x0ffff));
			sw.writeShort((short)(sideFrontRef & 0x0ffff));
			sw.writeShort((short)(sideBackRef & 0x0ffff));			
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
			sw.writeShort((short)(vertexStart & 0x0ffff));
			sw.writeShort((short)(vertexEnd & 0x0ffff));
			sw.writeShort((short)(getHexenBitFlags() & 0x0ffff));
			sw.writeByte((byte)(special & 0x0ff));
			sw.writeByte((byte)(argument0 & 0x0ff));				
			sw.writeByte((byte)(argument1 & 0x0ff));				
			sw.writeByte((byte)(argument2 & 0x0ff));				
			sw.writeByte((byte)(argument3 & 0x0ff));				
			sw.writeByte((byte)(argument4 & 0x0ff));				
			sw.writeShort((short)(sideFrontRef & 0x0ffff));
			sw.writeShort((short)(sideBackRef & 0x0ffff));			
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
			sw.writeShort((short)(vertexStart & 0x0ffff));
			sw.writeShort((short)(vertexEnd & 0x0ffff));
			sw.writeShort((short)(getStrifeBitFlags() & 0x0ffff));
			sw.writeShort((short)(special & 0x0ffff));
			sw.writeShort((short)(id & 0x0ffff));
			sw.writeShort((short)(sideFrontRef & 0x0ffff));
			sw.writeShort((short)(sideBackRef & 0x0ffff));			
		} catch (IOException e){}
		
		return bos.toByteArray();
	}

	/**
	 * Checks this data structure for data export integrity for the Doom format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	protected void callDoomCompatibilityCheck() throws DataExportException
	{
		checkShortUnsigned("Vertex Start", vertexStart);
		checkShortUnsigned("Vertex End", vertexEnd);
		checkShortUnsigned("Special", special);
		checkShortUnsigned("Id", id);
		checkZero("Argument 0", argument0);
		checkZero("Argument 1", argument1);
		checkZero("Argument 2", argument2);
		checkZero("Argument 3", argument3);
		checkZero("Argument 4", argument4);
		checkShortUnsigned("Front Sidedef", sideFrontRef);
		checkShortUnsigned("Back Sidedef", sideBackRef);
		
		checkFalse("Activate: Player Use", playerUse);
		checkFalse("Activate: Player Cross", playerCross);
		checkFalse("Activate: Monster Cross", monsterCross);
		checkFalse("Activate: Player Push", playerPush);
		checkFalse("Activate: Missile Impact", missileImpact);
		checkFalse("Activate: Monster Push", monsterPush);
		checkFalse("Activate: Monster Activate", monsterActivate);
		checkFalse("Repeatable", repeatable);
		checkFalse("Blocks Players", blocksPlayers);
		checkFalse("Blocks All", blocksAll);
		
		checkFalse("Translucent", translucent);
		checkFalse("Blocks Floaters", blockFloaters);
		checkFalse("Clip Middle Textures", clipMiddleTexture);
		checkFalse("Is Railing", railing);

		callZDoomUDMFCheck();
	}

	/**
	 * Checks this data structure for data export integrity for the ZDoom/Hexen format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	protected void callHexenCompatibilityCheck() throws DataExportException
	{
		checkShortUnsigned("Vertex Start", vertexStart);
		checkShortUnsigned("Vertex End", vertexEnd);
		checkByteUnsigned("Special", special);
		checkZero("Id", id);
		checkByte("Argument 0", argument0);
		checkByte("Argument 1", argument1);
		checkByte("Argument 2", argument2);
		checkByte("Argument 3", argument3);
		checkByte("Argument 4", argument4);
		checkShortUnsigned("Front Sidedef", sideFrontRef);
		checkShortUnsigned("Back Sidedef", sideBackRef);
		
		List<String> array = new List<String>();
		if (playerCross)
			array.add("Activate: Player Cross");
		if (playerUse && passThru)
			array.add("Activate: Player Use with Passthru");
		if (playerUse && !passThru)
			array.add("Activate: Player Use");
		if (monsterCross)
			array.add("Activate: Monster Cross");
		if (missileImpact)
			array.add("Activate: Missile Impact");
		if (playerPush)
			array.add("Activate: Player Push");
		if (missileCross)
			array.add("Activate: Missile Cross");
		
		if (array.size() == 0)
			throw new DataExportException("No valid activation flags were set.");
		else if (array.size() > 1)
		{
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.size(); i++)
			{
				sb.append(" "+array.getByIndex(i));
				if (i < array.size()-1)
					sb.append(",");
			}
			throw new DataExportException("Multiple conflicting flags were set:"+sb.toString());
		}

		checkFalse("Activate: Just Pass Thru without Player Use", passThru && !playerUse);
		checkFalse("Translucent", translucent);
		checkFalse("Blocks Floaters", blockFloaters);
		checkFalse("Is Railing", railing);
		checkFalse("Clip Middle Textures", clipMiddleTexture);

		callZDoomUDMFCheck();
	}

	/**
	 * Checks this data structure for data export integrity for the Strife format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	protected void callStrifeCompatibilityCheck() throws DataExportException
	{
		checkShortUnsigned("Vertex Start", vertexStart);
		checkShortUnsigned("Vertex End", vertexEnd);
		checkShortUnsigned("Special", special);
		checkShortUnsigned("Id", id);
		checkZero("Argument 0", argument0);
		checkZero("Argument 1", argument1);
		checkZero("Argument 2", argument2);
		checkZero("Argument 3", argument3);
		checkZero("Argument 4", argument4);
		checkShortUnsigned("Front Sidedef", sideFrontRef);
		checkShortUnsigned("Back Sidedef", sideBackRef);
		
		checkFalse("Activate: Pass Thru", passThru);
		checkFalse("Activate: Player Use", playerUse);
		checkFalse("Activate: Player Cross", playerCross);
		checkFalse("Activate: Monster Cross", monsterCross);
		checkFalse("Activate: Player Push", playerPush);
		checkFalse("Activate: Missile Impact", missileImpact);
		checkFalse("Activate: Monster Push", monsterPush);
		checkFalse("Activate: Monster Activate", monsterActivate);
		checkFalse("Repeatable", repeatable);
		checkFalse("Blocks Players", blocksPlayers);
		checkFalse("Blocks All", blocksAll);

		callZDoomUDMFCheck();
	}

	/**
	 * Checks this object's data for ZDoom-specific UDMF namespace significance.
	 * If it finds that a ZDoom-specific flag is set, it will throw an exception.
	 */
	protected void callZDoomUDMFCheck() throws DataExportException
	{
		checkFalse("Activate: Monster Use", monsterUse);
		checkFalse("Activate: Monster Push", monsterPush);
		checkEqual("Alpha Value", 1f, alpha);
		checkEqual("Renderstyle", RENDERSTYLE_TRANSLUCENT, renderStyle);
		checkFalse("Activate: Any Crosses", anyCross);
		checkFalse("Activate: Monsters", monsterActivate);
		checkFalse("Blocks Players", blocksPlayers);
		checkFalse("Blocks All", blocksAll);
		checkFalse("First Side Only", firstSideOnly);
		checkFalse("Zone Boundary", zoneBoundary);
		checkFalse("Wraps Middle Texture", wrapMiddleTexture);
		checkFalse("Middle Texture's 3D", middleTexture3D);
		checkFalse("Check Switch Range", checkSwitchRange);
		checkFalse("Blocks Projectiles", blocksProjectiles);
		checkFalse("Blocks Use", blocksUse);
	}
	
	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		vertexStart = sr.readShort() & 0x0ffff;
		vertexEnd = sr.readShort() & 0x0ffff;
		setDoomBitFlags(sr.readShort());
		special = sr.readShort() & 0x0ffff;
		id = sr.readShort() & 0x0ffff;
		sideFrontRef = sr.readShort() & 0x0ffff;
		sideBackRef = sr.readShort() & 0x0ffff;
	}

	@Override
	public void readHexenBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		vertexStart = sr.readShort() & 0x0ffff;
		vertexEnd = sr.readShort() & 0x0ffff;
		setHexenBitFlags(sr.readShort());
		special = sr.readByte() & 0x0ff;
		argument0 = sr.readByte();
		argument1 = sr.readByte();
		argument2 = sr.readByte();
		argument3 = sr.readByte();
		argument4 = sr.readByte();
		sideFrontRef = sr.readShort();
		sideBackRef = sr.readShort();
	}

	@Override
	public void readStrifeBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		vertexStart = sr.readShort() & 0x0ffff;
		vertexEnd = sr.readShort() & 0x0ffff;
		setStrifeBitFlags(sr.readShort());
		special = sr.readShort() & 0x0ffff;
		id = sr.readShort() & 0x0ffff;
		sideFrontRef = sr.readShort() & 0x0ffff;
		sideBackRef = sr.readShort() & 0x0ffff;
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
		return 14;
	}

	/**
	 * Returns the length of this structure in ZDoom/Hexen-formatted bytes. 
	 */
	public static int getHexenLength()
	{
		return 16;
	}
	
	/**
	 * Returns the length of this structure in Strife-formatted bytes. 
	 */
	public static int getStrifeLength()
	{
		return 14;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Linedef ");
		sb.append(vertexStart);
		sb.append(" to ");
		sb.append(vertexEnd);
		sb.append(" special: ");
		sb.append(special);
		sb.append(String.format("[%d, %d, %d, %d, %d]", 
				argument0, argument1, argument2, argument3, argument4));
		sb.append(" id: ");
		sb.append(id);
		sb.append(" frontside: ");
		sb.append(sideFrontRef);
		sb.append(" backside: ");
		sb.append(sideBackRef);
		return sb.toString();
	}
	
}
