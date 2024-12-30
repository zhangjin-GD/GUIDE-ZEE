package guide.app.udwohc;

import java.rmi.RemoteException;

import cn.jpush.api.utils.StringUtils;
import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDWORepairItemnum extends MAXTableDomain{

	public UDWORepairItemnum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ITEM", "ITEMNUM =:" + thisAttr);
		String[] FromStr = { "ITEMNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {  
		String sql = " ";
		MboSetRemote  list = super.getList();
		MboRemote mbo = getMboValue().getMbo(); //虚拟框
		String wonum = mbo.getString("wonum");
		MboSetRemote matusetranSet= MXServer.getMXServer().getMboSet("MATUSETRANS", MXServer.getMXServer().getSystemUserInfo());
		matusetranSet.setWhere(" ISSUETYPE='ISSUE' and refwo='"+ wonum + "' ");
		matusetranSet.reset();
		String itemnum ="";
        for (int i = 0; i < matusetranSet.count(); i++) {
        	itemnum = matusetranSet.getMbo(i).getString("itemnum")+","+itemnum;
        }
        sql = "itemnum in ("+"'"+ itemnum.replace(",", "','") +"'"+ ")";
        list.setWhere(sql);
        return list;
	}
}
