package com.shinonometn.re.ssim.std.term.info.v1.service;

import com.shinonometn.re.ssim.std.base.RSComponentVersion;
import com.shinonometn.re.ssim.std.base.RSFeature;
import com.shinonometn.re.ssim.std.term.info.v1.model.TermIdentityInfo;

import java.util.List;
import java.util.Optional;

@RSComponentVersion("1")
public interface TermQueryService {
    @RSFeature("term.all")
    Optional<List<TermIdentityInfo>> queryAllTerms();
}
