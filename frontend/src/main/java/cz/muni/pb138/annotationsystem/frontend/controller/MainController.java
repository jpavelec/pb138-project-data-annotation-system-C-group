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
            req.setAttribute("person", personManager.getPersonById((long)1));
        } catch (DaoException e) {
            return "redirect:/view-error";
        }

        return "view-admin";

    }

    @RequestMapping(value="/upload",method={RequestMethod.GET})
    public String doGet(ServletRequest req){

        return "view-upload";
    }

    @RequestMapping(value="/upload",method={RequestMethod.POST})
    public String doPost(ServletRequest req, HttpSession session, @RequestParam MultipartFile file){

        if (!file.isEmpty()) {

            File dir = new File(File.separator + "uploadedfile");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File serverFile = new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename());

            try {
                try (InputStream is = file.getInputStream();
                     BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile))) {
                    int i;
                    //write file to server
                    while ((i = is.read()) != -1) {
                        stream.write(i);
                    }
                    stream.flush();
                }
            } catch (IOException e) {
            }

            List<String> mojecsv = new ArrayList<String>();
            Integer counter = 0;

            String[] nextLine;
            try {
                FileReader fileReader = new FileReader(serverFile);
                CSVReader reader = new CSVReader(fileReader);
                try {
                    while ((nextLine = reader.readNext()) != null) {
                        for(int i=0;i<nextLine.length;i++){
                            counter++;
                            mojecsv.add(nextLine[i]);
                        }
                    }
                } catch (IOException e) {
                    req.setAttribute("error", e);
                    return "view-error";
                }
            } catch (FileNotFoundException e) {
                req.setAttribute("error", e);
                return "view-error";
            }

            req.setAttribute("csvContent", mojecsv);
            req.setAttribute("csvLength", counter-2);
            packManager.createPack(null, mojecsv, null, counter-2);

            return "view-assign";
        }
        else {
            req.setAttribute("error", "File empty.");
            return "view-error";
        }

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
