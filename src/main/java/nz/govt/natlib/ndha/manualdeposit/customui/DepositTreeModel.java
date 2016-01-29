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

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import nz.govt.natlib.ndha.common.ChecksumDigest;
//import nz.govt.natlib.ndha.common.MD5Digest;

public class DepositTreeModel extends DefaultTreeModel {

	public enum ETreeType {
		FileSystemTree, EntityTree, StructMapTree;
	}

	private static final long serialVersionUID = 6011250606048328230L;
	private ETreeType theTreeType;
//	private MD5Digest digest = null;
	private ChecksumDigest digest = null;

	public ETreeType getTreeType() {
		return theTreeType;
	}

	public void setTreeType(ETreeType value) {
		theTreeType = value;
	}

//	public MD5Digest getMD5Digest() {
//		return digest;
//	}
//
//	public void setMD5Digest(MD5Digest value) {
//		digest = value;
//	}
	
	public ChecksumDigest getChecksumDigest() {
		return digest;
	}

	public void setChecksumDigest(ChecksumDigest value) {
		digest = value;
	}

	public DepositTreeModel(final TreeNode root, ETreeType treeType) {
		super(root);
		theTreeType = treeType;
	}

	public DepositTreeModel(final TreeNode root,
			final boolean asksAllowsChildren, ETreeType treeType) {
		super(root, asksAllowsChildren);
		theTreeType = treeType;
	}
}
