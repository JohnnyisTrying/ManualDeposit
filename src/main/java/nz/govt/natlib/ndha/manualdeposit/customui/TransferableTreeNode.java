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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.tree.TreePath;

public class TransferableTreeNode implements Transferable, Serializable {
	private static final long serialVersionUID = -9182317107045343539L;
	public final static DataFlavor TREE_PATH_FLAVOR = new DataFlavor(
			TreePath.class, "Tree Path");
	private final DataFlavor flavors[] = { TREE_PATH_FLAVOR };
	private final TreePath path;

	public TransferableTreeNode(final TreePath tp) {
		path = tp;
	}

	public synchronized DataFlavor[] getTransferDataFlavors() {
		return flavors.clone();
	}

	public boolean isDataFlavorSupported(final DataFlavor flavor) {
		return (flavor.getRepresentationClass() == TreePath.class);
	}

	public synchronized Object getTransferData(final DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (isDataFlavorSupported(flavor)) {
			return (Object) path;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}
}
