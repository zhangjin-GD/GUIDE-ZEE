package guide.iface.oa.webservice;

import org.apache.commons.lang3.StringEscapeUtils;
import psdi.server.MXServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.*;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * @author 武汉港迪软件信息技术有限公司 创建日期：2022/4/22-17:15 版本 开发者 日期 描述 1.0.0 Sunmiley
 *         2022/4/22 新建
 */
public class OaWebService {

	public static void main(String[] args) throws IOException {
		String jsonText = "{'syscode':'EAM','flowid':'B00000000024','requestname':'请审批采购申请XXXX','workflowname':'采购申请','nodename':'部门主管审批','pcurl':'/maximo','appurl':'/maximo','isremark':'0','viewtype':'1','creator':'duxiaokun.csp','createdatetime':'2022-05-05 10:10:10','receiver':'duxiaokun.csp','receivedatetime':'2022-05-05 10:10:10'}";
		Map<String, String> jsonMaps = getOfficeAuto(jsonText);
		System.out.println("jsonMaps-->" + jsonMaps.toString());
		System.out.println("operResult-->" + jsonMaps.get("operResult"));
		System.out.println("message-->" + jsonMaps.get("message"));
	}

	public static Map<String, String> getOfficeAuto(String jsonText) throws RemoteException {
//		String url = "http://10.18.2.32/services/OfsTodoDataWebService";
		String url = MXServer.getMXServer().getProperty("guide.oa.url");
		String xml = jsonToXml(jsonText);
		return sendHttpPost(url, xml);
	}

	private static Map<String, String> sendHttpPost(String url, String xml) {
		Map<String, String> parse = new HashMap<String, String>();
		if (url != null && !url.equalsIgnoreCase("")) {
			// 服务的地址
			try {
				TrustManager[] tm = { new MyX509TrustManager() };
				SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, tm, new java.security.SecureRandom());
				SSLSocketFactory ssf = sslContext.getSocketFactory();

				URL wsUrl = new URL(url);
				HttpsURLConnection conn = (HttpsURLConnection) wsUrl.openConnection();
				conn.setSSLSocketFactory(ssf);
				// 有输入
				conn.setDoInput(true);
				// 有输出
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
				conn.setRequestProperty("SOAPAction", "/Process Definition");
				OutputStream os = conn.getOutputStream();
				os.write(xml.getBytes());
				InputStream is = conn.getInputStream();

				byte[] b = new byte[10240];
				int len = 0;
				String returnSoap = "";
				while ((len = is.read(b)) != -1) {
					String str = new String(b, 0, len, "UTF-8");
					String value = new String(str.getBytes("iso-8859-1"), "UTF-8");
					returnSoap += value;
				}
				is.close();
				os.close();
				conn.disconnect();
				String message = StringEscapeUtils.unescapeHtml3(returnSoap);
				System.out.println("message--->" + message);
				return OaUtil.getTagValueToMap(message, "ns1:out");
			} catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
				parse.put("operResult", "0");
				if (e.toString().length() > 200) {
					parse.put("message", e.toString().substring(0, 200));
				} else {
					parse.put("message", e.toString());
				}
				e.printStackTrace();
			}
		} else {
			parse.put("operResult", "2");
			parse.put("message", "配置OA地址为空");
		}
		return parse;
	}

	/**
	 * @Description json 字符串转 xml报文
	 * @Param [jsonText]
	 * @Return java.lang.String
	 * @Author Sunmiley
	 * @Date 2022/5/5 9:09
	 */
	public static String jsonToXml(String jsonText) {
		String xml = "";
		xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"webservices.ofs.weaver.com.cn\">\n"
				+ "   <soapenv:Header/>\n" + "   <soapenv:Body>\n" + "      <web:receiveRequestInfoByJson>\n"
				+ "         <web:in0>" + jsonText + "</web:in0>\n" + "      </web:receiveRequestInfoByJson>\n"
				+ "   </soapenv:Body>\n" + "</soapenv:Envelope>";
		return xml;
	}
}
