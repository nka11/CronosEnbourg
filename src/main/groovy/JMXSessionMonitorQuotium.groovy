/************************************************/
/* Version Groovy du script twiddlestat.ksh de  */
/* Patrick, développée afin de pouvoir utiliser */
/* cette commande dans le planificateur de      */
/* tâches de Windows                            */
/*                                              */
/************************************************/

import java.text.SimpleDateFormat
import org.jboss.jmx.adaptor.rmi.RMIAdaptor
import javax.management.ObjectName

// Vérification des paramètres
if (args.size() < 8) {
	println "Usage: <hostname> <port> <context> <context ajp> <datasourceName> <control port> <filename> <interval>"
    System.exit(-1)
}

// Appel à la fonction twiddle
stat(args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7])

////////////////////////////////////////////////////////

def stat(String hostname, String port,String context,String contextAjp,String dataSourceName, String controlPort, String filename, String sInterval) {
	println "Debut du monitoring de l'instance "+hostname+":"+port
	// Control de disponibilite
	def control = JMXServerAccess.checkAvailability(controlPort.toInteger())

	int interval = sInterval.toInteger()
	
	//	 Recuperation du serveur
	RMIAdaptor server =JMXServerAccess.getServer(hostname,port)
	ObjectName sessName = new ObjectName("jboss.web:type=Manager,path=/$context,host=localhost")
	ObjectName sysName = new ObjectName("jboss.system:type=ServerInfo")
	ObjectName ajpName = new ObjectName("jboss.web:name=$contextAjp,type=ThreadPool")
	ObjectName connectionPoolName
	if (null!=dataSourceName) {
		connectionPoolName = new ObjectName("jboss.jca:service=ManagedConnectionPool,name="+dataSourceName)
	}
	while(true){
		def fw
		try{
			fw= new FileWriter(filename)
			freeMemory = server.getAttribute(sysName, "FreeMemory")
			totalMemory = server.getAttribute(sysName, "TotalMemory")
			usedMemory = totalMemory-freeMemory
			fw.write("usedMemory="+usedMemory+"\n")
			fw.write("totalMemory="+totalMemory+"\n")
			fw.write("maxMemory="+server.getAttribute(sysName, "MaxMemory")+"\n")
			fw.write("activeThreadCount="+server.getAttribute(sysName, "ActiveThreadCount")+"\n")
			fw.write("activeSessions="+server.getAttribute(sessName, "activeSessions")+"\n")
			fw.write("expiredSessions="+server.getAttribute(sessName, "expiredSessions")+"\n")
			fw.write("Ajp_currentThreadCount="+server.getAttribute(ajpName, "currentThreadCount")+"\n")
			fw.write("Ajp_currentThreadsBusy="+server.getAttribute(ajpName, "currentThreadsBusy")+"\n")
			if (null!=dataSourceName && !"".equals(dataSourceName)) {
				fw.write("ManagedConnectionPool_InUseConnectionCount="+server.getAttribute(connectionPoolName,"InUseConnectionCount")+"\n")
				fw.write("ManagedConnectionPool_ConnectionCreatedCount="+server.getAttribute(connectionPoolName,"ConnectionCreatedCount")+"\n")
				fw.write("ManagedConnectionPool_ConnectionDestroyedCount="+server.getAttribute(connectionPoolName,"ConnectionDestroyedCount")+"\n")
				fw.write("ManagedConnectionPool_MaxConnectionsInUseCount="+server.getAttribute(connectionPoolName,"MaxConnectionsInUseCount")+"\n")
				fw.write("ManagedConnectionPool_MinSize="+server.getAttribute(connectionPoolName,"MinSize")+"\n")
				fw.write("ManagedConnectionPool_MaxSize="+server.getAttribute(connectionPoolName,"MaxSize")+"\n")
			}
			fw.flush()
			println "Stats "+hostname+":"+port+" written in "+filename;
		}catch(Exception e){
			e.printStackTrace()
			fw.close()
			fw= new FileWriter(filename)
			fw.write("usedMemory=0\n")
			fw.write("totalMemory=0\n")
			fw.write("maxMemory=0\n")
			fw.write("activeThreadCount=0\n")
			fw.write("activeSessions=0\n")
			fw.write("expiredSessions=0\n")
			fw.write("Ajp_currentThreadCount=0\n")
			fw.write("Ajp_currentThreadsBusy=0\n")
			if (null!=dataSourceName && !"".equals(dataSourceName)) {
				fw.write("ManagedConnectionPool_InUseConnectionCount=0\n")
				fw.write("ManagedConnectionPool_ConnectionCreatedCount=0\n")
				fw.write("ManagedConnectionPool_ConnectionDestroyedCount=0\n")
				fw.write("ManagedConnectionPool_MaxConnectionsInUseCount=0\n")
				fw.write("ManagedConnectionPool_MinSize=0\n")
				fw.write("ManagedConnectionPool_MaxSize=0\n")
			}
			println "Stats "+hostname+" with exception :"+port+" written in "+filename;
			try {
				server =JMXServerAccess.getServer(hostname,port)
				sessName = new ObjectName("jboss.web:type=Manager,path=/$context,host=localhost")
				sysName = new ObjectName("jboss.system:type=ServerInfo")
				ajpName = new ObjectName("jboss.web:name=$contextAjp,type=ThreadPool")
				if (null!=dataSourceName) {
					connectionPoolName = new ObjectName("jboss.jca:service=ManagedConnectionPool,name="+dataSourceName)
				}
			} catch(Exception e1){e1.printStackTrace()}
		}finally {
			fw.close()
			// Itération suivante...
			sleep interval * 1000
		}
	}
}
