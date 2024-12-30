package guide.iface.sap.webservice;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import psdi.server.MXServer;

import com.alibaba.fastjson.JSONObject;

public class SecondWebService {
	public static SecondHearBean itemRequestWebService1(String paramsJson) throws Exception{
		
//		if(paramsJson != "" && !paramsJson.equals("")) {
			//服务的地址
			URL wsUrl = null;
			String sapUrl = MXServer.getMXServer().getProperty("guide.sap.url");
//			String sapUrl ="http://172.17.8.62:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_ES02&receiverParty=&receiverService=&interface=SI_pur_inv_out&interfaceNamespace=urn:es02.cosco.com/pur_inv/2020";
			if(sapUrl != null && !sapUrl.equalsIgnoreCase(""))
				wsUrl = new URL(sapUrl);
			System.out.println("\n---------wsUrl:"+wsUrl);
			HttpURLConnection conn = (HttpURLConnection) wsUrl.openConnection();
			//有输入
			conn.setDoInput(true);
			//有输出
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
			String AccountPassword = "PIEXTIFUSER:Welcome8";
			String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(AccountPassword.getBytes());
			conn.setRequestProperty("Authorization", basicAuth);
			String soap = getSoap(paramsJson);
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
			SecondHearBean secondHearBean= SecondJX.returnSap(returnSoap);
			System.out.println("======================================：" + secondHearBean.getZHEADMSG() + "\r");
			 return secondHearBean;
//		}
//		return null;
	}
	
