package org.abondar.experimental.articlemanager.ui.author;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.model.Author;
import org.abondar.experimental.articlemanager.service.AuthorService;
import org.abondar.experimental.articlemanager.ui.MainLayout;


@Route(value = "authors", layout = MainLayout.class)
@PageTitle("Article manager/Authors")
@Slf4j
public class AuthorView extends HorizontalLayout {

    private final AuthorService authorService;

    public AuthorView(AuthorService authorService, AuthorAddUpdateForm authorAddUpdateForm) {
        this.authorService = authorService;

        var formLayout = new VerticalLayout();
        var author = new Author();

        var authorGrid = createGrid();

        var saveButton = new Button("Save", click -> {
            try {
                authorAddUpdateForm.getBinder().writeBean(author);
                var savedAuthor = authorService.save(author.getName(), author.getLastName(), author.getEmail());
                log.info("Saved author {}", savedAuthor.getId());

                authorGrid.getDataProvider().refreshAll();
                authorAddUpdateForm.getBinder().readBean(new Author());

            } catch (ValidationException e) {
                log.error(e.getMessage());
                Notification.show("Please correct the errors", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        authorGrid.addComponentColumn(at -> {
                    Button deleteBtn = new Button("Delete", click -> {
                        authorService.deleteAuthor(at.getId());
                        Notification.show("Author deleted", 3000, Notification.Position.TOP_CENTER);
                        log.info("Deleted author {}", at.getId());
                        authorGrid.getDataProvider().refreshAll();  // Refresh grid after deletion
                    });
                    deleteBtn.getStyle().set("color", "red");
                    return deleteBtn;
                })
                .setHeader("Actions")
                .setAutoWidth(true);

        var connectAuthors = new Button("Connect Authors", click -> {
            var selected = authorGrid.getSelectedItems();
            if (selected.size() != 2) {
                Notification.show("Only two authors can be connected at once", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            var iterator = selected.iterator();
            var author1Id = iterator.next().getId();
            var author2Id = iterator.next().getId();

            authorService.connectAuthors(author1Id, author2Id);
            log.info("Connected author {} and author {}", author1Id, author2Id);
            Notification.show("Authors connected", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        });

        var gridLayout = new VerticalLayout();
        gridLayout.add(authorGrid, connectAuthors);

        formLayout.add(authorAddUpdateForm, saveButton);
        add(formLayout,gridLayout);

    }

    private Grid<Author> createGrid() {
        var authorGrid = new Grid<>(Author.class);
        authorGrid.setColumns("name", "lastName", "email");
        authorGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        authorGrid.setWidthFull();
        authorGrid.setHeight("400px");

        authorGrid.setDataProvider(DataProvider.fromCallbacks(
                query -> {
                    var offset = query.getOffset();
                    var limit = query.getLimit();
                    return authorService.getAuthors(offset, limit)
                            .stream();
                },
                query -> (int) authorService.countAuthors()
        ));

        return authorGrid;
    }

}
