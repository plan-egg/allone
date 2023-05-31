package io.github.planegg.allone.devtool.git.filter;

import org.gitlab4j.api.models.Commit;

public class NoMergeFilter extends GitlabCommitFilter {



    @Override
    public boolean startFilter(Commit commit) {
        if (commit.getTitle().startsWith("Merge ")){
            return false;
        }
        return super.execute(getNextFilter(),commit);
    }
}
