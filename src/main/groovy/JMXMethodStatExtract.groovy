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
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.chart.plot.PlotOrientation as Orientation
import groovy.swing.SwingBuilder
import javax.management.MBeanInfo
import javax.management.MBeanOperationInfo
import javax.management.MBeanParameterInfo
import javax.management.ObjectName
import javax.naming.InitialContext
import org.jboss.jmx.adaptor.rmi.RMIAdaptor
import javax.swing.WindowConstants as WC

// Vérification des paramètres
if (args.size() < 5) {
	println "Usage: <hostname> <port> <control port> <domain> <filename>"
	System.exit(-1)
}

// Appel à la fonction twiddle
jmxStat(args[0],args[1],args[2],args[3],args[4])

////////////////////////////////////////////////////////

def jmxStat(String hostname, String port,String controlPort, String domain, String filename) {
	// Control de disponibilite
	def control = JMXServerAccess.checkAvailability(controlPort.toInteger())
	//	 Recuperation du serveur
	RMIAdaptor server =JMXServerAccess.getServer(hostname,port)
	// Initialisation des parametres liés au MBean
	ObjectName name = new ObjectName("$domain")
	MBeanInfo  info = server.getMBeanInfo(name)
//	JMXServerAccess.displayOperationInformation(name,server)
	// Agrs
	String[] sig    = ["java.lang.String"];
	String[] sig2    = [];
	Object[] opArgs = [""];
	Object[] opArgs2 = [];
//	println server.invoke(name,"resetStatistiques",opArgs2,sig2)
//	println server.invoke(name,"showPerformanceStatistics",opArgs,sig)
	stat = server.invoke(name,"showPerformanceStatisticsCsv",opArgs,sig)
	//println stat
	ligneTxt = new Scanner(stat).useDelimiter(/\n/)
	premLigne = ligneTxt.next()
	titre = new Scanner(premLigne).useDelimiter(/;/)
	i=0;
	def titreTab = []
	titre.each{titreTab[i++]=it}
	def fw= new FileWriter(filename)
	while (ligneTxt.hasNext()) {
		j=1;
		value = new Scanner(ligneTxt.next()).useDelimiter(/;/)
		methodName = value.next()
		value.each{fw.write(methodName+"_"+titreTab[j++]+"="+it+"\n")}
	}
	fw.close()
	println "PerfStats "+hostname+":"+port+" written in "+filename
}