	private static String getSoap(String paramsJson){
		SecondRequestBean requestBean1 = JSONObject.parseObject(paramsJson, SecondRequestBean.class);
		
		SecondRequestBean requestBean =getSecondRequest(requestBean1);
		
		String headStr  = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:ns='urn:es02.cosco.com/pur_inv/2020'>"
				+"   <soapenv:Header/>   "
				+"   <soapenv:Body> "
				+" <ns:MT_pur_inv_request> "
				+"  <!--Optional:-->"
				+"   <IT_HEADER>  "
				+"<!--Optional:-->  "
				+"<ZSOURCE>"+requestBean.getZSOURCE()+"</ZSOURCE>"
				+"<!--Optional:-->  "
				+"<BUKRS>"+requestBean.getBUKRS()+"</BUKRS>   "
				+"<!--Optional:-->  "
				+"<ZTYPE>"+requestBean.getZTYPE()+"</ZTYPE> "
				+"<!--Optional:-->  "
				+"<UNIQUEID>"+requestBean.getUNIQUEID()+"</UNIQUEID>"
				+"<!--Optional:-->  "
				+"<SHA1>"+requestBean.getSHA1()+"</SHA1>    "
				+"<!--Optional:-->  "
				+"<PATH>"+requestBean.getPATH()+"</PATH>"
				+"<!--Optional:-->  "
				+"<VENDORNAME>"+requestBean.getVENDORNAME()+"</VENDORNAME>"
				+"<!--Optional:-->  "
				+"<VATNUMBER>"+requestBean.getVATNUMBER()+"</VATNUMBER> "
				+"<!--Optional:-->  "
				+"<LIFNR>"+requestBean.getLIFNR()+"</LIFNR>  "
				+"<!--Optional:-->  "
				+"<COMPANYNAME>"+requestBean.getCOMPANYNAME()+"</COMPANYNAME>    "
				+"<!--Optional:-->  "
				+"<INVOICE_STATUS>"+requestBean.getINVOICE_STATUS()+"</INVOICE_STATUS>"
				+"<!--Optional:-->  "
				+"<ORIGINALINV>"+requestBean.getORIGINALINV()+"</ORIGINALINV> "
				+"<!--Optional:-->  "
				+"<EXPEDITIONDATE>"+requestBean.getEXPEDITIONDATE()+"</EXPEDITIONDATE>"
				+"<!--Optional:-->  "
				+"<DUEDATE>"+requestBean.getDUEDATE()+"</DUEDATE>   "
				+"<!--Optional:-->  "
				+"<DMBTR1>"+requestBean.getDMBTR1()+"</DMBTR1> "
				+"<!--Optional:-->  "
				+"<DMBTR2>"+requestBean.getDMBTR2()+"</DMBTR2> "
				+"<!--Optional:-->  "
				+"<WAERS>"+requestBean.getWAERS()+"</WAERS>"
				+"<!--Optional:-->  "
				+"<TEXT>"+requestBean.getTEXT()+"</TEXT>"
				+"<!--Optional:-->  "
				+"<PROCESS>"+requestBean.getPROCESS()+"</PROCESS>  "
				+"<!--Optional:-->  "
				+"<ZHEADER>"+requestBean.getZHEADER()+"</ZHEADER>   "
				+"<!--Optional:-->  "
				+"<ZZ01>"+requestBean.getZZ01()+"</ZZ01>"
				+"<!--Optional:-->  "
				+"<ZZ02>"+requestBean.getZZ02()+"</ZZ02>"
				+"<!--Optional:-->  "
				+"<ZZ03>"+requestBean.getZZ03()+"</ZZ03>"
				+"<!--Optional:-->  "
				+"<ZZ04>"+requestBean.getZZ04()+"</ZZ04>"
				+"<!--Optional:-->  "
				+"<ZZ05>"+requestBean.getZZ05()+"</ZZ05>"
				+"<!--Optional:-->  "
				+"<ZZ06>"+requestBean.getZZ06()+"</ZZ06>"
				+"<!--Optional:-->  "
				+"<ZZ07>"+requestBean.getZZ07()+"</ZZ07>"
				+"<!--Optional:-->  "
				+"<ZZ08>"+requestBean.getZZ08()+"</ZZ08>"
				+"<!--Optional:-->  "
				+"<ZZ09>"+requestBean.getZZ09()+"</ZZ09>"
				+"<!--Optional:-->  "
				+"<ZZ10>"+requestBean.getZZ10()+"</ZZ10>"
				+"<!--Optional:-->  "
				+"<EPIGRAPH>"+requestBean.getEPIGRAPH()+"</EPIGRAPH> "
				+"  </IT_HEADER>  ";

		List<SecondRequestItem> itemBeans = requestBean.getItem();
		String itemStr = "";
		for(SecondRequestItem item1 : itemBeans) {
			SecondRequestItem item =	getItem(item1);
			itemStr += "  <!--Optional:-->"
					+"  <IT_ITEM>"
					+"<!--Zero or more repetitions:-->  "
					+"<item>"
					+" <!--Optional:-->   "
					+" <ZSOURCE>"+ item.getZSOURCE()+"</ZSOURCE>  "
					+" <!--Optional:-->   "
					+" <BUKRS>"+ item.getBUKRS()+"</BUKRS>"
					+" <!--Optional:-->   "
					+" <ZTYPE>"+ item.getZTYPE()+"</ZTYPE>  "
					+" <!--Optional:-->   "
					+" <APPROVEDSTATUS>"+item.getAPPROVEDSTATUS() +"</APPROVEDSTATUS>    "
					+" <!--Optional:-->   "
					+" <UNIQUEID>"+ item.getUNIQUEID()+"</UNIQUEID>  "
					+" <!--Optional:-->   "
					+" <CONCEPTLINE>"+item.getCONCEPTLINE() +"</CONCEPTLINE>   "
					+" <!--Optional:-->   "
					+" <ZSTOCKNO>"+item.getZSTOCKNO() +"</ZSTOCKNO> "
					+" <!--Optional:-->   "
					+" <LINECONCEPT>"+ item.getLINECONCEPT()+"</LINECONCEPT>    "
					+" <!--Optional:-->   "
					+" <BOOKINGLINE>"+item.getBOOKINGLINE() +"</BOOKINGLINE>    "
					+" <!--Optional:-->   "
					+" <ANALYTICALACC>"+item.getANALYTICALACC() +"</ANALYTICALACC>"
					+" <!--Optional:-->   "
					+" <ZSERVICE>"+ item.getZSERVICE()+"</ZSERVICE>   "
					+" <!--Optional:-->   "
					+" <ZKOSTL>"+item.getZKOSTL() +"</ZKOSTL>    "
					+" <!--Optional:-->   "
					+" <DETAIL>"+ item.getDETAIL()+"</DETAIL>  "
					+" <!--Optional:-->   "
					+" <WORKERTYPE>"+item.getWORKERTYPE() +"</WORKERTYPE> "
					+" <!--Optional:-->   "
					+" <PERCENTDISTRIBUTION>"+item.getPERCENTDISTRIBUTION() +"</PERCENTDISTRIBUTION>  "
					+" <!--Optional:-->   "
					+" <DMBTR2>"+ item.getDMBTR2()+"</DMBTR2>   "
					+" <!--Optional:-->   "
					+" <ZZMWSKZ1>"+ item.getZZMWSKZ1()+"</ZZMWSKZ1> "
					+" <!--Optional:-->   "
					+" <ZZWITHHOLD>"+ item.getZZWITHHOLD()+"</ZZWITHHOLD> "
					+" <!--Optional:-->   "
					+" <WORKINGDAY>"+ item.getWORKINGDAY()+"</WORKINGDAY> "
					+" <!--Optional:-->   "
					+" <ACC_DESCRIPTION>"+item.getACC_DESCRIPTION()+"</ACC_DESCRIPTION>   "
					+" <!--Optional:-->   "
					+" <EXPENSETYPE>"+ item.getEXPENSETYPE()+"</EXPENSETYPE>    "
					+" <!--Optional:-->   "
					+" <ZZ01>"+ item.getZZ01()+"</ZZ01>  "
					+" <!--Optional:-->   "
					+" <ZZ02>"+ item.getZZ02()+"</ZZ02>  "
					+" <!--Optional:-->   "
					+" <ZZ03>"+ item.getZZ03()+"</ZZ03>  "
					+" <!--Optional:-->   "
					+" <ZZ04>"+ item.getZZ04()+"</ZZ04>  "
					+" <!--Optional:-->   "
					+" <ZZ05>"+item.getZZ05() +"</ZZ05>  "
					+" <!--Optional:-->   "
					+" <ZZ06>"+ item.getZZ06()+"</ZZ06>  "
					+" <!--Optional:-->   "
					+" <ZZ07>"+ item.getZZ07()+"</ZZ07>  "
					+" <!--Optional:-->   "
					+" <ZZ08>"+item.getZZ08() +"</ZZ08>  "
					+" <!--Optional:-->   "
					+" <ZZ09>"+item.getZZ09() +"</ZZ09>  "
					+" <!--Optional:-->   "
					+" <ZZ10>"+item.getZZ10() +"</ZZ10>  "
					+"</item>    "
					+"  </IT_ITEM>    ";
		}
		String bodyStr = " </ns:MT_pur_inv_request>"
				+"   </soapenv:Body>"
				+"</soapenv:Envelope>";
		
		return headStr+itemStr+bodyStr;
	}
	
//	public static void main(String[] args) throws Exception {
//		SecondWebService i = new SecondWebService();
//		i.itemRequestWebService1(null);
//	}
	
