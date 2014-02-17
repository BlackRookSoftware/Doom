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
 * Exception thrown on a data export error of a Doom data structure.
 * Commonly occurs when a data structure contains a value that cannot
 * be properly exported in such a way that data loss or loss of unit
 * significance will occur.
 * @author Matthew Tropiano
 */
public class DataExportException extends IOException
{
	private static final long serialVersionUID = 4909358686922122514L;

	public DataExportException()
	{
		this("A data export exception has occurred.");
	}
	
	public DataExportException(String message)
	{
		super(message);
	}
}
