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


public class UDWFZEEPoBudget implements ActionCustomClass{
	@Override
	public void applyCustomAction(MboRemote mbo, Object[] arg1)
			throws MXException, RemoteException {
		MboSetRemote polineSet = mbo.getMboSet("POLINE");
		List<String> budgetnumList = new ArrayList<>();
		List<Integer> polinenumList = new ArrayList<>();
		List<String> nobudgetnumList = new ArrayList<>();
		String ponum = mbo.getString("ponum");
		int polinenum = 0;
		String nowf = "False";
		Double overcost = 0.0D;
		if(!polineSet.isEmpty() && polineSet.count() > 0){
			for(int i = 0; i <polineSet.count(); i++){
				MboRemote poline = polineSet.getMbo(i);
				String udbudgetnum = poline.getString("udbudgetnum");
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
	            		MboSetRemote polineSet1 = MXServer.getMXServer().getMboSet("POLINE", MXServer.getMXServer().getSystemUserInfo());
	            		polineSet1.setWhere("udbudgetnum = '"+udbudgetnum+"' and ponum = '"+ponum+"' ");
	            		polineSet1.reset();
	            		if(!polineSet1.isEmpty() && polineSet1.count() > 0){
	            			for(int j = 0; j < polineSet1.count(); j++){
	            			polinenum = polineSet1.getMbo(j).getInt("polinenum");
	            			polinenumList.add(polinenum);//记录所有不合规超预算的行号
	            			}
	            		}
		            	polineSet1.close();
	            		nowf = "True";
	            		overcost = -purrestcost;
	            		nobudgetnumList.add(udbudgetnum+"-"+"Over cost: "+overcost);
	            	}
	            }
	            udbudgetSet.close();
	        }
			HashSet<Integer> polinenumSet = new HashSet<>(polinenumList);//去重
			HashSet<String> nobudgetbnumSet = new HashSet<>(nobudgetnumList);//去重
	        if(nowf.equalsIgnoreCase("True")){
	        	Object params[] = { "Notice: It is over budget ! The budget number " + nobudgetbnumSet +" and poline Number : "+ polinenumSet+" ! "};
	        	throw new MXApplicationException( "instantmessaging", "tsdimexception",params);
	        }
		}
		polineSet.close();
	}
}
