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


public class JX {
	
	public static HearBean returnSap(String soap) throws Exception{
    	MessageFactory msgFactory = MessageFactory.newInstance();
    	SOAPMessage reqMsg = msgFactory.createMessage(new MimeHeaders(), new ByteArrayInputStream(soap.getBytes("UTF-8")));
    	reqMsg.saveChanges();
    	SOAPBody body = reqMsg.getSOAPBody();
        Iterator<SOAPElement> iterator = body.getChildElements();
        SapBean sb =parse(iterator);
        HearBean hearBean =sb.getHearBean();
        String msg = hearBean.getZHEADMSG();
        List<ItemBean> itemBeans =  sb.getItemBeans();
        if(msg != null && !msg.equalsIgnoreCase("Success")){
	        for(ItemBean item :itemBeans){
	        	msg+=item.getZITEMMSG();
	        }
	        hearBean.setZHEADMSG(msg);
        }
        return hearBean;
	}
 
    private static SapBean parse(Iterator<SOAPElement> iterator) {
    	SapBean sapBean = new SapBean();
    	List<ItemBean> itemBeans = new ArrayList<ItemBean>();
    	HearBean hearBean = new HearBean();
    	while (iterator.hasNext()) {
    		SOAPElement element = iterator.next();
    		if ("ns1:MT_stock_return_response".equals(element.getNodeName())) {
    			Iterator<SOAPElement> it = element.getChildElements();
    			SOAPElement el = null;
    			while (it.hasNext()) {
    				el = it.next();
    				if ("ET_HEAR_RETURN".equals(el.getLocalName())) {
    					Iterator<SOAPElement> itChild = el.getChildElements();
    					SOAPElement elChild = null;
    					while (itChild.hasNext()) {
    						elChild = itChild.next();
    						if("ZSOURCE".equals(elChild.getLocalName())) {
    							hearBean.setZSOURCE(elChild.getValue());
    						}
    						if("BUKRS".equals(elChild.getLocalName())) {
    							hearBean.setBUKRS(elChild.getValue());
    						}
    						if("ZSTOCKNO".equals(elChild.getLocalName())) {
    							hearBean.setZSTOCKNO(elChild.getValue());
    						}
    						if("BUDAT".equals(elChild.getLocalName())) {
    							hearBean.setBUDAT(elChild.getValue());
    						}
    						if("ZDATE1".equals(elChild.getLocalName())) {
    							hearBean.setZDATE1(elChild.getValue());
    						}
    						if("LIFNR".equals(elChild.getLocalName())) {
    							hearBean.setLIFNR(elChild.getValue());
    						}
    						if("ZRETURN_CODE".equals(elChild.getLocalName())) {
    							hearBean.setZRETURN_CODE(elChild.getValue());
    						}
    						
    						if("ZHEADMSG".equals(elChild.getLocalName())) {
    							hearBean.setZHEADMSG(elChild.getValue());
    						}
    						if("BELNR".equals(elChild.getLocalName())) {
    							hearBean.setBELNR(elChild.getValue());
    						}
    						
    					}
    				}
    				if("ET_ITEM_RETURN".equals(el.getLocalName())) {
    					Iterator<SOAPElement> itChild = el.getChildElements();
    					SOAPElement elChild = null;
    					while (itChild.hasNext()) {
    						ItemBean itemBean = new ItemBean();
    						elChild = itChild.next();
    						if("item".equals(elChild.getLocalName())) {
    							Iterator<SOAPElement> itGrandson = elChild.getChildElements();
    							SOAPElement elGrandson = null;
    							while (itGrandson.hasNext()) {
    								elGrandson = itGrandson.next();
    								if("ZSTOCKNO".equals(elGrandson.getLocalName())) {
    									itemBean.setZSTOCKNO(elGrandson.getValue());
    								}
    								if("ZSTOCKITEMNO".equals(elGrandson.getLocalName())) {
    									itemBean.setZSTOCKITEMNO(elGrandson.getValue());
    								}
    								if("ZRETURN_CODE".equals(elGrandson.getLocalName())) {
    									itemBean.setZRETURN_CODE(elGrandson.getValue());
    								}
    								if("ZITEMMSG".equals(elGrandson.getLocalName())) {
    									itemBean.setZITEMMSG(elGrandson.getValue());
    								}
    							}
    						}
    						itemBeans.add(itemBean);
    					}
    				}
    			}
    		}
    		sapBean.setHearBean(hearBean);
    		sapBean.setItemBeans(itemBeans);
    	}
    	return sapBean;
    }
}
