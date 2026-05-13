import java.util.*;
public class VSEPRGeometry{
    int bondingPairs;
    int lonePairs;
    public String getShape(int bondingPairs, int lonePairs){

        int total = bondingPairs + lonePairs;

        if (bondingPairs == 1) return "linear";

        if (total == 2) return "linear";

        if (total == 3){
            if (lonePairs == 0) return "trigonal_planar";
            if (lonePairs == 1) return "bent";
        }

        if (total == 4){
            if (lonePairs == 0) return "tetrahedral";
            if (lonePairs == 1) return "trigonal_pyramidal";
            if (lonePairs == 2) return "bent";
        }

        if (total == 5){
            if (lonePairs == 0) return "trigonal_bipyramidal";
            if (lonePairs == 1) return "seesaw";
            if (lonePairs == 2) return "t_shape";
            if (lonePairs == 3) return "linear";
        }

        if (total == 6){
            if (lonePairs == 0) return "octahedral";
            if (lonePairs == 1) return "square_pyramidal";
            if (lonePairs == 2) return "square_planar";
        }

        return "unknown";
    }

    public double generatePositions(){
        return 1.0;
    }

    public double getBondAngle(String shape){
        switch (shape){
            case "linear":
                return 180.0;
            case "trigonal_planar":
                return 120.0;
            case "tetrahedral":
                return 109.5;
            case "trigonal_pyramidal":
                return 107.0;
            case "bent":
                return 104.5;
            case "trigonal_bipyramidal":
                return 90.0;
            case "square_planar":
                return 90.0;
            case "square_pyramidal":
                return 90.0;
            case "octahedral":
                return 90.0;
            case "seesaw":
                return 90.0;
            case "t_shape":
                return 90.0;
            default:
                return 0.0;
        }
    }
    //3d coordinates for the surroundin atoms
    public ArrayList<double[]> generatePositions(String shape){
        double scale = 2.0;
        ArrayList<double[]> positions = new ArrayList<>();
        double r= 2.0; //bond length
        switch (shape){
            case "linear":
                positions.add(new double []{r,0,0});
                positions.add(new double []{-r,0,0});
                break;
            case "trigonal_planar":
                positions.add(new double []{r,0,0});
                positions.add(new double []{-0.5 * r,Math.sqrt(3)/2 * r,0});
                positions.add(new double []{-0.5 * r,-Math.sqrt(3)/2 * r,0});
                break;
            case "tetrahedral":
                positions.add(new double []{r,r,r});
                positions.add(new double []{-r,-r,-r});
                positions.add(new double []{-r,r,-r});
                positions.add(new double []{r,-r,-r});
                break;
            case "trigonal_pyramidal":

                double z = 0.6;
                double r1 = Math.sqrt(1 - z*z);

                positions.add(new double[]{ r1 * scale, 0, -z * scale });

                positions.add(new double[]{
                        -r1 * 0.5 * scale,
                        r1 * Math.sqrt(3)/2 * scale,
                        -z * scale
                    });

                positions.add(new double[]{
                        -r1 * 0.5 * scale,
                        -r1 * Math.sqrt(3)/2 * scale,
                        -z * scale
                    });

                break;
            case "bent":

                double angle = Math.toRadians(52.25);
                scale = 2.0;

                positions.add(new double[]{
                        Math.cos(angle) * scale,
                        Math.sin(angle) * scale,
                        0
                    });

                positions.add(new double[]{
                        Math.cos(angle) * scale,
                        -Math.sin(angle) * scale,
                        0
                    });

                break;
            case "trigonal_bipyramidal":
                positions.add(new double []{r,0,0});
                positions.add(new double []{-r,-0,0});
                positions.add(new double []{0,r,0});
                positions.add(new double []{0,-r,0});
                positions.add(new double []{0,0,1.5});
                break;
            case "seesaw":
                positions.add(new double []{r,r,r});
                positions.add(new double []{-r,-r,-r});
                positions.add(new double []{-r,r,-r});
                positions.add(new double []{r,-r,-r});
                break;
            case "t_shape":
                positions.add(new double []{r,0,0});
                positions.add(new double []{-r,0,0});
                positions.add(new double []{0,r,0});
                break;
            case "octahedral":
                positions.add(new double []{r,0,0});
                positions.add(new double []{-r,0,0});
                positions.add(new double []{0,r,0});
                positions.add(new double []{0,-r,0});
                positions.add(new double []{0,0,-r});
                positions.add(new double []{0,0,r});
                break;
            case "square_planar":
                positions.add(new double []{r,0,0});
                positions.add(new double []{-r,0,0});
                positions.add(new double []{0,r,0});
                positions.add(new double []{0,-r,0});
                break;
            case "square_pyramidal":
                positions.add(new double []{r,0,0});
                positions.add(new double []{-r,0,0});
                positions.add(new double []{0,r,0});
                positions.add(new double []{0,-r,0});
                positions.add(new double []{0,0,r});
                break;
        }
        return positions;
    }

    public String getElectronGeom(int bondingPairs, int lonePairs){
        int total = bondingPairs + lonePairs;
        switch(total){
            case 2: return "Linear";
            case 3: return "Trigonal Planar";
            case 4: return "Tetrahedral";
            case 5: return "Trigonal Bipyramidal";
            case 6: return "Octahedral"; 
            default: return "Unknown";
        }
    }

    public String getHybridization(int bondingPairs, int lonePairs){
        int total = bondingPairs + lonePairs;
        switch(total){
            case 2: return "sp";
            case 3: return "sp2";
            case 4: return "sp3";
            case 5: return "sp3d";
            case 6: return "sp3d2"; 
            default: return "Unknown";
        }
    }
}