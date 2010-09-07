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

import groovy.util.Node;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.cronosenbourg.tools.BinaryOper;
import org.cronosenbourg.tools.Operation;
import org.cronosenbourg.tools.SimpleItemOper;

/**
 * Class for a flat file output reinitialised at each measure.
 * 
 * @author karagni
 */
public class DefaultRawOutput implements IOutput {
	private Logger					log			= null;
	private String					fileName;
	private File					outFile;
	SortedMap<Integer, Operation>	params		= new TreeMap<Integer, Operation>();
	Node							nodeParams	= null;

	public void setParamNode(Node nodeParams) {
		this.nodeParams = nodeParams;
		this.fileName = (String) nodeParams.attribute("file");
		String loggerName = DefaultRawOutput.class.getName().substring(InstanceThread.class.getName().lastIndexOf(".")+1)+ ":" + this.fileName;
		log = Logger.getLogger(loggerName);
		for (Object param : nodeParams.children()) {
			Node nodeParam = (Node) param;
			if (nodeParam.name().toString().equals("item")) {
				Operation oper = new SimpleItemOper((String) nodeParam.attribute("propertyref"));
				params.put(Integer.parseInt((String) nodeParam.attribute("ind")), oper);
			}
			if (nodeParam.name().toString().equals("oper")) {
				Operation oper = new BinaryOper((String) nodeParam.attribute("property"),
						(String) nodeParam.attribute("arg1propertyref"),
						(String) nodeParam.attribute("arg2propertyref"), 
						(String) nodeParam.attribute("operator"));
				params.put(Integer.parseInt((String) nodeParam.attribute("ind")), oper);

			}
		}
		for (Map.Entry<Integer, Operation> entry : params.entrySet()) {
			System.out.println(entry.getKey() + "/" + entry.getValue().getPropertyName());
		}

	}

	public void writeRecord(Map<String, String> record) {
		try {
			// reinit output file for each write
			this.outFile = new File(this.fileName);
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.outFile));
	
			for (Map.Entry<Integer, Operation> entry : params.entrySet()) {
				try {
					String val = entry.getValue().getResult(record);
					if (val == null) val = "0";
					if (val == "") val = "0";
					writer.write(val);
				} catch (Exception e){
					writer.write("null");
				} finally {
					writer.newLine();
				}
			}
			writer.flush();
			writer.close();
			log.info("Records successfully writen as raw data (" + this.fileName + ")");
		} catch (IOException e) {
			log.error("I/O error while writing output" + this.fileName,e);
		}
	}

}
