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

package nz.govt.natlib.ndha.manualdeposit.metadata;

import java.awt.Component;
import java.awt.Font;

import nz.govt.natlib.ndha.common.exlibris.SIPStatus;
import nz.govt.natlib.ndha.manualdeposit.IBaseForm;

public interface IMetaDataConfigurator extends IBaseForm {

	public void setPresenter(MetaDataConfiguratorPresenter thePresenter);

	public void setupScreen(String settingsPath);

	public void setFormFont(Font theFont);

	public void showError(String header, String message);

	public void showMessage(String header, String message);

	public boolean confirm(String message);

	public String getInput(String header, String message);

	public void loadData(MetaDataTypeImpl theData, String dcOther);

	public void setConfigurationFileName(String fileName);

	public Component getComponent();

	public void checkButtons();

	public void editLookupValue(MetaDataListValues value);

	public void editCmsMappingValue(String value);

	public void showUser(IndigoUser user);

	public void loadUserGroupData(UserGroupData theData);

	public void showApplicationData(ApplicationData appData);

	public void showSipStatus(SIPStatus status);

}
