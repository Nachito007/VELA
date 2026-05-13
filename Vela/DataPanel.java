import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

public class DataPanel extends JPanel {

    JLabel formulaLabel;
    JLabel sigmaLabel;
    JLabel piLabel;
    JLabel massLabel;
    JLabel valenceLabel;
    JLabel electronegativityLabel;
    JLabel polarityLabel;
    JLabel imfLabel;

    
    JTextArea atomInfoArea;
    JTextArea bondInfoArea;
    JTextArea geometryArea;

    public DataPanel(){

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(320, 700));
        setBackground(new Color(40,40,40));

        JLabel generalTitle = createLabel("General Molecular Information");
        add(generalTitle);

        formulaLabel = createLabel("Formula: ");
        massLabel = createLabel("Molar Mass: ");
        valenceLabel = createLabel("Total Valence Electrons: ");
        electronegativityLabel = createLabel("Average Electronegativity: ");
        polarityLabel = createLabel("Polarity: ");
        imfLabel = createLabel("IMF: ");
        sigmaLabel = createLabel("Sigma Bonds: ");
        piLabel = createLabel("Pi Bonds: ");

        add(formulaLabel);
        add(massLabel);
        add(valenceLabel);
        add(electronegativityLabel);
        add(polarityLabel);
        add(imfLabel);
        add(sigmaLabel);
        add(piLabel);

        add(Box.createVerticalStrut(15));

        JLabel geometryTitle = createLabel("Geometry Analysis");
        add(geometryTitle);

        geometryArea = createTextArea();
        add(new JScrollPane(geometryArea));

        add(Box.createVerticalStrut(15));

        JLabel atomTitle = createLabel("Unique Atom Data");
        add(atomTitle);

        atomInfoArea = createTextArea();
        add(new JScrollPane(atomInfoArea));

        add(Box.createVerticalStrut(15));

        JLabel bondTitle = createLabel("Unique Bond Data");
        add(bondTitle);

