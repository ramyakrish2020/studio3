/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import com.aptana.plist.PListParserFactory;
import com.aptana.theme.internal.OrderedProperties;

/**
 * An importer to bring in Textmate themes to our theme system. This is not guaranteed to work 100% because we don't
 * honor the same scope format. Scopes with spaces in them in particular are not going to translate properly and will
 * need to be tweaked manually.
 * 
 * @author cwilliams
 */
public class TextmateImporter
{

	/**
	 * Property names used in textmate plist files.
	 */
	private static final String NAME = "name"; //$NON-NLS-1$
	private static final String SCOPE = "scope"; //$NON-NLS-1$
	private static final String FONT_STYLE = "fontStyle"; //$NON-NLS-1$
	private static final String BACKGROUND = "background"; //$NON-NLS-1$
	private static final String FOREGROUND = "foreground"; //$NON-NLS-1$
	private static final String SETTINGS = "settings"; //$NON-NLS-1$

	public TextmateImporter()
	{
	}

	/**
	 * Converts a Textmate plist backed theme to a RadRails style theme object.
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public Theme convert(File file) throws FileNotFoundException
	{
		try
		{
			return new Theme(ThemePlugin.getDefault().getColorManager(), convertToProperties(file));
		}
		catch (Exception e)
		{
			ThemePlugin.logError(e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static Properties convertToProperties(File file) throws IOException
	{
		Map<String, Object> plistProperties = parse(file);
		List<Map<String, Object>> tokenList = (List<Map<String, Object>>) plistProperties.get(SETTINGS);
		Map<String, Object> globals = (Map<String, Object>) tokenList.get(0).get(SETTINGS);
		Properties radRailsProps = new OrderedProperties();
		for (Map.Entry<String, Object> entry : globals.entrySet())
		{
			// FIXME Skip invisibles
			radRailsProps.put(entry.getKey(), entry.getValue());
		}
		radRailsProps.put(Theme.THEME_NAME_PROP_KEY, plistProperties.get(NAME));

		tokenList.remove(0);
		for (Map<String, Object> token : tokenList)
		{
			// FIXME Handle separators which have a name but no scope. They're useful visually for editing themes, but
			// should be ignored in terms of actual rules
			if (!token.containsKey(SCOPE))
				continue;

			String name = (String) token.get(NAME);

			String scope = (String) token.get(SCOPE);
			Map<String, Object> colors = (Map<String, Object>) token.get(SETTINGS);

			StringBuilder value = new StringBuilder();
			if (colors.containsKey(FOREGROUND))
			{
				String fg = (String) colors.get(FOREGROUND);
				value.append(fg);
			}
			else
			{
				if (colors.containsKey(BACKGROUND) || colors.containsKey(FONT_STYLE))
					value.append(radRailsProps.getProperty(Theme.FOREGROUND_PROP_KEY));
				else
				{
					String tokenName = (String) token.get(NAME);
					ThemePlugin.logWarning(MessageFormat.format("Token failed to import: {0}", tokenName)); //$NON-NLS-1$
					continue;
				}
			}

			if (colors.containsKey(BACKGROUND))
			{
				String bg = (String) colors.get(BACKGROUND);
				if (bg != null && bg.length() > 0)
				{
					value.append(Theme.DELIMETER);
					value.append(bg);
				}
			}

			if (colors.containsKey(FONT_STYLE))
			{
				String fontStyle = (String) colors.get(FONT_STYLE);
				if (fontStyle != null && fontStyle.length() > 0)
				{
					StringTokenizer tokenizer = new StringTokenizer(fontStyle);
					while (tokenizer.hasMoreTokens())
					{
						value.append(Theme.DELIMETER);
						value.append(tokenizer.nextToken());
					}
				}
			}

			value.append("^").append(scope);
			radRailsProps.put(name, value.toString());
		}

		return radRailsProps;
	}

	private static Map<String, Object> parse(File file) throws IOException
	{
		return PListParserFactory.parse(file);
	}
}
