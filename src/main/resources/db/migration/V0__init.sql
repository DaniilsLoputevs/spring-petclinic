create TABLE vets (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   first_name TEXT,
   last_name TEXT,
   CONSTRAINT vets_pkey PRIMARY KEY (id)
);

create TABLE specialties (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   name TEXT,
   CONSTRAINT specialties_pkey PRIMARY KEY (id)
);

create TABLE types (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   name TEXT,
   CONSTRAINT types_pkey PRIMARY KEY (id)
);

create TABLE owners (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   first_name TEXT,
   last_name TEXT,
   address TEXT,
   city TEXT,
   telephone TEXT,
   CONSTRAINT owners_pkey PRIMARY KEY (id)
);

create TABLE pets (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   name TEXT,
   birth_date date,
   type_id INTEGER NOT NULL,
   owner_id INTEGER,
   CONSTRAINT pets_pkey PRIMARY KEY (id)
);

create TABLE visits (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   pet_id INTEGER,
   visit_date date,
   description TEXT,
   CONSTRAINT visits_pkey PRIMARY KEY (id)
);

create TABLE vet_specialties (
  vet_id INTEGER NOT NULL,
   specialty_id INTEGER NOT NULL
);

create index vets_last_name_idx on vets(last_name);

create index specialties_name_idx on specialties(name);

create index types_name_idx on types(name);

create index owners_last_name_idx on owners(last_name);

create index pets_name_idx on pets(name);

create index pets_owner_id_idx on pets(owner_id);

create index visits_pet_id_idx on visits(pet_id);

alter table vet_specialties add CONSTRAINT vet_specialties_vet_id_specialty_id_key UNIQUE (vet_id, specialty_id);

alter table pets add CONSTRAINT pets_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES owners (id) ON update NO ACTION ON delete NO ACTION;

alter table pets add CONSTRAINT pets_type_id_fkey FOREIGN KEY (type_id) REFERENCES types (id) ON update NO ACTION ON delete NO ACTION;

alter table vet_specialties add CONSTRAINT vet_specialties_specialty_id_fkey FOREIGN KEY (specialty_id) REFERENCES specialties (id) ON update NO ACTION ON delete NO ACTION;

alter table vet_specialties add CONSTRAINT vet_specialties_vet_id_fkey FOREIGN KEY (vet_id) REFERENCES vets (id) ON update NO ACTION ON delete NO ACTION;

alter table visits add CONSTRAINT visits_pet_id_fkey FOREIGN KEY (pet_id) REFERENCES pets (id) ON update NO ACTION ON delete NO ACTION;
