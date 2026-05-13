public class Bond{
    Atom a1;
    Atom a2;
    int type; //single , double, triple bond
    double polarityDifference;
    public Bond(Atom a1, Atom a2, int type){
        this.a1 = a1;
        this.a2 = a2;
        this.type = type;
    }
}