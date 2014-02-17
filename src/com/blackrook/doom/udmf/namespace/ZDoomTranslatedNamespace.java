/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.udmf.namespace;

/**
 * UDMF namespace for ZDoom (Translated) port data.
 * Be forewarned that any specials that are set in a ZDoomTranslated
 * structure does not necessarily belie the ACTUAL specials or thing types:
 * it depends on the target game. 
 * @author Matthew Tropiano
 */
public class ZDoomTranslatedNamespace extends ZDoomCommonNamespace
{

	@Override
	public String getName()
	{
		return "ZDoomTranslated";
	}

}
