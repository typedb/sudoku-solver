import grakn.client.GraknClient;
import grakn.client.concept.Concept;
import grakn.client.concept.thing.Attribute;
import grakn.client.concept.thing.Thing;
import graql.lang.Graql;
import graql.lang.pattern.Pattern;
import graql.lang.statement.Statement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class SudokuSolver {

    static class Solution {
        private final Map<String, Object> mappings = new HashMap<>();
        private final int size;

        Solution(grakn.client.concept.thing.Relation.Remote solution) {
            this.size = (int) Math.sqrt(solution.type().roles().count());

            solution.rolePlayersMap().forEach((key, value) -> {
                Thing.Remote<?, ?> rp = value.iterator().next();
                Attribute<?> attr = rp.attributes().findFirst().orElse(null);
                mappings.put(key.label().getValue(), attr.value());
            });
        }

        public void print() {
            System.out.println("Found solution:");
            for(int i = 1; i <= size ; i++){
                for(int j = 1; j <= size ; j++){
                    String role = "pos" + (i) + (j);
                    System.out.print(mappings.get(role) + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public Pattern queryPattern(GraknClient.Transaction tx, int[][] initial){
        Statement mainPattern = Graql.var("r");
        Set<Statement> statements = new HashSet<>();

        int size = initial.length;
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                String role = "pos" + (i+1) + (j+1);
                int val = initial[i][j];
                Statement rp = Graql.var(role);
                mainPattern = mainPattern.rel(role, rp);
                if (val != 0 ) {
                    String id = tx.getAttributeType("value").attribute(val).owners().iterator().next().id().getValue();
                    statements.add(rp.id(id));
                }
            }
        }
        statements.add(mainPattern.isa("solution"));
        return Graql.and(statements);
    }

    public Solution solve(GraknClient.Transaction tx, int[][] sudoku){
        printSudoku(sudoku);
        Pattern sudokuPattern = queryPattern(tx, sudoku);
        Concept<?> solutionRelation = tx.stream(Graql.match(sudokuPattern).get().limit(1)).get()
                .map(ans -> ans.get("r"))
                .findFirst().orElse(null);
        return new Solution(solutionRelation.asRelation().asRemote(tx));
    }

    private void printSudoku(int[][] sudoku) {
        System.out.println("Solving the following Sudoku:");
        for (int[] ints : sudoku) {
            for (int j = 0; j < sudoku.length; j++) {
                int val = ints[j];
                System.out.print((val != 0 ? val : ".") + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}