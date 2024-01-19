public class PuzzleInput {
    long day;
    String parseType;
    String fileName;
    String fileNameSample;

    public PuzzleInput(long d, String p, String f, String fs) {
        day = d;
        parseType = p;
        fileName = "src/inputFiles/" + f;
        fileNameSample = "src/inputFiles/" + fs;
    }

    public PuzzleInput() {
        day = -1;
        parseType = "";
        fileName = "";
        fileNameSample = "";
    }

}
