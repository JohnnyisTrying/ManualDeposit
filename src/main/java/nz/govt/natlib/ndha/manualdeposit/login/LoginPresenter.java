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

package nz.govt.natlib.ndha.manualdeposit.login;

import java.util.ArrayList;
import java.util.List;

import nz.govt.natlib.ndha.manualdeposit.AppProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoginPresenter implements ILoginPresenter {

	private final static Log LOG = LogFactory.getLog(LoginPresenter.class);
	private static ILoginView loginFrame;
	private List<ILoginListener> loginListeners = new ArrayList<ILoginListener>();
	private int loginAttempts = 0;
	private final static int MAX_LOGIN_ATTEMPTS = 3;
	private static AppProperties appProps;

	public LoginPresenter(ILoginView loginForm, AppProperties appProperties)
			throws Exception {
		loginFrame = loginForm;
		loginFrame.setPresenter(this);
		appProps = appProperties;
		LOG.debug("Login class: "
				+ appProps.getApplicationData().getLoginClass());
	}

	public void setup() {
		if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
			loginFrame.closeForm();
			for (ILoginListener listener : loginListeners) {
				LoginEvent evt = new LoginEvent(listener, "", "",
						"Maximum login attempts exceeded");
				listener.loginFailed(evt);
			}
		}
		loginFrame.showView();
	}

	public void login(String userName, String password) {
		LOG.debug("logging in with " + userName);
		String loginMessage = "Could not log in";
		loginAttempts++;
		boolean loggedIn = false;
		try {
			loggedIn = appProps.getApplicationData().getLogin().login(
					appProps.getApplicationData().getPdsUrl(),
					appProps.getApplicationData().getDepositUserInstitution(),
					userName, password);
		} catch (Exception e) {
			loginMessage = e.getMessage();
		}
		if (loggedIn) {
			loginFrame.closeForm();
			for (ILoginListener listener : loginListeners) {
				LoginEvent evt = new LoginEvent(listener, userName, password,
						null);
				listener.loginSucceeded(evt);
			}
		} else {
			if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
				loginFrame.closeForm();
				for (ILoginListener listener : loginListeners) {
					LoginEvent evt = new LoginEvent(listener, "", "",
							"Maximum login attempts exceeded");
					listener.loginFailed(evt);
				}
			} else {
				loginFrame.showError("Cannot log in", loginMessage);
			}
		}
	}

	public void resetLoginAttempts() {
		loginAttempts = 0;
	}

	public void cancelLogin() {
		for (ILoginListener listener : loginListeners) {
			LoginEvent evt = new LoginEvent(listener, "", "", "Login cancelled");
			listener.loginFailed(evt);
		}
		loginFrame.closeForm();
	}

	public void addLoginListener(ILoginListener listener) {
		loginListeners.add(listener);
	}

	public void clearLoginListeners() {
		loginListeners = new ArrayList<ILoginListener>();
	}

	public int getLoginListenerCount() {
		return loginListeners.size();
	}
}