	private static  SecondRequestBean getSecondRequest(SecondRequestBean requestBean){
		if(requestBean.getZSOURCE() == null) {
			requestBean.setZSOURCE("");
		}
		if(requestBean.getBUKRS() == null) {
			requestBean.setBUKRS("");
		}
		if(requestBean.getZTYPE() == null) {
			requestBean.setZTYPE("");
		}
		if(requestBean.getUNIQUEID() == null) {
			requestBean.setUNIQUEID("");
		}
		if(requestBean.getSHA1() == null) {
			requestBean.setSHA1("");
		}
		if(requestBean.getPATH() == null) {
			requestBean.setPATH("");
		}
		if(requestBean.getVENDORNAME() == null) {
			requestBean.setVENDORNAME("");
		}
if(requestBean.getVATNUMBER() == null) {
			requestBean.setVATNUMBER("");
		}
		if(requestBean.getLIFNR() == null) {
			requestBean.setLIFNR("");
		}
		if(requestBean.getCOMPANYNAME() == null) {
			requestBean.setCOMPANYNAME("");
		}
		if(requestBean.getINVOICE_STATUS() == null) {
			requestBean.setINVOICE_STATUS("");
		}
		if(requestBean.getORIGINALINV() == null) {
			requestBean.setORIGINALINV("");
		}
		if(requestBean.getEXPEDITIONDATE() == null) {
			requestBean.setEXPEDITIONDATE("");
		}
		if(requestBean.getDUEDATE() == null) {
			requestBean.setDUEDATE("");
		}
if(requestBean.getDMBTR1() == null) {
			requestBean.setDMBTR1("");
		}
		if(requestBean.getDMBTR2() == null) {
			requestBean.setDMBTR2("");
		}
		if(requestBean.getWAERS() == null) {
			requestBean.setWAERS("");
		}
		if(requestBean.getTEXT() == null) {
			requestBean.setTEXT("");
		}
		if(requestBean.getPROCESS() == null) {
			requestBean.setPROCESS("");
		}
		if(requestBean.getZHEADER() == null) {
			requestBean.setZHEADER("");
		}
		
       if(requestBean.getZZ01() == null) {
			requestBean.setZZ01("");
		}
		if(requestBean.getZZ02() == null) {
			requestBean.setZZ02("");
		}
		if(requestBean.getZZ03() == null) {
			requestBean.setZZ03("");
		}
		if(requestBean.getZZ04() == null) {
			requestBean.setZZ04("");
		}
		if(requestBean.getZZ05() == null) {
			requestBean.setZZ05("");
		}
		if(requestBean.getZZ06() == null) {
			requestBean.setZZ06("");
		}
		if(requestBean.getZZ07() == null) {
			requestBean.setZZ07("");
		}
		if(requestBean.getZZ08() == null) {
			requestBean.setZZ08("");
		}
		if(requestBean.getZZ09() == null) {
			requestBean.setZZ09("");
		}
		if(requestBean.getZZ10() == null) {
			requestBean.setZZ10("");
		}
			if(requestBean.getEPIGRAPH() == null) {
			requestBean.setEPIGRAPH("");
		}
		return requestBean;
		
	}
	
