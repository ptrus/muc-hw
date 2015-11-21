package com.google.datastore.services;

import com.google.datastore.entities.Connection;
import com.google.datastore.entities.Location;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

/**
 * Created by peterus on 21.11.2015.
 */
public class OfyService {

    static {
        ObjectifyService.register(Location.class);
        ObjectifyService.register(Connection.class);
    }

    public static Objectify ofy(){
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
