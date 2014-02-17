/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.struct;

import static com.blackrook.doom.DoomObjectUtils.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.blackrook.commons.ObjectPair;
import com.blackrook.commons.grid.SparseQueueGridMap;
import com.blackrook.commons.hash.HashedQueueMap;
import com.blackrook.commons.linkedlist.Queue;
import com.blackrook.commons.math.Pair;
import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomObject;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * Representation of the BLOCKMAP lump for a map.
 * This aids in collision detection for linedefs.
 * @author Matthew Tropiano
 */
public class Blockmap implements DoomObject
{
	/** Grid origin X-coordinate. */
	private int startX;
	/** Grid origin Y-coordinate. */
	private int startY;

	/** Grid mapping to linedef indices. */
	private SparseQueueGridMap<Integer> innerMap;
	
	/**
	 * Creates a new Blockmap, startX and startY set to 0.
	 */
	public Blockmap()
	{
		this(0, 0);
	}
	
	/**
	 * Creates a new Blockmap.
	 * @param startX	the grid lower-left start position (x-axis).
	 * @param startY	the grid lower-left start position (y-axis).
	 */
	public Blockmap(int startX, int startY)
	{
		this.startX = startX;
		this.startY = startY;
		
		innerMap = new SparseQueueGridMap<Integer>(65535/128, 65535/128);
	}
	
	/**
	 * Adds a linedef index to this blockmap.
	 * @param x	the grid row.
	 * @param y	the grid column.
	 * @param linedefIndex	the linedef index to add.
	 */
	public void addIndex(int x, int y, int linedefIndex)
	{
		if (x < 0 || y < 0 || linedefIndex < 0)
			throw new IllegalArgumentException("Column, Row, or Index is out of range.");
		innerMap.enqueue(x, y, linedefIndex);
	}
	
	/**
	 * Removes a linedef index to this blockmap.
	 * @param x	the grid column.
	 * @param y	the grid row.
	 * @param linedefIndex	the linedef index to remove.
	 */
	public boolean removeIndex(int x, int y, int linedefIndex)
	{
		if (x < 0 || y < 0 || linedefIndex < 0)
			throw new IllegalArgumentException("Column, Row, or Index is out of range.");
		return innerMap.get(x, y).remove(linedefIndex);
	}

	/**
	 * Returns the list of linedef indices in a particular block.
	 * @param x	the grid column.
	 * @param y	the grid row.
	 */
	public Queue<Integer> getIndexList(int x, int y)
	{
		return innerMap.get(x, y);
	}

	/**
	 * Returns the list of linedef indices in a particular block using map position.
	 * May return null if the point lies completely outside the grid.
	 * @param posX	the map position, X-coordinate.
	 * @param posY	the map position, Y-coordinate.
	 */
	public Queue<Integer> getIndexListAtPosition(float posX, float posY)
	{
		int x = getColumnByMapPosition(posX);
		int y = getRowByMapPosition(posY);
		return getIndexList(x, y);
	}

	/**
	 * Returns the map position start, X coordinate.
	 */
	public float getStartX()
	{
		return startX;
	}

	/**
	 * Returns the map position start, Y coordinate.
	 */
	public float getStartY()
	{
		return startY;
	}

	/**
	 * Returns the column index used by a particular map position,
	 * according to this grid's startX value.
	 * If posX is < startX, this returns -1.
	 */
	protected int getColumnByMapPosition(float posX)
	{
		if (posX < startX)
			return -1;
		return (int)((posX - startX) / 128);
	}
	
	/**
	 * Returns the row index used by a particular map position,
	 * according to this grid's startY value.
	 * If posY is < startY, this returns -1.
	 */
	protected int getRowByMapPosition(float posY)
	{
		if (posY < startY)
			return -1;
		return (int)((posY - startY) / 128);
	}

	@Override
	public boolean isDoomCompatible()
	{
		try {
			callDoomCompatibilityCheck();
		} catch (DataExportException e) {
			return false;
		}
		return true;
	}

