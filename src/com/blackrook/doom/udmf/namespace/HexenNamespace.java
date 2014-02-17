/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.udmf.namespace;

import com.blackrook.doom.struct.Linedef;
import com.blackrook.doom.struct.Thing;
import com.blackrook.doom.udmf.UDMFStruct;

/**
 * UDMF namespace for Hexen data.
 * @author Matthew Tropiano
 */
public class HexenNamespace extends CommonNamespace
{

	@Override
	public String getName()
	{
		return "Hexen";
	}

	@Override
	public void getLinedefAttribs(UDMFStruct struct, Linedef linedef)
	{
		super.getLinedefAttribs(struct, linedef);

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
	}

	@Override
	public void getThingAttribs(UDMFStruct struct, Thing thing)
	{
		super.getThingAttribs(struct, thing);

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
	}

	@Override
	public void setLinedefAttribs(Linedef linedef, UDMFStruct struct)
	{
		super.setLinedefAttribs(linedef, struct);
		
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
	}

	@Override
	public void setThingAttribs(Thing thing, UDMFStruct struct)
	{
		super.setThingAttribs(thing, struct);

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
	}
	

}
