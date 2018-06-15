package  impl;

import java.util.ArrayList;
import java.util.List;


public class CreateNomDataForValleyCrossing {

    static String PIPELINE_INITIAL = "pipeline_initial";
    static String HEADER_INITIAL = "header_initial";
    static String PIPELINE_FINAL = "pipeline_final";
    static String HEADER_FINAL = "header_final";
    static String NET_METER_INITIAL = "netmeter_initial";
    static String NET_METER_FINAL = "netmeter_final";

    static int PIPELINE_NOMS = 5;
    static int HEADER_NOMS = 5;

    public boolean isReadFile() {
        return readFile;
    }

    public void setReadFile(boolean readFile) {
        this.readFile = readFile;
    }

    private boolean readFile= false;

    public DataFactory getDc() {
        return dc;
    }

    private DataFactory dc = null;

    public Compressor getCompressor() {
        return compressor;
    }

    public void setCompressor(Compressor compressor) {
        this.compressor = compressor;
    }

    private Compressor compressor = null;

    public CreateNomDataForValleyCrossing(DataFactory dc, Compressor acompresor) {
        this.dc = dc;
        this.compressor = acompresor;
    }

    private  List<VCNomination> headerNoms= null;
    private List<VCNomination> pipelineNoms = null;

    public List<VCNomination> getHeaderNoms() {
        if(headerNoms==null)
            headerNoms=buildHeaderNominations();
        return headerNoms;
    }

    public void setHeaderNoms(List<VCNomination> headerNoms) {
        this.headerNoms = headerNoms;
    }

    public List<VCNomination> getPipelineNoms() {
        if(pipelineNoms==null)
            pipelineNoms=buildHeaderToPipelineNominations();
        return pipelineNoms;
    }

    public void setPipelineNoms(List<VCNomination> pipelineNoms) {
        this.pipelineNoms = pipelineNoms;
    }

    public List<VCNomination> buildHeaderNominations() {

        if(isReadFile()){
            ReadWriteToCSV helper = new ReadWriteToCSV();
            return helper.buildNomsFromFile(HEADER_INITIAL);
        }
        DataFactory dataFactory = getDc();

        List<VCNomination> noms = new ArrayList<VCNomination>();
        for (int i = 0; i < HEADER_NOMS; i++) {
            VCNomination aNom = new VCNomination();

            aNom.setRcptLocation(dataFactory.getHeaderLocationName());
            aNom.setDlvryLocation(dataFactory.getHeaderLocationName());
            if (aNom.getRcptLocation().equals(aNom.getDlvryLocation()))
                aNom.setDlvryLocation(dataFactory.getHeaderLocationName());
            if(aNom.getRcptLocation().startsWith("H") && aNom.getDlvryLocation().startsWith("L")){
                aNom.setPriority(dc.getHeaderHighToLowPriority());
            }
            else
                aNom.setPriority(dc.getHeaderOtherPriority());
            // set the header nom to be either compressed or uncompressed
            aNom.initPriority();
            aNom.setQty(dataFactory.getRandomNumber());
            noms.add(aNom);
        }

        // save data to file
        ReadWriteToCSV helper = new ReadWriteToCSV();
        helper.writeNomsToFile(HEADER_INITIAL,noms);
        return noms;
    }
    public  List<VCNomination> buildNomsFromFile() {
        ReadWriteToCSV helper = new ReadWriteToCSV();
        return helper.buildNomsFromFile(null);
    }

    public List<VCNomination> buildHeaderToPipelineNominations() {

        if(isReadFile()){
            ReadWriteToCSV helper = new ReadWriteToCSV();
            return helper.buildNomsFromFile(PIPELINE_INITIAL);
        }
        DataFactory dataFactory = getDc();
        List<VCNomination> noms = new ArrayList<VCNomination>();
        for (int i = 0; i < PIPELINE_NOMS; i++) {
            VCNomination aNom = new VCNomination();

            aNom.setRcptLocation(dataFactory.getHeaderLocationName());
            aNom.setDlvryLocation(dataFactory.getPipelineLocationName());


            // set the header nom to be either compressed or uncompressed
            aNom.setPriority(dataFactory.getPipelinePriority());
            aNom.setQty(dataFactory.getRandomNumber());
            noms.add(aNom);

        }
        // save data to file
        ReadWriteToCSV helper = new ReadWriteToCSV();
        helper.writeNomsToFile(PIPELINE_INITIAL,noms);
        return noms;

    }

    public void printNoms() {

        for (VCNomination headerNom :getHeaderNoms()
             ) {
            System.out.println(headerNom);
        }

        System.out.println("Pipeline noms");
        for (VCNomination pipeNom :getPipelineNoms()
                ) {
            System.out.println(pipeNom);
        }
    }
}