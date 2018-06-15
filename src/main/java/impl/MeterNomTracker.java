package impl;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by usalian on 5/1/2018.
 */
public class MeterNomTracker {

    private List<VCNomination> incomingNoms = new ArrayList<>();

    private List<VCNomination> outgoingNoms = new ArrayList<>();

    public List<VCNomination> getIncomingNoms() {
        return incomingNoms;
    }

    public void setIncomingNoms(List<VCNomination> incomingNoms) {
        this.incomingNoms = incomingNoms;
    }

    public List<VCNomination> getOutgoingNoms() {
        return outgoingNoms;
    }

    public void setOutgoingNoms(List<VCNomination> outgoingNoms) {
        this.outgoingNoms = outgoingNoms;
    }

    public String getMeterName() {
        return meterName;
    }

    public void setMeterName(String meterName) {
        this.meterName = meterName;
    }
    @CsvBindByName(column = "Meter Name", required = true)
    @CsvBindByPosition(position = 0)
    private String meterName;

    public void setNetQty(double netqty) {
        this.netQty = netqty;
    }

    // if returns positive number then it is net receipts out
    // else it is net deliveries
    @CsvBindByName(column = "Net Qty", required = true)
    @CsvBindByPosition(position = 1)

    private double netQty ;

    public double getNetQty() {
        double net = getOutgoingTotal() - getIncomingTotal();


          netQty = new BigDecimal(net).setScale(3, RoundingMode.HALF_UP).doubleValue();
          return netQty;
    }

    public void prorate(double amount , List<VCNomination> nomList){
        double total = nomList.stream().collect(Collectors.summingDouble(o->o.getQty()));
        for (VCNomination nom:nomList
             ) {
            nom.setCuttableQty((nom.getQty()/total)*amount);
        }


    }

    public void assignCuttable(double qty){
        // sort the inomcing noms into map of priority
        Map<String, List<VCNomination>> result =
                getIncomingNoms().stream().collect(Collectors.groupingBy(VCNomination::getPriority));

        for (String priority:getPriorityOrder()
             ) {
            List<VCNomination> nomList = result.get(priority);
            // prorate
            if(nomList != null) {
                double total = nomList.stream().collect(Collectors.summingDouble(o -> o.getQty()));
                if (qty < total) {

                    prorate(qty, nomList);
                    qty = 0;
                    return;
                } else {
                    nomList.stream().collect(Collectors.toList()).forEach(o -> {
                        o.setCuttableQty(o.getQty());
                    });
                    qty = qty - total;
                }
            }
        }
    }

    public double getDeliveryTotal() {
        deliveryTotal =getIncomingTotal();
        return deliveryTotal;
    }

    public void setDeliveryTotal(double deliveryTotal) {
        this.deliveryTotal = deliveryTotal;
    }

    public double getReceiptTotal() {
        receiptTotal = getOutgoingTotal();
        return receiptTotal;
    }

    public void setReceiptTotal(double receiptTotal) {
        this.receiptTotal = receiptTotal;
    }

    @CsvBindByName(column = "Delivery Total", required = true)
    @CsvBindByPosition(position = 3)
    private double deliveryTotal;


    @CsvBindByName(column = "Receipts Total", required = true)
    @CsvBindByPosition(position = 2)
    private double receiptTotal;


    public double getIncomingTotal() {
        return getIncomingNoms().stream().collect(Collectors.summingDouble(o->o.getQty()));

    }

    public double getOutgoingTotal() {
       return getOutgoingNoms().stream().collect(Collectors.summingDouble(o->o.getQty()));
    }

    public boolean isHeaderMeter(String name) {
        return name.startsWith("L") || name.startsWith("H");
    }
    public void processMeterNoms(){

        if(isHeaderMeter(getMeterName()))
            System.out.println( getMeterName() + " " + getNetQty() );

     //   System.out.println("Meter " + getMeterName() + " Outgoing(Receipts out) total: " + getOutgoingTotal() + " Incoming (deliveries) Total :" +  getIncomingTotal());
     //   System.out.println("Receipts out Noms: " + getOutgoingNoms());
     //   System.out.println("Delivery Noms : " + getIncomingNoms());

        if (getNetQty() >=0){
            // upgrade the p3 incoming to p2

            getIncomingNoms().stream().filter(o->o.getPriority().equals("3")).forEach(o->{o.setUpgraded(true);});
        }else{
            // if it net delivery then use it to set the cuttable for incoming noms;
            assignCuttable(getNetQty() *-1);
        }
    }

    public List<String> getPriorityOrder() {
        return  Arrays.asList("IT", "AO", "3","2","1");
    }
}
