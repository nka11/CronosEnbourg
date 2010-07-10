/************************************************/
/* Version Groovy de la recuperation des info   */
/* de la console JMX 							*/
/*                                              */
/* Camille - Janvier 2010	                    */
/************************************************/
import org.jboss.jmx.adaptor.rmi.RMIAdaptor
import javax.naming.InitialContext
import javax.management.ObjectName
import javax.management.MBeanInfo
import javax.management.MBeanOperationInfo
import javax.management.MBeanParameterInfo

public class JMXServerAccess {
	public synchronized static ServerSocket checkAvailability(int port){
		print "Verification de la disponibilité sur le port :"+port
		def socketControl
		try{
			socketControl =new ServerSocket();
			socketControl.bind(new InetSocketAddress("127.0.0.1",port) );
		}catch (BindException e){
			println "... Le processus est deja lancé ! Abandon !";
			System.exit(-1);
		}
		println "...Disponibilite OK !";
		return socketControl;
		
	}
	
	public static RMIAdaptor getServer(String hostname,String port){
		// Initialisation des parametres liés au serveur
		Properties environment = new Properties()
		environment.setProperty("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory")
		environment.setProperty("java.naming.provider.url","jnp://$hostname:$port")
		environment.setProperty("java.naming.factory.url.pkgs","org.jboss.naming rg.jnp.interfaces")
		InitialContext initialContext = new InitialContext(environment)
		return (RMIAdaptor) initialContext.lookup("jmx/invoker/RMIAdaptor")
	}
	
	public static void displayOperationInformation(ObjectName name,RMIAdaptor server){
		// Initialisation des parametres liés au MBean
		MBeanInfo  info = server.getMBeanInfo(name)
		
		// Affichage des info du MBean
		println "JNDIView Class: " + info.getClassName()
		println "JNDIView Operations: "
		MBeanOperationInfo[] opInfo = info.getOperations()
		for(int o = 0; o < opInfo.length; o ++) {
			MBeanOperationInfo op = opInfo[o]
			String returnType = op.getReturnType()
			String opName     = op.getName()
			print " + " + returnType + " " + opName + "("
			MBeanParameterInfo[] params = op.getSignature()
			for(int p = 0; p < params.length; p++)  {
				MBeanParameterInfo paramInfo = params[p]
				String pname = paramInfo.getName()
				String type  = paramInfo.getType()
				if (pname.equals(type)) {
					print  type
				} else {
					print type + " " + pname
				}
				if (p < params.length-1) {
					print ',' 
				}
			}
			println ")"
		}
	}
}