package guide.app.po;

import java.rmi.RemoteException;

import psdi.app.common.purchasing.FldPurWonum;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class FldPOLineWOnum extends FldPurWonum {


	public FldPOLineWOnum(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		String wonum = mbo.getString("wonum");
		MboSetRemote workorderSet = MXServer.getMXServer().getMboSet("WORKORDER",MXServer.getMXServer().getSystemUserInfo());
		workorderSet.setWhere("wonum='"+wonum+"'");
		workorderSet.reset();
		if(!workorderSet.isEmpty()&&workorderSet.count()>0){
			MboSetRemote assetSet = MXServer.getMXServer().getMboSet("ASSET",MXServer.getMXServer().getSystemUserInfo());
			assetSet.setWhere("assetnum='"+workorderSet.getMbo(0).getString("assetnum")+"'");
			assetSet.reset();
			if(!assetSet.isEmpty()&&assetSet.count()>0){
				mbo.setValue("udcostcenter", assetSet.getMbo(0).getString("udcostcenter"),11L); 
			}
			assetSet.close();
		}
		workorderSet.close();
	}
	
	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String udcompany = owner.getString("udcompany");
		String sql="udcompany='"+udcompany+"'";
		setListCriteria(sql);
		return super.getList();
	}

}
