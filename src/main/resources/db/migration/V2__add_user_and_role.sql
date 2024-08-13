create sequence  IF NOT EXISTS role_seq START with 1 INCREMENT BY 50;

create sequence  IF NOT EXISTS user_seq START with 1 INCREMENT BY 50;

create TABLE role (
  id BIGINT NOT NULL,
   authority VARCHAR(50),
   CONSTRAINT pk_role PRIMARY KEY (id)
);

create TABLE user_ (
  id BIGINT NOT NULL,
   username VARCHAR(50),
   password VARCHAR(500),
   enabled BOOLEAN,
   CONSTRAINT pk_user_ PRIMARY KEY (id)
);

create TABLE user__authorities (
  authorities_id BIGINT NOT NULL,
   user_id BIGINT NOT NULL,
   CONSTRAINT pk_user__authorities PRIMARY KEY (authorities_id, user_id)
);

alter table role add CONSTRAINT uc_role_authority UNIQUE (authority);

alter table user_ add CONSTRAINT uc_user__username UNIQUE (username);

create index idx_securityuser_username on user_(username);

alter table user__authorities add CONSTRAINT fk_useaut_on_role FOREIGN KEY (authorities_id) REFERENCES role (id);

alter table user__authorities add CONSTRAINT fk_useaut_on_user FOREIGN KEY (user_id) REFERENCES user_ (id);
