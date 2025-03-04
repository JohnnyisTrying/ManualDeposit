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

public enum EDataType {
	Date("Date"), MultiSelect("Multi-select"), Integer("Integer"), RealNumber(
			"Real Number"), Text("Text"), Boolean("Boolean"), ProvenanceNote(
			"Provenance Note");
	private final String _dataType;

	EDataType(String Type) {
		_dataType = Type;
	}

	public String type() {
		return _dataType;
	};

	public String toString() {
		return _dataType;
	};

}
