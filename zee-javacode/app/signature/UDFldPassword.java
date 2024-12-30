package guide.app.signature;

import java.rmi.RemoteException;

import psdi.app.signature.FldPassword;
import psdi.app.signature.SignatureServiceRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDFldPassword extends FldPassword {

	public UDFldPassword(MboValue mbv) {
		super(mbv);
	}

	public void validate() throws MXException, RemoteException {
		super.validate();
		SignatureServiceRemote sigserv = (SignatureServiceRemote) MXServer.getMXServer().lookup("SIGNATURE");
		String userAttr = "userid";
		try {
			getMboValue().getMbo().getThisMboSet().getMboSetInfo().getMboValueInfo(userAttr);
		} catch (Exception e) {
			userAttr = "username";
		}
		sigserv.validatePassword(getMboValue().getMbo().getString(userAttr), getMboValue().getString(), getMboValue().getMbo().getUserInfo(), getMboValue().getMbo());
		
		String userId = getMboValue().getMbo().getString("userid").toUpperCase();
		String passWord = getMboValue().getString().toUpperCase();
		String langCode = getMboValue().getMbo().getUserInfo().getLangCode();
		//登录名
		if(passWord.indexOf(userId) != -1){
			String message = "提示，密码存在登录信息“"+userId+"”，请重新输入！";
			if(langCode.equalsIgnoreCase("EN")){
				message = "Your input contains longinId <"+userId+">,please re-enter!";
			}
			Object params[] = { message };
			throw new MXApplicationException("instantmessaging","tsdimexception", params);
		}
		//增加弱密码口令验证
		MboSetRemote weakPwdEgSet = MXServer.getMXServer().getMboSet("UDWEAKPWDEG", MXServer.getMXServer().getSystemUserInfo());
		weakPwdEgSet.setWhere("'"+passWord+"' like '%'||upper(passWord)||'%'");
		if (!weakPwdEgSet.isEmpty() && weakPwdEgSet.count() > 0) {
			String message = "提示，密码存在易猜口令“"+weakPwdEgSet.getMbo(0).getString("password")+"”，请重新输入！";
			if(langCode.equalsIgnoreCase("EN")){
				message = "Your input contains easy guess password <"+weakPwdEgSet.getMbo(0).getString("password")+">,please re-enter!";
			}
			Object params[] = { message };
			throw new MXApplicationException("instantmessaging","tsdimexception", params);
		}
		weakPwdEgSet.close();
	}
	
}
