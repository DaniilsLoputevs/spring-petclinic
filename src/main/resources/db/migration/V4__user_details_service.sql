CREATE SEQUENCE  IF NOT EXISTS authority_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE  IF NOT EXISTS user_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE authority (
  id BIGINT NOT NULL,
   authority VARCHAR(50),
   CONSTRAINT pk_authority PRIMARY KEY (id)
);

CREATE TABLE user_ (
  id BIGINT NOT NULL,
   username VARCHAR(50),
   password VARCHAR(500),
   enabled BOOLEAN,
   CONSTRAINT pk_user_ PRIMARY KEY (id)
);

CREATE TABLE user__authorities (
  authorities_id BIGINT NOT NULL,
   user_id BIGINT NOT NULL,
   CONSTRAINT pk_user__authorities PRIMARY KEY (authorities_id, user_id)
);

ALTER TABLE authority ADD CONSTRAINT uc_authority_authority UNIQUE (authority);

ALTER TABLE user_ ADD CONSTRAINT uc_user__username UNIQUE (username);

CREATE INDEX idx_securityuser_username ON user_(username);

ALTER TABLE user__authorities ADD CONSTRAINT fk_useaut_on_authority FOREIGN KEY (authorities_id) REFERENCES authority (id);

ALTER TABLE user__authorities ADD CONSTRAINT fk_useaut_on_user FOREIGN KEY (user_id) REFERENCES user_ (id);
