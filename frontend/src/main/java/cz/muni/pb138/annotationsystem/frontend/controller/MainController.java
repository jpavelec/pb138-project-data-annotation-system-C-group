package cz.muni.pb138.annotationsystem.frontend.controller;

import au.com.bytecode.opencsv.CSVReader;
import com.sun.org.apache.xpath.internal.operations.Bool;
import cz.muni.pb138.annotationsystem.backend.api.*;
import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.inject.Inject;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
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
            req.setAttribute("error", e);
            return "view-error";
        }

        return "view-admin";

    }

    @RequestMapping(value = "/upload", method = {RequestMethod.GET})
    public String doGet(ServletRequest req) {

        return "view-upload";
    }

    @RequestMapping(value = "/upload", method = {RequestMethod.POST})
    public String doPost(RedirectAttributes redirectAttributes,
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
                        if (!nextLineAnswers[i].isEmpty()) answersList.add(nextLineAnswers[i]);
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
                            if (!nextLineNoise[i].isEmpty()) noiseList.add(nextLineNoise[i]);
                        }
                    }
                } catch (IOException e) {
                    req.setAttribute("error", e);
                    return "view-error";
                }
            }

            Pack pack = new Pack(answersList.get(0), files[0].getOriginalFilename(), Double.parseDouble(values[0]), Double.parseDouble(values[1]));

            List<String> helpList = new ArrayList<String>();

            for (int i = 2; i < answersList.size(); i++) {
                helpList.add(answersList.get(i));
            }

            try {
                packManager.createPack(pack, helpList, noiseList, Integer.parseInt(values[2]));

                //subpackManager.updatePersonsAssignment(personManager.getOrCreatePersonByUsername("Karlik"), subpackManager.getSubpacksInPack(pack));

            } catch (Exception e) {
                req.setAttribute("error", e);
                return "view-error";
            }

            //System.out.println("AAAAAAAAAAAAAAAAAAAAAA" + pack + ".." + values[0] + ".." + values[1] );
            //System.out.println("BBBBBBBBBBBBBBBBBBBBBB" + pack + ".." + answersList + ".." + noiseList + ".." + values[2] );

            redirectAttributes.addFlashAttribute("pack", pack);

            return "redirect:/assign/" + pack.getId();
        } else {
            req.setAttribute("error", "File empty.");
            return "view-error";
        }

    }

    @RequestMapping("/packages")
    public String packages(ServletRequest req, HttpServletRequest httpReq) {

        try {
            Person thisPerson = personManager.getOrCreatePersonByUsername(httpReq.getRemoteUser());
            List<Subpack> list = subpackManager.getSubpacksAssignedToPerson(thisPerson);

            Map subpacks = new HashMap();

            for (Subpack subpack : list) {
                Long progress = Math.round(statisticsManager.getProgressOfSubpackForPerson(subpack, thisPerson)*100)/100;
                subpacks.put(subpack, progress);
            }

            req.setAttribute("subpacks", subpacks);

        } catch (DaoException e) {
            req.setAttribute("error", e);
            return "view-error";
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

        return "redirect:/mark/{subpack}";
    }

    @RequestMapping("/stats")
    public String stats(ServletRequest req) {

        List<Pack> allPacks = null;
        try {
            allPacks = packManager.getAllPacks();
        } catch (DaoException e) {
            e.printStackTrace();
        }
        req.setAttribute("allPacks", allPacks);

        return "view-stats";
    }

    @RequestMapping("/stats/{packID}")
    public String stats(ServletRequest req, @PathVariable String packID) {
        try {
            Long longPack = Long.parseLong(packID);
            Pack packObj = packManager.getPackById(longPack);
            Double progress = Math.round(statisticsManager.getProgressOfPack(packObj)*100.0)/100.0;
            if (progress != Double.NaN) {
                progress = Math.round(progress*100.0)/100.0;
            }


            req.setAttribute("pack", packObj);
            req.setAttribute("progress", progress);
            List<Subpack> allSubPacks = subpackManager.getSubpacksInPack(packObj);
            req.setAttribute("allSubPacks", allSubPacks);


            Map subpackUserStats = new HashMap();

            for (Subpack subPack : allSubPacks) {
                List<Person> userList = subpackManager.getPersonsAssignedToSubpack(subPack);
                Map userStats = new HashMap();
                for (Person user : userList) {
                    Double[] currentUserStats = new Double[3];
                    currentUserStats[0] = statisticsManager.getProgressOfSubpackForPerson(subPack, user);

                    if (currentUserStats[0] != null) {
                       currentUserStats[0] = Math.round(currentUserStats[0])*100.0/100.0;
                    } else {
                        currentUserStats[0] = (double) 0;
                    }
                    currentUserStats[1] = statisticsManager.getCohenKappa(user, subPack);
                    if (currentUserStats[1] != null) {
                        currentUserStats[1] = Math.round(currentUserStats[1])*100.0/100.0;
                    }

                    currentUserStats[2] = statisticsManager.averageEvaluationTimeOfSubpackForPerson(subPack, user);
                    if (currentUserStats[2] != null) {
                        currentUserStats[2] = Math.round(currentUserStats[2])*100.0/100.0;
                    } else {
                        currentUserStats[2] = (double) 0;
                    }

                    userStats.put(user, currentUserStats);
                }
                subpackUserStats.put(subPack, userStats);
            }
            req.setAttribute("subpackUserStats", subpackUserStats);


            Map subpackGeneralStats = new HashMap();
            for (Subpack subPack : allSubPacks) {
                Double[] generalStats = new Double[7];

                generalStats[0] = statisticsManager.getProgressOfSubpack(subPack);
                System.out.println("GenStat0" + generalStats[0]);
                if ( !generalStats[0].isNaN()) {
                    generalStats[0] = Math.round(generalStats[0])*100.0/100.0;
                }
                generalStats[1] = statisticsManager.averageCompletionTimeOfSubpack(subPack);
                System.out.println(generalStats[1]);
                if (generalStats[1] != null) {

                    int seconds = (int) ((generalStats[1] / (1000)) % 60); //sec
                    generalStats[2] = (double) seconds;
                    int minutes = (int) ((generalStats[1] / (1000*60)) % 60); //min
                    generalStats[3] = (double) minutes;
                    int hour = (int) ((generalStats[1] / (1000*60*60)) % 24); //hour
                    generalStats[4] = (double) hour;
                    int day = (int) (generalStats[1] / (1000*60*60*24)); //days
                    generalStats[5] = (double) day;
                } else {
                    generalStats[1] = (double) 0;
                    generalStats[2] = (double) 0;
                    generalStats[3] = (double) 0;
                    generalStats[4] = (double) 0;
                    generalStats[5] = (double) 0;
                }
                generalStats[6] = statisticsManager.averageEvaluationTimeOfSubpack(subPack);

                if (generalStats[6] != null) {
                    generalStats[6] = Math.round(generalStats[1])*100.0/100.0;
                } else {
                    generalStats[6] = (double) 0;
                }

                System.out.println(generalStats[6]);

                subpackGeneralStats.put(subPack, generalStats);
            }
            req.setAttribute("subpackGeneralStats", subpackGeneralStats);



        } catch (Exception e) {
            req.setAttribute("error", e);
            return "view-error";
        }
        return "view-statsPack";
    }

    @RequestMapping(value = "/mark/{subpack}", method = {RequestMethod.GET})
    public String markGet(ServletRequest req, @PathVariable String subpack, HttpServletRequest httpReq,
                          Model model, @CookieValue("previousEvalId") String previousEvalId) throws DaoException {

        try {

            req.setAttribute("lStartTime", System.currentTimeMillis());

            Long longSubpack = Long.parseLong(subpack);
            Subpack thisSubpack = subpackManager.getSubpackById(longSubpack);
            req.setAttribute("thisSubpack", thisSubpack);

            Pack pack = thisSubpack.getParent();
            String question = pack.getQuestion();
            req.setAttribute("thisQuestion", question);

            Person person = personManager.getOrCreatePersonByUsername(httpReq.getRemoteUser());

            Long num = Math.round(statisticsManager.getProgressOfSubpackForPerson(thisSubpack, person)*100)/100;

            req.setAttribute("progress", num);


            Boolean isCorrection = (Boolean) model.asMap().get("isCorrection");

            Answer answer;
            Evaluation correction;
            try {
                correction = evaluationManager.getEvaluationById(Long.valueOf(previousEvalId));
            } catch (BeanNotExistsException e) {
                correction = null;
            }

            req.setAttribute("canCorrect", true);

            if (isCorrection != null && isCorrection == true) {
                req.setAttribute("canCorrect", false);
                answer = correction.getAnswer();
                req.setAttribute("thisAnswer", answer);
            } else {
                if (correction == null || !correction.getAnswer().getFromSubpack().equals(thisSubpack)) {
                    req.setAttribute("canCorrect", false);
                }
                try {
                    answer = answerManager.nextAnswer(
                            person,
                            thisSubpack);
                    req.setAttribute("thisAnswer", answer);
                } catch (IllegalStateException e) {
                    if (e.getMessage() == "No more answers left.") {
                        return "view-finished";
                    } else {
                        req.setAttribute("error", e);
                        return "view-error";
                    }
                }
            }

        } catch (Exception e) {
            req.setAttribute("error", e);
            return "view-error";
        }
        return "view-mark";

    }


    @RequestMapping(value = "/correct/{subpackId}", method = {RequestMethod.GET})
    public String markCorrect(ServletRequest req, RedirectAttributes redir) {

        try {
            redir.addFlashAttribute("isCorrection", true);
            return "redirect:/mark/{subpackId}";
        } catch (Exception e) {
            req.setAttribute("error", e);
            return "view-error";
        }

    }



    @RequestMapping(value = "/mark/{subpack}/{answer}/{time}", method = {RequestMethod.POST})
    public String markPost(RedirectAttributes redirectAttributes, @RequestParam String value,
                           ServletRequest req, @RequestParam String isCorrection,
                           @PathVariable String subpack, @PathVariable String answer,
                           @PathVariable String time, HttpServletRequest httpReq,
                           HttpServletResponse res,
                           @CookieValue("previousEvalId") long previousEvalId) {

        try {

            long lEndTime = System.currentTimeMillis();

            long difference = lEndTime - Long.parseLong(time);

            Answer thisAnswer = answerManager.getAnswerById(Long.parseLong(answer));
            Person thisPerson = personManager.getOrCreatePersonByUsername(httpReq.getRemoteUser());

            if (Boolean.valueOf(isCorrection)) {
                Evaluation cor = evaluationManager.getEvaluationById(previousEvalId);
                if (Integer.parseInt(value) == 1) {
                    cor.setRating(Rating.POSITIVE);
                } else if (Integer.parseInt(value) == 2) {
                    cor.setRating(Rating.NEGATIVE);
                } else {
                    cor.setRating(Rating.NONSENSE);
                }
                evaluationManager.correct(cor);
            } else {
                Evaluation evaluation;
                if (Integer.parseInt(value) == 1) {
                    evaluation = new Evaluation(thisPerson, thisAnswer, Rating.POSITIVE, (int) (long) difference);
                } else if (Integer.parseInt(value) == 2) {
                    evaluation = new Evaluation(thisPerson, thisAnswer, Rating.NEGATIVE, (int) (long) difference);
                } else {
                    evaluation = new Evaluation(thisPerson, thisAnswer, Rating.NONSENSE, (int) (long) difference);
                }
                evaluationManager.eval(evaluation);

                Cookie previousEvalCookie = new Cookie("previousEvalId", String.valueOf(evaluation.getId()));
                previousEvalCookie.setPath("/");
                res.addCookie(previousEvalCookie);
            }


            Long longSubpack = Long.parseLong(subpack);
            Subpack thisSubpack = subpackManager.getSubpackById(longSubpack);
            redirectAttributes.addFlashAttribute("thisSubpack", thisSubpack);

            Pack pack = thisSubpack.getParent();
            String question = pack.getQuestion();
            redirectAttributes.addFlashAttribute("thisQuestion", question);

        } catch (DaoException e) {
            req.setAttribute("error", e);
            return "view-error";
        }

        return "redirect:/mark/{subpack}";

    }

    @RequestMapping(value = "/mark/{subpack}/{answer}/{time}/report", method = {RequestMethod.POST})
    public String markReport(RedirectAttributes redirectAttributes, @PathVariable String time, ServletRequest req,
                             @PathVariable String subpack, @PathVariable String answer, HttpServletRequest httpReq,
                             @RequestParam String isCorrection) {

        try {

            long lEndTime = System.currentTimeMillis();

            long difference = lEndTime - Long.parseLong(time);

            Answer thisAnswer = answerManager.getAnswerById(Long.parseLong(answer));
            Person thisPerson = personManager.getOrCreatePersonByUsername(httpReq.getRemoteUser());

            Evaluation evaluation = new Evaluation(thisPerson, thisAnswer, Rating.NONSENSE, (int) (long) difference);
            evaluationManager.eval(evaluation);

            Long longSubpack = Long.parseLong(subpack);
            Subpack thisSubpack = subpackManager.getSubpackById(longSubpack);
            redirectAttributes.addFlashAttribute("thisSubpack", thisSubpack);

            Pack pack = thisSubpack.getParent();
            String question = pack.getQuestion();
            redirectAttributes.addFlashAttribute("thisQuestion", question);

        } catch (DaoException e) {
            req.setAttribute("error", e);
            return "view-error";
        }

        return "redirect:/mark/{subpack}";

    }

    @RequestMapping(value = "/assign/{packID}/{userID}", method = {RequestMethod.GET})
    public String assignPackGet(ServletRequest req, @PathVariable String packID, @PathVariable String userID) {
        try {
            long longPackID = Long.parseLong(packID);
            long longUserID = Long.parseLong(userID);

            Pack pack = packManager.getPackById(longPackID);
            List<Subpack> subpackList = subpackManager.getSubpacksInPack(pack);
            Person user = personManager.getPersonById(longUserID);

            req.setAttribute("user", user);
            req.setAttribute("pack", pack);

            Map subpackMap = new HashMap();
            Map subpackUserMap = new HashMap();
            boolean allAssigned = true;

            for (Subpack subPack : subpackList) {
                List<Person> users = subpackManager.getPersonsAssignedToSubpack(subPack);
                int size = users.size();
                subpackMap.put(subPack, size);
                subpackUserMap.put(subPack, users);
                if(size == 0)
                    allAssigned = false;
            }
            req.setAttribute("subpackMap", subpackMap);
            req.setAttribute("allAssigned", allAssigned);
            req.setAttribute("subpackUserMap", subpackUserMap);

        } catch (DaoException e) {
            req.setAttribute("error", e);
            return "view-error";
        }
        return "view-assignPack";
    }

    @RequestMapping(value ="/assign/{packID}/{userID}", method = {RequestMethod.POST})
    public String assignPackGet(ServletRequest req,
                                @PathVariable long packID,
                                @PathVariable String userID,
                                @RequestParam("value") String[] values) {
        try {
            List<Subpack> subpackList = new ArrayList<>();
            for (int i = 0; i < values.length; i++) {
                Subpack subpack = subpackManager.getSubpackById(Long.parseLong(values[i]));
                subpackList.add(subpack);
            }
            Person user = personManager.getPersonById(Long.parseLong(userID));
            subpackManager.updatePersonsAssignment(user, packManager.getPackById(packID), subpackList);
        } catch (DaoException e) {
            req.setAttribute("error", e);
            return "view-error";
        }


        return "redirect:/assign/{packID}";
    }


    @RequestMapping("/assign/{id}")
    public String assign(ServletRequest req, @PathVariable String id) {
        try {
            long packID = Long.parseLong(id);
            Pack pack = packManager.getPackById(packID);
            List<Person> users = personManager.getAllPersons();
            req.setAttribute("users", users);
            req.setAttribute("pack", pack);

            List<Subpack> subpackList = subpackManager.getSubpacksInPack(pack);
            boolean allAssigned = true;
            for (Subpack subPack : subpackList) {
                List<Person> usersOfSubpack = subpackManager.getPersonsAssignedToSubpack(subPack);
                int size = usersOfSubpack.size();
                if(size == 0)
                    allAssigned = false;
            }
            req.setAttribute("allAssigned", allAssigned);



        } catch (DaoException e) {
            req.setAttribute("error", e);
            return "view-error";
        }

        return "view-assign";

    }

    @RequestMapping("/assignMenu")
    public String assignMenu(ServletRequest req) {

        List<Pack> allPacks = null;
        try {
            allPacks = packManager.getAllPacks();
        } catch (DaoException e) {
            e.printStackTrace();
        }
        req.setAttribute("allPacks", allPacks);

        return "view-assignMenu";
    }


}
