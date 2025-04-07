alter table if exists quotes
    add constraint fk_quotes_author_id
        foreign key (author_id)
            references authors