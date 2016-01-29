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

import java.util.List;
import java.util.Set;

import nz.govt.natlib.ndha.common.mets.FileSystemObject;
import nz.govt.natlib.ndha.common.mets.FileSystemObject.SortBy;
import nz.govt.natlib.ndha.manualdeposit.bulkupload.IBulkUpload;
import nz.govt.natlib.ndha.manualdeposit.customizemetadata.ICustomizeMetaDataEditorView;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataFields;

/**
 * Main interface for the manual deposit model/view/presenter view
 * 
 * @author PlayerM<br>
 */
public interface IManualDepositMainFrame extends IBaseForm {

	void setPresenter(ManualDepositPresenter thePresenter);

	String getInput(String header, String message, String defaultInput);

	void setSearchType(MetaDataFields.ECMSSystem cmsSystem);

	void setCurrentDirectory(String currentDirectory);

	void setupScreen(AppProperties appProperties, String settingsPath)
			throws Exception;

	void setWaitCursor(boolean isWaiting);
	
	void setProgressLevel(int percentage);

	void checkButtons();

	void showError(String header, String message);

	void showError(String header, String message, Exception ex);

	void showMessage(String header, String message);

	boolean confirm(String message);

	boolean confirm(String message, boolean useYesNo);

	String getInput(String header, String message);

	SortBy getCurrentSortBy();

	javax.swing.JFrame getComponent();

	void showMissingFiles(String settingsPath,
			List<FileSystemObject> missingFiles);
	
	void showDuplicateFiles(String settingsPath,
			Set<String> duplicateFiles);

	IBulkUpload createBulkUploadForm();
	
	ICustomizeMetaDataEditorView createCustomizeMetaDataForm();

	void setIELabel(boolean submitOK);

	void setProgressBarVisible(boolean isVisible);

}
