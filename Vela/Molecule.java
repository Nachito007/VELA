import java.util.*;
public class Molecule{
    ArrayList<Atom> atoms;
    ArrayList<Bond> bonds;
    ArrayList<String> messages;
    boolean valid;
    String shape;
    double bondAngle;

    Controller cont = new Controller();
    Molecule(){
        atoms = new ArrayList<>();
        bonds = new ArrayList<>();
        messages = new ArrayList<>();
        valid = true;

    }

    public void addBond(Atom a1, Atom a2, int type){
        Bond b = new Bond(a1,a2,type);
        ElementData d1 = ChemData.getElementData(a1.element);
        ElementData d2 = ChemData.getElementData(a2.element);
        if(d1 != null && d2 != null){
            b.polarityDifference = Math.abs(d1.electronegativity - d2.electronegativity);
        }
        bonds.add(b);
        a1.bonds.add(b);
        a2.bonds.add(b);

    }

    public double getMolecularMass(){

        double total = 0;

        for(Atom a : atoms){

            ElementData data =
                ChemData.getElementData(a.element);

            if(data != null){
                total += data.atomicMass;
            }
        }

        return total;
    }

    public int getTotalValenceElectrons(){

        int total = 0;

        for(Atom a : atoms){

            ElementData data =
                ChemData.getElementData(a.element);

            if(data != null){
                total += data.valenceElectrons;
            }
        }

        return total;
    }

    public double getAverageElectronegativity(){

        double total = 0;

        int count = 0;

        for(Atom a : atoms){

            ElementData data =
                ChemData.getElementData(a.element);

            if(data != null){

                total += data.electronegativity;

                count++;
            }
        }

        if(count == 0){
            return 0;
        }

        return total / count;
    }

    public void setShape(String shape){
        this.shape = shape;
    }

    public void setBondAngle(double angle){
        this.bondAngle = angle;
    }

    public void generateGeometry(){

    }

    public void addAtom(Atom atom){
        atoms.add(atom);
    }

    public void addMessage(String message){
        messages.add(message);
        valid = false;
    }

    public void addNote(String message){
        messages.add(message);
    }

    public boolean hasMessages(){
        return messages.size() > 0;
    }

    public String getMessages(){
        StringBuilder builder = new StringBuilder();

        for(String message : messages){
            builder.append(message).append("\n");
        }

        return builder.toString();
    }

    public void clearMolecule(){
        atoms.clear();    
    }

    public Atom getMainAtom(){
        Atom best = null; //We're looking to get the atom with the most amount of bonds, so that we can use that as our 'central' atom for angle, hybridization, electron geometry and regular geometry!!!
        int maxConnections = -1;
        for(Atom a : atoms){
            //Never use Hydrogen tho
            if(a.element.equals("H")) continue;
            int connections = a.bonds.size();
            if(connections > maxConnections){
                maxConnections = connections;
                best = a;
            }
        }
        //if no bonds, just return first Atom
        if(best == null && atoms.size() > 0){
            return atoms.get(0);
        }
        return best;
    }

    public boolean isPolar(){
        Atom main = getMainAtom();
        if(main == null) return false;
        if(main.molecularGeometry == null) return false;

        if(bonds.size() == 1){
            return bonds.get(0).polarityDifference > 0.4;
        }

        //Bent & Pyramidal are always polar
        if(main.molecularGeometry.equals("bent") || main.molecularGeometry.equals("trigonal_pyramidal") || main.molecularGeometry.equals("seesaw") || main.molecularGeometry.equals("square_pyramidal")){
            return true;
        }
        //Symmetrical geometries are usually nonpolar...
        if(main.molecularGeometry.equals("linear") || main.molecularGeometry.equals("trigonal_planar") || main.molecularGeometry.equals("tetrahedral") || main.molecularGeometry.equals("trigonal_bipyramidal") || main.molecularGeometry.equals("octahedral") || main.molecularGeometry.equals("square_planar")){
            //Check if all surrounding atoms are the same
            ArrayList<Atom> neighbors = cont.getNeighbors(main);
            if(neighbors.size() == 0) {return false;}
            String first = neighbors.get(0).element;
            for(Atom a : neighbors){
                if(!a.element.equals(first)){
                    return true; //If we find a different surrounding atom, it's polar
                }
            }
            return false; //All surrounding atoms are the same = nonpolar
        }
        return true; //default
    }

    public String getIMF(){
        boolean polar = isPolar();
        boolean hBond = false;
        for(Bond b : bonds){
            String a1 = b.a1.element;
            String a2 = b.a2.element;
            if((a1.equals("H") && (a2.equals("O") || a2.equals("N") || a2.equals("F"))) || (a2.equals("H") && (a1.equals("O") || a1.equals("N") || a1.equals("F")))){
                hBond = true;
            }
        }
        if(hBond){
            return "Hydrogen Bonding";
        }
        if(polar){
            return "Dipole-Dipole";
        }
        return "London Dispersion";
    }
}
