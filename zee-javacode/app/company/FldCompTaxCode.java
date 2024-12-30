package guide.app.company;

import java.rmi.RemoteException;

import psdi.app.financial.FldTaxCode;
import psdi.mbo.Mbo;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldCompTaxCode extends FldTaxCode {

	public FldCompTaxCode(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote listSet = super.getList();
		Mbo mbo = getMboValue().getMbo();
		String company = mbo.getString("udcompany");
		if ("AE03ADT".equalsIgnoreCase(company)) {
			listSet.setWhere("taxcode like 'J%'");
		} else if ("PE03CP".equalsIgnoreCase(company)) {
			listSet.setWhere("taxcode like '%R'");
		} else {
			listSet.setWhere("taxcode not like 'J%' and taxcode not like '%R'");
		}
		return listSet;
	}
}
