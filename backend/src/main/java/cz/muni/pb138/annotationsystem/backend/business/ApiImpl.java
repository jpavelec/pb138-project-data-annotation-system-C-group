package cz.muni.pb138.annotationsystem.backend.business;

import cz.muni.pb138.annotationsystem.backend.api.Api;

import javax.inject.Named;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@Named
public class ApiImpl implements Api {

    @Override
    public String prilisZlutoucky(String animal) {
        return "Příliš žluťoučký " + animal;
    }
}
