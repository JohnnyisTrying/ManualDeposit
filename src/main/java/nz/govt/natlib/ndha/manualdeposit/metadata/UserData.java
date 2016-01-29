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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import nz.govt.natlib.ndha.common.PropertiesUtil;
import nz.govt.natlib.ndha.common.XMLHandler;
import nz.govt.natlib.ndha.common.XMLHandler.XMLObject;
import nz.govt.natlib.ndha.common.exceptions.XmlException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class UserData implements Serializable, Iterable<IndigoUser> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1682896060935300190L;

	private final static Log LOG = LogFactory.getLog(UserData.class);
	private final static String STANDARD_OBJECT_TYPE = "UserData";
	private final static String STANDARD_USER_OBJECT_TYPE = "IndigoUser";

	private List<IndigoUser> theUsers = new ArrayList<IndigoUser>();

	public static UserData create(final String xmlFileName)
			throws FileNotFoundException {
		return new UserData(xmlFileName);
	}

	public UserData(final String xmlFileName) throws FileNotFoundException {
		loadUserData(xmlFileName, true);
	}

	public static UserData create(final String xmlFileName, boolean loadMetaData)
			throws FileNotFoundException {
		return new UserData(xmlFileName, loadMetaData);
	}

	public UserData(final String xmlFileName, final boolean loadMetaData)
			throws FileNotFoundException {
		loadUserData(xmlFileName, loadMetaData);
	}

	private void loadUserData(final String xmlFileName,
			final boolean loadMetaData) throws FileNotFoundException {
		String fileName = xmlFileName;
		File xmlFile = new File(fileName);
		if (!xmlFile.exists()) { // Search for it
			final ClassLoader contextClassLoader = PropertiesUtil
					.getContextClassLoaderInternal();
			final Enumeration<URL> urls = PropertiesUtil.getResources(
					contextClassLoader, xmlFileName);
			if ((urls == null) || (!urls.hasMoreElements())) {
				LOG.error("User file not found - " + fileName);
				throw new FileNotFoundException(fileName + " not found");
			}
			final URL xmlFileNameURL = (URL) urls.nextElement();
			xmlFile = new File(xmlFileNameURL.getPath());
			fileName = xmlFile.getPath();
		}
		loadFromXML(fileName, loadMetaData);
	}

	public void storeAsXML(final String xmlFileName) throws IOException,
			XmlException {
		final XMLHandler handler = new XMLHandler(STANDARD_OBJECT_TYPE,
				xmlFileName);
		try {
			final List<XMLObject> objects = new ArrayList<XMLObject>();
			XMLObject documentation = XMLObject.create("Documentation");
			String documentationDetail = "To edit this file, use Indigo.exe /MetaData";
			documentation.setObjectValue(documentationDetail);
			objects.add(documentation);
			for (IndigoUser user : theUsers) {
				XMLObject object = user.getXMLObject(handler);
				objects.add(object);
			}
			handler.setObjects(objects);
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error(ex.getMessage(), ex);
		}
		handler.writeXMLFile();
	}

	public void loadFromXML(String xmlFileName, boolean loadMetaData)
			throws FileNotFoundException {
		XMLHandler handler = new XMLHandler(STANDARD_OBJECT_TYPE, xmlFileName);
		List<String> keys = handler.getObjectNames();
		for (String key : keys) {
			try {
				XMLObject object = handler.getObject(key);
				if (object.getObjectType().equals(STANDARD_USER_OBJECT_TYPE)) {
					IndigoUser user = IndigoUser.create(object, loadMetaData);
					theUsers.add(user);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
	}

	public Iterator<IndigoUser> iterator() {
		return theUsers.iterator();
	}

	public int size() {
		return theUsers.size();
	}

	public void addUser(IndigoUser value) {
		theUsers.add(value);
	}

	public void deleteUser(IndigoUser value) {
		for (int i = 0; i < theUsers.size(); i++) {
			IndigoUser user = theUsers.get(i);
			if (user.getUserName().equals(value.getUserName())) {
				theUsers.remove(i);
				break;
			}
		}
	}

	public IndigoUser getAt(int position) {
		return getUsers().get(position);
	}

	public List<IndigoUser> getUsers() {
		return theUsers;
	}

	public IndigoUser getUser(String userName) {
		IndigoUser user = null;
		for (IndigoUser userTest : theUsers) {
			if (userTest.getUserName().equals(userName)) {
				user = userTest;
				break;
			}
		}
		return user;
	}
}
