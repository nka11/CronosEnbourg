import java.text.SimpleDateFormat
import org.jboss.jmx.adaptor.rmi.RMIAdaptor
import javax.management.ObjectName
import groovy.swing.SwingBuilder
import javax.swing.WindowConstants
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.chart.plot.PlotOrientation as Orientation

// Vérification des paramètres
if (args.size() < 7) {
	println "Usage: <hostname> <port> <context> <contextAjp> <control port> <interval in seconds> <number of iterations> <filename> <graph enable>"
    System.exit(-1)
}

// Appel à la fonction twiddle
stat(args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7])

////////////////////////////////////////////////////////

def stat(String hostname, String port,String context, String contextAjp, String controlPort, String sInterval, String sNumber, String filename, Boolean isGraphEnable) {
	println "Debut du monitoring de l'instance "+hostname+":"+port
	// Control de disponibilite
	def control = JMXServerAccess.checkAvailability(controlPort.toInteger())

	// Titres des colonnes
	println "Host		Time     	UsedMemory 	FreeMemory 	ActiveThreadCount 	ActiveSessions 	ExpiredSessions"

	int interval = sInterval.toInteger()
	int number = sNumber.toInteger()
	
	mySDF = new SimpleDateFormat("HH:mm:ss")
	
	def fw= new FileWriter(filename)
	def fw2= new FileWriter(filename+"_JMS")
	fw.write("time;usedMemory;totalMemory;activeThreadCount;activeSessions;expiredSessions;connectionCount;availableConnectionCount;inUseConnectionCount;maxConnectionsInUseCount;connectionCreatedCount;connectionDestroyedCount;stateString;currentAjpThreadCount;currentAjpThreadsBusy\n")
//	 Recuperation du serveur
	RMIAdaptor server =JMXServerAccess.getServer(hostname,port)
	ObjectName sessName = new ObjectName("jboss.web:type=Manager,path=/$context,host=localhost")
	ObjectName sysName = new ObjectName("jboss.system:type=ServerInfo")
	ObjectName jmsName = new ObjectName("jboss.mq:service=DestinationManager")
	//ObjectName poolName = new ObjectName("jboss.jca:name=ServicesSource,service=ManagedConnectionPool")
	ObjectName poolName = new ObjectName("bv.ds:name=bv_framework.BVRuntimeDBPool,service=ManagedConnectionPool")
	ObjectName ajpName = new ObjectName("jboss.web:name=$contextAjp,type=ThreadPool")
	String[] sig2    = [];
	Object[] opArgs2 = [];
	while (number > 0) {
		freeMemory = server.getAttribute(sysName, "FreeMemory")
		totalMemory = server.getAttribute(sysName, "TotalMemory")
		maxMemory = server.getAttribute(sysName, "MaxMemory")
		activeThreadCount = server.getAttribute(sysName, "ActiveThreadCount")
		usedMemory = totalMemory-freeMemory
		//Décomposition de la ligne résultat pour les sessions
		activeSessions = server.getAttribute(sessName, "activeSessions")
		expiredSessions = server.getAttribute(sessName, "expiredSessions")
		//Pool de connexion DB2
		connectionCount = server.getAttribute(poolName, "ConnectionCount")
		availableConnectionCount = server.getAttribute(poolName, "AvailableConnectionCount")
		inUseConnectionCount = server.getAttribute(poolName, "InUseConnectionCount")
		maxConnectionsInUseCount = server.getAttribute(poolName, "MaxConnectionsInUseCount")
		connectionCreatedCount = server.getAttribute(poolName, "ConnectionCreatedCount")
		connectionDestroyedCount = server.getAttribute(poolName, "ConnectionDestroyedCount")
		stateString  = server.getAttribute(poolName, "StateString")
		// AJP
		currentAjpThreadCount = server.getAttribute(ajpName, "currentThreadCount")
		currentAjpThreadsBusy = server.getAttribute(ajpName, "currentThreadsBusy")
		// Affichage de la ligne résultat avec l'heure en première colonne
		myDate = mySDF.format(new Date())
		println"$hostname:$port\t$myDate\t$usedMemory\t$freeMemory\t$activeThreadCount\t\t\t$activeSessions\t\t\t$expiredSessions\t$connectionCount\t$availableConnectionCount\t$inUseConnectionCount\t$maxConnectionsInUseCount\t$connectionCreatedCount\t$connectionDestroyedCount\t$stateString\t$currentAjpThreadCount\t$currentAjpThreadsBusy"
		//fw.write(myDate+";"+usedMemory+";"+totalMemory+";"+activeThreadCount+";"+activeSessions+";"+expiredSessions+";"+connectionCount+";"+availableConnectionCount+";"+inUseConnectionCount+";"+maxConnectionsInUseCount+";"+connectionCreatedCount+";"+connectionDestroyedCount+";"+stateString+"\n")
		fw.write(myDate+";"+usedMemory+";"+totalMemory+";"+activeThreadCount+";"+activeSessions+";"+expiredSessions+";"+connectionCount+";"+availableConnectionCount+";"+inUseConnectionCount+";"+maxConnectionsInUseCount+";"+connectionCreatedCount+";"+connectionDestroyedCount+";"+stateString+";"+currentAjpThreadCount+";"+currentAjpThreadsBusy+"\n")
		fw.flush()
		try{
			fw2.write(myDate+";"+server.invoke(jmsName,"listMessageCounter",opArgs2,sig2)+"\n");
			fw2.flush();
		}catch(Exception e){
			//e.printStackTrace();
		}
		// Itération suivante...
		sleep interval * 1000
		number--
	}
	fw.close()
	fw2.close()
	println "Stats "+hostname+":"+port+" written in "+filename;
}