        bondInfoArea = createTextArea();
        add(new JScrollPane(bondInfoArea));
    }

    public JLabel createLabel(String text){
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("x", Font.BOLD, 14));
        return label;
    }

    public JTextArea createTextArea(){
        JTextArea area = new JTextArea(10,20);
        area.setEditable(false);
        area.setBackground(new Color(30,30,30));
        area.setForeground(Color.WHITE);
        area.setFont(new Font("x", Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    public void updateData(Molecule m, String formula){
        if(m == null){
            return;
        }

        formulaLabel.setText("Formula: " + formula);

        massLabel.setText(
            "Molar Mass: "
            + String.format("%.3f g/mol", m.getMolecularMass())
        );

        valenceLabel.setText(
            "Total Valence Electrons: "
            + m.getTotalValenceElectrons()
        );

        electronegativityLabel.setText(
            "Average Electronegativity: "
            + String.format("%.2f", m.getAverageElectronegativity())
        );

        polarityLabel.setText(
            "Polarity: "
            + (m.isPolar() ? "Polar" : "Nonpolar")
        );

        imfLabel.setText("IMF: " + m.getIMF());

        int sigma = 0;
        int pi = 0;

        for(Bond b : m.bonds){
            sigma++;

            if(b.type == 2){
                pi += 1;
            }

            if(b.type == 3){
                pi += 2;
            }
        }

        sigmaLabel.setText("Sigma Bonds: " + sigma);
        piLabel.setText("Pi Bonds: " + pi);
        

        updateGeometryArea(m);
        updateAtomInfoArea(m);
        updateBondInfoArea(m);
    }

    

    public void updateGeometryArea(Molecule m){
        StringBuilder geometryBuilder = new StringBuilder();

        for(Atom a : m.atoms){
            if(a.bonds.size() <= 1){
                continue;
            }

            geometryBuilder
            .append(a.element)
            .append("\n");

            geometryBuilder
            .append("Molecular Geometry: ")
            .append(a.molecularGeometry)
            .append("\n");

            geometryBuilder
            .append("Electron Geometry: ")
            .append(a.electronGeometry)
            .append("\n");

            geometryBuilder
            .append("Hybridization: ")
            .append(a.hybridization)
            .append("\n");

            geometryBuilder
            .append("Bonding Regions: ")
            .append(a.bonds.size())
            .append("\n");

            geometryBuilder
            .append("Lone Pairs: ")
            .append(a.lonePairs)
            .append("\n");

            geometryBuilder
            .append("Bond Angle: ")
            .append(String.format("%.1f degrees", a.bondAngle))
            .append("\n\n");
        }

        if(geometryBuilder.length() == 0){
            geometryBuilder.append("No central atom geometry available.\n");
        }

        geometryArea.setText(geometryBuilder.toString());
    }

    public void updateAtomInfoArea(Molecule m){
        HashSet<String> uniqueAtoms = new HashSet<>();
        StringBuilder atomBuilder = new StringBuilder();

        for(Atom a : m.atoms){
            if(uniqueAtoms.contains(a.element)){
                continue;
            }

            uniqueAtoms.add(a.element);

            ElementData data = ChemData.getElementData(a.element);

            if(data == null){
                atomBuilder
                .append(a.element)
                .append("\nNo data available.\n\n");
                continue;
            }

            atomBuilder
            .append(data.name)
            .append(" (")
            .append(data.symbol)
            .append(")")
            .append("\n");

            atomBuilder
            .append("Atomic Number: ")
            .append(data.atomicNumber)
            .append("\n");

            atomBuilder
            .append("Group: ")
            .append(data.groupNumber)
            .append("\n");

            atomBuilder
            .append("Period: ")
            .append(data.period)
            .append("\n");

            atomBuilder
            .append("Type: ")
            .append(data.category)
            .append("\n");

            atomBuilder
            .append("Valence Electrons: ")
            .append(data.valenceElectrons)
            .append("\n");

            atomBuilder
            .append("Formal Charge: ")
            .append(a.getFormalChargeText())
            .append("\n");

            atomBuilder
            .append("Electronegativity: ")
            .append(formatElectronegativity(data.electronegativity))
            .append("\n");

            atomBuilder
            .append("Atomic Mass: ")
            .append(String.format("%.3f g/mol", data.atomicMass))
            .append("\n");

            atomBuilder
            .append("Covalent Radius: ")
            .append(data.covalentRadiusPm)
            .append(" pm\n\n");
        }

        atomInfoArea.setText(atomBuilder.toString());
    }

    public void updateBondInfoArea(Molecule m){
        HashSet<String> uniqueBonds = new HashSet<>();
        StringBuilder bondBuilder = new StringBuilder();

        for(Bond b : m.bonds){
            String uniqueKey = getUniqueBondKey(b);

            if(uniqueBonds.contains(uniqueKey)){
                continue;
            }

            uniqueBonds.add(uniqueKey);

            String name = ChemData.getBondName(
                b.a1.element,
                b.a2.element,
                b.type
            );

            BondData data = ChemData.getBond(b);

            bondBuilder
            .append(name)
            .append("\n");

            if(data != null){
                bondBuilder
                .append("Length: ")
                .append(String.format("%.0f pm", data.lengthPm));

                if(data.estimatedLength){
                    bondBuilder.append(" (estimated)");
                }

                bondBuilder.append("\n");

                if(data.bondEnergyKjMol > 0){
                    bondBuilder
                    .append("Bond Energy: ")
                    .append(String.format("%.0f kJ/mol", data.bondEnergyKjMol))
                    .append("\n");
                }
            }
            else{
                bondBuilder.append("Length: N/A\n");
            }

            bondBuilder
            .append("Electronegativity Difference: ")
            .append(String.format("%.2f", b.polarityDifference))
            .append("\n\n");
        }

        if(bondBuilder.length() == 0){
            bondBuilder.append("No bonds available.\n");
        }

        bondInfoArea.setText(bondBuilder.toString());
    }

    public String getUniqueBondKey(Bond b){
        String first = b.a1.element;
        String second = b.a2.element;

        if(first.compareTo(second) > 0){
            String temp = first;
            first = second;
            second = temp;
        }

        return first + ChemData.getBondSymbol(b.type) + second;
    }

    public String formatElectronegativity(double electronegativity){
        if(electronegativity == 0.0){
            return "N/A";
        }

        return String.format("%.2f", electronegativity);
    }
}
