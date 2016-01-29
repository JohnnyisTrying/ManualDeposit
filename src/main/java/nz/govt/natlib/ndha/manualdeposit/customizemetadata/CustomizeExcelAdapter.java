package nz.govt.natlib.ndha.manualdeposit.customizemetadata;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.util.*;

/**
 * CustomizeExcelAdapter adds Excel type functionality to JTables.
 */
public class CustomizeExcelAdapter implements ActionListener {
	private String rowstring, value;
	private Clipboard system;
	private StringSelection stsel;
	private JTable jTable1;

	/**
	 * The Excel Adapter is constructed with a JTable on which it enables
	 * Copy-Paste and acts as a Clipboard listener.
	 */
	public CustomizeExcelAdapter(JTable myJTable) {
		jTable1 = myJTable;
		KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
		KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);
		KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);

		jTable1.registerKeyboardAction(this, "Copy", copy, JComponent.WHEN_FOCUSED);
		jTable1.registerKeyboardAction(this, "Paste", paste, JComponent.WHEN_FOCUSED);
		jTable1.registerKeyboardAction(this, "Delete", delete, JComponent.WHEN_FOCUSED);
		system = Toolkit.getDefaultToolkit().getSystemClipboard();

	}

	/**
	 * Public Accessor methods for the Table on which this adapter acts.
	 */
	public JTable getJTable() {
		return jTable1;
	}

	public void setJTable(JTable jTable1) {
		this.jTable1 = jTable1;
	}

	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().compareTo("Copy") == 0) {
			StringBuffer sbf = new StringBuffer();
			// Check to ensure we have selected only a contiguous block of
			// cells
			int numcols = jTable1.getSelectedColumnCount();
			int numrows = jTable1.getSelectedRowCount();
			int[] rowsselected = jTable1.getSelectedRows();
			int[] colsselected = jTable1.getSelectedColumns();
			if (!((numrows - 1 == rowsselected[rowsselected.length - 1]
					- rowsselected[0] && numrows == rowsselected.length) && (numcols - 1 == colsselected[colsselected.length - 1]
					- colsselected[0] && numcols == colsselected.length))) {
				JOptionPane.showMessageDialog(null, "Invalid Copy Selection",
						"Invalid Copy Selection", JOptionPane.ERROR_MESSAGE);
				return;
			}
			for (int i = 0; i < numrows; i++) {
				for (int j = 0; j < numcols; j++) {
					sbf.append(jTable1.getValueAt(rowsselected[i],
							colsselected[j]));
					if (j < numcols - 1)
						sbf.append("\t");
				}
				sbf.append("\n");
			}
			stsel = new StringSelection(sbf.toString());
			system = Toolkit.getDefaultToolkit().getSystemClipboard();
			system.setContents(stsel, stsel);
		}
		if (e.getActionCommand().compareTo("Paste") == 0) {
//			System.out.println("Trying to Paste");
			int startRow = (jTable1.getSelectedRows())[0];
			int startCol = (jTable1.getSelectedColumns())[0];
			try {
				String trstring = (String) (system.getContents(this)
						.getTransferData(DataFlavor.stringFlavor));
//				System.out.println("String is:" + trstring);
				StringTokenizer st1 = new StringTokenizer(trstring, "\n");
				for (int i = 0; st1.hasMoreTokens(); i++) {
					rowstring = st1.nextToken();
					StringTokenizer st2 = new StringTokenizer(rowstring, "\t");
					for (int j = 0; st2.hasMoreTokens(); j++) {
						value = (String) st2.nextToken();
						if (startRow + i < jTable1.getRowCount()
								&& startCol + j < jTable1.getColumnCount())
							jTable1.setValueAt(value, startRow + i, startCol
									+ j);
//						System.out.println("Putting " + value + "at row="
//								+ startRow + i + "column=" + startCol + j);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		if (e.getActionCommand().compareTo("Delete")==0){
	         int numcols=jTable1.getSelectedColumnCount();
	         int numrows=jTable1.getSelectedRowCount();
	         int startRow=(jTable1.getSelectedRows())[0];
	         int startCol=(jTable1.getSelectedColumns())[0];
	         String Nothing="";
	         for (int i=startRow; i<startRow+numrows; i++) {
	        	 for(int j=startCol; j<startCol+numcols; j++){
	        		 jTable1.setValueAt(Nothing,i,j);
	        	 }
			 }
		}
	}
}
