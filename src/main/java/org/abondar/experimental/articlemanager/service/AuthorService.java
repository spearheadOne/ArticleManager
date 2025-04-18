package org.abondar.experimental.articlemanager.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.exception.AuthorNotFoundException;
import org.abondar.experimental.articlemanager.model.Author;
import org.abondar.experimental.articlemanager.repository.ArticleRepository;
import org.abondar.experimental.articlemanager.repository.AuthorRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final ArticleRepository articleRepository;

    public Author save(String firstName, String lastName, String email) {
        var id = UUID.randomUUID().toString();
        var author = new Author(id, firstName, lastName, email, Set.of(), Set.of());

        log.info("Author saved with id {}", id);

        return authorRepository.save(author);
    }

    public void connectAuthors(String author1Id, String author2Id) {
        getAuthorById(author1Id);
        getAuthorById(author2Id);

        authorRepository.createConnection(author1Id, author2Id);
    }

    public void disconnectAuthors(String author1Id, String author2Id) {
        getAuthorById(author1Id);
        getAuthorById(author2Id);

        authorRepository.removeConnection(author1Id, author2Id);
    }

    public boolean connectionExists(String author1Id, String author2Id) {
        return authorRepository.checkExistingConnection(author1Id, author2Id);
    }

    public Author updateAuthor(Author author) {
        getAuthorById(author.getId());

        return authorRepository.save(author);
    }

    public List<Author> findConnectionsById(String id) {
        return authorRepository.findConnectionsById(id);
    }

    public void deleteAuthor(String id) {
        getAuthorById(id);

        articleRepository.removeMainAuthor(id);
        articleRepository.removeCoAuthor(id);
        authorRepository.removeRelationships(id);

        authorRepository.deleteById(id);
    }

    public Author getAuthorById(String id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found"));
    }

    public List<Author> getAuthors(int offset, int limit) {
        return authorRepository.findAll(PageRequest.of(offset,limit)).getContent();
    }

    public long countAuthors() {
        return authorRepository.count();
    }

    public List<Author> searchAuthors(String searchTerm) {

        if (!searchTerm.contains(" ")) {
            return authorRepository.findByNameContainingIgnoreCase(searchTerm);
        } else {
            var split = searchTerm.split(" ");
            if (split.length == 2) {
                var name = searchTerm.split(" ")[0];
                var lastName = searchTerm.split(" ")[1];
                return authorRepository.findByNameAndLastNameContainingIgnoreCase(name, lastName);
            }
        }

        return List.of();
    }
}
