package guide.app.po;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import psdi.app.po.POLineSet;
import psdi.app.po.POLineSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDPOLineSet extends POLineSet implements POLineSetRemote {

	public UDPOLineSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet var1) throws MXException, RemoteException {
		return new UDPOLine(var1);
	}

	public void copyPoLineSet(MboSetRemote invBalancesSet) throws RemoteException, MXException {

		MboRemote invBalances = null;
		Vector vector = invBalancesSet.getSelection();
		Enumeration e = vector.elements();
		String storeloc = "";
		String udvendor = "";
		HashSet<String> hashSet = new HashSet<String>();
		MboRemote owner = this.getOwner();
		if (owner != null && owner instanceof UDPO) {
			String personid = owner.getUserInfo().getPersonId();
			MboSetRemote maxUserSet = owner.getMboSet("$MAXUSER", "MAXUSER");
			maxUserSet.setWhere("personid ='" + personid + "'");
			maxUserSet.reset();
			if (maxUserSet != null && !maxUserSet.isEmpty()) {
				MboRemote maxUser = maxUserSet.getMbo(0);
				storeloc = maxUser.getString("defstoreroom");
			}
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				udvendor = mr.getString("UDVENDOR");
				hashSet.add(udvendor);
			}
			if (hashSet.size() > 1) {
				throw new MXApplicationException("guide", "1073");
			}
			owner.setValue("udpurplat", "CPO", 11L);
			owner.setValue("vendor", udvendor, 2L);
			while (e.hasMoreElements()) {
				invBalances = (MboRemote) e.nextElement();
				MboRemote newPOLine = null;
				try {
					newPOLine = this.addAtEnd();
					if (owner != null && owner.isBasedOn("PO")) {
						newPOLine.setValue("itemnum", invBalances.getString("itemnum"), 2L);
						newPOLine.setValue("storeloc", storeloc, 2L);
						newPOLine.setValue("orderqty", invBalances.getDouble("curbal"), 2L);
						newPOLine.setValue("udtotalprice", invBalances.getDouble("udpredicttaxprice"), 2L);
						newPOLine.setValue("remark", "寄售库转库", 11L);
						newPOLine.setValue("udinvbalancesid", invBalances.getInt("invbalancesid"), 11L);
					}
				} catch (MXException mx) {
					if (newPOLine != null) {
						newPOLine.delete();
						newPOLine.getThisMboSet().remove();
					}
					throw mx;
				}
			}
		}
	}
}
