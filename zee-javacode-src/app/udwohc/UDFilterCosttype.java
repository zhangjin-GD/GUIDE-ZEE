package guide.app.udwohc;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;
/**
 * 用于UDWOINREPAIR弹框筛选COSTTYPE
 * @author djy
 *
 */
public class UDFilterCosttype extends MAXTableDomain{
	public UDFilterCosttype(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDCOSTTYPE", "UDKOSTENSOORT=:" + thisAttr);
		String[] FromStr = { "UDKOSTENSOORT" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote list = super.getList();
		MboRemote mbo = getMboValue().getMbo(); //虚拟框
		String itemnum = mbo.getString("itemnum");
		System.out.println("itemnum1111---"+itemnum);
		if(!itemnum.equalsIgnoreCase("") && !itemnum.isEmpty()){
				list.setWhere("udmaterialtype in (select udmaterialtype from item where itemnum = '"+itemnum + "') ");
				list.reset();
		}
		else{
			list.setWhere("1 = 1");
			list.reset();
		}
		return list;
	}
	
}
