/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.udmf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * Writes UDMF data.
 * @author Matthew Tropiano
 */
public final class UDMFWriter
{
	private UDMFWriter() {}
	
	/**
	 * Writes UDMF-formatted data into an OutputStream.
	 * Does not close the OutputStream at the end of the write.
	 * @param out the OutputStream to write to.
	 * @throws IOException if the output stream cannot be written to.
	 */
	public static void writeData(UDMFTable table, OutputStream out) throws IOException
	{
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF8"), true);
		
		writeFields(table.getGlobalFields(), pw, "");

		for (String strName : table.getAllStructNames())
		{
			int x = 0;
			for (UDMFStruct struct : table.getStructs(strName))
			{
				writeStructStart(strName, pw, x, "");
				writeFields(struct, pw, "\t");
				writeStructEnd(strName, pw, x, "");
				x++;
			}
		}
		
	}

	/**
	 * Writes the fields out to the stream.
	 */
	private static void writeFields(UDMFStruct struct, PrintWriter pw, String lineprefix)
	{
		Iterator<String> it = struct.keyIterator();
		while(it.hasNext())
		{
			String fieldName = it.next();
			pw.println(lineprefix + fieldName + " = "+renderFieldData(struct.get(fieldName))+";");
		}
	}
	
	/**
	 * Starts the structure.
	 */
	private static void writeStructStart(String name, PrintWriter pw, int count, String lineprefix)
	{
		pw.println(lineprefix + name + " // " + count);
		pw.println(lineprefix + "{");
	}
	
	/**
	 * Ends the structure.
	 */
	private static void writeStructEnd(String name, PrintWriter pw, int count, String lineprefix)
	{
		pw.println(lineprefix + "}");
		pw.println();
	}
	
	private static String renderFieldData(String data)
	{
		if (data == null)
			return "\"\"";
		
		try {
			float f = Float.parseFloat(data);
			if (noMantissa(f))
				return ((int)f)+"";
			else
				return f+"";
		} catch (NumberFormatException e) {
			if (data.equalsIgnoreCase("true"))
				return "true";
			else if (data.equalsIgnoreCase("false"))
				return "false";
			else
				return "\""+data+"\"";
		}
	}
	
	private static boolean noMantissa(float f)
	{
		return f - Math.floor(f) == 0.0;
	}
	
}
