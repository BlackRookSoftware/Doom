/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.udmf.namespace;

import com.blackrook.commons.Common;
import com.blackrook.doom.struct.Linedef;
import com.blackrook.doom.struct.Sector;
import com.blackrook.doom.struct.Sidedef;
import com.blackrook.doom.struct.Thing;
import com.blackrook.doom.udmf.UDMFStruct;

/**
 * Common namespace for all ZDoom-compatible structures - should be "super-called" by
 * all namespaces that extend this class and override its methods 
 * in order to set common keyword values across all implementing namespaces.
 * @author Matthew Tropiano
 */
public abstract class ZDoomCommonNamespace extends CommonNamespace
{

	@Override
	public void getLinedefAttribs(UDMFStruct struct, Linedef linedef)
	{
		super.getLinedefAttribs(struct, linedef);
		
		linedef.setBoomPassThru(struct.getBoolean("passuse"));

		linedef.setTranslucent(struct.getBoolean("translucent"));
		linedef.setRailing(struct.getBoolean("jumpover"));
		linedef.setBlockFloaters(struct.getBoolean("blockfloaters"));

		linedef.setActivatesByPlayerCross(struct.getBoolean("playercross"));
		linedef.setActivatesByPlayerUse(struct.getBoolean("playeruse"));
		linedef.setActivatesByMonsterCross(struct.getBoolean("monstercross"));
		linedef.setActivatesByMonsterUse(struct.getBoolean("monsteruse"));
		linedef.setActivatesByMissileImpact(struct.getBoolean("impact"));
		linedef.setActivatesByPlayerPush(struct.getBoolean("playerpush"));
		linedef.setActivatesByMonsterPush(struct.getBoolean("monsterpush"));
		linedef.setActivatesByMissileCross(struct.getBoolean("missilecross"));
		linedef.setRepeatable(struct.getBoolean("repeatspecial"));
		
		linedef.setArgument0(struct.getInt("argument0"));
		linedef.setArgument1(struct.getInt("argument1"));
		linedef.setArgument2(struct.getInt("argument2"));
		linedef.setArgument3(struct.getInt("argument3"));
		linedef.setArgument4(struct.getInt("argument4"));
		
		linedef.setAlpha(struct.getFloat("alpha", 1.0f));
		linedef.setRenderStyle(struct.getString("renderstyle",Linedef.RENDERSTYLE_TRANSLUCENT));
		linedef.setActivateByAnyCross(struct.getBoolean("anycross"));
		linedef.setActivatableByMonster(struct.getBoolean("monsteractivate"));
		linedef.setBlocksPlayers(struct.getBoolean("blockplayers"));
		linedef.setBlocksAll(struct.getBoolean("blockeverything"));
		linedef.setFirstSideOnly(struct.getBoolean("firstsideonly"));
		linedef.setZoneBoundary(struct.getBoolean("zoneboundary"));
		linedef.setClipMiddleTexture(struct.getBoolean("clipmidtex"));
		linedef.setWrapMiddleTexture(struct.getBoolean("wrapmidtex"));
		linedef.setMiddleTextureIs3D(struct.getBoolean("midtex3d"));
		linedef.setCheckSwitchRange(struct.getBoolean("checkswitchrange"));
		linedef.setBlocksPlayers(struct.getBoolean("blocksplayers"));
		linedef.setBlocksUse(struct.getBoolean("blockuse"));
	}

