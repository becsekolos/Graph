package com.postgresql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Becse Kolos
 */
public class App {

    private final String url = "jdbc:postgresql://localhost:5432/testdb";
    private final String user = "postgres";
    private final String password = "postgres";
    public List<Edge> edgeList = new LinkedList<>();
    public List<Edge> edgeListToWrite = new LinkedList<>();
    public List<Node> nodeList = new LinkedList<>();
    public List<Long> longList = new LinkedList<>();
    //public List<Long> longList2 = new LinkedList<>();
    public List<Long> longListForStartPoints = new LinkedList<>();
    public List<Long> longListForEndPoints = new LinkedList<>();
 
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
    
    public void getRows() {
 
        String SQL = "SELECT id, point_start, point_end FROM testtable";
 
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)) {
            storeRows(rs);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void storeRows(ResultSet rs) throws SQLException {
             
        while (rs.next()) {
            Edge edge = new Edge(rs.getLong("id"), rs.getLong("point_start"), rs.getLong("point_end"));
            edgeList.add(edge);
            //longList.add(rs.getLong("point_start"));   //Itt lehet be-add-olni a sok csv-s adatot...
            //longList.add(rs.getLong("point_end"));
        }
    }
    
    public void insertRows() {
        String SQL = "INSERT INTO outtable(point_start, point_end) "
                + "VALUES(?,?)";
        try (
                Connection conn = connect();
                PreparedStatement statement = conn.prepareStatement(SQL);) {
            int count = 0;
 
            for (Edge edge : edgeListToWrite) {
                
                statement.setLong(1, edgeListToWrite.get(count).getPoint_start());
                statement.setLong(2, edgeListToWrite.get(count).getPoint_end());
 
                statement.addBatch();
                count++;
                if (count % 100 == 0 || count == edgeListToWrite.size()) {
                    statement.executeBatch();
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void conversion() {
        
        Map<Long, Long> map = new LinkedHashMap<>();
        
            longList.add((long)10);         //"Beégetett" adatok a kisebb mintás teszteléshez...
            longList.add((long)12);
            longList.add((long)11);
            longList.add((long)13);
            longList.add((long)13);
            longList.add((long)15);
            longList.add((long)12);
            longList.add((long)11);
            longList.add((long)10);
            longList.add((long)16);
            longList.add((long)15);
            longList.add((long)14);
            longList.add((long)19);
            longList.add((long)17);
            longList.add((long)17);
            longList.add((long)18);
            longList.add((long)18);
            longList.add((long)20);
            longList.add((long)10);
            longList.add((long)17);
        
        for(Long i : longList) {                //Megszámolja a kezdő, illetve végpontok előfordulását (számát)
            if (map.containsKey(i)) {
                map.put(i, map.get(i) + 1);
            } else {
                map.put(i, (long)1);
            }
        }    
        
        Set<Long> set = new HashSet<>();
        int j = longList.size();
        boolean logicalGate = false;
        
        for(int i = 0; i < j; i++) {                //A prigram logikája, gráf "átkonvertálása"
            if(logicalGate == true) {
                i=0;
            }
            long selected = longList.get(i);
            long selectedsPair;
            int selected2Index;
            //long selected2 = longList.get(selected2Index);
            long selected2sPair;
            if(map.get(selected) == (long)2 && !set.contains(selected)) {
                if((i % 2) != 0) {
                    selectedsPair = longList.get(i-1);
                    longList.remove(selected);
                    longList.remove(selectedsPair);
                    logicalGate=true;
                }
                else {
                    selectedsPair = longList.get(i+1);
                    longList.remove(selected);
                    longList.remove(selectedsPair);
                    logicalGate=true;
                }
                selected2Index = longList.lastIndexOf(selected);
                
                if((selected2Index % 2) != 0) {
                    selected2sPair = longList.get(selected2Index-1);
                    longList.remove(selected2Index);
                    longList.remove(selected2Index-1);
                    logicalGate=true;
                }
                else {
                    selected2sPair = longList.get(selected2Index+1);
                    longList.remove(selected2Index+1);
                    longList.remove(selected2Index);
                    logicalGate=true;
                }
                set.add(selected);
                j = j-2;
                longList.add(selectedsPair);
                longList.add(selected2sPair); 
                
            }
            else {
                logicalGate=false;
            }
        }
        
        for(int i = 0; i < longList.size(); i++) {              //Kezdő -és végpontok különválasztása
            if((i % 2) != 0) {
                longListForEndPoints.add(longList.get(i));          
            }
            else {
                longListForStartPoints.add(longList.get(i));                             
            }
        }
        
        for(int i = 0; i < longListForStartPoints.size(); i++) {                //Rendezés
            if(longListForStartPoints.get(i) > longListForEndPoints.get(i)) {
                long start = longListForStartPoints.get(i);
                long end = longListForEndPoints.get(i);
                longListForStartPoints.set(i, end);
                longListForEndPoints.set(i, start);
            }
        }
        
        for(int i = 0; i < longListForStartPoints.size(); i++) {                                    //Adatok előkészítése PostgreSQL konverzióra
            Edge edge = new Edge(0, longListForStartPoints.get(i), longListForEndPoints.get(i));
            edgeListToWrite.add(edge);
        }
        
        Set<Edge> toWrite = new HashSet<>();       
        
        for(Edge edge : edgeListToWrite) {
            toWrite.add(edge);
        }
        edgeListToWrite.clear();
        edgeListToWrite.addAll(toWrite);  
        
        for(int i = 0; i < edgeListToWrite.size(); i++) {
           System.out.println(edgeListToWrite.get(i).getPoint_start() + "-" + edgeListToWrite.get(i).getPoint_end());
        }
        
        /*int j = longList.size();              //Próbálkozás 1
        Set<Long> set = new HashSet<>();
        
        for(int i = 0; i < j; i++) {
            long selected = longList.get(i);
            long selectedsPair;
            int selected2Index;
            //long selected2 = longList.get(selected2Index);
            long selected2sPair;
            if(map.get(selected) == (long)2 && !set.contains(selected)) {
                if((i % 2) != 0) {
                    selectedsPair = longList.get(i-1);
                }
                else {
                    selectedsPair = longList.get(i+1);
                }
                selected2Index = longList.lastIndexOf(selected);
                
                if((selected2Index % 2) != 0) {
                    selected2sPair = longList.get(selected2Index-1);
                }
                else {
                    selected2sPair = longList.get(selected2Index+1);
                }
                
                nodeList.add(new Node(selected, selectedsPair, selected2sPair)); 
                set.add(selected);
            }
        }
        for(Node n : nodeList) {
            edgeList.add(n.makeEdge());
        }
        
        for(Edge e : edgeList) {
            System.out.println(e.getId() + "-" + e.getPoint_start() + "-" + e.getPoint_end());
            System.out.println();
        }*/
        
        
       /* int j = longList.size();        //Próbálkozás 2
        boolean valami = false;
        
        for(int i = 0; i < j; i++) {
            if(valami == true) {
                i++;
            }
            long selected;
            try{ selected = longList.get(i);
            
            }catch(IndexOutOfBoundsException e){
                break;
            }
            
            long selectedsPair;
            if(map.get(selected) == (long)2) {
                if((i % 2) != 0) {
                    selectedsPair = longList.get(i-1);
                    longList2.add(selectedsPair);
                    longList2.add(selected);
                }
                else {
                    selectedsPair = longList.get(i+1);
                    longList2.add(selected);
                    longList2.add(selectedsPair);
                    valami=true;
                }
                
            }
            else {
                valami=false;
            }
            System.out.println(i);
        }
        System.out.println(longList2);*/
                               
        
        /*for(int i = 0; i < edgeList.size(); i++) {                    //Próbálkozás 3
            //System.out.println(map.get(edgeList.get(i).getPoint_start()));
            if(map.get(edgeList.get(i).getPoint_start()) != (long)2) {
                //map.remove(edgeList.get(i).getPoint_start());
            }
        }
        for(int i = 0; i < edgeList.size(); i++) {
            System.out.println(map.get(edgeList.get(i).getPoint_start()));
        }*/
        
        /*for(int i = 0; i < edgeList.size(); i++) {
            int counter = 0;
            
            Map<Long, Long> map = new LinkedHashMap<Long, Long>();
            

            for(int j = i + 1; j < edgeList.size(); j++) {
                System.out.println(i);
                if(edgeList.get(i).getPoint_start() == edgeList.get(j).getPoint_start() || edgeList.get(i).getPoint_start() == edgeList.get(j).getPoint_end()) {
                    counter++;
                }
            }
            Node node = new Node(edgeList.get(i).getPoint_start(), counter);
            nodeList.add(node);
            
            counter = 0;
            
            for(int j = i + 1; j < edgeList.size(); j++) {
                if(edgeList.get(i).getPoint_end() == edgeList.get(j).getPoint_start() || edgeList.get(i).getPoint_end() == edgeList.get(j).getPoint_end()) {
                    counter++;
                }
            }
            Node node2 = new Node(edgeList.get(i).getPoint_end(), counter);
            nodeList.add(node2);
            
        }*/
    }
    
    public static void main(String[] args) {
        
        App app = new App();
        app.getRows();
        app.conversion();
        app.insertRows();
    }
    
}
