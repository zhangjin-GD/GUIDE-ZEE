package guide.app.invoice;

import java.rmi.RemoteException;

import psdi.app.invoice.FldInvoiceLinePOLineNum;
import psdi.app.invoice.InvoiceCost;
import psdi.app.invoice.InvoiceCostSetRemote;
import psdi.app.invoice.InvoiceLineRemote;
import psdi.app.invoice.InvoiceLineSet;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.SqlFormat;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 * 
 * @function:
 * @author:YS
 * @modify:
 */
public class UDFldInvoiceLinePOLineNum extends FldInvoiceLinePOLineNum
{
  public UDFldInvoiceLinePOLineNum(MboValue mbv)
    throws MXException, RemoteException
  {
    super(mbv);
    setRelationship("POLine", "ponum=:ponum and polinenum=:polinenum and siteid=:positeid ");
    setLookupKeyMapInOrder(new String[] { "positeid", "porevisionnum", "ponum", "polinenum" }, new String[] { "siteid", "revisionnum", "ponum", "polinenum" });
  }

  public void action()
    throws MXException, RemoteException
  {
    InvoiceLineRemote mbo = (InvoiceLineRemote)getMboValue().getMbo();

    MboRemote po = null;
    po = ((InvoiceLineSet)mbo.getThisMboSet()).getPOForQueryOnly(mbo.getString("ponum"), mbo.getString("positeid"), mbo.getString("porevisionnum"));
    if (po == null)
    {
      String poWappr = getTranslator().toExternalList("POSTATUS", "WAPPR", mbo);//添加的
      String poAppr = getTranslator().toExternalList("POSTATUS", "APPR", mbo);
      String poInprg = getTranslator().toExternalList("POSTATUS", "INPRG", mbo);
      String poClose = getTranslator().toExternalList("POSTATUS", "CLOSE", mbo);

      String statuses = poAppr + ", " + poInprg + ", " + poClose + ", " + poWappr;//添加的poWappr

      String query = "status in (" + statuses + ") and ponum = :1 and siteid = :2";
      
      SqlFormat sqfNew = new SqlFormat(mbo.getUserInfo(), query);
      sqfNew.setObject(1, "INVOICELINE", "ponum", getMboValue("ponum").getString());
      sqfNew.setObject(2, "INVOICELINE", "positeid", getMboValue("positeid").getString());
      MboSetRemote poSet = getMboValue().getMbo().getMboSet("$invoicelinepo", "PO", sqfNew.format());

      if (poSet.isEmpty()) {
        throw new MXApplicationException("po", "InvalidPONum");
      }
      po = poSet.getMbo(0);
      ((InvoiceLineSet)mbo.getThisMboSet()).setPOforQueryOnly(po);
    }

    if (po.getBoolean("internal")) {
      throw new MXApplicationException("po", "InvalidPONum");
    }

    String invoiceOrgId = getMboValue("orgid").getString();
    String poOrgId = po.getString("orgid");
    if (!invoiceOrgId.equals(poOrgId))
    {
      getMboValue("positeid").setValueNull(2L);
      Object[] params = { getMboValue("ORGID").getColumnTitle(), invoiceOrgId };
      throw new MXApplicationException("system", "OrgIdCannotBeSet", params);
    }

    getMboValue("porevisionnum").setValue(po.getInt("revisionnum"), 11L);

    if (mbo.getMboSet("POLINE").getMbo(0).getBoolean("consignment")) {
      throw new MXApplicationException("invoice", "cannotInvoiceConsignment");
    }

    MboRemote newCost = null;

    InvoiceCostSetRemote invCostSet = (InvoiceCostSetRemote)mbo.getMboSet("INVOICECOST");

    if ((getMboValue().isNull()) || ((!getMboValue().getPreviousValue().isNull()) && 
      (getMboValue().getCurrentValue() != getMboValue().getPreviousValue())))
    {
      mbo.clearPOLine();

      getMboValue("description").setReadOnly(false);
      getMboValue("prorateservice").setReadOnly(false);
      getMboValue("linetype").setReadOnly(false);

      for (int i = 0; invCostSet.getMbo(i) != null; i++)
      {
        invCostSet.remove(i);
      }

      if (getMboValue().isNull())
      {
        newCost = invCostSet.add(2L);

        newCost.setValue("Percentage", 100, 11L);
        newCost.setValue("costlinenum", 1, 11L);
      }

      MboSetRemote invMatchSet = mbo.getMboSet("INVOICEMATCH");
      if (!invMatchSet.isEmpty()) {
        invMatchSet.deleteAll(2L);
      }

      if (mbo.getOwner().isNull("ponum")) {
        getMboValue("ponum").setReadOnly(false);
      }

      if (getMboValue().isNull()) {
        return;
      }

    }
    else
    {
      for (int i = 0; invCostSet.getMbo(i) != null; i++)
      {
        invCostSet.remove(i);
      }

    }

    mbo.copyPOLine();

    MboRemote poLine = mbo.getMboSet("POLINE").getMbo(0);
    mbo.addCostFromPOLine(invCostSet, poLine);

    getMboValue("ponum").setReadOnly(true);

    if (!mbo.getInternalLineType().equalsIgnoreCase("stdservice"))
    {
      mbo.setValue("prorateservice", false, 2L);
      getMboValue("prorateservice").setReadOnly(true);
    }

    getMboValue("description").setReadOnly(true);
    getMboValue("linetype").setReadOnly(true);

    if (newCost != null)
      ((InvoiceCost)newCost).setReadonly();
  }
}