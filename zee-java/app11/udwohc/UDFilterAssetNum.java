package guide.app.udwohc;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;
/**
 * 用于UDWOINREPAIR弹框筛选ASSETNUM
 * @author djy
 *
 */
public class UDFilterAssetNum extends MAXTableDomain{
	public UDFilterAssetNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ASSET", "ASSETNUM=:" + thisAttr);
		String[] FromStr = { "ASSETNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote list = super.getList();
		MboRemote mbo = getMboValue().getMbo(); //虚拟框
		String itemnum = mbo.getString("itemnum");
		System.out.println("itemnum1111---"+itemnum);
		if(!itemnum.equalsIgnoreCase("") && !itemnum.isEmpty()){
			/**
			 * 修改了第33行，内部维修：assetnum可以选择所有
			 */
				list.setWhere("udcompany = 'ZEE'  ");
				list.reset();
		}
		else{
			list.setWhere("udcompany = 'ZEE' ");
			list.reset();
		}
		return list;
	}
}
