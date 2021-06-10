package com.example.myapplication.Models;





import com.example.myapplication.Utils.Pair;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


// This class is used for sending graph structure between activities
// The structure is memorate in a map with key being the number of node and value being a list of
// pair with first being one of adjacency nodes and value being the distance to the adjacent node
public class GraphAdjacenciesDTO implements Serializable {
    private Map<Integer, List<Pair<Integer,Integer>>> graph;

    public GraphAdjacenciesDTO() {
    }

    public GraphAdjacenciesDTO(Map<Integer, List<Pair<Integer, Integer>>> graph) {
        this.graph = graph;
    }

    public Map<Integer, List<Pair<Integer, Integer>>> getGraph() {
        return graph;
    }

    public void setGraph(Map<Integer, List<Pair<Integer, Integer>>> graph) {
        this.graph = graph;
    }
}

