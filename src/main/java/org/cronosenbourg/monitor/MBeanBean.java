/* 
 *   This file is part of CronosEnbourg, a JMX monitoring tools with Groovy.
 *
 *   CronosEnbourg is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   CronosEnbourg is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.cronosenbourg.monitor;

import java.util.HashMap;
import java.util.Map;

public class MBeanBean {
	private String name;
	private Map<String, String> attributes = new HashMap<String, String>();
	private Map<String,OperationBean> operations=new HashMap<String, OperationBean>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public Map<String, OperationBean> getOperations() {
		return operations;
	}

	public void setOperations(Map<String, OperationBean> operations) {
		this.operations = operations;
	}

}
