package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.processmining.framework.log.AuditTrailEntry;
import org.processmining.framework.log.LogFile;
import org.processmining.framework.log.ProcessInstance;
import org.processmining.framework.log.rfb.LogData;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.petrinetmining.PetriNetResult;
import org.xml.sax.SAXException;

import data.EventLog;
import data.Trace;

/**
 * Data Util
 * 
 * @author qinlongguo
 * 
 */
public class DataUtil {
	
	/**
	 * Get PetriNet from InputStream
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static PetriNet getPetriNetFromInputStream(InputStream is) throws IOException
	{				
		PnmlImport pnmlImport = new PnmlImport();
		PetriNetResult modelResult = (PetriNetResult) pnmlImport.importFile(is);
		PetriNet model = modelResult.getPetriNet();
		is.close();		
		return model;
	}
	
	/**
	 * Get PetriNet from FilePath
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static PetriNet getPetriNetFromFilePath(String filePath) throws IOException
	{
		FileInputStream fis = new  FileInputStream(filePath);
		PetriNet ret = getPetriNetFromInputStream(fis);
		return ret;
	}
	
	/**
	 * Get Event Log from FilePath
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static EventLog getEventLogFromFilePath(String filePath) throws ParserConfigurationException, SAXException, IOException 
	{
		EventLog ret = new EventLog();		
		
		LogFile logFile = LogFile.getInstance(filePath);
		LogData data = LogData.createInstance(logFile);
		
		for(ProcessInstance process:data.instances()){
			Trace trace = new Trace();
			for(AuditTrailEntry ate:process.getListOfATEs()){
				trace.addEvent(ate.getName());
			}
			ret.addTrace(trace);
		}
		
		return ret;
	}
	

}
