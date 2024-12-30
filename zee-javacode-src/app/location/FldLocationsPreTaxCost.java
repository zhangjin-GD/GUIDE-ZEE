package guide.app.location;

import java.rmi.RemoteException;
import java.sql.SQLException;

import guide.app.common.ComExecute;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldLocationsPreTaxCost extends MboValueAdapter {

	public FldLocationsPreTaxCost(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		double cost = 0.00d;
		MboRemote mbo = this.getMboValue().getMbo();
		String location = mbo.getString("location");
		boolean isSap = mbo.getBoolean("udissap");
		boolean isConsignment = mbo.getBoolean("udisconsignment");
		if (isSap) {
			if (isConsignment) {
				// 寄售
				String sql = "select sum(invbalances.curbal * nvl(invbalances.udpredictprice,0)) as sumcost from invbalances"
						+ " where invbalances.curbal > 0 and invbalances.location = '" + location + "'";
				try {
					cost = ComExecute.Query(sql);
				} catch (RemoteException | MXException | SQLException e) {
					e.printStackTrace();
				}
			} else {
				// 生产
				String sql = "select sum(invbalances.curbal *  nvl(invcost.avgcost,0)) as sumcost from invbalances"
						+ " left join invcost on invcost.itemnum=invbalances.itemnum and invcost.location=invbalances.location"
						+ " where invbalances.curbal > 0 and invbalances.location = '" + location + "'";
				try {
					cost = ComExecute.Query(sql);
				} catch (RemoteException | MXException | SQLException e) {
					e.printStackTrace();
				}
			}
		}
		this.getMboValue().setValue(cost, 11L);
	}
}