	@Override
	public byte[] getDoomBytes() throws DataExportException
	{
		callDoomCompatibilityCheck();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
		try{
			sw.writeShort((short)startX);
			sw.writeShort((short)startY);
			int max_x = 0;
			int max_y = 0;

			for (ObjectPair<Pair, Queue<Integer>> hp : innerMap)
			{
				Pair p = hp.getKey();
				max_x = Math.max(max_x, p.x);
				max_y = Math.max(max_y, p.y);
			}
			max_x++;
			max_y++;

			sw.writeUnsignedShort(max_x);
			sw.writeUnsignedShort(max_y);
			
			// convert linedef indices for offset calculation
			short[][][] shorts = new short[max_x][max_y][];
			
			for (int x = 0; x < max_x; x++)
				for (int y = 0; y < max_y; y++)
				{
					Queue<Integer> list = innerMap.get(x,y);
					if (list != null)
					{
						shorts[x][y] = new short[list.size()];
						int n = 0;
						for (Integer s : list)
							shorts[x][y][n++] = s.shortValue();
					}
					else
						shorts[x][y] = new short[0];
				}
			
			// set starting offset
			short offset = (short)(4 + (max_x*max_y));

			// write offset table
			for (int x = 0; x < max_x; x++)
				for (int y = 0; y < max_y; y++)
				{
					sw.writeShort(offset);
					offset += 2 + shorts[x][y].length;
				}
			
			// write index lists
			for (int x = 0; x < max_x; x++)
				for (int y = 0; y < max_y; y++)
				{
					sw.writeShort((short)0);
					for (int n = 0; n < shorts[x][y].length; n++)
						sw.writeShort(shorts[x][y][n]);
					sw.writeShort((short)-1);
				}

		} catch (IOException e){}
		
		return bos.toByteArray();
	}

	/**
	 * Checks this data structure for data export integrity for the Doom format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	protected void callDoomCompatibilityCheck() throws DataExportException
	{
		checkShort("Grid start X", (int)startX);
		checkShort("Grid start Y", (int)startY);
		int max_x = 0;
		int max_y = 0;
		int idx = 0;
		for (ObjectPair<Pair, Queue<Integer>> hp : innerMap)
		{
			Pair p = hp.getKey();
			max_x = Math.max(max_x, p.x);
			max_y = Math.max(max_y, p.y);
			for (Integer i : hp.getValue())
				idx = Math.max(idx,i);
		}
		checkShortUnsigned("Columns", max_x);
		checkShortUnsigned("Rows", max_y);
		checkShortUnsigned("Linedef index", idx);
	}

	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		innerMap.clear();
		startX = sr.readShort();
		startY = sr.readShort();
		int max_x = sr.readUnsignedShort();
		int max_y = sr.readUnsignedShort();

		// read offset table
		short[] indices = sr.readShorts(max_x*max_y);
		
		// data must be treated as a stream: find highest short offset so that the reading can stop.
		int offMax = -1;
		for (short s : indices)
		{
			int o = s & 0x0ffff;
			offMax = o > offMax ? o : offMax;
		}
		
		// precache linedef lists at each particular offset: blockmap may be compressed.
		HashedQueueMap<Integer, Integer> indexList = new HashedQueueMap<Integer, Integer>();
		int index = 4 + (max_x*max_y);
		while (index <= offMax)
		{
			int nindex = index;
			short n = sr.readShort();
			nindex++;
			
			if (n != 0)
				throw new IOException("Blockmap list at short index "+index+" should start with 0.");

			n = sr.readShort();
			nindex++;
			while (n != -1)
			{
				indexList.enqueue(index, (n & 0x0ffff));
				n = sr.readShort();
				nindex++;
			}
			
			index = nindex;
		}

		// read into internal blockmap table.
		for (int i = 0; i < max_x; i++)
			for (int j = 0; j < max_y; j++)
			{
				// "touch" entry. this is so the maximum column/row
				// still gets written on call to getDoomBytes()
				innerMap.set(i, j, new Queue<Integer>());			
				
				// add index list to map.
				int ind = indices[(i*max_y)+j];
				Queue<Integer> list = indexList.get(ind);
				if (list != null) for (Integer line : list)
					addIndex(i, j, line);
			}
	}

	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}
	
}
