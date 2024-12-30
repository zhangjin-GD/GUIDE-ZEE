package guide.iface.sap.webservice;


import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;


public class JXOfCurrency {
	
	public static HearBeanOfCurrency returnSap(String soap) throws Exception{
    	MessageFactory msgFactory = MessageFactory.newInstance();
    	SOAPMessage reqMsg = msgFactory.createMessage(new MimeHeaders(), new ByteArrayInputStream(soap.getBytes("UTF-8")));
    	reqMsg.saveChanges();
    	SOAPBody body = reqMsg.getSOAPBody();
        Iterator<SOAPElement> iterator = body.getChildElements();
        HearBeanOfCurrency sb =parse(iterator);
        return sb;
	}
 
    private static HearBeanOfCurrency parse(Iterator<SOAPElement> iterator) {
    	HearBeanOfCurrency hearBean = new HearBeanOfCurrency();
    	while (iterator.hasNext()) {
    		SOAPElement element = iterator.next();
    		Iterator<SOAPElement> it1 = element.getChildElements();
    		SOAPElement element1 = it1.next();
    		Iterator<SOAPElement> it2 = element1.getChildElements();
    		SOAPElement element2 = it2.next();
    		Iterator<SOAPElement> it3 = element2.getChildElements();
    		SOAPElement elChild = null;
    		while (it3.hasNext()) {
    			elChild = it3.next();
    			if("PACKGID".equals(elChild.getLocalName())) {
    				if(elChild.getLocalName()==null) {
    					hearBean.setPACKGID("");
    				}
					hearBean.setPACKGID(elChild.getValue());
				}
				if("ZSTATUS".equals(elChild.getLocalName())) {
					hearBean.setZSTATUS(elChild.getValue());
				}
				if("ITEMID".equals(elChild.getLocalName())) {
					if(elChild.getLocalName()==null) {
    					hearBean.setITEMID("");
    				}
					hearBean.setITEMID(elChild.getValue());
				}
				if("KURST".equals(elChild.getLocalName())) {
					hearBean.setKURST(elChild.getValue());
				}
				if("GDATU".equals(elChild.getLocalName())) {
					hearBean.setGDATU(elChild.getValue());
				}
				if("FCURR".equals(elChild.getLocalName())) {
					hearBean.setFCURR(elChild.getValue());
				}
				if("TCURR".equals(elChild.getLocalName())) {
					hearBean.setTCURR(elChild.getValue());
				}
				if("UKURS".equals(elChild.getLocalName())) {
					hearBean.setUKURS(elChild.getValue());
				}
				if("ZPROMSG".equals(elChild.getLocalName())) {
					hearBean.setZPROMSG(elChild.getValue());
				}
    		}
    	}
    	return hearBean;
    }
}

