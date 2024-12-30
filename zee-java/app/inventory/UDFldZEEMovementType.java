package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;
/**
 *@function:ZEE-1.生产库、寄售库默认值 2.物料类型&交易类型，放大镜可选
 *@date:2023-11-22 10:31:20
 *@modify:
 */
public class UDFldZEEMovementType extends MAXTableDomain{

	public UDFldZEEMovementType(MboValue mbv) {
        super(mbv);
        String thisAttr = getMboValue().getAttributeName();
        setRelationship("UDZEEMOVEMENTTYPE", "UDMOVEMENTTYPE=:" + thisAttr);
        String[] FromStr = { "UDMOVEMENTTYPE" };
        String[] ToStr = { thisAttr };
        setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
		MboSetRemote itemSet = MXServer.getMXServer().getMboSet("ITEM", MXServer.getMXServer().getSystemUserInfo());
		itemSet.setWhere(" itemnum = '"+mbo.getString("itemnum")+"' ");
		itemSet.reset();
		String udmaterialtype = "";
		String issuetype = mbo.getString("issuetype");
		String storeloc = mbo.getString("storeloc");
		if(!itemSet.isEmpty() && itemSet.count() > 0){
			MboRemote item = itemSet.getMbo(0);
			udmaterialtype = item.getString("udmaterialtype");
		}
		if(udmaterialtype != null && !udmaterialtype.equalsIgnoreCase("")){
			if(storeloc.equalsIgnoreCase("ZEE-01") && issuetype.equalsIgnoreCase("ISSUE")){
				String udmovementtype1 = "('204','204X','205','205X','206','206X','207','207X','208','208X')";
				setListCriteria("udmaterialtype = '" + udmaterialtype + "' and udmovementtype in " + udmovementtype1 );	
			}else if(storeloc.equalsIgnoreCase("ZEE-01") && issuetype.equalsIgnoreCase("RETURN")){
				String udmovementtype2 = "('304','304X','305','305X','306','306X','307','307X','308','308X')";
				setListCriteria("udmaterialtype = '" + udmaterialtype + "' and udmovementtype in " + udmovementtype2 );	
			}else if(storeloc.equalsIgnoreCase("ZEE-02") && issuetype.equalsIgnoreCase("ISSUE")){
				String udmovementtype3 = "('405','405X')";
				setListCriteria("udmaterialtype = '" + udmaterialtype + "' and udmovementtype in " + udmovementtype3 );	
			}else if(storeloc.equalsIgnoreCase("ZEE-02") && issuetype.equalsIgnoreCase("RETURN")){
				String udmovementtype4 = "('505','505X')";
				setListCriteria("udmaterialtype = '" + udmaterialtype + "' and udmovementtype in " + udmovementtype4 );	
			}		
		}
		return super.getList();
	}
}
