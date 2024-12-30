package guide.app.workorder;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldWoPersonIdList extends MboValueAdapter {

	public FldWoPersonIdList(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		
		MboRemote mbo = this.getMboValue().getMbo();
		String personIdList = mbo.getString("udpersonidlist");
		List<String> peronList = Arrays.asList(personIdList.split(","));
		MboSetRemote laborSet = mbo.getMboSet("UDWPLABOR");
		String personId = "";
		MboSetRemote laborCraftRateSet = null;
		if(peronList.size() > 0){
			laborSet.deleteAll();
		}
		for(int i = 0 ; i<peronList.size(); i++) {
			personId = peronList.get(i).toString();
			MboRemote labor = laborSet.add();
			labor.setValue("wonum", mbo.getString("wonum"), 11L);
			labor.setValue("laborcode", personId, 11L);
			
			laborCraftRateSet = mbo.getMboSet("$LABORCRAFTRATE", "LABORCRAFTRATE", "laborcode='"+personId+"'");
			setLaborCraftRate(laborCraftRateSet, labor);
		}
	
	}

	private void setLaborCraftRate(MboSetRemote laborCraftRateSet, MboRemote labor) throws RemoteException, MXException {
		MboRemote mbo = this.getMboValue().getMbo();
		String udshift = mbo.getString("udshift");
		if (!laborCraftRateSet.isEmpty() && laborCraftRateSet.count() > 0) {
			labor.setValue("unitcost", laborCraftRateSet.getMbo(0).getString("displayrate"), 11L);
		}
		if (udshift != null && !udshift.equalsIgnoreCase("")) {
			labor.setValue("udshift", udshift, 11L);
		}
	}

}
