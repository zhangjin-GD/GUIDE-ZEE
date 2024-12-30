package guide.webclient.beans.pr;

import guide.app.pr.UDPR;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class UDPRVendorTableBean extends DataBean{
	/**
	 * 
	 *  ZEE- PR创建PO（根据供应商list整单/逐行多个、单个）
	 *  DJY
	 *  2024/7/4
	 * 18-126
	 */
	//整单创建
	public int udaddallpo() throws MXException, RemoteException {
		UDPR owner = (UDPR) this.app.getAppBean().getMbo();	
		 List<String> ponumlist = new ArrayList<String>();
		 List<String> vendorlist = new ArrayList<String>();
		 List<String> mergelist = new ArrayList<String>();
		 List<String> checklist = new ArrayList<String>();
		 List<String> list1 = new ArrayList<String>();
		 List<String> list2 = new ArrayList<String>();
//		 List<String> list3 = new ArrayList<String>();
//		 List<String> list4 = new ArrayList<String>();
		MboSetRemote prvendorSet = owner.getMboSet("UDPRVENDOR");
		if(prvendorSet.isEmpty()){//供应商list为空，则不允许创建
			Object str0[] = { " No vendors here, you cannot create PO ! "};
			throw new MXApplicationException("instantmessaging", "tsdimexception",str0);
		}
		if(!owner.getString("status").equalsIgnoreCase("APPR") && !owner.getString("status").equalsIgnoreCase("RELEASE")){//PO状态不是APPR或RELEASE，则不允许创建
			Object str1[] = { " You can only create PO if not approved or released! "};
			throw new MXApplicationException("instantmessaging", "tsdimexception",str1);
		}
		//如果PR勾选了使用RFQ则不允许在PR里点击创建PO按钮
		if(owner.getString("udmakerfq").equalsIgnoreCase("Y")){
			Object str2[] = { " You can only create PO because you tick 'using RFQ' !"};
			throw new MXApplicationException("instantmessaging", "tsdimexception",str2);
		}
		if(!prvendorSet.isEmpty() && prvendorSet.count() > 0){//整单创建只要有重复创建的供应商行，则不允许创建，并且弹框告知供应商编号
			for(int i = 0; i< prvendorSet.count(); i++ ){
				MboRemote prvendor = prvendorSet.getMbo(i);
				if(prvendor.getString("udcreated").equalsIgnoreCase("Y")){
					checklist.add(prvendor.getString("vendor"));
				}
			}
			if(!checklist.isEmpty()){	
				Object str2[] = { " You cannot create PO duplicately for these vendors: " +checklist+" ! Maybe create batch po is a better choice ! "};
				throw new MXApplicationException("instantmessaging", "tsdimexception",str2);
			}
		}
		//如果PR供应商list的所有供应商属于非合同供应商，则不允许创建
		if(!prvendorSet.isEmpty() && prvendorSet.count() > 0){
			for(int i = 0; i< prvendorSet.count(); i++ ){
				MboRemote prvendor = prvendorSet.getMbo(i);
				String convendor = prvendor.getString("vendor");
				String prnum = prvendor.getString("prnum");
				
					MboSetRemote udcontractSet = MXServer.getMXServer().getMboSet("UDCONTRACT", MXServer.getMXServer().getSystemUserInfo());
					udcontractSet.setWhere("vendor = '"+convendor+"' and status = 'APPR' and  to_char(sysdate,'yyyy-mm-dd')>= to_char(startdate,'yyyy-mm-dd') and to_char(sysdate,'yyyy-mm-dd')<= to_char(enddate,'yyyy-mm-dd') ");
					udcontractSet.reset();
					if(!udcontractSet.isEmpty() && udcontractSet.count() > 0){
						MboRemote udcontract = udcontractSet.getMbo(0);
						MboSetRemote udcontractlineSet = udcontract.getMboSet("UDCONTRACTLINE");
						if(!udcontractlineSet.isEmpty() && udcontractlineSet.count() > 0){
						for(int j = 0; j< udcontractlineSet.count(); j++){
							list1.add(udcontractlineSet.getMbo(j).getString("itemnum"));
						    }
						}
					}
					MboSetRemote prlineSet = MXServer.getMXServer().getMboSet("PRLINE", MXServer.getMXServer().getSystemUserInfo());
					prlineSet.setWhere("prnum ='"+prnum+"' and udprevendor = '"+convendor+"' ");
					prlineSet.reset();
					if(!prlineSet.isEmpty() && prlineSet.count() > 0){
						for(int j = 0; j< prlineSet.count(); j++){
							list2.add(prlineSet.getMbo(j).getString("itemnum"));
						}
					}
			}
			 List<String> list3 = new ArrayList<String>(list2);
			 list3.removeAll(list1);//list3存放非合同的itemnum
			 
//				MboSetRemote prlineSet1 = MXServer.getMXServer().getMboSet("PRLINE", MXServer.getMXServer().getSystemUserInfo());
//				prlineSet1.setWhere("prnum ='"+prnum+"' and (udprevendor is null or udprevendor='') ");
//				prlineSet1.reset();
//				if(!prlineSet1.isEmpty() && prlineSet1.count() > 0){
//					for(int j = 0; j< prlineSet1.count(); j++){
//						list3.add(prlineSet1.getMbo(j).getString("udprevendor"));
//					}
//				}
			 
			 HashSet<String> hashlist3 = new HashSet<>(list3);//hashlist3存放非合同的itemnum（去重）
			 List<String> list4 = new ArrayList<String>(list2);
			 list4.retainAll(list1);//list4存放合同的itemnum
				if(!hashlist3.isEmpty()){	
					clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", " You can not directly create PO if these items don't belong to contract! "+hashlist3, 1);
				}
		}
		
		
		if(!prvendorSet.isEmpty() && prvendorSet.count() > 0 ){//整单创建PO
			for(int i = 0; i < prvendorSet.count(); i++){
				MboRemote prvendor = prvendorSet.getMbo(i);
				prvendor.setValue("isawarded", "1", 2L);
				boolean isawarded = prvendor.getBoolean("isawarded");
				if (isawarded) {//所有授予的供应商给PR主表供应商赋值
				String vendor = prvendor.getString("vendor");
				owner.setValue("vendor", vendor, 11L);	
				//
				MboSetRemote oldprlineSet = MXServer.getMXServer().getMboSet("PRLINE", MXServer.getMXServer().getSystemUserInfo());
				oldprlineSet.setWhere("prnum = '"+prvendor.getString("prnum")+"' and udprevendor = '"+vendor+"' "
						+ "and itemnum in (select itemnum from udcontractline where gconnum in (select gconnum from udcontract where vendor = '"+vendor+"' and status = 'APPR' and  to_char(sysdate,'yyyy-mm-dd')>= to_char(startdate,'yyyy-mm-dd') and to_char(sysdate,'yyyy-mm-dd')<= to_char(enddate,'yyyy-mm-dd') "
						+ ")"
						+ ")");
				oldprlineSet.reset();
				if(!oldprlineSet.isEmpty() && oldprlineSet.count() > 0){
				String ponum = owner.addAllPOFromPR(owner.getString("description"));
				this.app.getAppBean().save();
				ponumlist.add(ponum);
				vendorlist.add(prvendor.getString("vendor"));	
				prvendor.setValue("udcreated", "1", 11L);
				prvendorSet.save();
				}
				}
			}
			for(int i = 0; i< vendorlist.size(); i++){//拼接ponum和vendor合并格式
				mergelist.add(ponumlist.get(i)+"_"+vendorlist.get(i));
			}	
			if(!mergelist.isEmpty()){
			clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Result： Create all PO , POnum_Vendor :  "+mergelist, 1);
				}
			}
		return 1;
	}

	//逐行分批创建PO
		public int udaddonepo() throws MXException, RemoteException{
			UDPR owner = (UDPR) this.app.getAppBean().getMbo();	
			 List<String> ponumslist = new ArrayList<String>();
			 List<String> vendorslist = new ArrayList<String>();
			 List<String> mergeslist = new ArrayList<String>();	
			 List<String> checkslist = new ArrayList<String>();
			 List<String> list1 = new ArrayList<String>();
			 List<String> list2 = new ArrayList<String>();
			 MboSetRemote prvendorSet = owner.getMboSet("UDPRVENDOR");
			 MboSetRemote prvendorSet1 = owner.getMboSet("UDPRVENDOR");
			 prvendorSet1.setWhere(" prnum ='"+owner.getInt("prnum")+"' and isawarded = '1' ");
			 prvendorSet1.reset();
				if(prvendorSet1.isEmpty()){//供应商list相应行未被授予，则不允许创建
					Object str0[] = { " No awarded vendor here, you cannot create PO ! "};
					throw new MXApplicationException("instantmessaging", "tsdimexception",str0);
				}
				if(prvendorSet.isEmpty()){//供应商list为空，则不允许创建
					Object str0[] = { " No vendors here, you cannot create PO ! "};
					throw new MXApplicationException("instantmessaging", "tsdimexception",str0);
				}
				if(!owner.getString("status").equalsIgnoreCase("APPR") && !owner.getString("status").equalsIgnoreCase("RELEASE")){//PO状态不是APPR或RELEASE，则不允许创建
					Object str1[] = { " You can only create PO if not approved or released! "};
					throw new MXApplicationException("instantmessaging", "tsdimexception",str1);
				}
				//如果PR勾选了使用RFQ则不允许在PR里点击创建PO按钮
				if(owner.getString("udmakerfq").equalsIgnoreCase("Y")){
					Object str2[] = { " You can only create PO because you tick 'using RFQ' !"};
					throw new MXApplicationException("instantmessaging", "tsdimexception",str2);
				}
				if(!prvendorSet.isEmpty() && prvendorSet.count() > 0){//逐行创建只要有选中重复创建的供应商行，则不允许创建，并且弹框告知供应商编号
					for(int i = 0; i< prvendorSet.count(); i++ ){
						MboRemote prvendor = prvendorSet.getMbo(i);
						if(prvendor.getString("udcreated").equalsIgnoreCase("Y") && prvendor.getString("isawarded").equalsIgnoreCase("Y")){
							checkslist.add(prvendor.getString("vendor"));
						}
					}
					if(!checkslist.isEmpty()){	
						Object str2[] = { " You cannot create PO duplicately for these vendors: " +checkslist+" ! "};
						throw new MXApplicationException("instantmessaging", "tsdimexception",str2);
					}
				}
				
				//如果PR供应商list的所有供应商属于非合同供应商，则不允许创建
				if(!prvendorSet.isEmpty() && prvendorSet.count() > 0){
					for(int i = 0; i< prvendorSet.count(); i++ ){
						MboRemote prvendor = prvendorSet.getMbo(i);
						String convendor = prvendor.getString("vendor");
						String prnum = prvendor.getString("prnum");
						
							MboSetRemote udcontractSet = MXServer.getMXServer().getMboSet("UDCONTRACT", MXServer.getMXServer().getSystemUserInfo());
							udcontractSet.setWhere("vendor = '"+convendor+"' and status = 'APPR' and  to_char(sysdate,'yyyy-mm-dd')>= to_char(startdate,'yyyy-mm-dd') and to_char(sysdate,'yyyy-mm-dd')<= to_char(enddate,'yyyy-mm-dd') ");
							udcontractSet.reset();
							if(!udcontractSet.isEmpty() && udcontractSet.count() > 0){
								String gconnum = udcontractSet.getMbo(0).getString("gconnum");
								MboSetRemote udcontractlineSet = MXServer.getMXServer().getMboSet("UDCONTRACTLINE", MXServer.getMXServer().getSystemUserInfo());
								udcontractlineSet.setWhere("gconnum = '"+gconnum+"' ");
								udcontractlineSet.reset();
								if(!udcontractlineSet.isEmpty() && udcontractlineSet.count() > 0){
								for(int j = 0; j< udcontractlineSet.count(); j++){
									list1.add(udcontractlineSet.getMbo(j).getString("itemnum"));
								    }
								}
							}
							MboSetRemote prlineSet = MXServer.getMXServer().getMboSet("PRLINE", MXServer.getMXServer().getSystemUserInfo());
							prlineSet.setWhere("prnum ='"+prnum+"' and udprevendor = '"+convendor+"' ");
							prlineSet.reset();
							if(!prlineSet.isEmpty() && prlineSet.count() > 0){
								for(int j = 0; j< prlineSet.count(); j++){
									list2.add(prlineSet.getMbo(j).getString("itemnum"));
								    }	
							}
					}
					 List<String> list3 = new ArrayList<String>(list2);
					 list3.removeAll(list1);//list3存放非合同的itemnum
					 HashSet<String> hashlist3 = new HashSet<>(list3);//hashlist3存放非合同的itemnum（去重）
					 List<String> list4 = new ArrayList<String>(list2);
					 list4.retainAll(list1);//list4存放合同的itemnum
						if(!hashlist3.isEmpty()){	
							clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", " You can not directly create PO if these items don't belong to contract! "+hashlist3, 1);
						}
				}
				
			 if(!prvendorSet.isEmpty() && prvendorSet.count() > 0 ){
					for(int i = 0; i < prvendorSet.count(); i++){
						MboRemote prvendor = prvendorSet.getMbo(i);
						 boolean isawarded = prvendor.getBoolean("isawarded");
						if (isawarded) {//获取已授予的每一行vendor，给主表PR的vendor赋值
							String vendor = prvendor.getString("vendor");
							owner.setValue("vendor", vendor, 11L);	
							//							
							MboSetRemote oldprlineSet = MXServer.getMXServer().getMboSet("PRLINE", MXServer.getMXServer().getSystemUserInfo());
							oldprlineSet.setWhere("prnum = '"+prvendor.getString("prnum")+"' and udprevendor = '"+vendor+"' "
									+ "and itemnum in (select itemnum from udcontractline where gconnum in (select gconnum from udcontract where vendor = '"+vendor+"' and status = 'APPR' and  to_char(sysdate,'yyyy-mm-dd')>= to_char(startdate,'yyyy-mm-dd') and to_char(sysdate,'yyyy-mm-dd')<= to_char(enddate,'yyyy-mm-dd') "
									+ ")"
									+ ")");
							oldprlineSet.reset();
						if(!oldprlineSet.isEmpty() && oldprlineSet.count() > 0){
						String ponum = owner.addAllPOFromPR(owner.getString("description"));
						this.app.getAppBean().save();
						ponumslist.add(ponum);
						vendorslist.add(prvendor.getString("vendor"));	
						prvendor.setValue("udcreated", "1", 11L);
						prvendorSet.save();
						} 
						}
					}
					for(int i = 0; i< vendorslist.size(); i++){//拼接ponum和vendor合并格式
						mergeslist.add(ponumslist.get(i)+"_"+vendorslist.get(i));
					}	
					if(!mergeslist.isEmpty()){
					clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Result： Create PO , POnum_Vendor :  "+mergeslist, 1);
			 }
			 }
			return 1;
		}
}
