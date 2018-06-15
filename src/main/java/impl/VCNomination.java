package impl;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by usalian on 4/27/2018.
 */
public class VCNomination {

   // @CsvBindByName(column = "Rcpt Location", required = true)
    @CsvBindByPosition(position = 0)
    private String rcptLocation;
  //  @CsvBindByName(column = "Dlvry Location", required = true)
    @CsvBindByPosition(position = 1)
    private String dlvryLocation;
  //  @CsvBindByName(column = "Old Qty", required = true)
    @CsvBindByPosition(position = 2)
    private double qty;
   // @CsvBindByName(column = "Priority", required = true)
    @CsvBindByPosition(position = 3)
    private String priority;
  //  @CsvBindByName(column = "isUpgraded", required = false)
    @CsvBindByPosition(position = 4)
    private boolean upgraded = false;
  //  @CsvBindByName(column = "New Qty", required = false)
    @CsvBindByPosition(position = 5)
    private double newQty;
  //  @CsvBindByName(column = "Cuttable Qty", required = true)
    @CsvBindByPosition(position = 6)

    private double cuttableQty;




    public double getEconomicValue() {
        return economicValue;
    }

    public void setEconomicValue(double economicValue) {
        this.economicValue = economicValue;
    }
 //   @CsvBindByName(column = "Economic Value", required = true)
    @CsvBindByPosition(position = 7)

    private double economicValue;

    public double getNewQty() {
        return newQty;
    }

    public void setNewQty(double newQty) {
        this.newQty = newQty;
    }

    public double getCuttableQty() {
        return cuttableQty;
    }

    public void setCuttableQty(double cuttableQty) {
        this.cuttableQty  = new BigDecimal(cuttableQty).setScale(4, RoundingMode.HALF_UP).doubleValue();
        //this.cuttableQty = cuttableQty;
    }

    public String getDlvryLocation() {
        return dlvryLocation;
    }

    public void setDlvryLocation(String dlvryLocation) {
        this.dlvryLocation = dlvryLocation;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public String getPriority() {

            return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isHeaderToHeader() {
        return ((getRcptLocation().startsWith("L") || getRcptLocation().startsWith("H")) &&
                (getDlvryLocation().startsWith("L") || getDlvryLocation().startsWith("H")));
    }



    public boolean isUpgraded() {
        return upgraded;
    }
    public boolean  getUpgraded(){return upgraded; }

    public void setUpgraded(boolean upgradedFlag) {
        setPriority("2");
        System.out.println("upgraded noms");
        upgraded = upgradedFlag;

    }

    public String getRcptLocation() {
        return rcptLocation;
    }

    public void setRcptLocation(String rcptLocation) {
        this.rcptLocation = rcptLocation;
    }
    public void initPriority() {
        if (isHeaderToHeader()) {
            // if going from high to low, then set priority as 2

        }
    }

    public boolean isLowPressureNom() {
        return getRcptLocation().startsWith("L");

    }

    public String toString() {
        return getRcptLocation() + " " + getDlvryLocation() +" "+ getPriority() +" "+ (isUpgraded()?"Upgraded":"NotUpgr")+ " " + getQty() + " " + getCuttableQty() + " " + (isHeaderToHeader()?"H2H":"H2P");

    }
    public boolean isCompressedHeaderNom () {

        // the nom is header and compressed nom, but is not upgraded (p3) will be included
        if(isHeaderToHeader() && (getRcptLocation().startsWith("L")
                || (getRcptLocation().startsWith("H")&& getDlvryLocation().startsWith("H"))) && !isUpgraded()
                )
            return true;
        else
            return false;
    }
    public boolean isPiplelineNom() {
        return !isHeaderToHeader();
    }
    public boolean isIncludedForPipelineCapacity() {
        // if it is a pipeline nom
        if(!isHeaderToHeader())
            return true;
        // if it is header to header, then check if it is priority compressed gas
        if(isCompressedHeaderNom())
            return true;
        return false;
    }
}
