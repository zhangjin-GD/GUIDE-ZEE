package guide.iface.mdm.webservice;

import org.apache.commons.lang3.StringUtils;

import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.util.*;

public class SoapUtil {

    /**
     * @throws DocumentException
     */
    public static ReturnBean returnSap(String vendorNum, String updateTime) throws  Exception {
        try {
            String soap = getSoap(vendorNum, updateTime);
            String REG = "((?=vENDOR_CODE))";
            String str = "";
            SOAPMessage msg = formatSoapString(soap);
            SOAPBody body = msg.getSOAPBody();
            Iterator<SOAPElement> iterator = body.getChildElements();
            List<String> arrayList = new ArrayList<>();
            PrintBody(iterator, arrayList);
            for (String s : arrayList) {
                str += s + "~";
            }
            String[] array = str.split(REG);
            List<String> list = Arrays.asList(array);
            String s1 = list.get(0).substring(0, list.get(0).length() - 1);
            ReturnBean returnBean = new ReturnBean();
            String[] split = s1.split("~");
            Map<String, Object> headMap = new HashMap<>();
            for (String s : split) {
                headMap.put(s.split(":")[0], s.split(":")[1]);
            }
            returnBean.setMap(headMap);
            List<Map<String, Object>> listMap = new ArrayList<>();
            HashMap<String, Object> venMap = new HashMap<>();

            for (int i = 1; i < list.size(); i++) {
                //第一个供应商字符串,去掉最后一个符号
                String ven = list.get(i).substring(0, list.get(i).length() - 1);
                //根据~切分成多个属性
                String[] split1 = ven.split("~");
                //遍历属性，然后切分为 key 和value
                for (String s : split1) {
                    venMap.put(s.split(":")[0], s.split(":")[1]);
                }
                listMap.add(venMap);
            }
            returnBean.setList(listMap);
           return  returnBean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 把soap字符串格式化为SOAPMessage
     *
     * @param soapString
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static SOAPMessage formatSoapString(String soapString) {
        MessageFactory msgFactory;
        try {
            msgFactory = MessageFactory.newInstance();
            SOAPMessage reqMsg = msgFactory.createMessage(new MimeHeaders(),
                    new ByteArrayInputStream(soapString.getBytes("UTF-8")));
            reqMsg.saveChanges();
            return reqMsg;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<String> PrintBody(Iterator<SOAPElement> iterator, List<String> arrayList) {

        while (iterator.hasNext()) {
            Object o = iterator.next();
            if (o != null) {
                SOAPElement element = null;
                try {
                    element = (SOAPElement) o;
                    if ("displayValue".equals(element.getNodeName().split(":")[1])) {
                        String nodeName = element.getParentElement().getNodeName().split(":")[1];
                        arrayList.add(nodeName + ":" + element.getValue());
                    } else {
                        if (StringUtils.isNotEmpty(element.getValue().trim())) {
                            String str = element.getNodeName().split(":")[1] + ":" + element.getValue();
                            arrayList.add(str);
                        }

                    }
                } catch (Exception e) {
                }
                if (element != null) {
                    PrintBody(element.getChildElements(), arrayList);
                }
            }
        }
        return arrayList;
    }


    public static  String  getSoap(String vendorNum,String updateTime){
        String soap="";
        if (StringUtils.isNotBlank(updateTime)){
             soap ="<?xml version='1.0' encoding='utf-8'?>" 
            		 +"<SOAP-ENV:Envelope xmlns:SOAP-ENV='http://schemas.xmlsoap.org/soap/envelope/' xmlns:xs='http://www.w3.org/2001/XMLSchema' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" 
            		 +"  <SOAP-ENV:Header>" 
            		 +"    <WSCorIDSOAPHeader xmlns='http://www.wilytech.com/' CorID='4F00A93FAC1108425117DD6787639B8C,1:1,0,0,,,AgAAALZIQgAAAAFGAAAAAQAAABFqYXZhLnV0aWwuSGFzaE1hcAAAAAJIQgAAAAJGAAAAAgAAABBqYXZhLmxhbmcuU3RyaW5nAA9DYWxsZXJUaW1lc3RhbXBIQgAAAANFAAAAAgANMTY1MDU5Mjg4NTA1NUhCAAAABEUAAAACAApUeG5UcmFjZUlkSEIAAAAFRQAAAAIAJDRGMDAzQTIxQUMxMTA4NDI1MTE3REQ2NzVGRjE0RDIxMzQ3MQ=='/>" 
            		 +"  </SOAP-ENV:Header>" 
            		 +"  <SOAP-ENV:Body>" 
            		 +"    <pns:searchVENDOR xmlns:pns='urn:VendorWSVi'>" 
            		 +"      <yq1:query xmlns:yq1='urn:VendorWSVi' xmlns:pns='urn:com.sap.mdm.ws.beans.vendorws'>" 
            		 +"        <pns:criteria>" 
            		 +"          <yq2:uPDATE_DATE_MDM xmlns:yq2='urn:com.sap.mdm.ws.beans.vendorws' xmlns:pns='urn:com.sap.mdm.ws.beans'>" 
            		 +"            <pns:constraint>" 
            		 +"              <pns:expressionOperator>GREATER_OR_EQUAL</pns:expressionOperator>" 
            		 +"              <pns:value>"+ updateTime+"T00:00:00.175+08:00</pns:value>" 
            		 +"            </pns:constraint>" 
            		 +"            <pns:constraint>" 
            		 +"              <pns:expressionOperator>LESS_OR_EQUAL</pns:expressionOperator>" 
            		 +"              <pns:value>"+updateTime +"T23:59:59.292+08:00</pns:value>" 
            		 +"            </pns:constraint>"  
            		 +"          </yq2:uPDATE_DATE_MDM>" 
            		 +"        </pns:criteria>" 
            		 +"      </yq1:query>" 
            		 +"      <yq3:reposInfo xmlns:yq3='urn:VendorWSVi' xmlns:pns='urn:com.sap.mdm.core.beans'>" 
            		 +"        <pns:destinationName>COSCOCS_MDM_VENDOR</pns:destinationName>" 
            		 +"        <pns:repositoryName>VENDOR</pns:repositoryName>" 
            		 +"        <pns:serverName>172.17.8.78</pns:serverName>" 
            		 +"      </yq3:reposInfo>" 
            		 +"    </pns:searchVENDOR>" 
            		 +"  </SOAP-ENV:Body>" 
            		 +"</SOAP-ENV:Envelope>";
        }
        if (StringUtils.isNotBlank(vendorNum)){
            soap="<?xml version='1.0' encoding='utf-8'?>"
                    +"<SOAP-ENV:Envelope xmlns:SOAP-ENV='http://schemas.xmlsoap.org/soap/envelope/' xmlns:xs='http://www.w3.org/2001/XMLSchema' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>"
                    +"  <SOAP-ENV:Header>"
                    +"    <WSCorIDSOAPHeader xmlns='http://www.wilytech.com/' CorID='550FDA68AC1108425117DD670CCE6703,1:1,0,0,,,AgAAALZIQgAAAAFGAAAAAQAAABFqYXZhLnV0aWwuSGFzaE1hcAAAAAJIQgAAAAJGAAAAAgAAABBqYXZhLmxhbmcuU3RyaW5nAA9DYWxsZXJUaW1lc3RhbXBIQgAAAANFAAAAAgANMTYzNzgwOTY0MjA4OEhCAAAABEUAAAACAApUeG5UcmFjZUlkSEIAAAAFRQAAAAIAJDU1MENFQjk2QUMxMTA4NDI1MTE3REQ2N0RCNzBFQTlDMzgwMw=='/>"
                    +"  </SOAP-ENV:Header>"
                    +"  <SOAP-ENV:Body>"
                    +"    <pns:searchVENDOR xmlns:pns='urn:VendorWSVi'>"
                    +"      <yq1:query xmlns:yq1='urn:VendorWSVi' xmlns:pns='urn:com.sap.mdm.ws.beans.vendorws'>"
                    +"        <pns:criteria>"
                    +"          <yq2:vENDOR_CODE xmlns:yq2='urn:com.sap.mdm.ws.beans.vendorws' xmlns:pns='urn:com.sap.mdm.ws.beans'>"
                    +"            <pns:constraint>"
                    +"              <pns:value>"+vendorNum+"</pns:value>"
                    +"              <pns:expressionOperator>equals</pns:expressionOperator>"
                    +"            </pns:constraint>"
                    +"          </yq2:vENDOR_CODE>"
                    +"        </pns:criteria>"
                    +"      </yq1:query>"
                    +"      <yq3:reposInfo xmlns:yq3='urn:VendorWSVi' xmlns:pns='urn:com.sap.mdm.core.beans'>"
                    +"        <pns:destinationName>COSCOCS_MDM_VENDOR</pns:destinationName>"
                    +"        <pns:repositoryName>VENDOR</pns:repositoryName>"
                    +"        <pns:serverName>172.17.8.78</pns:serverName>"
                    +"      </yq3:reposInfo>"
                    +"    </pns:searchVENDOR>"
                    +"  </SOAP-ENV:Body>"
                    +"</SOAP-ENV:Envelope>";
        }
        return soap;
    }
}
