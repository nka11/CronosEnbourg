/*
 *   This file is part of CronosEnbourg.
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
/************************************************/
/* Version Groovy de la recuperation des info   */
/* de la console JMX 							*/
/*                                              */
/* Camille - Janvier 2010	                    */
/************************************************/
package org.cronosenbourg.monitor;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Properties;

import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ObjectName;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.jboss.jmx.adaptor.rmi.RMIAdaptor;

public class JMXServerAccess {
	private static Logger log = Logger.getLogger(JMXServerAccess.class);

	public synchronized static ServerSocket checkAvailability(int port) throws Exception {
		log.info("Verification de la disponibilité sur le port :" + port);
		ServerSocket socketControl = null;
		try {
			socketControl = new ServerSocket();
			socketControl.bind(new InetSocketAddress("127.0.0.1", port));
		} catch (Exception e) {
			log.fatal("... Le processus est deja lancé ! Abandon !");
			throw e;
		}
		log.info("...Disponibilite OK !");
		return socketControl;

	}

	public static RMIAdaptor getServer(String hostname, int port) throws Exception {
		// Initialisation des parametres liés au serveur
		Properties environment = new Properties();
		environment.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
		environment.setProperty("java.naming.provider.url", "jnp://" + hostname + ":" + port);
		environment.setProperty("java.naming.factory.url.pkgs", "org.jboss.naming rg.jnp.interfaces");
		InitialContext initialContext = new InitialContext(environment);
		return (RMIAdaptor) initialContext.lookup("jmx/invoker/RMIAdaptor");
	}

	public static void displayOperationInformation(ObjectName name, RMIAdaptor server) throws Exception {
		// Initialisation des parametres liés au MBean
		MBeanInfo info = server.getMBeanInfo(name);

		// Affichage des info du MBean
		System.out.println("JNDIView Class: " + info.getClassName());
		System.out.println("JNDIView Operations: ");
		MBeanOperationInfo[] opInfo = info.getOperations();
		for (int o = 0; o < opInfo.length; o++) {
			MBeanOperationInfo op = opInfo[o];
			String returnType = op.getReturnType();
			String opName = op.getName();
			System.out.print(" + " + returnType + " " + opName + "(");
			MBeanParameterInfo[] params = op.getSignature();
			for (int p = 0; p < params.length; p++) {
				MBeanParameterInfo paramInfo = params[p];
				String pname = paramInfo.getName();
				String type = paramInfo.getType();
				if (pname.equals(type)) {
					System.out.print(type);
				} else {
					System.out.print(type + " " + pname);
				}
				if (p < params.length - 1) {
					System.out.print(',');
				}
			}
			System.out.println(")");
		}
	}
}