/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.enums;

/**
 * List of game types for the Doom Engine's binary game data.
 * @author Matthew Tropiano
 */
public enum GameType
{
	UNKNOWN(DataFormat.DOOM),
	DOOM(DataFormat.DOOM),
	DOOM2(DataFormat.DOOM),
	TNT(DataFormat.DOOM),
	PLUTONIA(DataFormat.DOOM),
	HERETIC(DataFormat.DOOM),
	HEXEN(DataFormat.HEXEN),
	STRIFE(DataFormat.STRIFE);
	
	private DataFormat defaultInternalFormat;
	private GameType(DataFormat defaultInternalFormat)
	{
		this.defaultInternalFormat = defaultInternalFormat;
	}
	
	/**
	 * Returns the default internal format for a game type.
	 */
	public DataFormat getDefaultInternalFormat()
	{
		return defaultInternalFormat;
	}
	
}
