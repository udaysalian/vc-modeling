package impl;

import java.util.Locale;

/**
 * Created by usalian on 4/27/2018.
 */
public class VCLocationNameDataValues extends ResourceBundleValues implements LocationNameDataValues {

    private static String[] headreNames;
    private static String[] headerLocationTypes;
    private static String[] pipelineLocationNames;
    private static String[] pipelinePriorities;
    private static String[] headerHighToLowPriorities;
    private static String[] headerOtherPrioritites;

    public VCLocationNameDataValues(Locale locale) {
        super("VCLocationDataValues", locale);
    }

    @Override
    public String[] getPipelineLocationNames() {
        if (pipelineLocationNames == null) {
            pipelineLocationNames = getValues("PipelineLocationNames");
        }
        return pipelineLocationNames;
    }



    @Override
    public String[] getHeaderLocationNames() {
        if (headreNames == null) {
            headreNames = getValues("HeaderLocationNames");
        }
        return headreNames;
    }

    @Override
    public String[] getPipelinePriorities() {
        if (pipelinePriorities == null) {
            pipelinePriorities = getValues("PipelinePriorities");
        }
        return pipelinePriorities;
    }
    @Override
    public String[] getHeaderHighToLowPriorities() {
        if (pipelinePriorities == null) {
            headerHighToLowPriorities = getValues("HeaderHighToLow");
        }
        return headerHighToLowPriorities;
    }
    @Override
    public String[] getHeaderOtherPrioritites() {
        if (headerOtherPrioritites == null) {
            headerOtherPrioritites = getValues("HeaderOther");
        }
        return headerOtherPrioritites;
    }
}
