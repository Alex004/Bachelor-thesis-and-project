package com.example.myapplication.Utils;

//define Node
public class Node implements Comparable<Node>{

    public final String value;
    public Edge[] adjacencies;
    public double shortestDistance = Double.POSITIVE_INFINITY;
    public Node parent;

    public Node(String val){
        value = val;
    }

    public String toString(){
        return value;
    }

    public int compareTo(Node other){
        return Double.compare(shortestDistance, other.shortestDistance);
    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Node)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Node c = (Node) o;

        // Compare the data members and return accordingly
        return this.value.equals(c.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

}
