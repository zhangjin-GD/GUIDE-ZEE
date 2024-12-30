package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.server.MXServer;
import psdi.util.MXException;

public class FldRfqLineLsjg extends MboValueAdapter {

	public FldRfqLineLsjg(MboValue mbv) {
		super(mbv);
	}
	
	@Override
	public void init() throws MXException, RemoteException {
		super.init();
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String udcompany = owner.getString("udcompany");
		String itemnum = mbo.getString("itemnum");
		MboSetRemote polineSet = MXServer.getMXServer().getMboSet("poline",MXServer.getMXServer().getSystemUserInfo());
		polineSet.setWhere("ponum in (select ponum from po where udcompany='"+udcompany+"') and itemnum='"+itemnum+"' order by enterdate desc");
		polineSet.reset();
		if(!polineSet.isEmpty()&&polineSet.count()>0){
			MboRemote poline = polineSet.getMbo(0);
			String udtotalprice = poline.getString("udtotalprice");
			mbo.setValue("udlsjg", udtotalprice,11L);
		}
		polineSet.close();
	}

}
