package cz.muni.pb138.annotationsystem.frontend.controller;

import au.com.bytecode.opencsv.CSVReader;
import com.sun.javafx.collections.MappingChange;
import cz.muni.pb138.annotationsystem.backend.api.*;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
    private PackManager packManager;

    @Inject
    private AnswerManager answerManager;

    @Inject
    private EvaluationManager evaluationManager;

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
    public @ResponseBody String doPost(RedirectAttributes redirectAttributes,
                         ServletRequest req,
                         @RequestParam("file") MultipartFile[] files,
                         @RequestParam("value") String[] values)
                          {

        if (!(files.length < 1)) {

            List<String> answersList = new ArrayList<String>();
            String[] nextLineAnswers;

            List<String> noiseList = new ArrayList<String>();
            String[] nextLineNoise;

            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(files[0].getInputStream()));
                CSVReader reader = new CSVReader(bufferedReader);

                while ((nextLineAnswers = reader.readNext()) != null) {
                    for (int i = 0; i < nextLineAnswers.length; i++) {
                        answersList.add(nextLineAnswers[i]);
                    }
                }
            } catch (IOException e) {
                req.setAttribute("error", e);
                return "view-error";
            }

            if (files.length > 1) {

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(files[1].getInputStream()));
                    CSVReader reader = new CSVReader(bufferedReader);

                    while ((nextLineNoise = reader.readNext()) != null) {
                        for (int i = 0; i < nextLineNoise.length; i++) {
                            noiseList.add(nextLineNoise[i]);
                        }
                    }
                } catch (IOException e) {
                    req.setAttribute("error", e);
                    return "view-error";
                }
            }

            Pack pack = new Pack(answersList.get(0),files[0].getOriginalFilename(),Double.parseDouble(values[0]),Double.parseDouble(values[1]));

            try{
            packManager.createPack(pack, answersList, noiseList, Integer.parseInt(values[2]));
                }
             catch (Exception e) {
                req.setAttribute("error", e);
                return "view-error";}

            //System.out.println("AAAAAAAAAAAAAAAAAAAAAA" + pack + ".." + values[0] + ".." + values[1] );
            //System.out.println("BBBBBBBBBBBBBBBBBBBBBB" + pack + ".." + answersList + ".." + noiseList + ".." + values[2] );

            redirectAttributes.addFlashAttribute("pack", pack);

            return "redirect:/view-assign/" + pack.getId();
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

            List<Pack> allPacks =  packManager.getAllPacks();
            req.setAttribute("allPacks", allPacks);

        return "view-stats";
    }

    @RequestMapping("/stats/{pack}")
    public String stats(ServletRequest req, @PathVariable String pack) {
        try {
            Long longPack = Long.parseLong(pack);
            Pack packObj = packManager.getPackById(longPack);
            double width = statisticsManager.getProgressOfPack(packObj);
            req.setAttribute("Pack", packObj);
            req.setAttribute("width", width);
            List<Subpack> allSubPacks =  subpackManager.getSubpacksInPack(packObj);
            req.setAttribute("allSubPacks", allSubPacks);
            Map statMap = new HashMap();

             for (Subpack subPack: allSubPacks) {
                List<Person> userList = subpackManager.getPersonsAssignedToSubpack(subPack);
                Map subpackStat = new HashMap();
                for (Person user: userList) {
                    subpackStat.put(user, statisticsManager.getProgressOfSubpackForPerson(subPack, user));
                }
                statMap.put(subPack, subpackStat);
            }
            req.setAttribute("stats", statMap);

        } catch (Exception e) {
            return "redirect:/view-error";
        }
        return "view-statsPack";
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

    @RequestMapping(value ="/assign/{packID}/{userID}", method = {RequestMethod.GET})
    public String assignPackGet(ServletRequest req, @PathVariable String packID, @PathVariable String userID) {

        try {
            long longPackID = Long.parseLong(packID);
            long longUserID = Long.parseLong(userID);

            Pack pack = packManager.getPackById(longPackID);
            List<Subpack> subpackList = subpackManager.getSubpacksInPack(pack);
            Person user = personManager.getPersonById(longUserID);

            req.setAttribute("user", user);
            req.setAttribute("subPackList", subpackList);
            req.setAttribute("pack", pack);
        } catch (DaoException e) {
            return "redirect:/view-error";
        }
        return "view-assignPack";
    }


    @RequestMapping("/assign/{id}")
    public String assign(ServletRequest req, @PathVariable String id ) {
        try {
            long packID = Long.parseLong(id);
            Pack pack = packManager.getPackById(packID);
            List<Person> users = personManager.getAllPersons();
            req.setAttribute("users", users);
            req.setAttribute("pack", pack);

        } catch (DaoException e) {
            return "redirect:/view-error";
        }

        return "view-assign";

    }


}
