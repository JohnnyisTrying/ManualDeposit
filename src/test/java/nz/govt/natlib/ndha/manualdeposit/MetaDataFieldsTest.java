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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;

import nz.govt.natlib.ndha.manualdeposit.metadata.EDataType;
import nz.govt.natlib.ndha.manualdeposit.metadata.IMetaDataTypeExtended;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataFields;

import org.junit.Test;

public class MetaDataFieldsTest {
	private final String TEMPLATE_WITH_PROVERNANCE_NOTES = "src/test/resources/Templates/MetaData/template_with_two_provernance_notes.xml";

	@Test
	public void testLoadingTemplateWithProvNotes() throws FileNotFoundException {
		MetaDataFields metaDataFields = new MetaDataFields(
				TEMPLATE_WITH_PROVERNANCE_NOTES);
		assertThat(metaDataFields.getMetaDataFields().size(), is(equalTo(15)));

		int countProvNotes = 0;
		for (IMetaDataTypeExtended metaDataType : metaDataFields
				.getMetaDataFields()) {
			if (metaDataType.getDataType() == EDataType.ProvenanceNote)
				countProvNotes++;
		}

		assertThat(countProvNotes, is(equalTo(2)));
	}

}
