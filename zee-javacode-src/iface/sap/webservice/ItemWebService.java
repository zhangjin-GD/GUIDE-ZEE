package guide.iface.sap.webservice;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import psdi.server.MXServer;

import com.alibaba.fastjson.JSONObject;

public class ItemWebService {

	public static HearBean itemRequestWebService(String paramsJson) throws Exception {

		if (paramsJson != "" && !paramsJson.equals("")) {
			// 服务的地址
			URL wsUrl = null;
			String sapUrl = MXServer.getMXServer().getProperty("guide.sap.url");
//			String sapUrl ="http://172.17.8.62:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=CP_EAM&receiverParty=&receiverService=&interface=SI_stock_return_EAMOUT&interfaceNamespace=urn:cp.cosco.com/eam/2020";
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
			return JX.returnSap(returnSoap);
		}
		return null;
	}

	private static String getSoap(String paramsJson) {
		RequestBean requestBean = JSONObject.parseObject(paramsJson, RequestBean.class);

		if (requestBean.getZSOURCE() == null) {
			requestBean.setZSOURCE("");
		}
		if (requestBean.getBUKRS() == null) {
			requestBean.setBUKRS("");
		}
		if (requestBean.getZSTOCKNO() == null) {
			requestBean.setZSTOCKNO("");
		}
		if (requestBean.getBUDAT() == null) {
			requestBean.setBUDAT("");
		}
		if (requestBean.getZDATE1() == null) {
			requestBean.setZDATE1("");
		}
		if (requestBean.getLIFNR() == null) {
			requestBean.setLIFNR("");
		}
		if (requestBean.getZTRAN() == null) {
			requestBean.setZTRAN("");
		}
		if (requestBean.getZEAMHEADFIELD1() == null) {
			requestBean.setZEAMHEADFIELD1("");
		}

		if (requestBean.getZEAMHEADFIELD2() == null) {
			requestBean.setZEAMHEADFIELD2("");
		}

		if (requestBean.getZEAMHEADFIELD3() == null) {
			requestBean.setZEAMHEADFIELD3("");
		}

		if (requestBean.getZEAMHEADFIELD4() == null) {
			requestBean.setZEAMHEADFIELD4("");
		}
		if (requestBean.getZEAMHEADFIELD5() == null) {
			requestBean.setZEAMHEADFIELD5("");
		}

		String headStr = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"urn:cp.cosco.com/eam/2020\">"
				+ "<soapenv:Header/>" + "<soapenv:Body>" + "<ns:MT_stock_return_request>" + "<IT_EAM_HEADER>"
				+ "<ZSOURCE>" + requestBean.getZSOURCE() + "</ZSOURCE>" + "<BUKRS>" + requestBean.getBUKRS()
				+ "</BUKRS>" + "<ZSTOCKNO>" + requestBean.getZSTOCKNO() + "</ZSTOCKNO>" + "<BUDAT>"
				+ requestBean.getBUDAT() + "</BUDAT>" + "<ZDATE1>" + requestBean.getZDATE1() + "</ZDATE1>" + "<LIFNR>"
				+ requestBean.getLIFNR() + "</LIFNR>" + "<ZTRAN>" + requestBean.getZTRAN() + "</ZTRAN>"
				+ "<ZEAMHEADFIELD1>" + requestBean.getZEAMHEADFIELD1() + "</ZEAMHEADFIELD1>" + "<ZEAMHEADFIELD2>"
				+ requestBean.getZEAMHEADFIELD2() + "</ZEAMHEADFIELD2>" + "<ZEAMHEADFIELD3>"
				+ requestBean.getZEAMHEADFIELD3() + "</ZEAMHEADFIELD3>" + "<ZEAMHEADFIELD4>"
				+ requestBean.getZEAMHEADFIELD4() + "</ZEAMHEADFIELD4>" + "<ZEAMHEADFIELD5>"
				+ requestBean.getZEAMHEADFIELD5() + "</ZEAMHEADFIELD5>" + "</IT_EAM_HEADER>" + "<IT_EAM_ITEM>";

		List<DT_stock_return_requestIT_EAM_ITEMItem> itemBeans = requestBean.getItem();
		String itemStr = "";
		for (DT_stock_return_requestIT_EAM_ITEMItem item : itemBeans) {
			if (item.getZSTOCKNO() == null) {
				item.setZSTOCKNO("");
			}
			if (item.getZSTOCKITEMNO() == null) {
				item.setZSTOCKITEMNO("");
			}
			if (item.getZSTOCKITEMNO().length() > 3) {
				System.out.println("ERROR: ZSTOCKITEMNO长度大于3， 当前长度为：" + item.getZSTOCKITEMNO().length());
			}
			if (item.getEBELN() == null) {
				item.setEBELN("");
			}
			if (item.getEBELP() == null) {
				item.setEBELP("");
			}
			if (item.getMWSKZ() == null) {
				item.setMWSKZ("");
			}
			if (item.getWRBTR1() == null) {
				item.setWRBTR1("");
			}
			if (item.getDMBTR1() == null) {
				item.setDMBTR1("");
			}
			if (item.getDMBTR3() == null) {
				item.setDMBTR3("");
			}
			if (item.getDMBTR4() == null) {
				item.setDMBTR4("");
			}
			if (item.getLIFNR1() == null) {
				item.setLIFNR1("");
			}
			if (item.getKOSTL() == null) {
				item.setKOSTL("");
			}
			if (item.getAUFNR() == null) {
				item.setAUFNR("");
			}
			if (item.getMTART() == null) {
				item.setMTART("");
			}
			if (item.getMAKTX() == null) {
				item.setMAKTX("");
			}
			if (item.getZQUANTITY() == null) {
				item.setZQUANTITY("");
			}
			if (item.getZUNIT() == null) {
				item.setZUNIT("");
			}
			if (item.getPRCTR() == null) {
				item.setPRCTR("");
			}
			if (item.getWAERS() == null) {
				item.setWAERS("");
			}
			if (item.getANLN1() == null) {
				item.setANLN1("");
			}
			if (item.getZAUXFIELD() == null) {
				item.setZAUXFIELD("");
			}
			if (item.getZSTATUS() == null) {
				item.setZSTATUS("");
			}
			if (item.getZEAMITEMFIELD1() == null) {
				item.setZEAMITEMFIELD1("");
			}
			if (item.getZMATERIALDESCL2() == null) {
				item.setZMATERIALDESCL2("");
			}
			if (item.getZMATERIALDESCL3() == null) {
				item.setZMATERIALDESCL3("");
			}
			if (item.getZEAMHEADFIELD3() == null) {
				item.setZEAMHEADFIELD3("");
			}
			if (item.getZEAMHEADFIELD4() == null) {
				item.setZEAMHEADFIELD4("");
			}
			if (item.getZEAMHEADFIELD5() == null) {
				item.setZEAMHEADFIELD5("");
			}
			if (item.getZMATERIALDESCL1() == null) {
				item.setZMATERIALDESCL1("");
			}
			if (item.getZMATERIALCODE() == null) {
				item.setZMATERIALCODE("");
			}
			if (item.getZMATERIALL3() == null) {
				item.setZMATERIALL3("");
			}
			if (item.getZMATERIALL2() == null) {
				item.setZMATERIALL2("");
			}
			if (item.getZMATERIALL1() == null) {
				item.setZMATERIALL1("");
			}
			if (item.getZEAMHEADFIELD2() == null) {
				item.setZEAMHEADFIELD2("");
			}
			if (item.getZREPAIRTYPE() == null) {
				item.setZREPAIRTYPE("");
			}
			if (item.getZEQUIPCODE() == null) {
				item.setZEQUIPCODE("");
			}
			if (item.getZEQUIPNAME() == null) {
				item.setZEQUIPNAME("");
			}

			if (item.getZEQUIPCLASS() == null) {
				item.setZEQUIPCLASS("");
			}
			if (item.getZEQUIPCLASSNAME() == null) {
				item.setZEQUIPCLASSNAME("");
			}
			if (item.getZWORKORDER() == null) {
				item.setZWORKORDER("");
			}

			itemStr += "<item>" + "<ZSTOCKNO>" + item.getZSTOCKNO() + "</ZSTOCKNO>" + "<ZSTOCKITEMNO>"
					+ item.getZSTOCKITEMNO() + "</ZSTOCKITEMNO>" + "<EBELN>" + item.getEBELN() + "</EBELN>" + "<EBELP>"
					+ item.getEBELP() + "</EBELP>" + "<MWSKZ>" + item.getMWSKZ() + "</MWSKZ>" + "<WRBTR1>"
					+ item.getWRBTR1() + "</WRBTR1>" + "<DMBTR1>" + item.getDMBTR1() + "</DMBTR1>" + "<DMBTR3>"
					+ item.getDMBTR3() + "</DMBTR3>" + "<DMBTR4>" + item.getDMBTR4() + "</DMBTR4>" + "<LIFNR1>"
					+ item.getLIFNR1() + "</LIFNR1>" + "<KOSTL>" + item.getKOSTL() + "</KOSTL>" + "<AUFNR>"
					+ item.getAUFNR() + "</AUFNR>" + "<MTART>" + item.getMTART() + "</MTART>" + "<MAKTX>"
					+ item.getMAKTX() + "</MAKTX>" + "<ZQUANTITY>" + item.getZQUANTITY() + "</ZQUANTITY>" + "<ZUNIT>"
					+ item.getZUNIT() + "</ZUNIT>" + "<PRCTR>" + item.getPRCTR() + "</PRCTR>" + "<WAERS>"
					+ item.getWAERS() + "</WAERS>" + "<ANLN1>" + item.getANLN1() + "</ANLN1>" + "<ZAUXFIELD>"
					+ item.getZAUXFIELD() + "</ZAUXFIELD>" + "<ZSTATUS>" + item.getZSTATUS() + "</ZSTATUS>"
					+ "<ZEAMITEMFIELD1>" + item.getZEAMITEMFIELD1() + "</ZEAMITEMFIELD1>" + "<ZMATERIALDESCL2>"
					+ item.getZMATERIALDESCL2() + "</ZMATERIALDESCL2>" + "<ZMATERIALDESCL3>" + item.getZMATERIALDESCL3()
					+ "</ZMATERIALDESCL3>" + "<ZEAMHEADFIELD3>" + item.getZEAMHEADFIELD3() + "</ZEAMHEADFIELD3>"
					+ "<ZEAMHEADFIELD4>" + item.getZEAMHEADFIELD4() + "</ZEAMHEADFIELD4>" + "<ZEAMHEADFIELD5>"
					+ item.getZEAMHEADFIELD5() + "</ZEAMHEADFIELD5>" + "<ZMATERIALDESCL1>" + item.getZMATERIALDESCL1()
					+ "</ZMATERIALDESCL1>" + "<ZMATERIALCODE>" + item.getZMATERIALCODE() + "</ZMATERIALCODE>"
					+ "<ZMATERIALL3>" + item.getZMATERIALL3() + "</ZMATERIALL3>" + "<ZMATERIALL2>"
					+ item.getZMATERIALL2() + "</ZMATERIALL2>" + "<ZMATERIALL1>" + item.getZMATERIALL1()
					+ "</ZMATERIALL1>" + "<ZEAMHEADFIELD2>" + item.getZEAMHEADFIELD2() + "</ZEAMHEADFIELD2>"
					+ "<ZREPAIRTYPE>" + item.getZREPAIRTYPE() + "</ZREPAIRTYPE>" + "<ZEQUIPCODE>" + item.getZEQUIPCODE()
					+ "</ZEQUIPCODE>" + "<ZEQUIPNAME>" + item.getZEQUIPNAME() + "</ZEQUIPNAME>" + "<ZEQUIPCLASS>"
					+ item.getZEQUIPCLASS() + "</ZEQUIPCLASS>" + "<ZEQUIPCLASSNAME>" + item.getZEQUIPCLASSNAME()
					+ "</ZEQUIPCLASSNAME>" + "<ZWORKORDER>" + item.getZWORKORDER() + "</ZWORKORDER>" + "</item>";
		}
		String bodyStr = "</IT_EAM_ITEM>" + "</ns:MT_stock_return_request>" + "</soapenv:Body></soapenv:Envelope>";
		return headStr + itemStr + bodyStr;
	}

