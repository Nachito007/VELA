import java.util.*;

public class ChemData {
    static HashMap<String, ElementData> elements = new HashMap<>();
    static HashMap<String, BondData> bonds = new HashMap<>();

    static{
        addElement("H", "Hydrogen", 1, 1, 1, "Nonmetal", 1.008, 2.20, 1, 31);
        addElement("He", "Helium", 2, 18, 1, "Noble Gas", 4.0026, 0.00, 8, 28);
        addElement("Li", "Lithium", 3, 1, 2, "Alkali Metal", 6.94, 0.98, 1, 128);
        addElement("Be", "Beryllium", 4, 2, 2, "Alkaline Earth Metal", 9.0122, 1.57, 2, 96);
        addElement("B", "Boron", 5, 13, 2, "Metalloid", 10.81, 2.04, 3, 84);
        addElement("C", "Carbon", 6, 14, 2, "Nonmetal", 12.011, 2.55, 4, 76);
        addElement("N", "Nitrogen", 7, 15, 2, "Nonmetal", 14.007, 3.04, 5, 71);
        addElement("O", "Oxygen", 8, 16, 2, "Nonmetal", 15.999, 3.44, 6, 66);
        addElement("F", "Fluorine", 9, 17, 2, "Halogen", 18.998, 3.98, 7, 57);
        addElement("Ne", "Neon", 10, 18, 2, "Noble Gas", 20.180, 0.00, 8, 58);
        addElement("Na", "Sodium", 11, 1, 3, "Alkali Metal", 22.990, 0.93, 1, 166);
        addElement("Mg", "Magnesium", 12, 2, 3, "Alkaline Earth Metal", 24.305, 1.31, 2, 141);
        addElement("Al", "Aluminum", 13, 13, 3, "Post-transition Metal", 26.982, 1.61, 3, 121);
        addElement("Si", "Silicon", 14, 14, 3, "Metalloid", 28.085, 1.90, 4, 111);
        addElement("P", "Phosphorus", 15, 15, 3, "Nonmetal", 30.974, 2.19, 5, 107);
        addElement("S", "Sulfur", 16, 16, 3, "Nonmetal", 32.06, 2.58, 6, 105);
        addElement("Cl", "Chlorine", 17, 17, 3, "Halogen", 35.45, 3.16, 7, 102);
        addElement("Ar", "Argon", 18, 18, 3, "Noble Gas", 39.948, 0.00, 8, 106);
        addElement("K", "Potassium", 19, 1, 4, "Alkali Metal", 39.098, 0.82, 1, 203);
        addElement("Ca", "Calcium", 20, 2, 4, "Alkaline Earth Metal", 40.078, 1.00, 2, 176);
        addElement("Sc", "Scandium", 21, 3, 4, "Transition Metal", 44.956, 1.36, 3, 170);
        addElement("Ti", "Titanium", 22, 4, 4, "Transition Metal", 47.867, 1.54, 4, 160);
        addElement("V", "Vanadium", 23, 5, 4, "Transition Metal", 50.942, 1.63, 5, 153);
        addElement("Cr", "Chromium", 24, 6, 4, "Transition Metal", 51.996, 1.66, 6, 139);
        addElement("Mn", "Manganese", 25, 7, 4, "Transition Metal", 54.938, 1.55, 7, 139);
        addElement("Fe", "Iron", 26, 8, 4, "Transition Metal", 55.845, 1.83, 2, 132);
        addElement("Co", "Cobalt", 27, 9, 4, "Transition Metal", 58.933, 1.88, 2, 126);
        addElement("Ni", "Nickel", 28, 10, 4, "Transition Metal", 58.693, 1.91, 2, 124);
        addElement("Cu", "Copper", 29, 11, 4, "Transition Metal", 63.546, 1.90, 1, 132);
        addElement("Zn", "Zinc", 30, 12, 4, "Transition Metal", 65.38, 1.65, 2, 122);
        addElement("Br", "Bromine", 35, 17, 4, "Halogen", 79.904, 2.96, 7, 120);
        addElement("Sr", "Strontium", 38, 2, 5, "Alkaline Earth Metal", 87.62, 0.95, 2, 195);
        addElement("Ag", "Silver", 47, 11, 5, "Transition Metal", 107.868, 1.93, 1, 145);
        addElement("Sn", "Tin", 50, 14, 5, "Post-transition Metal", 118.710, 1.96, 4, 139);
        addElement("I", "Iodine", 53, 17, 5, "Halogen", 126.904, 2.66, 7, 139);
        addElement("Cs", "Cesium", 55, 1, 6, "Alkali Metal", 132.905, 0.79, 1, 244);
        addElement("Ba", "Barium", 56, 2, 6, "Alkaline Earth Metal", 137.327, 0.89, 2, 215);
        addElement("Pt", "Platinum", 78, 10, 6, "Transition Metal", 195.084, 2.28, 2, 136);
        addElement("Au", "Gold", 79, 11, 6, "Transition Metal", 196.967, 2.54, 1, 136);
        addElement("Hg", "Mercury", 80, 12, 6, "Transition Metal", 200.592, 2.00, 2, 132);
        addElement("Pb", "Lead", 82, 14, 6, "Post-transition Metal", 207.2, 2.33, 4, 146);
        addElement("Ra", "Radium", 88, 2, 7, "Alkaline Earth Metal", 226.0, 0.90, 2, 221);
        addElement("Fr", "Francium", 87, 1, 7, "Alkali Metal", 223.0, 0.70, 1, 260);
    }

