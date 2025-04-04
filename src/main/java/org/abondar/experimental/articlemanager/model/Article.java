package org.abondar.experimental.articlemanager.model;


import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"author","coAuthors"})
public class Article {

    @Id
    private  String id;

    private String title;

    private String articleKey;

    @Relationship(type = "AUTHOR")
    private Author author;

    @Relationship(type = "CO_AUTHOR", direction = Relationship.Direction.OUTGOING)
    private List<Author> coAuthors = new ArrayList<>();

}

