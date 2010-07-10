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

import org.apache.log4j.Logger;

public class Daemon extends Thread {

	private Logger log = Logger.getLogger(Daemon.class);
	/**
	 * Monitoring Daemon listen port
	 */
	private int listen = 3214;

	// private String iface = "127.0.0.1";
	/**
	 * Interval in minutes
	 */
	private int interval = 60;

	private Map<String, InstanceThread> instances;

	public Daemon() {
		super();
		instances = new HashMap<String, InstanceThread>();
	}

	public int getListen() {
		return listen;
	}

	public void setListen(int listen) {

		this.listen = listen;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public InstanceThread getInstanceThread(String name) {
		return instances.get(name);
	}

	public InstanceThread createInstanceThread(String name) {
		InstanceThread newInstance = new InstanceThread();
		instances.put(name, newInstance);
		return newInstance;
	}

	public Map<String, InstanceThread> getInstances() {
		return instances;
	}

	public void setInstances(Map<String, InstanceThread> instances) {
		this.instances = instances;
	}

	@Override
	public void run() {
		try {
			for (InstanceThread instance : instances.values()) {

				try {
					instance.start();
				} catch (Exception e) {
					log.fatal("Erreur sur l'instance " + instance.getHost() + ":" + instance.getPort(), e);
				}
			}
			log.info("Lancement terminé!");
		} catch (Exception e) {
			log.fatal("", e);
			System.exit(-1);
		}
	}

}
