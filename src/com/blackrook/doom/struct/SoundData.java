/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.struct;

import static com.blackrook.doom.DoomObjectUtils.*;

import java.io.*;

import com.blackrook.commons.math.RMath;
import com.blackrook.commons.math.wave.CustomWaveForm;
import com.blackrook.commons.math.wave.CustomWaveForm.InterpolationType;
import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomObject;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * This class holds digital sound information.
 * The format that this reads is the DMX PCM Format, by Digital Expressions, Inc.,
 * written by Paul Radek. Doom uses this format for storing sound data.
 * @author Matthew Tropiano
 * @since 2.6.0
 */
public class SoundData implements DoomObject
{
	/** 8 kHz Sampling rate. */
	public static final int SAMPLERATE_8KHZ = 8000;
	/** 11 kHz Sampling rate. */
	public static final int SAMPLERATE_11KHZ = 11025;
	/** 22 kHz Sampling rate. */
	public static final int SAMPLERATE_22KHZ = 22050;
	/** 44 kHz Sampling rate. */
	public static final int SAMPLERATE_44KHZ = 44100;
	
	/** Sampling rate in Samples per Second. */
	private int sampleRate;
	/** Sound samples put in a native waveform. */
	private CustomWaveForm waveForm;
	
	/**
	* Creates a new, blank SoundData.
	*/	
	public SoundData()
	{
		sampleRate = SAMPLERATE_11KHZ;
		waveForm = new CustomWaveForm(new double[]{0.0});
	}

	/**
	 * Creates a new SoundData using a set of discrete samples
	 * at a particular sampling rate.
	 * @param sampleRate the sampling rate of this sound in samples per second.
	 * @param samples the discrete samples.
	 */
	public SoundData(int sampleRate, double[] samples)
	{
		this.sampleRate = sampleRate;
		waveForm = new CustomWaveForm(samples);
	}
	
	/**
	 * Gets the sampling rate of this sound clip in samples per second.
	 */
	public int getSampleRate()
	{
		return sampleRate;
	}

	/**
	 * Sets the sampling rate of this sound clip in samples per second.
	 * This does NOT change the underlying waveform!
	 * @param sampleRate the new sampling rate.
	 */
	public void setSampleRate(int sampleRate)
	{
		this.sampleRate = sampleRate;
	}

	/**
	 * Changes the sampling rate of this sound clip,
	 * and resamples the underlying data as well.
	 * @param newSamplingRate the new sampling rate to use and resample to.
	 */
	public void resample(int newSamplingRate)
	{
		double change = (double)newSamplingRate / (double)sampleRate;
		this.sampleRate = newSamplingRate;
		waveForm.resampleInline((int)(waveForm.getSampleCount() * change));
	}
	
	/**
	 * Changes the sampling rate of this sound clip, and resamples the underlying data as well.
	 * @param interpolation the interpolation type to use.
	 * @param newSamplingRate the new sampling rate to use and resample to.
	 */
	public void resample(InterpolationType interpolation, int newSamplingRate)
	{
		waveForm.setInterpolationType(interpolation);
		resample(newSamplingRate);
	}
	
	/**
	 * Returns the waveform that holds all of the samples in this sound clip.
	 * @see CustomWaveForm
	 */
	public CustomWaveForm getWaveForm()
	{
		return waveForm;
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
		final byte[] PADDING = new byte[]{
			0x7F, 0x7F, 0x7F, 0x7F,
			0x7F, 0x7F, 0x7F, 0x7F,
			0x7F, 0x7F, 0x7F, 0x7F,
			0x7F, 0x7F, 0x7F, 0x7F
	};
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
		try{
			sw.writeUnsignedShort(3); // format type
			sw.writeUnsignedShort(sampleRate);
			sw.writeUnsignedInteger(waveForm.getSampleCount() + 32);
			sw.writeBytes(PADDING);
			for (int i = 0; i < waveForm.getSampleCount(); i++)
			{
				sw.writeUnsignedByte((int)(((waveForm.getSampleValue(i) + 1.0) / 2.0) * 255.0));
			}
			sw.writeBytes(PADDING);
		} catch (IOException e){}
		
		return bos.toByteArray();
	}

	/**
	 * Checks this data structure for data export integrity for the Doom format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	protected void callDoomCompatibilityCheck() throws DataExportException
	{
		checkShortUnsigned("Sample Rate", sampleRate);
	}

	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in, SuperReader.LITTLE_ENDIAN);
		int type = sr.readUnsignedShort();
		if (type != 3)
			throw new IOException("Not a sound clip.");
		
		sampleRate = sr.readUnsignedShort();
		int sampleCount = (int)sr.readUnsignedInt();
		
		waveForm = new CustomWaveForm(sampleCount - 32);
		waveForm.setAmplitude(1.0);
		
		sr.readBytes(16); // padding
		
		byte[] b = sr.readBytes(sampleCount - 32);
		for (int i = 0; i < b.length; i++)
			waveForm.setSampleValue(i, (RMath.getInterpolationFactor((b[i] & 0x0ff), 0, 255) * 2.0) - 1.0);
		
		sr.readBytes(16); // padding
	}

	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

	@Override
	public String toString()
	{
		return String.format("SOUND Sample Rate: %d Hz, %d Samples, 8-bit", sampleRate, waveForm.getSampleCount());
	}
	
}
