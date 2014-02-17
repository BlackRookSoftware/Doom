/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom;

import java.io.IOException;

/**
 * Main Exception class for DoomStruct.
 * @author Matthew Tropiano
 */
public class WadException extends IOException
{
	private static final long serialVersionUID = 3794613771393304327L;

	/**
	 * Main exception constructor. Calls Exception superconstructor with: "An unknown error 
	 * has occured."
	 */
	public WadException()
	{
		super("An unknown error has occurred.");
	}

	/**
	 * Secondary exception constructor. Calls Exception superconstructor with String <i>s</i>.
	 */
	public WadException(String s)
	{
		super(s);
	}
}
