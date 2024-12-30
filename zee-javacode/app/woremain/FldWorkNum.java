package guide.app.woremain;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;


public class FldWorkNum extends MAXTableDomain{

	public FldWorkNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("WORKORDER", "WONUM =:" + thisAttr);
		String[] FromStr = { "WONUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote workSet = mbo.getMboSet("WORKORDER");
		if (!workSet.isEmpty()) {
			MboRemote work = workSet.getMbo(0);
			mbo.setValue("WORKLEADER", work.getString("LEAD"),11L);
			mbo.setValue("ASSETNUM", work.getString("ASSETNUM"),11L);
			mbo.setValue("UDOFS", work.getString("UDOFS"),11L);
			mbo.setValue("UDCOMPANY", work.getString("UDCOMPANY"),11L);
			mbo.setValue("UDDEPT", work.getString("UDDEPT"),11L);
		}
	}
	
	public String[] getAppLink() throws MXException, RemoteException {
		
		MboRemote mbo = this.getMboValue().getMbo();
		String thisAttr = getMboValue().getAttributeName();
		String wonum = mbo.getString(thisAttr);
		String appType = getWorktype(wonum);
		if(appType == null || appType.equalsIgnoreCase("")){
			return new String[] { "" };
		}else if(appType.equalsIgnoreCase("PM") || appType.equalsIgnoreCase("IM")){
			return new String[] { "UDWOPM" };
		}else if(appType.equalsIgnoreCase("CM")){
			return new String[] { "UDWOCM" };
		}else if(appType.equalsIgnoreCase("EM")){
			return new String[] { "UDWOEM" };
		}else if(appType.equalsIgnoreCase("FM")){
			return new String[] { "UDWOFM" };
		}else {
			return super.getAppLink();
		}
		
	}
	
	private String getWorktype(String wonum) throws RemoteException, MXException {
		MboSetRemote woSet = MXServer.getMXServer().getMboSet("WORKORDER", MXServer.getMXServer().getSystemUserInfo());
		woSet.setWhere("wonum = '"+wonum+"'");
		if(!woSet.isEmpty() && woSet.count()>0){
			String typeValue = woSet.getMbo(0).getString("worktype");
			woSet.close();
			return typeValue;
		}
		woSet.close();
		return "";
	} 
	
}
