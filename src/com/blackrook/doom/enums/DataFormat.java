/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.enums;

/**
 * List of exportable game types for the Doom Engine's binary game data.
 * <p>
 * The DOOM type is used by Doom, Doom 2, Boom (and related base source ports), and Heretic.
 * The HEXEN type is used by Hexen and ZDoom (and related base source ports).
 * The STRIFE type is used by Strife.
 * @author Matthew Tropiano
 */
public enum DataFormat
{
	DOOM,
	HEXEN,
	STRIFE
}
