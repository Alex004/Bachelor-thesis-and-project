package com.example.myapplication.Utils;

//define Edge
public class Edge{
    public final Node target;
    public final double weight;
    public Edge(Node targetNode, double weightVal){
        target = targetNode;
        weight = weightVal;
    }
}
