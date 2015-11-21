package com.google.datastore.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ConflictException;
import com.google.datastore.entities.Connection;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;

import static com.google.datastore.services.OfyService.ofy;

/**
 * Created by peterus on 21.11.2015.
 */

@Api(
        name = "connectionEndpoint",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "datastore.google.com",
                ownerName = "datastore.google.com",
                packagePath = ""
        )
)
public class ConnectionEndpoint {
    public ConnectionEndpoint(){}

    @ApiMethod(name = "listConnections")
    public CollectionResponse<Connection> listConnections(@Named("email") String email){

        Query<Connection> query = ofy().load().type(Connection.class).filter("ownerEmail", email);
        List<Connection> connections = new ArrayList<>();
        System.out.println("number of connections:" + query.count());
        for (Connection aQuery : query) {
            connections.add(aQuery);
            System.out.println("in here");
        }

        return CollectionResponse.<Connection>builder().setItems(connections).build();
    }

    @ApiMethod(name = "insertConnection")
    public Connection insertConnection(Connection connection) throws ConflictException {
        if(connection.get_id() != null) {
            if (findRecord(connection.get_id()) != null) {
                throw new ConflictException("Object already exists");
            }
        }

        ofy().save().entity(connection).now();
        return connection;
    }


    private Connection findRecord(Long id){
        return ofy().load().type(Connection.class).id(id).now();
    }

}
