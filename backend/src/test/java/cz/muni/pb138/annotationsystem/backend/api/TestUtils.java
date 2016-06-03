package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Evaluation;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Rating;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;

import java.util.Arrays;
import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class TestUtils {

    static public final double EPSILON = 0.001;
    static public final int SUBPACK_SIZE_0 = 15;
    static public final int SUBPACK_SIZE_1 = 12;
    static public final int SUBPACK_SIZE_2 = 9;


    static public Pack getPack0() {
        Pack pack = new Pack("Is it an animal?", "Animals", 22, 28);
        return pack;
    }

    static public Pack getPack1() {
        Pack pack = new Pack("Is it a correct word?", "Correct words", 16, 23);
        return pack;
    }

    static public Pack getPack2() {
        Pack pack = new Pack("Is it a name?", "Names", 28, 14);
        return pack;
    }

    static public List<String> getAnswers0() {
        return Arrays.asList(
                "antelope", "armadillo", "bat", "hear", "beaver", "buffalo", "bull", "belt", "cow", "book",
                "spoon", "ant", "bear", "bee", "bird", "camel", "cat", "cheetah", "chicken", "chimpanzee",
                "smoke", "crocodile", "deer", "dog", "dolphin", "duck", "helicopter", "elephant", "fish", "fly",
                "fox", "frog", "movie", "goat", "goldfish", "hamster", "hippopotamus", "horse", "kangaroo", "kitten",
                "lion", "meal", "monkey", "octopus", "owl", "panda", "pig", "puppy", "rabbit", "rat"
        );
    }

    static public List<String> getNoise0() {
        return Arrays.asList(
                "brown", "clip", "edge", "alex", "casino", "cycle", "orange", "grill", "hammer", "thor");
    }

    static public List<String> getAnswers1() {
        return Arrays.asList(
                "upset", "handsome", "waste", "man", "knowing", "boil", "little", "fair", "shivering", "dark",
                "hrdava", "terrible", "day", "abashed", "colorful", "pushy", "healthy", "complain", "produce", "can",
                "trees", "scare", "work", "kunlum", "lucky", "left", "watch", "empty", "subtract", "bat",
                "kimasa", "distance", "hands", "oiuha", "closed", "efficient", "worm", "health", "ugly", "milk",
                "tra", "private", "obese", "melodic", "low", "guide", "acdobol", "pollution", "gardq", "ploccc"
                );
    }

    static public List<String> getNoise1() {
        return Arrays.asList(
                "grpkac", "dupelin", "helekina", "heeuio", "hzard", "kolq", "ooddne", "odklmn", "poqertz", "pqodmst");
    }

    static public List<String> getAnswers2() {
        return Arrays.asList(
                "Willie", "Jose", "Barbara", "Richard", "Database", "Lillian", "First", "Margaret", "Phillip", "Ronald",
                "Lawrence", "Carol", "Random", "Kenneth", "Beverly", "Cheryl", "Jason", "James", "Contains", "Lisa",
                "The", "Cynthia", "Eugene", "Pamela", "Peter", "Carolyn", "Phyllis", "Generator", "Janice", "Steve",
                "Lois", "Harry", "Jessica", "Todd", "Christina", "Samuel", "Laura", "Theresa", "Mildred", "Terry",
                "Martha", "Nicole", "Frank", "On", "Justin", "Howard", "Sean", "Doris", "English", "Europe"
        );

    }

    static public List<String> getNoise2() {
        return Arrays.asList(
                "People", "Dragon", "Happy", "Heureka", "Hazard", "Coca-Cola", "Odyn", "Obey", "Ant", "Bridge");
    }

    static public Person getPerson0() {
        Person person = new Person("RichLannister");
        return person;
    }

    static public Person getPerson1() {
        Person person = new Person("DeadSnow");
        return person;
    }

    static public Person getPerson2() {
        Person person = new Person("Khaleesi");
        return person;
    }

    static public Pack[] createPacks(PackManager packManager) throws Exception {
        Pack pack0 = getPack0();
        packManager.createPack(pack0, getAnswers0(), getNoise0(), SUBPACK_SIZE_0);

        Pack pack1 = getPack1();
        packManager.createPack(pack1, getAnswers1(), getNoise1(), SUBPACK_SIZE_1);

        Pack pack2 = getPack2();
        packManager.createPack(pack2, getAnswers2(), getNoise2(), SUBPACK_SIZE_2);

        return new Pack[]{pack0, pack1, pack2};
    }

    static public Person[] createPersons(PersonManager personManager) throws Exception {
        Person person0 = personManager.getOrCreatePersonByUsername(getPerson0().getUsername());

        Person person1 = personManager.getOrCreatePersonByUsername(getPerson1().getUsername());

        Person person2 = personManager.getOrCreatePersonByUsername(getPerson2().getUsername());

        return new Person[]{person0, person1, person2};
    }


    static public Evaluation[] createEvals(EvaluationManager evaluationManager, AnswerManager answerManager, Person person, Subpack subpack) throws Exception {
        Answer answer0 = answerManager.nextAnswer(person, subpack);
        Evaluation eval0 = new Evaluation(person, answer0, Rating.POSITIVE, 10);
        evaluationManager.eval(eval0);
        Answer answer1 = answerManager.nextAnswer(person, subpack);
        Evaluation eval1 = new Evaluation(person, answer1, Rating.POSITIVE, 11);
        evaluationManager.eval(eval1);
        Answer answer2 = answerManager.nextAnswer(person, subpack);
        Evaluation eval2 = new Evaluation(person, answer2, Rating.NEGATIVE, 12);
        evaluationManager.eval(eval2);

        return new Evaluation[]{eval0, eval1, eval2};
    }

}
