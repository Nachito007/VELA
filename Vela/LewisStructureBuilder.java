import java.awt.*;
import java.util.*;

public class LewisStructureBuilder {

    public Molecule build(String formula) {
        LinkedHashMap<String, Integer> counts = parseFormulaCounts(formula);
        Molecule molecule = new Molecule();

        if (counts.size() == 0) {
            molecule.addMessage("Please enter a chemical formula.");
            return molecule;
        }

        validateElements(counts, molecule);

        ArrayList<Atom> atoms = createAtoms(counts, molecule);

        if (atoms.size() == 0) {
            return molecule;
        }

        ArrayList<Atom> hydrogens = new ArrayList<>();
        ArrayList<Atom> heavyAtoms = new ArrayList<>();

        for (Atom atom : atoms) {
            if (atom.element.equals("H")) {
                hydrogens.add(atom);
            }
            else {
                heavyAtoms.add(atom);
            }
        }

        if (isCarboxylFormula(formula, counts) && buildCarboxylStructure(molecule)) {
            assignLonePairs(molecule);
            validateBuild(molecule);
            return molecule;
        }

        if (heavyAtoms.size() == 0) {
            connectHydrogenOnlyMolecule(molecule, hydrogens);
            assignLonePairs(molecule);
            validateBuild(molecule);
            return molecule;
        }

        buildHeavyAtomSkeleton(molecule, heavyAtoms);
        applyCommonIonExceptions(counts, molecule);
        attachHydrogens(molecule, heavyAtoms, hydrogens);
        improveBondOrders(molecule);
        applyCommonPolyatomicIonShapes(counts, molecule);
        assignLonePairs(molecule);
        validateBuild(molecule);

        return molecule;
    }

    public ArrayList<String> parseFormula(String formula) {
        ArrayList<String> atoms = new ArrayList<>();
        LinkedHashMap<String, Integer> counts = parseFormulaCounts(formula);

        for (String element : counts.keySet()) {
            int count = counts.get(element);
            for (int i = 0; i < count; i++) {
                atoms.add(element);
            }
        }

        return atoms;
    }

    public LinkedHashMap<String, Integer> parseFormulaCounts(String formula) {
        FormulaParser parser = new FormulaParser(formula);
        return parser.parse();
    }

    public ArrayList<Atom> createAtoms(LinkedHashMap<String, Integer> counts, Molecule molecule) {
        ArrayList<Atom> atoms = new ArrayList<>();

        for (String element : counts.keySet()) {
            for (int i = 0; i < counts.get(element); i++) {
                Atom atom = new Atom(element);
                atom.color = getColor(element);
                atoms.add(atom);
                molecule.addAtom(atom);
            }
        }

        return atoms;
    }

    public boolean isCarboxylFormula(String formula, LinkedHashMap<String, Integer> counts) {
        String cleanFormula = formula.replaceAll("\\s+", "").toUpperCase();

        if (cleanFormula.contains("COOH")) {
            return true;
        }

        if (!counts.containsKey("C") || !counts.containsKey("O") || !counts.containsKey("H")) {
            return false;
        }

        int carbonCount = counts.get("C");
        int hydrogenCount = counts.get("H");
        int oxygenCount = counts.get("O");

        return oxygenCount == 2
            && carbonCount >= 1
            && hydrogenCount >= 2
            && hydrogenCount <= carbonCount * 2 + 2;
    }

    public boolean buildCarboxylStructure(Molecule molecule) {
        ArrayList<Atom> carbons = getAtomsByElement(molecule.atoms, "C");
        ArrayList<Atom> oxygens = getAtomsByElement(molecule.atoms, "O");
        ArrayList<Atom> hydrogens = getAtomsByElement(molecule.atoms, "H");

        if (carbons.size() < 1 || oxygens.size() < 2 || hydrogens.size() < 1) {
            return false;
        }

        Atom carboxylCarbon = carbons.get(carbons.size() - 1);
        Atom carbonylOxygen = oxygens.get(0);
        Atom hydroxylOxygen = oxygens.get(1);
        Atom hydroxylHydrogen = hydrogens.remove(0);

        addBondIfPossible(molecule, carboxylCarbon, carbonylOxygen, 2);
        addBondIfPossible(molecule, carboxylCarbon, hydroxylOxygen, 1);
        addBondIfPossible(molecule, hydroxylOxygen, hydroxylHydrogen, 1);

        ArrayList<Atom> carbonChain = new ArrayList<>();

        for (Atom carbon : carbons) {
            if (carbon != carboxylCarbon) {
                carbonChain.add(carbon);
            }
        }

        if (carbonChain.size() > 0) {
            connectChain(molecule, carbonChain);
            addBondIfPossible(molecule, carbonChain.get(carbonChain.size() - 1), carboxylCarbon, 1);
        }

        ArrayList<Atom> hydrogenTargets = new ArrayList<>();
        hydrogenTargets.addAll(carbonChain);
        hydrogenTargets.add(carboxylCarbon);

        for (Atom hydrogen : hydrogens) {
            Atom target = bestHydrogenTarget(hydrogenTargets);

            if (target != null) {
                addBondIfPossible(molecule, target, hydrogen, 1);
            }
        }

        molecule.addNote("Showing the -COOH carboxyl group as C(=O)-O-H.");
        return true;
    }

