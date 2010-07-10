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
import org.jboss.jmx.adaptor.rmi.RMIAdaptor
import javax.swing.WindowConstants as WC

// Vérification des paramètres
if (args.size() < 6) {
	println "Usage: <hostname> <port> <control port> <interval in seconds> <number of iterations> <domain> <attribute>"
	System.exit(-1)
}

// Appel à la fonction twiddle
jmxStat(args[0],args[1],args[2],args[3],args[4],args[5],args[6])

////////////////////////////////////////////////////////

def jmxStat(String hostname, String port,String controlPort, String sInterval, String sNumber,String domain, String attribute) {
	// Control de disponibilite
	def control = JMXServerAccess.checkAvailability(controlPort.toInteger())
	// Initialisation des parametres d'affichage
	int interval = sInterval.toInteger()
	int number = sNumber.toInteger()
	mySDF = new SimpleDateFormat("HH:mm:ss")
	
	// Initialisation de la fenetre de graphique 
	def dataset = new DefaultCategoryDataset()
	def labels = ["$attribute", "Time", "Value"]
	def options = [true, true, true]
	def chart = ChartFactory.createLineChart(*labels, dataset, Orientation.VERTICAL, *options)
	def swing = new SwingBuilder()
	def frame = swing.frame(title:'Groovy LineChart',
			defaultCloseOperation:WC.EXIT_ON_CLOSE) { panel(id:'canvas') { widget(new ChartPanel(chart)) } }
	
	//	 Recuperation du serveur
	RMIAdaptor server =JMXServerAccess.getServer(hostname,port)
	
	// Initialisation des parametres liés au MBean
	ObjectName name = new ObjectName("$domain")
	MBeanInfo  info = server.getMBeanInfo(name)
	
	// Affichage des info du MBean
	JMXServerAccess.displayOperationInformation(name,server)
	// Affichage des valeurs
	try{
		// Titres des colonnes
		println "Time     $attribute Value"
		while (number > 0) {
			monitored = server.getAttribute(name, "$attribute")
			myDate = mySDF.format(new Date())
			println "$myDate $attribute $monitored"
			try {
				dataset.addValue monitored, "Monitored", "$myDate"
				frame.pack()
				frame.show()
			}catch (Exception e){
			}
			// Itération suivante...
			sleep interval * 1000
			number--
		}
	}catch(Exception e){
		println "Probleme avec l'attribut $attribute :"+ e.getMessage()
	}
	println "Le processus s'est termine avec succes !"
	script = null;
}
