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

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

import nz.govt.natlib.ndha.common.mets.FileGroup;

public class DepositTreeEditor extends DefaultTreeCellEditor {
	private Object objBeingEdited;
	private Font standardFont = null;

	public DepositTreeEditor(final JTree tree,
			final DefaultTreeCellRenderer renderer) {
		super(tree, renderer);
	}

	public DepositTreeEditor(final JTree tree,
			final DefaultTreeCellRenderer renderer, final TreeCellEditor editor) {
		super(tree, renderer, editor);
	}

	/**
	 * Allow people to toggle the icon when in edit mode
	 * 
	 * @param editor
	 *            Icon
	 */
	public void setEditorIcon(Icon editor) {
		editingIcon = editor;
	}

	/**
	 * If the <code>realEditor</code> returns true to this message,
	 * <code>prepareForEditing</code> is messaged and true is returned.
	 * 
	 * @param event
	 *            EventObject
	 * @return boolean
	 */
	public boolean isCellEditable(EventObject event) {
		boolean isEditable = super.isCellEditable(event);
		if (isEditable) {
			TreePath path = null;
			if (event == null) {
				path = tree.getSelectionPath();
			} else {
				if (event instanceof MouseEvent) {
					MouseEvent evt = (MouseEvent) event;
					JTree src = (JTree) event.getSource();
					path = src
							.getClosestPathForLocation(evt.getX(), evt.getY());
				}
			}
			if (path != null) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
						.getLastPathComponent();
				if ((node.getUserObject() instanceof FileGroup)
						|| (node.getUserObject() instanceof String)) {
					isEditable = false;
				}
			}
		}
		return isEditable;
	}

	public void valueChanged(TreeSelectionEvent e) {
		super.valueChanged(e);
	}

	public Object getObjectBeingEdited() {
		return objBeingEdited;
	}

	public void setObjectBeingEdited(Object value) {
		objBeingEdited = value;
	}

	public Font getStandardFont() {
		return standardFont;
	}

	public void setStandardFont(Font value) {
		standardFont = value;
	}

	public TreeEditorField getEditor() {
		TreeEditorField field = (TreeEditorField) this.editingComponent;
		if (standardFont != null && field != null) {
			field.setFont(standardFont);
		}
		return field;
	}
}
