package guide.webclient.beans.contract;

import guide.app.common.CommonUtil;
import guide.app.po.UDPO;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import psdi.app.po.PO;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
/**
 * ZEE - 年度合同/合同创建PO、合并到已存在PO
 * 2025-1-14-10:00
 */
public class UDZEEContractlineDataBean extends DataBean {
    static String ponum = "";

    protected void initialize() throws MXException, RemoteException {
        super.initialize();
        MboRemote mbo = this.app.getAppBean().getMbo();
        MboRemote thismbo = this.getMbo(); // 虚拟表UDACCUMPO
        MboRemote owner = thismbo.getOwner(); // UDCONTRACTLINE
        if (owner != null) {
            MboRemote udcontract = owner.getMboSet("UDCONTRACT").getMbo(0); // UDCONTRACT
            setValue("vendor", udcontract.getString("vendor"), 11L);
        }
    }

    // 根据不同的contractline，新建PO
    public void UDZEECREATEPO() throws MXException, RemoteException {
        MboRemote mbo = this.app.getAppBean().getMbo(); // UDCONTRACT
        MboRemote thismbo = this.getMbo(); // 虚拟表UDACCUMPO
        MboRemote owner = thismbo.getOwner(); // UDCONTRACTLINE
        if (owner != null) {
            MboRemote udcontract = owner.getMboSet("UDCONTRACT").getMbo(0); // UDCONTRACT
            Vector<MboRemote> vec = owner.getThisMboSet().getSelection();
            String udcompany = udcontract.getString("udcompany");
            if (udcompany.equalsIgnoreCase("ZEE")) {
                if (vec.size() == 0) {
                	Object params[] = { "Please select at least one contractline data!" };
                	throw new MXApplicationException("instantmessaging", "tsdimexception",params);
//                    clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Please select at least one contractline data!", 1);
//                    return;
                } else {
                    MboRemote newpo = UDCREATEPO();
                    createOrUpdatePO(newpo, vec);
                    clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Result: Create a PO for contract, PO number is: " + ponum + "!", 1);
                }
            }
        }
    }

    // 合并到已存在PO
    public int execute() throws MXException, RemoteException {
        MboRemote mbo = this.app.getAppBean().getMbo(); // UDCONTRACT
        MboRemote thismbo = this.getMbo(); // 虚拟表UDACCUMPO
        MboRemote owner = thismbo.getOwner(); // UDCONTRACTLINE
        if (owner != null) {
        	MboRemote udcontract = owner.getMboSet("UDCONTRACT").getMbo(0); // UDCONTRACT
            Vector<MboRemote> vec = owner.getThisMboSet().getSelection();
            String udcompany = udcontract.getString("udcompany");
            if (udcompany.equalsIgnoreCase("ZEE")) {
            	if (vec.size() == 0) {
                	Object params[] = { "Please select at least one contractline data!" };
                	throw new MXApplicationException("instantmessaging", "tsdimexception",params);
//                    clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Please select at least one contractline data!", 1);
//                    return 1;
                }else {
                ponum = getString("ponum");
                MboRemote po = getExistingPO(ponum);
                if (po != null) {
                    createOrUpdatePO(po, vec);
                    clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Tips: " + vec.size() + " ContractLines has been added to " + ponum, 1);
	                }
	            }
	        }
        }
        return 1;
    }

    private MboRemote getExistingPO(String ponum) throws MXException, RemoteException {
        MboSetRemote poSet = MXServer.getMXServer().getMboSet("PO", MXServer.getMXServer().getSystemUserInfo());
        poSet.setWhere("ponum='" + ponum + "'");
        poSet.reset();
        return poSet.isEmpty() ? null : poSet.getMbo(0);
    }

    private void createOrUpdatePO(MboRemote po, Vector<MboRemote> vec) throws MXException, RemoteException {
        for (MboRemote selectMbo : vec) {
            MboSetRemote polineSet = po.getMboSet("POLINE");
            MboRemote poline = polineSet.add();
            String itemnum = selectMbo.getString("itemnum");
            String description = selectMbo.getString("description");
            String udcosttype = selectMbo.getString("udcosttype");
            Double orderqty = selectMbo.getDouble("orderqty");
            String orderunit = selectMbo.getString("orderunit");
            Double udroundfactor = selectMbo.getDouble("udroundfactor");
            Double udconversion = selectMbo.getDouble("udconversion");
            String tax1code = selectMbo.getString("tax1code");
            Double uddiscountprice = selectMbo.getDouble("uddiscountprice");
            Double unitcost = selectMbo.getDouble("unitcost");
            int udcontractlineid = selectMbo.getInt("udcontractlineid");

            String linetype = selectMbo.getString("linetype");
            poline.setValue("linetype", linetype, 2L);
            if (linetype.equalsIgnoreCase("ITEM")) {
                poline.setValue("itemnum", itemnum, 2L);
                poline.setValue("storeloc", getValueStoreroom(itemnum), 2L);
                poline.setValue("udcostcenterzee", getValueCostcenter(itemnum, selectMbo), 11L);
            } else if (linetype.equalsIgnoreCase("SERVICE")) {
                poline.setValue("itemnum", "", 2L);
                poline.setValue("description", description, 11L);
                poline.setValue("location", "LOCZEE", 11L); // 避免因为没有location而中止保存，给location设置默认值
            }

            if (udcosttype != null && !udcosttype.equalsIgnoreCase("")) {
                poline.setValue("udcosttype", udcosttype, 2L);
            } else  {
                        poline.setValue("udcosttype", getValueCosttype(itemnum), 2L);
            }

            poline.setValue("orderqty", orderqty, 2L);
            poline.setValue("orderunit", orderunit, 11L);
            if(!String.valueOf(udroundfactor).equals("") && udroundfactor != 0){
            	poline.setValue("udroundfactor", udroundfactor, 11L);
            }     
            if(!String.valueOf(udconversion).equals("") && udconversion != 0){
                poline.setValue("conversion", udconversion, 11L);
            }else {
            	poline.setValue("conversion", "1", 11L);
            }
            poline.setValue("tax1code", tax1code, 2L);
            poline.setValue("unitcost", uddiscountprice != null ? uddiscountprice : unitcost, 2L);
            poline.setValue("udcontractlineid", udcontractlineid, 2L);

            polineSet.save();
        }
    }

