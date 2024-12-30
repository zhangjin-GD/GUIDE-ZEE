package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;


//在非订单接收中，ZEE筛选已批准服务的itemnum（udzeeitemnumser）
public class UDFldZEEItemnumSer extends MAXTableDomain{
	public UDFldZEEItemnumSer(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("item","itemnum=:"+thisAttr);
		String[] FromStr={"itemnum"};
		String[] ToStr={thisAttr};
		setLookupKeyMapInOrder(ToStr,FromStr);
	}
	
	@Override
	public MboSetRemote getList() throws MXException, RemoteException{
	MboSetRemote list = super.getList();
	MboRemote mbo=getMboValue().getMbo();
	MboRemote owner=mbo.getOwner();
	String udcompany=owner.getString("udcompany");
	String udzeeponumwx=mbo.getString("udzeeponumwx");
	if(owner!=null){
		if(udcompany!=null && udcompany.equalsIgnoreCase("ZEE")){
			list.setWhere("itemnum in (select udzeeitemnum from poline where ponum='"+udzeeponumwx+"')");
		}
		list.reset();
		}
	return list;
	}
	
	@Override
	public void action() throws RemoteException, MXException{
		super.action();
		//在非订单接收中，将已批准服务的itemnum（udzeeitemnumser）代入原有的itemnum中
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String itemnum = mbo.getString("itemnum");
		String udcompany = owner.getString("udcompany");
		String udzeeitemnumser = mbo.getString("udzeeitemnumser");

		if(udcompany!=null && udcompany.equalsIgnoreCase("ZEE"))
		{
			mbo.setValue("itemnum",udzeeitemnumser,2L);
		}
	}
}
