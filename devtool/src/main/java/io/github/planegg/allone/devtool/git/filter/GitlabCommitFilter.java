package io.github.planegg.allone.devtool.git.filter;

import org.gitlab4j.api.models.Commit;

public abstract class GitlabCommitFilter {
    private GitlabCommitFilter nextFilter;


    public GitlabCommitFilter setNextFilter(GitlabCommitFilter nextFilter) {
        this.nextFilter = nextFilter;
        return this;
    }

    protected GitlabCommitFilter getNextFilter(){
        return nextFilter;
    }

    protected boolean execute(GitlabCommitFilter filter,Commit commit){
        if (filter == null){
            return true;
        }
        return filter.startFilter(commit);
    }

    public abstract boolean startFilter(Commit commit);
}