    public void validateElements(LinkedHashMap<String, Integer> counts, Molecule molecule) {
        for (String element : counts.keySet()) {
            if (ChemData.getElementData(element) == null) {
                molecule.addMessage("Unknown element: " + element);
            }
        }
    }

    public void applyCommonIonExceptions(LinkedHashMap<String, Integer> counts, Molecule molecule) {
        if (hasOnly(counts, "H", 3, "O", 1)) {
            Atom oxygen = findFirstAtom(molecule, "O");

            if (oxygen != null) {
                oxygen.maxBonds = 3;
                molecule.addNote("Showing H3O as hydronium, H3O+.");
            }
        }

        if (hasOnly(counts, "N", 1, "H", 4)) {
            Atom nitrogen = findFirstAtom(molecule, "N");

            if (nitrogen != null) {
                nitrogen.maxBonds = 4;
                molecule.addNote("Showing NH4 as ammonium, NH4+.");
            }
        }

        if (hasOnly(counts, "C", 1, "O", 1)) {
            Atom oxygen = findFirstAtom(molecule, "O");

            if (oxygen != null) {
                oxygen.maxBonds = 3;
                molecule.addNote("Showing CO with its common triple-bond Lewis structure.");
            }
        }
    }

    public void applyCommonPolyatomicIonShapes(LinkedHashMap<String, Integer> counts, Molecule molecule) {
        if (hasOnly(counts, "N", 1, "O", 3)) {
            Atom nitrogen = findFirstAtom(molecule, "N");

            if (nitrogen != null) {
                nitrogen.maxBonds = 4;
                nitrogen.lonePairs = 0;
                upgradeOneBondFrom(nitrogen);
                molecule.addNote("Showing NO3 as nitrate, NO3-. Resonance is simplified with one double bond.");
            }
        }

        if (hasOnly(counts, "S", 1, "O", 4)) {
            Atom sulfur = findFirstAtom(molecule, "S");

            if (sulfur != null) {
                sulfur.lonePairs = 0;
                upgradeOneBondFrom(sulfur);
                upgradeOneBondFrom(sulfur);
                molecule.addNote("Showing SO4 as sulfate, SO4 2-. Resonance is simplified with two double bonds.");
            }
        }

        if (hasOnly(counts, "C", 1, "O", 3)) {
            molecule.addNote("Showing CO3 as carbonate, CO3 2-. Resonance is simplified.");
        }

        if (hasOnly(counts, "P", 1, "O", 4)) {
            molecule.addNote("Showing PO4 as phosphate, PO4 3-. Resonance is simplified.");
        }
    }

    public void upgradeOneBondFrom(Atom atom) {
        Bond upgrade = bestUpgradeableBond(atom);

        if (upgrade == null) {
            return;
        }

        upgrade.type++;
        upgrade.a1.currentBonds++;
        upgrade.a2.currentBonds++;
    }

    public boolean hasOnly(
        LinkedHashMap<String, Integer> counts,
        String firstElement,
        int firstCount,
        String secondElement,
        int secondCount
    ) {
        return counts.size() == 2
            && counts.containsKey(firstElement)
            && counts.containsKey(secondElement)
            && counts.get(firstElement) == firstCount
            && counts.get(secondElement) == secondCount;
    }

    public Atom findFirstAtom(Molecule molecule, String element) {
        for (Atom atom : molecule.atoms) {
            if (atom.element.equals(element)) {
                return atom;
            }
        }

        return null;
    }

