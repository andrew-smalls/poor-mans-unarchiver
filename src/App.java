public class App {
    public static void main(String[] args) {
        String baseDirPath = "b:\\Clean\\Winrars\\";
        String outputDirPath = baseDirPath + "Unzipped\\";

        int cores = Runtime.getRuntime().availableProcessors();
        int maxParallelism = (int) (cores * 0.7);
        Unarchiver unarchiver = new Unarchiver(baseDirPath, outputDirPath, maxParallelism);

        unarchiver.unarchive();
    }
}