	@Override
	public void getSectorAttribs(UDMFStruct struct, Sector sector)
	{
		super.getSectorAttribs(struct, sector);
		
		sector.setFloorOffsetX(struct.getFloat("xpanningfloor"));
		sector.setFloorOffsetY(struct.getFloat("ypanningfloor"));
		sector.setCeilingOffsetX(struct.getFloat("xpanningceiling"));
		sector.setCeilingOffsetY(struct.getFloat("ypanningceiling"));
		sector.setFloorScaleX(struct.getFloat("xscalefloor", 1.0f));
		sector.setFloorScaleY(struct.getFloat("yscalefloor", 1.0f));
		sector.setCeilingScaleX(struct.getFloat("xscaleceiling", 1.0f));
		sector.setCeilingScaleY(struct.getFloat("yscaleceiling", 1.0f));
		sector.setFloorRotation(struct.getFloat("rotationfloor"));
		sector.setCeilingRotation(struct.getFloat("rotationceiling"));
		sector.setFloorLighting(struct.getInt("lightfloor"));
		sector.setCeilingLighting(struct.getInt("lightceiling"));
		sector.setFloorLightingAbsolute(struct.getBoolean("lightfloorabsolute"));
		sector.setCeilingLightingAbsolute(struct.getBoolean("lightceilingabsolute"));
		sector.setGravity(struct.getFloat("gravity", 1.0f));
		sector.setLightColor(Common.argbToColor(struct.getInt("lightcolor",0xffffffff)));
		sector.setFadeColor(Common.argbToColor(struct.getInt("fadecolor",0xff000000)));
		sector.setDesaturation(struct.getFloat("desaturation"));
		sector.setSilent(struct.getBoolean("silent"));
		sector.setNoFallingDamage(struct.getBoolean("nofallingdamage"));
		sector.setDropsActors(struct.getBoolean("dropactors"));
		sector.setNoRespawn(struct.getBoolean("norespawn"));
	}

	@Override
	public void getSidedefAttribs(UDMFStruct struct, Sidedef sidedef)
	{
		super.getSidedefAttribs(struct, sidedef);
		
		sidedef.setUpperTextureScaleX(struct.getFloat("scalex_top", 1.0f));
		sidedef.setUpperTextureScaleY(struct.getFloat("scaley_top", 1.0f));
		sidedef.setMiddleTextureScaleX(struct.getFloat("scalex_mid", 1.0f));
		sidedef.setMiddleTextureScaleY(struct.getFloat("scaley_mid", 1.0f));
		sidedef.setLowerTextureScaleX(struct.getFloat("scalex_bottom", 1.0f));
		sidedef.setLowerTextureScaleY(struct.getFloat("scaley_bottom", 1.0f));
		sidedef.setUpperTextureOffsetX(struct.getFloat("offsetx_top"));
		sidedef.setUpperTextureOffsetY(struct.getFloat("offsety_top"));
		sidedef.setMiddleTextureOffsetX(struct.getFloat("offsetx_mid"));
		sidedef.setMiddleTextureOffsetY(struct.getFloat("offsety_mid"));
		sidedef.setLowerTextureOffsetX(struct.getFloat("offsetx_bottom"));
		sidedef.setLowerTextureOffsetY(struct.getFloat("offsety_bottom"));
		sidedef.setLight(struct.getInt("light"));
		sidedef.setLightAbsolute(struct.getBoolean("lightabsolute"));
		sidedef.setNotUseFakeContrast(struct.getBoolean("nofakecontrast"));
		sidedef.setSmoothlyLit(struct.getBoolean("smoothlighting"));
		sidedef.setClipMiddleTexture(struct.getBoolean("clipmidtex"));
		sidedef.setWrapMiddleTexture(struct.getBoolean("wrapmidtex"));
		sidedef.setNoDecals(struct.getBoolean("nodecals"));
	}

