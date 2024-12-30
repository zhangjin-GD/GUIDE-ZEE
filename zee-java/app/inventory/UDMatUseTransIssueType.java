package guide.app.inventory;

import guide.app.workorder.UDWO;

import java.rmi.RemoteException;

import psdi.app.inventory.FldMatUseTransIssueType;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;
/**
 *@function:ZEE-选择issuetype自动给移动类型赋默认值：issue-205、return-305
 *@date:2023-11-15 10:31:20
 *@modify:
 */
public class UDMatUseTransIssueType extends FldMatUseTransIssueType{

	public UDMatUseTransIssueType(MboValue mbv) throws MXException {
		super(mbv);
		// TODO Auto-generated constructor stub
	}

	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String udcompany = "";
		if (owner!=null && owner instanceof UDWO) {
			udcompany = owner.getString("udcompany");
		}
		if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
			String issuetype = mbo.getString("issuetype");
			String storeloc = mbo.getString("storeloc");
			if(issuetype!=null && !issuetype.equalsIgnoreCase("")){
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
		}
	}
}
