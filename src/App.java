public class App {
    public static void main(String[] args) {
        String baseDirPath = "b:\\Clean\\Zips\\Under 1 MB\\";
        String outputDirPath = "b:\\Clean\\Zips\\Under 1 MB\\Unzipped\\";

        int cores = Runtime.getRuntime().availableProcessors();
        int maxParallelism = (int) (cores * 0.7);
        Unarchiver unarchiver = new Unarchiver(baseDirPath, outputDirPath, maxParallelism);

        unarchiver.unarchive();
    }
}
