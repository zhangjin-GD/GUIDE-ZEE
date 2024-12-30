package guide.webclient.beans.invuse;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXCipher;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelMatByPwd1DateBean extends DataBean {


	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		MboSetRemote invoiceSet = mbo.getMboSet("UDINVUSE");
		if (!invoiceSet.isEmpty() && invoiceSet.count() > 0) {
			MboRemote invoice = invoiceSet.getMbo(0);
			String matby = invoice.getString("udmatbyv1");
			String inputPwd = invoice.getString("udmatpwd1");
			String queryPwd = getMaximoPassWord(matby);
			if (!inputPwd.equals(queryPwd)) {
				throw new MXApplicationException("guide", "1025");
			}
			mbo.setValue("udmatby1", matby, 11L);
		} else {
			throw new MXApplicationException("guide", "1025");
		}
		return super.execute();
	}

	private String getMaximoPassWord(String matby) throws RemoteException, MXException {
		MboSetRemote maxUserSet = MXServer.getMXServer().getMboSet("MAXUSER",
				MXServer.getMXServer().getSystemUserInfo());
		maxUserSet.setWhere("personid = '" + matby + "' and status in('ACTIVE','活动')");
		if (!maxUserSet.isEmpty() && maxUserSet.count() > 0) {
			return getPassWord(maxUserSet.getMbo(0).getBytes("password"));
		}
		return "";
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
}
