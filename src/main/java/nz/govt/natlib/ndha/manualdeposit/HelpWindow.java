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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import nz.govt.natlib.ndha.common.guiutilities.FormControl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HelpWindow extends JFrame implements ActionListener {

	private static final long serialVersionUID = 7839075266786971361L;
	private final static Log LOG = LogFactory.getLog(HelpWindow.class);
	private final JEditorPane editorpane;
	private final URL helpURL;
	@SuppressWarnings("unused")
	private FormControl frmControl; // NOPMD Needs to be here for it to work

	/** Creates new form HelpWindow */
	public HelpWindow(final String title, final URL hlpURL,
			final String settingsPath) {
		super(title);
		try {
			frmControl = new FormControl(this, settingsPath);
		} catch (Exception ex) {
			LOG.error("Error loading form parameters", ex);
		}
		helpURL = hlpURL;
		editorpane = new JEditorPane();
		editorpane.setEditable(false);
		try {
			editorpane.setPage(helpURL);
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
		// anonymous inner listener
		editorpane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(final HyperlinkEvent ev) {
				try {
					if (ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						editorpane.setPage(ev.getURL());
					}
				} catch (IOException ex) {
					// put message in window
					LOG.error(ex.getMessage(), ex);
				}
			}
		});
		getContentPane().add(new JScrollPane(editorpane));
		addButtons();
		// no need for listener just dispose
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	public void actionPerformed(final ActionEvent e) {
		final String strAction = e.getActionCommand();
		try {
			if (strAction != null && strAction.equals("Contents")) {
				editorpane.setPage(helpURL);
			}
			if (strAction != null && strAction.equals("Close")) {
				// more portable if delegated
				processWindowEvent(new WindowEvent(this,
						WindowEvent.WINDOW_CLOSING));
			}
		} catch (IOException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	/**
	 * add buttons at the south
	 */
	private void addButtons() {
		final JButton btncontents = new JButton("Contents");
		btncontents.addActionListener(this);
		final JButton btnclose = new JButton("Close");
		btnclose.addActionListener(this);
		// put into JPanel
		final JPanel panebuttons = new JPanel();
		panebuttons.add(btncontents);
		panebuttons.add(btnclose);
		// add panel south
		getContentPane().add(panebuttons, BorderLayout.SOUTH);
	}
}
