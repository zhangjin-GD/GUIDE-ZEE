package guide.app.inventory;

import guide.app.common.CommonUtil;
import guide.app.workorder.UDWO;

import java.rmi.RemoteException;

import psdi.app.inventory.FldMatUseTransItemNum;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
/**
 *@function:ZEE-泽港工单领料MATUSETRANS
 *@date:2023-08-15 13:31:20
 *@modify:
 */
public class UDFldMatuseItemnum extends FldMatUseTransItemNum {

	public UDFldMatuseItemnum(MboValue mbv) throws MXException {
		super(mbv);
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String udcompany = "";
		if (owner!=null && owner instanceof UDWO) {
			udcompany = owner.getString("udcompany");
		}
		if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
			String itemnum = mbo.getString("itemnum");
			String issuetype = mbo.getString("issuetype");
//			if(itemnum!=null && !itemnum.equalsIgnoreCase("") && issuetype!=null && issuetype.equalsIgnoreCase("ISSUE")) {
//				MboSetRemote fifoLocSet = MXServer.getMXServer().getMboSet("INVLIFOFIFOCOST", MXServer.getMXServer().getSystemUserInfo());
//				fifoLocSet.setWhere(" itemnum='"+itemnum+"' order by costdate asc ");
//				fifoLocSet.reset();
//				if (!fifoLocSet.isEmpty() && fifoLocSet.count() > 0) {
//					MboRemote fifoLoc = fifoLocSet.getMbo(0);
//					mbo.setValue("storeloc", fifoLoc.getString("location"), 2L);
//					
//					MboSetRemote invbalancesBinSet = MXServer.getMXServer().getMboSet("INVBALANCES", MXServer.getMXServer().getSystemUserInfo());
//					invbalancesBinSet.setWhere(" itemnum='"+itemnum+"' and location='"+mbo.getString("storeloc")+"' order by physcntdate asc ");
//					invbalancesBinSet.reset();
//					if (!invbalancesBinSet.isEmpty() && invbalancesBinSet.count() > 0) {
//						MboRemote invbalancesBin = invbalancesBinSet.getMbo(0);
//						mbo.setValue("binnum", invbalancesBin.getString("binnum"), 2L);
//					}
//					invbalancesBinSet.close();
//				}
//				fifoLocSet.close();
//				
//				MboSetRemote fifoSet = MXServer.getMXServer().getMboSet("INVLIFOFIFOCOST", MXServer.getMXServer().getSystemUserInfo());
//				fifoSet.setWhere(" itemnum='"+itemnum+"' and location='"+mbo.getString("storeloc")+"' order by costdate asc ");
//				fifoSet.reset();
//				if (!fifoSet.isEmpty() && fifoSet.count() > 0) {
//					MboRemote fifo = fifoSet.getMbo(0);
//					double unitcost = fifo.getDouble("unitcost");
//					mbo.setValue("unitcost", unitcost, 2L);
//				}
//				fifoSet.close();
//			} else {
//				mbo.setValue("storeloc", "", 2L);
//				mbo.setValue("binnum", "", 2L);
//				mbo.setValue("unitcost", "", 2L);
//			}
			//ZEE - 领料自动关联预算编号
			setOrderRequired(mbo, true);
			
			//ZEE - 选择物资后自动带出costtype 88 - 98
			if(itemnum!=null && !itemnum.equalsIgnoreCase("")){
				MboSetRemote uditemcpSet = MXServer.getMXServer().getMboSet("UDITEMCP", MXServer.getMXServer().getSystemUserInfo());
				uditemcpSet.setWhere("itemnum = '" + itemnum + "' and udcompany = 'ZEE' ");
				uditemcpSet.reset();
				if(!uditemcpSet.isEmpty() && uditemcpSet.count() > 0){
					String udcosttype = uditemcpSet.getMbo(0).getString("udcosttype");
					mbo.setValue("udcosttype", udcosttype, 11L);
				}
				uditemcpSet.close();
				//ZEE - 选择物资后，根据issuetype&库房类型的值，自动给移动类型赋予默认值：生产库issue-205、return-305,寄售库issue-405、return-505， 93-105
				if(issuetype!=null && !issuetype.equalsIgnoreCase("")){
					String storeloc = mbo.getString("storeloc");
					if(issuetype.equalsIgnoreCase("ISSUE") && storeloc.equalsIgnoreCase("ZEE-01")){
						mbo.setValue("udzeemovementtype", "205", 11L);
					}else if(issuetype.equalsIgnoreCase("ISSUE") && storeloc.equalsIgnoreCase("ZEE-02")){
						mbo.setValue("udzeemovementtype", "405", 11L);
					}else if(issuetype.equalsIgnoreCase("RETURN") && storeloc.equalsIgnoreCase("ZEE-01")){
						mbo.setValue("udzeemovementtype", "305", 11L);
					}else if(issuetype.equalsIgnoreCase("RETURN") && storeloc.equalsIgnoreCase("ZEE-02")){
						mbo.setValue("udzeemovementtype", "505", 11L);
					}else{
						mbo.setValue("udzeemovementtype", "", 11L);
					}
				}
				//ZEE - 选择物资后自动带出costcenter（默认值：如果设备存在，则取设备的成本中心，如果设备不存在则取部门的成本中心）107-138
				String assetnum = mbo.getString("assetnum");
				if(!itemnum.equalsIgnoreCase("") && itemnum != null){
						MboSetRemote assetSet = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
						assetSet.setWhere(" assetnum =  '"+assetnum+"' and udcompany = 'ZEE' ");
						assetSet.reset();
						if(!assetSet.isEmpty() && assetSet.count() > 0){
							MboRemote udasset = assetSet.getMbo(0);
							String udcostcenter = udasset.getString("udcostcenter");
							if(!udcostcenter.equalsIgnoreCase("")){
								mbo.setValue("udcostcenterasset", udcostcenter, 11L);
							}
						}
						String enterby = mbo.getString("enterby");
						MboSetRemote personSet = MXServer.getMXServer().getMboSet("PERSON", MXServer.getMXServer().getSystemUserInfo());
						personSet.setWhere(" personid ='"+enterby+"' ");
						personSet.reset();
						if(!personSet.isEmpty() && personSet.count() > 0){
							MboRemote person = personSet.getMbo(0);
							String uddept = person.getString("uddept");
							MboSetRemote uddeptSet = MXServer.getMXServer().getMboSet("UDDEPT", MXServer.getMXServer().getSystemUserInfo());
							uddeptSet.setWhere(" deptnum ='"+uddept+"' ");
							uddeptSet.reset();
							if(!uddeptSet.isEmpty() && uddeptSet.count() > 0){
								MboRemote dept = uddeptSet.getMbo(0);
								String costcenter = dept.getString("costcenter");
								if(!costcenter.equalsIgnoreCase("")){
									mbo.setValue("udcostcenterzee", costcenter, 11L);
								}
							}
						}
				}
			}
		}
	}
	
	private void setOrderRequired(MboRemote mbo, boolean setDefault) throws RemoteException, MXException{
		String sql = "1=2";
		MboRemote owner = mbo.getOwner();
		String movementType = mbo.getString("udzeemovementtype");
		sql = "materialtype='" + CommonUtil.getValue(mbo, "ITEM", "udmaterialtype") + "' and movementtype='" + movementType + "' ";
		MboSetRemote sapMappingSet = mbo.getMboSet("$UDSAPMAPPING", "UDSAPMAPPING", sql);
		if (!sapMappingSet.isEmpty() && sapMappingSet.count() > 0) {
			if (setDefault){
				mbo.setValue("udbudgetnum",CommonUtil.getBudget(owner, sapMappingSet.getMbo(0).getString("buditemnum")), 11L);
			}
//			if (!mbo.getUserInfo().getLangCode().equalsIgnoreCase("en")) {
//				if (mbo.getString("udbudgetnum") == null || mbo.getString("udbudgetnum").equalsIgnoreCase("")) {
//					throw new MXApplicationException("guide", "1015");
//				}
//			}
		}
	}
	
}
