import org.cronosenbourg.monitor.ArgumentBean 
import org.cronosenbourg.monitor.Daemon
import org.cronosenbourg.monitor.MBeanBean 
import org.cronosenbourg.monitor.OperationBean 

def confFileName
def main = new Daemon()

if (args.size() == 0) {
	confFileName = "CronosEnbourg.xml"
} else {
	confFileName = args[0]
	if (confFileName == null) {
		confFileName = "CronosEnbourg.xml"
	}
}
println "using conf " + confFileName
def cronosenbourg = new XmlParser().parse(confFileName);
def listen = cronosenbourg.listen;
def instances = cronosenbourg.instance;
instances.each {
	def instance = main.createInstanceThread(it.'@name')
	//instance.output=it.'@output'
	instance.interval=it.'@interval'.toInteger()
	instance.host=it.'@host'
	instance.port=it.'@port'.toInteger()
	it.output.each{
		//println it.getClass().getName()
		println it.'@className'
		def outputWriter = this.class.forName(it.'@className').newInstance()
		outputWriter.setParamNode(it)
		instance.addOutput(outputWriter)
	}
	
	def mbeans = it.mbean;
	mbeans.each {
		def mbeanbean = new MBeanBean();
		mbeanbean.name=it.'@name';
		it.attribute.each{
			mbeanbean.attributes.put(it.'@property',it.'@name')
		}
		it.operation.each{
			def operationBean = new OperationBean();
			operationBean.name=it.'@name';
			it.arg.each{
				def argumentBean = new ArgumentBean();
				argumentBean.type=it.'@type'
				argumentBean.value=it.'@value'
				operationBean.arguments.put(Integer.valueOf(it.'@pos'),argumentBean);
			}
			mbeanbean.operations.put(it.'@property',operationBean)
		}
		instance.mbeans.put(it.'@name',mbeanbean);
	}
}
//println main.instances
main.start()