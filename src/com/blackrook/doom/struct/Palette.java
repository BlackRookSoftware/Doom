/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.struct;

import java.awt.Color;
import java.io.*;
import java.util.*;

import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomObject;
import com.blackrook.doom.util.DoomUtil;
import com.blackrook.io.SuperReader;

/**
 * The palette that makes up the Doom Engine's color palette.
 * All colors are stored as java.awt.Colors and are all opaque.
 * Normally, this contains an indexed set of 256 colors. 
 * @author Matthew Tropiano
 */
public class Palette implements DoomObject, Iterable<Color>
{
	/** 
	 * Default Doom palette.
	 */
	public static final Palette DOOM = new Palette(){{
		setColor(0,new Color(0,0,0));
		setColor(1,new Color(31,23,11));
		setColor(2,new Color(23,15,7));
		setColor(3,new Color(75,75,75));
		setColor(4,new Color(255,255,255));
		setColor(5,new Color(27,27,27));
		setColor(6,new Color(19,19,19));
		setColor(7,new Color(11,11,11));
		setColor(8,new Color(7,7,7));
		setColor(9,new Color(47,55,31));
		setColor(10,new Color(35,43,15));
		setColor(11,new Color(23,31,7));
		setColor(12,new Color(15,23,0));
		setColor(13,new Color(79,59,43));
		setColor(14,new Color(71,51,35));
		setColor(15,new Color(63,43,27));
		setColor(16,new Color(255,183,183));
		setColor(17,new Color(247,171,171));
		setColor(18,new Color(243,163,163));
		setColor(19,new Color(235,151,151));
		setColor(20,new Color(231,143,143));
		setColor(21,new Color(223,135,135));
		setColor(22,new Color(219,123,123));
		setColor(23,new Color(211,115,115));
		setColor(24,new Color(203,107,107));
		setColor(25,new Color(199,99,99));
		setColor(26,new Color(191,91,91));
		setColor(27,new Color(187,87,87));
		setColor(28,new Color(179,79,79));
		setColor(29,new Color(175,71,71));
		setColor(30,new Color(167,63,63));
		setColor(31,new Color(163,59,59));
		setColor(32,new Color(155,51,51));
		setColor(33,new Color(151,47,47));
		setColor(34,new Color(143,43,43));
		setColor(35,new Color(139,35,35));
		setColor(36,new Color(131,31,31));
		setColor(37,new Color(127,27,27));
		setColor(38,new Color(119,23,23));
		setColor(39,new Color(115,19,19));
		setColor(40,new Color(107,15,15));
		setColor(41,new Color(103,11,11));
		setColor(42,new Color(95,7,7));
		setColor(43,new Color(91,7,7));
		setColor(44,new Color(83,7,7));
		setColor(45,new Color(79,0,0));
		setColor(46,new Color(71,0,0));
		setColor(47,new Color(67,0,0));
		setColor(48,new Color(255,235,223));
		setColor(49,new Color(255,227,211));
		setColor(50,new Color(255,219,199));
		setColor(51,new Color(255,211,187));
		setColor(52,new Color(255,207,179));
		setColor(53,new Color(255,199,167));
		setColor(54,new Color(255,191,155));
		setColor(55,new Color(255,187,147));
		setColor(56,new Color(255,179,131));
		setColor(57,new Color(247,171,123));
		setColor(58,new Color(239,163,115));
		setColor(59,new Color(231,155,107));
		setColor(60,new Color(223,147,99));
		setColor(61,new Color(215,139,91));
		setColor(62,new Color(207,131,83));
		setColor(63,new Color(203,127,79));
		setColor(64,new Color(191,123,75));
		setColor(65,new Color(179,115,71));
		setColor(66,new Color(171,111,67));
		setColor(67,new Color(163,107,63));
		setColor(68,new Color(155,99,59));
		setColor(69,new Color(143,95,55));
		setColor(70,new Color(135,87,51));
		setColor(71,new Color(127,83,47));
		setColor(72,new Color(119,79,43));
		setColor(73,new Color(107,71,39));
		setColor(74,new Color(95,67,35));
		setColor(75,new Color(83,63,31));
		setColor(76,new Color(75,55,27));
		setColor(77,new Color(63,47,23));
		setColor(78,new Color(51,43,19));
		setColor(79,new Color(43,35,15));
		setColor(80,new Color(239,239,239));
		setColor(81,new Color(231,231,231));
		setColor(82,new Color(223,223,223));
		setColor(83,new Color(219,219,219));
		setColor(84,new Color(211,211,211));
		setColor(85,new Color(203,203,203));
		setColor(86,new Color(199,199,199));
		setColor(87,new Color(191,191,191));
		setColor(88,new Color(183,183,183));
		setColor(89,new Color(179,179,179));
		setColor(90,new Color(171,171,171));
		setColor(91,new Color(167,167,167));
		setColor(92,new Color(159,159,159));
		setColor(93,new Color(151,151,151));
		setColor(94,new Color(147,147,147));
		setColor(95,new Color(139,139,139));
		setColor(96,new Color(131,131,131));
		setColor(97,new Color(127,127,127));
		setColor(98,new Color(119,119,119));
		setColor(99,new Color(111,111,111));
		setColor(100,new Color(107,107,107));
		setColor(101,new Color(99,99,99));
		setColor(102,new Color(91,91,91));
		setColor(103,new Color(87,87,87));
		setColor(104,new Color(79,79,79));
		setColor(105,new Color(71,71,71));
		setColor(106,new Color(67,67,67));
		setColor(107,new Color(59,59,59));
		setColor(108,new Color(55,55,55));
		setColor(109,new Color(47,47,47));
		setColor(110,new Color(39,39,39));
		setColor(111,new Color(35,35,35));
		setColor(112,new Color(119,255,111));
		setColor(113,new Color(111,239,103));
		setColor(114,new Color(103,223,95));
		setColor(115,new Color(95,207,87));
		setColor(116,new Color(91,191,79));
		setColor(117,new Color(83,175,71));
		setColor(118,new Color(75,159,63));
		setColor(119,new Color(67,147,55));
		setColor(120,new Color(63,131,47));
		setColor(121,new Color(55,115,43));
		setColor(122,new Color(47,99,35));
		setColor(123,new Color(39,83,27));
		setColor(124,new Color(31,67,23));
		setColor(125,new Color(23,51,15));
		setColor(126,new Color(19,35,11));
		setColor(127,new Color(11,23,7));
		setColor(128,new Color(191,167,143));
		setColor(129,new Color(183,159,135));
		setColor(130,new Color(175,151,127));
		setColor(131,new Color(167,143,119));
		setColor(132,new Color(159,135,111));
		setColor(133,new Color(155,127,107));
		setColor(134,new Color(147,123,99));
		setColor(135,new Color(139,115,91));
		setColor(136,new Color(131,107,87));
		setColor(137,new Color(123,99,79));
		setColor(138,new Color(119,95,75));
		setColor(139,new Color(111,87,67));
		setColor(140,new Color(103,83,63));
		setColor(141,new Color(95,75,55));
		setColor(142,new Color(87,67,51));
		setColor(143,new Color(83,63,47));
		setColor(144,new Color(159,131,99));
		setColor(145,new Color(143,119,83));
		setColor(146,new Color(131,107,75));
		setColor(147,new Color(119,95,63));
		setColor(148,new Color(103,83,51));
		setColor(149,new Color(91,71,43));
		setColor(150,new Color(79,59,35));
		setColor(151,new Color(67,51,27));
		setColor(152,new Color(123,127,99));
		setColor(153,new Color(111,115,87));
		setColor(154,new Color(103,107,79));
		setColor(155,new Color(91,99,71));
		setColor(156,new Color(83,87,59));
		setColor(157,new Color(71,79,51));
		setColor(158,new Color(63,71,43));
		setColor(159,new Color(55,63,39));
		setColor(160,new Color(255,255,115));
		setColor(161,new Color(235,219,87));
		setColor(162,new Color(215,187,67));
		setColor(163,new Color(195,155,47));
		setColor(164,new Color(175,123,31));
		setColor(165,new Color(155,91,19));
		setColor(166,new Color(135,67,7));
		setColor(167,new Color(115,43,0));
		setColor(168,new Color(255,255,255));
		setColor(169,new Color(255,219,219));
		setColor(170,new Color(255,187,187));
		setColor(171,new Color(255,155,155));
		setColor(172,new Color(255,123,123));
		setColor(173,new Color(255,95,95));
		setColor(174,new Color(255,63,63));
		setColor(175,new Color(255,31,31));
		setColor(176,new Color(255,0,0));
		setColor(177,new Color(239,0,0));
		setColor(178,new Color(227,0,0));
		setColor(179,new Color(215,0,0));
		setColor(180,new Color(203,0,0));
		setColor(181,new Color(191,0,0));
		setColor(182,new Color(179,0,0));
		setColor(183,new Color(167,0,0));
		setColor(184,new Color(155,0,0));
		setColor(185,new Color(139,0,0));
		setColor(186,new Color(127,0,0));
		setColor(187,new Color(115,0,0));
		setColor(188,new Color(103,0,0));
		setColor(189,new Color(91,0,0));
		setColor(190,new Color(79,0,0));
		setColor(191,new Color(67,0,0));
		setColor(192,new Color(231,231,255));
		setColor(193,new Color(199,199,255));
		setColor(194,new Color(171,171,255));
		setColor(195,new Color(143,143,255));
		setColor(196,new Color(115,115,255));
		setColor(197,new Color(83,83,255));
		setColor(198,new Color(55,55,255));
		setColor(199,new Color(27,27,255));
		setColor(200,new Color(0,0,255));
		setColor(201,new Color(0,0,227));
		setColor(202,new Color(0,0,203));
		setColor(203,new Color(0,0,179));
		setColor(204,new Color(0,0,155));
		setColor(205,new Color(0,0,131));
		setColor(206,new Color(0,0,107));
		setColor(207,new Color(0,0,83));
		setColor(208,new Color(255,255,255));
		setColor(209,new Color(255,235,219));
		setColor(210,new Color(255,215,187));
		setColor(211,new Color(255,199,155));
		setColor(212,new Color(255,179,123));
		setColor(213,new Color(255,163,91));
		setColor(214,new Color(255,143,59));
		setColor(215,new Color(255,127,27));
		setColor(216,new Color(243,115,23));
		setColor(217,new Color(235,111,15));
		setColor(218,new Color(223,103,15));
		setColor(219,new Color(215,95,11));
		setColor(220,new Color(203,87,7));
		setColor(221,new Color(195,79,0));
		setColor(222,new Color(183,71,0));
		setColor(223,new Color(175,67,0));
		setColor(224,new Color(255,255,255));
		setColor(225,new Color(255,255,215));
		setColor(226,new Color(255,255,179));
		setColor(227,new Color(255,255,143));
		setColor(228,new Color(255,255,107));
		setColor(229,new Color(255,255,71));
		setColor(230,new Color(255,255,35));
		setColor(231,new Color(255,255,0));
		setColor(232,new Color(167,63,0));
		setColor(233,new Color(159,55,0));
		setColor(234,new Color(147,47,0));
		setColor(235,new Color(135,35,0));
		setColor(236,new Color(79,59,39));
		setColor(237,new Color(67,47,27));
		setColor(238,new Color(55,35,19));
		setColor(239,new Color(47,27,11));
		setColor(240,new Color(0,0,83));
		setColor(241,new Color(0,0,71));
		setColor(242,new Color(0,0,59));
		setColor(243,new Color(0,0,47));
		setColor(244,new Color(0,0,35));
		setColor(245,new Color(0,0,23));
		setColor(246,new Color(0,0,11));
		setColor(247,new Color(0,0,0));
		setColor(248,new Color(255,159,67));
		setColor(249,new Color(255,231,75));
		setColor(250,new Color(255,123,255));
		setColor(251,new Color(255,0,255));
		setColor(252,new Color(207,0,207));
		setColor(253,new Color(159,0,155));
		setColor(254,new Color(111,0,107));
		setColor(255,new Color(167,107,107));
}};
	
