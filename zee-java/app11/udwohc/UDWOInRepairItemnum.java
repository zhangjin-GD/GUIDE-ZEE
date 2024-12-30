package guide.app.udwohc;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDWOInRepairItemnum extends MAXTableDomain{

	public UDWOInRepairItemnum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ITEM", "ITEMNUM =:" + thisAttr);
		String[] FromStr = { "ITEMNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	@Override
	public MboSetRemote getList() throws MXException, RemoteException{
		MboSetRemote list = super.getList();
		MboRemote mbo = getMboValue().getMbo(); //虚拟框
		String wonum = mbo.getString("wonum");
		/**
		 * 修改了第30行，内部维修：repaired item可以选择所有物资
		 */
		list.setWhere(" itemnum in (select itemnum from uditemcp where udcompany = 'ZEE') ");
		list.reset();
		return list;
	}//获取itemnum
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo(); //虚拟框
		Double itemcost = 0.0D;
		String wonum = mbo.getString("wonum");
		String udisrepair = "";
		String itemnum = "";
		double quantity = 0.0D;
		MboSetRemote matusetranSet= MXServer.getMXServer().getMboSet("MATUSETRANS", MXServer.getMXServer().getSystemUserInfo());
		matusetranSet.setWhere(" ISSUETYPE='ISSUE' and refwo='"+ wonum + "' and udisrepair='1' ");
		matusetranSet.reset();
		if(!matusetranSet.isEmpty() && matusetranSet.count() > 0){
			itemcost = matusetranSet.sum("linecost");
			itemnum = matusetranSet.getMbo(0).getString("itemnum");
			quantity = matusetranSet.getMbo(0).getDouble("positivequantity");
		}
		matusetranSet.close();
		mbo.setValue("itemcost", itemcost, 11L);//给虚拟框itemcost赋值
		mbo.setValue("quantity", quantity, 11L);//给虚拟框quantity赋值
		MboSetRemote uditemcpSet= MXServer.getMXServer().getMboSet("UDITEMCP", MXServer.getMXServer().getSystemUserInfo());
		uditemcpSet.setWhere(" itemnum = '" + mbo.getString("itemnum") + "' ");
		uditemcpSet.reset();
		if(!uditemcpSet.isEmpty() && uditemcpSet.count() > 0){
			mbo.setValue("costtype", uditemcpSet.getMbo(0).getString("udcosttype"), 11L);//给虚拟框costtype赋值
		}
		uditemcpSet.close();
	}
}
