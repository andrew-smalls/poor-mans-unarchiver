public class Unarchiver {

    private String baseDirPath;
    private String outputDirPath;

    private final UnarchiverService unarchiverService = new UnarchiverService();

    public Unarchiver(String baseDirPath, String outputDirPath) {
        this.baseDirPath = baseDirPath;
        this.outputDirPath = outputDirPath;
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