	public static void main(String[] args) throws Exception {
		ItemWebService i = new ItemWebService();
		for (int i1 = 100; i1 < 102; i1++) {
			String paramsJson = "{\n" + "\t\"BUKRS\": \"11A7\",\n" + "\t\"ZSOURCE\": \"660\",\n" + "\t\"item\": [{\n"
					+ "\t\t\"KOSTL\": \"11A7010900\",\n" + "\t\t\"ZSTOCKNO\": \"ADJ1040\",\n"
					+ "\t\t\"ZEAMHEADFIELD3\": \"\",\n" + "\t\t\"ZEAMHEADFIELD4\": \"\",\n"
					+ "\t\t\"ZEAMHEADFIELD5\": \"\",\n" + "\t\t\"AUFNR\": \"\",\n" + "\t\t\"ZREPAIRTYPE\": \"\",\n"
					+ "\t\t\"WAERS\": \"CNY\",\n" + "\t\t\"ZSTOCKITEMNO\": 1,\n" + "\t\t\"ZQUANTITY\": 10,\n"
					+ "        \"MTART\": \"1001\",\n" + "\t\t\"ZEQUIPCLASSNAME\": \"\",\n" + "\t\t\"DMBTR3\": 10,\n"
					+ "\t\t\"ZEAMHEADFIELD2\": \"\",\n" + "\t\t\"ZEAMITEMFIELD1\": \"\"\n" + "\t}],\n"
					+ "\t\"ZSTOCKNO\": \"ADJ1040\",\n" + "\t\"ZEAMHEADFIELD3\": \"\",\n"
					+ "\t\"ZEAMHEADFIELD4\": \"\",\n" + "\t\"ZEAMHEADFIELD5\": \"\",\n"
					+ "\t\"ZDATE1\": \"20220607\",\n" + "\t\"ZEAMHEADFIELD1\": \"\",\n"
					+ "\t\"ZEAMHEADFIELD2\": \"\",\n" + "\t\"BUDAT\": \"20220607\",\n" + "\t\"ZTRAN\": \"209\"\n" + "}";
			System.out.println(paramsJson);
			i.itemRequestWebService(paramsJson);
		}
	}
}
