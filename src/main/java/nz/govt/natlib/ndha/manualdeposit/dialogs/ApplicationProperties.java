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

package nz.govt.natlib.ndha.manualdeposit.dialogs;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import nz.govt.natlib.ndha.common.guiutilities.FormControl;
import nz.govt.natlib.ndha.manualdeposit.FormUtilities;
import nz.govt.natlib.ndha.manualdeposit.ManualDepositMain;
import nz.govt.natlib.ndha.manualdeposit.metadata.PersonalSettings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class ApplicationProperties extends javax.swing.JDialog {

	protected class WholeNumberDocument extends PlainDocument {

		private static final long serialVersionUID = 2819561773418391970L;

		@Override
		public void insertString(final int offs, final String str,
				final AttributeSet a) throws BadLocationException {
			final char[] source = str.toCharArray();
			final char[] result = new char[source.length];
			int j = 0;
			for (int i = 0; i < result.length; i++) {
				if ((Character.isDigit(source[i]))) {
					result[j++] = source[i];
				} else {
					TOOLKIT.beep();
				}
			}
			super.insertString(offs, new String(result, 0, j), a);
		}
	}

	private static final long serialVersionUID = 1217295448500642568L;
	private final static Toolkit TOOLKIT = Toolkit.getDefaultToolkit();
	private final ManualDepositMain theParent;
	private Font theStandardFont;
	private boolean isSystemChange = false;
	@SuppressWarnings("unused")
	private FormControl theFormControl; // NOPMD Won't work if it's made a local
										// variable
	private final static Log LOG = LogFactory
			.getLog(ApplicationProperties.class);
	private final PersonalSettings personalSettings;

	public ApplicationProperties(final ManualDepositMain parent,
			final boolean modal, final Font theFont, final String settingsPath,
			final PersonalSettings settings) {
		super(parent, modal);
		initComponents();
		txtFontSize.setDocument(new WholeNumberDocument());
		txtNoOfRetries.setDocument(new WholeNumberDocument());
		theParent = parent;
		personalSettings = settings;
		setupForm(settingsPath);
		setFormFont(theFont);
		checkButtons();
	}

	private void setupForm(final String settingsPath) {
		this.setTitle("System Properties");
		try {
			theFormControl = new FormControl(this, settingsPath);
		} catch (Exception ex) {
			LOG.error("Error loading form parameters", ex);
		}
		// Set fonts
		isSystemChange = true;
		final GraphicsEnvironment env = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		final Font[] fonts = env.getAllFonts();
		DefaultListModel model = new DefaultListModel();
		for (int i = 0; i < fonts.length; i++) {
			final Font font = fonts[i];
			model.add(i, font.getName());
		}
		lstFont.setModel(model);

		// Set styles
		model = new DefaultListModel();
		model.addElement("Plain");
		model.addElement("Bold");
		model.addElement("Italic");
		model.addElement("Bold Italic");
		lstStyle.setModel(model);

		// Set sizes
		model = new DefaultListModel();
		model.addElement("8");
		model.addElement("10");
		model.addElement("12");
		model.addElement("14");
		model.addElement("18");
		model.addElement("20");
		model.addElement("22");
		model.addElement("24");
		lstSize.setModel(model);
		txtFontSize.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(final DocumentEvent e) {
				updateFontSize();
			}

			public void removeUpdate(final DocumentEvent e) {
				updateFontSize();
			}

			public void insertUpdate(final DocumentEvent e) {
				updateFontSize();
			}
		});

		model = new DefaultListModel();
		for (String favourite : personalSettings.getFavourites()) {
			model.addElement(favourite);
		}
		lstFavourites.setModel(model);

		model = new DefaultListModel();
		for (PersonalSettings.FileDescriptions descriptions : personalSettings
				.getFilesToIgnore()) {
			model.addElement(descriptions);
		}
		lstFilesToIgnore.setModel(model);

		final DefaultComboBoxModel cmbModel = new DefaultComboBoxModel();
		for (PersonalSettings.TextPosition position : PersonalSettings.TextPosition
				.values()) {
			cmbModel.addElement(position);
		}
		cmbTextPosition.setModel(cmbModel);

		chkSortRunning.setSelected(personalSettings.isSortRunningAscending());
		chkSortPending.setSelected(personalSettings.isSortPendingAscending());
		chkSortFailed.setSelected(personalSettings.isSortFailedAscending());
		chkSortDeposited.setSelected(personalSettings
				.isSortDepositedAscending());
		chkSortComplete.setSelected(personalSettings.isSortCompleteAscending());
		txtNoOfRetries.setText(String.format("%d", personalSettings
				.getNoOfRetries()));

		isSystemChange = false;
	}

	private void interpretFont() {
		isSystemChange = true;
		final DefaultListModel model = (DefaultListModel) lstSize.getModel();
		final String size = String.format("%d", theStandardFont.getSize());
		if (model.contains(size)) {
			lstSize.setSelectedValue(size, true);
		} else {
			lstSize.clearSelection();
		}
		txtFontSize.setText(String.format("%d", theStandardFont.getSize()));
		lstFont.setSelectedValue(theStandardFont.getFontName(), true);
		if (theStandardFont.isBold()) {
			if (theStandardFont.isItalic()) {
				lstStyle.setSelectedValue("Bold Italic", true);
			} else {
				lstStyle.setSelectedValue("Bold", true);
			}
		} else if (theStandardFont.isItalic()) {
			lstStyle.setSelectedValue("Italic", true);
		} else {
			lstStyle.setSelectedValue("Plain", true);
		}
		txtFontStyle.setText((String) lstStyle.getSelectedValue());
		txtFont.setText((String) lstFont.getSelectedValue());
		isSystemChange = false;
	}

	public void setFormFont(final Font theFont) {
		theStandardFont = theFont;
		FormUtilities.setFormFont(this, theStandardFont);
		interpretFont();
	}

	private void setNewFont() {
		final String fontName = txtFont.getText();
		txtFont.setText(fontName);
		final String fontStyle = txtFontStyle.getText();
		int style;
		if (fontStyle == null) {
			style = Font.PLAIN;
		} else if (fontStyle.equalsIgnoreCase("Bold Italic")) {
			style = Font.BOLD + Font.ITALIC;
		} else if (fontStyle.equalsIgnoreCase("Bold")) {
			style = Font.BOLD;
		} else if (fontStyle.equalsIgnoreCase("Italic")) {
			style = Font.ITALIC;
		} else {
			style = Font.PLAIN;
		}
		int fontSize = 11;
		try {
			fontSize = Integer.parseInt(txtFontSize.getText());
		} catch (Exception ex) {
			LOG.error("Error parsing font size", ex);
		}
		theStandardFont = new Font(fontName, style, fontSize);
		FormUtilities.setFormFont(this, theStandardFont);
		lstFont.ensureIndexIsVisible(lstFont.getSelectedIndex());
		lstSize.ensureIndexIsVisible(lstSize.getSelectedIndex());
		lstStyle.ensureIndexIsVisible(lstStyle.getSelectedIndex());
	}

	private void closeForm() {
		setVisible(false);
	}

	private void saveAndClose() {
		personalSettings.clearFavourites();
		DefaultListModel model = (DefaultListModel) lstFavourites.getModel();
		int size = model.getSize();
		for (int i = 0; i < size; i++) {
			final String favourite = (String) model.get(i);
			personalSettings.addFavourite(favourite);
		}
		personalSettings.clearFilesToIgnore();
		model = (DefaultListModel) lstFilesToIgnore.getModel();
		size = model.getSize();
		for (int i = 0; i < size; i++) {
			final PersonalSettings.FileDescriptions fileToIgnore = (PersonalSettings.FileDescriptions) model
					.get(i);
			personalSettings.addFileToIgnore(fileToIgnore);
		}
		personalSettings.setStandardFont(theStandardFont);
		personalSettings.setSortRunningAscending(chkSortRunning.isSelected());
		personalSettings.setSortPendingAscending(chkSortPending.isSelected());
		personalSettings.setSortFailedAscending(chkSortFailed.isSelected());
		personalSettings.setSortDepositedAscending(chkSortDeposited
				.isSelected());
		personalSettings.setSortCompleteAscending(chkSortComplete.isSelected());
		try {
			personalSettings.setNoOfRetries(Integer.parseInt(txtNoOfRetries
					.getText()));
		} catch (Exception ex) {
			LOG.error("Error setting no of retries", ex);
		}

		theParent.setFormFont(theStandardFont);
		closeForm();
	}

	private void updateFontSize() {
		if (!isSystemChange) {
			isSystemChange = true;
			final DefaultListModel model = (DefaultListModel) lstSize
					.getModel();
			if (model.contains(txtFontSize.getText())) {
				lstSize.setSelectedValue(txtFontSize.getText(), true);
			} else {
				lstSize.clearSelection();
			}
			setNewFont();
			isSystemChange = false;
		}
	}

	private void addFavourite() {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select favourite directory");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		final int result = fc.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			final DefaultListModel model = (DefaultListModel) lstFavourites
					.getModel();
			model.addElement(fc.getSelectedFile().getAbsolutePath());
		}
		checkButtons();
	}

	private void deleteFavourite() {
		final DefaultListModel model = (DefaultListModel) lstFavourites
				.getModel();
		model.remove(lstFavourites.getSelectedIndex());
		checkButtons();
	}

	private void addFileToIgnore() {
		final PersonalSettings.TextPosition textPosition = (PersonalSettings.TextPosition) cmbTextPosition
				.getSelectedItem();
		final PersonalSettings.FileDescriptions description = PersonalSettings.FileDescriptions
				.create(textPosition, txtTextToLookFor.getText());
		final DefaultListModel model = (DefaultListModel) lstFilesToIgnore
				.getModel();
		model.addElement(description);
		txtTextToLookFor.setText("");
		cmbTextPosition.setSelectedIndex(0);
		checkButtons();
	}

	private void deleteFileToIgnore() {
		final DefaultListModel model = (DefaultListModel) lstFilesToIgnore
				.getModel();
		model.remove(lstFilesToIgnore.getSelectedIndex());
		checkButtons();
	}

	private void checkButtons() {
		btnDeleteFavourite.setEnabled(lstFavourites.getSelectedIndex() > -1);
		btnDeleteFileToIgnore
				.setEnabled(lstFilesToIgnore.getSelectedIndex() > -1);
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

		jCheckBox3 = new javax.swing.JCheckBox();
		pnlMain = new javax.swing.JPanel();
		lblFont = new javax.swing.JLabel();
		lblFontStyle = new javax.swing.JLabel();
		lblFontSize = new javax.swing.JLabel();
		txtFont = new javax.swing.JTextField();
		txtFontStyle = new javax.swing.JTextField();
		txtFontSize = new javax.swing.JTextField();
		scrlFont = new javax.swing.JScrollPane();
		lstFont = new javax.swing.JList();
		scrlStyle = new javax.swing.JScrollPane();
		lstStyle = new javax.swing.JList();
		scrlSize = new javax.swing.JScrollPane();
		lstSize = new javax.swing.JList();
		btnOK = new javax.swing.JButton();
		btnCancel = new javax.swing.JButton();
		scrlFavourites = new javax.swing.JScrollPane();
		lstFavourites = new javax.swing.JList();
		jLabel1 = new javax.swing.JLabel();
		btnAddFavourite = new javax.swing.JButton();
		btnDeleteFavourite = new javax.swing.JButton();
		scrlFilesToIgnore = new javax.swing.JScrollPane();
		lstFilesToIgnore = new javax.swing.JList();
		jLabel2 = new javax.swing.JLabel();
		jPanel1 = new javax.swing.JPanel();
		jLabel3 = new javax.swing.JLabel();
		btnAddFileToIgnore = new javax.swing.JButton();
		txtTextToLookFor = new javax.swing.JTextField();
		cmbTextPosition = new javax.swing.JComboBox();
		jLabel4 = new javax.swing.JLabel();
		btnDeleteFileToIgnore = new javax.swing.JButton();
		chkSortRunning = new javax.swing.JCheckBox();
		chkSortPending = new javax.swing.JCheckBox();
		chkSortFailed = new javax.swing.JCheckBox();
		chkSortDeposited = new javax.swing.JCheckBox();
		chkSortComplete = new javax.swing.JCheckBox();
		lblNoOfRetries = new javax.swing.JLabel();
		txtNoOfRetries = new javax.swing.JTextField();

		jCheckBox3.setText("jCheckBox3");

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		pnlMain.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Select System Font"));

		lblFont.setText("Font");

		lblFontStyle.setText("Font Style");

		lblFontSize.setText("Font Size");

		txtFont.setEditable(false);

		txtFontStyle.setEditable(false);

		lstFont
				.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
					public void valueChanged(
							javax.swing.event.ListSelectionEvent evt) {
						lstFontValueChanged(evt);
					}
				});
		scrlFont.setViewportView(lstFont);

		lstStyle
				.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
					public void valueChanged(
							javax.swing.event.ListSelectionEvent evt) {
						lstStyleValueChanged(evt);
					}
				});
		scrlStyle.setViewportView(lstStyle);

		lstSize
				.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
					public void valueChanged(
							javax.swing.event.ListSelectionEvent evt) {
						lstSizeValueChanged(evt);
					}
				});
		scrlSize.setViewportView(lstSize);

		javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(
				pnlMain);
		pnlMain.setLayout(pnlMainLayout);
		pnlMainLayout
				.setHorizontalGroup(pnlMainLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								pnlMainLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												pnlMainLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																scrlFont,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																305,
																Short.MAX_VALUE)
														.addComponent(
																txtFont,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																305,
																Short.MAX_VALUE)
														.addComponent(
																lblFont,
																javax.swing.GroupLayout.Alignment.LEADING))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												pnlMainLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																pnlMainLayout
																		.createSequentialGroup()
																		.addGroup(
																				pnlMainLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								lblFontStyle)
																						.addComponent(
																								txtFontStyle,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								105,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				pnlMainLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								txtFontSize,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								195,
																								Short.MAX_VALUE)
																						.addComponent(
																								lblFontSize)))
														.addGroup(
																pnlMainLayout
																		.createSequentialGroup()
																		.addComponent(
																				scrlStyle,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				107,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				scrlSize,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				193,
																				Short.MAX_VALUE)))
										.addContainerGap()));
		pnlMainLayout
				.setVerticalGroup(pnlMainLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								pnlMainLayout
										.createSequentialGroup()
										.addGroup(
												pnlMainLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																lblFontStyle)
														.addComponent(
																lblFontSize)
														.addComponent(lblFont))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												pnlMainLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																txtFontStyle,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																txtFontSize,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																txtFont,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												pnlMainLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																scrlStyle,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																58,
																Short.MAX_VALUE)
														.addComponent(
																scrlSize,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																58,
																Short.MAX_VALUE)
														.addComponent(
																scrlFont,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																58,
																Short.MAX_VALUE))
										.addContainerGap()));

		btnOK.setMaximumSize(new java.awt.Dimension(67, 23));
		btnOK.setMinimumSize(new java.awt.Dimension(67, 23));
		btnOK.setPreferredSize(new java.awt.Dimension(67, 23));
		btnOK.setText("OK");
		btnOK.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnOKActionPerformed(evt);
			}
		});

		btnCancel.setText("Cancel");
		btnCancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnCancelActionPerformed(evt);
			}
		});

		lstFavourites
				.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
					public void valueChanged(
							javax.swing.event.ListSelectionEvent evt) {
						lstFavouritesValueChanged(evt);
					}
				});
		scrlFavourites.setViewportView(lstFavourites);

		jLabel1.setText("Favourite Directories");

		btnAddFavourite.setText("Add Favourite");
		btnAddFavourite.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnAddFavouriteActionPerformed(evt);
			}
		});

		btnDeleteFavourite.setText("Delete Favourite");
		btnDeleteFavourite
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						btnDeleteFavouriteActionPerformed(evt);
					}
				});

		lstFilesToIgnore
				.setToolTipText("These are files that Indigo will ignore when automatically adding files to an IE");
		lstFilesToIgnore
				.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
					public void valueChanged(
							javax.swing.event.ListSelectionEvent evt) {
						lstFilesToIgnoreValueChanged(evt);
					}
				});
		scrlFilesToIgnore.setViewportView(lstFilesToIgnore);

		jLabel2.setText("Files to ignore");

		jPanel1.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Add file to ignore"));

		jLabel3.setText("Look for");

		btnAddFileToIgnore.setText("Add File Description");
		btnAddFileToIgnore
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						btnAddFileToIgnoreActionPerformed(evt);
					}
				});

		jLabel4.setText("the file name");

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel1Layout
										.createSequentialGroup()
										.addComponent(jLabel3)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(
																				btnAddFileToIgnore)
																		.addContainerGap())
														.addComponent(
																txtTextToLookFor,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																143,
																Short.MAX_VALUE)))
						.addComponent(cmbTextPosition, 0, 186, Short.MAX_VALUE)
						.addGroup(
								jPanel1Layout.createSequentialGroup()
										.addComponent(jLabel4)
										.addContainerGap()));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel3)
														.addComponent(
																txtTextToLookFor,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												cmbTextPosition,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel4)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE).addComponent(
												btnAddFileToIgnore)));

		btnDeleteFileToIgnore.setText("Delete File Description");
		btnDeleteFileToIgnore
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						btnDeleteFileToIgnoreActionPerformed(evt);
					}
				});

		chkSortRunning.setText("Sort running jobs by date ascending");

		chkSortPending.setText("Sort pending jobs by date ascending");

		chkSortFailed.setText("Sort failed jobs by date ascending");

		chkSortDeposited.setText("Sort deposited jobs by date ascending");

		chkSortComplete.setText("Sort completed jobs by date ascending");

		lblNoOfRetries
				.setText("Number of times system will retry copying files");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				chkSortRunning)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				177,
																				Short.MAX_VALUE)
																		.addComponent(
																				lblNoOfRetries)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				txtNoOfRetries,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				48,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addComponent(
																chkSortFailed)
														.addComponent(
																pnlMain,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(jLabel1)
														.addGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																layout
																		.createSequentialGroup()
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup(
																								layout
																										.createSequentialGroup()
																										.addGroup(
																												layout
																														.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.LEADING)
																														.addComponent(
																																btnDeleteFileToIgnore)
																														.addComponent(
																																chkSortDeposited)
																														.addComponent(
																																chkSortComplete))
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																												199,
																												Short.MAX_VALUE)
																										.addComponent(
																												btnOK,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												112,
																												javax.swing.GroupLayout.PREFERRED_SIZE)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED))
																						.addComponent(
																								scrlFavourites,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								528,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING)
																						.addGroup(
																								layout
																										.createParallelGroup(
																												javax.swing.GroupLayout.Alignment.LEADING,
																												false)
																										.addComponent(
																												btnAddFavourite,
																												javax.swing.GroupLayout.Alignment.TRAILING,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												Short.MAX_VALUE)
																										.addComponent(
																												btnDeleteFavourite,
																												javax.swing.GroupLayout.Alignment.TRAILING,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												Short.MAX_VALUE))
																						.addComponent(
																								btnCancel,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								121,
																								javax.swing.GroupLayout.PREFERRED_SIZE)))
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								jLabel2)
																						.addComponent(
																								scrlFilesToIgnore,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								445,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jPanel1,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addComponent(
																chkSortPending))
										.addContainerGap()));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												pnlMain,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel1)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				btnAddFavourite)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				48,
																				Short.MAX_VALUE)
																		.addComponent(
																				btnDeleteFavourite))
														.addComponent(
																scrlFavourites,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																84,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel2)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																scrlFilesToIgnore,
																0, 0,
																Short.MAX_VALUE)
														.addComponent(
																jPanel1,
																javax.swing.GroupLayout.Alignment.TRAILING,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(15, 15, 15)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				btnDeleteFileToIgnore)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								chkSortRunning,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								23,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								lblNoOfRetries)
																						.addComponent(
																								txtNoOfRetries,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				chkSortPending)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				chkSortFailed)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				chkSortDeposited)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				chkSortComplete))
														.addGroup(
																layout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(
																				btnCancel)
																		.addComponent(
																				btnOK,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap()));

		pack();
   }// </editor-fold>//GEN-END:initComponents

	private void lstFilesToIgnoreValueChanged(
			final javax.swing.event.ListSelectionEvent evt) {
		checkButtons();
	}

	private void btnAddFileToIgnoreActionPerformed(
			final java.awt.event.ActionEvent evt) {
		addFileToIgnore();
	}

	private void btnDeleteFileToIgnoreActionPerformed(
			final java.awt.event.ActionEvent evt) {
		deleteFileToIgnore();
	}

	private void lstFavouritesValueChanged(
			final javax.swing.event.ListSelectionEvent evt) {
		checkButtons();
	}

	private void btnDeleteFavouriteActionPerformed(
			final java.awt.event.ActionEvent evt) {
		deleteFavourite();
	}

	private void btnAddFavouriteActionPerformed(
			final java.awt.event.ActionEvent evt) {
		addFavourite();
	}

	private void btnCancelActionPerformed(final java.awt.event.ActionEvent evt) {
		closeForm();
	}

	private void btnOKActionPerformed(final java.awt.event.ActionEvent evt) {
		saveAndClose();
	}

	private void lstSizeValueChanged(
			final javax.swing.event.ListSelectionEvent evt) {
		if (!isSystemChange) {
			isSystemChange = true;
			txtFontSize.setText((String) lstSize.getSelectedValue());
			setNewFont();
			isSystemChange = false;
		}
	}

	private void lstStyleValueChanged(
			final javax.swing.event.ListSelectionEvent evt) {
		if (!isSystemChange) {
			txtFontStyle.setText((String) lstStyle.getSelectedValue());
			setNewFont();
		}
	}

	private void lstFontValueChanged(
			final javax.swing.event.ListSelectionEvent evt) {
		if (!isSystemChange) {
			txtFont.setText((String) lstFont.getSelectedValue());
			setNewFont();
		}
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnAddFavourite;
	private javax.swing.JButton btnAddFileToIgnore;
	private javax.swing.JButton btnCancel;
	private javax.swing.JButton btnDeleteFavourite;
	private javax.swing.JButton btnDeleteFileToIgnore;
	private javax.swing.JButton btnOK;
	private javax.swing.JCheckBox chkSortComplete;
	private javax.swing.JCheckBox chkSortDeposited;
	private javax.swing.JCheckBox chkSortFailed;
	private javax.swing.JCheckBox chkSortPending;
	private javax.swing.JCheckBox chkSortRunning;
	private javax.swing.JComboBox cmbTextPosition;
	private javax.swing.JCheckBox jCheckBox3;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JLabel lblFont;
	private javax.swing.JLabel lblFontSize;
	private javax.swing.JLabel lblFontStyle;
	private javax.swing.JLabel lblNoOfRetries;
	private javax.swing.JList lstFavourites;
	private javax.swing.JList lstFilesToIgnore;
	private javax.swing.JList lstFont;
	private javax.swing.JList lstSize;
	private javax.swing.JList lstStyle;
	private javax.swing.JPanel pnlMain;
	private javax.swing.JScrollPane scrlFavourites;
	private javax.swing.JScrollPane scrlFilesToIgnore;
	private javax.swing.JScrollPane scrlFont;
	private javax.swing.JScrollPane scrlSize;
	private javax.swing.JScrollPane scrlStyle;
	private javax.swing.JTextField txtFont;
	private javax.swing.JTextField txtFontSize;
	private javax.swing.JTextField txtFontStyle;
	private javax.swing.JTextField txtNoOfRetries;
	private javax.swing.JTextField txtTextToLookFor;
	// End of variables declaration//GEN-END:variables
	
}