    public void validateBuild(Molecule molecule) {
        if (molecule.atoms.size() <= 1) {
            return;
        }

        if (containsMetal(molecule) && containsNonmetal(molecule)) {
            molecule.addNote(
                "This looks ionic, so VSEPR geometry is only a simplified display."
            );
        }

        ArrayList<String> unconnectedAtoms = new ArrayList<>();

        for (Atom atom : molecule.atoms) {
            if (atom.bonds.size() == 0) {
                unconnectedAtoms.add(atom.element);
            }
        }

        if (unconnectedAtoms.size() > 0) {
            molecule.addMessage(
                "Some atoms could not be connected: "
                + joinSymbols(unconnectedAtoms)
                + ". This formula may need a charge, a different structure, or may be outside this app's simple AP Chem model."
            );
        }

        if (molecule.bonds.size() < molecule.atoms.size() - 1) {
            molecule.addMessage(
                "The generated structure may be incomplete because not every atom is connected into one molecule."
            );
        }
    }

    public String joinSymbols(ArrayList<String> symbols) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < symbols.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }

            builder.append(symbols.get(i));
        }

        return builder.toString();
    }

    public boolean containsMetal(Molecule molecule) {
        for (Atom atom : molecule.atoms) {
            if (isMetal(atom.element)) {
                return true;
            }
        }

        return false;
    }

    public boolean containsNonmetal(Molecule molecule) {
        for (Atom atom : molecule.atoms) {
            if (!isMetal(atom.element)) {
                return true;
            }
        }

        return false;
    }

    public void buildHeavyAtomSkeleton(Molecule molecule, ArrayList<Atom> heavyAtoms) {
        if (heavyAtoms.size() == 1) {
            return;
        }

        ArrayList<Atom> carbons = getAtomsByElement(heavyAtoms, "C");

        if (carbons.size() > 1) {
            connectChain(molecule, carbons);

            for (Atom atom : heavyAtoms) {
                if (!atom.element.equals("C")) {
                    Atom target = bestAttachmentAtom(carbons);
                    addBondIfPossible(molecule, target, atom, 1);
                }
            }

            return;
        }

        Atom central = chooseCentralAtom(heavyAtoms);

        for (Atom atom : heavyAtoms) {
            if (atom != central) {
                addBondIfPossible(molecule, central, atom, 1);
            }
        }
    }

    public void connectChain(Molecule molecule, ArrayList<Atom> atoms) {
        for (int i = 0; i < atoms.size() - 1; i++) {
            addBondIfPossible(molecule, atoms.get(i), atoms.get(i + 1), 1);
        }
    }

    public void attachHydrogens(Molecule molecule, ArrayList<Atom> heavyAtoms, ArrayList<Atom> hydrogens) {
        for (Atom hydrogen : hydrogens) {
            Atom target = bestHydrogenTarget(heavyAtoms);

            if (target != null) {
                addBondIfPossible(molecule, target, hydrogen, 1);
            }
        }
    }

    public void connectHydrogenOnlyMolecule(Molecule molecule, ArrayList<Atom> hydrogens) {
        for (int i = 0; i + 1 < hydrogens.size(); i += 2) {
            addBondIfPossible(molecule, hydrogens.get(i), hydrogens.get(i + 1), 1);
        }
    }

    public void improveBondOrders(Molecule molecule) {
        boolean changed = true;

        while (changed) {
            changed = false;

            for (Atom atom : molecule.atoms) {
                if (atom.element.equals("H")) {
                    continue;
                }

                while (needsOctet(atom)) {
                    Bond upgrade = bestUpgradeableBond(atom);

                    if (upgrade == null) {
                        break;
                    }

                    upgrade.type++;
                    upgrade.a1.currentBonds++;
                    upgrade.a2.currentBonds++;
                    changed = true;
                }
            }
        }
    }

    public Bond bestUpgradeableBond(Atom atom) {
        Bond best = null;
        int bestScore = Integer.MIN_VALUE;

        for (Bond bond : atom.bonds) {
            if (bond.type >= 3) {
                continue;
            }

            Atom other = getOtherAtom(bond, atom);

            if (other.element.equals("H")) {
                continue;
            }

            if (atom.currentBonds >= atom.maxBonds || other.currentBonds >= other.maxBonds) {
                continue;
            }

            int score = octetElectrons(other) - targetElectrons(other);

            if (score > bestScore) {
                bestScore = score;
                best = bond;
            }
        }

        return best;
    }

    public void assignLonePairs(Molecule molecule) {
        for (Atom atom : molecule.atoms) {
            int valence = getValenceElectrons(atom.element);
            int formalCharge = preferredFormalCharge(atom);
            int nonBondingElectrons = valence - atom.currentBonds - formalCharge;

            if (nonBondingElectrons < 0) {
                nonBondingElectrons = 0;
            }

            atom.lonePairs = nonBondingElectrons / 2;
            atom.formalCharge = valence - (atom.lonePairs * 2) - atom.currentBonds;
        }
    }

    public int preferredFormalCharge(Atom atom) {
        if (atom.element.equals("N") && atom.currentBonds == 4) {
            return 1;
        }

        if (atom.element.equals("O") && atom.currentBonds == 3) {
            return 1;
        }

        if (atom.element.equals("C") && atom.currentBonds == 3 && atom.bonds.size() == 1) {
            return -1;
        }

        if ((atom.element.equals("O") || atom.element.equals("S")) && atom.currentBonds == 1) {
            return -1;
        }

        return 0;
    }

    public boolean needsOctet(Atom atom) {
        return octetElectrons(atom) < targetElectrons(atom) && atom.currentBonds < atom.maxBonds;
    }

    public int octetElectrons(Atom atom) {
        int electrons = 0;

        for (Bond bond : atom.bonds) {
            electrons += bond.type * 2;
        }

        return electrons;
    }

    public int targetElectrons(Atom atom) {
        if (atom.element.equals("H")) {
            return 2;
        }

        if (atom.element.equals("B") || atom.element.equals("Al")) {
            return 6;
        }

        return 8;
    }

    public boolean addBondIfPossible(Molecule molecule, Atom a, Atom b, int type) {
        if (a == null || b == null || a == b) {
            return false;
        }

        if (a.currentBonds + type > a.maxBonds || b.currentBonds + type > b.maxBonds) {
            return false;
        }

        molecule.addBond(a, b, type);
        a.currentBonds += type;
        b.currentBonds += type;
        return true;
    }

    public Atom chooseCentralAtom(ArrayList<Atom> atoms) {
        Atom best = null;

        for (Atom atom : atoms) {
            if (isTerminalElement(atom.element) && atoms.size() > 1) {
                continue;
            }

            if (best == null || centralAtomScore(atom) > centralAtomScore(best)) {
                best = atom;
            }
        }

        if (best != null) {
            return best;
        }

        return atoms.get(0);
    }

    public int centralAtomScore(Atom atom) {
        ElementData data = ChemData.getElementData(atom.element);
        int score = atom.maxBonds * 100;

        if (data != null) {
            score -= (int)(data.electronegativity * 10);
        }

        return score;
    }

    public Atom bestHydrogenTarget(ArrayList<Atom> heavyAtoms) {
        Atom best = null;

        for (Atom atom : heavyAtoms) {
            if (isMetal(atom.element)) {
                continue;
            }

            if (atom.currentBonds >= atom.maxBonds) {
                continue;
            }

            if (best == null || hydrogenTargetScore(atom) > hydrogenTargetScore(best)) {
                best = atom;
            }
        }

        return best;
    }

    public int hydrogenTargetScore(Atom atom) {
        int missingBonds = atom.maxBonds - atom.currentBonds;
        int score = missingBonds * 100;

        if (atom.element.equals("C")) {
            score += 20;
        }

        if (atom.element.equals("N") || atom.element.equals("O")) {
            score += 10;
        }

        return score;
    }

    public Atom bestAttachmentAtom(ArrayList<Atom> atoms) {
        Atom best = null;

        for (Atom atom : atoms) {
            if (atom.currentBonds >= atom.maxBonds) {
                continue;
            }

            if (best == null || atom.currentBonds < best.currentBonds) {
                best = atom;
            }
        }

        return best;
    }

    public ArrayList<Atom> getAtomsByElement(ArrayList<Atom> atoms, String element) {
        ArrayList<Atom> matches = new ArrayList<>();

        for (Atom atom : atoms) {
            if (atom.element.equals(element)) {
                matches.add(atom);
            }
        }

        return matches;
    }

    public Atom getOtherAtom(Bond bond, Atom atom) {
        if (bond.a1 == atom) {
            return bond.a2;
        }

        return bond.a1;
    }

    public boolean isTerminalElement(String element) {
        return element.equals("H")
            || element.equals("F")
            || element.equals("Cl")
            || element.equals("Br")
            || element.equals("I");
    }

    public boolean isMetal(String element) {
        return element.equals("Li")
            || element.equals("Na")
            || element.equals("K")
            || element.equals("Rb")
            || element.equals("Cs")
            || element.equals("Fr")
            || element.equals("Be")
            || element.equals("Mg")
            || element.equals("Ca")
            || element.equals("Sr")
            || element.equals("Ba")
            || element.equals("Ra")
            || element.equals("Fe")
            || element.equals("Cu")
            || element.equals("Zn")
            || element.equals("Ag")
            || element.equals("Au")
            || element.equals("Pb")
            || element.equals("Sn")
            || element.equals("Hg");
    }

    public int getValenceElectrons(String element) {
        ElementData data = ChemData.getElementData(element);

        if (data == null) {
            return 0;
        }

        return data.valenceElectrons;
    }

    public Color getColor(String element) {
        switch(element){
            case "H": return new Color(245, 245, 245);
            case "C": return new Color(45, 45, 45);
            case "N": return new Color(50, 110, 255);
            case "O": return new Color(235, 55, 55);
            case "F": return new Color(80, 220, 120);
            case "Cl": return new Color(55, 190, 70);
            case "Br": return new Color(150, 75, 35);
            case "I": return new Color(115, 70, 190);
            case "S": return new Color(245, 210, 55);
            case "P": return new Color(255, 145, 35);
            case "B": return new Color(255, 180, 135);
            case "Si": return new Color(210, 170, 135);
            case "Al": return new Color(170, 185, 195);
            case "Li": return new Color(180, 90, 255);
            case "Na": return new Color(120, 120, 255);
            case "K": return new Color(140, 70, 210);
            case "Mg": return new Color(95, 210, 95);
            case "Ca": return new Color(70, 185, 95);
            case "Fe": return new Color(210, 105, 75);
            case "Cu": return new Color(205, 125, 65);
            case "Zn": return new Color(125, 145, 175);
            case "Ag": return new Color(190, 200, 210);
            case "Au": return new Color(240, 190, 50);
            case "Pb": return new Color(85, 95, 115);
            case "Sn": return new Color(150, 165, 175);
            case "Hg": return new Color(175, 185, 200);
            default: return new Color(150, 150, 150);
        }
    }

    public static class FormulaParser {
        String formula;
        int index;

        FormulaParser(String formula) {
            this.formula = formula.replaceAll("\\s+", "");
            this.index = 0;
        }

        LinkedHashMap<String, Integer> parse() {
            return parseGroup();
        }

        public LinkedHashMap<String, Integer> parseGroup() {
            LinkedHashMap<String, Integer> counts = new LinkedHashMap<>();

            while (index < formula.length()) {
                char current = formula.charAt(index);

                if (current == '(') {
                    index++;
                    LinkedHashMap<String, Integer> inner = parseGroup();
                    int multiplier = parseNumber();
                    addCounts(counts, inner, multiplier);
                }
                else if (current == ')') {
                    index++;
                    return counts;
                }
                else if (Character.isUpperCase(current)) {
                    String element = parseElement();
                    int count = parseNumber();
                    addCount(counts, element, count);
                }
                else {
                    index++;
                }
            }

            return counts;
        }

        public String parseElement() {
            String element = "" + formula.charAt(index);
            index++;

            if (index < formula.length() && Character.isLowerCase(formula.charAt(index))) {
                element += formula.charAt(index);
                index++;
            }

            return element;
        }

        public int parseNumber() {
            int start = index;

            while (index < formula.length() && Character.isDigit(formula.charAt(index))) {
                index++;
            }

            if (start == index) {
                return 1;
            }

            return Integer.parseInt(formula.substring(start, index));
        }

        public void addCounts(LinkedHashMap<String, Integer> target, LinkedHashMap<String, Integer> source, int multiplier) {
            for (String element : source.keySet()) {
                addCount(target, element, source.get(element) * multiplier);
            }
        }

        public void addCount(LinkedHashMap<String, Integer> counts, String element, int count) {
            if (!counts.containsKey(element)) {
                counts.put(element, 0);
            }

            counts.put(element, counts.get(element) + count);
        }
    }
}
