package com.shinonometn.re.ssim.commons.file.fundation;

/**
 * Created by cattenlinger on 2017/10/31.
 */
public interface FilenameGenerator {
    /**
     * Generate a new filename
     *
     * @param origin origin filename, might be empty("")
     * @return new filename
     */
    String get(String origin);
}
