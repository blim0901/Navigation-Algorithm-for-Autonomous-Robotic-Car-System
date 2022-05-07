package algo;

import entities.Cell;

import java.util.ArrayList;

public class Permute {

    public static ArrayList<ArrayList<Cell>> permute(ArrayList<Cell> o) {
        ArrayList<ArrayList<Cell>> results = new ArrayList<>();
        if (o == null || o.size() == 0) {
            return results;
        }
        ArrayList<Cell> result = new ArrayList<>();
        dfs(o, results, result);
        return results;
    }

    public static void dfs(ArrayList<Cell> o, ArrayList<ArrayList<Cell>> results, ArrayList<Cell> result) {
        if (o.size() == result.size()) {
            ArrayList<Cell> temp = new ArrayList<>(result);
            results.add(temp);
        }
        for (int i=0; i<o.size(); i++) {
            if (!result.contains(o.get(i))) {
                result.add(o.get(i));
                dfs(o, results, result);
                result.remove(result.size() - 1);
            }
        }
    }


}
