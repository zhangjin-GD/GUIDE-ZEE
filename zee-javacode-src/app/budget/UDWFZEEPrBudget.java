package guide.app.budget;

import guide.app.common.CommonUtil;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import psdi.common.action.ActionCustomClass;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDWFZEEPrBudget implements ActionCustomClass{
	@Override
	public void applyCustomAction(MboRemote mbo, Object[] arg1)
			throws MXException, RemoteException {
		MboSetRemote prlineSet = mbo.getMboSet("PRLINE");
		List<String> budgetnumList = new ArrayList<>();
		List<Integer> prlinenumList = new ArrayList<>();
		List<String> nobudgetnumList = new ArrayList<>();
		String prnum = mbo.getString("prnum");
		int prlinenum = 0;
		String nowf = "False";
		Double overcost = 0.0D;
		if(!prlineSet.isEmpty() && prlineSet.count() > 0){
			for(int i = 0; i <prlineSet.count(); i++){
				MboRemote prline = prlineSet.getMbo(i);
				String udbudgetnum = prline.getString("udbudgetnum");
				budgetnumList.add(udbudgetnum);
			}
			HashSet<String> budgetnumSet = new HashSet<>(budgetnumList);//去重
	        for (String udbudgetnum : budgetnumSet) {
	            MboSetRemote udbudgetSet = MXServer.getMXServer().getMboSet("UDBUDGET", MXServer.getMXServer().getSystemUserInfo());
	            udbudgetSet.setWhere(" budgetnum = '"+udbudgetnum+"'and status = 'APPR' and  year='"+CommonUtil.getCurrentDateFormat("yyyy")+"' "); //本年度的APPR预算
	            udbudgetSet.reset();
	            if(!udbudgetSet.isEmpty() && udbudgetSet.count() > 0){
	            	Double purrestcost = udbudgetSet.getMbo(0).getDouble("purrecost");//采购剩余预算总额
	            	Double budgetcost =  udbudgetSet.getMbo(0).getDouble("budgetcost");//采购预算总额
	            	if(purrestcost < 0){
	            		MboSetRemote prlineSet1 = MXServer.getMXServer().getMboSet("PRLINE", MXServer.getMXServer().getSystemUserInfo());
	            		prlineSet1.setWhere("udbudgetnum = '"+udbudgetnum+"' and prnum = '"+prnum+"' ");
	            		prlineSet1.reset();
	            		if(!prlineSet1.isEmpty() && prlineSet1.count() > 0){
	            			for(int j = 0; j < prlineSet1.count(); j++){
	            			prlinenum = prlineSet1.getMbo(j).getInt("prlinenum");
	            			prlinenumList.add(prlinenum);//记录所有不合规超预算的行号
	            			}
	            		}
		            	prlineSet1.close();
	            		nowf = "True";
	            		overcost = -purrestcost;
	            		nobudgetnumList.add(udbudgetnum+"-"+"Over cost: "+overcost);
	            	}
	            }
	            udbudgetSet.close();
	        }
			HashSet<Integer> prlinenumSet = new HashSet<>(prlinenumList);//去重
			HashSet<String> nobudgetbnumSet = new HashSet<>(nobudgetnumList);//去重
	        if(nowf.equalsIgnoreCase("True")){
	        	Object params[] = { "Notice: It is over budget ! The budget number " + nobudgetbnumSet +" and prline Number : "+ prlinenumSet+" ! "};
	        	throw new MXApplicationException( "instantmessaging", "tsdimexception",params);
	        }
		}
		prlineSet.close();
	}
}
