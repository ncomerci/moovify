package ar.edu.itba.paw.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetBiConsumer<T, U> {

    /*
     *   Functional Interface to Operate based on a ResultSet.
     *   Throws SQLException.
     *   Receives two arguments t and u.
     */

    void accept(ResultSet rs, T t, U u) throws SQLException;
}
