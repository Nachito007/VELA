import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class InputPanel extends JPanel implements ActionListener {

    JTextField formulaField;
    JComboBox<String> commonMoleculeBox;
    JButton generateButton;

    Controller controller;

    DrawPanel drawPanel;
    DataPanel dataPanel;

    public InputPanel(
    Controller controller,
    DrawPanel drawPanel,
    DataPanel dataPanel
    ){

        this.controller = controller;
        this.drawPanel = drawPanel;
        this.dataPanel = dataPanel;
        int x = 30;
        int y = 255;
        setLayout(new FlowLayout());

        JLabel label = new JLabel("Formula:");
        label.setForeground(new Color(200,200,200));
        formulaField = new JTextField(15);
        formulaField.setBackground(new Color(200,200,200));

        commonMoleculeBox = new JComboBox<>(
            new String[]{
                "Common molecules",
                "H2O",
                "H3O",
                "NH3",
                "NH4",
                "CO2",
                "CO",
                "CH4",
                "O2",
                "N2",
                "H2",
                "HCl",
                "HCN",
                "CH2O",
                "HCOOH",
                "CH3COOH",
                "BF3",
                "PCl5",
                "SF6",
                "NO3",
                "SO4",
                "CO3",
                "PO4",
                "NaCl"
            }
        );
        commonMoleculeBox.setBackground(new Color(200,200,200));
        commonMoleculeBox.addActionListener(this);

        generateButton = new JButton("Generate");
        generateButton.setBackground(new Color(x,x,x));
        generateButton.setForeground(new Color(y,y,y));
        generateButton.setOpaque(true);

        generateButton.addActionListener(this);

        add(label);
        add(formulaField);
        add(commonMoleculeBox);
        add(generateButton);
        this.setBackground(new Color(x,x,x));
    }

    @Override
    public void actionPerformed(ActionEvent e){

        if(e.getSource() == commonMoleculeBox){
            String selected = (String) commonMoleculeBox.getSelectedItem();

            if(selected != null && !selected.equals("Common molecules")){
                formulaField.setText(selected);
            }

            return;
        }

        String formula = formulaField.getText();

        Molecule m = controller.generateFromFormula(formula);

        drawPanel.updateMolecule(m);

        dataPanel.updateData(m, formula);

        if(!m.valid){
            JOptionPane.showMessageDialog(
                this,
                m.getMessages(),
                "Molecule Warning",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }
}
