/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.udmf.namespace;

import com.blackrook.doom.struct.Linedef;
import com.blackrook.doom.struct.Sector;
import com.blackrook.doom.struct.Sidedef;
import com.blackrook.doom.struct.Thing;
import com.blackrook.doom.struct.Vertex;
import com.blackrook.doom.udmf.UDMFStruct;

/**
 * Common namespace for all Doom structures - should be "super-called" by
 * all namespaces that extend this class and override its methods 
 * in order to set common keyword values across all implementing namespaces.
 * @author Matthew Tropiano
 */
public abstract class CommonNamespace extends UDMFNamespace
{

	@Override
	public void getLinedefAttribs(UDMFStruct struct, Linedef linedef)
	{
		linedef.setComment(struct.getString("comment"));

		linedef.setId(struct.getInt("id"));
		linedef.setVertexStart(struct.getInt("v1"));
		linedef.setVertexEnd(struct.getInt("v2"));
		linedef.setFrontSidedef(struct.getInt("sidefront"));
		linedef.setBackSidedef(struct.getInt("sideback"));
		
		linedef.setImpassable(struct.getBoolean("blocking"));
		linedef.setBlocksMonsters(struct.getBoolean("blockmonsters"));
		linedef.setTwoSided(struct.getBoolean("twosided"));
		linedef.setUpperUnpegged(struct.getBoolean("dontpegtop"));
		linedef.setLowerUnpegged(struct.getBoolean("dontpegbottom"));
		linedef.setSecret(struct.getBoolean("secret"));
		linedef.setBlocksSound(struct.getBoolean("blocksound"));
		linedef.setNeverDrawn(struct.getBoolean("dontdraw"));
		linedef.setAlwaysDrawn(struct.getBoolean("mapped"));
		
		linedef.setSpecial(struct.getInt("special"));
	}
	
	@Override
	public void getSectorAttribs(UDMFStruct struct, Sector sector)
	{
		sector.setComment(struct.getString("comment"));

		sector.setTag(struct.getInt("id"));
		sector.setFloorHeight(struct.getInt("heightfloor"));
		sector.setCeilingHeight(struct.getInt("heightceiling"));
		sector.setFloorTexture(struct.getString("texturefloor"));
		sector.setCeilingTexture(struct.getString("textureceiling"));
		sector.setLightLevel(struct.getInt("lightlevel"));
		sector.setSpecial(struct.getInt("special"));
	}

	@Override
	public void getSidedefAttribs(UDMFStruct struct, Sidedef sidedef)
	{
		sidedef.setComment(struct.getString("comment"));
		
		sidedef.setOffsetX(struct.getInt("offsetx"));
		sidedef.setOffsetY(struct.getInt("offsety"));
		sidedef.setUpperTexture(struct.getString("texturetop"));
		sidedef.setLowerTexture(struct.getString("texturebottom"));
		sidedef.setMiddleTexture(struct.getString("texturemiddle"));
		sidedef.setSectorRef(struct.getInt("sector"));
	}

	@Override
	public void getThingAttribs(UDMFStruct struct, Thing thing)
	{
		thing.setComment(struct.getString("comment"));

		thing.setId(struct.getInt("id"));
		thing.setX(struct.getFloat("x"));
		thing.setY(struct.getFloat("y"));
		thing.setAngle(struct.getInt("angle"));
		thing.setType(struct.getInt("type"));
		
		thing.setSkill1(struct.getBoolean("skill1"));
		thing.setSkill2(struct.getBoolean("skill2"));
		thing.setSkill3(struct.getBoolean("skill3"));
		thing.setSkill4(struct.getBoolean("skill4"));
		thing.setSkill5(struct.getBoolean("skill5"));
		thing.setAmbusher(struct.getBoolean("ambush"));
		thing.setAppearsOnSinglePlayer(struct.getBoolean("single"));
		thing.setAppearsOnDeathmatch(struct.getBoolean("dm"));
		thing.setAppearsOnCooperative(struct.getBoolean("coop"));
	}

	@Override
	public void getVertexAttribs(UDMFStruct struct, Vertex vertex)
	{
		vertex.setX(struct.getFloat("x"));
		vertex.setY(struct.getFloat("y"));
	}

	@Override
	public void setLinedefAttribs(Linedef linedef, UDMFStruct struct)
	{
		struct.put("comment",linedef.getComment());
		
		struct.put("id",linedef.getId());
		struct.put("v1", linedef.getVertexStart());
		struct.put("v2", linedef.getVertexEnd());
		struct.put("sidefront", linedef.getFrontSidedef());
		struct.put("sideback", linedef.getBackSidedef());

		struct.put("blocking", linedef.isImpassable());
		struct.put("blockmonsters", linedef.blocksMonsters());
		struct.put("twosided", linedef.isTwoSided());
		struct.put("dontpegtop", linedef.isUpperUnpegged());
		struct.put("dontpegbottom", linedef.isLowerUnpegged());
		struct.put("secret", linedef.isSecret());
		struct.put("blocksound", linedef.blocksSound());
		struct.put("dontdraw", linedef.isNeverDrawn());
		struct.put("mapped", linedef.isAlwaysDrawn());
		struct.put("special", linedef.getSpecial());
	}

	@Override
	public void setSectorAttribs(Sector sector, UDMFStruct struct)
	{
		struct.put("comment", sector.getComment());

		struct.put("id", sector.getTag());
		struct.put("heightfloor", sector.getFloorHeight());
		struct.put("heightceiling", sector.getCeilingHeight());
		struct.put("texturefloor", sector.getFloorTexture());
		struct.put("textureceiling", sector.getCeilingTexture());
		struct.put("lightlevel", sector.getLightLevel());
		struct.put("special", sector.getSpecial());
	}

	@Override
	public void setSidedefAttribs(Sidedef sidedef, UDMFStruct struct)
	{
		struct.put("comment", sidedef.getComment());
		
		struct.put("offsetx", sidedef.getOffsetX());
		struct.put("offsety", sidedef.getOffsetY());
		struct.put("texturetop", sidedef.getUpperTexture());
		struct.put("texturebottom", sidedef.getLowerTexture());
		struct.put("texturemiddle", sidedef.getMiddleTexture());
		struct.put("sector", sidedef.getSectorRef());
	}

	@Override
	public void setThingAttribs(Thing thing, UDMFStruct struct)
	{
		struct.put("comment", thing.getComment());

		struct.put("id", thing.getId());
		struct.put("x", thing.getX());
		struct.put("y", thing.getY());
		struct.put("angle", thing.getAngle());
		struct.put("type", thing.getType());
		
		struct.put("skill1", thing.getSkill1());
		struct.put("skill2", thing.getSkill2());
		struct.put("skill3", thing.getSkill3());
		struct.put("skill4", thing.getSkill4());
		struct.put("skill5", thing.getSkill5());
		struct.put("ambush", thing.isAmbusher());
		struct.put("single", thing.appearsOnSinglePlayer());
		struct.put("dm", thing.appearsOnDeathmatch());
		struct.put("coop", thing.appearsOnCooperative());
	}

	@Override
	public void setVertexAttribs(Vertex vertex, UDMFStruct struct)
	{
		struct.put("x", vertex.getX());
		struct.put("y", vertex.getY());
	}

}
