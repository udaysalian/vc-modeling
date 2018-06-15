package impl;

/**
 * Created by usalian on 4/27/2018.
 */
public interface LocationNameDataValues {
    String[] getHeaderLocationNames();
    String[] getPipelineLocationNames();
    String[] getPipelinePriorities();
    public String[] getHeaderHighToLowPriorities();

    public String[] getHeaderOtherPrioritites();

}
