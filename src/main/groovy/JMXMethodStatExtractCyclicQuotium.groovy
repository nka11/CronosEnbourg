/************************************************/
/* Version Groovy de la recuperation des info   */
/* de la console JMX 							*/
/*                                              */
/* Camille - Janvier 2010	                    */
/************************************************/

import java.util.regex.Pattern
import java.util.regex.Matcher
import java.lang.Exception
import java.text.SimpleDateFormat
import groovy.swing.SwingBuilder
import javax.management.MBeanInfo
import javax.management.MBeanOperationInfo
import javax.management.MBeanParameterInfo
import javax.management.ObjectName
import org.jboss.jmx.adaptor.rmi.RMIAdaptor
import javax.swing.WindowConstants as WC

// Vérification des paramètres
if (args.size() < 5) {
	println "Usage: <hostname> <port> <control port> <domain> <filename> <interval in seconds>"
	System.exit(-1)
}

// Appel à la fonction twiddle
jmxStat(args[0],args[1],args[2],args[3],args[4],args[5])

////////////////////////////////////////////////////////

def jmxStat(String hostname, String port,String controlPort, String sInterval,String domain, String filename) {
	// Control de disponibilite
	def control = JMXServerAccess.checkAvailability(controlPort.toInteger())
	//	 Initialisation des parametres d'affichage
	int interval = sInterval.toInteger()
	//	 Recuperation du serveur
	def server = JMXServerAccess.getServer(hostname,port)
	
	// Initialisation des parametres liés au MBean
	ObjectName name = new ObjectName("$domain")
	// Args
	String[] sig    = ["java.lang.String"];
	Object[] opArgs = [""];	
	String[] sig2    = [];
	Object[] opArgs2 = [];
	//println server.invoke(name,"resetStatistiques",opArgs2,sig2)
	while (true) {
		def fw
		try{
			//println server.invoke(name,"showPerformanceStatistics",opArgs,sig)
			stat = server.invoke(name,"showPerformanceStatisticsCsv",opArgs,sig)
			//println stat
			ligneTxt = new Scanner(stat).useDelimiter(/\n/)
			premLigne = ligneTxt.next()
			titre = new Scanner(premLigne).useDelimiter(/;/)
			i=0;
			def titreTab = []
			titre.each{titreTab[i++]=it}
			fw= new FileWriter(filename)
			while (ligneTxt.hasNext()) {
				j=1;
				value = new Scanner(ligneTxt.next()).useDelimiter(/;/)
				methodName = value.next()
				value.each{fw.write(methodName+"_"+titreTab[j++]+"="+it+"\n")}
			}
			println "PerfStats "+hostname+":"+port+" written in "+filename;
			// Itération suivante...
		}catch(Exception e){
			e.printStackTrace()
			fw.close()
			fw= new FileWriter(filename)
			try {
				server =JMXServerAccess.getServer(hostname,port)
				name = new ObjectName("$domain")
			} catch(Exception e1){e1.printStackTrace()}
		}finally {
			fw.close()
			// Itération suivante...
			sleep interval * 1000
		}
	}
}
