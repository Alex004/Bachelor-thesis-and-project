package com.example.myapplication.Utils;

import androidx.annotation.NonNull;


import com.example.myapplication.Data.Local.Code.CodeEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class DijkstraAlgo {
    /* Dijkstra Algorithm
     *
     *
     */
    public static List<Node> nodes;
//    public static List<Node> unchangedNodes;


    public static void computePaths(Node source) {
        source.shortestDistance = 0;

        //implement a priority queue
        PriorityQueue<Node> queue = new PriorityQueue<Node>();
        queue.add(source);

        while (!queue.isEmpty()) {
            Node u = queue.poll();

			/*visit the adjacencies, starting from
			the nearest node(smallest shortestDistance)*/

            for (Edge e : u.adjacencies) {

                Node v = e.target;
                double weight = e.weight;

                //relax(u,v,weight)
                double distanceFromU = u.shortestDistance + weight;
                if (distanceFromU <= v.shortestDistance) {

					/*remove v from queue for updating
					the shortestDistance value*/
                    queue.remove(v);
                    v.shortestDistance = distanceFromU;
                    v.parent = u;
                    queue.add(v);

                }
            }
        }
    }

    public static List<Node> getShortestPathTo(List<Node> target) {

        List<Node> finalPath = new ArrayList<>();

//
//        for(Node node1 = node; node1!=null; node1 = node1.parent){
//            path.add(node1);
//            value += node1.shortestDistance;
//        }

        double shortestDistance = Double.POSITIVE_INFINITY;
        for (Node node : target) {
            double value = 0;

            //trace path from target to source
            List<Node> path = new ArrayList<Node>();
            for (Node node1 = node; node1 != null; node1 = node1.parent) {
                path.add(node1);
                value += node1.shortestDistance;
            }
            if (value < shortestDistance) {
                shortestDistance = value;
                finalPath = path;
            }
        }

        //reverse the order such that it will be from source to target
        Collections.reverse(finalPath);

        return finalPath;
    }


    public static void setMap(Map<Integer, List<Pair<Integer, Integer>>> graphAdjacencies, List<CodeEntity> codeEntityList) {


        Map<Integer, Node> stringNodeMap = new HashMap<>();

        for (CodeEntity codeEntity : codeEntityList) {
            stringNodeMap.put(codeEntity.getId(), new Node(codeEntity.getCode()));
        }

        for (Map.Entry<Integer, List<Pair<Integer, Integer>>> entry : graphAdjacencies.entrySet()) {
            Node node = stringNodeMap.get(entry.getKey());
            List<Edge> edgeList = new ArrayList<>();
            for (Pair<Integer, Integer> pair : entry.getValue()) {
                edgeList.add(new Edge(stringNodeMap.get(pair.first), pair.second));
            }
            System.out.println(edgeList.size());
            node.adjacencies = new Edge[edgeList.size()];
            edgeList.toArray(node.adjacencies);
            stringNodeMap.put(entry.getKey(), node);
        }

//        unchangedNodes = new ArrayList<>(stringNodeMap.values());
        nodes = new ArrayList<>(stringNodeMap.values());

//        //compute paths
//        computePaths(nodeList.get(6));
//
//        //print shortest paths
//		/*
//		for(Node n: nodes){
//			System.out.println("Distance to " +
//				n + ": " + n.shortestDistance);
//    		List<Node> path = getShortestPathTo(n);
//    		System.out.println("Path: " + path);
//		}*/
//
//        List<Node> path = getShortestPathTo(nodeList.get(3));
//        System.out.println("Path: " + path);

    }


}




