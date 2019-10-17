package com.shinonometn.re.ssim.commons.file.fundation;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Objects;

public class FileServiceAdapter<I extends Serializable> implements FileService<I> {

    public final static FilenameGenerator DEFAULT_GENERATOR = s -> s;

    private final File rootFolder;
    private final String domainName;

    private FilenameGenerator filenameGenerator = DEFAULT_GENERATOR;

    public FileServiceAdapter(File rootFolder, String domainName) {
        this.rootFolder = rootFolder;
        this.domainName = domainName;

        File currentContextFolder = new File(rootFolder, domainName);
        if (!currentContextFolder.exists() && !currentContextFolder.mkdir())
            throw new RuntimeException("Could not create domain folder " + currentContextFolder.getAbsolutePath());
    }

    public FileContext contextOf(I id) {
        if (Objects.isNull(id)) throw new NullPointerException();
        FileContext fileContext = new FileContext();
        fileContext.setRootPath(getRootFolder().toPath());
        fileContext.setDomainPath(String.format("%s/%s", getDomainName(), String.valueOf(id)));
        fileContext.setFile(new File(getRootFolder(), fileContext.getDomainPath()));
        return fileContext;
    }

    @Override
    public FileContext save(I i, String filename, InputStream inputStream) throws IOException {
        FileContext fileContext = contextOf(i);
        if (!fileContext.exists() && !fileContext.getFile().mkdir())
            throw new IllegalStateException("Could not create entity folder " + fileContext.getDomainPath());

        String newFilename = filenameGenerator.get(filename);
        FileContext newFile = fileContext.contextOf(newFilename);
        File targetFile = newFile.getFile();
        IOUtils.copyLarge(inputStream, new FileOutputStream(targetFile));
        return newFile;
    }

    @Override
    public FileContext get(I i, String filename) {
        return contextOf(i).contextOf(filename);
    }

    @Override
    public void delete(I i) throws IOException {
        FileContext fileContext = contextOf(i);
        if (fileContext.exists()) {
            FileUtils.deleteDirectory(fileContext.getFile());
        }
    }

    public File getRootFolder() {
        return rootFolder;
    }

    public String getDomainName() {
        return domainName;
    }

    public FilenameGenerator getFilenameGenerator() {
        return filenameGenerator;
    }

    public void setFilenameGenerator(FilenameGenerator filenameGenerator) {
        this.filenameGenerator = filenameGenerator;
    }
}
