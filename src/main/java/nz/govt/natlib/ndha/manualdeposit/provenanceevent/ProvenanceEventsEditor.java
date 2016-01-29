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

package nz.govt.natlib.ndha.manualdeposit.provenanceevent;

import java.awt.Font;
import java.util.List;

import javax.swing.JOptionPane;

import nz.govt.natlib.ndha.common.guiutilities.FormControl;
import nz.govt.natlib.ndha.manualdeposit.FormUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;

public class ProvenanceEventsEditor extends javax.swing.JDialog implements
		ProvenanceEventsEditorView {

	// WARNING
	// When using the GUI editor, you will need to change the following in the
	// initComponents proc:
	// eventOutcomeDetailValue = new MaxLengthTextField("", 0, _maximumLength);
	// WARNING
	/**
	 * 
	 */
	private static final long serialVersionUID = 3254154190797991984L;
	private final static Log LOG = LogFactory
			.getLog(ProvenanceEventsEditor.class);
	private ProvenanceEventsPresenter thePresenter;
	private String theSettingsPath = "./";
	@SuppressWarnings("unused")
	private FormControl theControl;
	private int theMaximumLength = 100;
	private Font theStandardFont = new Font("Tahoma", Font.PLAIN, 11);

	/** Creates new form ProvenanceEventsEditor */
	public ProvenanceEventsEditor(java.awt.Frame parent, boolean modal,
			String settingsPath, int maximumLength) {
		super(parent, modal);
		theMaximumLength = maximumLength;
		theSettingsPath = settingsPath;
		initComponents(); // NOPMD
	}

	public void showView() {
		setVisible(true);
	}

	public void setFormFont(Font theFont) {
		FormUtilities.setFormFont(this, theFont);
	}

	public String getEventIdentifierValue() {
		return this.eventIdentifierValueTextBox.getText();
	}

	public void setEventIdentifierValue(String eventIdentifierValue) {
		this.eventIdentifierValueTextBox.setText(eventIdentifierValue);
	}

	public boolean getEventOutcomeValue() {
		return chkEventOutcome.isSelected();
	}

	public void setEventOutcomeValue(boolean eventDetail) {
		chkEventOutcome.setSelected(eventDetail);
	}

	public String getEventOutcomeDetailValue() {
		return this.eventOutcomeDetailValue.getText();
	}

	public void setEventOutcomeDetailValue(String eventOutcomeDetail) {
		this.eventOutcomeDetailValue.setText(eventOutcomeDetail);
	}

	@Override
	public String getProvenanceEventDescription() {
		return this.eventDescription.getText();
	}

	@Override
	public void setProvenanceEventDescription(String provenanceEventDescription) {
		this.eventDescription.setText(provenanceEventDescription);
	}

	public String getEventIdentifierType() {
		return this.eventIdentifierTypeTextBox.getText();
	}

	public void setEventIdentifierType(String eventIdentifierType) {
		this.eventIdentifierTypeTextBox.setText(eventIdentifierType);
	}

	public String getSelectedEventType() {
		int selectedIndex = this.eventTypeComboBox.getSelectedIndex();
		if (selectedIndex == -1) {
			return null;
		}

		return (String) this.eventTypeComboBox.getSelectedItem();
	}

	public void setEventTypes(List<String> values) {
		eventTypeComboBox.removeAllItems();
		for (String item : values) {
			eventTypeComboBox.addItem(item);
		}
		//As per the requirements for defect/enhancement Track ID: #63
		eventTypeComboBox.setSelectedIndex(6);
	}

	public void setSelectedEventType(String item) {
		eventTypeComboBox.setSelectedItem(item);
	}

	public void setAddProvenanceNoteEnabled(boolean enabled) {
		this.addProvenanceNoteButton.setEnabled(enabled);
	}
	
	public void setCopyProvenanceNoteEnabled(boolean enabled) {
		this.copyProvenanceNoteButton.setEnabled(enabled);
	}

	public void setEditableControlsEnabled(boolean enabled) {
		this.removeProvenanceNoteButton.setEnabled(enabled);
//		this.copyProvenanceNoteButton.setEnabled(enabled);

		this.eventIdentifierTypeTextBox.setEnabled(false);
		this.eventIdentifierValueTextBox.setEnabled(false);
		this.eventTypeComboBox.setEnabled(enabled);

		this.eventOutcomeDetailValue.setEditable(enabled);
		this.chkEventOutcome.setEnabled(enabled);

		if (enabled == false) {
			eventOutcomeDetailValue.setText(null);
			chkEventOutcome.setSelected(true);
		}
	}
	
	public void resetEventControls() {
		eventIdentifierTypeTextBox.setText(ProvenanceEventsPresenter.DEFAULT_IDENTIFIER_TYPE);
		eventIdentifierValueTextBox.setText(ProvenanceEventsPresenter.DEFAULT_IDENTIFIER_VALUE);
		eventDescription.setText(ProvenanceEventsPresenter.DEFAULT_EVENT_DESCRIPTION);
		eventTypeComboBox.setSelectedIndex(6);
	}

	public void setWarningMessage(String message) {
		warningMessageLabel.setText(message);
	}

	public void setSelectNotesItemIndex(int index) {
		provenanceNotesList.setSelectedIndex(index);
	}

	public List<ProvenanceEvent> getNotes() {
		return thePresenter.getProvenanceNotes();
	}

	public void setPresenter(ProvenanceEventsPresenter value) {
		thePresenter = value;
		thePresenter.setHandlers(provenanceNotesList, entityNamesList);
	}

	public ProvenanceEventsPresenter getPresenter() {
		return thePresenter;
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addProvenanceNoteButton = new javax.swing.JButton();
        removeProvenanceNoteButton = new javax.swing.JButton();
        copyProvenanceNoteButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        sharedNotePropertiesPanel = new javax.swing.JPanel();
        eventTypeComboBox = new javax.swing.JComboBox();
        eventTypeLabel = new javax.swing.JLabel();
        eventIdentifierTypeLabel = new javax.swing.JLabel();
        eventIdentifierTypeTextBox = new javax.swing.JTextField();
        eventIdentifierValueLabel = new javax.swing.JLabel();
        eventIdentifierValueTextBox = new javax.swing.JTextField();
        eventOutcomeLabel = new javax.swing.JLabel();
        chkEventOutcome = new javax.swing.JCheckBox();
        eventOutcomeDetailLabel = new javax.swing.JLabel();
        eventDescription = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        eventOutcomeDetailValue = new javax.swing.JTextArea();
        warningMessageLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        entityNamesList = new javax.swing.JList();
        scrlProvenanceNotesList = new javax.swing.JScrollPane();
        provenanceNotesList = new javax.swing.JList();

        setTitle("Edit Provenance Events");
        setName("ProvenanceEventsEditor"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        addProvenanceNoteButton.setText("Add");
        addProvenanceNoteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addProvenanceNoteButtonActionPerformed(evt);
            }
        });
        
        copyProvenanceNoteButton.setText("Copy");
        copyProvenanceNoteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	copyProvenanceNoteButtonActionPerformed(evt);
            }
        });

        removeProvenanceNoteButton.setText("Remove");
        removeProvenanceNoteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeProvenanceNoteButtonActionPerformed(evt);
            }
        });

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        sharedNotePropertiesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Provenance Note properties"));

        eventTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        eventTypeLabel.setText("Event type");

        eventIdentifierTypeLabel.setText("Event identifier type");

        eventIdentifierTypeTextBox.setText(ProvenanceEventsPresenter.DEFAULT_IDENTIFIER_TYPE);
        eventIdentifierTypeTextBox.setEnabled(false);

        eventIdentifierValueLabel.setText("Event identifier value");

        eventIdentifierValueTextBox.setText(ProvenanceEventsPresenter.DEFAULT_IDENTIFIER_VALUE);
        eventIdentifierValueTextBox.setEnabled(false);

        eventOutcomeLabel.setText("Successful outcome?");

        eventOutcomeDetailLabel.setText("Event outcome detail");

        eventDescription.setText(ProvenanceEventsPresenter.DEFAULT_EVENT_DESCRIPTION);
        eventDescription.setEnabled(false);

        jLabel1.setLabelFor(eventDescription);
        jLabel1.setText("Event Description");

        eventOutcomeDetailValue.setColumns(20);
        eventOutcomeDetailValue.setLineWrap(true);
        eventOutcomeDetailValue.setRows(5);
        jScrollPane1.setViewportView(eventOutcomeDetailValue);

        javax.swing.GroupLayout sharedNotePropertiesPanelLayout = new javax.swing.GroupLayout(sharedNotePropertiesPanel);
        sharedNotePropertiesPanel.setLayout(sharedNotePropertiesPanelLayout);
        sharedNotePropertiesPanelLayout.setHorizontalGroup(
            sharedNotePropertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sharedNotePropertiesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sharedNotePropertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(eventIdentifierValueLabel)
                    .addComponent(eventIdentifierTypeLabel)
                    .addComponent(eventTypeLabel)
                    .addComponent(eventOutcomeLabel)
                    .addComponent(eventOutcomeDetailLabel)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sharedNotePropertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(eventDescription)
                    .addComponent(chkEventOutcome)
                    .addComponent(eventIdentifierTypeTextBox)
                    .addComponent(eventTypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(eventIdentifierValueTextBox)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        sharedNotePropertiesPanelLayout.setVerticalGroup(
            sharedNotePropertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sharedNotePropertiesPanelLayout.createSequentialGroup()
                .addGroup(sharedNotePropertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(eventTypeLabel)
                    .addComponent(eventTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sharedNotePropertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(eventIdentifierTypeLabel)
                    .addComponent(eventIdentifierTypeTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sharedNotePropertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(eventIdentifierValueLabel)
                    .addComponent(eventIdentifierValueTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sharedNotePropertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkEventOutcome)
                    .addComponent(eventOutcomeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sharedNotePropertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(eventOutcomeDetailLabel)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addGroup(sharedNotePropertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(eventDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)))
        );

        jLabel1.getAccessibleContext().setAccessibleName("event Description");

        warningMessageLabel.setForeground(new java.awt.Color(255, 51, 0));
        warningMessageLabel.setText("warning_message_place_holder");

        entityNamesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(entityNamesList);

//        jList2.setModel(new javax.swing.AbstractListModel() {
//            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
//            public int getSize() { return strings.length; }
//            public Object getElementAt(int i) { return strings[i]; }
//        });
        scrlProvenanceNotesList.setViewportView(provenanceNotesList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(layout.createParallelGroup(Alignment.TRAILING)
        				.addGroup(layout.createSequentialGroup()
        					.addComponent(addProvenanceNoteButton, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        					.addComponent(removeProvenanceNoteButton))
        				.addComponent(scrlProvenanceNotesList, GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE))
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addGroup(layout.createSequentialGroup()
        					.addGap(18)
        					.addGroup(layout.createParallelGroup(Alignment.TRAILING)
        						.addComponent(warningMessageLabel, GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
        						.addGroup(layout.createParallelGroup(Alignment.LEADING)
        							.addGroup(layout.createSequentialGroup()
        								.addGap(10)
        								.addComponent(copyProvenanceNoteButton))
        							.addComponent(sharedNotePropertiesPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        					.addContainerGap())
        				.addGroup(layout.createSequentialGroup()
        					.addPreferredGap(ComponentPlacement.RELATED, 436, Short.MAX_VALUE)
        					.addComponent(closeButton)
        					.addGap(22))))
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addGroup(layout.createSequentialGroup()
        					.addComponent(sharedNotePropertiesPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(copyProvenanceNoteButton)
        					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        					.addComponent(warningMessageLabel))
        				.addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
        				.addComponent(scrlProvenanceNotesList, GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(closeButton)
        				.addComponent(removeProvenanceNoteButton)
        				.addComponent(addProvenanceNoteButton))
        			.addGap(46))
        );
        getContentPane().setLayout(layout);

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void formWindowClosing(java.awt.event.WindowEvent evt) {
		try {
			thePresenter.closeForm();
		} catch (Exception ex) {
		}
	}

	private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			thePresenter.closeForm();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null ,ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	void removeProvenanceNoteButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		thePresenter.removeSelectedItem();
	}

	void addProvenanceNoteButtonActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			thePresenter.addNewProvenanceNote();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null ,ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	void copyProvenanceNoteButtonActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			CopyEventsEditor copyEditor = new CopyEventsEditor(this,
					true, theSettingsPath);
			copyEditor.setFormFont(theStandardFont);
			copyEditor.setPresenter(thePresenter);
			
			thePresenter.copyExistingProvenanceNote(copyEditor);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null ,ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void formWindowOpened(java.awt.event.WindowEvent evt) {
		try {
			theControl = new FormControl(this, theSettingsPath);
		} catch (Exception ex) {
			LOG.error("Error loading form parameters", ex);
		}
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addProvenanceNoteButton;
    private javax.swing.JButton copyProvenanceNoteButton;
    private javax.swing.JCheckBox chkEventOutcome;
    private javax.swing.JButton closeButton;
    private javax.swing.JTextField eventDescription;
    private javax.swing.JLabel eventIdentifierTypeLabel;
    private javax.swing.JTextField eventIdentifierTypeTextBox;
    private javax.swing.JLabel eventIdentifierValueLabel;
    private javax.swing.JTextField eventIdentifierValueTextBox;
    private javax.swing.JLabel eventOutcomeDetailLabel;
    private javax.swing.JTextArea eventOutcomeDetailValue;
    private javax.swing.JLabel eventOutcomeLabel;
    private javax.swing.JComboBox eventTypeComboBox;
    private javax.swing.JLabel eventTypeLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList entityNamesList;
    private javax.swing.JList provenanceNotesList;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane scrlProvenanceNotesList;
    private javax.swing.JButton removeProvenanceNoteButton;
    private javax.swing.JPanel sharedNotePropertiesPanel;
    private javax.swing.JLabel warningMessageLabel;
    // End of variables declaration//GEN-END:variables

}