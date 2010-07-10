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

import java.util.ArrayList;
import java.util.List;

public class OperationBean {
	private String name;
	private List<ArgumentBean> arguments = new ArrayList<ArgumentBean>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ArgumentBean> getArguments() {
		return arguments;
	}

	public void setArguments(List<ArgumentBean> arguments) {
		this.arguments = arguments;
	}

}
