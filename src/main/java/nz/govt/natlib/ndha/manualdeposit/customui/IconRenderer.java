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

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import nz.govt.natlib.ndha.common.ChecksumDigest;
//import nz.govt.natlib.ndha.common.MD5Digest;
import nz.govt.natlib.ndha.common.mets.FileGroup;
import nz.govt.natlib.ndha.common.mets.FileGroupCollection;
import nz.govt.natlib.ndha.common.mets.FileSystemObject;
import nz.govt.natlib.ndha.common.mets.MimeTypes;
import nz.govt.natlib.ndha.common.mets.StructMap;
import nz.govt.natlib.ndha.common.mets.FileSystemObject.RepresentationTypes;
import nz.govt.natlib.ndha.manualdeposit.customui.DepositTreeModel.ETreeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IconRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 8567875825235138072L;
	private final static Log LOG = LogFactory.getLog(IconRenderer.class);
	private final Icon accessCopyIcon;
	private final Icon digitalOriginalIcon;
	private final Icon documentIcon;
	private final Icon folderEntityIcon;
	private final Icon folderFileSystemIcon;
	private final Icon folderHomeIcon;
	private final Icon modifiedMasterIcon;
	private final Icon preservationCopyIcon;
	private final Icon structMapIcon;
	private final Map<String, Icon> mimeTypeIcons;
	private String currentIconDirectory = "icons/16/";

	public String getIconDirectory() {
		return currentIconDirectory;
	}

	public void setIconDirectory(final String value) {
		currentIconDirectory = value;
	}

	private Icon createIcon(final ClassLoader cLoader, final String iconPath) {
		Icon retVal = null;
		if (new File(iconPath).exists()) {
			retVal = new ImageIcon(iconPath);
		} else {
			final java.net.URL imageURL = cLoader.getResource(iconPath);
			if (imageURL != null) {
				retVal = new ImageIcon(imageURL);
			}
		}
		return retVal;
	}

	public IconRenderer(final String iconDirectory) {
		currentIconDirectory = iconDirectory;
		final ClassLoader cLoader = Thread.currentThread()
				.getContextClassLoader();
		final String accessCopyIconName = currentIconDirectory
				+ "AccessCopy.png";
		accessCopyIcon = createIcon(cLoader, accessCopyIconName);
		final String digitalOriginalIconName = currentIconDirectory
				+ "DigitalOriginal.png";
		digitalOriginalIcon = createIcon(cLoader, digitalOriginalIconName);
		final String documentIconName = currentIconDirectory + "Binary.png";
		documentIcon = createIcon(cLoader, documentIconName);
		String folderEntityIconName = currentIconDirectory + "FolderEntity.png";
		folderEntityIcon = createIcon(cLoader, folderEntityIconName);
		String folderFileSystemIconName = currentIconDirectory
				+ "FolderFileSystem.png";
		folderFileSystemIcon = createIcon(cLoader, folderFileSystemIconName);
		String folderHomeIconName = currentIconDirectory + "FolderHome.png";
		folderHomeIcon = createIcon(cLoader, folderHomeIconName);
		String modifiedMasterIconName = currentIconDirectory
				+ "ModifiedMaster.png";
		modifiedMasterIcon = createIcon(cLoader, modifiedMasterIconName);
		String preservationCopyIconName = currentIconDirectory
				+ "PreservationCopy.png";
		preservationCopyIcon = createIcon(cLoader, preservationCopyIconName);
		String structMapIconName = currentIconDirectory + "StructMap.png";
		structMapIcon = createIcon(cLoader, structMapIconName);
		mimeTypeIcons = new HashMap<String, Icon>();
		try {
			MimeTypes mimeTypes = new MimeTypes();
			for (MimeTypes.MimeType mimeType : mimeTypes.getImageList()) {
				String iconName = currentIconDirectory
						+ mimeType.getImageName();
				Icon icon = createIcon(cLoader, iconName);
				if (icon != null) {
					mimeTypeIcons.put(mimeType.getMimeType(), icon);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error(ex.getMessage(), ex);
		}
	}

	private Icon getMimeTypeIcon(final FileSystemObject fso) {
		String mimeType = "binary";
		if (mimeTypeIcons.containsKey(mimeType)) {
			return mimeTypeIcons.get(mimeType);
		} else {
			return documentIcon;
		}
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
		this.setToolTipText("");
		DepositTreeModel model = null;
//		MD5Digest digest = null;
		ChecksumDigest digest = null;
		if (tree.getModel() instanceof DepositTreeModel) {
			model = (DepositTreeModel) tree.getModel();
			digest = model.getChecksumDigest();
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		if ((node.getParent() != null) && (node.getUserObject() instanceof FileGroupCollection)
				&& (model.getChecksumDigest() != null) && !(model.getChecksumDigest().isFixitySuccessful(node))) {
				setIcon(folderHomeIcon);
				this.setForeground(java.awt.Color.red);
		} 		
		else if (node.getUserObject() instanceof FileSystemObject) {
			FileSystemObject fso = (FileSystemObject) node.getUserObject();
			if (digest != null && fso.getOriginalChecksum() != null) {
				if (!fso.checksumMatches()) {
					this.setForeground(java.awt.Color.red);
					this.setToolTipText("Invalid Fixity value");
				} else {
					this.setForeground(new Color(0, 125, 0));
					this.setToolTipText("File has correct fixity value");
				}
			} else if (digest != null && fso.getIsDuplicate()) {
				this.setForeground(new Color(255, 127, 80));
				this.setToolTipText("Duplicate filename in checksum digest");
			}			
			else if (digest != null && digest.getFileStatus(fso) == null
					&& fso.getIsFile()) {
				this.setForeground(java.awt.Color.blue);
				this.setToolTipText("File is not listed in checksum Digest");
			}
			if (model != null
					&& model.getTreeType() == ETreeType.FileSystemTree) {
				if (fso.getIsFile()) {
					setIcon(getMimeTypeIcon(fso));
				} else {
					setIcon(folderFileSystemIcon);
				}
			} else if (model != null
					&& model.getTreeType() == ETreeType.EntityTree) {
				if (fso.getIsFile()) {
					setIcon(getMimeTypeIcon(fso));
				} else {
					setIcon(folderEntityIcon);
				}
			} else if (model != null
					&& model.getTreeType() == ETreeType.StructMapTree) {
				if (fso.getIsFile()) {
					setIcon(getMimeTypeIcon(fso));
				} else {
					setIcon(folderFileSystemIcon);
				}
			}
		} else if (node.getUserObject() instanceof FileGroup) {
			FileGroup group = (FileGroup) node.getUserObject();
			if (group.getEntityType() == RepresentationTypes.DigitalOriginal) {
				setIcon(digitalOriginalIcon);
			} else if (group.getEntityType() == RepresentationTypes.PreservationMaster) {
				setIcon(preservationCopyIcon);
			} else if (group.getEntityType() == RepresentationTypes.ModifiedMaster) {
				setIcon(modifiedMasterIcon);
			} else if (group.getEntityType() == RepresentationTypes.AccessCopy || group.getEntityType() == RepresentationTypes.AccessCopy_High ||
						group.getEntityType() == RepresentationTypes.AccessCopy_Medium || group.getEntityType() == RepresentationTypes.AccessCopy_Low ||	
						group.getEntityType() == RepresentationTypes.AccessCopy_Epub || group.getEntityType() == RepresentationTypes.AccessCopy_Pdf) {
				setIcon(accessCopyIcon);
			} else {
				setIcon(digitalOriginalIcon);
			}
		} else if (node.getUserObject() instanceof StructMap) {
			setIcon(structMapIcon);
		} else {
			setIcon(folderHomeIcon);
			
			// Build tooltip at SIP level, to inform user of all colour coded issues with this SIP.
			String toolTipText = "";
			if((node.getParent() == null) && (node.getUserObject() != null) && (model != null) && (model.getChecksumDigest() != null)){
				if (model.getChecksumDigest().getMissingFiles().size() > 0) {
					toolTipText = "Some files from checksum digest can't be located.";
				}
				if (model.getChecksumDigest().getFilesMissingFromDigest(node).size() > 0) {
					if(toolTipText.isEmpty())
						toolTipText = "Some files not listed in the checksum digest. (BLUE)";
					else
						toolTipText = toolTipText + "<br>" + "Some files not listed in the checksum digest. (BLUE)";
				}
				if (model.getChecksumDigest().hasDuplicateFiles(node)) {
					if(toolTipText.isEmpty())
						toolTipText = "Duplicate files in checksum digest. (ORANGE)";
					else
						toolTipText = toolTipText + "<br>" + "Duplicate files in checksum digest. (ORANGE)";
				}
				if (!(model.getChecksumDigest().isFixitySuccessful(node))) {
					if(toolTipText.isEmpty())
						toolTipText = "Files with invalid fixity values. (RED)";
					else
						toolTipText = toolTipText + "<br>" + "Files with invalid fixity values. (RED)";
				}
			}
			if(!toolTipText.isEmpty()){
				this.setForeground(java.awt.Color.red);
				toolTipText = "<html>" + toolTipText + "</html>";
				this.setToolTipText(toolTipText);
			}
			
			// If an IE has customized Metadata then change colour of entity
			if(node.getUserObject() != null){				
				if(node.getUserObject() instanceof FileGroupCollection && (!node.getUserObject().toString().equals("Preservation Master"))){
					FileGroupCollection entity = (FileGroupCollection) node.getUserObject();
					if(entity.isCustomized()){
						this.setForeground(java.awt.Color.MAGENTA);
						this.setToolTipText("IE has customized Metadata.");
					}
				}
			}
		}
		repaint();
		return this;
	}

}
