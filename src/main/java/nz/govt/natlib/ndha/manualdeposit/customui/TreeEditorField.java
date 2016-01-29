/**
 * Software License
 *
 * Copyright 2007/2010 National Library of New Zealand.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *
 * or the file "LICENSE.txt" included with the software.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

package nz.govt.natlib.ndha.manualdeposit.customui;

import java.awt.Toolkit;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import nz.govt.natlib.ndha.common.FileUtils;

public final class TreeEditorField extends JTextField {
	private static final long serialVersionUID = -3863530513567202265L;
	private final Toolkit toolkit = Toolkit.getDefaultToolkit();
	private final int maxLength;

	public TreeEditorField(final int maximumLength) {
		super();
		maxLength = maximumLength;
	}

	public TreeEditorField(final String value, final int maximumLength) {
		super();
		maxLength = maximumLength;
		setText(value);
	}

	public TreeEditorField(final JTextField value, final int maximumLength) {
		super();
		maxLength = maximumLength;
		setText(value.getText());
	}

	public void setText(final String textIn) {
		String theText = textIn;
		if (theText == null || (theText != null && theText.length() == 0)) {
			theText = "";
		}
		if (theText.length() > maxLength) {
			toolkit.beep();
		} else {
			super.setText(theText);
		}
	}

	protected Document createDefaultModel() {
		return new TreeEditorDocument();
	}

	protected class TreeEditorDocument extends PlainDocument {

		private static final long serialVersionUID = -45559958048069623L;

		@Override
		public void insertString(final int offs, final String str,
				final AttributeSet a) throws BadLocationException {
			final int totalLength = super.getLength() + str.length();
			if (totalLength > maxLength) {
				toolkit.beep();
				// System.err.println("String too long");
				return;
			}
			// Make sure nothing goes in front of the first \\ or it will stuff
			// up the regexp - which needs another \\ (i.e. \\\\) to parse a \
			final String resultStr = FileUtils.specialCharToUnderscore(str);
			if (!str.equals(resultStr)) {
				toolkit.beep();
				// _Frame.showError("Invalid Name",
				// "Names cannot contain the characters " + rubbishString);
			}
			super.insertString(offs, resultStr, a);
		}
	}
}
