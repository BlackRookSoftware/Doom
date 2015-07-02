/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.udmf;

import com.blackrook.commons.hash.CaseInsensitiveHashMap;

/**
 * An abstract representation of a UDMF structure.
 * Each <code>identifier = value;</code> field describes a (String, String) pair.
 * This structure does not hold a name identifying what the structure type is.
 * @author Matthew Tropiano
 */
public class UDMFStruct extends CaseInsensitiveHashMap<String>
{
	/**
	 * Creates a new UDMFStruct with capacity 10, rehash ratio 0.75.
	 */
	public UDMFStruct()
	{
		this(DEFAULT_CAPACITY, DEFAULT_REHASH);
	}
	
	/**
	 * Creates a new UDMFStruct with capacity <i>cap</i> and rehash ratio 0.75. 
	 * @param cap	the capacity. cannot be negative.
	 */
	public UDMFStruct(int cap)
	{
		this(cap, DEFAULT_REHASH);
	}
	
	/**
	 * Creates a new UDMFStruct.
	 * @param cap	the capacity. cannot be negative.
	 * @param ratio	the ratio of capacity/tablesize. if this ratio is exceeded, the table's capacity is expanded, and the table is rehashed.
	 * @throws IllegalArgumentException if capacity is negative or ratio is 0 or less.
	 */
	public UDMFStruct(int cap, float ratio)
	{
		super(cap,ratio);
	}
	
	/**
	 * Adds a new entry to this structure.
	 * If the entry already exists in this structure, its value is replaced.
	 * @param key	the key.
	 * @param value	the value associated with this key.
	 */
	public synchronized void put(String key, int value)
	{
		put(key,String.valueOf(value));
	}
	
	/**
	 * Adds a new entry to this structure.
	 * If the entry already exists in this structure, its value is replaced.
	 * @param key	the key.
	 * @param value	the value associated with this key.
	 */
	public synchronized void put(String key, float value)
	{
		put(key,String.valueOf(value));
	}
	
	/**
	 * Adds a new entry to this structure.
	 * If the entry already exists in this structure, its value is replaced.
	 * @param key	the key.
	 * @param value	the value associated with this key.
	 */
	public synchronized void put(String key, boolean value)
	{
		put(key,String.valueOf(value));
	}
	
	/**
	 * Adds a new entry to this structure.
	 * If the entry already exists in this structure, its value is replaced.
	 * @param key	the key.
	 * @param value	the value associated with this key.
	 */
	@Override
	public synchronized void put(String key, String value)
	{
		if (containsKey(key))
			removeUsingKey(key);
		super.put(key,value);
	}
	
	/**
	 * Gets a value from this structure as a String.
	 * If the value does not exist, this returns the empty string.
	 * If the value was originally added as an integer, this returns it represented as a String.
	 * If the value was originally added as a float, this returns it represented as a String.
	 * If the value was originally added as an boolean, this returns it represented as a String ("true" or "false").
	 */
	public String getString(String key)
	{
		return getString(key, "");
	}
	
	/**
	 * Gets a value from this structure as an integer.
	 * If the value does not exist, this returns 0.
	 * If the value was originally added as a String, this returns it as 0.
	 * If the value was originally added as a float, this returns it as the floor of that float value.
	 * If the value was originally added as an boolean, this returns it as 0 if false, 1 if true.
	 */
	public int getInt(String key)
	{
		return getInt(key, 0);
	}
	
	/**
	 * Gets a value from this structure as a float.
	 * If the value does not exist, this returns 0.0.
	 * If the value was originally added as a String, this returns it as 0.0.
	 * If the value was originally added as a integer, this returns it as a float with a zero mantissa.
	 * If the value was originally added as an boolean, this returns it as 0.0 if false, 1.0 if true.
	 */
	public float getFloat(String key)
	{
		return getFloat(key, 0.0f);
	}
	
	/**
	 * Gets a value from this structure as a boolean.
	 * If the value does not exist, this returns false.
	 * If the value was originally added as a String, this returns true if this value equals "true", ignoring case.
	 */
	public boolean getBoolean(String key)
	{
		return getBoolean(key, false);
	}

	/**
	 * Gets a value from this structure as a String.
	 * Returns the value in <code>def</code> if the key does not exist.
	 * If the value was originally added as an integer, this returns it represented as a String.
	 * If the value was originally added as a float, this returns it represented as a String.
	 * If the value was originally added as an boolean, this returns it represented as a String ("true" or "false").
	 */
	public String getString(String key, String def)
	{
		String s = get(key);
		if (s == null)
			return def;
		return s;
	}

	/**
	 * Gets a value from this structure as an integer.
	 * Returns the value in <code>def</code> if the key does not exist.
	 * If the value was originally added as a String, this returns it as 0.
	 * If the value was originally added as a float, this returns it as the floor of that float value.
	 * If the value was originally added as an boolean, this returns it as 0 if false, 1 if true.
	 */
	public int getInt(String key, int def)
	{
		String s = get(key);
		if (s == null)
			return def;
		try {
			if ((s.indexOf('x') >= 0) || (s.indexOf('X') >= 0))
				return Integer.parseInt(s.substring(Math.max(s.indexOf('x'), s.indexOf('X')) + 1), 16);
			else
				return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			if (s.equalsIgnoreCase("true"))
				return 1;
			return 0;
		}
	}

	/**
	 * Gets a value from this structure as a float.
	 * Returns the value in <code>def</code> if the key does not exist.
	 * If the value was originally added as a String, this returns it as 0.0.
	 * If the value was originally added as an integer, this returns it as a float with a zero mantissa.
	 * If the value was originally added as an boolean, this returns it as 0.0 if false, 1.0 if true.
	 */
	public float getFloat(String key, float def)
	{
		String s = get(key);
		if (s == null)
			return def;
		try {
			if ((s.indexOf('x') >= 0) || (s.indexOf('X') >= 0))
				return (float)Integer.parseInt(s.substring(Math.max(s.indexOf('x'), s.indexOf('X')) + 1), 16);
			else
				return Float.parseFloat(s);
		} catch (NumberFormatException e) {
			if (s.equalsIgnoreCase("true"))
				return 1.0f;
			return 0.0f;
		}
	}

	/**
	 * Gets a value from this structure as a boolean.
	 * Returns the value in <code>def</code> if the key does not exist.
	 * If the value was originally added as a String, this returns true if this value equals "true", ignoring case.
	 */
	public boolean getBoolean(String key, boolean def)
	{
		String s = get(key);
		if (s == null)
			return def;
		return Boolean.parseBoolean(s);
	}
	

	
}
