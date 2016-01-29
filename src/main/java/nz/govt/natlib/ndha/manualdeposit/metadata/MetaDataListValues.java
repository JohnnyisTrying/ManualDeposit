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

import java.util.ArrayList;
import java.util.List;

import nz.govt.natlib.ndha.common.XMLHandler;
import nz.govt.natlib.ndha.common.XMLHandler.XMLObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MetaDataListValues {

	private final static Log LOG = LogFactory.getLog(MetaDataListValues.class);
	private final static String LIST_VALUES_OBJECT_TYPE = "ListValues";
	private final static String CMS_MAPPINGS_OBJECT_TYPE = "ListValues";
	private final static String VALUE_ATTR = "Value";
	private final static String DISPLAY_ATTR = "Display";
	private final static String SORT_ORDER_ATTR = "SortOrder";
	private String theValue;
	private String theDisplay;
	private int theSortOrder;
	private List<String> cmsMappings;

	public static MetaDataListValues create(String value, String display,
			int sortOrder) {
		return new MetaDataListValues(value, display, sortOrder);
	}

	public MetaDataListValues(String value, String display, int sortOrder) {
		theValue = value;
		theDisplay = display;
		theSortOrder = sortOrder;
		cmsMappings = new ArrayList<String>();
	}

	public static MetaDataListValues create(XMLObject object) {
		return new MetaDataListValues(object);
	}

	public MetaDataListValues(XMLObject object) {
		createFromXMLObject(object);
	}

	public static String getObjectType() {
		return LIST_VALUES_OBJECT_TYPE;
	}

	public void setValue(String value) {
		theValue = value;
	}

	public String getValue() {
		return theValue;
	}

	public void setDisplay(String value) {
		theDisplay = value;
	}

	public String getDisplay() {
		return theDisplay;
	}

	public void setSortOrder(int value) {
		theSortOrder = value;
	}

	public int getSortOrder() {
		return theSortOrder;
	}

	public void setCmsMappings(List<String> value) {
		cmsMappings = value;
	}

	public List<String> getCmsMappings() {
		return cmsMappings;
	}

	public void clearCmsMappings() {
		cmsMappings = new ArrayList<String>();
	}

	public void addCmsMapping(String value) {
		cmsMappings.add(value);
	}

	public void removeCmsMapping(String value) {
		for (String mapping : cmsMappings) {
			if (mapping.equalsIgnoreCase(value)) {
				cmsMappings.remove(mapping);
				break;
			}
		}
	}

	public boolean mappingExists(String value) {
		boolean retVal = false;
		for (String mapping : cmsMappings) {
			if (mapping.equalsIgnoreCase(value)) {
				retVal = true;
				break;
			}
		}
		return retVal;
	}

	public String toString() {
		if (theDisplay == null || theDisplay.equals("")) {
			return theValue;
		} else {
			return theDisplay;
		}
	}

	public XMLObject getXMLObject(XMLHandler handler) {
		XMLObject object = handler.createXMLObject(LIST_VALUES_OBJECT_TYPE,
				String.format("%d", theSortOrder));
		object.addAttribute(VALUE_ATTR, theValue);
		object.addAttribute(DISPLAY_ATTR, theDisplay);
		object.addAttribute(SORT_ORDER_ATTR, String.format("%d", theSortOrder));
		for (String mapping : cmsMappings) {
			XMLObject mappingObject = handler.createXMLObject(
					CMS_MAPPINGS_OBJECT_TYPE, mapping, mapping);
			object.addChild(mapping, mappingObject);
		}
		return object;
	}

	private void createFromXMLObject(XMLObject object) {
		if (object.getAttribute(VALUE_ATTR) != null) {
			theValue = object.getAttribute(VALUE_ATTR);
		}
		if (object.getAttribute(DISPLAY_ATTR) != null) {
			theDisplay = object.getAttribute(DISPLAY_ATTR);
		}
		if (object.getAttribute(SORT_ORDER_ATTR) != null) {
			try {
				theSortOrder = Integer.parseInt(object
						.getAttribute(SORT_ORDER_ATTR));
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
		cmsMappings = new ArrayList<String>();
		for (XMLObject child : object.getChildObjects()) {
			if (child.getObjectType().equals(CMS_MAPPINGS_OBJECT_TYPE)) {
				cmsMappings.add(child.getObjectValue());
			}
		}
	}
}
