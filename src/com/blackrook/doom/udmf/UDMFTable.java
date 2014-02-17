/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.udmf;

import java.util.Iterator;

import com.blackrook.commons.hash.CaseInsensitiveHashedQueueMap;
import com.blackrook.commons.linkedlist.Queue;

/**
 * This holds a bunch of UDMFStructs for reading Doom information.
 * Also contains a structure for "global" fields in the UDMF, like "namespace".
 * @author Matthew Tropiano
 */
public class UDMFTable
{
	private static final UDMFStruct[] EMPTY_STRUCT_LIST = new UDMFStruct[0];
	
	/** Root fields table. */
	private UDMFStruct globalFields;
	/** UDMF tables. */
	private CaseInsensitiveHashedQueueMap<UDMFStruct> innerTable;
	
	/**
	 * Creates a new UDMFTable.
	 */
	public UDMFTable()
	{
		super();
		globalFields = new UDMFStruct();
		innerTable = new CaseInsensitiveHashedQueueMap<UDMFStruct>();
	}

	/**
	 * Returns the root fields structure.
	 */
	public UDMFStruct getGlobalFields()
	{
		return globalFields;
	}
	
	/**
	 * Returns all structures of a specific name into an array.
	 * The names are case-insensitive.
	 * @param name	the name of the structures to retrieve.
	 * @return	the queue of structures with the matching name in the order that
	 * they were added to the structure. If there are none, an empty array
	 * is returned.
	 */
	public UDMFStruct[] getStructs(String name)
	{
		Queue<UDMFStruct> list = innerTable.get(name);
		if (list == null)
			return EMPTY_STRUCT_LIST;
		UDMFStruct[] out = new UDMFStruct[list.size()];
		list.toArray(out);
		return out;
	}
	
	/**
	 * Adds a struct of a particular name to this table.
	 * Keep in mind that the order in which these are added is important.
	 * @param name	the name of this type of structure.
	 * @return	a reference to the new structure created.
	 */
	public UDMFStruct addStruct(String name)
	{
		return addStruct(name, new UDMFStruct());
	}

	/**
	 * Adds a struct of a particular type name to this table.
	 * Keep in mind that the order in which these are added is important.
	 * @param name	the name of this type of structure.
	 * @return	a reference to the added structure.
	 */
	public UDMFStruct addStruct(String name, UDMFStruct struct)
	{
		innerTable.enqueue(name, struct);
		return struct;
	}
	
	/**
	 * Returns a list of all of the struct type names in the table.
	 */
	public String[] getAllStructNames()
	{
		String[] out = new String[innerTable.size()];
		int i = 0;
		Iterator<String> it = innerTable.keyIterator();
		while (it.hasNext())
			out[i++] = it.next();
		return out;
	}
	
}
