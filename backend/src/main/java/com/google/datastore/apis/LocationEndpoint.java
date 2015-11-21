package com.google.datastore.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ConflictException;
import com.google.datastore.entities.Location;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;

import static com.google.datastore.services.OfyService.ofy;
/**
 * Created by peterus on 21.11.2015.
 */
@Api(
        name = "locationEndpoint",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "datastore.google.com",
                ownerName = "datastore.google.com",
                packagePath = ""
        )
)
public class LocationEndpoint {
    public LocationEndpoint(){}

    @ApiMethod(name = "listLocations")
    public CollectionResponse<Location> listLocations(@Named("email") String email){

        Query<Location> query = ofy().load().type(Location.class).filter("ownerEmail", email);
        List<Location> locations = new ArrayList<>();

        for (Location aQuery : query) {
            locations.add(aQuery);
        }

        return CollectionResponse.<Location>builder().setItems(locations).build();
    }

    @ApiMethod(name = "insertLocation")
    public Location insertLocation(Location location) throws ConflictException {
        if(location.get_id() != null) {
            if (findRecord(location.get_id()) != null) {
                throw new ConflictException("Object already exists");
            }
        }

        ofy().save().entity(location).now();
        return location;
    }

    private Location findRecord(Long id){
        return ofy().load().type(Location.class).id(id).now();
    }
}