	/** 
	 * Default Heretic palette.
	 */
	public static final Palette HERETIC = new Palette(){{
		setColor(0,new Color(2,2,2));
		setColor(1,new Color(2,2,2));
		setColor(2,new Color(16,16,16));
		setColor(3,new Color(24,24,24));
		setColor(4,new Color(31,31,31));
		setColor(5,new Color(36,36,36));
		setColor(6,new Color(44,44,44));
		setColor(7,new Color(48,48,48));
		setColor(8,new Color(55,55,55));
		setColor(9,new Color(63,63,63));
		setColor(10,new Color(70,70,70));
		setColor(11,new Color(78,78,78));
		setColor(12,new Color(86,86,86));
		setColor(13,new Color(93,93,93));
		setColor(14,new Color(101,101,101));
		setColor(15,new Color(108,108,108));
		setColor(16,new Color(116,116,116));
		setColor(17,new Color(124,124,124));
		setColor(18,new Color(131,131,131));
		setColor(19,new Color(139,139,139));
		setColor(20,new Color(146,146,146));
		setColor(21,new Color(154,154,154));
		setColor(22,new Color(162,162,162));
		setColor(23,new Color(169,169,169));
		setColor(24,new Color(177,177,177));
		setColor(25,new Color(184,184,184));
		setColor(26,new Color(192,192,192));
		setColor(27,new Color(200,200,200));
		setColor(28,new Color(207,207,207));
		setColor(29,new Color(210,210,210));
		setColor(30,new Color(215,215,215));
		setColor(31,new Color(222,222,222));
		setColor(32,new Color(228,228,228));
		setColor(33,new Color(236,236,236));
		setColor(34,new Color(245,245,245));
		setColor(35,new Color(255,255,255));
		setColor(36,new Color(50,50,50));
		setColor(37,new Color(59,60,59));
		setColor(38,new Color(69,72,68));
		setColor(39,new Color(78,80,77));
		setColor(40,new Color(88,93,86));
		setColor(41,new Color(97,100,95));
		setColor(42,new Color(109,112,104));
		setColor(43,new Color(116,123,112));
		setColor(44,new Color(125,131,121));
		setColor(45,new Color(134,141,130));
		setColor(46,new Color(144,151,139));
		setColor(47,new Color(153,161,148));
		setColor(48,new Color(163,171,157));
		setColor(49,new Color(172,181,166));
		setColor(50,new Color(181,189,176));
		setColor(51,new Color(189,196,185));
		setColor(52,new Color(20,16,36));
		setColor(53,new Color(24,24,44));
		setColor(54,new Color(36,36,60));
		setColor(55,new Color(52,52,80));
		setColor(56,new Color(68,68,96));
		setColor(57,new Color(88,88,116));
		setColor(58,new Color(108,108,136));
		setColor(59,new Color(124,124,152));
		setColor(60,new Color(148,148,172));
		setColor(61,new Color(164,164,184));
		setColor(62,new Color(180,184,200));
		setColor(63,new Color(192,196,208));
		setColor(64,new Color(208,208,216));
		setColor(65,new Color(224,224,224));
		setColor(66,new Color(27,15,8));
		setColor(67,new Color(38,20,11));
		setColor(68,new Color(49,27,14));
		setColor(69,new Color(61,31,14));
		setColor(70,new Color(65,35,18));
		setColor(71,new Color(74,37,19));
		setColor(72,new Color(83,43,19));
		setColor(73,new Color(87,47,23));
		setColor(74,new Color(95,51,27));
		setColor(75,new Color(103,59,31));
		setColor(76,new Color(115,67,35));
		setColor(77,new Color(123,75,39));
		setColor(78,new Color(131,83,47));
		setColor(79,new Color(143,91,51));
		setColor(80,new Color(151,99,59));
		setColor(81,new Color(160,108,64));
		setColor(82,new Color(175,116,74));
		setColor(83,new Color(180,126,81));
		setColor(84,new Color(192,135,91));
		setColor(85,new Color(204,143,93));
		setColor(86,new Color(213,151,103));
		setColor(87,new Color(216,159,115));
		setColor(88,new Color(220,167,126));
		setColor(89,new Color(223,175,138));
		setColor(90,new Color(227,183,149));
		setColor(91,new Color(230,190,161));
		setColor(92,new Color(233,198,172));
		setColor(93,new Color(237,206,184));
		setColor(94,new Color(240,214,195));
		setColor(95,new Color(62,40,11));
		setColor(96,new Color(75,50,16));
		setColor(97,new Color(84,59,23));
		setColor(98,new Color(95,67,30));
		setColor(99,new Color(103,75,38));
		setColor(100,new Color(110,83,47));
		setColor(101,new Color(123,95,55));
		setColor(102,new Color(137,107,62));
		setColor(103,new Color(150,118,75));
		setColor(104,new Color(163,129,84));
		setColor(105,new Color(171,137,92));
		setColor(106,new Color(180,146,101));
		setColor(107,new Color(188,154,109));
		setColor(108,new Color(196,162,117));
		setColor(109,new Color(204,170,125));
		setColor(110,new Color(208,176,133));
		setColor(111,new Color(37,20,4));
		setColor(112,new Color(47,24,4));
		setColor(113,new Color(57,28,6));
		setColor(114,new Color(68,33,4));
		setColor(115,new Color(76,36,3));
		setColor(116,new Color(84,40,0));
		setColor(117,new Color(97,47,2));
		setColor(118,new Color(114,54,0));
		setColor(119,new Color(125,63,6));
		setColor(120,new Color(141,75,9));
		setColor(121,new Color(155,83,17));
		setColor(122,new Color(162,95,21));
		setColor(123,new Color(169,103,26));
		setColor(124,new Color(180,113,32));
		setColor(125,new Color(188,124,20));
		setColor(126,new Color(204,136,24));
		setColor(127,new Color(220,148,28));
		setColor(128,new Color(236,160,23));
		setColor(129,new Color(244,172,47));
		setColor(130,new Color(252,187,57));
		setColor(131,new Color(252,194,70));
		setColor(132,new Color(251,201,83));
		setColor(133,new Color(251,208,97));
		setColor(134,new Color(251,214,110));
		setColor(135,new Color(251,221,123));
		setColor(136,new Color(250,228,136));
		setColor(137,new Color(157,51,4));
		setColor(138,new Color(170,65,2));
		setColor(139,new Color(185,86,4));
		setColor(140,new Color(213,118,4));
		setColor(141,new Color(236,164,3));
		setColor(142,new Color(248,190,3));
		setColor(143,new Color(255,216,43));
		setColor(144,new Color(255,255,0));
		setColor(145,new Color(67,0,0));
		setColor(146,new Color(79,0,0));
		setColor(147,new Color(91,0,0));
		setColor(148,new Color(103,0,0));
		setColor(149,new Color(115,0,0));
		setColor(150,new Color(127,0,0));
		setColor(151,new Color(139,0,0));
		setColor(152,new Color(155,0,0));
		setColor(153,new Color(167,0,0));
		setColor(154,new Color(179,0,0));
		setColor(155,new Color(191,0,0));
		setColor(156,new Color(203,0,0));
		setColor(157,new Color(215,0,0));
		setColor(158,new Color(227,0,0));
		setColor(159,new Color(239,0,0));
		setColor(160,new Color(255,0,0));
		setColor(161,new Color(255,52,52));
		setColor(162,new Color(255,74,74));
		setColor(163,new Color(255,95,95));
		setColor(164,new Color(255,123,123));
		setColor(165,new Color(255,155,155));
		setColor(166,new Color(255,179,179));
		setColor(167,new Color(255,201,201));
		setColor(168,new Color(255,215,215));
		setColor(169,new Color(60,12,88));
		setColor(170,new Color(80,8,108));
		setColor(171,new Color(104,8,128));
		setColor(172,new Color(128,0,144));
		setColor(173,new Color(152,0,176));
		setColor(174,new Color(184,0,224));
		setColor(175,new Color(216,44,252));
		setColor(176,new Color(224,120,240));
		setColor(177,new Color(37,6,129));
		setColor(178,new Color(60,33,147));
		setColor(179,new Color(82,61,165));
		setColor(180,new Color(105,88,183));
		setColor(181,new Color(128,116,201));
		setColor(182,new Color(151,143,219));
		setColor(183,new Color(173,171,237));
		setColor(184,new Color(196,198,255));
		setColor(185,new Color(2,4,41));
		setColor(186,new Color(2,5,49));
		setColor(187,new Color(6,8,57));
		setColor(188,new Color(2,5,65));
		setColor(189,new Color(2,5,79));
		setColor(190,new Color(0,4,88));
		setColor(191,new Color(0,4,96));
		setColor(192,new Color(0,4,104));
		setColor(193,new Color(2,5,121));
		setColor(194,new Color(2,5,137));
		setColor(195,new Color(6,9,159));
		setColor(196,new Color(12,16,184));
		setColor(197,new Color(32,40,200));
		setColor(198,new Color(56,60,220));
		setColor(199,new Color(80,80,253));
		setColor(200,new Color(80,108,252));
		setColor(201,new Color(80,136,252));
		setColor(202,new Color(80,164,252));
		setColor(203,new Color(80,196,252));
		setColor(204,new Color(72,220,252));
		setColor(205,new Color(80,236,252));
		setColor(206,new Color(84,252,252));
		setColor(207,new Color(152,252,252));
		setColor(208,new Color(188,252,244));
		setColor(209,new Color(11,23,7));
		setColor(210,new Color(19,35,11));
		setColor(211,new Color(23,51,15));
		setColor(212,new Color(31,67,23));
		setColor(213,new Color(39,83,27));
		setColor(214,new Color(47,99,35));
		setColor(215,new Color(55,115,43));
		setColor(216,new Color(63,131,47));
		setColor(217,new Color(67,147,55));
		setColor(218,new Color(75,159,63));
		setColor(219,new Color(83,175,71));
		setColor(220,new Color(91,191,79));
		setColor(221,new Color(95,207,87));
		setColor(222,new Color(103,223,95));
		setColor(223,new Color(111,239,103));
		setColor(224,new Color(119,255,111));
		setColor(225,new Color(23,31,23));
		setColor(226,new Color(27,35,27));
		setColor(227,new Color(31,43,31));
		setColor(228,new Color(35,51,35));
		setColor(229,new Color(43,55,43));
		setColor(230,new Color(47,63,47));
		setColor(231,new Color(51,71,51));
		setColor(232,new Color(59,75,55));
		setColor(233,new Color(63,83,59));
		setColor(234,new Color(67,91,67));
		setColor(235,new Color(75,95,71));
		setColor(236,new Color(79,103,75));
		setColor(237,new Color(87,111,79));
		setColor(238,new Color(91,115,83));
		setColor(239,new Color(95,123,87));
		setColor(240,new Color(103,131,95));
		setColor(241,new Color(255,223,0));
		setColor(242,new Color(255,191,0));
		setColor(243,new Color(255,159,0));
		setColor(244,new Color(255,127,0));
		setColor(245,new Color(255,95,0));
		setColor(246,new Color(255,63,0));
		setColor(247,new Color(244,14,3));
		setColor(248,new Color(55,0,0));
		setColor(249,new Color(47,0,0));
		setColor(250,new Color(39,0,0));
		setColor(251,new Color(23,0,0));
		setColor(252,new Color(15,15,15));
		setColor(253,new Color(11,11,11));
		setColor(254,new Color(7,7,7));
		setColor(255,new Color(255,255,255));
}};
	
