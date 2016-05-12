package cz.muni.pb138.annotationsystem.frontend.controller;

import cz.muni.pb138.annotationsystem.backend.api.PackManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import javax.servlet.ServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/**")
public class MainController {

    @Inject
    private PackManager packManager;

    @RequestMapping("/")
    public String primaryView(ServletRequest req) {

        //TO DO: if user is an administrator return "view-admin" else return "view-packages"

        return "view-admin";

    }

    @RequestMapping("/upload")
    public String upload(ServletRequest req) {

        return "view-upload";

    }

    @RequestMapping("/packages")
    public String packages(ServletRequest req) {

        return "view-packages";

    }

    @RequestMapping("/stats")
    public String stats(ServletRequest req) {

        return "view-stats";

    }

    @RequestMapping("/mark")
    public String mark(ServletRequest req) {

        return "view-mark";

    }

    @RequestMapping("/assign")
    public String assign(ServletRequest req) {

        return "view-assign";

    }


    @RequestMapping("/mark/{animal}")
    public String whatever(ServletRequest req, @PathVariable String animal) {

        packManager.createPack(null, null, 1000);
        req.setAttribute("yellow", "placeholder");

        return "redirect:/pes";

    }


}