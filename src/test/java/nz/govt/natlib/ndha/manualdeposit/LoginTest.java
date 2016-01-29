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

import static org.junit.Assert.fail;

import java.awt.Font;

import nz.govt.natlib.ndha.manualdeposit.login.ILoginPresenter;
import nz.govt.natlib.ndha.manualdeposit.login.ILoginView;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

/**
 * @author PlayerM
 */
public class LoginTest implements ILoginView {

	private boolean formIsVisible = false;
	private final static Log LOG = LogFactory.getLog(LoginTest.class);
	private String errorMessage = null;
	private boolean errorFails = true;

	public void setPresenter(final ILoginPresenter presenter) {

	}

	public void closeForm() {
		formIsVisible = false;
	}

	public void showView() {
		LOG.debug("LoginTest showView");
		formIsVisible = true;
	}

	public void showError(String header, String message) {
		if (errorFails) {
			LOG.debug("Error occurred " + header + ", " + message);
			fail();
		} else {
			errorMessage = header + "." + message;
		}
	}

	public void showMessage(String header, String message) {
		LOG.debug("show Message " + header + ", " + message);
	}

	public boolean confirm(String message) {
		return true;
	}

	public void setFormFont(Font theFont) {

	}

	public boolean getIsVisible() {
		return formIsVisible;
	}

	public void setIsVisible(boolean value) {
		formIsVisible = value;
	}

	@Test
	public final void testSetup() {

	}

	public void setErrorFails(boolean value) {
		errorFails = value;
	}

	public boolean getErrorFails() {
		return errorFails;
	}

	public void setErrorMessage(String value) {
		errorMessage = value;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
