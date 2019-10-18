package com.shinonometn.re.ssim.caterpillar.application.service;

import com.shinonometn.re.ssim.commons.file.fundation.FileServiceAdapter;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class CaterpillarFileManageService extends FileServiceAdapter<Integer> {
    public CaterpillarFileManageService(File rootFolder) {
        super(rootFolder, "caterpillar");
    }

    public File dataFolderOfTask(Integer taskId) {
        return new File(contextOf(taskId).getFile(), "data");
    }

    public File bundleFileOfTask(Integer taskId) {
        return new File(contextOf(taskId).getFile(), "data_bundle.zip");
    }
}
