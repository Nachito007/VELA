public class BondData {
    String bondName;
    double lengthPm;
    double bondEnergyKjMol;
    boolean estimatedLength;

    public BondData(String bondName, double lengthPm, double bondEnergyKjMol){
        this(bondName, lengthPm, bondEnergyKjMol, false);
    }

    public BondData(String bondName, double lengthPm, double bondEnergyKjMol, boolean estimatedLength){
        this.bondName = bondName;
        this.lengthPm = lengthPm;
        this.bondEnergyKjMol = bondEnergyKjMol;
        this.estimatedLength = estimatedLength;
    }
}
