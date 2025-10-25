import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    public static void main(String[] args) {

        final String baseDirPath = "b:\\Clean\\Automate Unarchiving\\";
        File baseDir = null;
        try {
            baseDir = new File(baseDirPath);
        } catch (Exception e) {
            System.out.println("Could not open file: " + baseDirPath);
        }
        if (baseDir == null) {
            return;
        }

        File[] listOfFiles = baseDir.listFiles();
        if (listOfFiles == null) {
            System.out.println("No files found in directory: " + baseDirPath);
            return;
        }
        for (File file : listOfFiles) {
            if (file.isFile()) {
                //System.out.println("File: " + file.getName());
                if (file.getName().endsWith(".zip")) {
                    unzipFile(file, baseDir + "\\unzipped\\" + file.getName().replace(".zip", ""));
                } else if (file.getName().endsWith(".rar")) {
                    unrarFile(file, baseDir);
                }
            } else if (file.isDirectory()) {
                System.out.println("Directory: " + file.getName());
            }
        }
    }

    private static void unzipFile(File zipFile, String destDir) {
        System.out.println("Unzipping file: " + zipFile.getName() + " to directory: " + destDir);
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

    private static void unrarFile(File rarFile, File destDir) {
        // Placeholder for unrar logic
        System.out.println("Unraring file: " + rarFile.getName() + " to directory: " + destDir.getPath());
    }
}