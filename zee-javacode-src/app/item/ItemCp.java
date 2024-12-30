package guide.app.item;

import guide.app.common.CommonUtil;

import java.rmi.RemoteException;

import psdi.app.item.Item;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class ItemCp extends Mbo implements MboRemote {

	public ItemCp(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if ((parent != null) && (parent instanceof Item)) {
			String itemnum = parent.getString("itemnum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			String personid = parent.getUserInfo().getPersonId();
			MboSetRemote personSet = parent.getMboSet("$PERSON", "PERSON");
			personSet.setWhere("personid ='" + personid + "'");
			personSet.reset();
			if (personSet != null && !personSet.isEmpty()) {
				MboRemote person = personSet.getMbo(0);
				String udcompany = person.getString("udcompany");
				this.setValue("udcompany", udcompany, 11L);
			}
			this.setValue("linenum", linenum, 11L);
			this.setValue("itemnum", itemnum, 11L);
		}
	}

	@Override
	public void init() throws MXException {
		super.init();

		try {
			String[] attrs = { "linenum", "purmethod", "abctype", "supplycycle", "storeloc", "isreturn", "isgeneral",
					"isspare", "udcompany", "udmatnum", "remarks", "purchaser", "keeper" };
			String company = CommonUtil.getValue("PERSON", "personid='" + getUserInfo().getPersonId() + "'",
					"udcompany");
			if (company != null && !company.equalsIgnoreCase("CSPL")
					&& !company.equalsIgnoreCase(getString("udcompany"))) {
				// setFieldFlag(attrs, 7L, true);
				setFlag(7L, true);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

}
