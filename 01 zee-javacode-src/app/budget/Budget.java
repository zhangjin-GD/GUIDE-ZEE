package guide.app.budget;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;

public class Budget extends UDMbo implements MboRemote {

	public Budget(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		Date currentDate = MXServer.getMXServer().getDate();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY");
		String year = sdf.format(currentDate);
		this.setValue("budgetcost", 0, 11L);
		this.setValue("year", year, 11L);
	}

	@Override
	public void modify() throws MXException, RemoteException {
		super.modify();
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
		String personId = getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		if (isModified("BUDTYPE")) {
			MboSetRemote mboSet = getMboSet("UDCHANGEHIS");
			MboRemote add = mboSet.add(11L);
			add.setValue("ownerid", getString("UDBUDGETID"), 11L);
			add.setValue("ownertable", "UDBUDGET", 11L);
			add.setValue("attributename", "BUDTYPE", 11L);
			add.setValue("description", "类型", 11L);
			add.setValue("oldvalue", getMboValue("BUDTYPE").getPreviousValue().asString(), 11L);
			add.setValue("newvalue", getString("BUDTYPE"), 11L);
			add.setValue("changeby", personId, 11L);
			add.setValue("changedate", currentDate, 11L);
		}
		if (isModified("YEAR")) {
			MboSetRemote mboSet = getMboSet("UDCHANGEHIS");
			MboRemote add = mboSet.add(11L);
			add.setValue("ownerid", getString("UDBUDGETID"), 11L);
			add.setValue("ownertable", "UDBUDGET", 11L);
			add.setValue("attributename", "YEAR", 11L);
			add.setValue("description", "年度", 11L);
			add.setValue("oldvalue", getMboValue("YEAR").getPreviousValue().asString(), 11L);
			add.setValue("newvalue", getString("YEAR"), 11L);
			add.setValue("changeby", personId, 11L);
			add.setValue("changedate", currentDate, 11L);
		}
		if (isModified("NATURE")) {
			MboSetRemote mboSet = getMboSet("UDCHANGEHIS");
			MboRemote add = mboSet.add(11L);
			add.setValue("ownerid", getString("UDBUDGETID"), 11L);
			add.setValue("ownertable", "UDBUDGET", 11L);
			add.setValue("attributename", "NATURE", 11L);
			add.setValue("description", "性质", 11L);
			add.setValue("oldvalue", getMboValue("NATURE").getPreviousValue().asString(), 11L);
			add.setValue("newvalue", getString("NATURE"), 11L);
			add.setValue("changeby", personId, 11L);
			add.setValue("changedate", currentDate, 11L);
		}
		if (isModified("BUDITEMNUM")) {
			MboSetRemote mboSet = getMboSet("UDCHANGEHIS");
			MboRemote add = mboSet.add(11L);
			add.setValue("ownerid", getString("UDBUDGETID"), 11L);
			add.setValue("ownertable", "UDBUDGET", 11L);
			add.setValue("attributename", "BUDITEMNUM", 11L);
			add.setValue("description", "承诺项目", 11L);
			add.setValue("oldvalue", getMboValue("BUDITEMNUM").getPreviousValue().asString(), 11L);
			add.setValue("newvalue", getString("BUDITEMNUM"), 11L);
			add.setValue("changeby", personId, 11L);
			add.setValue("changedate", currentDate, 11L);
		}
		if (isModified("BUDGETCOST")) {
			MboSetRemote mboSet = getMboSet("UDCHANGEHIS");
			MboRemote add = mboSet.add(11L);
			add.setValue("ownerid", getString("UDBUDGETID"), 11L);
			add.setValue("ownertable", "UDBUDGET", 11L);
			add.setValue("attributename", "BUDGETCOST", 11L);
			add.setValue("description", "预算金额", 11L);
			add.setValue("oldvalue", getMboValue("BUDGETCOST").getPreviousValue().asString(), 11L);
			add.setValue("newvalue", getString("BUDGETCOST"), 11L);
			add.setValue("changeby", personId, 11L);
			add.setValue("changedate", currentDate, 11L);
		}
	}
}
