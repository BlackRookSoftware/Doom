/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.struct;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomObject;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

import static com.blackrook.doom.DoomObjectUtils.*;

/**
 * This contains the BSP tree information for a  
 * single node in the tree.
 * @author Matthew Tropiano
 */
public class BSPNode implements DoomObject
{
	/** This node's partition line's X-coordinate. */
	protected int partitionLineX;
	/** This node's partition line's Y-coordinate. */
	protected int partitionLineY;
	/** This node's partition line's change in X to the end of the line. */
	protected int partitionDeltaX;
	/** This node's partition line's change in Y to the end of the line. */
	protected int partitionDeltaY;
	/** This node's right bounding box coordinates. */
	protected int[] rightRect;
	/** This node's left bounding box coordinates. */
	protected int[] leftRect;
	
	/** Is this node's right child a leaf node (subsector index)? */
	protected boolean rightIsLeaf;
	/** This node's right child index or subsector index. */
	protected int rightIndex;
	/** Is this node's left child a leaf node (subsector index)? */
	protected boolean leftIsLeaf;
	/** This node's left child index or subsector index. */
	protected int leftIndex;
	
	/**
	 * Creates a new BSP Node.
	 */
	public BSPNode()
	{
		partitionLineX = 0;
		partitionLineY = 0;
		partitionDeltaX = 0;
		partitionDeltaY = 0;
		rightRect = new int[4];
		leftRect = new int[4];
		rightIsLeaf = false;
		rightIndex = 0;
		leftIsLeaf = false;
		leftIndex = 0;
	}
	

	/** Sets this node's partition line's X-coordinate. */
	public void setPartitionLineX(int val)			{partitionLineX = val;}
	/** Sets this node's partition line's Y-coordinate. */
	public void setPartitionLineY(int val)			{partitionLineY = val;}
	/** Sets this node's partition line's change in X to the end of the line. */
	public void setPartitionDeltaX(int val)			{partitionDeltaX = val;}
	/** Sets this node's partition line's change in Y to the end of the line. */
	public void setPartitionDeltaY(int val)			{partitionDeltaY = val;}
	/** Sets this node's right bounding box coordinates (top, bottom, left, right). */
	public void setRightRect(int top, int bottom, int left, int right)			
	{
		rightRect[0] = top;
		rightRect[1] = bottom;
		rightRect[2] = left;
		rightRect[3] = right;
	}
	/** Sets this node's left bounding box coordinates (top, bottom, left, right). */
	public void setLeftRect(int top, int bottom, int left, int right)				
	{
		leftRect[0] = top;
		leftRect[1] = bottom;
		leftRect[2] = left;
		leftRect[3] = right;
	}
	/** Sets if this node's right child is a leaf node. */
	public void setRightIsLeaf(boolean val)			{rightIsLeaf = val;}
	/** Sets this node's right child index or subsector index. */
	public void setRightIndex(int val)				{rightIndex = val;}
	/** Sets if this node's left child is a leaf node. */
	public void setLeftIsLeaf(boolean val)			{leftIsLeaf = val;}
	/** Sets this node's left child index or subsector index. */
	public void setLeftIndex(int val)				{leftIndex = val;}
	
	/** Gets this node's partition line's X-coordinate. */
	public int getPartitionLineX()					{return partitionLineX;}
	/** Gets this node's partition line's Y-coordinate. */
	public int getPartitionLineY()					{return partitionLineY;}
	/** Gets this node's partition line's change in X to the end of the line. */
	public int getPartitionDeltaX()					{return partitionDeltaX;}
	/** Gets this node's partition line's change in Y to the end of the line. */
	public int getPartitionDeltaY()					{return partitionDeltaY;}
	/** Gets this node's right bounding box coordinates (top, bottom, left, right). */
	public int[] getRightRect()						{return rightRect;}
	/** Gets this node's left bounding box coordinates (top, bottom, left, right). */
	public int[] getLeftRect()						{return leftRect;}
	/** Is this node's right child a leaf node (subsector index)? */
	public boolean rightIsLeaf()					{return rightIsLeaf;}
	/** Gets this node's right child index or subsector index. */
	public int getRightIndex()						{return rightIndex;}
	/** Is this node's left child a leaf node (subsector index)? */
	public boolean leftIsLeaf()						{return leftIsLeaf;}
	/** Gets this node's left child index or subsector index. */
	public int getLeftIndex()						{return leftIndex;}
	
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
			sw.writeShort((short)partitionLineX);
			sw.writeShort((short)partitionLineY);
			sw.writeShort((short)partitionDeltaX);
			sw.writeShort((short)partitionDeltaY);
			for (int i = 0; i < 4; i++)
				sw.writeShort((short)rightRect[i]);
			for (int i = 0; i < 4; i++)
				sw.writeShort((short)leftRect[i]);
			sw.writeShort((short)((rightIsLeaf ? 0x8000 : 0x0000) | (rightIndex & 0x07fff)));
			sw.writeShort((short)((leftIsLeaf ? 0x8000 : 0x0000) | (leftIndex & 0x07fff)));
		} catch (IOException e){}
		
		return bos.toByteArray();
	}

	/**
	 * Checks this data structure for data export integrity for the Doom format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	protected void callDoomCompatibilityCheck() throws DataExportException
	{
		checkShort("Partition Line X", partitionLineX);
		checkShort("Partition Line Y", partitionLineY);
		checkShort("Partition Delta X", partitionDeltaX);
		checkShort("Partition Delta Y", partitionDeltaY);
		checkShort("Right Box Top", rightRect[0]);
		checkShort("Right Box Bottom", rightRect[1]);
		checkShort("Right Box Left", rightRect[2]);
		checkShort("Right Box Right", rightRect[3]);
		checkShort("Left Box Top", leftRect[0]);
		checkShort("Left Box Bottom", leftRect[1]);
		checkShort("Left Box Left", leftRect[2]);
		checkShort("Left Box Right", leftRect[3]);
		checkRange("Right Child Index", 0, 32767, rightIndex);
		checkRange("Left Child Index", 0, 32767, leftIndex);
	}

	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		partitionLineX = sr.readShort();
		partitionLineY = sr.readShort();
		partitionDeltaX = sr.readShort();
		partitionDeltaY = sr.readShort();
		for (int i = 0; i < 4; i++)
			rightRect[i] = sr.readShort();
		for (int i = 0; i < 4; i++)
			leftRect[i] = sr.readShort();

		short n;

		n = sr.readShort();
		rightIsLeaf = (0x8000 & n) != 0;
		rightIndex = 0x07fff & n;
		n = sr.readShort();
		leftIsLeaf = (0x8000 & n) != 0;
		leftIndex = 0x07fff & n;
	}

	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

	/**
	 * Returns the length of this structure in Doom-formatted bytes. 
	 */
	public static int getDoomLength()
	{
		return 28;
	}

}
