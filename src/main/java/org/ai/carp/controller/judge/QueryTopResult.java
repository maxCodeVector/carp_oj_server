package org.ai.carp.controller.judge;

import org.ai.carp.model.dataset.BaseDataset;
import org.ai.carp.model.judge.BaseCase;

import java.util.*;
import java.util.stream.Collectors;

public class QueryTopResult {

    // Query cache
    private static Map<String, List<BaseCaseLite>> queryCache = new HashMap<>();
    private static Map<String, Long> queryCacheTime = new HashMap<>();

    public static List<BaseCaseLite> getFinalList(String datasetId) {
        if (queryCache.containsKey(datasetId)) {
            return queryCache.get(datasetId);
        } else {
            return new ArrayList<>();
        }
    }

    private List<BaseCaseLite> baseCases;

    private List<BaseCaseLite> getUserList(List<BaseCaseLite> fullList) {
        List<BaseCaseLite> partList = new ArrayList<>();
        for (BaseCaseLite c : fullList) {
            partList.add(c);
            if (partList.size() >= 20) {
                break;
            }
        }
        return partList;
    }

    public QueryTopResult(BaseDataset dataset, List<BaseCase> cases, Set<String> invalidUids, boolean admin) {
        if (dataset.isFinalJudge()) {
            if (queryCache.containsKey(dataset.getId())) {
                if (new Date().getTime() - queryCacheTime.get(dataset.getId()) < 60000L) {
                    baseCases = queryCache.get(dataset.getId());
                    if (!admin) {
                        baseCases = getUserList(baseCases);
                    }
                    return;
                }
            }
            Map<String, BaseCaseLite> caseMap = new HashMap<>();
            for (BaseCase c : cases) {
                if (invalidUids.contains(c.getUserId())) {
                    continue;
                }
                if (!caseMap.containsKey(c.getUserId())) {
                    caseMap.put(c.getUserId(),
                            new BaseCaseLite(c.getUser().getUsername(), c.getTime(), c.getResult()));
                } else {
                    caseMap.get(c.getUserId()).addResult(c);
                }
            }
            baseCases = caseMap.values().stream().filter(c -> c.getCount() >= 3)
                    .collect(Collectors.toList());
            baseCases.sort(Comparator.comparing(BaseCaseLite::getResult));
            queryCache.put(dataset.getId(), baseCases);
            queryCacheTime.put(dataset.getId(), new Date().getTime());
            if (!admin) {
                baseCases = getUserList(baseCases);
            }
        } else {
            baseCases = new ArrayList<>();
            Set<String> uids = new HashSet<>();
            for (BaseCase c : cases) {
                if (!uids.contains(c.getUser().getId())) {
                    baseCases.add(new BaseCaseLite(c));
                    uids.add(c.getUser().getId());
                    if (!admin && baseCases.size() >= QueryTopController.COUNT_LEADERBOARD) {
                        break;
                    }
                }
            }
        }
    }

    public List<BaseCaseLite> getBaseCases() {
        return baseCases;
    }
}

