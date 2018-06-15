package impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by usalian on 4/27/2018.
 */
public class PipelineRestrictor {


    public double getWorkingCap() {
        return workingCap;
    }

    public void setWorkingCap(double workingCap) {
        this.workingCap = workingCap;
    }

    public List<VCNomination> getPipelineNoms() {
        return pipelineNoms;
    }

    public void setPipelineNoms(List<VCNomination> pipelineNoms) {
        this.pipelineNoms = pipelineNoms;
    }

    private double workingCap = 0;

    public Compressor getCompressor() {
        return compressor;
    }

    public boolean isPipelineConstraint() {
        return pipelineConstraint;
    }

    public void setPipelineConstraint(boolean pipelineConstraint) {
        this.pipelineConstraint = pipelineConstraint;
    }

    private boolean pipelineConstraint = true;

    public void setCompressor(Compressor compressor) {
        this.compressor = compressor;
    }

    private Compressor compressor= null;

    public PipelineRestrictor(List<VCNomination> pipelineNoms) {
        this.pipelineNoms = pipelineNoms;
    }


    public PipelineRestrictor(Compressor compressor) {
        this.setCompressor(compressor);
        this.setPipelineConstraint(compressor.isPipeLine());
        if(isPipelineConstraint())
            this.pipelineNoms = compressor.getPipelineConstraintNoms();
        else
            this.pipelineNoms = compressor.getHeaderContraintNoms();
    }
    private List<VCNomination> pipelineNoms;

    private Map<String,Double> prioritySums = new HashMap<>();

    public Map<String, Double> getCuttableSums() {
        return cuttableSums;
    }

    public void setCuttableSums(Map<String, Double> cuttableSums) {
        this.cuttableSums = cuttableSums;
    }

    private Map<String,Double> cuttableSums = new HashMap<>();


    public void sumNoms() {
        // key = id, value - websites
        Map<String, List<VCNomination>> result =
                getPipelineNoms().stream().collect(Collectors.groupingBy(VCNomination::getPriority));

        for (String nomListKey:result.keySet()
             ) {
            double prioritySum = 0;
            double cuttableSum = 0;
            for (VCNomination aNom: result.get(nomListKey)
                 ) {
                prioritySum = prioritySum + aNom.getQty();
                cuttableSum = cuttableSum + aNom.getCuttableQty();

            }
            //set the priority nom sum in the priority sum map
            getPrioritySums().put(nomListKey,new Double(prioritySum));
            getCuttableSums().put(nomListKey,new Double(cuttableSum));

        }
    }
    public void restrictPipe() {

        sumNoms();

        double totalNomQty = 0;
        double totalNomCuttableQty = 0;
        StringBuffer post = new StringBuffer();

        post.append("Working Capcity for Pipeline: " + getWorkingCap());
        post.append("\n");
        post.append("Priority, PriorityQty, CuttableQty \n");
        for (String priorityQtyKey: getPrioritySums().keySet()
             ) {
            totalNomQty = totalNomQty + getPrioritySums().get(priorityQtyKey).doubleValue();
            totalNomCuttableQty = totalNomCuttableQty + getCuttableSums().get(priorityQtyKey).doubleValue();

            post.append(priorityQtyKey + "," + getPrioritySums().get(priorityQtyKey).doubleValue() +","+ getCuttableSums().get(priorityQtyKey).doubleValue());
            post.append("\n");
        }

        double formattedTotalCuttable = new BigDecimal(totalNomCuttableQty).setScale(3, RoundingMode.HALF_UP).doubleValue();

       post.append("Totals" + "," + totalNomQty+","+ formattedTotalCuttable);
        post.append("\n");
        double exceedingQtyFormatted = new BigDecimal( (totalNomCuttableQty - getWorkingCap())).setScale(3, RoundingMode.HALF_UP).doubleValue();
       post.append("Exceeding Cap" + "," + exceedingQtyFormatted);
        post.append("\n");

        ReadWriteToCSV helper = new ReadWriteToCSV();
        helper.writeRestrictionInfoToFile(getCompressor().isPipeLine()?"pipeline":"header",post.toString());

    }


    public Map<String, Double> getPrioritySums() {

        return prioritySums;
    }

    public void setPrioritySums(Map<String, Double> prioritySums) {
        this.prioritySums = prioritySums;
    }
}
