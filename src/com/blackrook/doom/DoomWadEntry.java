/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom;

/**
 * All and any Doom Wad Entries.
 * @author Matthew Tropiano
 */
public interface DoomWadEntry
{
	/** The length of an entry in bytes. */
	public static final int WADENTRY_LEN = 16;

	/** Returns the byte offset into the WAD. */
	public int getOffset();

	/** Returns the size of the entry data in bytes. */
	public int getSize();

	/** Returns the name of the entry. */
	public String getName();

	/**
	 * Converts this WadEntry into bytes.
	 */
	public byte[] toBytes();
	
}
