package com.shinonometn.re.ssim.data.kingo.application.service;

import com.shinonometn.re.ssim.commons.file.fundation.FileServiceAdapter;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class BundleFileService extends FileServiceAdapter<Integer> {
    public BundleFileService(File rootFolder) {
        super(rootFolder, "bundle");
    }
}