	@Override
	public void getThingAttribs(UDMFStruct struct, Thing thing)
	{
		super.getThingAttribs(struct, thing);
		
		thing.setFriendly(struct.getBoolean("friendly"));

		thing.setTranslucent(struct.getBoolean("translucent"));
		thing.setStandsStill(struct.getBoolean("standing"));
		thing.setStrifeAlly(struct.getBoolean("strifeally"));
		thing.setInvisible(struct.getBoolean("invisible"));

		thing.setSpecial(struct.getInt("special"));
		thing.setArgument0(struct.getInt("argument0"));
		thing.setArgument1(struct.getInt("argument1"));
		thing.setArgument2(struct.getInt("argument2"));
		thing.setArgument3(struct.getInt("argument3"));
		thing.setArgument4(struct.getInt("argument4"));
		
		thing.setDormant(struct.getBoolean("dormant"));
		thing.setClass1(struct.getBoolean("class1"));
		thing.setClass2(struct.getBoolean("class2"));
		thing.setClass3(struct.getBoolean("class3"));

		thing.setSkill6(struct.getBoolean("skill6"));
		thing.setSkill7(struct.getBoolean("skill7"));
		thing.setSkill8(struct.getBoolean("skill8"));

		thing.setClass4(struct.getBoolean("class4"));
		thing.setClass5(struct.getBoolean("class5"));
		thing.setClass6(struct.getBoolean("class6"));
		thing.setClass7(struct.getBoolean("class7"));
		thing.setClass8(struct.getBoolean("class8"));
	}

	@Override
	public void setLinedefAttribs(Linedef linedef, UDMFStruct struct)
	{
		super.setLinedefAttribs(linedef, struct);
		
		struct.put("passuse", linedef.isBoomPassThru());

		struct.put("translucent", linedef.isTranslucent());
		struct.put("jumpover", linedef.isRailing());
		struct.put("blockfloaters", linedef.blocksFloaters());

		struct.put("playercross", linedef.activatesByPlayerCross());
		struct.put("playeruse", linedef.activatesByPlayerUse());
		struct.put("monstercross", linedef.activatesByMonsterCross());
		struct.put("monsteruse", linedef.activatesByMonsterUse());
		struct.put("impact", linedef.activatesByMissileImpact());
		struct.put("playerpush", linedef.activatesByPlayerPush());
		struct.put("monsterpush", linedef.activatesByMonsterPush());
		struct.put("missilecross", linedef.activatesByMissileCross());
		struct.put("repeatspecial", linedef.isRepeatable());
		
		struct.put("argument0", linedef.getArgument0());
		struct.put("argument1", linedef.getArgument1());
		struct.put("argument2", linedef.getArgument2());
		struct.put("argument3", linedef.getArgument3());
		struct.put("argument4", linedef.getArgument4());
		
		struct.put("alpha", linedef.getAlpha());
		struct.put("renderstyle", linedef.getRenderStyle());
		struct.put("anycross", linedef.activatesByAnyCross());
		struct.put("monsteractivate", linedef.activatableByMonster());
		struct.put("blockplayers", linedef.blocksPlayers());
		struct.put("blockeverything", linedef.blocksAll());
		struct.put("firstsideonly", linedef.isFirstSideOnly());
		struct.put("zoneboundary", linedef.isZoneBoundary());
		struct.put("clipmidtex", linedef.clipsMiddleTexture());
		struct.put("wrapmidtex", linedef.wrapsMiddleTexture());
		struct.put("midtex3d", linedef.middleTextureIs3D());
		struct.put("checkswitchrange", linedef.checksSwitchRange());
		struct.put("blocksplayers", linedef.blocksPlayers());
		struct.put("blockuse", linedef.blocksUse());
	}

