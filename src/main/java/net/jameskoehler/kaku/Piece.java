package net.jameskoehler.kaku;

public class Piece {

    private PieceInfo type;
    private PieceInfo affiliation;

    private boolean hasMoved;

    public Piece() {
        //Default constructor for specifically blank pieces
        type = PieceInfo.BLANK;
    }

    public Piece(PieceInfo _type, PieceInfo _affiliation){
        //Constructor to handle pieces that matter for castling before moving
        type = _type;
        affiliation = _affiliation;

        hasMoved = false;
    }

    public Piece(PieceInfo _type, PieceInfo _affiliation, boolean _hasMoved){
        //Constructor to handle piece update after moving
        type = _type;
        affiliation = _affiliation;
        //this should usually get updated
        hasMoved = _hasMoved;
    }

    public PieceInfo getType() {
        return type;
    }
    public void setType(PieceInfo _type) {
        this.type = _type;
    }

    public PieceInfo getAffiliation() {
        return affiliation;
    }
    public void setAffiliation(PieceInfo _affiliation) {
        this.affiliation = _affiliation;
    }

    public boolean getHasMoved() {
        return hasMoved;
    }
    public void setHasMoved(boolean _hasMoved) {
        this.hasMoved = _hasMoved;
    }
}
