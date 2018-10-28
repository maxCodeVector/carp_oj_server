package org.ai.carp.controller.judge;

import org.ai.carp.model.judge.CARPCase;

import java.util.*;

public class QueryTopResult {

    private List<CARPCaseLite> carpCases;

    public QueryTopResult(List<CARPCase> cases) {
        carpCases = new ArrayList<>();
        Set<String> uids = new HashSet<>();
        for (CARPCase c : cases) {
            if (!uids.contains(c.getUser().getId())) {
                carpCases.add(new CARPCaseLite(c));
                uids.add(c.getUser().getId());
            }
        }
    }

    public List<CARPCaseLite> getCarpCases() {
        return carpCases;
    }
}

class CARPCaseLite {

    private String userName;
    private String datasetName;
    private Date submitTime;
    private double time;
    private int cost;

    public CARPCaseLite(CARPCase carpCase) {
        this.userName = carpCase.getUserName();
        this.datasetName = carpCase.getDatasetName();
        this.cost = carpCase.getCost();
        this.submitTime = carpCase.getSubmitTime();
        this.time = carpCase.getTime();
    }

    public String getUserName() {
        return userName;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public int getCost() {
        return cost;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public double getTime() {
        return time;
    }
}
