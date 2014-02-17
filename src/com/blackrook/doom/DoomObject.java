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
 * Base abstract enclosure interface for all Doom data structures.
 * @author Matthew Tropiano
 */
public interface DoomObject
{
	/**
	 * Returns <code>true</code> if this data structure can 
	 * be reliably exported to Doom, <code>false</code> otherwise.
	 */
	public boolean isDoomCompatible();
	
	/**
	 * Converts this structure into a byte form that Doom can read.
	 * @return a byte array of the serialized information.
	 * @throws DataExportException if writing the data structure this way requires a loss of data.
	 */
	public byte[] getDoomBytes() throws DataExportException;

	/**
	 * Sets this structure's data using a bytestring that Doom can read.
	 * @param in	the InputStream to read raw bytes from.
	 * @throws IOException if an error occurs during the read.
	 */
	public void readDoomBytes(InputStream in) throws IOException;

	/**
	 * Exports this structure's data using a bytestring that Doom can read.
	 * @param out	the OutputStream to write raw bytes to.
	 * @throws IOException if an error occurs during the write.
	 * @throws DataExportException if writing the data structure this way requires a loss of data.
	 */
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException;

}
