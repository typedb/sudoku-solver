import grakn.client.GraknClient;
import graql.lang.Graql;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SudokuMain {

    public static void loadFromFile(String filePath, GraknClient.Transaction tx) {
        try {
            System.out.println("Loading... " + filePath);
            String s = Files.lines(Paths.get(filePath)).collect(Collectors.joining("\n"));
            Graql.parseList(s).forEach(tx::execute);
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    public static int[][] readSudoku(String filePath) throws IOException {
        int[] sudoku1d = Files.lines(Paths.get(filePath))
                .flatMap(line -> Stream.of(line.split(" "))).mapToInt(Integer::parseInt).toArray();
        int size = (int) Math.sqrt(sudoku1d.length);
        int [][] sudoku = new int[size][size];
        for(int i = 0; i < size ; i++){
            System.arraycopy(sudoku1d, i * size, sudoku[i], 0, size);
        }
        return sudoku;
    }

    public static void main(String ...args) throws IOException {
        String sudokuFilePath = args.length > 0? args[0] : "src/main/resources/sudoku";
        String sudokuSchemaPath = "src/main/resources/sudoku6x6.gql";
        int[][] sudoku = readSudoku(sudokuFilePath);

        try(GraknClient client = new GraknClient("localhost:48555")) {
            String sessionId = String.format("s%s", UUID.randomUUID().toString().toLowerCase().replace("-", ""));
            System.out.println("Using session id: " + sessionId);
            try(GraknClient.Session session = client.session(sessionId)) {
                SudokuSolver solver = new SudokuSolver();

                try (GraknClient.Transaction tx = session.transaction(GraknClient.Transaction.Type.WRITE)) {
                    loadFromFile(sudokuSchemaPath, tx);
                    tx.commit();
                }

                try (GraknClient.Transaction tx = session.transaction(GraknClient.Transaction.Type.WRITE)) {
                    SudokuSolver.Solution solution = solver.solve(tx, sudoku);
                    solution.print();
                }
            }
        }
    }
}