    public MboRemote UDCREATEPO() throws RemoteException, MXException {
        MboRemote po = null;
        Date sysdate = MXServer.getMXServer().getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sysdateStr = sdf.format(sysdate);
        MboRemote udmbo = this.app.getAppBean().getMbo(); // UDCONTRACT
        MboRemote owner = this.getMbo().getOwner(); // UDCONTRACTLINE
        MboRemote mbo = owner.getMboSet("UDCONTRACT").getMbo(0); // UDCONTRACT
        String gconnum = mbo.getString("gconnum");
        String description = mbo.getString("description");
        String vendor = mbo.getString("vendor");
        String purchaseagent = mbo.getString("purchaseagent");
        String udcompany = mbo.getString("udcompany");
        String uddept = mbo.getString("uddept");
        String status = mbo.getString("status");
        String personId = mbo.getUserInfo().getPersonId();
        if (udcompany.equalsIgnoreCase("ZEE") && status.equalsIgnoreCase("APPR")) {
            MboSetRemote newpoSet = MXServer.getMXServer().getMboSet("PO", MXServer.getMXServer().getSystemUserInfo());
            newpoSet.setWhere("1=2");
            UDPO newpo = (UDPO) newpoSet.add();
            ponum = newpo.getString("ponum");
            newpo.setValue("udapptype", "POZEE", 11L);
            newpo.setValue("description", "Contract: '" + gconnum + "' " + description, 11L);
            newpo.setValue("udpurplat", "Annual Contract", 11L);
            newpo.setValue("vendor", vendor, 2L);
            newpo.setValue("orderdate", sysdate, 11L); // 创建时间
            newpo.setValue("purchaseagent", purchaseagent, 11L);
            newpo.setValue("udcreateby", personId, 11L);
            newpo.setValue("udcompany", udcompany, 11L);
            newpo.setValue("uddept", uddept, 11L);
            newpo.setValue("udcurrency", "EUR", 2L);
            newpo.setValue("udcreatetime", sysdate, 11L);
            newpoSet.save();
            newpoSet.close();
            po = newpo;
            }
            return po;
            }
    
	public String getValueCosttype(String itemnum) throws RemoteException, MXException{
		String udcosttype = "";
		MboSetRemote uditemcpSet = MXServer.getMXServer().getMboSet("UDITEMCP", MXServer.getMXServer().getSystemUserInfo());
		uditemcpSet.setWhere(" udcompany='ZEE' and itemnum='"+itemnum+"' ");
		uditemcpSet.reset();
		if (!uditemcpSet.isEmpty() && uditemcpSet.count() > 0) {
			MboRemote uditemcp = uditemcpSet.getMbo(0);
			udcosttype = uditemcp.getString("udcosttype");
		}
		uditemcpSet.close();
		return udcosttype;
	}
	
	public String getValueCostcenter(String itemnum,MboRemote mbo) throws RemoteException, MXException{
		String linetype = mbo.getString("linetype");
		String costcenter = "";
		String deptnum = "";
		MboSetRemote uditemcpSet = MXServer.getMXServer().getMboSet("UDITEMCP", MXServer.getMXServer().getSystemUserInfo());
		uditemcpSet.setWhere(" udcompany='ZEE' and itemnum='"+itemnum+"' ");
		uditemcpSet.reset();
		if (!linetype.equalsIgnoreCase("") && linetype.equalsIgnoreCase("ITEM")) {
			if (!uditemcpSet.isEmpty() && uditemcpSet.count() > 0) {
				MboRemote uditemcp = uditemcpSet.getMbo(0);
				deptnum = uditemcp.getString("dept");
			}
			MboSetRemote uddeptSet = MXServer.getMXServer().getMboSet("UDDEPT",MXServer.getMXServer().getSystemUserInfo());
			uddeptSet.setWhere("deptnum = '" + deptnum + "' ");
			uddeptSet.reset();
			if (!uddeptSet.isEmpty() && uddeptSet.count() > 0) {
				MboRemote dept = uddeptSet.getMbo(0);
				costcenter = dept.getString("costcenter");
			}
			uddeptSet.close();
		}
		return costcenter;
	}
	
	public String getValueStoreroom(String itemnum) throws RemoteException, MXException{
				String storeloc = "";
				MboSetRemote uditemcpSet = MXServer.getMXServer().getMboSet("UDITEMCP",MXServer.getMXServer().getSystemUserInfo());
				uditemcpSet.setWhere(" itemnum = '" + itemnum+ "' and udcompany = 'ZEE' ");
				uditemcpSet.reset();
				if (!uditemcpSet.isEmpty() && uditemcpSet.count() > 0) {
					MboRemote uditemcp = uditemcpSet.getMbo(0);
					storeloc = uditemcp.getString("storeloc");
				}else{
					storeloc = "ZEE-01";
				}
				uditemcpSet.close();
				return storeloc;
	}
    
}