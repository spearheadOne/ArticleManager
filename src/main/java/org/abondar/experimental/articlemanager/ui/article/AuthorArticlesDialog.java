package org.abondar.experimental.articlemanager.ui.article;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.model.Article;
import org.abondar.experimental.articlemanager.model.Author;

import java.util.List;


@Slf4j
public class AuthorArticlesDialog extends Dialog {

    private final String awsEndpoint;

    private final String articleBucket;

    public AuthorArticlesDialog(List<Article> articles, Author author, String awsEndpoint, String articleBucket) {
        this.awsEndpoint = awsEndpoint;
        this.articleBucket = articleBucket;

        setHeaderTitle("Articles of: " + author.getFullName());

        if (articles.isEmpty()) {
            var span = new Span("No articles available for selected author");
            add(span);
        } else {
            VirtualList<Article> articleList = new VirtualList<>();
            articleList.setItems(articles);
            articleList.setRenderer(createArticleRenderer());
            add(articleList);
        }

        var close = new Button("Close", e -> close());
        getFooter().add(close);
    }

    private ComponentRenderer<Component, Article> createArticleRenderer() {
        return new ComponentRenderer<>(
                article -> {
                    var articleLayout = new VerticalLayout();
                    articleLayout.setSpacing(false);
                    articleLayout.setMargin(false);


                    String fileKey = article.getArticleKey();
                    String fileUrl = awsEndpoint + "/" + articleBucket + "/" + fileKey;

                    var fileLink = new Anchor(fileUrl, article.getTitle());
                    fileLink.setTarget("_blank");

                    articleLayout.getElement().appendChild(
                            fileLink.getElement()
                    );

                    var coAuthorLayout = new VerticalLayout();
                    coAuthorLayout.setSpacing(false);
                    coAuthorLayout.setPadding(false);

                    if (article.getCoAuthors() != null) {
                        coAuthorLayout.add(new Span("Co-authors:"));
                        article.getCoAuthors().forEach(coAuthor ->
                                coAuthorLayout.add(new Div(new Text(coAuthor.getFullName())))
                        );


                        articleLayout.add(coAuthorLayout);
                    }


                    return articleLayout;
                }
        );
    }
}
