package guide.app.share;

import java.rmi.RemoteException;

import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class ShareUse extends UDMbo implements MboRemote {

	public ShareUse(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
		try {
			// 借用公司
			MboSetRemote lineSet = getMboSet("BORROWLINE");
			if (!lineSet.isEmpty() && lineSet.count() > 0) {
				setFieldFlag("udcompanysource", 7L, true);
			} else {
				setFieldFlag("udcompanysource", 7L, false);
			}

			// 待补充
			String status = getString("status");
			if (status != null && !status.equalsIgnoreCase("")) {

				if ("BORROW".equalsIgnoreCase(status)) {
					getMboSet("BORROWLINE").setFlag(7L, true);
				} else {
					getMboSet("BORROWLINE").setFlag(7L, false);
				}

				if ("CAN".equalsIgnoreCase(status)) {
					setFlag(7L, true);
				} else {
					setFlag(7L, false);
				}
			}

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
		String status = getString("status");
		if (status != null && ("WAPPR".equalsIgnoreCase(status)
				|| (!getMboValue("status").getInitialValue().asString().equalsIgnoreCase("APPR")
						&& "APPR".equalsIgnoreCase(status)))) {// 领料
			checkQuantity();
		}
	}

	public void checkQuantity() throws RemoteException, MXException {
		MboSetRemote borrowLineSet = this.getMboSet("BORROWLINE");
		if (borrowLineSet != null && !borrowLineSet.isEmpty()) {
			String linenum = "";
			for (int i = 0; borrowLineSet.getMbo(i) != null; i++) {
				double curbal = 0, invuseQty = 0, borrowQty = 0, returnQty = 0;
				MboRemote borrowLine = borrowLineSet.getMbo(i);
				if (!borrowLine.toBeDeleted()) {

					MboSetRemote invbalSet = borrowLine.getMboSet("INVBALANCES");
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
					MboRemote owner = borrowLine.getOwner();

					MboSetRemote borrowSet = owner.getMboSet("BORROWLINE");
					if (!borrowSet.isEmpty() && borrowSet.count() > 0) {
						int invbalancesid = borrowLine.getInt("invbalancesid");
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
						int udshareuselineid = borrowLine.getInt("udshareuselineid");
						for (int j = 0; returnSet.getMbo(j) != null; j++) {
							MboRemote returnLineMbo = returnSet.getMbo(j);
							int borrowid = returnLineMbo.getInt("borrowid");
							if (!returnLineMbo.toBeDeleted() && borrowid == udshareuselineid) {
								returnQty += returnLineMbo.getDouble("orderqty");
							}
						}
					}
					double shareUseQty = borrowQty - returnQty;
					if (shareUseQty > qty) {
						int invuselinenum = borrowLine.getInt("linenum");
						linenum += invuselinenum + ",";
					}
				}
			}
			if (linenum != null && !linenum.equalsIgnoreCase("")) {
				// 去掉逗号
				String params = linenum.substring(0, linenum.length() - 1);
				Object[] obj = { params };
				throw new MXApplicationException("guide", "1094", obj);
			}
		}
	}
}
