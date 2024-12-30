package guide.iface.mdm.webservice;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;


public class MdmJX {
	
	/**
     * @param soap
     * @throws DocumentException
     */
    public static ReturnBean returnSap(String soap) throws Exception {
        try {
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
}
