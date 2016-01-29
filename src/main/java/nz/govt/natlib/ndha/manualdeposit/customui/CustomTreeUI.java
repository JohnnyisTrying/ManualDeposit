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

import java.awt.event.MouseEvent;

import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import nz.govt.natlib.ndha.common.mets.FileGroup;
import nz.govt.natlib.ndha.common.mets.FileGroupCollection;
import nz.govt.natlib.ndha.common.mets.FileSystemObject;
import nz.govt.natlib.ndha.common.mets.StructMap;

public class CustomTreeUI extends BasicTreeUI {

	TreePath editingPath;

	protected boolean startEditing(TreePath path, MouseEvent event) {
		// default to standard behaviour
		boolean result = super.startEditing(path, event);
		editingPath = path;
		// start editing this node?
		if (result) {
			// start peeling the object to get a handle to the editor
			DepositTreeEditor editor = (DepositTreeEditor) tree.getCellEditor();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
					.getSelectionPath().getLastPathComponent();
			editor.setObjectBeingEdited(node.getUserObject());
			TreeEditorField textField = editor.getEditor();
			textField.selectAll();
		}
		return result;
	}

	protected void completeEditing(boolean messageStop, boolean messageCancel,
			boolean messageTree) {
		super.completeEditing(messageStop, messageCancel, messageTree);
		if (!messageCancel) {
			DepositTreeEditor editor = (DepositTreeEditor) tree.getCellEditor();
			TreeEditorField textField = editor.getEditor();
			Object objectBeingEdited = editor.getObjectBeingEdited();
			String newName = textField.getText();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) editingPath
					.getLastPathComponent();
			if (objectBeingEdited instanceof StructMap) {
				StructMap map = (StructMap) objectBeingEdited;
				map.setStructureName(newName);
				node.setUserObject(map);
			} else if (objectBeingEdited instanceof FileSystemObject) {
				FileSystemObject fso = (FileSystemObject) objectBeingEdited;
				fso.setDescription(newName);
				fso.setFileLabel(newName);
				node.setUserObject(fso);
			} else if (objectBeingEdited instanceof FileGroup) {
				FileGroup entity = (FileGroup) objectBeingEdited;
				entity.setEntityName(newName);
				node.setUserObject(entity);
			} else if (objectBeingEdited instanceof FileGroupCollection) {
				FileGroupCollection entity = (FileGroupCollection) objectBeingEdited;
				entity.setEntityName(newName);
				node.setUserObject(entity);
			}
		}
	}

}
