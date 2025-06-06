package org.abondar.experimental.articlemanager.model;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@Node
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"connections","articles"})
@ToString(exclude = {"connections", "articles"})
public class Author {
    @Id
    private String id;

    private String name;

    private String lastName;

    private String email;

    @Relationship(type = "WROTE", direction = Relationship.Direction.OUTGOING)
    private Set<Article> articles;

   @Relationship(type = "KNOWS", direction = Relationship.Direction.OUTGOING)
    private Set<Author> connections;

   public String getFullName(){
       return name + " " + lastName;
   }
}
