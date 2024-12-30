package guide.app.item;

import java.rmi.RemoteException;

import psdi.app.item.Item;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class UDFldUdItemcpVen extends Mbo implements MboRemote {

	public UDFldUdItemcpVen(MboSet ms) throws RemoteException {
		super(ms);
		// TODO Auto-generated constructor stub
	}

	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote owner = getOwner();
		if ((owner != null) && (owner instanceof Item)) {
			String itemnum = owner.getString("itemnum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			String personid = owner.getUserInfo().getPersonId();
			MboSetRemote personSet = owner.getMboSet("$PERSON", "PERSON");
			personSet.setWhere("personid ='" + personid + "'");
			personSet.reset();
			if (personSet != null && !personSet.isEmpty() && personSet.count() > 0){
				for (int i = 0; i < personSet.count(); i++){
				MboRemote person = personSet.getMbo(i);
				String udcompany = person.getString("udcompany");
				this.setValue("udcompany", udcompany, 11L);

				}
				this.setValue("itemnum",itemnum,11L);
				this.setValue("linenum", linenum, 11L);
			}
		}

	}
	//UDITEMCPVEN表ZEE选择ACTIVE的供应商后给UDITEMCP表的供应商赋值
	public void save() throws MXException, RemoteException{
		super.save();
		String AppName = getOwner().getThisMboSet().getApp();
		String udcompany = getString("udcompany");
		if(AppName.equalsIgnoreCase("UDITEM") && udcompany.equalsIgnoreCase("ZEE")){
		   if(getOwner()==null ){
			     return ;
		    }
		String itemnum = getString("itemnum");
		boolean udactive = getBoolean("udactive");
		MboSetRemote uditemcpSet = getMboSet( "UDITEMCP");	
		uditemcpSet.setWhere("itemnum ='" + itemnum + "' and udcompany = '"+udcompany+"'");
		uditemcpSet.reset();
		if (uditemcpSet != null && !uditemcpSet.isEmpty()){
			for(int i = 0; i < uditemcpSet.count(); i++){
			MboRemote uditemcp = uditemcpSet.getMbo(0);
			if(udactive){
				String vendor = getString("vendor");
				uditemcp.setValue("vendor",vendor,11L);
			}
			}
		}
		uditemcpSet.close();
	}
}
}
