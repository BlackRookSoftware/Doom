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
 * UDMF namespace for Doom data.
 * @author Matthew Tropiano
 */
public class DoomNamespace extends CommonNamespace
{

	@Override
	public String getName()
	{
		return "Doom";
	}

	@Override
	public void getLinedefAttribs(UDMFStruct struct, Linedef linedef)
	{
		super.getLinedefAttribs(struct, linedef);
		linedef.setBoomPassThru(struct.getBoolean("passuse"));
	}

	@Override
	public void getThingAttribs(UDMFStruct struct, Thing thing)
	{
		super.getThingAttribs(struct, thing);
		thing.setFriendly(struct.getBoolean("friendly"));
	}

	@Override
	public void setLinedefAttribs(Linedef linedef, UDMFStruct struct)
	{
		super.setLinedefAttribs(linedef, struct);
		struct.put("passuse", linedef.isBoomPassThru());
	}

	@Override
	public void setThingAttribs(Thing thing, UDMFStruct struct)
	{
		super.setThingAttribs(thing, struct);
		struct.put("friendly", thing.isFriendly());
	}
	
}
