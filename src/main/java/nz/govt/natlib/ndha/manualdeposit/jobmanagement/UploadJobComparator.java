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

package nz.govt.natlib.ndha.manualdeposit.jobmanagement;

import java.io.Serializable;
import java.util.Comparator;

import nz.govt.natlib.ndha.manualdeposit.metadata.PersonalSettings;

public class UploadJobComparator implements Comparator<UploadJob>, Serializable {

	/**
     *
     */
	private static final long serialVersionUID = 7860831924200766215L;
	private PersonalSettings personalSettings;

	public UploadJobComparator(PersonalSettings settings) {
		personalSettings = settings;
	}

	public int compare(UploadJob jobFrom, UploadJob jobTo) {
		int retVal = 0;
		if (jobFrom.getJobState() != jobTo.getJobState()) {
			retVal = jobFrom.getJobState().compareTo(jobTo.getJobState());
		} else {
			boolean sortAscending = false;
			switch (jobFrom.getJobState()) {
			case Requested:
			case Running:
				sortAscending = personalSettings.isSortRunningAscending();
				break;
			case Pending:
				sortAscending = personalSettings.isSortPendingAscending();
				break;
			case Cancelled:
			case Failed:
				sortAscending = personalSettings.isSortFailedAscending();
				break;
			case Deposited:
				sortAscending = personalSettings.isSortDepositedAscending();
				break;
			case AwaitingCleanup:
			case Complete:
				sortAscending = personalSettings.isSortCompleteAscending();
				break;
			default:
				break;
			}
			if (sortAscending) {
				retVal = jobFrom.getDepositDateTime().compareTo(
						jobTo.getDepositDateTime());
			} else {
				retVal = jobTo.getDepositDateTime().compareTo(
						jobFrom.getDepositDateTime());
			}
		}
		return retVal;
	}

}