	private static  SecondRequestItem getItem(SecondRequestItem item){
		if(item.getZSOURCE() == null) {
			item.setZSOURCE("");
		}
		if(item.getBUKRS() == null) {
			item.setBUKRS("");
		}
		if(item.getZTYPE() == null) {
			item.setZTYPE("");
		}
		if(item.getAPPROVEDSTATUS() == null) {
			item.setAPPROVEDSTATUS("");
		}
		if(item.getUNIQUEID() == null) {
			item.setUNIQUEID("");
		}
		if(item.getCONCEPTLINE() == null) {
			item.setCONCEPTLINE("");
		}
		if(item.getZSTOCKNO() == null) {
			item.setZSTOCKNO("");
		}
		if(item.getLINECONCEPT() == null) {
			item.setLINECONCEPT("");
		}
		if(item.getBOOKINGLINE() == null) {
			item.setBOOKINGLINE("");
		}
		if(item.getANALYTICALACC() == null) {
			item.setANALYTICALACC("");
		}
	
	if(item.getZSERVICE() == null) {
			item.setZSERVICE("");
		}
		if(item.getZKOSTL() == null) {
			item.setZKOSTL("");
		}
		if(item.getDETAIL() == null) {
			item.setDETAIL("");
		}
		if(item.getWORKERTYPE() == null) {
			item.setWORKERTYPE("");
		}
		if(item.getPERCENTDISTRIBUTION() == null) {
			item.setPERCENTDISTRIBUTION("");
		}
		if(item.getDMBTR2() == null) {
			item.setDMBTR2("");
		}
		if(item.getZZMWSKZ1() == null) {
			item.setZZMWSKZ1("");
		}
		if(item.getZZWITHHOLD() == null) {
			item.setZZWITHHOLD("");
		}
		if(item.getWORKINGDAY() == null) {
			item.setWORKINGDAY("");
		}
		if(item.getACC_DESCRIPTION() == null) {
			item.setACC_DESCRIPTION("");
		}
	
		if(item.getEXPENSETYPE() == null) {
			item.setEXPENSETYPE("");
		}
	if(item.getZZ01() == null) {
			item.setZZ01("");
		}
		if(item.getZZ02() == null) {
			item.setZZ02("");
		}
		if(item.getZZ03() == null) {
			item.setZZ03("");
		}
		if(item.getZZ04() == null) {
			item.setZZ04("");
		}
		if(item.getZZ05() == null) {
			item.setZZ05("");
		}
		if(item.getZZ06() == null) {
			item.setZZ06("");
		}
		if(item.getZZ07() == null) {
			item.setZZ07("");
		}
		if(item.getZZ08() == null) {
			item.setZZ08("");
		}
		if(item.getZZ09() == null) {
			item.setZZ09("");
		}
		if(item.getZZ10() == null) {
			item.setZZ10("");
		}
		return item;
		
	}
		
		
		

}
