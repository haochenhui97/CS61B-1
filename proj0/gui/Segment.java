package gui;

import java.awt.Point;

/**
 * Represents a line segment as a pair of Points.
 * 
 * @author P. N. Hilfinger.
 */
class Segment {
    /** The segment P0 P1. */
    Segment(Point p0, Point p1) {
        this._p0 = p0;
        this._p1 = p1;
    }

    /** Return the first argument to my constructor. */
    Point p0() {
        return this._p0;
    }

    /** Return the second argument to my constructor. */
    Point p1() {
        return this._p1;
    }

    /**
     * Return true iff I have the same SET of endpoints as L, which must be a
     * Segment. Equality of endpoints is identity (pointer equality or ==).
     */
    @Override
    public boolean equals(Object L) {
        Segment s = (Segment) L;
        return this._p0 == s._p0 && this._p1 == s._p1 || this._p0 == s._p1 && this._p1 == s._p0;
    }

    @Override
    public int hashCode() {
        return this._p0.hashCode() * this._p1.hashCode();
    }

    /** My endpoints. */
    private Point _p0, _p1;

}
