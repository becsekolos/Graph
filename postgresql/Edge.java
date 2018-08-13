package com.postgresql;

/**
 *
 * @author Becse Kolos
 */
public class Edge {
    
    private long id;
    private long point_start;
    private long point_end;

    public Edge(long id, long point_start, long point_end) {
        this.id = id;
        this.point_start = point_start;
        this.point_end = point_end;
    }    

    public long getId() {
        return id;
    }

    public long getPoint_start() {
        return point_start;
    }

    public long getPoint_end() {
        return point_end;
    }   
    
}
