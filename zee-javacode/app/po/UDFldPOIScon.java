package guide.app.po;

import java.rmi.RemoteException;


import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class UDFldPOIScon extends MboValueAdapter {

	public UDFldPOIScon(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		String udcompany = mbo.getString("udcompany");
		boolean udiscon = mbo.getBoolean("udiscon");
		if (udiscon == true && udcompany.equalsIgnoreCase("11A7TCT")) {
			mbo.setFieldFlag("udconnum", 128L, true);
		}
		if(udiscon == false) {
			mbo.setValueNull("udconnum");
			mbo.setFieldFlag("udconnum", 128L, false);
		}
	}

}
