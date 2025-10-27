public class Unarchiver {

    private String baseDirPath;
    private String outputDirPath;
    private final UnarchiverService unarchiverService;

    public Unarchiver(String baseDirPath, String outputDirPath, int maxParallelism) {
        this.baseDirPath = baseDirPath;
        this.outputDirPath = outputDirPath;
        this.unarchiverService = new UnarchiverService(maxParallelism);
        System.out.println("Unarchiving " + baseDirPath + " to " + outputDirPath + " with parallelism " + maxParallelism);
    }

    public void unarchive() {
        unarchiverService.unarchive(baseDirPath, outputDirPath);
    }

    public String getBaseDirPath() {
        return baseDirPath;
    }

    public void setBaseDirPath(String baseDirPath) {
        this.baseDirPath = baseDirPath;
    }

    public String getOutputDirPath() {
        return outputDirPath;
    }

    public void setOutputDirPath(String outputDirPath) {
        this.outputDirPath = outputDirPath;
    }
}
