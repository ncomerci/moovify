package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.Post;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class PostDaoImpl implements PostDao {

    @Override
    public Post findById(long id){
        //TODO hacer de verdadz
        return new Post(id,
                LocalDateTime.now(),
                "Post de Testeo",
                "Este es un post de testeo",
                6,
                "tobias@tobias.com");
    }

}
