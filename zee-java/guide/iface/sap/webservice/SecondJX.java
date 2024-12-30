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


public class SecondJX {
	
	public static SecondHearBean returnSap(String soap) throws Exception{
		//String soap = "<SOAP:Envelope xmlns:SOAP='http://schemas.xmlsoap.org/soap/envelope/'><SOAP:Header/><SOAP:Body><ns1:MT_stock_return_response xmlns:ns1='urn:cp.cosco.com/eam/2020'><ET_HEAR_RETURN><ZSOURCE>650</ZSOURCE><BUKRS>23K9</BUKRS><ZSTOCKNO>JI201607000015</ZSTOCKNO><BUDAT>2016-07-11</BUDAT><ZDATE1>2016-07-11</ZDATE1><LIFNR>1000071512</LIFNR><ZTRAN>207</ZTRAN><ZRETURN_CODE>1</ZRETURN_CODE><ZHEADMSG>错误： 源系统和公司代码与SAP端配置不匹配,行项目存在错误</ZHEADMSG><BELNR/><GJAHR>0000</GJAHR></ET_HEAR_RETURN><ET_ITEM_RETURN><item><ZSTOCKNO>JI201607000015</ZSTOCKNO><ZSTOCKITEMNO>001</ZSTOCKITEMNO><ZRETURN_CODE>1</ZRETURN_CODE><ZITEMMSG>错误： 科目配置表中公司代码：23K9业务类型：207物料类型：1005数据未配置,存在重复的出入库单项目号,出库业务不能填写税码,此公司代码下的费用供应商不存在,内部订单不存在或非本公司内部订单</ZITEMMSG></item><item><ZSTOCKNO>JI201607000015</ZSTOCKNO><ZSTOCKITEMNO>001</ZSTOCKITEMNO><ZRETURN_CODE>1</ZRETURN_CODE><ZITEMMSG>错误： 科目配置表中公司代码：23K9业务类型：207物料类型：1005数据未配置,出库业务不能填写税码,此公司代码下的费用供应商不存在,内部订单不存在或非本公司内部订单</ZITEMMSG></item></ET_ITEM_RETURN></ns1:MT_stock_return_response></SOAP:Body></SOAP:Envelope>";
    	MessageFactory msgFactory = MessageFactory.newInstance();
    	SOAPMessage reqMsg = msgFactory.createMessage(new MimeHeaders(), new ByteArrayInputStream(soap.getBytes("UTF-8")));
    	reqMsg.saveChanges();
    	SOAPBody body = reqMsg.getSOAPBody();
        Iterator<SOAPElement> iterator = body.getChildElements();
        SecondSapBean secondSapBean = parse(iterator);
        SecondHearBean secondHearBean=  secondSapBean.getSecondHearBean();
        String msg1 = secondHearBean.getZHEADMSG();
        List<SecondItemBean> secondItemBeans = secondSapBean.getSecondItemBeans();
        String msg = msg1.toLowerCase();
        if(msg != null && !msg.contains("success")){
	        for(SecondItemBean item :secondItemBeans){
	        	msg+=item.getZITEMMSG();
	        }
	        secondHearBean.setZHEADMSG(msg);
        }
        return secondHearBean;
	}
 
    private static SecondSapBean parse(Iterator<SOAPElement> iterator) {
    	SecondSapBean secondSapBean = new SecondSapBean();
    	List<SecondItemBean> secondItemBeans = new ArrayList<SecondItemBean>();
    	SecondHearBean secondHearBean = new SecondHearBean();
    	while (iterator.hasNext()) {
    		SOAPElement element = iterator.next();
    		if ("ns1:MT_pur_inv_response".equals(element.getNodeName())) {
    			Iterator<SOAPElement> it = element.getChildElements();
    			SOAPElement el = null;
    			while (it.hasNext()) {
    				el = it.next();
    				if ("ET_HEAD_RETURN".equals(el.getLocalName())) {
    					Iterator<SOAPElement> itChild = el.getChildElements();
    					SOAPElement elChild = null;
    					while (itChild.hasNext()) {
    						elChild = itChild.next();
    						if("ZSOURCE".equals(elChild.getLocalName())) {
    							secondHearBean.setZSOURCE(elChild.getValue());
    						}
    						if("BUKRS".equals(elChild.getLocalName())) {
    							secondHearBean.setBUKRS(elChild.getValue());
    						}
    						if("UNIQUEID".equals(elChild.getLocalName())) {
    							secondHearBean.setUNIQUEID(elChild.getValue());
    						}
    						if("ZTYPE".equals(elChild.getLocalName())) {
    							secondHearBean.setZTYPE(elChild.getValue());
    						}
    						if("DOCNUMBER".equals(elChild.getLocalName())) {
    							secondHearBean.setDOCNUMBER(elChild.getValue());
    						}
    						if("POSTDATE".equals(elChild.getLocalName())) {
    							secondHearBean.setPOSTDATE(elChild.getValue());
    						}
    						if("ZRETURN_CODE".equals(elChild.getLocalName())) {
    							secondHearBean.setZRETURN_CODE(elChild.getValue());
    						}
    						
    						if("ZHEADMSG".equals(elChild.getLocalName())) {
    							secondHearBean.setZHEADMSG(elChild.getValue());
    						}
    					}
    				}
    				if("ET_ITEM_RETURN".equals(el.getLocalName())) {
    					Iterator<SOAPElement> itChild = el.getChildElements();
    					SOAPElement elChild = null;
    					while (itChild.hasNext()) {
    						SecondItemBean secondItemBean = new SecondItemBean();
    						elChild = itChild.next();
    						if("item".equals(elChild.getLocalName())) {
    							Iterator<SOAPElement> itGrandson = elChild.getChildElements();
    							SOAPElement elGrandson = null;
    							while (itGrandson.hasNext()) {
    								elGrandson = itGrandson.next();
    								if("BUKRS".equals(elGrandson.getLocalName())) {
    									secondItemBean.setBUKRS(elGrandson.getValue());
    								}
    								if("UNIQUEID".equals(elGrandson.getLocalName())) {
    									secondItemBean.setUNIQUEID(elGrandson.getValue());
    								}
    								if("CONCEPTLINE".equals(elGrandson.getLocalName())) {
    									secondItemBean.setCONCEPTLINE(elGrandson.getValue());
    								}
    								if("ZRETURN_CODE".equals(elGrandson.getLocalName())) {
    									secondItemBean.setZRETURN_CODE(elGrandson.getValue());
    								}
    								if("ZITEMMSG".equals(elGrandson.getLocalName())) {
    									secondItemBean.setZITEMMSG(elGrandson.getValue());
    								}
    							}
    						}
    						secondItemBeans.add(secondItemBean);
    					}
    				}
    			}
    		}
    		secondSapBean.setSecondHearBean(secondHearBean);
    		secondSapBean.setSecondItemBeans(secondItemBeans);
    	}
    	return secondSapBean;
    }
}