	/** 
	 * Default Hexen palette.
	 */
	public static final Palette HEXEN = new Palette(){{
		setColor(0,new Color(2,2,2));
		setColor(1,new Color(4,4,4));
		setColor(2,new Color(15,15,15));
		setColor(3,new Color(19,19,19));
		setColor(4,new Color(27,27,27));
		setColor(5,new Color(28,28,28));
		setColor(6,new Color(33,33,33));
		setColor(7,new Color(39,39,39));
		setColor(8,new Color(45,45,45));
		setColor(9,new Color(51,51,51));
		setColor(10,new Color(57,57,57));
		setColor(11,new Color(63,63,63));
		setColor(12,new Color(69,69,69));
		setColor(13,new Color(75,75,75));
		setColor(14,new Color(81,81,81));
		setColor(15,new Color(86,86,86));
		setColor(16,new Color(92,92,92));
		setColor(17,new Color(98,98,98));
		setColor(18,new Color(104,104,104));
		setColor(19,new Color(112,112,112));
		setColor(20,new Color(121,121,121));
		setColor(21,new Color(130,130,130));
		setColor(22,new Color(139,139,139));
		setColor(23,new Color(147,147,147));
		setColor(24,new Color(157,157,157));
		setColor(25,new Color(166,166,166));
		setColor(26,new Color(176,176,176));
		setColor(27,new Color(185,185,185));
		setColor(28,new Color(194,194,194));
		setColor(29,new Color(203,203,203));
		setColor(30,new Color(212,212,212));
		setColor(31,new Color(221,221,221));
		setColor(32,new Color(230,230,230));
		setColor(33,new Color(29,32,29));
		setColor(34,new Color(38,40,37));
		setColor(35,new Color(50,50,50));
		setColor(36,new Color(59,60,59));
		setColor(37,new Color(69,72,68));
		setColor(38,new Color(78,80,77));
		setColor(39,new Color(88,93,86));
		setColor(40,new Color(97,100,95));
		setColor(41,new Color(109,112,104));
		setColor(42,new Color(116,123,112));
		setColor(43,new Color(125,131,121));
		setColor(44,new Color(134,141,130));
		setColor(45,new Color(144,151,139));
		setColor(46,new Color(153,161,148));
		setColor(47,new Color(163,171,157));
		setColor(48,new Color(172,181,166));
		setColor(49,new Color(181,189,176));
		setColor(50,new Color(189,196,185));
		setColor(51,new Color(22,29,22));
		setColor(52,new Color(27,36,27));
		setColor(53,new Color(31,43,31));
		setColor(54,new Color(35,51,35));
		setColor(55,new Color(43,55,43));
		setColor(56,new Color(47,63,47));
		setColor(57,new Color(51,71,51));
		setColor(58,new Color(59,75,55));
		setColor(59,new Color(63,83,59));
		setColor(60,new Color(67,91,67));
		setColor(61,new Color(75,95,71));
		setColor(62,new Color(79,103,75));
		setColor(63,new Color(87,111,79));
		setColor(64,new Color(91,115,83));
		setColor(65,new Color(95,123,87));
		setColor(66,new Color(103,131,95));
		setColor(67,new Color(20,16,36));
		setColor(68,new Color(30,26,46));
		setColor(69,new Color(40,36,57));
		setColor(70,new Color(50,46,67));
		setColor(71,new Color(59,57,78));
		setColor(72,new Color(69,67,88));
		setColor(73,new Color(79,77,99));
		setColor(74,new Color(89,87,109));
		setColor(75,new Color(99,97,120));
		setColor(76,new Color(109,107,130));
		setColor(77,new Color(118,118,141));
		setColor(78,new Color(128,128,151));
		setColor(79,new Color(138,138,162));
		setColor(80,new Color(148,148,172));
		setColor(81,new Color(62,40,11));
		setColor(82,new Color(75,50,16));
		setColor(83,new Color(84,59,23));
		setColor(84,new Color(95,67,30));
		setColor(85,new Color(103,75,38));
		setColor(86,new Color(110,83,47));
		setColor(87,new Color(123,95,55));
		setColor(88,new Color(137,107,62));
		setColor(89,new Color(150,118,75));
		setColor(90,new Color(163,129,84));
		setColor(91,new Color(171,137,92));
		setColor(92,new Color(180,146,101));
		setColor(93,new Color(188,154,109));
		setColor(94,new Color(196,162,117));
		setColor(95,new Color(204,170,125));
		setColor(96,new Color(208,176,133));
		setColor(97,new Color(27,15,8));
		setColor(98,new Color(38,20,11));
		setColor(99,new Color(49,27,14));
		setColor(100,new Color(61,31,14));
		setColor(101,new Color(65,35,18));
		setColor(102,new Color(74,37,19));
		setColor(103,new Color(83,43,19));
		setColor(104,new Color(87,47,23));
		setColor(105,new Color(95,51,27));
		setColor(106,new Color(103,59,31));
		setColor(107,new Color(115,67,35));
		setColor(108,new Color(123,75,39));
		setColor(109,new Color(131,83,47));
		setColor(110,new Color(143,91,51));
		setColor(111,new Color(151,99,59));
		setColor(112,new Color(160,108,64));
		setColor(113,new Color(175,116,74));
		setColor(114,new Color(180,126,81));
		setColor(115,new Color(192,135,91));
		setColor(116,new Color(204,143,93));
		setColor(117,new Color(213,151,103));
		setColor(118,new Color(216,159,115));
		setColor(119,new Color(220,167,126));
		setColor(120,new Color(223,175,138));
		setColor(121,new Color(227,183,149));
		setColor(122,new Color(37,20,4));
		setColor(123,new Color(47,24,4));
		setColor(124,new Color(57,28,6));
		setColor(125,new Color(68,33,4));
		setColor(126,new Color(76,36,3));
		setColor(127,new Color(84,40,0));
		setColor(128,new Color(97,47,2));
		setColor(129,new Color(114,54,0));
		setColor(130,new Color(125,63,6));
		setColor(131,new Color(141,75,9));
		setColor(132,new Color(155,83,17));
		setColor(133,new Color(162,95,21));
		setColor(134,new Color(169,103,26));
		setColor(135,new Color(180,113,32));
		setColor(136,new Color(188,124,20));
		setColor(137,new Color(204,136,24));
		setColor(138,new Color(220,148,28));
		setColor(139,new Color(236,160,23));
		setColor(140,new Color(244,172,47));
		setColor(141,new Color(252,187,57));
		setColor(142,new Color(252,194,70));
		setColor(143,new Color(251,201,83));
		setColor(144,new Color(251,208,97));
		setColor(145,new Color(251,221,123));
		setColor(146,new Color(2,4,41));
		setColor(147,new Color(2,5,49));
		setColor(148,new Color(6,8,57));
		setColor(149,new Color(2,5,65));
		setColor(150,new Color(2,5,79));
		setColor(151,new Color(0,4,88));
		setColor(152,new Color(0,4,96));
		setColor(153,new Color(0,4,104));
		setColor(154,new Color(4,6,121));
		setColor(155,new Color(2,5,137));
		setColor(156,new Color(20,23,152));
		setColor(157,new Color(38,41,167));
		setColor(158,new Color(56,59,181));
		setColor(159,new Color(74,77,196));
		setColor(160,new Color(91,94,211));
		setColor(161,new Color(109,112,226));
		setColor(162,new Color(127,130,240));
		setColor(163,new Color(145,148,255));
		setColor(164,new Color(31,4,4));
		setColor(165,new Color(39,0,0));
		setColor(166,new Color(47,0,0));
		setColor(167,new Color(55,0,0));
		setColor(168,new Color(67,0,0));
		setColor(169,new Color(79,0,0));
		setColor(170,new Color(91,0,0));
		setColor(171,new Color(103,0,0));
		setColor(172,new Color(115,0,0));
		setColor(173,new Color(127,0,0));
		setColor(174,new Color(139,0,0));
		setColor(175,new Color(155,0,0));
		setColor(176,new Color(167,0,0));
		setColor(177,new Color(185,0,0));
		setColor(178,new Color(202,0,0));
		setColor(179,new Color(220,0,0));
		setColor(180,new Color(237,0,0));
		setColor(181,new Color(255,0,0));
		setColor(182,new Color(255,46,46));
		setColor(183,new Color(255,91,91));
		setColor(184,new Color(255,137,137));
		setColor(185,new Color(255,171,171));
		setColor(186,new Color(20,16,4));
		setColor(187,new Color(13,24,9));
		setColor(188,new Color(17,33,12));
		setColor(189,new Color(21,41,14));
		setColor(190,new Color(24,50,17));
		setColor(191,new Color(28,57,20));
		setColor(192,new Color(32,65,24));
		setColor(193,new Color(35,73,28));
		setColor(194,new Color(39,80,31));
		setColor(195,new Color(44,86,37));
		setColor(196,new Color(46,95,38));
		setColor(197,new Color(51,104,43));
		setColor(198,new Color(60,122,51));
		setColor(199,new Color(68,139,58));
		setColor(200,new Color(77,157,66));
		setColor(201,new Color(85,174,73));
		setColor(202,new Color(94,192,81));
		setColor(203,new Color(157,51,4));
		setColor(204,new Color(170,65,2));
		setColor(205,new Color(185,86,4));
		setColor(206,new Color(213,119,6));
		setColor(207,new Color(234,147,5));
		setColor(208,new Color(255,178,6));
		setColor(209,new Color(255,195,26));
		setColor(210,new Color(255,216,45));
		setColor(211,new Color(4,133,4));
		setColor(212,new Color(8,175,8));
		setColor(213,new Color(2,215,2));
		setColor(214,new Color(3,234,3));
		setColor(215,new Color(42,252,42));
		setColor(216,new Color(121,255,121));
		setColor(217,new Color(3,3,184));
		setColor(218,new Color(15,41,220));
		setColor(219,new Color(28,80,226));
		setColor(220,new Color(41,119,233));
		setColor(221,new Color(54,158,239));
		setColor(222,new Color(67,197,246));
		setColor(223,new Color(80,236,252));
		setColor(224,new Color(244,14,3));
		setColor(225,new Color(255,63,0));
		setColor(226,new Color(255,95,0));
		setColor(227,new Color(255,127,0));
		setColor(228,new Color(255,159,0));
		setColor(229,new Color(255,195,26));
		setColor(230,new Color(255,223,0));
		setColor(231,new Color(43,13,64));
		setColor(232,new Color(61,14,89));
		setColor(233,new Color(90,15,122));
		setColor(234,new Color(120,16,156));
		setColor(235,new Color(149,16,189));
		setColor(236,new Color(178,17,222));
		setColor(237,new Color(197,74,232));
		setColor(238,new Color(215,129,243));
		setColor(239,new Color(234,169,253));
		setColor(240,new Color(61,16,16));
		setColor(241,new Color(90,36,33));
		setColor(242,new Color(118,56,49));
		setColor(243,new Color(147,77,66));
		setColor(244,new Color(176,97,83));
		setColor(245,new Color(204,117,99));
		setColor(246,new Color(71,53,2));
		setColor(247,new Color(81,63,6));
		setColor(248,new Color(96,72,0));
		setColor(249,new Color(108,80,0));
		setColor(250,new Color(120,88,0));
		setColor(251,new Color(128,96,0));
		setColor(252,new Color(149,112,1));
		setColor(253,new Color(181,136,3));
		setColor(254,new Color(212,160,4));
		setColor(255,new Color(255,255,255));
}};
	
