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

import java.net.ServerSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import javax.naming.CommunicationException;

import org.apache.log4j.Logger;
import org.jboss.jmx.adaptor.rmi.RMIAdaptor;

/**
 * @author nico
 * 
 */
public class InstanceThread extends Thread {
	private static DateFormat		dateFormat	= new SimpleDateFormat("HH:mm:ss");
	private Map<String, MBeanBean>	mbeans		= new HashMap<String, MBeanBean>();
	private Map<String, String>		resultMap	= new HashMap<String, String>();
	private List<IOutput>			outputList	= new ArrayList<IOutput>();
	private long					interval;
	private String					host;
	private int						port;
	private ServerSocket			serverSocket;

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Map<String, MBeanBean> getMbeans() {
		return mbeans;
	}

	public void setMbeans(Map<String, MBeanBean> mbeans) {
		this.mbeans = mbeans;
	}

	public MBeanBean getMBeanBean(String name) {
		return mbeans.get(name);
	}

	public Map<String, String> getResultMap() {
		return resultMap;
	}

	public void setResultMap(Map<String, String> resultMap) {
		this.resultMap = resultMap;
	}

	@Override
	public void run() {
		String loggerName = InstanceThread.class.getName().substring(InstanceThread.class.getName().lastIndexOf(".")+1)+ ">" + host + ":" + port;
		Logger log = Logger.getLogger( loggerName );
		boolean run = true;
		infiniteLoop:while (run) {
			resultMap.clear();
			try {
				RMIAdaptor adaptor = JMXServerAccess.getServer(host, port);
				String currentDate = dateFormat.format(new Date());
				resultMap.put("Time", currentDate);
				for (MBeanBean mBeanBean : mbeans.values()) {
					ObjectName objectName = new ObjectName(mBeanBean.getName());
					for (String attributeLibelle : mBeanBean.getAttributes().keySet()) {
						String result = "";
						String attributeName = mBeanBean.getAttributes().get(attributeLibelle);
						try {
							result = adaptor.getAttribute(objectName, attributeName).toString();
						} catch (AttributeNotFoundException e) {
							log.error("AttributeNotFoundException MBean: " + mBeanBean.getName() + " Attribute: " + attributeName);
							log.debug(e);
						} catch (InstanceNotFoundException e) {
							log.error("AttributeNotFoundException MBean: " + mBeanBean.getName() + " Attribute: " + attributeName);
							log.debug(e);
						} catch (Exception e) {
							log.error("Unexpected error", e);
						}
						resultMap.put(attributeLibelle, result);
					}
				}
				log.info("Data successfully grabed from JMX");
			} catch (CommunicationException e) {
				// Remote server is down,
				log.error("Communication with server failed");
			} catch (InstanceNotFoundException e) {
				// Remote server is down, just an other implementation...
				// may cause tracking problems later...
				log.error("Communication with server failed");
			} catch (Exception e) {
				log.error("Unexpected error", e);
			} finally {
				// writing results to outputs
				for (IOutput output : outputList) {
					output.writeRecord(resultMap);
					try {
						Thread.sleep(interval * 1000);
					} catch (InterruptedException e) {
						log.fatal("thread interruption Exception");
						break infiniteLoop;
					}
				}
			}
		}
	}

	public void setOutputList(List<IOutput> outputList) {
		this.outputList = outputList;
	}

	public List<IOutput> getOutputList() {
		return outputList;
	}

	public void addOutput(IOutput output) {
		this.outputList.add(output);
	}

}
