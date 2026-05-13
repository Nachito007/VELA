public class ElementData {
    String symbol;
    String name;
    int atomicNumber;
    int groupNumber;
    int period;
    String category;
    double atomicMass;
    double electronegativity;
    int valenceElectrons;
    int covalentRadiusPm;

    public ElementData(
        String symbol,
        String name,
        int atomicNumber,
        int groupNumber,
        int period,
        String category,
        double atomicMass,
        double electronegativity,
        int valenceElectrons,
        int covalentRadiusPm
    ){
        this.symbol = symbol;
        this.name = name;
        this.atomicNumber = atomicNumber;
        this.groupNumber = groupNumber;
        this.period = period;
        this.category = category;
        this.atomicMass = atomicMass;
        this.electronegativity = electronegativity;
        this.valenceElectrons = valenceElectrons;
        this.covalentRadiusPm = covalentRadiusPm;
    }
}
