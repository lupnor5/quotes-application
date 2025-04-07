create table quotes (
                        id bigint not null,
                        created_at timestamp(6),
                        text varchar(1000) not null,
                        updated_ad timestamp(6),
                        author_id bigint,
                        primary key (id)
)