	@Override
	public void setSectorAttribs(Sector sector, UDMFStruct struct)
	{
		super.setSectorAttribs(sector, struct);
		
		struct.put("xpanningfloor", sector.getFloorOffsetX());
		struct.put("ypanningfloor", sector.getFloorOffsetY());
		struct.put("xpanningceiling", sector.getCeilingOffsetX());
		struct.put("ypanningceiling", sector.getCeilingOffsetY());
		struct.put("xscalefloor", sector.getFloorScaleX());
		struct.put("yscalefloor", sector.getFloorScaleY());
		struct.put("xscaleceiling", sector.getCeilingScaleX());
		struct.put("yscaleceiling", sector.getCeilingScaleY());
		struct.put("rotationfloor", sector.getFloorRotation());
		struct.put("rotationceiling", sector.getCeilingRotation());
		struct.put("lightfloor", sector.getFloorLighting());
		struct.put("lightceiling", sector.getCeilingLighting());
		struct.put("lightfloorabsolute", sector.isFloorLightingAbsolute());
		struct.put("lightceilingabsolute", sector.isCeilingLightingAbsolute());
		struct.put("gravity", sector.getGravity());
		struct.put("lightcolor", Common.colorToARGB(sector.getLightColor()));
		struct.put("fadecolor", Common.colorToARGB(sector.getFadeColor()));
		struct.put("desaturation", sector.getDesaturation());
		struct.put("silent", sector.isSilent());
		struct.put("nofallingdamage", sector.doesNoFallingDamage());
		struct.put("dropactors", sector.dropsActors());
		struct.put("norespawn", sector.isNoRespawn());
	}

	@Override
	public void setSidedefAttribs(Sidedef sidedef, UDMFStruct struct)
	{
		super.setSidedefAttribs(sidedef, struct);
		
		struct.put("scalex_top", sidedef.getUpperTextureScaleX());
		struct.put("scaley_top", sidedef.getUpperTextureScaleY());
		struct.put("scalex_mid", sidedef.getMiddleTextureScaleX());
		struct.put("scaley_mid", sidedef.getMiddleTextureScaleY());
		struct.put("scalex_bottom", sidedef.getLowerTextureScaleX());
		struct.put("scaley_bottom", sidedef.getLowerTextureScaleY());
		struct.put("offsetx_top", sidedef.getUpperTextureOffsetX());
		struct.put("offsety_top", sidedef.getUpperTextureOffsetY());
		struct.put("offsetx_mid", sidedef.getMiddleTextureOffsetX());
		struct.put("offsety_mid", sidedef.getMiddleTextureOffsetY());
		struct.put("offsetx_bottom", sidedef.getLowerTextureOffsetX());
		struct.put("offsety_bottom", sidedef.getLowerTextureOffsetY());
		
		struct.put("light", sidedef.getLight());
		struct.put("lightabsolute", sidedef.isLightAbsolute());
		struct.put("nofakecontrast", sidedef.notUseFakeContrast());
		struct.put("smoothlighting", sidedef.isSmoothlyLit());
		struct.put("clipmidtex", sidedef.clipsMiddleTexture());
		struct.put("wrapmidtex", sidedef.wrapsMiddleTexture());
		struct.put("nodecals", sidedef.isNoDecals());
	}

	@Override
	public void setThingAttribs(Thing thing, UDMFStruct struct)
	{
		super.setThingAttribs(thing, struct);
		
		struct.put("friendly", thing.isFriendly());

		struct.put("translucent", thing.isTranslucent());
		struct.put("standing", thing.standsStill());
		struct.put("strifeally", thing.isStrifeAlly());
		struct.put("invisible", thing.isInvisible());
		struct.put("special", thing.getSpecial());

		struct.put("argument0", thing.getArgument0());
		struct.put("argument1", thing.getArgument1());
		struct.put("argument2", thing.getArgument2());
		struct.put("argument3", thing.getArgument3());
		struct.put("argument4", thing.getArgument4());

		struct.put("dormant", thing.isDormant());
		struct.put("class1", thing.appearsForClass1());
		struct.put("class2", thing.appearsForClass2());
		struct.put("class3", thing.appearsForClass3());

		struct.put("skill6", thing.getSkill6());
		struct.put("skill7", thing.getSkill7());
		struct.put("skill8", thing.getSkill8());

		struct.put("class4", thing.appearsForClass4());
		struct.put("class5", thing.appearsForClass5());
		struct.put("class6", thing.appearsForClass6());
		struct.put("class7", thing.appearsForClass7());
		struct.put("class8", thing.appearsForClass8());
	}

}
