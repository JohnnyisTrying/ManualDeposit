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

import java.io.FileNotFoundException;

import nz.govt.natlib.ndha.common.XMLHandler;
import nz.govt.natlib.ndha.common.XMLHandler.XMLObject;
import nz.govt.natlib.ndha.manualdeposit.exceptions.InvalidApplicationDataException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.InvalidCMSSystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class IndigoUser {
	private final static Log LOG = LogFactory.getLog(IndigoUser.class);
	private final static String STANDARD_OBJECT_TYPE = "IndigoUser";
	private final static String PROP_USER_NAME = "UserName";
	private final static String PROP_USER_GROUP_DATA_FILE = "UserGroupDataFile";
	private final static String PROP_ALLOW_BULK_LOAD = "AllowBulkLoad";
	private String theUserName;
	private String theUserGroupDataFile;
	private UserGroupData theUserGroupData = null;
	private boolean allowBulkLoad = false;

	public String getUserName() {
		return theUserName;
	}

	public void setUserName(final String value) {
		theUserName = value;
	}

	public String getUserGroupDataFile() {
		return theUserGroupDataFile;
	}

	public void setUserGroupDataFile(final String value) {
		theUserGroupDataFile = value;
	}

	public boolean isAllowBulkLoad() {
		return allowBulkLoad;
	}

	public void setAllowBulkLoad(final boolean value) {
		allowBulkLoad = value;
	}

	public UserGroupData getUserGroupData() throws FileNotFoundException,
			InvalidApplicationDataException, InvalidCMSSystemException {
		if (theUserGroupData == null) {
			theUserGroupData = UserGroupData.create(theUserGroupDataFile);
		}
		return theUserGroupData;
	}

	public static IndigoUser create(final String userName,
			final String userGroupDataFile) {
		return new IndigoUser(userName, userGroupDataFile);
	}

	public IndigoUser(final String userName, final String userGroupDataFile) {
		theUserName = userName;
		theUserGroupDataFile = userGroupDataFile;
	}

	public String toString() {
		return theUserName;
	}

	public static IndigoUser create(final XMLObject object) {
		return new IndigoUser(object);
	}

	public IndigoUser(final XMLObject object) {
		setFromXMLObject(object, true);
	}

	public static IndigoUser create(final XMLObject object,
			final boolean loadMetaData) {
		return new IndigoUser(object, loadMetaData);
	}

	public IndigoUser(final XMLObject object, final boolean loadMetaData) {
		setFromXMLObject(object, loadMetaData);
	}

	public XMLObject getXMLObject(final XMLHandler handler) {
		final XMLObject object = handler.createXMLObject(STANDARD_OBJECT_TYPE,
				theUserName);
		object.setObjectType(STANDARD_OBJECT_TYPE);
		object.setObjectName(theUserName);
		object.addAttribute(PROP_USER_NAME, theUserName);
		object.addAttribute(PROP_USER_GROUP_DATA_FILE, theUserGroupDataFile);
		object.addAttribute(PROP_ALLOW_BULK_LOAD, String.format("%b",
				allowBulkLoad));
		return object;
	}

	public void setFromXMLObject(final XMLObject object,
			final boolean loadMetaData) {
		object.setObjectType(STANDARD_OBJECT_TYPE);
		if (object.getAttribute(PROP_USER_NAME) != null) {
			theUserName = object.getAttribute(PROP_USER_NAME);
		}
		if (object.getAttribute(PROP_USER_GROUP_DATA_FILE) != null) {
			theUserGroupDataFile = object
					.getAttribute(PROP_USER_GROUP_DATA_FILE);
			try {
				theUserGroupData = UserGroupData.create(theUserGroupDataFile,
						loadMetaData, true);
			} catch (Exception ex) {
				LOG.error("Error loading Indigo user", ex);
			}
		}
		if (object.getAttribute(PROP_ALLOW_BULK_LOAD) != null) {
			try {
				allowBulkLoad = Boolean.parseBoolean(object
						.getAttribute(PROP_ALLOW_BULK_LOAD));
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
	}
}
