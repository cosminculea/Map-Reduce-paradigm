import utils.Input;
import utils.Output;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Tema2 {

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }

        Scanner scanner = new Scanner(new FileReader(args[1]));
        FileWriter writer = new FileWriter(args[2]);

        /*
            Read the fragment length and the number of files from in_file given as argument
         */

        int fragmentLength = scanner.nextInt();
        int filesNumber = scanner.nextInt();

        String fileName;

        /*
            Create an Input object for every file which needs to be processed
         */

        List<Input> inputValues = new ArrayList<>(filesNumber);
        for (int i = 0; i < filesNumber; i++) {
            fileName = scanner.next();
            RandomAccessFile file = new RandomAccessFile(fileName, "r");
            for (long j = 0; j < file.length(); j += fragmentLength) {
                if (j + fragmentLength >= file.length()) {
                    inputValues.add(new Input(fileName, j, (int) (file.length() - j)));
                } else {
                    inputValues.add(new Input(fileName, j, fragmentLength));
                }
            }
        }

        /*
                Create a MapReduceFiles object with the input values, run the logic for map-reduce
            and print the relevant information from Output objects returned in the output file
            specified in the arguments
         */

        MapReduceFiles mapReduceFiles = new MapReduceFiles(inputValues, Integer.parseInt(args[0]));

        try {
            List<Output> outputList = mapReduceFiles.runLogic();
            outputList.sort(Comparator.comparingDouble(Output::getRang).reversed());
            StringBuilder outputString = new StringBuilder();
            for (Output output : outputList) {
                outputString.append(output.toString()).append("\n");
            }

            writer.write(outputString.toString());
            writer.flush();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }
}
