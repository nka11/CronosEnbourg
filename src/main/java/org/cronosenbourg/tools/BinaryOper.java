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
package org.cronosenbourg.tools;

import java.util.Map;

public class BinaryOper implements Operation {

	private String arg1;
	private String arg2;
	private String oper;
	private String propertyName;
	
	public BinaryOper(String propertyName, String arg1, String arg2, String oper) {
		super();
		this.propertyName = propertyName;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.oper = oper;
	}

	public String getResult(Map<String, String> record) {
		// TODO Auto-generated method stub
		
		Long val1 = Long.valueOf(record.get(this.arg1));
		Long val2 = Long.valueOf(record.get(this.arg2));
		Long  result = 0L;
		if (this.oper.equals("-")) {
			result = val1 - val2;
		}
		if (this.oper.equals("+")) {
			result = val1 + val2;
		}
		return result.toString();
	}

	public String getPropertyName() {
		return this.propertyName;
	}

}
