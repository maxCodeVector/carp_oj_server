package org.ai.carp.controller.dataset;

import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.BaseDataset;
import org.ai.carp.model.user.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dataset/all")
public class DatasetAllController {

    @GetMapping
    public DatasetAllResponse get(HttpSession session) {
        User user = UserUtils.getUser(session, User.MAX);
        List<BaseDataset> datasets = new ArrayList<>();
        datasets.addAll(Database.getInstance().getCarpDatasets().findAll());
        datasets.addAll(Database.getInstance().getIseDatasets().findAll());
        datasets.addAll(Database.getInstance().getImpDatasets().findAll());
        datasets = datasets.stream().filter(BaseDataset::isEnabled).collect(Collectors.toList());
        if (user.getType() > User.ADMIN) {
            datasets = datasets.stream().filter(d -> !d.isFinalJudge()).collect(Collectors.toList());
        }
        return new DatasetAllResponse(datasets);
    }

}

class DatasetAllResponse {
    private List<BaseDataset> datasets;

    public DatasetAllResponse(List<BaseDataset> datasets) {
        this.datasets = datasets;
    }

    public List<BaseDataset> getDatasets() {
        return datasets;
    }
}
