package misc.astar;public class Node {        public Location location;    double costFromStart;    double costToGoal;    double totalCost;    Node parent;        @Override	public boolean equals(Object o) {        if(o instanceof Node) {            return this.location.equals(((Node) o).location);        }        return false;    }}