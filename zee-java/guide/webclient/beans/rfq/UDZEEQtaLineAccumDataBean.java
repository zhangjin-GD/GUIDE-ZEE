package guide.webclient.beans.rfq;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;


/**
 * ZEE - RFQVENDOR:合并已授予供应商line到已存在PO
 * DJY
 * 2024-10-25 14:02
 **/
public class UDZEEQtaLineAccumDataBean extends DataBean{

	protected void initialize() throws MXException, RemoteException {
		super.initialize();
		MboRemote mbo = this.app.getAppBean().getMbo(); //UDRFQ
		MboRemote thismbo = this.getMbo(); //虚拟表UDACCUMPO
		MboRemote owner = thismbo.getOwner(); //RFQVENDOR
						
		if (owner!=null) {			
			setValue("vendor", owner.getString("vendor"), 11L);
					
			MboSetRemote poSet0 = MXServer.getMXServer().getMboSet("PO", MXServer.getMXServer().getSystemUserInfo());
			poSet0.setWhere("udcompany='ZEE' and status='WAPPR' and vendor ='"+owner.getString("vendor")+"' ");
			poSet0.reset();
			//poSet0为空校验：该供应商没有待审批的PO
			if(poSet0.isEmpty()){
				Object str1[] = { " No this vendor's relevant existing waiting approved po here! "};
				throw new MXApplicationException("instantmessaging", "tsdimexception",str1);
			}
			
			MboSetRemote quotationlineSet0 =  MXServer.getMXServer().getMboSet("QUOTATIONLINE", MXServer.getMXServer().getSystemUserInfo());
			quotationlineSet0.setWhere(" rfqnum = '"+mbo.getString("rfqnum")+"' ");
			quotationlineSet0.reset();
			//quotationlineSet0为空校验：没有询价行
			if(quotationlineSet0.isEmpty()){
				Object str3[] = { " No quotation lines here! "};
				throw new MXApplicationException("instantmessaging", "tsdimexception",str3);
			}
			
			MboSetRemote quotationlineSet =  MXServer.getMXServer().getMboSet("QUOTATIONLINE", MXServer.getMXServer().getSystemUserInfo());
			quotationlineSet.setWhere(" rfqnum = '"+mbo.getString("rfqnum")+"' and isawarded = '1' and vendor = '"+thismbo.getString("vendor")+"' ");
			quotationlineSet.reset();			
			//quotationlineSet为空校验：没有被授予的询价行
			if(quotationlineSet.isEmpty()){
				Object str4[] = { " No awarded quotation lines here! "};
				throw new MXApplicationException("instantmessaging", "tsdimexception",str4);
			}
		}
	}
	
	//合并到已存在PO
	public int execute() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo(); //UDRFQ
		MboRemote thismbo = this.getMbo(); //虚拟表UDACCUMPO
		MboRemote owner = thismbo.getOwner(); //RFQVENDOR
		
		if (owner!=null) {			
			//虚拟表UDACCUMPO-ponum为空校验:弹框没有选择ponum
			if(thismbo.getString("ponum").equalsIgnoreCase("")){
				Object str2[] = { " No selected ponum here! "};
				throw new MXApplicationException("instantmessaging", "tsdimexception",str2);
			}

			String ponum = thismbo.getString("ponum");
			String rfqnum = mbo.getString("rfqnum");
			MboRemote po = null;
			MboRemote quotationline = null;
			MboRemote rfqline = null;
			MboRemote prline = null;
			MboSetRemote polineSet = null;
			MboRemote poline = null;
			
			MboSetRemote poSet = MXServer.getMXServer().getMboSet("PO", MXServer.getMXServer().getSystemUserInfo());
			poSet.setWhere(" ponum='"+ponum+"' ");
			poSet.reset();
			
			if (!poSet.isEmpty() && poSet.count() > 0) {
				po = poSet.getMbo(0);
				polineSet = po.getMboSet("POLINE");

				MboSetRemote quotationlineSet =  MXServer.getMXServer().getMboSet("QUOTATIONLINE", MXServer.getMXServer().getSystemUserInfo());
				quotationlineSet.setWhere(" rfqnum = '"+rfqnum+"' and isawarded = '1' and vendor = '"+thismbo.getString("vendor")+"' ");
				quotationlineSet.reset();
				if(!quotationlineSet.isEmpty() && quotationlineSet.count() > 0){										
					for(int i = 0; i<quotationlineSet.count(); i++){
						quotationline = quotationlineSet.getMbo(i);//相关的quotatioinline
						int rfqlinenum = quotationline.getInt("rfqlinenum");
						MboSetRemote rfqlineSet =  MXServer.getMXServer().getMboSet("RFQLINE", MXServer.getMXServer().getSystemUserInfo());
						rfqlineSet.setWhere(" rfqnum = '"+rfqnum+"'  and rfqlinenum ='"+rfqlinenum+"' ");
						rfqlineSet.reset();						
						if(!rfqlineSet.isEmpty() && rfqlineSet.count() > 0){
							rfqline = rfqlineSet.getMbo(0);//相关quotatioinline的rfqline
						}
						MboSetRemote prlineSet =  MXServer.getMXServer().getMboSet("PRLINE", MXServer.getMXServer().getSystemUserInfo());
						prlineSet.setWhere(" rfqnum = '"+rfqnum+"'  and rfqlinenum ='"+rfqlinenum+"' ");
						prlineSet.reset();
						if(!prlineSet.isEmpty() && prlineSet.count() > 0){
							prline = prlineSet.getMbo(0);//相关quotatioinline的prline
						}						
						if(!polineSet.isEmpty() && polineSet.count() > 0){
							poline = polineSet.add();
							poline.setValue("itemnum", rfqline.getString("itemnum"), 2L);
							poline.setValue("udprojectnum", rfqline.getString("udprojectnum"), 11L);
							poline.setValue("udbudgetnum", rfqline.getString("udbudgetnum"), 11L);
							poline.setValue("gldebitacct", "COSCO", 2L);
							poline.setValue("udcapex", rfqline.getString("udcapex"), 11L);
							poline.setValue("udcosttype", rfqline.getString("udcosttype"), 2L);
							po.setValue("udprojectnum", rfqline.getString("udprojectnum"), 11L);
							po.setValue("udcapex", rfqline.getString("udcapex"), 11L);
							poline.setValue("udcostcenterzee", rfqline.getString("PRLINE.udcostcenterzee"),11L);
							poline.setValue("udglzee", rfqline.getString("PRLINE.udglzee"),11L);
							poline.setValue("udcosttype", rfqline.getString("PRLINE.udcosttype"),2L);
							poline.setValue("tax1code", rfqline.getString("PRLINE.tax1code"),11L);
							poline.setValue("tax1code", quotationline.getString("tax1code"),2L);
							poline.setValue("unitcost", quotationline.getDouble("unitcost"),2L);
							if(!prline.getString("conversion").equalsIgnoreCase("")){
							poline.setValue("conversion", prline.getString("conversion"),2L);
							}
							poline.setValue("storeloc", rfqline.getString("storeloc"),11L);
						}	
					}
				}
				polineSet.save();
				clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Tips: "+quotationlineSet.count()+" quotationlines have been add to "+ponum+" ! ", 1);
			}

		}		
		return 1;		
	}
}
