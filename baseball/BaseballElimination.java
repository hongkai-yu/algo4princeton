/* *****************************************************************************
 *  Name: Hongkai Yu
 *  Date: 14 Nov 2020
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BaseballElimination {


    private final String[] teams;
    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int[][] against;

    private final Map<String, Integer> teamIndices; // faster for retrieving indices

    // for flow network
    private int[] teamVertices;

    // certificate
    private Set<String> certificate;


    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        int numberOfTeams = in.readInt();

        teams = new String[numberOfTeams];
        wins = new int[numberOfTeams];
        losses = new int[numberOfTeams];
        remaining = new int[numberOfTeams];
        against = new int[numberOfTeams][numberOfTeams];
        teamIndices = new HashMap<>();

        teamVertices = new int[numberOfTeams];

        for (int i = 0; i < numberOfTeams; i++) {
            teams[i] = in.readString();
            wins[i] = in.readInt();
            losses[i] = in.readInt();
            remaining[i] = in.readInt();

            teamIndices.put(teams[i], i);

            for (int j = 0; j < numberOfTeams; j++) {
                against[i][j] = in.readInt();
                if (i > j && against[i][j] != against[j][i])
                    throw new IllegalArgumentException("Invalid Schedule");
            }
        }

    }

    // number of teams
    public int numberOfTeams() {
        return teamIndices.size();

    }

    // all teams
    public Iterable<String> teams() {
        return teamIndices.keySet();
    }

    private int teamIndex(String team) {

        if (!teamIndices.containsKey(team))
            throw new IllegalArgumentException("Invalid team name");

        return teamIndices.get(team);
    }

    // number of wins for given team
    public int wins(String team) {
        return wins[teamIndex(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        return losses[teamIndex(team)];

    }

    // number of remaining games for given team
    public int remaining(String team) {
        return remaining[teamIndex(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        return against[teamIndex(team1)][teamIndex(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        certificate = new HashSet<>();
        int teamIndex = teamIndex(team);

        return isTriviallyEliminated(teamIndex)
                || isNonTriviallyEliminated(teamIndex);

    }

    private boolean isTriviallyEliminated(int teamIndex) {
        int maxWins = wins[teamIndex] + remaining[teamIndex];
        for (int i = 0; i < numberOfTeams(); i++) {
            if (maxWins < wins[i]) {
                certificate.add(teams[i]);
                return true;
            }
        }

        return false;
    }

    private boolean isNonTriviallyEliminated(int teamIndex) {
        FlowNetwork network = makeNetwork(teamIndex);

        double totalCapacity = 0;
        for (FlowEdge e : network.adj(0))
            totalCapacity += e.capacity();

        FordFulkerson fordFulkerson = new FordFulkerson(network, 0, network.V() - 1);

        for (int v = 1; v < numberOfTeams(); v++) { // team vertices
            if (fordFulkerson.inCut(v)) {
                if (v <= teamIndex) {
                    certificate.add(teams[v - 1]);
                }
                else {
                    certificate.add(teams[v]);
                }
            }

        }

        return (totalCapacity - fordFulkerson.value()
                > 0.0001); // because of floating rounding error
    }

    private FlowNetwork makeNetwork(int teamIndex) {

        int maxWins = wins[teamIndex] + remaining[teamIndex];

        int numberOfOtherTeams = numberOfTeams() - 1;
        int numberOfVertices = numberOfOtherTeams * (numberOfOtherTeams - 1) / 2 // number of games
                + numberOfOtherTeams + 2;
        FlowNetwork network = new FlowNetwork(numberOfVertices);

        int source = 0;
        int sink = numberOfVertices - 1;

        int v = 1;

        for (int i = 0; i < numberOfTeams(); i++) {
            if (i == teamIndex)
                teamVertices[i] = -1; // not a vertex
            else {
                teamVertices[i] = v;
                network.addEdge(new FlowEdge(v, sink, maxWins - wins[i]));
                v++;
            }
        }

        for (int i = 0; i < numberOfTeams(); i++) {
            for (int j = i + 1; j < numberOfTeams(); j++) {
                if (i != teamIndex && j != teamIndex) {
                    network.addEdge(new FlowEdge(source, v, against[i][j]));
                    network.addEdge(new FlowEdge(v, teamVertices[i], Double.POSITIVE_INFINITY));
                    network.addEdge(new FlowEdge(v, teamVertices[j], Double.POSITIVE_INFINITY));
                    v++;
                }
            }
        }

        // validate v;
        assert sink == v + 1;

        return network;
    }


    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (isEliminated(team)) // to calculate
            return certificate;
        else
            return null;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);

        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}

