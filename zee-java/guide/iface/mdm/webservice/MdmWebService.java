package guide.iface.mdm.webservice;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.bind.DatatypeConverter;

import psdi.server.MXServer;

public class MdmWebService {

	public static ReturnBean itemRequestWebService(String vendorNum, String updateTime) throws Exception {
		URL wsUrl = null;
//			String sapUrl = "http://172.17.8.66:50000/VendorWS/HTTPNone?wsdl&mode=ws_policy&style=document";
		String url = MXServer.getMXServer().getProperty("guide.mdm.url");
		if (url != null && !url.equalsIgnoreCase(""))
			wsUrl = new URL(url);
		System.out.println("\n---------wsUrl:" + wsUrl);
		HttpURLConnection conn = (HttpURLConnection) wsUrl.openConnection();
		// 有输入
		conn.setDoInput(true);
		// 有输出
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
		String AccountPassword = "PIEXTIFUSER:Welcome8";
		String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(AccountPassword.getBytes());
		conn.setRequestProperty("Authorization", basicAuth);
		String soap = SoapUtil.getSoap(vendorNum, updateTime);
//			OutputStream os = conn.getOutputStream();
//			os.write(soap.getBytes());
		OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
		os.write(soap);
		os.flush();

		InputStream is = conn.getInputStream();
		byte[] b = new byte[1024];
		int len = 0;
		String returnSoap = "";
		while ((len = is.read(b)) != -1) {
			String str = new String(b, 0, len, "UTF-8");
			returnSoap += str;
		}
		is.close();
		os.close();
		conn.disconnect();
		ReturnBean r = MdmJX.returnSap(returnSoap);
		return r;
//		}
//		return null;
	}

	private static String getSoap() {
		String headStr = "<?xml version='1.0' encoding='utf-8'?>"
				+ "<SOAP-ENV:Envelope xmlns:SOAP-ENV='http://schemas.xmlsoap.org/soap/envelope/' xmlns:xs='http://www.w3.org/2001/XMLSchema' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>"
				+ "  <SOAP-ENV:Header>"
				+ "    <WSCorIDSOAPHeader xmlns='http://www.wilytech.com/' CorID='550FDA68AC1108425117DD670CCE6703,1:1,0,0,,,AgAAALZIQgAAAAFGAAAAAQAAABFqYXZhLnV0aWwuSGFzaE1hcAAAAAJIQgAAAAJGAAAAAgAAABBqYXZhLmxhbmcuU3RyaW5nAA9DYWxsZXJUaW1lc3RhbXBIQgAAAANFAAAAAgANMTYzNzgwOTY0MjA4OEhCAAAABEUAAAACAApUeG5UcmFjZUlkSEIAAAAFRQAAAAIAJDU1MENFQjk2QUMxMTA4NDI1MTE3REQ2N0RCNzBFQTlDMzgwMw=='/>"
				+ "  </SOAP-ENV:Header>" + "  <SOAP-ENV:Body>" + "    <pns:searchVENDOR xmlns:pns='urn:VendorWSVi'>"
				+ "      <yq1:query xmlns:yq1='urn:VendorWSVi' xmlns:pns='urn:com.sap.mdm.ws.beans.vendorws'>"
				+ "        <pns:criteria>"
				+ "          <yq1:uPDATE_DATE_MDM xmlns:yq3='urn:com.sap.mdm.ws.beans.vendorws' xmlns:pns='urn:com.sap.mdm.ws.beans'>"
				+ "            <pns:constraint>" + "              <pns:expressionOperator>LESS</pns:expressionOperator>"
				+ "              <pns:value>2022-04-13T14:50:14.372+08:00</pns:value>" + "            </pns:constraint>"
				+ "          </yq1:uPDATE_DATE_MDM>" + "        </pns:criteria>" + "      </yq1:query>"
				+ "      <yq3:reposInfo xmlns:yq3='urn:VendorWSVi' xmlns:pns='urn:com.sap.mdm.core.beans'>"
				+ "        <pns:destinationName>COSCOCS_MDM_VENDOR</pns:destinationName>"
				+ "        <pns:repositoryName>VENDOR</pns:repositoryName>"
				+ "        <pns:serverName>172.17.8.78</pns:serverName>" + "      </yq3:reposInfo>"
				+ "    </pns:searchVENDOR>" + "  </SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>";
		return headStr;
	}

	public static void main(String[] args) throws Exception {
		MdmWebService i = new MdmWebService();

//		i.itemRequestWebService();
	}
}
