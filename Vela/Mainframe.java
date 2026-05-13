import javax.swing.*;
import java.awt.*;
public class Mainframe extends JFrame{
    InputPanel inputPanel;
    DrawPanel drawPanel;
    Mainframe(){
        setTitle("VELA");
        setSize(1920,1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        Controller controller= new Controller();
        DrawPanel drawPanel = new DrawPanel();
        DataPanel dataPanel = new DataPanel();
        JSplitPane split =
            new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                dataPanel,
                drawPanel
            );

        split.setResizeWeight(0.50);
        
        add(split, BorderLayout.CENTER);
        ButtonPanel buttonPanel = new ButtonPanel(drawPanel);
        InputPanel inputPanel= new InputPanel(controller, drawPanel, dataPanel);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(inputPanel, BorderLayout.NORTH);
        leftPanel.add(drawPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.CENTER);
        add(dataPanel, BorderLayout.EAST);
        setVisible(true);
    }

}