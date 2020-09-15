package ar.edu.itba.paw.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetMonoConsumer<T> {

    /*
    *   Functional Interface to Operate based on a ResultSet.
    *   Throws SQLException.
    *   Receives one argument t.
     */

    void accept(ResultSet rs, T t) throws SQLException;
}
