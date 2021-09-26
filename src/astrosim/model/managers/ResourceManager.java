package astrosim.model.managers;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.stream.Stream;

public class ResourceManager {
    private ResourceManager() {}

    private static final Path rootFP = Path.of(System.getProperty("user.dir"));

    public static FileInputStream readFile(Path filePath) {
        File file = new File(String.valueOf(filePath));
        if (!file.exists()) {
            try {
                Files.createDirectories(filePath.getParent());
                if (ResourceManager.class.getResource("/" + rootFP.relativize(filePath)) == null) {
                    // no default, simply create blank file
                    Files.createFile(filePath);
                } else {
                    // copy default over
                    Files.copy(Objects.requireNonNull(ResourceManager.class.getResourceAsStream("/" + rootFP.relativize(filePath))), filePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return is;
    }

    public static FileInputStream readFile(String fp) {
        return readFile(rootFP.resolve(Path.of(fp)));
    }

    public static void restoreDefault(Path fp) throws IOException {
        // guarantee the file exists, returning its fp
        restoreDefault(rootFP.relativize(fp).toString());
    }

    public static void restoreDefault(String fp) throws IOException {
        Path filePath = rootFP.resolve(Path.of(fp));
        // copy default over
        Files.copy(Objects.requireNonNull(ResourceManager.class.getResourceAsStream("/" + fp)), filePath);
    }

    public static FileOutputStream writeFile(Path fp) {
        // guarantee the file exists, returning its fp
        return writeFile(rootFP.relativize(fp).toString());
    }

    public static FileOutputStream writeFile(String fp) {
        Path filePath = rootFP.resolve(Path.of(fp));
        File file = new File(String.valueOf(filePath));
        if (!file.exists()) {
            try {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return os;
    }

    public static void guaranteeExists(Path fp, String defaultPath) {
        // guarantee the file exists, returning its fp
        guaranteeExists(rootFP.relativize(fp).toString(), defaultPath);
    }

    public static Path guaranteeExists(String fp, String defaultPath) {
        // guarantee the file exists, returning its fp
        Path filePath = rootFP.resolve(Path.of(fp));
        File file = new File(String.valueOf(filePath));
        if (!file.exists()) {
            try {
                Files.createDirectories(filePath.getParent());
                // copy default over
                Files.copy(Objects.requireNonNull(ResourceManager.class.getResourceAsStream(defaultPath)), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }

    public static Path getPath(String append) {
        return rootFP.resolve(Path.of(append));
    }

    public static void copyFromResourceDirectory(String source, Path target) {
        Stream<Path> toBeCopied = null;
        try {
            Path sourcePath = Path.of(Objects.requireNonNull(ResourceManager.class.getResource(source)).toURI());
            toBeCopied = Files.list(sourcePath);
            Files.createDirectories(target);
            toBeCopied.forEach(p -> {
                try {
                    Files.copy(p, target.resolve(sourcePath.relativize(p)), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        } finally {
            if (toBeCopied != null) toBeCopied.close();
        }
    }

    public static void copyFromResourceDirectory(String source, String target) {
        copyFromResourceDirectory(source, rootFP.resolve(Path.of(target)));
    }

    public static Stream<Path> getFilesInDirectory(String directory) {
        return getFilesInDirectory(rootFP.resolve(Path.of(directory)));
    }

    public static Stream<Path> getFilesInDirectory(Path dirPath) {
        try {
            return Files.list(dirPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
