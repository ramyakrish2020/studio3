/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.model;

import org.eclipse.osgi.util.NLS;

/**
 * Messages
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.css.core.model.messages"; //$NON-NLS-1$

	public static String ClassGroupElement_ClassGroupElementName;
	public static String ColorGroupElement_ColorElementName;
	public static String CSSElement_ElementName;
	public static String IdGroupElement_IdGroupName;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
