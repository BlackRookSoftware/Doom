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
 * UDMF namespace for Strife data.
 * @author Matthew Tropiano
 */
public class StrifeNamespace extends CommonNamespace
{

	@Override
	public String getName()
	{
		return "Strife";
	}

	@Override
	public void getLinedefAttribs(UDMFStruct struct, Linedef linedef)
	{
		super.getLinedefAttribs(struct, linedef);
		linedef.setTranslucent(struct.getBoolean("translucent"));
		linedef.setRailing(struct.getBoolean("jumpover"));
		linedef.setBlockFloaters(struct.getBoolean("blockfloaters"));
	}

	@Override
	public void getThingAttribs(UDMFStruct struct, Thing thing)
	{
		super.getThingAttribs(struct, thing);
		thing.setTranslucent(struct.getBoolean("translucent"));
		thing.setStandsStill(struct.getBoolean("standing"));
		thing.setStrifeAlly(struct.getBoolean("strifeally"));
		thing.setInvisible(struct.getBoolean("invisible"));
	}

	@Override
	public void setLinedefAttribs(Linedef linedef, UDMFStruct struct)
	{
		super.setLinedefAttribs(linedef, struct);
		struct.put("translucent", linedef.isTranslucent());
		struct.put("jumpover", linedef.isRailing());
		struct.put("blockfloaters", linedef.blocksFloaters());
	}

	@Override
	public void setThingAttribs(Thing thing, UDMFStruct struct)
	{
		super.setThingAttribs(thing, struct);
		struct.put("translucent", thing.isTranslucent());
		struct.put("standing", thing.standsStill());
		struct.put("strifeally", thing.isStrifeAlly());
		struct.put("invisible", thing.isInvisible());
	}
	
}
