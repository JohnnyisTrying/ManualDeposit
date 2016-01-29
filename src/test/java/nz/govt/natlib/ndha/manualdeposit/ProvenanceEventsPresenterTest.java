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

import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nz.govt.natlib.ndha.common.mets.FileGroupCollection;
import nz.govt.natlib.ndha.common.mets.FileSystemObject;
import nz.govt.natlib.ndha.manualdeposit.provenanceevent.ProvenanceEvent;
import nz.govt.natlib.ndha.manualdeposit.provenanceevent.ProvenanceEventsEditorView;
import nz.govt.natlib.ndha.manualdeposit.provenanceevent.ProvenanceEventsPresenter;

public class ProvenanceEventsPresenterTest {
	private ProvenanceEventsPresenter presenter;
	private Mockery mockContext;
	private ProvenanceEventsEditorView view;
	AppProperties props = null;

	@Before
	public void setup() {
		mockContext = new Mockery();
		view = mockContext.mock(ProvenanceEventsEditorView.class);
		try {
			props = new AppProperties();
		} catch (Exception ex) {
			fail();
		}
//		presenter = new ProvenanceEventsPresenter(props);
	}

	@Test
	public void testRemovingNoteSelectsPreviousNote() {
		mockContext.checking(new Expectations() {
			{
				allowing(view).setWarningMessage("");

				allowing(view).setEventTypes(with(any(List.class)));
				allowing(view).setSelectedEventType(with(any(String.class)));
				allowing(view).setEventIdentifierType(with(any(String.class)));
				allowing(view).setEventIdentifierValue(with(any(String.class)));
				allowing(view).setEventOutcomeValue(with(any(boolean.class)));
				allowing(view).setProvenanceEventDescription(with(any(String.class)));
				allowing(view).setEventOutcomeDetailValue(
						with(any(String.class)));

				allowing(view).setSelectedEventType(
						ProvenanceEvent.DEFAULT_EVENT_TYPE);

				allowing(view).setEditableControlsEnabled(
						with(any(Boolean.class)));

				// one(view).replaceNoteItems(with(any(List.class)));

				allowing(view).setEditableControlsEnabled(
						with(any(Boolean.class)));
				allowing(view)
						.setSelectNotesItemIndex(with(any(Integer.class)));
				allowing(view).resetEventControls();
				allowing(view).getEventOutcomeValue();
				allowing(view).getEventOutcomeDetailValue();
				allowing(view).getEventIdentifierType();
				allowing(view).getEventIdentifierValue();
				allowing(view).getSelectedEventType();
				allowing(view).setCopyProvenanceNoteEnabled(with(any(boolean.class)));
			}
		});
//		presenter.setView(view);
		presenter = new ProvenanceEventsPresenter(view, props, new ArrayList<FileGroupCollection>(), new FileSystemObject("Test Desc", new File("Test File.jpg"), null));
		presenter.setHandlers(new javax.swing.JList(), new javax.swing.JList());

		try {
			presenter.addNewProvenanceNote();
			presenter.addNewProvenanceNote();
			presenter.selectItem(1);
		} catch (Exception ex) {
			fail();
		}


		presenter.removeSelectedItem();

		mockContext.assertIsSatisfied();
	}
}
