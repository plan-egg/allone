package io.github.planegg.allone.devtool.git;

import java.util.Set;

/**
 * 提交比较结果
 */
public class GitlabRlseCommpareDiffDto {

    private Boolean missInSrc;
    private Boolean missInTgt;
    private Set<String> lessInTarget;
    private Set<String> moreInTarget;

    public Boolean getMissInSrc() {
        return missInSrc;
    }

    public void setMissInSrc(Boolean missInSrc) {
        this.missInSrc = missInSrc;
    }

    public Boolean getMissInTgt() {
        return missInTgt;
    }

    public void setMissInTgt(Boolean missInTgt) {
        this.missInTgt = missInTgt;
    }

    public Set<String> getLessInTarget() {
        return lessInTarget;
    }

    public void setLessInTarget(Set<String> lessInTarget) {
        this.lessInTarget = lessInTarget;
    }

    public Set<String> getMoreInTarget() {
        return moreInTarget;
    }

    public void setMoreInTarget(Set<String> moreInTarget) {
        this.moreInTarget = moreInTarget;
    }
}
