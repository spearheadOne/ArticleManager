package org.abondar.experimental.articlemanager.repository;

import org.abondar.experimental.articlemanager.model.Author;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends Neo4jRepository<Author,String> {

    List<Author> findConnectionsById(String id);

    @Query("MATCH (a:Author)-[r:KNOWS]-(b:Author) WHERE a.id = $id DELETE r")
    void removeRelationships(String id);


    @Query("MATCH (a:Author) WHERE a.id IN $ids RETURN a")
    List<Author> findByIds(List<String> ids);

}
