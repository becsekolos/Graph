package com.postgresql;

/**
 *
 * @author Becse Kolos
 */
public class Node {
    
    private long id;
    private long fisrtNeighbour;
    private long secondNeighbour;

    public Node(long id, long fisrtNeighbour, long secondNeighbour) {
        this.id = id;
        this.fisrtNeighbour = fisrtNeighbour;
        this.secondNeighbour = secondNeighbour;               
    }

    public long getId() {
        return id;
    }
    
    public Edge makeEdge() {
        return new Edge(id, fisrtNeighbour, secondNeighbour);
    }
       
}
