package cz.muni.pb138.annotationsystem.backend.business;

import ch.qos.logback.classic.spi.PackagingDataCalculator;
import cz.muni.pb138.annotationsystem.backend.api.PackManager;
import cz.muni.pb138.annotationsystem.backend.dao.AnswerDao;
import cz.muni.pb138.annotationsystem.backend.dao.PackDaoImpl;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Pack;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@Named
public class PackManagerImpl implements PackManager {

    @Inject
    private PackDaoImpl packDao;

    @Override
    public void createPack(Pack pack, List<Answer> answers, int numOfAnswersInSubpack) {

    }

    @Override
    public Pack getPackById(Long id) {
        Pack p = new Pack("Is it animal?", "Animals 003", 3.5, 2.5);
        p.setId((long) 3);
        return p;
    }

    @Override
    public List<Pack> getAllPacks() {
        Pack p1 = new Pack("Is it animal?", "Animals 003", 3.5, 2.5);
        p1.setId((long) 3);
        Pack p2 = new Pack("Is it verb?", "Verbs 001", 3.0, 3.0);
        p2.setId((long) 2);
        List<Pack> packs = new ArrayList<>();
        packs.add(p1);
        packs.add(p2);
        return packs;
    }

    @Override
    public void deletePack(Pack pack) {

    }
}
