INSERT INTO role (id, authority) VALUES (10, 'ceo');
INSERT INTO role (id, authority) VALUES (11, 'vet');
INSERT INTO role (id, authority) VALUES (12, 'manager');
INSERT INTO role (id, authority) VALUES (13, 'legacy');

INSERT INTO user_ (id, username, password, enabled) VALUES (20, 'vivanov', '{noop}vivanov', TRUE);
INSERT INTO user_ (id, username, password, enabled) VALUES (21, 'ipetrov', '{noop}ipetrov', TRUE);
INSERT INTO user_ (id, username, password, enabled) VALUES (22, 'abelova', '{noop}abelova', TRUE);

INSERT INTO user__authorities (authorities_id, user_id) VALUES (10, 20);
INSERT INTO user__authorities (authorities_id, user_id) VALUES (13, 20);
INSERT INTO user__authorities (authorities_id, user_id) VALUES (12, 21);
INSERT INTO user__authorities (authorities_id, user_id) VALUES (12, 22);
