package guide.app.asset;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldEqRunLogLineDifferToValue extends MboValueAdapter {

	public FldEqRunLogLineDifferToValue(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		double electrickwhcur = mbo.getDouble("electrickwhcur");// 本月抄表（电）
		double electrickwhpre = mbo.getDouble("electrickwhpre");// 上月抄表（电）
		double watercur = mbo.getDouble("watercur");// 本月抄表（水）
		double waterpre = mbo.getDouble("waterpre");// 上月抄表（水）
		double electrickwh = electrickwhcur - electrickwhpre;
		double water = watercur - waterpre;
		mbo.setValue("electrickwh", electrickwh, 11L);
		mbo.setValue("water", water, 11L);
	}
}
