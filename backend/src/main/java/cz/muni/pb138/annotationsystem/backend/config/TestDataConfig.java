package cz.muni.pb138.annotationsystem.backend.config;

import cz.muni.pb138.annotationsystem.backend.api.AnswerManager;
import cz.muni.pb138.annotationsystem.backend.api.EvaluationManager;
import cz.muni.pb138.annotationsystem.backend.api.PackManager;
import cz.muni.pb138.annotationsystem.backend.api.PersonManager;
import cz.muni.pb138.annotationsystem.backend.api.StatisticsManager;
import cz.muni.pb138.annotationsystem.backend.api.SubpackManager;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Evaluation;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Rating;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@Configuration
@Import(BackendConfig.class)
public class TestDataConfig {

    @Inject
    private PersonManager personManager;
    @Inject
    private PackManager packManager;
    @Inject
    private SubpackManager subpackManager;
    @Inject
    private AnswerManager answerManager;
    @Inject
    private EvaluationManager evaluationManager;
    
    @PostConstruct
    public void dataLoading() throws DaoException {

        Person anna = personManager.getOrCreatePersonByUsername("anna");
        Person carl = personManager.getOrCreatePersonByUsername("carl");
        Person phil = personManager.getOrCreatePersonByUsername("phil");
        
        Pack animalsPack = animalsPack();
        packManager.createPack(animalsPack, animalsAnswers(), animalsNoise(), 15);
        Pack wordPack = wordPack();
        packManager.createPack(wordPack, wordsAnswers(), wordNoise(), 12);
        Pack namePack = namePack();
        packManager.createPack(namePack, nameAnswers(), nameNoise(), 9);

        List<Subpack> annasAnimalsSubpacks = subpackManager.getSubpacksInPack(animalsPack).subList(0, 2);
        List<Subpack> annasWordSubpacks = subpackManager.getSubpacksInPack(wordPack).subList(0, 1);
        List<Subpack> annasNameSubpacks = subpackManager.getSubpacksInPack(namePack).subList(1, 3);

        subpackManager.updatePersonsAssignment(anna, animalsPack, annasAnimalsSubpacks);
        subpackManager.updatePersonsAssignment(anna, wordPack, annasWordSubpacks);
        subpackManager.updatePersonsAssignment(anna, namePack, annasNameSubpacks);

        List<Subpack> annasSubpacks = new ArrayList<>();
        annasSubpacks.addAll(annasAnimalsSubpacks);
        annasSubpacks.addAll(annasWordSubpacks);
        annasSubpacks.addAll(annasNameSubpacks);

        List<Subpack> carlsAnimalsSubpacks = subpackManager.getSubpacksInPack(animalsPack).subList(1, 2);
        List<Subpack> carlsWordSubpacks = subpackManager.getSubpacksInPack(wordPack).subList(0, 1);

        subpackManager.updatePersonsAssignment(carl, animalsPack, carlsAnimalsSubpacks);
        subpackManager.updatePersonsAssignment(carl, wordPack, carlsWordSubpacks);

        List<Subpack> carlsSubpacks = new ArrayList<>();
        carlsSubpacks.addAll(carlsAnimalsSubpacks);
        carlsSubpacks.addAll(carlsWordSubpacks);

        Answer annaAnswer;
        annaAnswer = answerManager.nextAnswer(anna, annasSubpacks.get(1));
        evaluationManager.eval(new Evaluation(anna, annaAnswer, Rating.POSITIVE, 16));
        annaAnswer = answerManager.nextAnswer(anna, annasSubpacks.get(1));
        evaluationManager.eval(new Evaluation(anna, annaAnswer, Rating.POSITIVE, 12));
        annaAnswer = answerManager.nextAnswer(anna, annasSubpacks.get(1));
        evaluationManager.eval(new Evaluation(anna, annaAnswer, Rating.NEGATIVE, 31));
        annaAnswer = answerManager.nextAnswer(anna, annasSubpacks.get(1));
        evaluationManager.eval(new Evaluation(anna, annaAnswer, Rating.NONSENSE, 30));

        Answer carlAnswer;
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(0));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.POSITIVE, 15));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(0));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.POSITIVE, 10));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(1));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.NEGATIVE, 21));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(1));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.POSITIVE, 10));
        carlAnswer = answerManager.nextAnswer(carl, carlsSubpacks.get(1));
        evaluationManager.eval(new Evaluation(carl, carlAnswer, Rating.NEGATIVE, 28));
        
    }


    static public Pack animalsPack() {
        Pack pack = new Pack("an animal", "animals.csv", 22, 28);
        return pack;
    }

    static public Pack wordPack() {
        Pack pack = new Pack("a correct word", "words.csv", 16, 23);
        return pack;
    }

    static public Pack namePack() {
        Pack pack = new Pack("a name", "names.csv", 28, 14);
        return pack;
    }

    static public List<String> animalsAnswers() {
        return Arrays.asList(
                "antelope", "armadillo", "bat", "hear", "beaver", "buffalo", "bull", "belt", "cow", "book",
                "spoon", "ant", "bear", "bee", "bird", "camel", "cat", "cheetah", "chicken", "chimpanzee",
                "smoke", "crocodile", "deer", "dog", "dolphin", "duck", "helicopter", "elephant", "fish", "fly",
                "fox", "frog", "movie", "goat", "goldfish", "hamster", "hippopotamus", "horse", "kangaroo", "kitten",
                "lion", "meal", "monkey", "octopus", "owl", "panda", "pig", "puppy", "rabbit", "rat"
        );
    }

    static public List<String> animalsNoise() {
        return Arrays.asList(
                "brown", "clip", "edge", "alex", "casino", "cycle", "orange", "grill", "hammer", "thor");
    }

    static public List<String> wordsAnswers() {
        return Arrays.asList(
                "upset", "handsome", "waste", "man", "knowing", "boil", "little", "fair", "shivering", "dark",
                "hrdava", "terrible", "day", "abashed", "colorful", "pushy", "healthy", "complain", "produce", "can",
                "trees", "scare", "work", "kunlum", "lucky", "left", "watch", "empty", "subtract", "bat",
                "kimasa", "distance", "hands", "oiuha", "closed", "efficient", "worm", "health", "ugly", "milk",
                "tra", "private", "obese", "melodic", "low", "guide", "acdobol", "pollution", "gardq", "ploccc"
        );
    }

    static public List<String> wordNoise() {
        return Arrays.asList(
                "grpkac", "dupelin", "helekina", "heeuio", "hzard", "kolq", "ooddne", "odklmn", "poqertz", "pqodmst");
    }

    static public List<String> nameAnswers() {
        return Arrays.asList(
                "Willie", "Jose", "Barbara", "Richard", "Database", "Lillian", "First", "Margaret", "Phillip", "Ronald",
                "Lawrence", "Carol", "Random", "Kenneth", "Beverly", "Cheryl", "Jason", "James", "Contains", "Lisa",
                "The", "Cynthia", "Eugene", "Pamela", "Peter", "Carolyn", "Phyllis", "Generator", "Janice", "Steve",
                "Lois", "Harry", "Jessica", "Todd", "Christina", "Samuel", "Laura", "Theresa", "Mildred", "Terry",
                "Martha", "Nicole", "Frank", "On", "Justin", "Howard", "Sean", "Doris", "English", "Europe"
        );

    }

    static public List<String> nameNoise() {
        return Arrays.asList(
                "People", "Dragon", "Happy", "Heureka", "Hazard", "Coca-Cola", "Odyn", "Obey", "Ant", "Bridge");
    }


}
