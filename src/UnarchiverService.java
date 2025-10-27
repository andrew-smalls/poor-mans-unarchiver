import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnarchiverService {

    private final Semaphore concurrencyLimiter;

    public UnarchiverService(int maxParallelism) {
        this.concurrencyLimiter = new Semaphore(maxParallelism);
    }

    public void unarchive(String baseDirPath, String outputDirPath) {
        File baseDir = getOrCreateFile(baseDirPath);
        File outputDir = getOrCreateFile(outputDirPath);
        if (!baseDir.exists() || !outputDir.exists()) {
            throw new IllegalArgumentException("Base directory or output directory does not exist");
        }
        File[] filesInDir = listDirContents(baseDir);
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            parseDir(filesInDir, outputDirPath, executor);
        }
    }

    public File[] listDirContents(File someDir) {
        File[] listOfFiles = someDir.listFiles();
        if (listOfFiles == null) {
            System.out.println("No files found in directory: " + someDir);
        } else {
            System.out.println("Found " + listOfFiles.length + " files.");
        }
        return listOfFiles;
    }

    private void parseDir(File[] listOfFiles, String outputDirPath, ExecutorService executor) {
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (file.getName().endsWith(".zip")) {
                    executor.submit(() -> {
                        try {
                            System.out.println("Acquiring concurrency limiter.");
                            concurrencyLimiter.acquire();
                            unzipFile(file, outputDirPath + "\\" + file.getName().replace(".zip", ""));
                        } catch (Exception e) {
                            System.err.println("Failed to unzip: " + file.getName() + " - " + e.getMessage());
                        } finally {
                            concurrencyLimiter.release();
                            System.out.println("Releasing concurrency limiter.");
                        }
                    });
                } else {
                    System.out.println("Skipping " + file.getName() + " because it is not a zip file.");
                }
            } else if (file.isDirectory()) {
                System.out.println("Directory: " + file.getName());
            }
        }
    }

    public File getOrCreateFile(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
        } catch (Exception e) {
            System.out.println("Could not open file: " + filePath + ". Reason: " + e.getMessage());
        }
        if (file == null || (!file.exists() && !file.mkdirs())) {
            System.out.println("Could not create directory: " + filePath);
        }
        return file;
    }

    private static void unzipFile(File zipFile, String destDir) {
        String threadName = Thread.currentThread().toString();
        System.out.printf("[%s] Starting unzip: %s%n to directory %s.", threadName, zipFile.getName(), destDir);
        File destDirectory = new File(destDir);
        if (!destDirectory.exists()) {
            if (!destDirectory.mkdirs()) {
                System.out.println("Failed to create directory: " + destDir);
                return;
            }
        }
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                try {
                    handleZipEntry(zis, zipEntry, destDirectory);
                } catch (Exception e) {
                    System.out.println("Error processing entry: " + zipEntry.getName() + " - " + e.getMessage());
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (Exception e) {
            System.out.println("Error unzipping file: " + zipFile.getName() + " - " + e.getMessage());
        }
    }

    private static void handleZipEntry(ZipInputStream zis, ZipEntry zipEntry, File destDirectory) throws Exception {
        File newUnzipFile = newFile(destDirectory, zipEntry);
        System.out.println("Extracting to: " + newUnzipFile.getAbsolutePath());
        if (zipEntry.isDirectory()) {
            if (!newUnzipFile.isDirectory() && !newUnzipFile.mkdirs()) {
                throw new Exception("Failed to create directory " + newUnzipFile);
            }
        } else {
            File parent = newUnzipFile.getParentFile();
            if (!parent.isDirectory() && !parent.mkdirs()) {
                throw new Exception("Failed to create directory " + parent);
            }

            try (FileOutputStream fos = new FileOutputStream(newUnzipFile)) {
                int len;
                byte[] buffer = new byte[1024];
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
            } catch (Exception e) {
                throw new Exception("Error writing file " + newUnzipFile + " - " + e.getMessage());
            }
        }
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws Exception {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new Exception("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}
