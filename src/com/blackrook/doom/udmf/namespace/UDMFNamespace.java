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
 * A UDMF namespace type for applying structure attributes
 * to Doom map structure objects.
 * @author Matthew Tropiano
 */
public abstract class UDMFNamespace
{
	/**
	 * Returns the commonly accepted name of this namespace for UDMF parsers.
	 */
	public abstract String getName();
	
	/**
	 * Applies attributes from a UDMF structure to a linedef.
	 * The structure used presumably holds linedef attributes. 
	 * @param struct	the structure to use.
	 * @param linedef	the linedef to apply attributes to.
	 */
	public abstract void getLinedefAttribs(UDMFStruct struct, Linedef linedef);
	
	/**
	 * Applies attributes from a UDMF structure to a sidedef.
	 * The structure used presumably holds sidedef attributes. 
	 * @param struct	the structure to use.
	 * @param sidedef	the sidedef to apply attributes to.
	 */
	public abstract void getSidedefAttribs(UDMFStruct struct, Sidedef sidedef);

	/**
	 * Applies attributes from a UDMF structure to a sector.
	 * The structure used presumably holds sector attributes. 
	 * @param struct	the structure to use.
	 * @param sector	the sector to apply attributes to.
	 */
	public abstract void getSectorAttribs(UDMFStruct struct, Sector sector);

	/**
	 * Applies attributes from a UDMF structure to a vertex.
	 * The structure used presumably holds vertex attributes. 
	 * @param struct	the structure to use.
	 * @param vertex	the vertex to apply attributes to.
	 */
	public abstract void getVertexAttribs(UDMFStruct struct, Vertex vertex);

	/**
	 * Applies attributes from a UDMF structure to a thing.
	 * The structure used presumably holds thing attributes. 
	 * @param struct	the structure to use.
	 * @param thing	the thing to apply attributes to.
	 */
	public abstract void getThingAttribs(UDMFStruct struct, Thing thing);

	/**
	 * Applies attributes from a linedef to a UDMF structure.
	 * @param linedef	the linedef to use.
	 * @param struct	the structure to apply attributes to.
	 */
	public abstract void setLinedefAttribs(Linedef linedef, UDMFStruct struct);
	
	/**
	 * Applies attributes from a sidedef to a UDMF structure.
	 * @param sidedef	the sidedef to use.
	 * @param struct	the structure to apply attributes to.
	 */
	public abstract void setSidedefAttribs(Sidedef sidedef, UDMFStruct struct);

	/**
	 * Applies attributes from a sector to a UDMF structure.
	 * @param sector	the sector to use.
	 * @param struct	the structure to apply attributes to.
	 */
	public abstract void setSectorAttribs(Sector sector, UDMFStruct struct);

	/**
	 * Applies attributes from a vertex to a UDMF structure.
	 * @param vertex	the vertex to use.
	 * @param struct	the structure to apply attributes to.
	 */
	public abstract void setVertexAttribs(Vertex vertex, UDMFStruct struct);

	/**
	 * Applies attributes from a thing to a UDMF structure.
	 * @param thing		the thing to use.
	 * @param struct	the structure to apply attributes to.
	 */
	public abstract void setThingAttribs(Thing thing, UDMFStruct struct);

}
