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

import javax.swing.JLabel;
import javax.swing.JTextField;

import nz.govt.natlib.ndha.common.ilsquery.SruIndex;

public class LabelTextPair {

	private JLabel theLabel = null;
	private JTextField theField = null;
	private SruIndex theIndex = null;

	public void setLabel(JLabel label) {
		theLabel = label;
	}

	public JLabel getLabel() {
		return theLabel;
	}

	public void setField(JTextField field) {
		theField = field;
	}

	public JTextField getField() {
		return theField;
	}

	public void setIndex(SruIndex index) {
		theIndex = index;
	}

	public SruIndex getIndex() {
		return theIndex;
	}

	public static LabelTextPair create(JLabel label, JTextField field) {
		return new LabelTextPair(label, field);
	}

	public LabelTextPair(JLabel label, JTextField field) {
		theLabel = label;
		theField = field;
	}

}
