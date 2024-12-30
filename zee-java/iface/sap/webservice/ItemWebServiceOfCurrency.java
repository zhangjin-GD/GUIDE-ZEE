package guide.iface.sap.webservice;


import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.bind.DatatypeConverter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import psdi.server.MXServer;

//import psdi.server.MXServer;



public class ItemWebServiceOfCurrency {

	public static String itemRequestWebServiceOfCurrency(String paramsJson1,String paramsJson2) throws Exception {

		if (paramsJson1 != "" && !paramsJson1.equals("") &&paramsJson2 != "" && !paramsJson2.equals("") ) {
			// 服务的地址
			URL wsUrl = null;
			String sapUrl = MXServer.getMXServer().getProperty("guide.sap.url.currency");
//			String sapUrl ="http://sappodev.cns.cosco.cos:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=CP_BS&receiverParty=&receiverService=&interface=SI_ZFM_DOCPOST_RATE_IF&interfaceNamespace=urn:cpcosco.com/2020";
			if (sapUrl != null && !sapUrl.equalsIgnoreCase(""))
				wsUrl = new URL(sapUrl);
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
			String soap = getSoap(paramsJson1,paramsJson2);
			System.out.print("请求报文：" + soap + "\r"); 
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
			System.out.println("返回报文：" + returnSoap + "\r");
			is.close();
			os.close();
			conn.disconnect();
			HearBeanOfCurrency h = JXOfCurrency.returnSap(returnSoap);
			String s  = JSONObject.toJSONString(h, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue);
			return s;
		}
		return null;
	}
	private static String getSoap(String paramsJson1,String paramsJson2) {
		//请求头信息
		RequestBeanOfCurrency requestBean = JSONObject.parseObject(paramsJson1, RequestBeanOfCurrency.class);
				
		if (requestBean.getCRDATE() == null) {
			requestBean.setCRDATE("");
		}
		if (requestBean.getCRNAME() == null) {
			requestBean.setCRNAME("");
		}
	
		if (requestBean.getZRTOALL() == null) {
			requestBean.setZRTOALL("");
		}
	
		if (requestBean.getPACKGID() == null) {
			requestBean.setPACKGID("");
		}
		String headStr = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"urn:cpcosco.com/2020\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>" 
				+ "<ns:MT_ZFM_DOCPOST_RATE_REQ>" + 
				"         <!--Optional:-->" + 
				"         <IS_HEADER>" + 
				"            <!--Optional:-->" + 
				"            <CRDATE>"+ requestBean.getCRDATE()+"</CRDATE>"
				+ "<!--Optional:-->" + 
				"            <CRNAME>"+requestBean.getCRNAME()+"</CRNAME>" + 
				"            <!--Optional:-->" + 
				"            <ZRTOALL>"+requestBean.getZRTOALL()+"</ZRTOALL>" + 
				"            <!--Optional:-->" + 
				"            <PACKGID>"+requestBean.getPACKGID()+"</PACKGID>" + 
				"         </IS_HEADER>" + 
				"         <!--Optional:-->\r\n";

		//请求明细行信息
		ItemBeanOfCurrency itemBean = JSONObject.parseObject(paramsJson2, ItemBeanOfCurrency.class);
		
		String itemStr = "<IT_ITEM>" + 
				"            <!--Zero or more repetitions:-->" + 
				"            <item>" + 
				"               <!--Optional:-->" + 
				"               <ITEMID>"+itemBean.getITEMID()+"</ITEMID>" + 
				"               <!--Optional:-->" + 
				"               <KURST>"+itemBean.getKURST()+"</KURST>" + 
				"               <!--Optional:-->" + 
				"               <GDATU>"+itemBean.getGDATU()+"</GDATU>" + 
				"               <!--Optional:-->" + 
				"               <FCURR>"+itemBean.getFCURR()+"</FCURR>" + 
				"               <!--Optional:-->" + 
				"               <TCURR>"+itemBean.getTCURR()+"</TCURR>" + 
				"            </item>" + 
				"         </IT_ITEM>" + 
				"      </ns:MT_ZFM_DOCPOST_RATE_REQ>" + 
				"   </soapenv:Body>" + 
				"</soapenv:Envelope>";
		return headStr + itemStr;
	}

//	public static void main(String[] args) throws Exception {
//		ItemWebService i = new ItemWebService();
//	
//			String paramsJson1= "{\"CRDATE\":\"20221221\",\"CRNAME\":\"\",\"PACKGID\":\"20221221114227\",\"ZRTOALL\":\"1\"}";
//			String paramsJson2 = "{\"KURST\":\"Z\",\"FCURR\":\"USD\",\"TCURR\":\"ADE\",\"ITEMID\":\"1\",\"GDATU\":\"20221131\"}";
//			HearBean h=	i.itemRequestWebService(paramsJson1,paramsJson2);
//			
//			String s  = JSONObject.toJSONString(h, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue);
//			System.out.println("返回实体：" + s );
//	}
}
