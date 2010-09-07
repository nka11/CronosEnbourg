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

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.tools.ant.taskdefs.Length;

public class OperationBean {
	Object[]							result		= null;
	private String						name		= null;
	private Map<Integer, ArgumentBean>	arguments	= new TreeMap<Integer, ArgumentBean>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<Integer, ArgumentBean> getArguments() {
		return arguments;
	}

	public void setArguments(Map<Integer, ArgumentBean> arguments) {
		this.arguments = arguments;
	}

	/**
	 * change string values of xml file to types for MBean operation scalar type
	 * and common object types are handled, and should work for many cases.
	 * TODO: maybe a plugin architecture to allow third party add-ons
	 * 
	 * @return
	 */
	public Object[] getArgsValues() {
		if (result == null) {

			int resultlen = arguments.size();
			result = new Object[resultlen];
			int i = 0;
			for (ArgumentBean arg : arguments.values()) {
				if (arg.getType().equals("boolean")) {
					result[i++] = Boolean.parseBoolean(arg.getValue());
				} else if (arg.getType().equals("int")) {
					result[i++] = Integer.parseInt(arg.getValue());
				} else if (arg.getType().equals("float")) {
					result[i++] = Float.parseFloat(arg.getValue());
				} else if (arg.getType().equals("long")) {
					result[i++] = Long.parseLong(arg.getValue());
				} else if (arg.getType().equals("double")) {
					result[i++] = Double.parseDouble(arg.getValue());
				} else if (arg.getType().equals("byte")) {
					result[i++] = Byte.parseByte(arg.getValue());
				} else if (arg.getType().equals("short")) {
					result[i++] = Short.parseShort(arg.getValue());
				} else if (arg.getType().equals("char")) {
					result[i++] = arg.getValue().charAt(0);
				} else if (arg.getType().equals("java.net.URL")) {
					try {
						result[i++] = new java.net.URL(arg.getValue());
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else { // Unmatched cases go here.
							// an attemp to cast string value from xml to
							// described type is done
					try {
						Class castclass = Class.forName(arg.getType());
						result[i++] = castclass.cast(arg.getValue());
					} catch (ClassNotFoundException e) {
						System.out.println("Type " + arg.getType() + " doesn't exist");
						System.out.println("Correct type name or program classloader");
						e.printStackTrace();
					} catch (ClassCastException e) {
						// If cast fails....
						System.out.println("Incorrect class cast, incompatible type");
						System.out.println("org.cronosenbourg.monitor.OperationBean implementation must to be improved");
						System.out.println("type " + arg.getType() + "not correctly handled.");
						System.out.println("Please send this message to CronosEnbourg bug tracker or correct implementation and send us back your patch");
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}

	public String[] getSignature() {
		int resultlen = arguments.size();
		String[] result = new String[resultlen];
		int i = 0;
		for (ArgumentBean arg : arguments.values()) {
			result[i++] = arg.getType();
		}
		return result;
	}

}
