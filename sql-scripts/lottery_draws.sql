create table lottery_draws (
                               id serial primary key,
                               drawn boolean default false,
                               created_at numeric not null,
                               last_modified_at numeric not null
);
