package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldWOResult extends MboValueAdapter {

	public FldWOResult(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote parent = mbo.getOwner();
		StringBuffer resultset = new StringBuffer();
		if (parent != null && parent instanceof UDWO) {
			parent.getMboSet("SHOWTASKS").resetQbe();
			MboSetRemote lineSet = mbo.getThisMboSet();
			if (!lineSet.isEmpty() && lineSet.count() > 0) {
				for (int i = 0; lineSet.getMbo(i) != null; i++) {
					MboRemote line = lineSet.getMbo(i);
					String udresult = line.getString("udresult");
					if (udresult != null && !udresult.isEmpty() && !"正常".equals(udresult) && !udresult.equalsIgnoreCase("yes")) {
						resultset.append(udresult).append(";");
					}
				}
			}
			parent.setValue("udresultset", resultset.toString(), 11L);
		}
	}
}