	/** 
	 * Default Strife palette.
	 */
	public static final Palette STRIFE = new Palette(){{
		setColor(0,new Color(0,0,0));
		setColor(1,new Color(231,227,227));
		setColor(2,new Color(223,219,219));
		setColor(3,new Color(215,211,211));
		setColor(4,new Color(207,203,203));
		setColor(5,new Color(199,195,195));
		setColor(6,new Color(191,191,191));
		setColor(7,new Color(183,183,183));
		setColor(8,new Color(179,175,175));
		setColor(9,new Color(171,167,167));
		setColor(10,new Color(163,159,159));
		setColor(11,new Color(155,151,151));
		setColor(12,new Color(147,147,147));
		setColor(13,new Color(139,139,139));
		setColor(14,new Color(131,131,131));
		setColor(15,new Color(123,123,123));
		setColor(16,new Color(119,115,115));
		setColor(17,new Color(111,111,111));
		setColor(18,new Color(103,103,103));
		setColor(19,new Color(95,95,95));
		setColor(20,new Color(87,87,87));
		setColor(21,new Color(79,79,79));
		setColor(22,new Color(71,71,71));
		setColor(23,new Color(67,63,63));
		setColor(24,new Color(59,59,59));
		setColor(25,new Color(51,51,51));
		setColor(26,new Color(43,43,43));
		setColor(27,new Color(35,35,35));
		setColor(28,new Color(27,27,27));
		setColor(29,new Color(19,19,19));
		setColor(30,new Color(11,11,11));
		setColor(31,new Color(7,7,7));
		setColor(32,new Color(187,191,183));
		setColor(33,new Color(179,183,171));
		setColor(34,new Color(167,179,159));
		setColor(35,new Color(163,171,147));
		setColor(36,new Color(155,167,139));
		setColor(37,new Color(147,159,127));
		setColor(38,new Color(139,155,119));
		setColor(39,new Color(131,147,107));
		setColor(40,new Color(127,143,103));
		setColor(41,new Color(119,135,91));
		setColor(42,new Color(115,131,83));
		setColor(43,new Color(107,123,75));
		setColor(44,new Color(103,119,67));
		setColor(45,new Color(99,111,63));
		setColor(46,new Color(91,107,55));
		setColor(47,new Color(87,99,47));
		setColor(48,new Color(83,95,43));
		setColor(49,new Color(75,87,35));
		setColor(50,new Color(71,83,31));
		setColor(51,new Color(67,75,27));
		setColor(52,new Color(63,71,23));
		setColor(53,new Color(59,63,19));
		setColor(54,new Color(51,59,15));
		setColor(55,new Color(47,51,11));
		setColor(56,new Color(43,47,7));
		setColor(57,new Color(39,43,7));
		setColor(58,new Color(31,35,7));
		setColor(59,new Color(27,31,0));
		setColor(60,new Color(23,23,0));
		setColor(61,new Color(15,19,0));
		setColor(62,new Color(11,11,0));
		setColor(63,new Color(7,7,0));
		setColor(64,new Color(219,43,43));
		setColor(65,new Color(203,35,35));
		setColor(66,new Color(191,31,31));
		setColor(67,new Color(175,27,27));
		setColor(68,new Color(163,23,23));
		setColor(69,new Color(147,19,19));
		setColor(70,new Color(135,15,15));
		setColor(71,new Color(119,11,11));
		setColor(72,new Color(107,7,7));
		setColor(73,new Color(91,7,7));
		setColor(74,new Color(79,0,0));
		setColor(75,new Color(63,0,0));
		setColor(76,new Color(51,0,0));
		setColor(77,new Color(39,0,0));
		setColor(78,new Color(23,0,0));
		setColor(79,new Color(11,0,0));
		setColor(80,new Color(235,231,0));
		setColor(81,new Color(231,211,0));
		setColor(82,new Color(215,179,0));
		setColor(83,new Color(199,151,0));
		setColor(84,new Color(183,127,0));
		setColor(85,new Color(167,103,0));
		setColor(86,new Color(151,83,0));
		setColor(87,new Color(135,63,0));
		setColor(88,new Color(119,47,0));
		setColor(89,new Color(103,35,0));
		setColor(90,new Color(87,23,0));
		setColor(91,new Color(71,11,0));
		setColor(92,new Color(55,7,0));
		setColor(93,new Color(39,0,0));
		setColor(94,new Color(23,0,0));
		setColor(95,new Color(11,0,0));
		setColor(96,new Color(183,231,127));
		setColor(97,new Color(163,215,111));
		setColor(98,new Color(143,199,95));
		setColor(99,new Color(127,183,79));
		setColor(100,new Color(107,171,67));
		setColor(101,new Color(91,155,55));
		setColor(102,new Color(75,139,43));
		setColor(103,new Color(63,123,35));
		setColor(104,new Color(47,111,27));
		setColor(105,new Color(35,95,19));
		setColor(106,new Color(23,79,11));
		setColor(107,new Color(15,67,7));
		setColor(108,new Color(7,51,7));
		setColor(109,new Color(0,35,0));
		setColor(110,new Color(0,19,0));
		setColor(111,new Color(0,7,0));
		setColor(112,new Color(199,207,255));
		setColor(113,new Color(183,187,239));
		setColor(114,new Color(163,171,219));
		setColor(115,new Color(151,155,203));
		setColor(116,new Color(135,139,187));
		setColor(117,new Color(123,127,171));
		setColor(118,new Color(107,111,155));
		setColor(119,new Color(95,99,139));
		setColor(120,new Color(83,83,123));
		setColor(121,new Color(67,71,107));
		setColor(122,new Color(55,59,91));
		setColor(123,new Color(47,47,75));
		setColor(124,new Color(35,35,59));
		setColor(125,new Color(23,23,43));
		setColor(126,new Color(15,15,27));
		setColor(127,new Color(0,0,11));
		setColor(128,new Color(199,191,147));
		setColor(129,new Color(179,171,131));
		setColor(130,new Color(167,155,119));
		setColor(131,new Color(155,139,111));
		setColor(132,new Color(143,127,99));
		setColor(133,new Color(131,111,91));
		setColor(134,new Color(119,99,79));
		setColor(135,new Color(107,87,71));
		setColor(136,new Color(91,71,59));
		setColor(137,new Color(79,59,51));
		setColor(138,new Color(67,47,43));
		setColor(139,new Color(55,39,35));
		setColor(140,new Color(43,27,27));
		setColor(141,new Color(31,19,19));
		setColor(142,new Color(19,11,11));
		setColor(143,new Color(7,7,0));
		setColor(144,new Color(143,195,211));
		setColor(145,new Color(123,179,195));
		setColor(146,new Color(107,167,183));
		setColor(147,new Color(91,155,167));
		setColor(148,new Color(75,139,155));
		setColor(149,new Color(59,127,139));
		setColor(150,new Color(47,115,127));
		setColor(151,new Color(35,103,115));
		setColor(152,new Color(27,91,99));
		setColor(153,new Color(19,79,87));
		setColor(154,new Color(11,67,71));
		setColor(155,new Color(7,55,59));
		setColor(156,new Color(0,43,43));
		setColor(157,new Color(0,31,31));
		setColor(158,new Color(0,19,19));
		setColor(159,new Color(0,7,7));
		setColor(160,new Color(211,191,175));
		setColor(161,new Color(203,179,163));
		setColor(162,new Color(195,171,151));
		setColor(163,new Color(191,159,143));
		setColor(164,new Color(183,151,131));
		setColor(165,new Color(175,143,123));
		setColor(166,new Color(171,135,115));
		setColor(167,new Color(163,123,103));
		setColor(168,new Color(155,115,95));
		setColor(169,new Color(151,107,87));
		setColor(170,new Color(143,99,79));
		setColor(171,new Color(139,91,71));
		setColor(172,new Color(131,83,67));
		setColor(173,new Color(123,75,59));
		setColor(174,new Color(119,67,51));
		setColor(175,new Color(111,59,47));
		setColor(176,new Color(103,55,39));
		setColor(177,new Color(99,47,35));
		setColor(178,new Color(91,43,31));
		setColor(179,new Color(83,35,27));
		setColor(180,new Color(79,31,23));
		setColor(181,new Color(71,27,19));
		setColor(182,new Color(63,19,15));
		setColor(183,new Color(59,15,11));
		setColor(184,new Color(51,11,7));
		setColor(185,new Color(43,7,7));
		setColor(186,new Color(39,7,0));
		setColor(187,new Color(31,0,0));
		setColor(188,new Color(27,0,0));
		setColor(189,new Color(19,0,0));
		setColor(190,new Color(11,0,0));
		setColor(191,new Color(7,0,0));
		setColor(192,new Color(211,199,187));
		setColor(193,new Color(203,191,179));
		setColor(194,new Color(195,183,171));
		setColor(195,new Color(191,175,163));
		setColor(196,new Color(183,167,155));
		setColor(197,new Color(175,159,147));
		setColor(198,new Color(171,151,139));
		setColor(199,new Color(163,143,135));
		setColor(200,new Color(155,139,127));
		setColor(201,new Color(151,131,119));
		setColor(202,new Color(143,123,111));
		setColor(203,new Color(135,115,107));
		setColor(204,new Color(131,107,99));
		setColor(205,new Color(123,103,95));
		setColor(206,new Color(115,95,87));
		setColor(207,new Color(111,87,83));
		setColor(208,new Color(103,83,75));
		setColor(209,new Color(95,75,71));
		setColor(210,new Color(91,67,63));
		setColor(211,new Color(83,63,59));
		setColor(212,new Color(79,55,51));
		setColor(213,new Color(71,51,47));
		setColor(214,new Color(63,43,43));
		setColor(215,new Color(59,39,39));
		setColor(216,new Color(51,35,31));
		setColor(217,new Color(43,27,27));
		setColor(218,new Color(39,23,23));
		setColor(219,new Color(31,19,19));
		setColor(220,new Color(23,15,15));
		setColor(221,new Color(19,11,11));
		setColor(222,new Color(11,7,7));
		setColor(223,new Color(7,7,0));
		setColor(224,new Color(239,239,0));
		setColor(225,new Color(231,215,0));
		setColor(226,new Color(227,191,0));
		setColor(227,new Color(219,171,0));
		setColor(228,new Color(215,151,0));
		setColor(229,new Color(211,131,0));
		setColor(230,new Color(203,111,0));
		setColor(231,new Color(199,91,0));
		setColor(232,new Color(191,75,0));
		setColor(233,new Color(187,59,0));
		setColor(234,new Color(183,43,0));
		setColor(235,new Color(255,0,0));
		setColor(236,new Color(223,0,0));
		setColor(237,new Color(191,0,0));
		setColor(238,new Color(159,0,0));
		setColor(239,new Color(127,0,0));
		setColor(240,new Color(0,0,0));
		setColor(241,new Color(139,199,103));
		setColor(242,new Color(107,171,75));
		setColor(243,new Color(79,143,55));
		setColor(244,new Color(55,115,35));
		setColor(245,new Color(35,87,19));
		setColor(246,new Color(19,63,11));
		setColor(247,new Color(215,223,255));
		setColor(248,new Color(187,203,247));
		setColor(249,new Color(143,167,219));
		setColor(250,new Color(99,131,195));
		setColor(251,new Color(63,91,167));
		setColor(252,new Color(203,203,203));
		setColor(253,new Color(215,215,215));
		setColor(254,new Color(223,223,223));
		setColor(255,new Color(235,235,235));
}};

	
	/** The number of total colors in a standard Doom palette. */
	public static final int
	NUM_COLORS = 256;
	
