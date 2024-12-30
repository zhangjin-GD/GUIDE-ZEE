package guide.app.matsafe;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.server.MXServer;
import psdi.util.MXException;

public class FldMatSafeUpperDate extends MboValueAdapter {

	public FldMatSafeUpperDate(MboValue mbovalue) {
		super(mbovalue);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		Date sysdate = MXServer.getMXServer().getDate();
		Date upperdate = mbo.getDate("upperdate");
		double teuinit = mbo.getDouble("teuinit");
		double unitinit = mbo.getDouble("unitinit");
		double actioninit = mbo.getDouble("actioninit");
		double runinit = mbo.getDouble("runinit");
		int day = 0;
		double teuact = 0, unitact = 0, actionact = 0, runact = 0;
		if (sysdate != null && upperdate != null) {
			day = (int) ((sysdate.getTime() - upperdate.getTime()) / (24 * 60 * 60 * 1000));
			MboSetRemote workSet = mbo.getMboSet("$UDTOSWORKLOAD", "UDTOSWORKLOAD",
					"udcompany=:udcompany and assetnum=:assetnum and rundate >= :upperdate");
			teuact = workSet.sum("teuload") + teuinit;
			unitact = workSet.sum("workload") + unitinit;
			actionact = workSet.sum("move") + actioninit;

			MboSetRemote runSet = mbo.getMboSet("$UDTOSRUNTIME", "UDTOSRUNTIME",
					"udcompany=:udcompany and assetnum=:assetnum and rundate >= :upperdate");
			runact = runSet.sum("runtime") + runinit;
		}
		mbo.setValue("teuact", teuact, 11L);
		mbo.setValue("unitact", unitact, 11L);
		mbo.setValue("actionact", actionact, 11L);
		mbo.setValue("runact", runact, 11L);
		mbo.setValue("calact", day, 11L);

		MboSetRemote parentSet = mbo.getMboSet("PARENT");
		if (!parentSet.isEmpty() && parentSet.count() > 0) {
			MboRemote parent = parentSet.getMbo(0);
			parent.setValue("lowerdate", upperdate, 11L);
		}
	}
}
