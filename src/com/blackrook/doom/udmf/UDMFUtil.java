/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.udmf;

import com.blackrook.commons.hash.CaseInsensitiveHashMap;
import com.blackrook.doom.DoomMap;
import com.blackrook.doom.struct.*;
import com.blackrook.doom.udmf.namespace.*;

/**
 * UDMF utility library for reading/writing UDMF structures.
 * @author Matthew Tropiano
 */
public final class UDMFUtil
{
	private UDMFUtil() {}
	
	/** Doom namespace. */
	public static final DoomNamespace DOOM_NAMESPACE = new DoomNamespace();
	/** Heretic namespace. */
	public static final HereticNamespace HERETIC_NAMESPACE = new HereticNamespace();
	/** Hexen namespace. */
	public static final HexenNamespace HEXEN_NAMESPACE = new HexenNamespace();
	/** Strife namespace. */
	public static final StrifeNamespace STRIFE_NAMESPACE = new StrifeNamespace();
	/** ZDoom namespace. */
	public static final ZDoomNamespace ZDOOM_NAMESPACE = new ZDoomNamespace();
	/** ZDoom namespace. */
	public static final ZDoomTranslatedNamespace ZDOOM_TRANSLATED_NAMESPACE = new ZDoomTranslatedNamespace();
	
	public static final CaseInsensitiveHashMap<UDMFNamespace> NAMESPACE_TABLE = 
		new CaseInsensitiveHashMap<UDMFNamespace>(){{
			put(DOOM_NAMESPACE.getName(), DOOM_NAMESPACE);
			put(HERETIC_NAMESPACE.getName(), HERETIC_NAMESPACE);
			put(HEXEN_NAMESPACE.getName(), HEXEN_NAMESPACE);
			put(STRIFE_NAMESPACE.getName(), STRIFE_NAMESPACE);
			put(ZDOOM_NAMESPACE.getName(), ZDOOM_NAMESPACE);
			put(ZDOOM_TRANSLATED_NAMESPACE.getName(), ZDOOM_TRANSLATED_NAMESPACE);
	}};
	
	/**
	 * Returns the appropriate namespace for reading map information from a set of UDMF structures,
	 * given a namespace (case-insensitive);
	 * Returns null if no such namespace. 
	 */
	public static UDMFNamespace getNamespaceForName(String name)
	{
		return NAMESPACE_TABLE.get(name);
	}
	
	/**
	 * Converts a DoomMap to a UDMF structure using a specified namespace.
	 * See UDMFWriter for a means of exporting this structure to text.
	 * @param namespace	the UDMF namespace to use.
	 * @param dm		the DoomMap to convert.
	 * @return			a UDMFTable that is a UDMF representation of this map.
	 */
	public static UDMFTable mapToUDMF(UDMFNamespace namespace, DoomMap dm)
	{
		UDMFTable out = new UDMFTable();
		
		out.getGlobalFields().put("namespace", namespace.getName());
		
		for (Linedef linedef : dm.getLinedefList())
		{
			UDMFStruct struct = new UDMFStruct();
			namespace.setLinedefAttribs(linedef, struct);
			out.addStruct("linedef", struct);
		}
		
		for (Sidedef sidedef : dm.getSidedefList())
		{
			UDMFStruct struct = new UDMFStruct();
			namespace.setSidedefAttribs(sidedef, struct);
			out.addStruct("sidedef", struct);
		}
		
		for (Sector sector : dm.getSectorList())
		{
			UDMFStruct struct = new UDMFStruct();
			namespace.setSectorAttribs(sector, struct);
			out.addStruct("sector", struct);
		}
		
		for (Vertex vertex : dm.getVertexList())
		{
			UDMFStruct struct = new UDMFStruct();
			namespace.setVertexAttribs(vertex, struct);
			out.addStruct("vertex", struct);
		}
		
		for (Thing thing : dm.getThingList())
		{
			UDMFStruct struct = new UDMFStruct();
			namespace.setThingAttribs(thing, struct);
			out.addStruct("thing", struct);
		}
		
		return out;
	}
	
}
