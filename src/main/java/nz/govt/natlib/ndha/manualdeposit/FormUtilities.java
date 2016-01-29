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

package nz.govt.natlib.ndha.manualdeposit;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

public class FormUtilities {

	private static final long serialVersionUID = -758052758437008163L;

	public enum TextAttributes {
		BoldText("Bold"), IncreaseSize("Size");

		private final String _description;

		TextAttributes(String description) {
			_description = description;
		}

		public String description() {
			return _description;
		}

		public String toString() {
			return _description;
		}
	}

	private static void setMenuFont(Object item, Font theFont) {
		if (item instanceof JMenu) {
			JMenu menu = (JMenu) item;
			menu.setFont(theFont);
			for (int j = 0; j < menu.getItemCount(); j++) {
				if (menu.getItem(j) != null) {
					setMenuFont(menu.getItem(j), theFont);
				}
			}
		} else {
			JMenuItem menuItem = (JMenuItem) item;
			menuItem.setFont(theFont);
			for (int i = 0; i < menuItem.getSubElements().length; i++) {
				setMenuFont((JMenuItem) menuItem.getSubElements()[i], theFont);
			}
		}
	}

	public static void setFormFont(Container container, Font theFont) {
		Component[] components = container.getComponents();
		for (int i = 0; i < components.length; i++) {
			Font componentFont = theFont;

			if (components[i] instanceof JComponent) {
				JComponent component = (JComponent) components[i];
				if (component.getClientProperty(TextAttributes.BoldText
						.description()) != null) {
					componentFont = new Font(theFont.getFontName(), Font.BOLD,
							theFont.getSize());
				}
				if (component.getClientProperty(TextAttributes.IncreaseSize
						.description()) != null) {
					int increase = (Integer) (component
							.getClientProperty(TextAttributes.IncreaseSize
									.description()));
					componentFont = new Font(componentFont.getFontName(),
							componentFont.getStyle(), componentFont.getSize()
									+ increase);
				}
			}
			components[i].setFont(componentFont);
			if (components[i] instanceof JPanel) {
				JPanel panel = (JPanel) components[i];
				if ((panel.getBorder() != null)
						&& (panel.getBorder() instanceof TitledBorder)) {
					TitledBorder border = (TitledBorder) panel.getBorder();
					border.setTitleFont(theFont);
				}
			} else if (components[i] instanceof JMenu) {
				JMenu menu = (JMenu) components[i];
				for (int j = 0; j < menu.getItemCount(); j++) {
					if (menu.getItem(j) != null) {
						setMenuFont(menu.getItem(j), theFont);
					}
				}
			} else if (components[i] instanceof JTable) {
				JTable table = (JTable) components[i];
				table.setRowHeight(theFont.getSize() + 8);
			}
			if (components[i] instanceof Container) {
				setFormFont((Container) components[i], theFont);
			}
		}
	}

}
