package guide.iface.oa.webservice;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.alibaba.fastjson.JSONObject;

public class OaUtil {

	public static String getTagValueToString(String oaXML, String value) {
		return formatSoapMap(oaXML, value);
	}

	public static Map<String, String> getTagValueToMap(String oaXML, String value) {
		String out = formatSoapMap(oaXML, value);
		Map<String, String> parse = (Map) JSONObject.parse(out);
		return parse;
	}

	public static String formatSoapMap(String oaXML, String value) {
		String jsonOut = "";
		if (oaXML != null && !oaXML.equals("")) {
			try {
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				SOAPMessage msg = formatSoapString(oaXML);
				SOAPBody body = msg.getSOAPBody();
				Iterator<SOAPElement> iterator = body.getChildElements();
				PrintBody(iterator, map);
				jsonOut = map.get(value);
			} catch (SOAPException e) {
				e.printStackTrace();
			}
		}
		return jsonOut;
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

	public static LinkedHashMap<String, String> PrintBody(Iterator<SOAPElement> iterator,
			LinkedHashMap<String, String> map) {
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj != null) {
				SOAPElement element = null;
				try {
					element = (SOAPElement) obj;
					map.put(element.getNodeName(), element.getValue());
				} catch (Exception e) {
				}
				if (element != null) {
					PrintBody(element.getChildElements(), map);
				}
			}
		}
		return map;
	}
}
