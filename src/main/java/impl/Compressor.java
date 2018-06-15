package impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by usalian on 4/30/2018.
 */
public class Compressor {
    public Compressor(boolean pipeLine) {
        this.pipeLine = pipeLine;
    }

    public double getCompressorQtyMax() {
        return compressorQtyMax;
    }

    public void setCompressorQtyMax(double compressorQtyMax) {
        this.compressorQtyMax = compressorQtyMax;
    }

    public double getCompressorMaxLowPressureQty() {
        return compressorMaxLowPressureQty;
    }

    public void setCompressorMaxLowPressureQty(double compressorMaxLowPressureQty) {
        this.compressorMaxLowPressureQty = compressorMaxLowPressureQty;
    }

    public boolean isPipeLine() {
        return pipeLine;
    }

    public void setPipeLine(boolean pipeLine) {
        this.pipeLine = pipeLine;
    }

    private boolean pipeLine = false;


    private  double compressorQtyMax;
    private  double compressorMaxLowPressureQty ;

    public List<VCNomination> getCompressedNoms() {
        if( compressedNoms ==null)
            compressedNoms = new ArrayList<>();
        return compressedNoms;
    }

    public void setCompressedNoms(ArrayList<VCNomination> compressedNoms) {
        this.compressedNoms = compressedNoms;
    }

    private List<VCNomination> compressedNoms;

    public void addToCompressedNoms(VCNomination aNom){
        getCompressedNoms().add(aNom);
    }
    public double getCompressedTotal(){
        return getCompressedNoms().stream().filter(x->(x.isCompressedHeaderNom() || x.isPiplelineNom()) ).collect(Collectors.summingDouble(o->o.getQty()));

    }
    public double getUncompressedTotal() {
        return getCompressedNoms().stream().filter(x->(!x.isCompressedHeaderNom() && !x.isPiplelineNom()) ).collect(Collectors.summingDouble(o->o.getQty()));

    }



    public double getCompressorLowPressureTotal(){
        return getCompressedNoms().stream().filter(x->x.isCompressedHeaderNom() ).collect(Collectors.summingDouble(o->o.getQty()));

    }

    public List<VCNomination> getPipelineConstraintNoms(){
       // return getCompressedNoms().stream().filter(x->(x.isCompressedHeaderNom() || x.isPiplelineNom())).collect(Collectors.toList());
        return getCompressedNoms().stream().filter(x->!x.getPriority().equals("2")).collect(Collectors.toList());

    }

    public List<VCNomination> getHeaderContraintNoms(){
        //return getCompressedNoms().stream().filter(x->(!x.isCompressedHeaderNom() && !x.isPiplelineNom()) ).collect(Collectors.toList());
        return getCompressedNoms();
    }
    public boolean isLowPressureTotalLessThanLimit() {
        if(getCompressedTotal() < getCompressorMaxLowPressureQty())
            return true;
        else
            return false;


    }

    public Map<String, MeterNomTracker> getMeterNomTrackerList() {
        return meterNomTrackerList;
    }

    public void setMeterNomTrackerList(Map<String, MeterNomTracker> meterNomTrackerList) {
        this.meterNomTrackerList = meterNomTrackerList;
    }

    private Map<String,MeterNomTracker> meterNomTrackerList = new HashMap<String,MeterNomTracker>();

    public void buildMeterNomTracker(boolean pipeline) {
        for (VCNomination nom: getCompressedNoms()
             ) {

            // check if rcpt location meter exists in teh map, and if does add to its out going noms
            // else add a new meter to the list
            boolean excludeNom = false;

            // just found out that we need to include for net calcuation for pipe the p2 nom.
//            if (pipeline){
//                // in case the nom is pure p2 without upgrage then we exclude them in netting for pipeline constraint
//                if(!nom.isUpgraded() && nom.getPriority().equals("2"))
//                    excludeNom = true;
//            }

            if(!excludeNom) {
                if (getMeterNomTrackerList().get(nom.getRcptLocation()) != null) {
                    getMeterNomTrackerList().get(nom.getRcptLocation()).getOutgoingNoms().add(nom);
                } else {
                    MeterNomTracker newMeterNomTracker = new MeterNomTracker();
                    newMeterNomTracker.setMeterName(nom.getRcptLocation());
                    newMeterNomTracker.getOutgoingNoms().add(nom);
                    getMeterNomTrackerList().put(nom.getRcptLocation(), newMeterNomTracker);
                }

                if (getMeterNomTrackerList().get(nom.getDlvryLocation()) != null) {
                    getMeterNomTrackerList().get(nom.getDlvryLocation()).getIncomingNoms().add(nom);
                } else {
                    MeterNomTracker newMeterNomTracker = new MeterNomTracker();
                    newMeterNomTracker.setMeterName(nom.getDlvryLocation());
                    newMeterNomTracker.getIncomingNoms().add(nom);
                    getMeterNomTrackerList().put(nom.getDlvryLocation(), newMeterNomTracker);
                }
            }
        }

        System.out.println("Header meter Net Rcpt(+)/Dlvry(-)");
        for (MeterNomTracker meterNomTr:getMeterNomTrackerList().values()
             ) {
            meterNomTr.processMeterNoms();
        }
    }
    public List<MeterNomTracker> getAllHeaderMeterNomTrackers() {
        List<MeterNomTracker> list = new ArrayList<>();

        for (MeterNomTracker meterNomTr:getMeterNomTrackerList().values()
                ) {
            list.add(meterNomTr);
        }
        return list;

    }
}
