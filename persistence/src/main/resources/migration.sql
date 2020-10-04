-- feature/bajaLogica
alter table users add column enabled boolean not null default true;

alter table posts add column enabled boolean not null default true;

alter table comments add column enabled boolean not null default true;