	/** Number of bytes per color in a Doom palette. */
	public static final int
	BYTES_PER_COLOR = 3;

	/** The palette of colors. */
	protected Color[] colorPalette;

	/**
	 * Creates a new palette of black, opaque colors.
	 */
	public Palette()
	{
		colorPalette = new Color[NUM_COLORS];
		for (int i = 0; i < NUM_COLORS; i++)
			colorPalette[i] = new Color(0,0,0);
	}
	
	/**
	 * Returns the Color of a specific index in the palette.
	 * @param index	the index number of the color.
	 * @throws ArrayIndexOutOfBoundsException if index > NUM_COLORS or < 0.
	 * @return the color as a java.awt.Color.
	 */
	public Color getColor(int index)
	{
		return colorPalette[index];
	}
	
	/**
	 * Sets the color of a specific index in the Palette.
	 * @param index	the index number of the color.
	 * @param color the new Color.
	 * @throws ArrayIndexOutOfBoundsException if index > NUM_COLORS or < 0.
	 */
	public void setColor(int index, Color color)
	{
		colorPalette[index] = color;
	}
	
	/**
	 * Returns the index of the color nearest to a color in the palette.
	 * TODO: Make this perform better.
	 */
	public int getNearestColorIndex(Color color)
	{
		if (color.getAlpha() < 255)
			return -1;
		
		double dist = Double.MAX_VALUE;
		int out = -1;
		for (int i = 0; i < 256; i++)
		{
			Color c = getColor(i);
			double d = Math.sqrt(
					Math.pow(color.getRed() - c.getRed(), 2) +
					Math.pow(color.getGreen() - c.getGreen(), 2) +
					Math.pow(color.getBlue() - c.getBlue(), 2));
			
			if (d < dist)
			{
				dist = d;
				out = i;
			}
		}
		return out;
	}
	
