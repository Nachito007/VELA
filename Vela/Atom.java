import java.awt.*;
import java.util.*;

public class Atom{
    String element;
    double x,y,z,radius;
    Color color;
    int group;
    ArrayList<Bond> bonds;
    int maxBonds;
    int currentBonds;
    int lonePairs;
    int formalCharge;
    boolean positioned = false;
    Controller c = new Controller();
    String molecularGeometry;
    String electronGeometry;
    String hybridization;
    double bondAngle;
    public Atom(String element){
        bonds = new ArrayList<>();
        this.element = element;
        this.group = getGroup(element);
        switch(element){
            case "H": radius = 15; break;
            case "O": radius = 25; break;
            case "C": radius = 30; break;
            case "N": radius = 25; break;
            default: radius = 20;
        }
        this.maxBonds = getTypicalBonds(element);
        this.currentBonds = 0;
        this.lonePairs = 0;
        this.formalCharge = 0;
    }
    public int getTypicalBonds(String element){
        switch(element){
            case "H": return 1;
            case "O": return 2;
            case "C": return 4;
            case "N": return 3;
            case "P": return 5;
            case "S": return 6;
            case "F":return 1;
            case "Cl":return 1;
            case "Br": return 1;
            case "I" : return 1;
            case "Li": return 1;
            case "Na": return 1;
            case "K": return 1;
            case "Rb": return 1;
            case "Cs": return 1;
            case "Fr": return 1;
            case "Be": return 2;
            case "Mg": return 2;
            case "Ca": return 2;
            case "Sr": return 2;
            case "Ba": return 2;
            case "Ra": return 2;
            default: return 4;
        }
    }
    public int getGroup(String element){
        if(element.equals("H") ||element.equals("Li") || element.equals("Na") ||element.equals("K") ||element.equals("Rb") || element.equals("Cs") || element.equals("Fr")){
            return 1;
        }
        if(element.equals("Be") ||element.equals("Mg") || element.equals("Ca") ||element.equals("Sr") ||element.equals("Ba") || element.equals("Ra")){
            return 2;
        }
        if(element.equals("B") ||element.equals("Al") || element.equals("Ga") ||element.equals("In") ||element.equals("Tl")){
            return 13;
        }
        if(element.equals("C") ||element.equals("Si") || element.equals("Ge") ||element.equals("Sn") ||element.equals("Pb")){
            return 14;
        }
        if(element.equals("N") ||element.equals("P") || element.equals("As") ||element.equals("Sb") ||element.equals("Bi")){
            return 15;
        }
        if(element.equals("O") ||element.equals( "S" )|| element.equals("Se") ||element.equals("Te") ||element.equals("Po")){
            return 16;
        }
        if(element.equals("F") ||element.equals("Cl") || element.equals("Br") ||element.equals( "I" )||element.equals("At")){
            return 17;
        }
        if(element.equals("He") ||element.equals("Ne") || element.equals("Ar") ||element.equals("Kr") ||element.equals("Xe") || element.equals("Rn")){
            return 18;
        }
        return 0;
    }

    public void setPosition(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
        this.positioned = true;

    }

    public Point get2DPosition(){
        int drawX = (int)(x * 100)+300;//Scale & Center the atom
        int drawY = (int)(y * 100)+300;
        return new Point(drawX,drawY);
    }

    void draw(Graphics g, DrawPanel panel) {

        Graphics2D g2 = (Graphics2D) g;
        Point p = panel.project3Dto2D(x, y, z);

        double depth = panel.getDepth(x, y, z);
        double scaleFactor = Math.max(0.5, 1 + depth * 0.1);

        int r = Math.max(14, (int)(radius * scaleFactor * panel.getZoomScale() * 2.25));

        drawSphere(g2, p, r);

        g2.setColor(new Color(235, 235, 235, 170));
        g2.setStroke(new BasicStroke(panel.scaleForZoom(2)));
        g2.drawOval(p.x - r/2, p.y - r/2, r, r);

        drawElementLabel(g2, panel, p, r);

        if(formalCharge != 0){
            drawFormalCharge(g2, panel, p, r);
        }
    }

    void drawSphere(Graphics2D g2, Point p, int r) {
        int x = p.x - r / 2;
        int y = p.y - r / 2;

        Color highlight = blend(color, Color.WHITE, 0.55);
        Color midtone = color;
        Color shadow = blend(color, Color.BLACK, 0.45);

        RadialGradientPaint paint = new RadialGradientPaint(
            new Point(
                x + r / 3,
                y + r / 3
            ),
            r,
            new float[]{0.0f, 0.55f, 1.0f},
            new Color[]{highlight, midtone, shadow}
        );

        Paint oldPaint = g2.getPaint();
        g2.setPaint(paint);
        g2.fillOval(x, y, r, r);
        g2.setPaint(oldPaint);

        int shine = Math.max(4, r / 5);
        g2.setColor(new Color(255, 255, 255, 145));
        g2.fillOval(
            x + r / 4,
            y + r / 5,
            shine,
            Math.max(3, shine * 2 / 3)
        );

        g2.setColor(new Color(0, 0, 0, 70));
        g2.drawArc(
            x + r / 8,
            y + r / 8,
            r * 3 / 4,
            r * 3 / 4,
            210,
            120
        );
    }

    Color blend(Color first, Color second, double amount) {
        double keep = 1.0 - amount;

        int red = (int)(first.getRed() * keep + second.getRed() * amount);
        int green = (int)(first.getGreen() * keep + second.getGreen() * amount);
        int blue = (int)(first.getBlue() * keep + second.getBlue() * amount);

        return new Color(red, green, blue);
    }

    void drawElementLabel(Graphics g, DrawPanel panel, Point p, int r) {
        Font oldFont = g.getFont();
        Font labelFont = oldFont.deriveFont(Font.BOLD, (float)panel.scaleForZoom(18));

        g.setFont(labelFont);
        FontMetrics metrics = g.getFontMetrics();

        int textWidth = metrics.stringWidth(element);
        int textX = p.x - textWidth / 2;
        int textY = p.y + metrics.getAscent() / 2 - 3;

        g.setColor(getReadableTextColor());
        g.drawString(element, textX, textY);
        g.setFont(oldFont);
    }

    void drawFormalCharge(Graphics g, DrawPanel panel, Point p, int r) {
        String chargeText = getFormalChargeText();
        Font oldFont = g.getFont();
        Font chargeFont = oldFont.deriveFont(Font.BOLD, (float)panel.scaleForZoom(18));

        g.setFont(chargeFont);
        FontMetrics metrics = g.getFontMetrics();

        int textWidth = metrics.stringWidth(chargeText);
        int textX = p.x - textWidth / 2;
        int textY = p.y - r / 2 - panel.scaleForZoom(8);

        g.setColor(new Color(255, 245, 160));
        g.drawString(chargeText, textX, textY);
        g.setFont(oldFont);
    }

    Color getReadableTextColor() {
        int brightness =
            (int)(color.getRed() * 0.299
            + color.getGreen() * 0.587
            + color.getBlue() * 0.114);

        if(brightness < 120){
            return Color.WHITE;
        }

        return Color.BLACK;
    }

    String getFormalChargeText() {
        if(formalCharge > 0){
            if(formalCharge == 1){
                return "+";
            }

            return "+" + formalCharge;
        }

        if(formalCharge == -1){
            return "-";
        }

        return "" + formalCharge;
    }

    
}
