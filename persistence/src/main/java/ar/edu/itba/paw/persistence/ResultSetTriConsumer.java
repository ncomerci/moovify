package ar.edu.itba.paw.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetTriConsumer<T, U, V> {

    /*
     *   Functional Interface to Operate based on a ResultSet.
     *   Throws SQLException.
     *   Receives three arguments t, u and v.
     */

    void accept(ResultSet rs, T t, U u, V v) throws SQLException;
}