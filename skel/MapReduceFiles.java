import tasks.MapReduceLogic;
import utils.Input;
import utils.MappedInput;
import utils.Output;
import java.io.*;
import java.util.*;

public class MapReduceFiles extends MapReduceLogic<Input, MappedInput, Output> {
    private static final String delim = ";:/\\?~\\.\\\\,><‘\\[]\\{}\\(\\)!@#\\$%ˆ&-\\+'=\\*”|\n\r\t ";
    private static final String regex = "[;:/?~.\\\\,><‘\\[\\]{}()!@#$%ˆ&\\-+'=*”|\n\r\t ]";

    public MapReduceFiles(List<Input> inputValues, Integer executorsNumber) {
        super(inputValues, executorsNumber);
    }

    /**
     *  If the first letters in the sequence are part of a word which begins in the previous
     * sequence return true, else false. To verify if the characters are in fact letters, it is
     * used a negation which has the following meaning: "if it is not a delimiter then it is a
     * letter". The skip first logic is not used for the first sequence of the file, because it
     * does not have any previous characters, so the offset is verified as well.
     */

    public boolean skipFirst(long offset, char[] seq, char prevChar) {
        return offset != 0 && !delim.contains(String.valueOf(seq[0])) &&
                !delim.contains(String.valueOf(prevChar));
    }

    /**
     *  If the last letters in the sequence are part of a word which continues to the next sequence
     * then update the last word in the array with its actual form.
     */

    public void resolveLast(BufferedReader reader, char[] seq, String[] words) throws IOException {
        int state;

        if (!delim.contains(String.valueOf(seq[seq.length - 1]))) {
            char[] nextChars = new char[1];
            state = reader.read(nextChars, 0, 1);

            while (state == 1 && !delim.contains(String.valueOf(nextChars[0]))) {
                words[words.length - 1] += String.valueOf(nextChars[0]);
                state = reader.read(nextChars, 0, 1);
            }
        }
    }

    /**
     *  The implementation of the map logic for the particular case of the homework.
     *  The function does the following:
     *      - read sequence of "dim" length which starts on the "offset" pointer from the file.
     *  (dim, offset and file name are all included in the input value)
     *      - read previous chars in order to decide whether the first word detected is skipped
     *      - split sequence in words with the delimiters
     *      - run logic for first word skip and last word update
     *      - compute number of appearances for every word and keep the longest words by adding it
     *  to the mapped input object
     * @param value value which is mapped.
     * @return the mapped value
     */

    @Override
    public MappedInput map(Input value) {
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(value.getFileName()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        MappedInput mappedInput = new MappedInput(value.getFileName());
        long offset = value.getOffset();
        int dim = value.getDim();

        try {
            long state;
            int initialPosition;
            char[] seq = new char[dim];
            char[] prevChars = new char[1];
            String[] words;

            if (offset != 0) {
                state = reader.skip(offset - 1);
                assert state == offset - 1;
                state = reader.read(prevChars, 0, 1);
                assert state == 1;
            }

            state = reader.read(seq, 0, dim);
            assert state == dim;

            words = String.valueOf(seq).split(regex);

            initialPosition = 0;
            if (skipFirst(offset, seq, prevChars[0])) {
                initialPosition++;
            }

            resolveLast(reader, seq, words);

            for (int i = initialPosition; i < words.length; i++) {
                mappedInput.addAppearance(words[i]);
            }

            return mappedInput;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *  The implementation of combine logic for the particular case of the homework. The function
     * receives a list of MappedInput values and distributes all MappedInputs values in a particular
     * list depending on the file they originate from.
     * @param listValues the values which need to be combined
     * @return all lists generated for every file
     */

    @Override
    public List<List<MappedInput>> combine(List<MappedInput> listValues) {
        Map<String, List<MappedInput>> resultMap = new HashMap<>();
        List<List<MappedInput>> resultList = new ArrayList<>();

        for (MappedInput value : listValues) {
            if (resultMap.containsKey(value.getFileName())) {
                resultMap.get(value.getFileName()).add(value);
            } else {
                List<MappedInput> list = new ArrayList<>();
                list.add(value);
                resultMap.put(value.getFileName(), list);
            }
        }

        for (String key : resultMap.keySet()) {
            resultList.add(resultMap.get(key));
        }

        return resultList;
    }

    /**
     * The implementation of the reduce logic for the particular case of the homework.
     * The function does the following:
     *      - create a single MappedInput object which contains all the appearances of the words
     * length. This is done to avoid multiple fibonacci calls for the same length.
     *      - calculate the rang based on fibonacci computation of the length, number of appearances
     * for every length and total number of words
     *      - compute the maximum length of the words along with their number of appearances, by
     * iterating through the longestWords list
     * @param listValues the list values which needed to be reduced
     * @return an Output object which contains the rang, the maximum length, the number of
     * appearances of the longest words and the file name.
     */

    @Override
    public Output reduce(List<MappedInput> listValues) {
        double rang = 0;
        int numberAppearances = 0;
        int totalWords = 0;
        int maxLength = 0;

        MappedInput combinedValues = new MappedInput(listValues.get(0).getFileName());

        for (MappedInput value : listValues) {
            combinedValues.combineMappedInputs(value);
        }

        for (Integer length :  combinedValues.getAppearances().keySet()) {
            rang += fibonacciNumber(length + 1) * combinedValues.getAppearances().get(length);
            totalWords += combinedValues.getAppearances().get(length);
        }

        for (String word : combinedValues.getLongestWords()) {
            if (maxLength == word.length()) {
                numberAppearances++;
            } else if (maxLength <  word.length()) {
                numberAppearances = 1;
                maxLength =  word.length();
            }
        }

        rang /= totalWords;

        return new Output(rang, maxLength, numberAppearances, combinedValues.getFileName());
    }


    /**
     * Recursive implementation of the fibonacci algorithm.
     * @return the n-th fibonacci number
     */

    private int fibonacciNumber(int n) {
        if (n == 0) {
            return 0;
        }

        if (n <= 2) {
            return 1;
        }

        return fibonacciNumber(n - 1) + fibonacciNumber(n - 2);
    }
}
