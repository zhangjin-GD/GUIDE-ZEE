package guide.app.asset;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

public class FldAssetStopLineActDate extends MboValueAdapter
{
    public FldAssetStopLineActDate(MboValue mbv)
            throws MXException
    {
        super(mbv);
    }


    @Override
    public void initValue() throws MXException, RemoteException {
        super.initValue();

        MboRemote mbo = getMboValue().getMbo();
        Date starttime = mbo.getDate("actstarttime");
        Date endtime = mbo.getDate("actendtime");
        if ((starttime != null) && (endtime != null))
        {
            if (starttime.getTime() > endtime.getTime()) {
                throw new MXApplicationException("guide", "1072");
            }

            Long date = Long.valueOf((endtime.getTime() - starttime.getTime()) / 1000L / 60L);
            int intValue = date.intValue();
            BigDecimal b = new BigDecimal(intValue / 60.0D);
            Double hour = Double.valueOf(b.setScale(2, 4).doubleValue());

            mbo.setValue("actdate", hour.doubleValue(), 11L);
        }
    }
}
