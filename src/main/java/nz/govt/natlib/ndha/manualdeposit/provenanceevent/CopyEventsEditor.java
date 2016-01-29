package nz.govt.natlib.ndha.manualdeposit.provenanceevent;

import java.awt.Font;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import nz.govt.natlib.ndha.common.guiutilities.FormControl;
import nz.govt.natlib.ndha.manualdeposit.FormUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CopyEventsEditor extends javax.swing.JDialog {

	// WARNING
	// When using the GUI editor, you will need to change the following in the
	// initComponents proc:
	// eventOutcomeDetailValue = new MaxLengthTextField("", 0, _maximumLength);
	// WARNING
	/**
			 * 
			 */
	private static final long serialVersionUID = 3254154190797991984L;
	private final static Log LOG = LogFactory.getLog(CopyEventsEditor.class);
	private ProvenanceEventsPresenter thePresenter;
	private String theSettingsPath = "./";
	@SuppressWarnings("unused")
	private FormControl theControl;
	private int theMaximumLength = 100;

	/** Creates new form ProvenanceEventsEditor */
	public CopyEventsEditor(ProvenanceEventsEditor provenanceEventsEditor,
			boolean modal, String settingsPath) {
		super(provenanceEventsEditor, modal);
		theSettingsPath = settingsPath;
		// initComponents(); // NOPMD
	}

	public void showView() {
		setVisible(true);
	}

	public void setFormFont(Font theFont) {
		FormUtilities.setFormFont(this, theFont);
	}

	public void setPresenter(ProvenanceEventsPresenter value) {
		thePresenter = value;
		// thePresenter.setHandlers(provenanceNotesList, entityNamesList);
	}

	public void setDefaultListModel(DefaultListModel<Object> copyListModel) {
		jList1.setModel(copyListModel);
	}

	public void setJList(javax.swing.JList<Object> copyList) {
		jList1 = copyList;
		initComponents();
	}

	public ProvenanceEventsPresenter getPresenter() {
		return thePresenter;
	}

	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		copyToButton = new javax.swing.JButton();
		jScrollPane2 = new javax.swing.JScrollPane();
		// jList1 = new javax.swing.JList();

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

		copyToButton.setText("Copy To");
		copyToButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				copyToButtonActionPerformed(evt);
			}
		});

		jScrollPane2.setViewportView(jList1);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jScrollPane2,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										187,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE))
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap(
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addComponent(copyToButton).addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jScrollPane2,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										333,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(copyToButton)
								.addContainerGap(
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));

		pack();
	}// </editor-fold>

	private void formWindowClosing(java.awt.event.WindowEvent evt) {
		try {
			// this.setVisible(false);
			// thePresenter.closeForm();
		} catch (Exception ex) {
		}
	}

	
	/**
	 * Copy current note to selected entities.
	 * @param evt
	 */
	void copyToButtonActionPerformed(java.awt.event.ActionEvent evt) {
		try {
//			Object[] theEntities = jList1.getSelectedValues();
			List<Object> theEntities = jList1.getSelectedValuesList();
			
			// Save any changes to current note before trying to copy it.
			thePresenter.saveForm();
			
			// Check if note limit has been reached for any selected entities. If it has then display error and return.
			for(Object value : theEntities){
				if(thePresenter.noteLimitReached(value.toString())){
					JOptionPane.showMessageDialog(null, "Note limit has been reached for entity: " + value.toString() + ". \nNote was not copied to any entities as a result of this.", "Unable to add new note", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			
			// Copy note to all selected entities.
			for(Object value : theEntities){
				thePresenter.addNewProvenanceNote(value.toString());
			}
			
			this.setVisible(false);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	
	private void formWindowOpened(java.awt.event.WindowEvent evt) {
		try {
//			theControl = new FormControl(this, theSettingsPath);
		} catch (Exception ex) {
			LOG.error("Error loading form parameters", ex);
		}
	}

	// Variables declaration - do not modify
	private javax.swing.JButton copyToButton;
	private javax.swing.JList<Object> jList1;
	private javax.swing.JScrollPane jScrollPane2;
	// End of variables declaration

}