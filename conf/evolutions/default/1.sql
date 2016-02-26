# --- !Ups

create table users(
  user_id uuid primary key,
  user_code varchar(255) not null,
  first_name varchar(255) null,
  last_name varchar(255) null,
  created timestamp not null
);

insert into users values(RANDOM_UUID(), 'joeb', 'Joe', 'Black', CURRENT_TIMESTAMP());

# --- !Downs

drop table users;