	/**
	 * Returns the color nearest to a color in the palette.
	 * @return The closest color. Transparent color data is returned as completely transparent. 
	 */
	public Color getNearestColor(Color color)
	{
		int idx = getNearestColorIndex(color);
		if (idx < 0)
			return DoomUtil.COLOR_BLANK;
		else
			return colorPalette[idx];
	}
	
	/**
	 * Returns an iterator for iterating through this palette's colors. 
	 */
	public Iterator<Color> iterator()
	{
		return Arrays.asList(colorPalette).iterator();
	}
	
	@Override
	public boolean isDoomCompatible()
	{
		return true;
	}

	@Override
	public byte[] getDoomBytes() throws DataExportException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (Color c : colorPalette)
		{
			bos.write(c.getRed() & 0x0ff);
			bos.write(c.getGreen() & 0x0ff);
			bos.write(c.getBlue() & 0x0ff);
		}
		return bos.toByteArray();
	}

	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in, SuperReader.LITTLE_ENDIAN);
		for (int i = 0; i < NUM_COLORS; i++)
			colorPalette[i] = new Color(
					sr.readByte() & 0x0ff,
					sr.readByte() & 0x0ff,
					sr.readByte() & 0x0ff
					); 
	}

	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

	@Override
	public String toString()
	{
		return java.util.Arrays.toString(colorPalette);
	}
	
}
