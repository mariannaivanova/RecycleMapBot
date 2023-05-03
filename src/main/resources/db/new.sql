insert into users (chat_id, first_name, last_name, user_name)
values (1, 'na', 'na', 'na');

insert into subscribes (id, chat_id, geom, dist)
values (1, 1, st_setsrid(st_makepoint(37.53658, 55.748379), 4326), 1);

SELECT DISTINCT p.id, p.title, p.address, ST_AsText(p.geom) as geom, p.url,
                ST_DistanceSphere(p.geom, ST_GeomFromEWKT('SRID=4326;POINT(30.3141 59.9386)')) AS dist
FROM points p
         LEFT OUTER JOIN points_fractions pf ON pf.point_id = p.id
         LEFT OUTER JOIN fractions f ON f.id = pf.fraction_id
WHERE f."name" in ('BUMAGA', 'PLASTIC', 'STEKLO', 'LAMPOCHKI')
ORDER BY dist
LIMIT 10;

SELECT id, address, ST_AsText(geom) AS geom,
       ST_DistanceSphere(geom, ST_GeomFromEWKT('SRID=4326;POINT(30.3141 59.9386)')) AS dist
FROM points_fractions_view
WHERE fraction = 'BUMAGA' OR fraction = 'PLASTIC' OR fraction = 'STEKLO' OR fraction = 'LAMPOCHKI'
ORDER BY geom <-> ST_GeomFromEWKT('SRID=4326;POINT(30.3141 59.9386)')
LIMIT 10;