package com.shinonometn.re.ssim.data.kingo.application.service;

import com.shinonometn.re.ssim.commons.file.fundation.FileServiceAdapter;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class CaterpillarFileService extends FileServiceAdapter<Integer> {
    public CaterpillarFileService(File rootFolder) {
        super(rootFolder, "caterpillar");
    }

    public File dataFolderOfTask(Integer taskId) {
        return new File(contextOf(taskId).getFile(), "data");
    }
}
