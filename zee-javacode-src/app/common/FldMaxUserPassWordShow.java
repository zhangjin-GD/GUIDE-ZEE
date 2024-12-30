package guide.app.common;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXCipher;
import psdi.util.MXException;

public class FldMaxUserPassWordShow extends MboValueAdapter {

	public FldMaxUserPassWordShow(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();

		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote maxUserSet = mbo.getMboSet("MAXUSER");
		if (maxUserSet != null && !maxUserSet.isEmpty()) {
			MboRemote maxUser = maxUserSet.getMbo(0);
			byte[] password = maxUser.getBytes("password");
			String pw = passWordShow(password);
			this.getMboValue().setValue(pw, 11L);
		}
	}

	private String passWordShow(byte[] password) throws MXException {
		String algTest = "DESede";
		String modeTest = "CBC";
		String paddingTest = "PKCS5Padding";
		// 6
//	        String keyTest = "j3*9vk0e8rjvc9fj(*KFikd#";
//	        String specTest = "kE*(RKc%";
		// 7
		String keyTest = "Sa#qk5usfmMI-@2dbZP9`jL3";
		String specTest = "beLd7$lB";
		String modTest = "";
		String providerTest = "";
		MXCipher mxc = new MXCipher(algTest, modeTest, paddingTest, keyTest, specTest, modTest, providerTest);

		String pw = mxc.decData(password);
		return pw;
	}

}
