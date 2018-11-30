package com.shinonometn.re.ssim.commons.file.fundation;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

public class FileContext {
    // The file entity, maybe null
    private File file;

    // Absolute path of the root folder
    private Path rootPath;

    // Relative path of the current domain, related to the rootPath
    private String domainPath;

    /**
     * Check if target context file is exists,
     * rootPath and domainPath is necessary
     *
     * @return true or false
     * @throws NullPointerException if rootPath or domainPath is null
     */
    public boolean exists() {
        return getFile().exists();
    }

    public File getFile() {
        if (Objects.isNull(file)) {
            assertCanGetContextFolder();
            this.file = new File(rootPath.toFile(), domainPath);
        }

        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Path getRootPath() {
        return rootPath;
    }

    public void setRootPath(Path rootPath) {
        this.rootPath = rootPath;
    }

    public String getDomainPath() {
        return domainPath;
    }

    public void setDomainPath(String domainPath) {
        this.domainPath = domainPath;
    }

    public FileContext contextOf(String filename) {
        FileContext fileContext = new FileContext();
        fileContext.setDomainPath(String.format("%s/%s", domainPath, filename));
        fileContext.setRootPath(this.rootPath);
        fileContext.setFile(new File(rootPath.toFile(), fileContext.getDomainPath()));
        return fileContext;
    }

    public File fileOf(String filename) {
        if(!this.exists())
            throw new IllegalStateException("Context folder not exists");

        if (!this.getFile().isDirectory())
            throw new IllegalStateException("Not a directory");

        return new File(this.file, filename);
    }

    /*
     *
     * Private procedure
     *
     * */
    private void assertCanGetContextFolder() {
        if (Objects.isNull(rootPath)) throw new NullPointerException();
        if (Objects.isNull(domainPath)) throw new NullPointerException();
    }
}
