create table lottery_tickets (
                                 id uuid primary key,
                                 result boolean,
                                 draw_id integer references lottery_draws(id) on delete cascade,
                                 client_id uuid references clients(id) on delete cascade,
                                 created_at numeric not null,
                                 last_modified_at numeric not null
);
