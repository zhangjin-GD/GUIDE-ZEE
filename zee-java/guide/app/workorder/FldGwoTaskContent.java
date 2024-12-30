package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldGwoTaskContent extends MboValueAdapter {

	public FldGwoTaskContent(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		
		MboRemote mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if(owner != null){
			MboSetRemote assetSet = owner.getMboSet("ASSET");
			if (!assetSet.isEmpty() && assetSet.count() > 0) {
				MboRemote asset = assetSet.getMbo(0);
				String description = owner.getString("description");
				String assetWoDesc = asset.getString("description") + "维修";
				if(description != null && assetWoDesc != null && description.equalsIgnoreCase(assetWoDesc)){
					String content = mbo.getString("content");
					if(content.length() > 166){
						content = content.substring(0, 166);
					}
					owner.setValue("description", content, 11L);
				}
			}
		}
	
	}

}
