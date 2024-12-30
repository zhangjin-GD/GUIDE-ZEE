package guide.webclient.beans.login;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.api.client.util.Base64;

import guide.app.common.FormDecoder;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXCipher;
import psdi.util.MXException;
import psdi.util.MXSession;
import psdi.webclient.system.runtime.WebClientRuntime;

public class UDLogin {

	// http://123.60.22.73:7001/maximo/webclient/login/maxlogin.jsp?userName=lvzhx
	// http://123.60.22.73:7001/maximo/webclient/login/maxlogin.jsp?userName=lvzhx&appName=UDPRMAT&recordId=1084
	// http://10.18.35.106:7002/maximo/webclient/login/maxlogin.jsp?userName%3DZHANGYH%26appName%3DUDPRMAT%26recordId%3D1425
	// http://10.18.35.106:7002/maximo/webclient/login/maxlogin.jsp?url=dXNlck5hbWU9SFVBTkdYUCZhcHBOYW1lPVVEUFJNQVQmcmVjb3JkSWQ9MTU4NQ==
	public static void LoginMaximo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String url = new URL(new URL(request.getRequestURL().toString()),
				request.getContextPath() + "/webclient/login/maxlogin.jsp").toString();
		String urlerror = new URL(new URL(request.getRequestURL().toString()),
				request.getContextPath() + "/webclient/login/loginerror.jsp").toString();

//		String value = request.getParameter("appName");
//		String uniqueid = request.getParameter("recordId");// 单据唯一标识id
//		String userName = request.getParameter("userName");

		String reqUrl = request.getParameter("url");
//		System.out.println("reqUrl-->" + reqUrl);

		byte[] bytes = Base64.decodeBase64(reqUrl.getBytes(StandardCharsets.UTF_8));
		String decodeBase64 = new String(bytes);
//		System.out.println("decodeBase64-->" + decodeBase64);
//		String valueUrl = request.getQueryString();// 获取URL参数
//		String encodeValueUrl = URLDecoder.decode(valueUrl, "utf-8");// 解码

		FormDecoder decoder = new FormDecoder(decodeBase64);// key1=value1&key2=value2
		String value = decoder.get("appName");
		String uniqueid = decoder.get("recordId");
		String userName = decoder.get("userName");

//		userName=userName.toUpperCase();
		userName = userName.toLowerCase();
		if (checkUserId(userName)) {// 验证用户
			response.sendRedirect(urlerror);
		}
		String passWord = getMaximoPassWord(userName);// 获取密码

		if (passWord == null || passWord.equalsIgnoreCase("")) {
			response.sendRedirect(urlerror);
		}

		HttpSession session = request.getSession();
		MXSession mxsession = WebClientRuntime.getMXSession(session);
		if (mxsession.isConnected()) {
			mxsession.disconnect();
		}
		try {
			mxsession.setLangCode("ZH");
			mxsession.setUserName(userName);
			mxsession.setPassword(passWord);
			mxsession.connect();

			if (value == null || value.equalsIgnoreCase("null") || value.equalsIgnoreCase("") || value.length() == 0) {
				url = new URL(new URL(request.getRequestURL().toString()), request.getContextPath() + "/ui/login")
						.toString();
			} else {
				if (uniqueid != null && !uniqueid.equalsIgnoreCase("null") && !uniqueid.equalsIgnoreCase("")
						&& uniqueid.length() > 0) {
					url = new URL(new URL(request.getRequestURL().toString()),
							request.getContextPath() + "/ui/maximo.jsp?event=loadapp&value=" + value + "&uniqueid="
									+ uniqueid + "&additionalevent=inboxwf").toString();
				} else {
					url = new URL(new URL(request.getRequestURL().toString()),
							request.getContextPath() + "/ui/maximo.jsp?event=loadapp&value=" + value).toString();
				}
			}
			sendRedirect(request, response, url);// 跳转
			return;
		} catch (Exception e) {
			response.sendRedirect(urlerror);
			return;
		}
	}

	public static void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url)
			throws Exception {
		response.sendRedirect(url);
	}

	public static boolean checkUserId(String userName) throws RemoteException, MXException {
		MboSetRemote maxUserSet = MXServer.getMXServer().getMboSet("MAXUSER",
				MXServer.getMXServer().getSystemUserInfo());
		maxUserSet.setWhere("loginid = '" + userName + "' and status in('ACTIVE','活动')");
		if (!maxUserSet.isEmpty() && maxUserSet.count() > 0) {
			return false;
		}
		return true;
	}

	public static String getMaximoPassWord(String userName)
			throws RemoteException, MXException, UnsupportedEncodingException {
		MboSetRemote maxUserSet = MXServer.getMXServer().getMboSet("MAXUSER",
				MXServer.getMXServer().getSystemUserInfo());
		maxUserSet.setWhere("loginid = '" + userName + "' and status in('ACTIVE','活动')");
		if (!maxUserSet.isEmpty() && maxUserSet.count() > 0) {
			return getPassWord(maxUserSet.getMbo(0).getBytes("password"));
		}
		return null;
	}

	public static String getPassWord(byte[] MaximoPassWord) throws MXException {
		String passWord = "";
		String algTest = "DESede";
		String modeTest = "CBC";
		String paddingTest = "PKCS5Padding";
		// 6
//		String keyTest = "j3*9vk0e8rjvc9fj(*KFikd#";
//		String specTest = "kE*(RKc%";
		// 7
		String keyTest = "Sa#qk5usfmMI-@2dbZP9`jL3";
		String specTest = "beLd7$lB";

		String modTest = "";
		String providerTest = "";
		MXCipher mxc = new MXCipher(algTest, modeTest, paddingTest, keyTest, specTest, modTest, providerTest);
		passWord = mxc.decData(MaximoPassWord);
		return passWord;
	}

	public static byte[] StrToBytes(String str) {
		int len = str.length();
		if (len == 0 || len % 2 == 1) {
			return null;
		}
		byte[] b = new byte[len / 2];
		for (int i = 0; i < str.length(); i += 2) {
			b[i / 2] = (byte) Integer.decode("0x" + str.substring(i, i + 2)).intValue();
		}
		return b;
	}

}