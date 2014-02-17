/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Base abstract enclosure interface for all Strife data structures.
 * @author Matthew Tropiano
 */
public interface StrifeObject
{
	/**
	 * Returns <code>true</code> if this data structure can 
	 * be reliably exported to Strife, <code>false</code> otherwise.
	 */
	public boolean isStrifeCompatible();
		
	/**
	 * Converts this structure into a byte form that Strife can read.
	 * @return a byte array of the information.
	 * @throws DataExportException if writing the data structure this way requires a loss of data.
	 */
	public byte[] getStrifeBytes() throws DataExportException;

	/**
	 * Sets this structure's data using a bytestring that Strife can read.
	 * @param in	the InputStream to read raw bytes from.
	 * @throws IOException if an error occurs during the read.
	 */
	public void readStrifeBytes(InputStream in) throws IOException;
	
	/**
	 * Exports this structure's data using a bytestring that Strife can read.
	 * @param out	the OutputStream to write raw bytes to.
	 * @throws IOException if an error occurs during the write.
	 * @throws DataExportException if writing the data structure this way requires a loss of data.
	 */
	public void writeStrifeBytes(OutputStream out) throws IOException, DataExportException;
	
}
