package cz.muni.pb138.annotationsystem.frontend.controller;

import au.com.bytecode.opencsv.CSVReader;
import cz.muni.pb138.annotationsystem.backend.api.*;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.inject.Inject;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/**")
public class MainController {

    @Inject
    private AnswerManager answerManager;

    @Inject
    private EvaluationManager evaluationManager;

    @Inject
    private PackManager packManager;

    @Inject
    private PersonManager personManager;

    @Inject
    private StatisticsManager statisticsManager;

    @Inject
    private SubpackManager subpackManager;

    @RequestMapping("/")
    public String primaryView(ServletRequest req) {

        try {
            req.setAttribute("person", personManager.getPersonById((long) 1));
        } catch (DaoException e) {
            return "redirect:/view-error";
        }

        return "view-admin";

    }

    @RequestMapping(value = "/upload", method = {RequestMethod.GET})
    public String doGet(ServletRequest req) {

        return "view-upload";
    }

    @RequestMapping(value = "/upload", method = {RequestMethod.POST})
    public String doPost(ServletRequest req, @RequestParam MultipartFile file) {

        if (!file.isEmpty()) {

            List<String> mojecsv = new ArrayList<String>();
            Integer counter = 0;
            String[] nextLine;

            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(file.getInputStream()));
                CSVReader reader = new CSVReader(in);

                while ((nextLine = reader.readNext()) != null) {
                    for (int i = 0; i < nextLine.length; i++) {
                        counter++;
                        mojecsv.add(nextLine[i]);
                    }
                }
            } catch (IOException e) {
                req.setAttribute("error", e);
                return "view-error";
            }

            req.setAttribute("csvContent", mojecsv);
            req.setAttribute("csvLength", counter - 2);
            packManager.createPack(null, mojecsv, null, counter - 2);

            return "view-assign";
        } else {
            req.setAttribute("error", "File empty.");
            return "view-error";
        }

    }

    @RequestMapping("/packages")
    public String packages(HttpSession session, ServletRequest req) {

        try {
            req.setAttribute("subpacks", subpackManager.getSubpacksAssignedToPerson(personManager.getPersonById((long) 1)));
        } catch (DaoException e) {
            return "redirect:/view-error";
        }

        return "view-packages";

    }

    @RequestMapping(value = "/packages/{subpack}", method = {RequestMethod.GET})
    public String getPackage(RedirectAttributes redirectAttributes, ServletRequest req, @PathVariable String subpack) {

        try {
            Long longSubpack = Long.parseLong(subpack);
            Subpack thisSubpack = subpackManager.getSubpackById(longSubpack);
            redirectAttributes.addFlashAttribute("thisSubpack", thisSubpack);

        } catch (DaoException e) {
            return "redirect:/view-error";
        }

        return "redirect:/mark/{subpack}/10";
    }

    @RequestMapping("/stats")
    public String stats(ServletRequest req) {

        return "view-stats";

    }

    @RequestMapping("/mark/{subpack}/{answer}")
    public String mark(ServletRequest req, @PathVariable String subpack, @PathVariable String answer) {

        try {
            Long longAnswer = Long.parseLong(answer);
            Answer thisAnswer = answerManager.getAnswerById(longAnswer);
            req.setAttribute("thisAnswer", thisAnswer);
        } catch (DaoException e) {
            return "redirect:/view-error";
        }
        return "view-mark";

    }

    @RequestMapping("/assign")
    public String assign(ServletRequest req) {

        return "view-assign";

    }

}
