public class App {
    public static void main(String[] args) {
        String baseDirPath = "b:\\Clean\\Automate Unarchiving\\";
        String outputDirPath = "b:\\Clean\\Automate Unarchiving\\Unzipped\\";
        Unarchiver unarchiver = new Unarchiver(baseDirPath, outputDirPath);
        unarchiver.unarchive();
    }
}
