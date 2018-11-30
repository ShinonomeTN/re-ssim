package com.shinonometn.re.ssim.commons.file.fundation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public interface FileService<EntityId extends Serializable> {
    FileContext save(EntityId id, String filename, InputStream inputStream) throws IOException;

    FileContext get(EntityId id, String filename);

    String getDomainName();

    File getRootFolder();
}