    static{
        addBond("H", "H", 1, 74, 436);
        addBond("H", "B", 1, 119, 389);
        addBond("H", "C", 1, 109, 413);
        addBond("H", "N", 1, 101, 391);
        addBond("H", "O", 1, 96, 463);
        addBond("H", "F", 1, 92, 565);
        addBond("H", "P", 1, 142, 322);
        addBond("H", "S", 1, 134, 347);
        addBond("H", "Cl", 1, 127, 431);
        addBond("H", "Br", 1, 141, 366);
        addBond("H", "I", 1, 161, 299);

        addBond("C", "C", 1, 154, 347);
        addBond("C", "C", 2, 134, 614);
        addBond("C", "C", 3, 120, 839);
        addBond("C", "N", 1, 147, 305);
        addBond("C", "N", 2, 129, 615);
        addBond("C", "N", 3, 116, 891);
        addBond("C", "O", 1, 143, 358);
        addBond("C", "O", 2, 122, 799);
        addBond("C", "F", 1, 135, 485);
        addBond("C", "Cl", 1, 177, 327);
        addBond("C", "Br", 1, 194, 285);
        addBond("C", "I", 1, 214, 213);
        addBond("C", "S", 1, 182, 272);
        addBond("C", "S", 2, 160, 573);

        addBond("N", "N", 1, 145, 163);
        addBond("N", "N", 2, 125, 418);
        addBond("N", "N", 3, 110, 945);
        addBond("N", "O", 1, 140, 201);
        addBond("N", "O", 2, 121, 607);
        addBond("N", "F", 1, 136, 272);
        addBond("N", "Cl", 1, 175, 200);

        addBond("O", "O", 1, 148, 146);
        addBond("O", "O", 2, 121, 498);
        addBond("O", "F", 1, 142, 190);
        addBond("O", "Cl", 1, 170, 203);
        addBond("O", "S", 1, 158, 265);
        addBond("O", "S", 2, 143, 523);
        addBond("O", "P", 1, 163, 335);
        addBond("O", "P", 2, 150, 544);
        addBond("O", "Si", 1, 164, 452);

        addBond("F", "F", 1, 142, 159);
        addBond("F", "B", 1, 131, 613);
        addBond("F", "P", 1, 156, 490);
        addBond("F", "S", 1, 156, 327);
        addBond("F", "Cl", 1, 163, 253);
        addBond("Cl", "Cl", 1, 199, 243);
        addBond("Cl", "P", 1, 203, 326);
        addBond("Cl", "S", 1, 207, 255);
        addBond("Br", "Br", 1, 228, 193);
        addBond("I", "I", 1, 267, 151);
    }

    public static void addElement(
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
        elements.put(
            symbol,
            new ElementData(
                symbol,
                name,
                atomicNumber,
                groupNumber,
                period,
                category,
                atomicMass,
                electronegativity,
                valenceElectrons,
                covalentRadiusPm
            )
        );
    }

    public static void addBond(String a, String b, int type, double lengthPm, double energyKjMol){
        String key = getBondKey(a, b, type);
        bonds.put(key, new BondData(getBondName(a, b, type), lengthPm, energyKjMol));
    }

    public static ElementData getElementData(String symbol){
        return elements.get(symbol);
    }

    public static BondData getBond(Bond bond){
        return getBond(bond.a1.element, bond.a2.element, bond.type);
    }

    public static BondData getBond(String bond){
        return bonds.get(bond);
    }

    public static BondData getBond(String a, String b, int type){
        BondData data = bonds.get(getBondKey(a, b, type));

        if(data != null){
            return data;
        }

        ElementData d1 = getElementData(a);
        ElementData d2 = getElementData(b);

        if(d1 == null || d2 == null){
            return null;
        }

        double estimatedLength = d1.covalentRadiusPm + d2.covalentRadiusPm;

        if(type == 2){
            estimatedLength *= 0.87;
        }

        if(type == 3){
            estimatedLength *= 0.78;
        }

        return new BondData(getBondName(a, b, type), estimatedLength, 0, true);
    }

    public static String getBondName(String a, String b, int type){
        return a + getBondSymbol(type) + b;
    }

    public static String getBondSymbol(int type){
        if(type == 2){
            return "=";
        }

        if(type == 3){
            return "#";
        }

        return "-";
    }

    public static String getBondKey(String a, String b, int type){
        if(a.compareTo(b) <= 0){
            return a + getBondSymbol(type) + b;
        }

        return b + getBondSymbol(type) + a;
    }
}
