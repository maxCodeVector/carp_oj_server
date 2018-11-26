package org.ai.carp.controller.judge;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.controller.exceptions.PermissionDeniedException;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.judge.CARPCase;
import org.ai.carp.model.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class GetOutputController {

    @GetMapping("/judge/details")
    public String get(@RequestParam("cid") String cid, Model model, HttpSession session) {
        User user = UserUtils.getUser(session, User.USER);
        if (StringUtils.isEmpty(cid)) {
            throw new InvalidRequestException("No cid!");
        }
        Optional<CARPCase> optCarpCase = Database.getInstance().getCarpCases().findById(cid);
        if (!optCarpCase.isPresent()) {
            throw new InvalidRequestException("Case does not exist!");
        }
        CARPCase carpCase = optCarpCase.get();
        if (!carpCase.getUserId().equals(user.getId())) {
            throw new PermissionDeniedException("You do not own this case!");
        }
        if (carpCase.getStatus() != CARPCase.FINISHED && carpCase.getStatus() != CARPCase.ERROR) {
            throw new InvalidRequestException("Case has not finished!");
        }
        model.addAttribute("exitcode", carpCase.getExitcode());
        model.addAttribute("stdout", carpCase.getStdout());
//        model.addAttribute("stderr", carpCase.getStderr());
        model.addAttribute("stderr", "aa\nbb");
        return "output";
    }

}
