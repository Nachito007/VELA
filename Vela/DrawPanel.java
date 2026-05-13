import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class DrawPanel extends JPanel
implements MouseListener, MouseMotionListener, MouseWheelListener {

    Molecule molecule;

    double angleX = 0;
    double angleY = 0;

    double zoom = 120;
    double baseZoom = 120;

    int lastMouseX;
    int lastMouseY;

    boolean dragging = false;

    public DrawPanel(){

        setBackground(new Color(20,20,20));

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    @Override
    public void paintComponent(Graphics g){

        super.paintComponent(g);

        if(molecule == null){
            return;
        }

        drawBonds(g);

        drawAtomsSorted(g);

        drawCentralLonePairs(g);
    }

    public void drawAtomsSorted(Graphics g){

        ArrayList<Atom> allAtoms = new ArrayList<>();

        allAtoms.addAll(molecule.atoms);

        allAtoms.sort((a,b) ->
                Double.compare(
                    getDepth(a.x,a.y,a.z),
                    getDepth(b.x,b.y,b.z)
                )
        );

        for(Atom a : allAtoms){
            a.draw(g,this);
        }
    }

    public void drawBonds(Graphics g){

        Graphics2D g2 = (Graphics2D) g;

        for(Bond b : molecule.bonds){

            Point p1 = project3Dto2D(b.a1.x,b.a1.y,b.a1.z);
            Point p2 = project3Dto2D(b.a2.x,b.a2.y,b.a2.z);

            drawBond(g2,p1,p2,b.type);
        }
    }

    public void drawCentralLonePairs(Graphics g){
        Atom central = molecule.getMainAtom();

        if(central == null || central.lonePairs <= 0){
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        Point center = project3Dto2D(central.x, central.y, central.z);

        double depth = getDepth(central.x, central.y, central.z);
        double scaleFactor = Math.max(0.5, 1 + depth * 0.1);
        int atomRadius = Math.max(5, (int)(central.radius * scaleFactor * getZoomScale() * 1.8));
        int pairDistance = atomRadius / 2 + scaleForZoom(18);

        g2.setStroke(new BasicStroke(scaleForZoom(2)));

        for(int i = 0; i < central.lonePairs; i++){
            double angle = -Math.PI / 2 + i * (2 * Math.PI / Math.max(central.lonePairs, 3));
            int pairX = center.x + (int)(Math.cos(angle) * pairDistance);
            int pairY = center.y + (int)(Math.sin(angle) * pairDistance);

            double dotOffsetAngle = angle + Math.PI / 2;
            int dx = (int)(Math.cos(dotOffsetAngle) * scaleForZoom(5));
            int dy = (int)(Math.sin(dotOffsetAngle) * scaleForZoom(5));

            drawElectronDot(g2, pairX - dx, pairY - dy);
            drawElectronDot(g2, pairX + dx, pairY + dy);
        }
    }

    public void drawElectronDot(Graphics2D g2, int x, int y){
        int size = scaleForZoom(8);

        g2.setColor(new Color(90, 220, 255));
        g2.fillOval(x - size / 2, y - size / 2, size, size);
        g2.setColor(new Color(220, 250, 255));
        g2.drawOval(x - size / 2, y - size / 2, size, size);
    }

    public void drawBond(Graphics2D g2, Point p1, Point p2, int type){

        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;

        double length = Math.sqrt(dx*dx + dy*dy);

        double px = -dy / length;
        double py = dx / length;

        if(length == 0){
            return;
        }

        int offset = scaleForZoom(10);
        int strokeWidth = scaleForZoom(6);

        g2.setColor(Color.WHITE);

        g2.setStroke(
            new BasicStroke(
                strokeWidth,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
            )
        );

        if(type == 1){
            g2.drawLine(p1.x,p1.y,p2.x,p2.y);
            return;
        }

        if(type == 2){

            g2.drawLine(
                (int)(p1.x + px*offset),
                (int)(p1.y + py*offset),
                (int)(p2.x + px*offset),
                (int)(p2.y + py*offset)
            );

            g2.drawLine(
                (int)(p1.x - px*offset),
                (int)(p1.y - py*offset),
                (int)(p2.x - px*offset),
                (int)(p2.y - py*offset)
            );

            return;
        }

        if(type == 3){

            g2.drawLine(p1.x,p1.y,p2.x,p2.y);

            g2.drawLine(
                (int)(p1.x + px*offset),
                (int)(p1.y + py*offset),
                (int)(p2.x + px*offset),
                (int)(p2.y + py*offset)
            );

            g2.drawLine(
                (int)(p1.x - px*offset),
                (int)(p1.y - py*offset),
                (int)(p2.x - px*offset),
                (int)(p2.y - py*offset)
            );
        }
    }

    public double getZoomScale(){
        return Math.max(0.55, Math.min(2.6, zoom / baseZoom));
    }

    public int scaleForZoom(int value){
        return Math.max(1, (int)Math.round(value * getZoomScale()));
    }

    public Point project3Dto2D(double x, double y, double z){

        double cosY = Math.cos(angleY);
        double sinY = Math.sin(angleY);

        double x1 = x*cosY - z*sinY;
        double z1 = x*sinY + z*cosY;

        double cosX = Math.cos(angleX);
        double sinX = Math.sin(angleX);

        double y1 = y*cosX - z1*sinX;

        int centerX = getWidth()/2;
        int centerY = getHeight()/2;

        int screenX = (int)(x1 * zoom) + centerX;
        int screenY = (int)(y1 * zoom) + centerY;

        return new Point(screenX,screenY);
    }

    public double getDepth(double x,double y,double z){

        double cosY = Math.cos(angleY);
        double sinY = Math.sin(angleY);

        double z1 = x*sinY + z*cosY;

        double cosX = Math.cos(angleX);
        double sinX = Math.sin(angleX);

        return y*sinX + z1*cosX;
    }

    public void updateMolecule(Molecule m){

        this.molecule = m;

        repaint();
    }

    public void rotateY(double degrees){

        angleY += Math.toRadians(degrees);

        repaint();
    }

    public void resetView(){

        angleX = Math.toRadians(30);
        angleY = Math.toRadians(30);
        zoom = baseZoom;
        
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e){

        dragging = true;

        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e){

        dragging = false;
    }

    @Override
    public void mouseDragged(MouseEvent e){

        if(!dragging){
            return;
        }

        int dx = e.getX() - lastMouseX;
        int dy = e.getY() - lastMouseY;

        double sensitivity = 0.01;

        angleY += dx * sensitivity;
        angleX += dy * sensitivity;

        double maxTilt = Math.toRadians(89);

        angleX = Math.max(
            -maxTilt,
            Math.min(maxTilt, angleX)
        );

        lastMouseX = e.getX();
        lastMouseY = e.getY();

        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e){

        zoom -= e.getWheelRotation() * 10;

        zoom = Math.max(40,Math.min(400,zoom));

        repaint();
    }

    @Override public void mouseClicked(MouseEvent e){}

    @Override public void mouseEntered(MouseEvent e){}

    @Override public void mouseExited(MouseEvent e){}

    @Override public void mouseMoved(MouseEvent e){}
}
