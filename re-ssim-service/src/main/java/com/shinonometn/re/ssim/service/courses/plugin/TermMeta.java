package com.shinonometn.re.ssim.service.courses.plugin;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TermMeta {
    private Integer courseCount;
    private String version;

    public TermMeta() {
    }

    public TermMeta(Integer courseCount) {
        this.courseCount = courseCount;
    }

    public Integer getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(Integer courseCount) {
        this.courseCount = courseCount;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
