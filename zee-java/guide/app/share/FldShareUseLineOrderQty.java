package guide.app.share;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldShareUseLineOrderQty extends MboValueAdapter {

	public FldShareUseLineOrderQty(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void validate() throws MXException, RemoteException {
		super.validate();
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String usetype = mbo.getString("usetype");
		if (owner != null) {
			if ("BORROW".equalsIgnoreCase(usetype)) {
				double curbal = 0, invuseQty = 0, borrowQty = 0, returnQty = 0;
				int invbalancesid = mbo.getInt("invbalancesid");
				MboSetRemote invbalSet = mbo.getMboSet("INVBALANCES");
				if (!invbalSet.isEmpty() && invbalSet.count() > 0) {
					MboRemote invBal = invbalSet.getMbo(0);
					curbal = invBal.getDouble("curbal");// 余量
					// 领用数量
					MboSetRemote invuseLineSet = invBal.getMboSet("UDINVUSELINE");
					if (!invuseLineSet.isEmpty() && invuseLineSet.count() > 0) {
						invuseQty = invuseLineSet.sum("quantity");
					}
				}
				double qty = curbal - invuseQty;
				MboSetRemote borrowSet = owner.getMboSet("BORROWLINE");
				if (!borrowSet.isEmpty() && borrowSet.count() > 0) {
					for (int j = 0; borrowSet.getMbo(j) != null; j++) {
						MboRemote borrowLineMbo = borrowSet.getMbo(j);
						int invbalid = borrowLineMbo.getInt("invbalancesid");
						if (!borrowLineMbo.toBeDeleted() && invbalid == invbalancesid) {
							borrowQty += borrowLineMbo.getDouble("orderqty");
						}
					}
				}
				MboSetRemote returnSet = owner.getMboSet("RETURNLINE");
				if (!returnSet.isEmpty() && returnSet.count() > 0) {
					for (int j = 0; returnSet.getMbo(j) != null; j++) {
						MboRemote returnLineMbo = returnSet.getMbo(j);
						int invbalid = returnLineMbo.getInt("invbalancesid");
						if (!returnLineMbo.toBeDeleted() && invbalid == invbalancesid) {
							returnQty += returnLineMbo.getDouble("orderqty");
						}
					}
				}
				double shareUseQty = borrowQty - returnQty;
				if (shareUseQty > qty) {
					throw new MXApplicationException("guide", "1049");
				}
			} else if ("RETURN".equalsIgnoreCase(usetype)) {
				double borrowQty = 0;
				MboSetRemote borrowSet = mbo.getMboSet("BORROWID");
				if (!borrowSet.isEmpty() && borrowSet.count() > 0) {
					MboRemote borrow = borrowSet.getMbo(0);
					borrowQty = borrow.getDouble("ORDERQTY");// 借用数量
				}
				MboSetRemote returnSet = owner.getMboSet("RETURNLINE");
				double returnQty = 0;
				if (!returnSet.isEmpty() && returnSet.count() > 0) {
					int udshareuselineid = mbo.getInt("borrowid");
					for (int j = 0; returnSet.getMbo(j) != null; j++) {
						MboRemote returnLineMbo = returnSet.getMbo(j);
						int borrowid = returnLineMbo.getInt("borrowid");
						if (!returnLineMbo.toBeDeleted() && borrowid == udshareuselineid) {
							returnQty += returnLineMbo.getDouble("orderqty");
						}
					}
				}
				if (returnQty > borrowQty) {
					throw new MXApplicationException("guide", "1104");
				}
			}
		}
	}
}
