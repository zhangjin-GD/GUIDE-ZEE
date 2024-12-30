package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDFldUDBinNum extends MAXTableDomain {

	public UDFldUDBinNum(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void validate() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String location = owner.getString("location");
		String udbinnum = mbo.getString("udbinnum");
		MboSetRemote udbinSet = MXServer.getMXServer().getMboSet("UDBIN",MXServer.getMXServer().getSystemUserInfo());
		udbinSet.setWhere("location='"+location+"' and binnum='"+udbinnum+"'");
		udbinSet.reset();
		MboSetRemote udlocbinSet = MXServer.getMXServer().getMboSet("UDLOCBIN",MXServer.getMXServer().getSystemUserInfo());
		udlocbinSet.setWhere("udbinnum in (select udbinnum from udlocbin where udbinnum='"+udbinnum+"' and udlocnum in (select udlocnum from udlocations where location='"+location+"' and status='WAPPR'))");
		udlocbinSet.reset();
		if(!udbinSet.isEmpty()&&udbinSet.count()>0){
			throw new MXApplicationException("guide", "1200");
		}
		if(!udlocbinSet.isEmpty()&&udlocbinSet.count()>0){
			throw new MXApplicationException("guide", "1201");
		}
		
		udbinSet.close();
		udlocbinSet.close();
		super.validate();
	}

}
