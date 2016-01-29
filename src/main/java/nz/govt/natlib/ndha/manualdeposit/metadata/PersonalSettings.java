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

import java.awt.Font;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nz.govt.natlib.ndha.common.XMLHandler;
import nz.govt.natlib.ndha.common.XMLHandler.XMLObject;
import nz.govt.natlib.ndha.common.mets.FileSystemObject.SortBy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PersonalSettings {

	public enum TextPosition {
		End("Ending", "at the end of the file name"), Beginning("Starting",
				"at the start of the file name"), ExactMatch(
				"Exactly matching", "as the file name"), Anywhere(
				"Anywhere in (use with caution)",
				"anywhere in the file name (use with caution)");

		private final String theDescription;
		private final String theFullDescription;

		TextPosition(final String description, final String fullDescription) {
			theDescription = description;
			theFullDescription = fullDescription;
		}

		public String description() {
			return theDescription;
		}

		public String fullDescription() {
			return theFullDescription;
		}

		public String toString() {
			return theDescription;
		}
	}

	public static final class FileDescriptions {

		private static final String TEXT_POSITION_ATTR = "TextPosition";
		private static final String TEXT_TO_FIND_ATTR = "TextToFind";

		private TextPosition theTextPosition;
		private String theTextToFind;

		public static FileDescriptions create(final TextPosition textPosition,
				final String textToFind) {
			return new FileDescriptions(textPosition, textToFind);
		}

		public FileDescriptions(final TextPosition textPosition,
				final String textToFind) {
			theTextPosition = textPosition;
			theTextToFind = textToFind;
		}

		public static FileDescriptions create(final XMLObject object) {
			return new FileDescriptions(object);
		}

		public FileDescriptions(final XMLObject object) {
			this.setFromXMLObject(object);
		}

		public void setTextPosition(final TextPosition value) {
			theTextPosition = value;
		}

		public TextPosition getTextPosition() {
			return theTextPosition;
		}

		public void setTextToFind(final String value) {
			theTextToFind = value;
		}

		public String getTextToFind() {
			return theTextToFind;
		}

		public String toString() {
			final StringBuilder retVal = new StringBuilder();
			retVal.append("Look for ");
			retVal.append(theTextToFind);
			retVal.append(" ");
			retVal.append(theTextPosition.fullDescription());
			return retVal.toString();
		}

		public XMLObject getXMLObject(final XMLHandler handler) {
			final XMLObject object = handler.createXMLObject(
					FILES_TO_IGNORE_ATTR, theTextPosition + theTextToFind);
			XMLObject obj = XMLObject.create(TEXT_POSITION_ATTR,
					TEXT_POSITION_ATTR, theTextPosition.name());
			object.addChild(TEXT_POSITION_ATTR, obj);
			obj = XMLObject.create(TEXT_TO_FIND_ATTR, TEXT_TO_FIND_ATTR,
					theTextToFind);
			object.addChild(TEXT_TO_FIND_ATTR, obj);
			return object;
		}

		public void setFromXMLObject(final XMLObject object) {
			for (XMLObject childObject : object.getChildObjects()) {
				if (childObject.getObjectType().equals(TEXT_POSITION_ATTR)) {
					theTextPosition = TextPosition.valueOf(childObject
							.getObjectValue());
				} else if (childObject.getObjectType()
						.equals(TEXT_TO_FIND_ATTR)) {
					theTextToFind = childObject.getObjectValue();
				}
			}
		}
	}

	private final static Log LOG = LogFactory.getLog(PersonalSettings.class);
	private static final String XML_ROOT_NAME = "PersonalSettings";
	private static final String FAVOURITES_ATTR = "Favourites";
	private static final String FILES_TO_IGNORE_ATTR = "FilesToIgnore";
	private static final String FONT_ATTR = "Font";
	private static final String FONT_SIZE_ATTR = "FontSize";
	private static final String FONT_BOLD_ATTR = "FontBold";
	private static final String FONT_ITALIC_ATTR = "FontItalic";
	private static final String FONT_PLAIN_ATTR = "FontPlain";
	private static final String CURRENT_PATH_ATTR = "CurrentPath";
	private static final String SORT_FILES_BY_ATTR = "SortFilesBy";
	private static final String SORT_PENDING_JOBS_ATTR = "SortPendingJobs";
	private static final String SORT_RUNNING_JOBS_ATTR = "SortRunningJobs";
	private static final String SORT_DEPOSITED_JOBS_ATTR = "SortDepositedJobs";
	private static final String SORT_COMPLETE_JOBS_ATTR = "SortCompleteJobs";
	private static final String SORT_FAILED_JOBS_ATTR = "SortFailedJobs";
	private static final String NO_OF_RETRIES_ATTR = "NoOfRetries";

	private String fileName;
	private List<String> favourites = new ArrayList<String>();
	private List<FileDescriptions> filesToIgnore = new ArrayList<FileDescriptions>();
	private String fontName = "Tahoma";
	private int fontSize = 11;
	private boolean fontBold = false;
	private boolean fontItalic = false;
	private boolean fontPlain = true;
	private String currentPath;
	private SortBy sortFilesBy = SortBy.FileName;
	private boolean sortPendingAscending = false;
	private boolean sortRunningAscending = false;
	private boolean sortDepositedAscending = false;
	private boolean sortCompleteAscending = false;
	private boolean sortFailedAscending = false;
	private int noOfRetries = 3;

	public static PersonalSettings create(final String settingsPath)
			throws FileNotFoundException {
		return new PersonalSettings(settingsPath);
	}

	public PersonalSettings(final String settingsPath)
			throws FileNotFoundException {
		fileName = settingsPath + "/PersonalSettings.xml";
		loadData();
	}

	public List<String> getFavourites() {
		return favourites;
	}

	public void addFavourite(final String favourite) {
		if (!favouriteExists(favourite)) {
			favourites.add(favourite);
			saveData();
		}
	}

	public void deleteFavourite(final String favouriteToDelete) {
		for (String favourite : favourites) {
			if (favourite.equalsIgnoreCase(favouriteToDelete)) {
				favourites.remove(favourite);
				saveData();
				break;
			}
		}
	}

	private boolean favouriteExists(final String favoriteToCheck) {
		// No guarantee that the case will be the same, so can't use
		// _favourites.contains(favourite) as this is case sensitive
		boolean exists = false;
		for (String favourite : favourites) {
			if (favourite.equalsIgnoreCase(favoriteToCheck)) {
				exists = true;
				break;
			}
		}
		return exists;
	}

	public void clearFavourites() {
		favourites = new ArrayList<String>();
	}

	public int getNoOfFavourites() {
		return favourites.size();
	}

	public FileDescriptions createFileDescription(
			final TextPosition textPosition, final String textToFind) {
		return FileDescriptions.create(textPosition, textToFind);
	}

	public boolean fileToIgnoreExists(final TextPosition textPosition,
			final String textToFind) {
		return fileToIgnoreExists(createFileDescription(textPosition,
				textToFind));
	}

	public boolean fileToIgnoreExists(final FileDescriptions fileToIgnore) {
		boolean exists = false;
		if (filesToIgnore.contains(fileToIgnore)) {
			exists = true;
		} else {
			for (FileDescriptions testFileToIgnore : filesToIgnore) {
				if (testFileToIgnore.getTextPosition() == fileToIgnore
						.getTextPosition()
						&& testFileToIgnore.getTextToFind().equalsIgnoreCase(
								fileToIgnore.getTextToFind())) {
					exists = true;
					break;
				}
			}
		}
		return exists;
	}

	public List<FileDescriptions> getFilesToIgnore() {
		return filesToIgnore;
	}

	public void clearFilesToIgnore() {
		filesToIgnore = new ArrayList<FileDescriptions>();
	}

	public void deleteFileToIgnore(final TextPosition textPosition,
			final String textToFind) {
		deleteFileToIgnore(createFileDescription(textPosition, textToFind));
	}

	public void deleteFileToIgnore(final FileDescriptions fileToIgnore) {
		if (filesToIgnore.contains(fileToIgnore)) {
			filesToIgnore.remove(fileToIgnore);
		} else {
			for (FileDescriptions testFileToIgnore : filesToIgnore) {
				if (testFileToIgnore.getTextPosition() == fileToIgnore
						.getTextPosition()
						&& testFileToIgnore.getTextToFind().equalsIgnoreCase(
								fileToIgnore.getTextToFind())) {
					filesToIgnore.remove(testFileToIgnore);
					break;
				}
			}
		}
	}

	public void addFileToIgnore(final TextPosition textPosition,
			final String textToFind) {
		deleteFileToIgnore(FileDescriptions.create(textPosition, textToFind));
	}

	public void addFileToIgnore(final FileDescriptions fileToIgnore) {
		if (!fileToIgnoreExists(fileToIgnore)) {
			filesToIgnore.add(fileToIgnore);
		}
	}

	public boolean ignoreFile(final String fileName) {
		boolean fileShouldBeIgnored = false;
		for (FileDescriptions fileToIgnore : filesToIgnore) {
			switch (fileToIgnore.getTextPosition()) {
			case Beginning:
				if (fileName.toLowerCase(Locale.ENGLISH).startsWith(
						fileToIgnore.getTextToFind()
								.toLowerCase(Locale.ENGLISH))) {
					fileShouldBeIgnored = true;
				}
				break;
			case End:
				if (fileName.toLowerCase(Locale.ENGLISH).endsWith(
						fileToIgnore.getTextToFind()
								.toLowerCase(Locale.ENGLISH))) {
					fileShouldBeIgnored = true;
				}
				break;
			case ExactMatch:
				if (fileName.equalsIgnoreCase(fileToIgnore.getTextToFind())) {
					fileShouldBeIgnored = true;
				}
				break;
			case Anywhere:
				if (fileName.toLowerCase(Locale.ENGLISH).contains(
						fileToIgnore.getTextToFind()
								.toLowerCase(Locale.ENGLISH))) {
					fileShouldBeIgnored = true;
				}
				break;
			default:
				break;
			}
			if (fileShouldBeIgnored) {
				break;
			}
		}
		return fileShouldBeIgnored;
	}

	public Font getStandardFont() {
		int attribute = Font.PLAIN;
		if (fontBold) {
			attribute += Font.BOLD;
		}
		if (fontItalic) {
			attribute += Font.ITALIC;
		}
		return new Font(fontName, attribute, fontSize);
	}

	public void setStandardFont(final Font value) {
		fontName = value.getFontName();
		fontSize = value.getSize();
		fontBold = value.isBold();
		fontItalic = value.isItalic();
		fontPlain = value.isPlain();
		saveData();
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(final String value) {
		fontName = value;
		saveData();
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(final int value) {
		fontSize = value;
		saveData();
	}

	public boolean isFontBold() {
		return fontBold;
	}

	public void setFontBold(final boolean value) {
		fontBold = value;
		saveData();
	}

	public boolean isFontItalic() {
		return fontItalic;
	}

	public void setFontItalic(final boolean value) {
		fontItalic = value;
		saveData();
	}

	public boolean isFontPlain() {
		return fontPlain;
	}

	public void setFontPlain(final boolean value) {
		fontPlain = value;
		saveData();
	}

	public String getCurrentPath() {
		return currentPath;
	}

	public void setCurrentPath(final String value) {
		currentPath = value;
		saveData();
	}

	public SortBy getSortFilesBy() {
		return sortFilesBy;
	}

	public void setSortFilesBy(final SortBy value) {
		sortFilesBy = value;
		saveData();
	}

	public boolean isSortPendingAscending() {
		return sortPendingAscending;
	}

	public void setSortPendingAscending(final boolean value) {
		sortPendingAscending = value;
		saveData();
	}

	public boolean isSortRunningAscending() {
		return sortRunningAscending;
	}

	public void setSortRunningAscending(final boolean value) {
		sortRunningAscending = value;
		saveData();
	}

	public boolean isSortDepositedAscending() {
		return sortDepositedAscending;
	}

	public void setSortDepositedAscending(final boolean value) {
		sortDepositedAscending = value;
		saveData();
	}

	public boolean isSortCompleteAscending() {
		return sortCompleteAscending;
	}

	public void setSortCompleteAscending(final boolean value) {
		sortCompleteAscending = value;
		saveData();
	}

	public boolean isSortFailedAscending() {
		return sortFailedAscending;
	}

	public void setSortFailedAscending(final boolean value) {
		sortFailedAscending = value;
		saveData();
	}

	public int getNoOfRetries() {
		return noOfRetries;
	}

	public void setNoOfRetries(final int value) {
		noOfRetries = value;
		saveData();
	}

	private void saveData() {
		try {
			final XMLHandler handler = new XMLHandler(XML_ROOT_NAME, fileName);
			final List<XMLObject> objects = new ArrayList<XMLObject>();
			final XMLObject documentation = XMLObject.create("Documentation");
			final String documentationDetail = "This file is maintained by Indigo.  Manual modification is not recommended.";
			documentation.setObjectValue(documentationDetail);
			objects.add(documentation);
			for (String favourite : favourites) {
				final XMLObject favouriteObj = XMLObject.create(
						FAVOURITES_ATTR, favourite, favourite);
				objects.add(favouriteObj);
			}
			for (FileDescriptions fileToIgnore : filesToIgnore) {
				objects.add(fileToIgnore.getXMLObject(handler));
			}
			XMLObject obj = XMLObject.create(FONT_ATTR, FONT_ATTR, fontName);
			objects.add(obj);
			obj = XMLObject.create(FONT_SIZE_ATTR, FONT_SIZE_ATTR, String
					.format("%d", fontSize));
			objects.add(obj);
			obj = XMLObject.create(FONT_BOLD_ATTR, FONT_BOLD_ATTR, String
					.format("%b", fontBold));
			objects.add(obj);
			obj = XMLObject.create(FONT_ITALIC_ATTR, FONT_ITALIC_ATTR, String
					.format("%b", fontItalic));
			objects.add(obj);
			obj = XMLObject.create(FONT_PLAIN_ATTR, FONT_PLAIN_ATTR, String
					.format("%b", fontPlain));
			objects.add(obj);
			obj = XMLObject.create(CURRENT_PATH_ATTR, CURRENT_PATH_ATTR,
					currentPath);
			objects.add(obj);
			obj = XMLObject.create(SORT_FILES_BY_ATTR, SORT_FILES_BY_ATTR,
					sortFilesBy.name());
			objects.add(obj);
			obj = XMLObject.create(SORT_PENDING_JOBS_ATTR,
					SORT_PENDING_JOBS_ATTR, String.format("%b",
							sortPendingAscending));
			objects.add(obj);
			obj = XMLObject.create(SORT_RUNNING_JOBS_ATTR,
					SORT_RUNNING_JOBS_ATTR, String.format("%b",
							sortRunningAscending));
			objects.add(obj);
			obj = XMLObject.create(SORT_DEPOSITED_JOBS_ATTR,
					SORT_DEPOSITED_JOBS_ATTR, String.format("%b",
							sortDepositedAscending));
			objects.add(obj);
			obj = XMLObject.create(SORT_COMPLETE_JOBS_ATTR,
					SORT_COMPLETE_JOBS_ATTR, String.format("%b",
							sortCompleteAscending));
			objects.add(obj);
			obj = XMLObject.create(SORT_FAILED_JOBS_ATTR,
					SORT_FAILED_JOBS_ATTR, String.format("%b",
							sortFailedAscending));
			objects.add(obj);
			obj = XMLObject.create(NO_OF_RETRIES_ATTR, NO_OF_RETRIES_ATTR,
					String.format("%d", noOfRetries));
			objects.add(obj);
			handler.setObjects(objects);
			handler.writeXMLFile();
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	// Can safely ignore the high cyclomatic complexity
	// There are a lot of if statements because switch doesn't support strings.
	// This is effectively a switch statement though
	// Could be refactored to use an enum instead.
	private void loadData() throws FileNotFoundException { // NOPMD
		final XMLHandler handler = new XMLHandler(XML_ROOT_NAME, fileName);
		final List<String> keys = handler.getObjectNames();
		for (String key : keys) {
			final XMLObject object = handler.getObject(key);
			if (object.getObjectType().equals(FAVOURITES_ATTR)) {
				final String favouriteValue = object.getObjectValue();
				if (!favouriteExists(favouriteValue)) {
					favourites.add(favouriteValue);
				}
			} else if (object.getObjectType().equals(FILES_TO_IGNORE_ATTR)) {
				filesToIgnore.add(FileDescriptions.create(object));
			} else if (object.getObjectType().equals(FONT_ATTR)) {
				fontName = object.getObjectValue();
			} else if (object.getObjectType().equals(FONT_SIZE_ATTR)) {
				fontSize = Integer.parseInt(object.getObjectValue());
			} else if (object.getObjectType().equals(FONT_BOLD_ATTR)) {
				fontBold = Boolean.parseBoolean(object.getObjectValue());
			} else if (object.getObjectType().equals(FONT_ITALIC_ATTR)) {
				fontItalic = Boolean.parseBoolean(object.getObjectValue());
			} else if (object.getObjectType().equals(FONT_PLAIN_ATTR)) {
				fontPlain = Boolean.parseBoolean(object.getObjectValue());
			} else if (object.getObjectType().equals(CURRENT_PATH_ATTR)) {
				currentPath = object.getObjectValue();
			} else if (object.getObjectType().equals(SORT_FILES_BY_ATTR)) {
				sortFilesBy = SortBy.valueOf(object.getObjectValue());
			} else if (object.getObjectType().equals(SORT_PENDING_JOBS_ATTR)) {
				sortPendingAscending = Boolean.parseBoolean(object
						.getObjectValue());
			} else if (object.getObjectType().equals(SORT_RUNNING_JOBS_ATTR)) {
				sortRunningAscending = Boolean.parseBoolean(object
						.getObjectValue());
			} else if (object.getObjectType().equals(SORT_DEPOSITED_JOBS_ATTR)) {
				sortDepositedAscending = Boolean.parseBoolean(object
						.getObjectValue());
			} else if (object.getObjectType().equals(SORT_COMPLETE_JOBS_ATTR)) {
				sortCompleteAscending = Boolean.parseBoolean(object
						.getObjectValue());
			} else if (object.getObjectType().equals(SORT_FAILED_JOBS_ATTR)) {
				sortFailedAscending = Boolean.parseBoolean(object
						.getObjectValue());
			} else if (object.getObjectType().equals(NO_OF_RETRIES_ATTR)) {
				noOfRetries = Integer.parseInt(object.getObjectValue());
			}
		}

	}

}
