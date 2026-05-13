import java.util.*;
public class Controller{
    VSEPRGeometry geo = new VSEPRGeometry();
    LewisStructureBuilder lewisBuilder = new LewisStructureBuilder();

    public ArrayList<String> parseFormula (String formula){
        return lewisBuilder.parseFormula(formula);
    }

    public void buildStructure(Molecule molecule, Atom start){
        if(start == null){
            return;
        }

        start.setPosition(0, 0, 0);
        buildRecursive(start, null);
    }

    public void buildRecursive(Atom current, Atom parent){

        ArrayList<Atom> neighbors = getNeighbors(current);
        neighbors.sort((a,b) -> a.element.compareTo(b.element));

        int bondingRegions = current.bonds.size();
        int lonePairs = current.lonePairs;

        String shape = geo.getShape(bondingRegions, lonePairs);
        // Find geometry, electron geometry, hybridization, and bond angle for EVERY atom!!!!!!!!!
        current.molecularGeometry = shape;
        current.electronGeometry = geo.getElectronGeom(bondingRegions, lonePairs);
        current.hybridization = geo.getHybridization(bondingRegions, lonePairs);
        current.bondAngle = geo.getBondAngle(shape);
        ArrayList<double[]> positions = geo.generatePositions(shape);

        int i = 0;

        for (Atom neighbor : neighbors){

            if (neighbor == parent) continue;

            if (neighbor.positioned) continue;

            if (i >= positions.size()) break;

            double[] pos = positions.get(i);

            neighbor.setPosition(
                current.x + pos[0],
                current.y + pos[1],
                current.z + pos[2]
            );

            buildRecursive(neighbor, current);
            i++;
        }

    }

    public ArrayList<Atom> getNeighbors(Atom a){
        ArrayList<Atom> neighbors = new ArrayList<>();
        for(Bond b : a.bonds){
            if(b.a1 == a){neighbors.add(b.a2);}
            else {neighbors.add(b.a1);}

        }
        return neighbors;
    }

    public Molecule generateFromFormula(String formula){
        Molecule m = lewisBuilder.build(formula);
        Atom start = m.getMainAtom();

        if(start != null){
            buildStructure(m,start);
        }

        return m;
    }

}