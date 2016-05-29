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
    static public final int SUBPACK_SIZE_0 = 2;
    static public final int SUBPACK_SIZE_1 = 3;
    static public final int SUBPACK_SIZE_2 = 2;


    static public Pack getPack0() {
        Pack pack = new Pack("Is it an animal?", "Animals", 12, 18);
        return pack;
    }

    static public Pack getPack1() {
        Pack pack = new Pack("Is it a correct word?", "Correct words", 6, 3);
        return pack;
    }

    static public Pack getPack2() {
        Pack pack = new Pack("Is it a name?", "Names", 8, 4);
        return pack;
    }

    static public List<String> getAnswers0() {
        return Arrays.asList("dog", "cat", "spoon", "mouse", "helicopter", "tiger", "monkey", "giraffe", "movie", "Lion");
    }

    static public List<String> getNoise0() {
        return Arrays.asList("brown", "clip", "edge");
    }

    static public List<String> getAnswers1() {
        return Arrays.asList("journal", "brndrap", "halloween", "hill", "burger", "hjuvion", "park", "valley", "disk", "evgluph");
    }

    static public List<String> getNoise1() {
        return Arrays.asList("grpkac", "dupelin", "helekina");
    }

    static public List<String> getAnswers2() {
        return Arrays.asList("Felix", "Anna", "Vampire", "Clark", "Ben", "Hanna", "Mark", "Andrew", "London", "Obstacle");
    }

    static public List<String> getNoise2() {
        return Arrays.asList("People", "Dragon", "Happy");
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
        Person person0 = getPerson0();
        personManager.createPerson(person0);

        Person person1 = getPerson1();
        personManager.createPerson(person1);

        Person person2 = getPerson2();
        personManager.createPerson(person2);

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
