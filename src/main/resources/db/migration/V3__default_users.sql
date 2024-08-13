INSERT INTO role (id, authority) VALUES (10, 'MANAGER');
INSERT INTO role (id, authority) VALUES (11, 'VET');
INSERT INTO role (id, authority) VALUES (12, 'OWNER');

-- how to generate passwords for all
INSERT INTO user_ (id, username, password, enabled) VALUES (20, 'jcarter', '{bcrypt}$2a$10$tWFLDIGTgVCKHUbRXM9BTuds75gwvTKnE3AaXjy7IId1xssz/de5y', TRUE);
INSERT INTO user_ (id, username, password, enabled) VALUES (21, 'hleary', '{bcrypt}$2a$10$4NYstZC0cGkfQCfNQZr1je8WJifflAi8lmNfPe9co4J11MQM/ewEy', TRUE);
INSERT INTO user_ (id, username, password, enabled) VALUES (22, 'ldouglas', '{bcrypt}$2a$10$mXUClizzFgMlJc/WlWA7V.8Mg8inAWQSHtSlFMVr5EccSEK74Woni', TRUE);

INSERT INTO user__authorities (authorities_id, user_id) VALUES (11, 20);
INSERT INTO user__authorities (authorities_id, user_id) VALUES (11, 21);
INSERT INTO user__authorities (authorities_id, user_id) VALUES (10, 22);
INSERT INTO user__authorities (authorities_id, user_id) VALUES (11, 22);
