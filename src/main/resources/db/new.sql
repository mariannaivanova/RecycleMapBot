insert into users (chat_id, first_name, last_name, user_name)
values (1, 'na', 'na', 'na');

insert into subscribes (id, chat_id, geom, dist)
values (1, 1, st_setsrid(st_makepoint(37.53658, 55.748379), 4326), 1);